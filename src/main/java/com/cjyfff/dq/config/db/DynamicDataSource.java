package com.cjyfff.dq.config.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/9/16.
 */
@Component("dynamicDataSource")
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Autowired
    @Qualifier("druidDataSourceWrite")
    private DataSource writeDataSource;

    @Autowired
    @Qualifier("druidDataSourceRead")
    private DataSource readDataSource;

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * 应该是 Spring IOC 加载完这个类后会自动调用的协议方法
     */
    @Override
    public void afterPropertiesSet() {
        if (this.writeDataSource == null) {
            throw new IllegalArgumentException("Property 'writeDataSource' is required");
        }
        setDefaultTargetDataSource(writeDataSource);
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DynamicDataSourceGlobal.WRITE.name(), writeDataSource);
        if(readDataSource != null) {
            targetDataSources.put(DynamicDataSourceGlobal.READ.name(), readDataSource);
        }
        setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {

        DynamicDataSourceGlobal dynamicDataSourceGlobal = DynamicDataSourceHolder.getDataSource();

        if(dynamicDataSourceGlobal == null
            || dynamicDataSourceGlobal == DynamicDataSourceGlobal.WRITE) {
            logger.info("DynamicDataSource set Write mode...");
            return DynamicDataSourceGlobal.WRITE.name();
        }

        logger.info("DynamicDataSource set Read mode...");
        return DynamicDataSourceGlobal.READ.name();
    }

    public Object getWriteDataSource() {
        return writeDataSource;
    }

    public void setReadDataSource(DataSource readDataSource) {
        this.readDataSource = readDataSource;
    }

    public Object getReadDataSource() {
        return readDataSource;
    }

    public void setWriteDataSource(DataSource writeDataSource) {
        this.writeDataSource = writeDataSource;
    }
}

