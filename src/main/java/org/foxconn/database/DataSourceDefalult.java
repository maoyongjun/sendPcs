package org.foxconn.database;

import org.foxconn.util.EmptyUtils;
import org.foxconn.util.PropUtils;

/**
 * @author:myz
 * @version 1.0
 */
public class DataSourceDefalult {
	private static String defaultData = DataSource.master;

	public static void setDefaultDb(String username, String db) {
		PropUtils.setConfigValue(username, db);
	}

	public static String getDefaultDb(String username) {
		if (EmptyUtils.isEmpty(PropUtils.getConfigValue(username))) {
			return defaultData;
		} else {
			return PropUtils.getConfigValue(username);
		}
	}
}
