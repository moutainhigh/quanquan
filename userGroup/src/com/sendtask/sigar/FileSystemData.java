package com.sendtask.sigar;
/** 
 * @author  zhoujia 
 * @date 创建时间：2015年7月22日 下午6:37:01   
 */

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * 文件系统数据
 * 
 * 使用Sigar获得文件系统信息
 * 
 * @author wangrui
 * 
 */
public class FileSystemData {

	private FileSystem config;
	private FileSystemUsage stat;

	public FileSystemData() {
	}

	public void populate(Sigar sigar, FileSystem fs) throws SigarException {

		config = fs;

		try {
			stat = sigar.getFileSystemUsage(fs.getDirName());
		} catch (SigarException e) {

		}
	}

	public static FileSystemData gather(Sigar sigar, FileSystem fs) throws SigarException {

		FileSystemData data = new FileSystemData();
		data.populate(sigar, fs);
		return data;
	}

	public FileSystem getConfig() {
		return config;
	}

	public FileSystemUsage getStat() {
		return stat;
	}
}