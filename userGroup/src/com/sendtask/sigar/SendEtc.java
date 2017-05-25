package com.sendtask.sigar;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendEtc {
	private static Logger logger = LoggerFactory.getLogger(SendEtc.class);
	
	public static boolean stopFlag=false;
	private static String etcInfoToStop="CacheSize=15360, TotalSockets=1, Mhz=2099, Model=Xeon, TotalCores=2, Vendor=Intel, CoresPerSocket=2};CPU states: 100.0% user, 100.0% system, 0.0% nice, 0.0% wait, 0.0% idle;{User=1649950, SoftIrq=62840, Idle=529670210, Stolen=0, Wait=66760, Total=533325900, Irq=29620, Nice=30, Sys=1846490};Mem: 3908728K av, 2529128K used, 8192000K free;Swap: 8192000K av, 8192000K used, 0K free";

	//FIXME 删掉！！！
	//public static boolean testFlag=true;
	public static String send() {
		logger.info("报告负载");
		if(stopFlag){
			logger.info("报告假的负载");
			return etcInfoToStop;
		}
		//FIXME 删掉！！！
		//if(testFlag){
	//		logger.info("报告临时测试用的负载");
		//	return "CacheSize=15360, TotalSockets=1, Mhz=2099, Model=Xeon, TotalCores=2, Vendor=Intel, CoresPerSocket=2};CPU states: 0.0% user, 0.0% system, 0.0% nice, 0.0% wait, 0.0% idle;{User=1649950, SoftIrq=62840, Idle=529670210, Stolen=0, Wait=66760, Total=533325900, Irq=29620, Nice=30, Sys=1846490};Mem: 3908728K av, 0K used, 8192000K free;Swap: 8192000K av, 0K used, 8192000K free";
		//}
		
		Sigar sigar = new Sigar();
		StringBuffer sb = new StringBuffer();

		CpuData cpuData = null;
		try {
			cpuData = CpuData.gather(sigar);
		} catch (SigarException e1) {
			logger.error("报告cpu负载报错");
		}
		if (cpuData != null) {
			sb.append(cpuData.getInfo());
			sb.append(";");
			sb.append(cpuData.getPerc());
			sb.append(";");
			sb.append(cpuData.getTimer());
			sb.append(";");
		}

		MemoryData memData = null;
		try {
			memData = MemoryData.gather(sigar);
		} catch (SigarException e) {
			logger.error("报告内存负载报错");
		}
		if (memData != null) {
			sb.append(memData.getMem());
			sb.append(";");
			sb.append(memData.getSwap());
		}
		return sb.toString();
	}
}
