package com.sm.util.sigar;
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
        
    public static void main(String[] args) throws Exception {    
        Sigar sigar = new Sigar();    
        MemoryData memData = MemoryData.gather(sigar);  
        
        System.out.println(memData.mem);
        System.out.println(memData.swap);
//        XStream xstream = new XStream();    
//        xstream.alias("MemData", MemoryData.class);    
//        System.out.println(xstream.toXML(memData));    
    }    
    
}    