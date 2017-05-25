package com.sendtask.contentError;

/**
 * 容错解耦合用
 * 
 * @author qiuxy
 *
 */
public abstract class CEWatcher {
	public abstract void watcher(String dbType, String taskID, String spare);
}
