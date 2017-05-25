package com.circle.core.redis;

import com.circle.core.redis.incr.IncrRedisClient;
import com.circle.core.redis.shard.ShradRedisClient;
import com.circle.core.util.CircleMD5;
import com.circle.core.util.Config;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisShardInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化Redis 连接, 并且提供相关简易的查询功能 .
 *
 * @author clicoy
 */
@SuppressWarnings("unused")
public class Redis {
    // 用户ID 生成获取
    public final static String UID = "UID-SYNC";
    //虚拟用户ID 生成策略
    public final static String VID = "VID-SYNC";
    public final static String PR_VID = "V";
    // 回答ID 生成计算器
    public final static String PR_UID = "U";
    private static Logger logger = LoggerFactory.getLogger(Redis.class);
    //Redis 连接池
    public static IncrRedisClient CONNECT;

    public static ShradRedisClient shard;

    /**
     * id format 神器
     */
    public static DecimalFormat ID_FORMAT = new DecimalFormat("000000000");

    public static void initialShard(Config conf) {
        logger.info("== Initial ShardInfoRedisPool config ==========================");
        List<JedisShardInfo> shards = new ArrayList<>();
        String[] redis_nodes = conf.getAsString("redis.shard.nodes").split(",");
        int MaxTotal = conf.getAsInteger("redis.pool.MaxTotal");
        int MaxIdle = conf.getAsInteger("redis.pool.MaxIdle");
        int MinIdle = conf.getAsInteger("redis.pool.MinIdle");
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(MaxTotal);
        config.setMaxIdle(MaxIdle);
        config.setMinIdle(MinIdle);
        for (String node : redis_nodes) {
            logger.info("Redis node = " + node);
            JedisShardInfo hp = new JedisShardInfo(node.split(":")[0], Integer.valueOf(node.split(":")[1]));
            shards.add(hp);
        }
        shard = new ShradRedisClient(config, shards);
    }

    public static void initial(Config conf) throws Exception {
        // 如果 配置文件类为空 则抛出异常 , 启动失败
        logger.info("== Inital REDIS Config     ====================================");
        //CONNECT = new IncrRedisClient(conf);
        //初始化集群列链接
        List<HostAndPort> hps = new ArrayList<>();
        String[] redis_nodes = conf.getAsString("redis.incr.nodes").split(",");
        int MaxTotal = conf.getAsInteger("redis.pool.MaxTotal");
        int MaxIdle = conf.getAsInteger("redis.pool.MaxIdle");
        int MinIdle = conf.getAsInteger("redis.pool.MinIdle");
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(MaxTotal);
        config.setMaxIdle(MaxIdle);
        config.setMinIdle(MinIdle);
        for (String node : redis_nodes) {
            logger.info("Redis node = " + node);
            HostAndPort hp = new HostAndPort(node.split(":")[0], Integer.valueOf(node.split(":")[1]));
            hps.add(hp);
        }
        CONNECT = new IncrRedisClient(config, hps, "circle");
        logger.info("== Inital REDIS Config Successfully ! ===================");
    }

    /**
     * 获取对应值 key
     *
     * @param table redis String 类型Key前缀
     * @param key   Key后缀
     * @return 返回字符串类型
     */
    public static String get(String table, String key) {
        return CONNECT.get(table + key);
    }

    public static Boolean exists(String table, String key) {
        return CONNECT.exists(table + key);
    }

    public static Long incr(String table, String key) {
        return CONNECT.incr(table + key);
    }

    public static Long expire(String table, String key, int seconds) {
        return CONNECT.expire(table + key, seconds);
    }

    public static void del(String table, String mobile) {
        CONNECT.del(table + mobile);
    }

    /**
     * 生成ID
     *
     * @param key key
     * @return long after incr
     */
    public static Long incr(String key) {
        return CONNECT.incr(key);
    }

    public static String QID() {
        return CircleMD5.uuid();
    }

    public static String TID() {
        return CircleMD5.uuid();
    }

    public static long MSGID() {
        return incr(UID);
    }

    public static String UID() {
        return PR_UID + ID_FORMAT.format(incr(UID));
    }

    public static String VID() {
        return PR_VID + ID_FORMAT.format(incr(VID));
    }

    public static String AID() {
        return CircleMD5.uuid();
    }
}
