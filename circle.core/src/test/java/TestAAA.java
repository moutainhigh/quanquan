import com.circle.core.redis.incr.ReConnectPublish;
import org.apache.log4j.PropertyConfigurator;
import redis.clients.jedis.HostAndPort;

/**
 * @author Created by cxx15 on 2015/12/4.
 */
public class TestAAA extends ReConnectPublish{
    @Override
    public void onMessage(String channel, String message) {
        checkreconnect(channel,message);
        if(message.equals("close")){
            close();
        }
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure("config/log4j.properties");
        TestAAA connectPublish = new TestAAA();
        connectPublish.setDb(0);//设置监听Redis库
        connectPublish.setDelay_check(1000);//监测周期 + wait 时间
        connectPublish.setDelay_connect(1000);//延时重连
        connectPublish.setDelay_wait(2000);//等待管道监听者收到消息
        connectPublish.subscribe(new HostAndPort("redis-1.wenaaa.com", 6379), "questionLogFilter");
        connectPublish.subscribe(new HostAndPort("redis-1.wenaaa.com", 6379), "questionLogFilter");
        System.out.println("开始了哈哈");
    }
}
