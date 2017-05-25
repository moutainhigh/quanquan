package test;

import java.io.IOException;

import com.circle.core.util.Config;

/**
 * @author zhoujia
 *
 * @date 2015年7月27日
 */
public class ConfigTest {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		for (int i = 0; i < 10; i++) {
			Config cg = new Config("config/task_conf.properties");
			
			threadA a = new threadA(cg);
			Thread.sleep(1000);
			System.out.println(cg.getAsString("zhitou.jar"));
		}
	}

}
class threadA extends Thread{
	Config cg;
	public threadA(Config cg) {
		this.cg = cg;
	}
	@Override
	public void run() {
		synchronized (cg) {
			System.out.println("value=="+cg.getAsInteger("zhitou.jar"));
		}
		
	}
}