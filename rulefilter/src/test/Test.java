package test;

import java.io.IOException;

import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.rulesfilter.yy.zj.utils.ParamStatic;

/**
 * @author zhoujia
 *
 * @date 2015年9月6日
 */
public class Test {

	public static void main(String[] args) throws IOException, Exception {
		Redis.initialShard(new Config("config/conf.properties"));
		Redis.initial(new Config("config/conf.properties"));
		String log = "{\"beginAnswerTime\":123123123123123,\"endAnswoerTime\":123123123131278,\"beginSlectTime\":123123123123,\"endSlectTIme\":123123456456,\"questionId\":\"f9fbd7c6c4d2410d82bcf97a5e530f3b\"}";


		Redis.CONNECT.publish(ParamStatic.filterChannel, log);
		
	}
}
