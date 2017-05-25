package com.sendtask.etc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendtask.common.model.Slave;
import com.sendtask.common.utils.TaskConfig;

public class Etc {

	private static Logger logger = LoggerFactory.getLogger(Etc.class);
	
	/**
	 * 每台机器的负载情况
	 */
	public Map<String, String> etcInfo = new HashMap<String, String>();

	/**
	 * 可用节点
	 */
	public static List<String> availableSlave = new ArrayList<String>();

	public static List<Slave> calculationEtc(List<Slave> slaveEtcs) {
		for (Slave slave : slaveEtcs) {
			double used = getCupUesd(slave.getEtcData());
			logger.info(slave.getHost() + "===========" + used + "分");
			if (used >= TaskConfig.cpuPersint) {
				slaveEtcs.remove(slave);
			}
		}
		return slaveEtcs;
	}

	/**
	 * 读取信息中的cup占用率 {CacheSize=6144, TotalSockets=4, Mhz=3300, Model=Core(TM)
	 * i5-4440 CPU @ 3.10GHz, TotalCores=4, Vendor=Intel, CoresPerSocket=16};
	 * CPU states: 9.6% user, 1.0% system, 0.0% nice, 3.0% wait, 86.2% idle;
	 * {User=3456760, SoftIrq=480, Idle=693590340, Stolen=0, Wait=594340,
	 * Total=698321840, Irq=0, Nice=31620, Sys=648300}; Mem: 7897144K av,
	 * 4831364K used, 3065780K free; Swap: 8126460K av, 0K used, 8126460K free;
	 * 
	 * @param info
	 * @return
	 */
	private static double getCupUesd(String info) {
		try {
			if (info == null || "".equals(info)) {
				return 100;
			}

			logger.info("节点负载信息：" + info);
			String proc = info.split(";")[1];
			String user = proc.split(":")[1].split(",")[0];
			String persint = user.substring(0, user.indexOf("user"));
			persint = persint.substring(0, persint.indexOf("%"));
			return Double.parseDouble(persint);
		} catch (NumberFormatException e) {
			logger.debug("读取cpu占用率出错:", e);
		}
		return 100;
	}

}
