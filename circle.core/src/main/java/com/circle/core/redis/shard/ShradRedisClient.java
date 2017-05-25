package com.circle.core.redis.shard;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User this to connect many redis nodes , erver node need save , users data.
 *
 * @author Created by Fomky on 2015/6/7 0007.
 */
@SuppressWarnings("unused")
public class ShradRedisClient implements JedisCommands {
    private Logger logger = LoggerFactory.getLogger(ShradRedisClient.class);
    private ShardedJedisPool pool;
    private GenericObjectPoolConfig config;
    private List<JedisShardInfo> nodes;
    public static ExecutorService executor = Executors.newCachedThreadPool();

    public ShradRedisClient(GenericObjectPoolConfig config, List<JedisShardInfo> nodes) {
        this.config = config;
        this.nodes = nodes;
        pool = new ShardedJedisPool(config, nodes);
    }

    @Deprecated
    public ShardedJedis getResource() {
        return pool.getResource();
    }

    public void returnObjectResource(ShardedJedis jedis) {
        if (jedis != null) pool.returnResourceObject(jedis);
    }

    public ShardedJedis resource() {
        return pool.getResource();
    }

    public void close() {
        pool.close();
    }

    public ShardedJedisPool getPool() {
        return pool;
    }

    public void reconnection() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    pool.destroy();
                    pool = new ShardedJedisPool(config, nodes);
                } catch (Exception e) {
                    try {
                        logger.error("0.5 sec re try connext redis......... " + e.getMessage());
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        logger.error("0.5 sec re try connext redis", e1);
                    }
                    reconnection();
                }
            }
        });
    }


    /*===Redis  操作接口实现===========================================================*/
    public String set(String key, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.set(key, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String set(String key, String value, String nxxx, String expx, long time) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.set(key, value, nxxx, expx, time);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String get(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.get(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Boolean exists(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.exists(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long persist(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.persist(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String type(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.type(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long expire(String key, int seconds) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.expire(key, seconds);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long pexpire(String key, long milliseconds) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.pexpire(key, milliseconds);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long expireAt(String key, long milliseconds) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.expireAt(key, milliseconds);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long pexpireAt(String key, long millisecondsTimestamp) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.pexpireAt(key, millisecondsTimestamp);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long ttl(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.ttl(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Boolean setbit(String key, long offset, boolean value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.setbit(key, offset, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Boolean setbit(String key, long offset, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.setbit(key, offset, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Boolean getbit(String key, long offset) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.getbit(key, offset);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long setrange(String key, long offset, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.setrange(key, offset, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String getrange(String key, long startOffset, long endOffset) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.getrange(key, startOffset, endOffset);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String getSet(String key, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.getSet(key, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long setnx(String key, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.setnx(key, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String setex(String key, int seconds, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.setex(key, seconds, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long decrBy(String key, long num) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.decrBy(key, num);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long decr(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.decr(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long incrBy(String key, long num) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.incrBy(key, num);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Double incrByFloat(String key, double value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.incrByFloat(key, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long incr(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.incr(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long append(String key, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.append(key, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String substr(String key, int start, int end) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.substr(key, start, end);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long hset(String key, String field, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hset(key, field, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String hget(String key, String field) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hget(key, field);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long hsetnx(String key, String field, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hsetnx(key, field, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String hmset(String key, Map<String, String> hash) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hmset(key, hash);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public List<String> hmget(String key, String... values) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hmget(key, values);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long hincrBy(String key, String field, long integer) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hincrBy(key, field, integer);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Boolean hexists(String key, String field) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hexists(key, field);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long hdel(String key, String... fields) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hdel(key, fields);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long hlen(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hlen(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> hkeys(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hkeys(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public List<String> hvals(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hvals(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Map<String, String> hgetAll(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hgetAll(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long rpush(String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.rpush(key, strings);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long lpush(String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.lpush(key, strings);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long llen(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.llen(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public List<String> lrange(String key, long start, long end) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.lrange(key, start, end);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String ltrim(String key, long start, long end) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.ltrim(key, start, end);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String lindex(String key, long index) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.lindex(key, index);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String lset(String key, long index, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.lset(key, index, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long lrem(String key, long count, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.lrem(key, count, value);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String lpop(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.lpop(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String rpop(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.rpop(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long sadd(String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.sadd(key, strings);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> smembers(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.smembers(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long srem(String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.srem(key, strings);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String spop(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.spop(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> spop(String key, long count) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.spop(key, count);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long scard(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.scard(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Boolean sismember(String key, String member) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.sismember(key, member);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String srandmember(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.srandmember(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public List<String> srandmember(String key, int count) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.srandmember(key, count);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long strlen(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.strlen(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zadd(String key, double v, String s1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zadd(key, v, s1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zadd(String key, Map<String, Double> map) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zadd(key, map);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrange(String key, long l, long l1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrange(key, l, l1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zrem(String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrem(key, strings);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Double zincrby(String key, double v, String s1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zincrby(key, v, s1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zrank(String key, String s1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrank(key, s1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zrevrank(String key, String s1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrank(key, s1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrevrange(String key, long l, long l1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrange(key, l, l1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrangeWithScores(String key, long l, long l1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeWithScores(key, l, l1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeWithScores(String key, long l, long l1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeWithScores(key, l, l1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zcard(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zcard(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Double zscore(String key, String s1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zscore(key, s1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public List<String> sort(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.sort(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public List<String> sort(String key, SortingParams sortingParams) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.sort(key, sortingParams);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zcount(String key, double v, double v1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zcount(key, v, v1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zcount(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zcount(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrangeByScore(String key, double v, double v1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByScore(key, v, v1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrangeByScore(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByScore(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrevrangeByScore(String key, double v, double v1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByScore(key, v, v1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrangeByScore(String key, double v, double v1, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByScore(key, v, v1, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrevrangeByScore(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByScore(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrangeByScore(String key, String s1, String s2, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByScore(key, s1, s2, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrevrangeByScore(String key, double v, double v1, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByScore(key, v, v1, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double v, double v1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByScoreWithScores(key, v, v1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double v, double v1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByScoreWithScores(key, v, v1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double v, double v1, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByScoreWithScores(key, v, v1, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrevrangeByScore(String key, String s1, String s2, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByScore(key, s1, s2, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByScoreWithScores(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByScoreWithScores(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, String s1, String s2, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByScoreWithScores(key, s1, s2, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double v, double v1, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByScoreWithScores(key, v, v1, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, String s1, String s2, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByScoreWithScores(key, s1, s2, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zremrangeByRank(String key, long l, long l1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zremrangeByRank(key, l, l1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zremrangeByScore(String key, double v, double v1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zremrangeByScore(key, v, v1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zremrangeByScore(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zremrangeByScore(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zlexcount(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zlexcount(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrangeByLex(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByLex(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrangeByLex(String key, String s1, String s2, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrangeByLex(key, s1, s2, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrevrangeByLex(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByLex(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> zrevrangeByLex(String key, String s1, String s2, int i, int i1) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zrevrangeByLex(key, s1, s2, i, i1);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long zremrangeByLex(String key, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zremrangeByLex(key, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long linsert(String key, BinaryClient.LIST_POSITION list_position, String s1, String s2) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.linsert(key, list_position, s1, s2);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long lpushx(String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.lpushx(key, strings);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long rpushx(String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.rpushx(key, strings);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    @Deprecated
    public List<String> blpop(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.blpop(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public List<String> blpop(int i, String s) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.blpop(i, s);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    @Deprecated
    public List<String> brpop(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.brpop(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public List<String> brpop(int i, String s) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.brpop(i, s);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long del(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.del(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public String echo(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.echo(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long move(String key, int dbIndex) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.move(key, dbIndex);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long bitcount(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.bitcount(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long bitcount(String key, long start, long end) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.bitcount(key, start, end);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    @Deprecated
    public ScanResult<Map.Entry<String, String>> hscan(String key, int i) {
        return null;
    }

    @Deprecated
    public ScanResult<String> sscan(String key, int i) {
        return null;
    }

    @Deprecated
    public ScanResult<Tuple> zscan(String key, int i) {
        return null;
    }

    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.hscan(key, cursor);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public ScanResult<String> sscan(String key, String cursor) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.sscan(key, cursor);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public ScanResult<Tuple> zscan(String key, String cursor) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.zscan(key, cursor);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long pfadd(String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.pfadd(key, strings);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public long pfcount(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = resource();
            return jedis.pfcount(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return 0;
        } finally {
            returnObjectResource(jedis);
        }
    }
}