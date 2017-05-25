package com.circle.wena.task.huanxin.comm;

import com.circle.wena.task.huanxin.httpclient.vo.EndPoints;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PropertiesUtils
 * 
 * @author Lynch 2014-09-15
 *
 */
public class PropertiesUtils {
	static Properties properties;
	public static void getProperties(String config) throws IOException {
		properties = new Properties();
		InputStream inputStream = new FileInputStream(config);
		properties.load(inputStream);
		Constants.inital();
		EndPoints.instal();
	}

	public static Properties getProperties() {
		return properties;
	}

}
