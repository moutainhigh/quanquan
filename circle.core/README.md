## 2015年12月9日11:31:52 更新
1. 增加新API
    * Redis 的pub/sub 重连支持 
        
        public class TestAAA extends ReConnectPublish{
            @Override
            public void onMessage(String channel, String message) {
                //健康监测 - 信息
                checkreconnect(channel,message);
                //关闭信号
                if(message.equals("close")){
                    close();//关闭监听
                }
            }
        
            public static void main(String[] args) {
                PropertyConfigurator.configure("config/log4j.properties");
                TestAAA connectPublish = new TestAAA();
                connectPublish.setDb(0);//设置监听Redis库
                connectPublish.setDelay_check(1000);//监测周期 + wait 时间
                connectPublish.setDelay_connect(1000);//延时重连
                connectPublish.setDelay_wait(2000);//等待管道监听者收到消息
                connectPublish.subscribe(new HostAndPort("redis-1.wenaaa.com", 6379), "test_recopnnec");
            }
        }
        

## -- 2015年09月05日20:52:38 更新 CElastic 添加 update,delete 方法
    public class CElastic {
        ..........
        public boolean udpate(String table, String id, Map<String, String> hash) {
            UpdateRequestBuilder builder = client.prepareUpdate(circle_index, table, id);
            builder.setDoc(hash);
            return builder.get().isCreated();
        }
        public boolean delete(String table, String id) {
            return delete(circle_index,table,id);
        }
    
        public boolean delete(String index,String table, String id) {
            DeleteRequestBuilder builder = client.prepareDelete(index, table, id);
            return builder.get().isFound();
        }
        ..........
    }

Redis 增量Cluster API 实现
很简单的项目, 增量Redis,通过一定算法将对应的key映射到集群中的节点中去

