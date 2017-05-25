package com.sm.model;
/**
 * @author zhoujia
 *
 * @date 2015年9月9日
 */
public class NodeEtcInfo {
	
	/**cpu使用率**/
	private Double cpuPercent;
	/**内存使用率**/
	private Double memPercent;
	
	public NodeEtcInfo(Double cpuPercent,Double memPercent) {
		this.cpuPercent = cpuPercent;
		this.memPercent = memPercent;
	}

	@Override
	public String toString() {
		return "cpu使用率："+cpuPercent + "  内存使用率：" + memPercent;
	}
	
	public Double getCpuPercent() {
		return cpuPercent;
	}

	public void setCpuPercent(Double cpuPercent) {
		this.cpuPercent = cpuPercent;
	}

	public Double getMemPercent() {
		return memPercent;
	}

	public void setMemPercent(Double memPercent) {
		this.memPercent = memPercent;
	}
	
	
}
