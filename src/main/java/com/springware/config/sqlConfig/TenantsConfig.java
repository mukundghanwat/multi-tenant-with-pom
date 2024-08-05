package com.springware.config.sqlConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mukund.ghanwat
 * @Date 01/08/2024
 *
 * <b>TenantsConfig</b>
 * TenantsConfig is a simple Java class as shown below, will include the properties prefixed with app,
 * which is basically tenant information to establish connections to the DB.
 * Configuration of multiple databases is maintained in application.yaml file
 * and it creates dynamically TenantData objects at startup using this class.
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@PropertySource(value = "classpath:application.yaml")
public class TenantsConfig {

    private Map<String, TenantData> tenants = new HashMap<>();

    private String defaultTenant;

    public String getDefaultTenant() {
        return defaultTenant;
    }

    public void setDefaultTenant(String defaultTenant) {
        this.defaultTenant = defaultTenant;
    }

    public Map<String, TenantData> getTenants() {
        return tenants;
    }

    public void setTenants(Map<String, TenantData> tenants) {
        this.tenants = tenants;
    }

    /**
     * <b>TenantData</b>
     * This class is used as setter methods for application.YAML file variables. This is to import that database details.
     * Note :: variable names used below in the TenantData class should match the variables in application.yaml file
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantData {

        private String dbName;
        private String user;
        private String password;
        private String host;
        private int port;
        private String smsProvider;
        private String smsProviderAPIKey;
        private String fromMailId;
        private String fromMailPassword;
        private String emailHost;
        private DataSource dataSource;

        public TenantData(String dbName, DataSource dataSource) {
            this.dbName = dbName;
            this.dataSource = dataSource;
        }
    }
}
