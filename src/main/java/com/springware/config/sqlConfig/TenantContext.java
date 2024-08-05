package com.springware.config.sqlConfig;

/**
 * @author ankita.kulkarni
 * @Date 02/08/2019
 *
 *       <b>TenantContext</b> This is the configuration Class. It has a
 *       ThreadLocal set with the tenant/database name to be used.
 */

public class TenantContext {

	private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
	private static final ThreadLocal<String> DEFAULT_TENANT = new ThreadLocal<>();

	private static ThreadLocal<String> currentKYCType = new ThreadLocal<>();

	/**
	 * <b>getCurrentTenant</b> This method is used to get the current database /
	 * tenant name.
	 *
	 * @return current set client name
	 */
	public static String getCurrentTenant() {
		if (CURRENT_TENANT.get() != null)
			return CURRENT_TENANT.get();
		else
			return "m1";
	}

	/**
	 * <b>setCurrentTenant</b> This method is used to set the current database /
	 * tenant. Whenever any request will come, it will have tenant name (database to
	 * be connected). So we will call this method to set that name to current thread
	 * context.
	 *
	 * @param tenant name of the client
	 */
	public static void setCurrentTenant(String tenant) {
		CURRENT_TENANT.set(tenant);
	}

	public static void setDefaultTenant(String tenant) {
		DEFAULT_TENANT.set(tenant);
	}

	public static String getDefaultTenant() {
		return DEFAULT_TENANT.get();
	}

	public static void clear() {
		CURRENT_TENANT.remove();
	}
}
