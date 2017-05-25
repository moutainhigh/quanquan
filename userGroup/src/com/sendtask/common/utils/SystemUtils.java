package com.sendtask.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class SystemUtils {
	public static int getPid() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName(); // format: "pid@hostname"
		try {
			return Integer.parseInt(name.substring(0, name.indexOf('@')));
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 获取进程PID
	 * 
	 * @param p
	 * @return
	 * @throws IOException
	 */
	public static String getPid(Process p) throws IOException {

		final InputStream ii = p.getInputStream();

		final InputStreamReader ir = new InputStreamReader(ii);

		final BufferedReader br = new BufferedReader(ir);
		String str = null;
		String pid = null;

		while ((str = br.readLine()) != null) {
			if (str.indexOf("pid") > -1) {
				pid = str.split(" ")[1];
			}
			break;
		}
		try {
			ir.close();
			ii.close();
			br.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return pid;
	}
}
