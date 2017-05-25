package com.circle.core.redis.incr;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.JedisClusterCRC16;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User this to connect many redis nodes , erver node need save , users data.
 * Created by Fomky on 2015/6/7 0007.
 */
public class IncrRedisClient implements JedisCommands {
    private Logger logger = LoggerFactory.getLogger(IncrRedisClient.class);
    /**
     * 使用 IP 去映射 每个节点的连接池.
     */
    private Map<String, JedisPool> ipPool;
    /**
     * 使用数字去映射,连接池.
     */
    private Map<Integer, JedisPool> numberPool;

    private List<HostAndPort> nodes_l;
    private GenericObjectPoolConfig config;
    /**
     * 存储,当前节点个数
     */
    private int size;
    private long maxnumber = 3000;
    /**
     * 集群名称
     */
    private String clusterName;
    /**
     * 默认本地IP
     */
    private final static String local = "127.0.0.1";
    private final static String split = ":";
    /**
     * 默认端口
     */
    private final static int port = 6379;
    private final static String reg_number = "\\D";
    /**
     * 存储节点信息
     */
    private Map<Integer, HostAndPort> nodes;
    public static ExecutorService executor = Executors.newFixedThreadPool(2);

    public IncrRedisClient() {
        this(new HostAndPort(local, port));
    }

    public IncrRedisClient(HostAndPort node) {
        this(new GenericObjectPoolConfig(), node);
    }

    public IncrRedisClient(GenericObjectPoolConfig config, HostAndPort node) {
        size = 1;
        this.config = config;
        this.maxnumber = 1;
        this.config = config;
        this.nodes_l = new ArrayList<>();
        this.nodes_l.add(node);
        JedisPool pool = new JedisPool(config, node.getHost(), node.getPort());
        numberPool = new HashMap<>(1);
        numberPool.put(1, pool);
        nodes = new HashMap<>(1);
        nodes.put(1, node);
        ipPool = new HashMap<>(1);
        ipPool.put(node.toString(), pool);
    }

    public IncrRedisClient(List<HostAndPort> nodes, String clusterName) {
        this(new GenericObjectPoolConfig(), nodes, clusterName);
    }

    public IncrRedisClient(GenericObjectPoolConfig config, List<HostAndPort> nodes, String clusterName) {
        size = nodes.size();
        this.config = config;
        this.clusterName = clusterName;
        this.nodes = new HashMap<>();
        this.nodes_l = nodes;
        ipPool = new HashMap<>(size);
        numberPool = new HashMap<>(size);
        List<NodeInfo> nodeInfos = getAndCreateNodeInfo(nodes, clusterName);
        for (int i = 0; i < size; i++) {
            NodeInfo info = nodeInfos.get(i);
            JedisPool jedisPool = new JedisPool(config, info.getHp().getHost(), info.getHp().getPort());
            ipPool.put(info.getHp().toString(), jedisPool);
            numberPool.put(info.getId(), jedisPool);
            this.nodes.put(info.getId(), info.getHp());
        }
    }

    public IncrRedisClient(List<HostAndPort> nodes, String clusterName, int maxkeynumber) {
        this(new GenericObjectPoolConfig(), nodes, clusterName, maxkeynumber);
    }

    public IncrRedisClient(GenericObjectPoolConfig config, List<HostAndPort> nodes, String clusterName, int maxkeynumber) {
        size = nodes.size();
        this.maxnumber = maxkeynumber;
        this.config = config;
        this.nodes_l = nodes;
        this.clusterName = clusterName;
        ipPool = new HashMap<>(size);
        numberPool = new HashMap<>(size);
        this.nodes = new HashMap<>();
        List<NodeInfo> nodeInfos = getAndCreateNodeInfo(nodes, clusterName);
        for (int i = 0; i < size; i++) {
            NodeInfo info = nodeInfos.get(i);
            JedisPool jedisPool = new JedisPool(config, info.getHp().getHost(), info.getHp().getPort());
            ipPool.put(info.getHp().toString(), jedisPool);
            numberPool.put(info.getId(), jedisPool);
            this.nodes.put(info.getId(), info.getHp());
        }
    }

