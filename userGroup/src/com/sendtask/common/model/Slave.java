package com.sendtask.common.model;

/** 
 * @author  zhoujia 
 * @date 创建时间：2015年7月16日 下午2:50:13   
 */
public class Slave {
	
	private String ip;
	private Integer port;
	
	private String host;
	
	private String etcData;
	
//	/**记录本台机器上用户数量，每次分配任务则挑选用户最少的机器进行分配**/
//	private Integer userCount;
//	
//	/**任务列表  key - taskID  **/
//	private Map<String, SMTask> taskMap;

	public String getIp() {
		return ip;
	}

	/**
	 * @return the etcData
	 */
	public String getEtcData() {
		return etcData;
	}

	/**
	 * @param etcData the etcData to set
	 */
	public void setEtcData(String etcData) {
		this.etcData = etcData;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}


	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}


	

}
