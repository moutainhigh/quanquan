package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.rulesfilter.yy.zj.utils.ParamStatic;

/**
 * @author zhoujia
 *
 * @date 2015年12月8日
 * 
 * 删除队列，包括点对点队列和过程规则队列,请谨慎操作
 */
public class DelList {

	public static void main(String[] args) throws Exception {
		Redis.initialShard(new Config("config/conf.properties"));
		Redis.initial(new Config("config/conf.properties"));
		Config config = new Config(ParamStatic.configPath + "/conf.properties");
		String asString = config.getAsString("redis.shard.nodes");
		String[] hostAndPort = asString.split(",");
		List<String> keys = new ArrayList<String>();
		for (String string : hostAndPort) {
			Jedis jedis = new Jedis(string.split(":")[0],Integer.parseInt(string.split(":")[1]));
			Set<String> keys2 = jedis.keys("ACC|*");
			Set<String> keys3 = jedis.keys("FILTERPROCESS*");
			Set<String> keys4 = jedis.keys("record|tmp*");
			keys.addAll(keys2);
			keys.addAll(keys3);
			keys.addAll(keys4);
			jedis.close();
		}
		for (String string : keys) {
			System.out.println("key=="+string);
			//Thread.sleep(1000);
			if(string.endsWith("filterRule_point1") 
					|| string.endsWith("filterRule_point2")
					|| string.endsWith("filterRule_point3")
					|| string.endsWith("filterRule_point4")
					|| string.endsWith("filterRule_process1")
					|| string.endsWith("filterRule_process2")
					|| string.endsWith("filterRule_process3")
					|| string.endsWith("filterRule_process4")|| string.startsWith("record|tmp")){
				System.out.println("要删除的key=="+string);
				Redis.shard.del(string);
			}
			Redis.shard.del(string);
			
		}
		//Redis.shard.del("ACC|f3ac1ae7f0134701a207fcd4a37cb8c6|0af70920eb084c2cb7240ebbe836d714|filterRule_point4");
		System.out.println("总长度="+keys.size());
	}
}
