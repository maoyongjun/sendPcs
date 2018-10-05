package org.foxconn.database;

import org.foxconn.util.PropUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() {
		DataSourceHolder.setDataSource(PropUtils.getConfigValue("db"));
			
		return DataSourceHolder.getDataSource();
	}

}