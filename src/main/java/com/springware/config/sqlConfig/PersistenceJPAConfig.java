package com.springware.config.sqlConfig;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties({ JpaProperties.class })
@EnableJpaRepositories(basePackages = { "com.springware.*" }, transactionManagerRef = "mySQLtransactionManager")
@EntityScan({ "com.springware.*" })
@ComponentScan(basePackages = "com.springware.*")
public class PersistenceJPAConfig {

	private final TenantDataFactory tenantDataFactory;
	@Autowired
	Environment env;

	public PersistenceJPAConfig(TenantsConfig tenantsConfig) {
		this.tenantDataFactory = new TenantDataFactory(tenantsConfig.getTenants(), tenantsConfig.getDefaultTenant());
		TenantContext.setDefaultTenant(tenantsConfig.getDefaultTenant());
	}

	@Bean(name = "tenantJpaVendorAdapter")
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean
	public DataSource dataSource() {
		AbstractRoutingDataSource customDataSource = new TenantAwareRoutingSource();
		customDataSource.setTargetDataSources(tenantDataFactory.getTenantDataSources());
		customDataSource.setDefaultTargetDataSource(
				tenantDataFactory.getTenantDataSource(tenantDataFactory.getDefaultTenant()));
		customDataSource.afterPropertiesSet();
		return customDataSource;
	}

	@Bean
	public EntityManager entityManager() {
		return entityManagerFactory().getObject().createEntityManager();
	}

	/**
	 * The multi tenant connection provider
	 *
	 * @return
	 */
	@Bean
	public MultiTenantConnectionProvider multiTenantConnectionProvider() {
		// Autowires the multi connection provider
		return new TenantConnectionProvider(tenantDataFactory);
	}

	/**
	 * The current tenant identifier resolver
	 *
	 * @return
	 */
	@Bean
	public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
		return new CurrentTenantIdentifierResolverImpl();
	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
		emfBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		emfBean.setPersistenceUnitName("tenantdb-persistence-unit");
		emfBean.setDataSource(dataSource());
		// All tenant related entities, repositories and service classes must be scanned
		emfBean.setPackagesToScan(new String[] { "com.springware" });
		emfBean.setJpaVendorAdapter(this.jpaVendorAdapter());
		emfBean.setJpaPropertyMap(additionalProperties());

		return emfBean;
	}

	private Map<String, Object> additionalProperties() {
		Map<String, Object> jpaProperties = new HashMap<>();
		jpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
		jpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER,
				this.multiTenantConnectionProvider());
		jpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER,
				this.currentTenantIdentifierResolver());
		jpaProperties.put(org.hibernate.cfg.Environment.SHOW_SQL, env.getProperty("spring.jpa.show-sql"));
		jpaProperties.put(org.hibernate.cfg.Environment.FORMAT_SQL,
				env.getProperty("spring.jpa.properties.hibernate.format_sql"));
		jpaProperties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, env.getProperty("spring.jpa.hibernate.ddl-auto"));
		jpaProperties.put(org.hibernate.cfg.Environment.PHYSICAL_NAMING_STRATEGY,
				PhysicalNamingStrategyStandardImpl.class);
		jpaProperties.put(org.hibernate.cfg.Environment.DIALECT, MySQL8Dialect.class);

		return jpaProperties;
	}

	@Bean(name = "mySQLtransactionManager")
	@Primary
	public JpaTransactionManager jpaTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());
		transactionManager.setNestedTransactionAllowed(true);
		transactionManager.setPersistenceUnitName("tenantdb-persistence-unit");
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}
