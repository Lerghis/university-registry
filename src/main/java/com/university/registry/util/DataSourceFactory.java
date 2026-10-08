package com.university.registry.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The purpose of this class is to manufacture a HikariDataSource object from raw materials (the properties file).
 * First, it calls the loadProperties method to load the credentials file and stores the output to a Properties object.
 * Then it populates a HikariConfig object with the credentials (the contents of the properties file) and passes the
 * HikariConfig object to a HikariDataSource one, which constructs the connection pool.
 *
 * Sum up: It reads the credentials file, populates a config, constructs the pool.
 */
public class DataSourceFactory
{
    /*
    Field variable because the value of dataSource (i.e. the contents of db.properties) will be called by other classes
    long after the constructor of this class finished running. The object needs to store it somewhere permanent because it
    will be used in other method calls over the life of the program.
     */
    private final HikariDataSource dataSource;

    public DataSourceFactory()
    {
        Properties props = loadProperties(); // Local variable because the value (i.e. the db.properties) is only needed to hand it forward to dataSource, then forget it.
        this.dataSource = createDataSource(props); // The constructor doesn't accept a HikariDataSource as parameter because no other class in the program has any way to build a HikariDataSource obj except the current one.
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
