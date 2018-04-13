package org.foxconn.database;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
 
/**
 */
public class DynamicDataSource extends AbstractRoutingDataSource{
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getDataSource();
    }

}