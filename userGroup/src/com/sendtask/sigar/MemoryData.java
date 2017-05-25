package com.sendtask.sigar;
/** 
 * @author  zhoujia 
 * @date 创建时间：2015年7月22日 下午6:13:07   
 */

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

/**
 * 内存数据
 * 
 * 使用Sigar获得系统内存信息
 * 
 */
public class MemoryData {
	private Mem mem;
	private Swap swap;

	/**
	 * @return the mem
	 */
	public Mem getMem() {
		return mem;
	}

	/**
	 * @param mem
	 *            the mem to set
	 */
	public void setMem(Mem mem) {
		this.mem = mem;
	}

	/**
	 * @return the swap
	 */
	public Swap getSwap() {
		return swap;
	}

	/**
	 * @param swap
	 *            the swap to set
	 */
	public void setSwap(Swap swap) {
		this.swap = swap;
	}

	public MemoryData() {
	}

	public void populate(Sigar sigar) throws SigarException {
		mem = sigar.getMem();
		swap = sigar.getSwap();
	}

	public static MemoryData gather(Sigar sigar) throws SigarException {
		MemoryData data = new MemoryData();
		data.populate(sigar);
		return data;
	}

}