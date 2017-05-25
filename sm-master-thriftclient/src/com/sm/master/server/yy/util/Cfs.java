package com.sm.master.server.yy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by bjchenxx on 2015/2/3.
 */
public class Cfs {
    private Properties properties;
    
    /**
     * 
     * @param fileName 配置文件路径. 
     * @throws IOException 
     */
    public Cfs(String fileName) throws IOException {
        properties = new Properties();
        try {
            //properties.load(Cfs.class.getClassLoader().getResourceAsStream(fileName));
        	File file = new File(fileName);
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
        	throw e;
        }
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
