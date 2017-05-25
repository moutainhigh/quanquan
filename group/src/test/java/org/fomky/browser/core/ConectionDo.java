package org.fomky.browser.core;

import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public abstract class ConectionDo {
	private HttpURLConnection connection;

	public ConectionDo() {

	}

	public void setConnection(HttpURLConnection connection) {
		this.connection = connection;
	}

	public InputStream getInput() {
		try {
			if (connection.getDoInput()) {
				return connection.getInputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public OutputStream getOutputStream() {
		try {
			if (connection.getDoOutput())
				return connection.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public abstract void doSomething(HttpURLConnection paramHttpURLConnection) throws IOException;
	public abstract void doSomething(CloseableHttpResponse response) throws IOException;

}