package com.circle.core.redis.incr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据迁移类.
 * Created by clicoy on 15-6-15.
 */
public class DataMigration {
    Logger logger = LoggerFactory.getLogger(DataMigration.class);

    private IncrRedisClient client;

    public DataMigration(IncrRedisClient client) {
        this.client = client;
    }

    public void migration(String pattern) {
        Collection<HostAndPort> nodeInfos = client.getNodes().values();
        for (HostAndPort hostAndPort : nodeInfos) {
            ndoeaToNodeb(hostAndPort, pattern);
        }
    }

    public void scanAll() {
        migration("");
    }

    /**
     * 数据迁移. 添加节点 -- >  争取后面做到 可以不重启服务的 同时 , 可以动态的添加redis节点
     */
    public void ndoeaToNodeb(HostAndPort from, String table) {
        Jedis fromJedis = new Jedis(from.getHost(), from.getPort());
        ndoeDataToCluster(fromJedis, table);
        fromJedis.close();
    }

    /**
     * 数据迁移. 添加节点 -- >  争取后面做到 可以不重启服务的 同时 , 可以动态的添加redis节点
     * 将Jedis 所在节点的数据写入到 集群中.
     */
    public void ndoeDataToCluster(Jedis jedis, String table) {
        String pattern = table + "*";
        Set<String> keys = jedis.keys(pattern);
        for (String key : keys) {
            String type = jedis.type(key);
            if (type != null) {
                switch (type) {
                    case KeyType.TYPE_HASH:
                        Map<String, String> hash = jedis.hgetAll(key);
                        client.hmset(key, hash);
                        logger.info("Key=" + key + ", value=" + hash);
                        break;
                    case KeyType.TYPE_STRING:
                        String value = jedis.get(key);
                        client.set(key, value);
                        logger.info("Key=" + key + ", value=" + value);
                        break;
                    case KeyType.TYPE_LIST:
                        long len = jedis.llen(key);
                        List<String> values = jedis.lrange(key, 0, len);
                        logger.info("Key=" + key + ", value=" + values);
                        for (int i = 0; i < len; i++) {
                            client.lpush(key, values.get(i));
                        }
                        break;
                }
            }
        }
        logger.info("Host=" + jedis.getClient().getHost() + " , Port=" + jedis.getClient().getPort());
    }
}
