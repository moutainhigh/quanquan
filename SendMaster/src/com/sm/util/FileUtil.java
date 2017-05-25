package com.sm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zhoujia
 *
 * @date 2015年7月16日
 */
public class FileUtil {
	
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static List<String> readFileByLines(String fileName) {
    	List<String> ruleList = new ArrayList<String>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	if(!"".equals(tempString) && !tempString.startsWith("#")){
            		ruleList.add(tempString);
            	}
            }
            reader.close();
            return ruleList;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return null;
    }
    
    public static String getDataType(String key){
    	Properties properties = new Properties();
         try {
         	File file = new File("config/task_conf.properties");
            properties.load(new FileInputStream(file));
            return properties.getProperty(key);
         } catch (IOException e) {
         	e.printStackTrace();
         }
         return null;
    }
    
    //写文件
    public static void crateContentFile(String url,String content) {
    	try {
			File f = new File(url);
			if(f.exists()){
				f.mkdirs();
			}
			FileOutputStream out = new FileOutputStream(f);
			out.write(content.getBytes());
			out.close();
		} catch (FileNotFoundException e) {
			logger.error("配置文件未找到",e); 
		} catch (IOException e) {
			logger.error("写pid出错",e); 
		}
    }
    
    public static void main(String[] args) throws IOException {
//    	List<String> readFileByLines = readFileByLines("config/rules");
//    	for (String string : readFileByLines) {
//			System.out.println(string);
//		}
//    	crateContentFile("D:/add.txt", "dafd");
    	
    	//Integer.parseInt("7*24");
	}
    
    
	
}
