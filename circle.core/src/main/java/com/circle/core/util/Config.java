package com.circle.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Created by bjchenxx on 2015/2/3.
 */
public class Config {
    private Properties properties;

    /**
     * 
     * @param fileName 配置文件路径. 
     * @throws IOException 
     */
    public Config(String fileName) throws IOException {
        properties = new Properties();
        //properties.load(Config.class.getClassLoader().getResourceAsStream(fileName));
        File file = new File(fileName);
        properties.load(new FileInputStream(file));
    }
    public Config(File file) throws IOException {
        properties = new Properties();
        //properties.load(Config.class.getClassLoader().getResourceAsStream(fileName));
        properties.load(new FileInputStream(file));
    }
    public Config(InputStream inputStream) throws IOException {
        properties = new Properties();
        //properties.load(Config.class.getClassLoader().getResourceAsStream(fileName));
        properties.load(inputStream);
    }

    public Properties getProperties() {
        return properties;
    }

    public Integer getAsInteger(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }

    public String getAsString(String key) {
        return properties.getProperty(key);
    }
}