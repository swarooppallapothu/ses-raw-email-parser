package com.aws;

import java.util.Properties;

public class ApplicationProperties {

    private static Properties properties;

    static {
        properties = new Properties();
        loadProperties();
    }

    private static void loadProperties() {
        try {

            properties.clear();
            ClassLoader classLoader = (new ApplicationProperties()).getClass().getClassLoader();
            properties.load(classLoader.getResourceAsStream("application.properties"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String property) {
        return properties.getProperty(property);
    }

}
