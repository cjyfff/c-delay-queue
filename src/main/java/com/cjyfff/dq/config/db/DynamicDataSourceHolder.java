package com.cjyfff.dq.config.db;

/**
 * Created by jiashen on 2018/9/16.
 */
public class DynamicDataSourceHolder {
    private static final ThreadLocal<DynamicDataSourceGlobal> HOLDER = new ThreadLocal<>();

    private DynamicDataSourceHolder() {
        //
    }

    public static void putDataSource(DynamicDataSourceGlobal dataSource){
        HOLDER.set(dataSource);
    }

    public static DynamicDataSourceGlobal getDataSource(){
        return HOLDER.get();
    }

    public static void clearDataSource() {
        HOLDER.remove();
    }

}
