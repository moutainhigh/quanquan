package test;


import java.util.List;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.rulesfilter.yy.zj.subscrib.RecordToHbase;
import com.rulesfilter.yy.zj.utils.ParamStatic;

/**
 * @author zhoujia
 *
 * @date 2015年8月7日
 */
public class Test2 {
	public static void main(String[] args) throws Exception {
		Redis.initialShard(new Config("config/conf.properties"));
		Redis.initial(new Config("config/conf.properties"));
		
		//Redis.CONNECT.publish(ParamStatic.key_timeout_channel, "7");
		//Redis.shard.setex(ParamStatic.tmpkey_pix+"userId|logId|ruleId", 5, "111");
		String string = Redis.shard.get("filterRule");
		String pointrule = Redis.shard.get("pointRule");
		List<String> lrange = Redis.shard.lrange("FILTERPROCESS|all", 0, -1);
		List<String> lrange1 = Redis.shard.lrange("FILTERPOINT|all", 0, -1);
		String freshRule = Redis.shard.get("FRESHFILTERRULE|KEY");
//		System.out.println(string); 
//		System.out.println(lrange);
//		System.out.println(pointrule);
//		System.out.println(lrange1);
//		System.out.println(freshRule);
//		List<String> aaaaaa = Redis.shard.lrange("selectPriceKey", 0, -1);
//		System.out.println("aaa ="+aaaaaa); 
		
		//Redis.shard.del("payPriceKey");
		HostAndPort hostAndPort = Redis.CONNECT.hostAndPort("filterName"); 
		 
		Jedis jedis = new Jedis(hostAndPort.getHost(),hostAndPort.getPort());
		JedisPubSub jp = new RecordToHbase();
		//jedisPubSub
		jedis.subscribe(jp, "filterName");
		
		
		List<String> lrange11 = Redis.shard.lrange("payPriceKey", 0, -1);
		System.out.println("payPriceKey = "+lrange11);
	}

}
