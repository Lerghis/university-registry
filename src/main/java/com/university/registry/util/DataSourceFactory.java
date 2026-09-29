package com.university.registry.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceFactory
{
    private final HikariDataSource dataSource;

    public DataSourceFactory()
    {
        Properties props = loadProperties();
        this.dataSource = createDataSource(props);
    }

    private Properties loadProperties()
    {
        Properties props = new Properties();
        try (InputStream input = DataSourceFactory.class.getClassLoader().getResourceAsStream("db.properties"))
        {
            if (input == null)
            {
                throw new IllegalStateException("db.properties not found in the classpath");
            }
            props.load(input);
        }
        catch (IOException ex)
        {
            throw new IllegalStateException("Failed to load db.properties", ex);
        }
        return props;
    }

    private HikariDataSource createDataSource(Properties props)
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }

    public HikariDataSource getDataSource()
    {
        return dataSource;
    }

    public void close()
    {
        dataSource.close();
    }
}
