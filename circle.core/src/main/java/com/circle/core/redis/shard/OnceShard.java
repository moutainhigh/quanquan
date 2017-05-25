package com.circle.core.redis.shard;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cxx on 15-9-15.
 */
public class OnceShard {

    public ShardedJedis shardjedis(List<HostAndPort> hps) {
        List<JedisShardInfo> infos = new ArrayList<>();
        for (HostAndPort hp : hps) {
            infos.add(new JedisShardInfo(hp.getHost(), hp.getPort()));
        }
        return new ShardedJedis(infos);
    }
}
