package com.springware.config.sqlConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ankita.kulkarni
 * @Date 02/08/2019
 *
 *       <b>TenantDataFactory</b> This is the configuration Class. Configuration
 *       of multiple databases is maintained in YAML and it creates Map of
 *       tenant name and its corresponding MongoClient Object with other Tenant
 *       Data.
 */

@Component
class TenantDataFactory {

	private final Map<String, TenantsConfig.TenantData> tenantDataMap = new HashMap<>();

	private final Map<Object, Object> tenantDataSources = new HashMap<>();

	public String getDefaultTenant() {
		return defaultTenant;
	}

	private String defaultTenant;

	public TenantDataFactory() {
	}

	/**
	 * <b>TenantDataFactory :: Parameterised Constructor</b> This constructor will
	 * get called from MongoConfiguration at startup.
	 *
	 * @param tenants all the client configured in system
	 */
	TenantDataFactory(Map<String, TenantsConfig.TenantData> tenants, String defaultTenant) {
		this.defaultTenant = defaultTenant;
		for (Map.Entry<String, TenantsConfig.TenantData> entry : tenants.entrySet()) {
			TenantsConfig.TenantData tenantData = entry.getValue();
			// TODO :: uncomment below line for UAT
			DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
			dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
			dataSourceBuilder.username(tenantData.getUser());
			dataSourceBuilder.password(tenantData.getPassword());
			dataSourceBuilder.url("jdbc:mysql://" + tenantData.getHost() + ":3306/" + tenantData.getDbName()
					+ "?enabledTLSProtocols=TLSv1.2");
			// System.out.println("DATABASE URL ::- "+"jdbc:mysql://" +
			// tenantData.getHost()+":3306/"+tenantData.getDbName());
			tenantDataMap.put(entry.getKey(),
					new TenantsConfig.TenantData(tenantData.getDbName(), dataSourceBuilder.build()));
			tenantDataSources.put(entry.getKey(), dataSourceBuilder.build());
		}
	}

	public Map<Object, Object> getTenantDataSources() {
		return tenantDataSources;
	}

	public DataSource getTenantDataSource(String tenant) {
		return (DataSource) tenantDataSources.get(tenant);
	}

	/**
	 * <b>getTenantData</b> This method is used to get the data of a particular
	 * tenant name. Whenever any request will come, it will have tenant name
	 * (database to be connected). So to get the details of that database and its
	 * mongoclient object, we will use this method.
	 *
	 * @param tenant client name
	 * @return TenantData {@link TenantsConfig.TenantData}
	 */
	TenantsConfig.TenantData getTenantData(String tenant) {
		TenantsConfig.TenantData tenantData = tenantDataMap.get(tenant);
		if (tenantData == null) {
			tenantData = tenantDataMap.get(defaultTenant);
		}
		return tenantData;
	}
}
