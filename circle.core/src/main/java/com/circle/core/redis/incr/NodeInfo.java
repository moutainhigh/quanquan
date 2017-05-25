package com.circle.core.redis.incr;

import redis.clients.jedis.HostAndPort;

/**
 * @author Created by Fomky on 2015/6/7 0007.
 */
public class NodeInfo {

    public final static String NODE_INFO = "node.info";
    public final static String KEY_START = "node.info.start";
    public final static String CLUSTER = "node.info.cluster.name";
    public final static String KEY_END = "node.info.end";
    public final static String NODE_ID = "node.info.id";


    private Integer id;
    private Long start;
    private Long end;
    private String cluster;
    private HostAndPort hp;

    public HostAndPort getHp() {
        return hp;
    }

    public void setHp(HostAndPort hp) {
        this.hp = hp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

}