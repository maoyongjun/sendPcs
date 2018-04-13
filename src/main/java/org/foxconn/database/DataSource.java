package org.foxconn.database;
import java.lang.annotation.*;
/**
* @author:myz
* @version 1.0 
*/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String name() default DataSource.master;
 
    public static String master = "dataSource2";
 
    public static String slave1 = "dataSource1";
 
    public static String slave2 = "dataSource3";
 
}