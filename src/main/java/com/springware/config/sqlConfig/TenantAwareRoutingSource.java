package com.springware.config.sqlConfig;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantAwareRoutingSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {

		return TenantContext.getCurrentTenant();
	}
}
