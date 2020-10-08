package com.cjyfff.dq.config.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by jiashen on 2018/9/16.
 */
@Configuration
@MapperScan(basePackages = "com.cjyfff.dq.task.mapper")
public class MyBatisConfig {
    @Value("${druidOption.setTestWhileIdle}")
    boolean testWhileIdle;

    @Value("${druidOption.setMaxWait}")
    int maxWait;

    @Value("${druidOption.setMinIdle}")
    int minIdle;

    @Value("${druidOption.setMaxActive}")
    int maxActive;

    @Value("${druidOption.setPoolPreparedStatements}")
    boolean poolPreparedStatements;

    @Value("${druidOption.dataSourceFilters}")
    String dataSourceFilters;

    @Value("${druidOption.setMultiStatementAllow}")
    private boolean multiStatementAllow;

    @Autowired
    @Qualifier("dynamicDataSource")
    private DynamicDataSource dataSource;

    // 必须要加`@Primary`不然会提示发现多个`DataSource`的Bean，怀疑spring的其他地方有`DataSource`的注入
    @Primary
    @Bean(initMethod = "init", destroyMethod = "close", name = "druidDataSourceWrite")
    public DruidDataSource druidDataSourceWrite(
        @Value("${jdbc.driverClassName}") String driver,
        @Value("${jdbc.write.url}") String url,
        @Value("${jdbc.write.username}") String username,
        @Value("${jdbc.write.password}") String password) {

        return setDataSourcePrams(driver, url, username, password);
    }

    @Bean(initMethod = "init", destroyMethod = "close", name = "druidDataSourceRead")
    public DruidDataSource druidDataSourceRead(
        @Value("${jdbc.driverClassName}") String driver,
        @Value("${jdbc.read.url}") String url,
        @Value("${jdbc.read.username}") String username,
        @Value("${jdbc.read.password}") String password) {

        return setDataSourcePrams(driver, url, username, password);
    }


    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setConfigLocation(resolver.getResource("classpath:mybatis-config.xml"));
        //        下边两设置用于*.xml文件，其中setTypeAliasesPackage用于mapper xml中的resultType映射，
        //        setMapperLocations用于指定mapper xml的路径
        factoryBean.setTypeAliasesPackage("com.cjyfffblog.po");
        factoryBean.setMapperLocations(resolver.getResources("classpath:mybatis-mappers/*.xml"));

        Interceptor[] plugins = {new DynamicPlugin()};

        factoryBean.setPlugins(plugins);
        return factoryBean.getObject();
    }

    /**
     * 替代 xml 配置：<tx:annotation-driven transaction-manager="DynamicDataSourceTransactionManager"/>
     * @return
     */
    @Bean
    public PlatformTransactionManager dataSourceTransactionManager(){
        DataSourceTransactionManager dataSourceTransactionManager = new DynamicDataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

    private DruidDataSource setDataSourcePrams(String driver, String url, String username, String password) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        List<Filter> proxyFilters = new ArrayList<>();
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(multiStatementAllow);
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig);
        proxyFilters.add(wallFilter);
        druidDataSource.setProxyFilters(proxyFilters);
        try {
            druidDataSource.setFilters(dataSourceFilters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }
}
