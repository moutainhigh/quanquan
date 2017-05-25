package com.sm.util.sigar;
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
   
   public static void main(String[] args) throws Exception {
       	Sigar sigar = new Sigar();    
       	CpuData cpuData = CpuData.gather(sigar);    
         System.out.println(cpuData.info);
         System.out.println(cpuData.perc);
         System.out.println(cpuData.timer);
       //XStream xstream = new XStream();    
       //xstream.alias("CpuData", CpuData.class);    
       //System.out.println(xstream.toXML(cpuData));    
   }
   
   
   
   
   
   
   
   
} 