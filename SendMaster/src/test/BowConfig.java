package test;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Properties;

public class BowConfig {
	private Properties properties;
    private String user_agent;

    public BowConfig(String path) {
		this.properties = new Properties();
		try {
			this.properties.load(new FileInputStream(new File(path)));
            user_agent = get("User-Agent");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String get(String key) {
		return this.properties.getProperty(key);
	}

    public String getUser_agent() {
        return user_agent;
    }

    public void setConfig(HttpURLConnection httpc) {
		httpc.setRequestProperty(HttpHeaderNames.USER_AGENT.toString(), user_agent);
	}


}