    public void reconnection() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ipPool = new HashMap<>(size);
                    numberPool = new HashMap<>(size);
                    nodes = new HashMap<>();
                    List<NodeInfo> nodeInfos = getAndCreateNodeInfo(nodes_l, clusterName);
                    for (int i = 0; i < size; i++) {
                        NodeInfo info = nodeInfos.get(i);
                        JedisPool jedisPool = new JedisPool(config, info.getHp().getHost(), info.getHp().getPort());
                        ipPool.put(info.getHp().toString(), jedisPool);
                        numberPool.put(info.getId(), jedisPool);
                        nodes.put(info.getId(), info.getHp());
                    }
                } catch (Exception e) {
                    try {
                        logger.error("0.5 sec re try connext redis......... " + e.getMessage());
                        Thread.sleep(500);
                        reconnection();
                    } catch (InterruptedException e1) {
                        logger.error("0.5 sec re try connext redis", e1);
                    }
                }
            }
        });
    }

    /**
     * 根据集群名称获取或者创建集群信息
     *
     * @param nodes       the redis node ip and port
     * @param clusterName cluster name
     * @return the cluster node infos
     */
    private List<NodeInfo> getAndCreateNodeInfo(List<HostAndPort> nodes, String clusterName) {
        List<NodeInfo> nodeinfos = new ArrayList<>();
        //获取不在集群里面的节点
        Map<Integer, NodeInfo> nodeids = new HashMap<>();
        for (int i = 0; i < size; i++) {
            HostAndPort info = nodes.get(i);
            Jedis jedis = new Jedis(info.getHost(), info.getPort());
            NodeInfo node = nodeInfo(jedis, clusterName);
            if (node == null || clusterName.equals(node.getCluster())) {
                node = new NodeInfo();
                nodeinfos.add(node);
            } else {
                nodeids.put(node.getId(), node);
            }
            node.setHp(info);
            jedis.close();
        }
        if (nodeids.size() != size) {
            for (int i = 0; i < size; i++) {
                if (!nodeids.containsKey(i)) {
                    NodeInfo node = nodeinfos.get(0);
                    node.setId(i);                          //设置集群的ID
                    node.setStart(maxnumber * i);             //设置节点key开始 从0开始
                    node.setEnd(maxnumber * (i + 1) - 1);         //设置节点key结束
                    node.setCluster(clusterName);           //设置集群名称
                    Map<String, String> hash = createnodeInfoMap(node);
                    Jedis jedis = new Jedis(node.getHp().getHost(), node.getHp().getPort());
                    jedis.hmset(NodeInfo.NODE_INFO + "." + node.getCluster(), hash);
                    jedis.close();
                    nodeinfos.remove(node);
                    nodeids.put(i, node);
                }
            }
        }
        return new ArrayList<>(nodeids.values());
    }

    private Map<String, String> createnodeInfoMap(NodeInfo node) {
        Map<String, String> hash = new HashMap<>();
        hash.put(NodeInfo.KEY_END, node.getEnd().toString());
        hash.put(NodeInfo.KEY_START, node.getStart().toString());
        hash.put(NodeInfo.CLUSTER, node.getCluster());
        hash.put(NodeInfo.NODE_ID, node.getId().toString());
        return hash;
    }

    public long safeSolt(String key) {
        return JedisClusterCRC16.getCRC16(key);
    }

    public Jedis resource(String key) {
        JedisPool pool = resourcePool(key);
        if (pool == null) return new Jedis();
        return pool.getResource();
    }


    public JedisPool resourcePool(String key) {
        int number = (int) (safeSolt(key) / maxnumber);
        if (number < size && number >= 0) {
            return numberPool.get(number);
        } else {
            return numberPool.get(Math.abs(number) % size);
        }
    }

    public HostAndPort hostAndPort(String key) {
        int number = (int) (safeSolt(key) / maxnumber);
        if (number < size) {
            return nodes.get(number);
        } else {
            return nodes.get(number % size);
        }
    }

    public void returnObjectResource(Jedis jedis) {
        String key = hps(jedis);
        if (key != null) {
            ipPool.get(key).returnResourceObject(jedis);
        }
    }

    public String hps(Jedis jedis) {
        if (jedis != null) {
            return jedis.getClient().getHost() + split + jedis.getClient().getPort();
        }
        return null;
    }

    public static NodeInfo nodeInfo(Jedis jedis, String cluster) {
        if (jedis == null) return null;
        Map<String, String> map = jedis.hgetAll(NodeInfo.NODE_INFO + "." + cluster);
        if (map == null || map.isEmpty()) return null;
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setCluster(map.get(NodeInfo.CLUSTER));
        String start = map.get(NodeInfo.KEY_START);
        nodeInfo.setStart(start == null ? null : Long.valueOf(start));
        String end = map.get(NodeInfo.KEY_END);
        nodeInfo.setStart(end == null ? null : Long.valueOf(end));
        nodeInfo.setStart(start == null ? null : Long.valueOf(start));
        String nodeid = map.get(NodeInfo.NODE_ID);
        nodeInfo.setId(nodeid == null ? null : Integer.valueOf(nodeid));
        return nodeInfo;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getMaxnumber() {
        return maxnumber;
    }

    public void setMaxnumber(long maxnumber) {
        this.maxnumber = maxnumber;
    }

    public Map<Integer, HostAndPort> getNodes() {
        return nodes;
    }


    /*===Redis  操作接口实现===========================================================*/
    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
            return jedis.expireAt(key, milliseconds);
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long pexpireAt(String key, long millisecondsTimestamp) {
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        return null;
    }

    public Long zadd(String key, double v, String s1) {
        return null;
    }

    public Long zadd(String key, Map<String, Double> map) {
        return null;
    }

    public Set<String> zrange(String key, long l, long l1) {
        return null;
    }

    public Long zrem(String key, String... strings) {
        return null;
    }

    public Double zincrby(String key, double v, String s1) {
        return null;
    }

    public Long zrank(String key, String s1) {
        return null;
    }

    public Long zrevrank(String key, String s1) {
        return null;
    }

    public Set<String> zrevrange(String key, long l, long l1) {
        return null;
    }

    public Set<Tuple> zrangeWithScores(String key, long l, long l1) {
        return null;
    }

    public Set<Tuple> zrevrangeWithScores(String key, long l, long l1) {
        return null;
    }

    public Long zcard(String key) {
        return null;
    }

    public Double zscore(String key, String s1) {
        return null;
    }

    public List<String> sort(String key) {
        return null;
    }

    public List<String> sort(String key, SortingParams sortingParams) {
        return null;
    }

    public Long zcount(String key, double v, double v1) {
        return null;
    }

    public Long zcount(String key, String s1, String s2) {
        return null;
    }

    public Set<String> zrangeByScore(String key, double v, double v1) {
        return null;
    }

    public Set<String> zrangeByScore(String key, String s1, String s2) {
        return null;
    }

    public Set<String> zrevrangeByScore(String key, double v, double v1) {
        return null;
    }

    public Set<String> zrangeByScore(String key, double v, double v1, int i, int i1) {
        return null;
    }

    public Set<String> zrevrangeByScore(String key, String s1, String s2) {
        return null;
    }

    public Set<String> zrangeByScore(String key, String s1, String s2, int i, int i1) {
        return null;
    }

    public Set<String> zrevrangeByScore(String key, double v, double v1, int i, int i1) {
        return null;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double v, double v1) {
        return null;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double v, double v1) {
        return null;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double v, double v1, int i, int i1) {
        return null;
    }

    public Set<String> zrevrangeByScore(String key, String s1, String s2, int i, int i1) {
        return null;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, String s1, String s2) {
        return null;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, String s1, String s2) {
        return null;
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, String s1, String s2, int i, int i1) {
        return null;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double v, double v1, int i, int i1) {
        return null;
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, String s1, String s2, int i, int i1) {
        return null;
    }

    public Long zremrangeByRank(String key, long l, long l1) {
        return null;
    }

    public Long zremrangeByScore(String key, double v, double v1) {
        return null;
    }

    public Long zremrangeByScore(String key, String s1, String s2) {
        return null;
    }

    public Long zlexcount(String key, String s1, String s2) {
        return null;
    }

    public Set<String> zrangeByLex(String key, String s1, String s2) {
        return null;
    }

    public Set<String> zrangeByLex(String key, String s1, String s2, int i, int i1) {
        return null;
    }

    public Set<String> zrevrangeByLex(String key, String s1, String s2) {
        return null;
    }

    public Set<String> zrevrangeByLex(String key, String s1, String s2, int i, int i1) {
        return null;
    }

    public Long zremrangeByLex(String key, String s1, String s2) {
        return null;
    }

    public Long linsert(String key, BinaryClient.LIST_POSITION list_position, String s1, String s2) {
        return null;
    }

    public Long lpushx(String key, String... strings) {
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        return null;
    }

    public List<String> blpop(int i, String s) {
        return null;
    }

    @Deprecated
    public List<String> brpop(String key) {
        return null;
    }

    public List<String> brpop(int i, String s) {
        return null;
    }

    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
            return jedis.echo(key);
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long move(String key, int dbIndex) {
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
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
        Jedis jedis = null;
        try {
            jedis = resource(key);
            return jedis.pfcount(key);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return 0;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Long publish(String key, String message) {
        Jedis jedis = null;
        try {
            jedis = resource(key);
            return jedis.publish(key, message);
        } catch (JedisConnectionException e) {
            logger.error("have JedisConnectionException message : " + e.getMessage());
            reconnection();
            return null;
        } finally {
            returnObjectResource(jedis);
        }
    }

    public Set<String> keys(String key) {
        Set<String> set = new HashSet<>();
        for (JedisPool pool : ipPool.values()) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                set.addAll(keys(jedis, key));
            } catch (JedisConnectionException e) {
                logger.error("have JedisConnectionException message : " + e.getMessage());
                reconnection();
                return null;
            } finally {
                pool.returnResourceObject(jedis);
            }
        }
        return set;
    }

    private Set<String> keys(Jedis jedis, String key) {
        return jedis.keys(key);
    }
}