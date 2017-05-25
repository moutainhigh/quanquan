package com.circle.core.util;

import redis.clients.util.JedisClusterCRC16;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;

@SuppressWarnings("unused")
public class FileUtil {
	public final static String UTF_8 = "UTF-8";
	public static final String FILE_RW = "rw";
	public static final String ENTER = "\r\n";
	public static final SimpleDateFormat FORMA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * 向文件末尾追加 , 一行文字.
	 * @param path
	 * @param strs
	 * @throws IOException
	 */
	public static void appendStrToFile(String path,String strs) throws IOException{
		//检测文件是否存在 . 不存在,创建该文件
		File file = new File(path);
		if(!file.exists()){
			File dir = new File(path.substring(0,path.lastIndexOf("/")));
			if(!dir.exists())
				dir.mkdirs();
			file.createNewFile();
		}
		appendStrToFile(file, strs);
	}
	/**
	 * 向文件末尾追加 , 一行文字.
	 * @param file
	 * @param strs
	 * @throws IOException
	 */
	public static void appendStrToFile(File file,String strs) throws IOException{
		//"data/nio-data.txt"
		RandomAccessFile aFile = new RandomAccessFile(file, FILE_RW);  
		aFile.seek(aFile.length()); //讲指针指向文件的末尾
		FileChannel channel = aFile.getChannel(); 
		byte[] str = (strs+ENTER).getBytes(UTF_8);
		ByteBuffer buffer = ByteBuffer.allocate(str.length);
		buffer.put(str);
		buffer.clear();
		channel.write(buffer);
		buffer.flip();
		channel.close();
		aFile.close();
	}

	private static String createMsgDataFileName(String fuid, String tuid) {
		StringBuilder builder = new StringBuilder();
		if(JedisClusterCRC16.getSlot(tuid)>JedisClusterCRC16.getSlot(fuid)){
			builder.append(fuid).append("-").append(tuid) ;
		}else{
			builder.append(tuid).append("-").append(fuid) ;
		}
		builder.append("@");
		builder.append(todayDateString());
		return builder.toString();
	}
	public static String todayDateString(){
		return FORMA_DATE_FORMAT.format(System.currentTimeMillis());
	}
	
	 public static long amrDuration(File file) throws IOException {  
        long duration = -1;  
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };  
        RandomAccessFile randomAccessFile = null;  
        try {  
            randomAccessFile = new RandomAccessFile(file, "rw");  
            long length = file.length();//文件的长度  
            int pos = 6;//设置初始位置  
            int frameCount = 0;//初始帧数  
            int packedPos = -1;  
            /////////////////////////////////////////////////////  
            byte[] datas = new byte[1];//初始数据值  
            while (pos <= length) {  
                randomAccessFile.seek(pos);  
                if (randomAccessFile.read(datas, 0, 1) != 1) {  
                    duration = length > 0 ? ((length - 6) / 650) : 0;  
                    break;  
                }  
                packedPos = (datas[0] >> 3) & 0x0F;  
                pos += packedSize[packedPos] + 1;  
                frameCount++;  
            }  
            /////////////////////////////////////////////////////  
            duration += frameCount * 20;//帧数*20  
        } finally {  
            if (randomAccessFile != null) {  
                randomAccessFile.close();  
            }  
        }  
        return duration/1000;  
    }
	public static String renameAndGetLong(String filePath,File files) throws IOException {
		long times = amrDuration(files);
		String newFile = files.getName().replace(".amr", "_"+times+".amr");
		files.renameTo(new File(filePath + newFile));
		return newFile;
	} 
}
