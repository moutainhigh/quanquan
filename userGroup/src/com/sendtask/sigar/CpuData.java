package com.sendtask.sigar;
/** 
 * @author  zhoujia 
 * @date 创建时间：2015年7月22日 下午6:00:48   
 */

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

//import com.thoughtworks.xstream.XStream;    

/**
 * Cpu数据
 * 
 * 使用Sigar获得CPU的基本信息、使用百分比、使用时间
 * 
 */
public class CpuData {
	private CpuInfo info;
	private CpuPerc perc;
	private Cpu timer;

	/**
	 * @return the info
	 */
	public CpuInfo getInfo() {
		return info;
	}

	/**
	 * @param info
	 *            the info to set
	 */
	public void setInfo(CpuInfo info) {
		this.info = info;
	}

	/**
	 * @return the perc
	 */
	public CpuPerc getPerc() {
		return perc;
	}

	/**
	 * @param perc
	 *            the perc to set
	 */
	public void setPerc(CpuPerc perc) {
		this.perc = perc;
	}

	/**
	 * @return the timer
	 */
	public Cpu getTimer() {
		return timer;
	}

	/**
	 * @param timer
	 *            the timer to set
	 */
	public void setTimer(Cpu timer) {
		this.timer = timer;
	}

	public CpuData() {
	}

	public void populate(Sigar sigar) throws SigarException {
		info = sigar.getCpuInfoList()[0];
		perc = sigar.getCpuPerc();
		timer = sigar.getCpu();
	}

	public static CpuData gather(Sigar sigar) throws SigarException {
		CpuData data = new CpuData();
		data.populate(sigar);
		return data;
	}

}