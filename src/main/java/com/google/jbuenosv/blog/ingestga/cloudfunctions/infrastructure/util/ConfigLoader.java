package com.google.jbuenosv.blog.ingestga.cloudfunctions.infrastructure.util;

import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.exception.FirebaseProcessorException;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by jbuenosv@google.com
 */
public final class ConfigLoader {

    public static final Logger logger = Logger.getLogger(ConfigLoader.class.getName());

    private static ConfigLoader INSTANCE;
    private final Properties props;

    /**
     * Private constructor
     */
    private ConfigLoader() {
        try {
            props = new Properties();
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.properties");
            props.load(inputStream);
        }
        catch(Exception e) {
            logger.severe("Unable to load the configuration file.");
            throw new FirebaseProcessorException(e.getCause());
        }
    }

    /**
     * Gets the ConfigLoader instance
     * @return ConfigLoader instance
     */
    public static ConfigLoader getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ConfigLoader();
        }
        return INSTANCE;
    }

    /**
     * Gets a property value by name
     * @param propName property name
     * @return property value
     */
    public String getProperty(String propName) {
        return props.getProperty(propName);
    }

    /**
     * Gets a property value by name
     * @param propName property name
     * @return property value
     */
    public String getEnv(String propName) {
        return System.getenv(propName);
    }

}
