package com.springware.config.sqlConfig;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final Logger LOG = LoggerFactory.getLogger(TenantConnectionProvider.class);

	private final TenantDataFactory tenantDataFactory;

	public TenantConnectionProvider(TenantDataFactory tenantDataFactory) {
		this.tenantDataFactory = tenantDataFactory;
	}

	@Override
	protected DataSource selectAnyDataSource() {
		TenantsConfig.TenantData tenantData = this.tenantDataFactory.getTenantData(TenantContext.getCurrentTenant());
		return tenantData.getDataSource();
	}

	@Override
	protected DataSource selectDataSource(String tenantIdentifier) {
		TenantsConfig.TenantData tenantData = this.tenantDataFactory.getTenantData(TenantContext.getCurrentTenant());
		return tenantData.getDataSource();
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		TenantsConfig.TenantData tenantData = this.tenantDataFactory.getTenantData(TenantContext.getCurrentTenant());
		Connection connection = tenantData.getDataSource().getConnection();
		connection.createStatement().execute(String.format("USE %s;", tenantData.getDbName()));
		return connection;
	}
}
