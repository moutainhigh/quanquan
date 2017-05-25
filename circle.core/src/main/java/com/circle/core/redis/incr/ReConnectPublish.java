package com.circle.core.redis.incr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.net.NoRouteToHostException;

/**
 * 1. 定时 监控 redis 连接情况
 *
 * @author Created by cxx15 on 2015/12/4.
 */
public class ReConnectPublish extends JedisPubSub implements Runnable {
    protected Jedis jedis;
    protected JedisPubSub jedisPubSub;
    protected String channels;
    protected boolean close = true;
    public Thread listener;
    public boolean connect = true;
    public Thread thread;
    public HostAndPort hp;
    public int db = 0;
    public long delay_connect = 1000;
    public long delay_check = 2000;
    public long delay_wait = 2000;
    private Logger logger = LoggerFactory.getLogger(ReConnectPublish.class);
    private boolean sub = true;

    public void subscribe(final HostAndPort hp, final String channels) {
        //启动监听线程
        this.hp = hp;
        jedis = new Jedis(hp.getHost(), hp.getPort());
        this.channels = channels;
        this.jedisPubSub = this;
        thread = new Thread(this);
        listener = new Thread() {
            private Logger logger = LoggerFactory.getLogger(Thread.class);

            @Override
            public void run() {
                JedisPool pool = new JedisPool(hp.getHost(), hp.getPort());
                while (true) {
                    if (!close) {
                        logger.info("循环居然结速了----");
                        return;
                    }
                    connect = true;
                    try {
                        Thread.sleep(delay_check);
                        Jedis jedis = pool.getResource();
                        jedis.publish(channels, channels);
                        pool.returnResourceObject(jedis);
                        synchronized (listener) {
                            listener.wait(delay_wait);
                        }
                    } catch (InterruptedException e) {
                        logger.info("收到检测消息 -> 打断成功");
                        connect = false;
                    } catch (Exception e1) {
                        logger.error("检测到其他异常" + e1.getMessage());
                    }
                    //重连 --- 等待 10秒
                    if (connect && close) {
                        //重连接
                        reconnect();
                    }
                    if (!close) {
                        logger.info("循环居然结速了----");
                        return;
                    }
                }
            }
        };
        thread.setDaemon(false);
        thread.start();
    }

    public void psubscribe(final HostAndPort hp, final String channels) {
        this.sub = false;
        subscribe(hp, channels);
    }

    public void reconnect() {
        logger.info("重新建立连接 -------- ----- -- - -- -- - --- -- ---- ---- -----");
        try {
            thread.interrupt();
        } catch (Exception e) {
            logger.error("Exception" + e.getMessage(), e);
        }
    }

    public void setDb(int db) {
        this.db = db;
    }

    public void setDelay_connect(long delay_connect) {
        this.delay_connect = delay_connect;
    }

    public void setDelay_check(long delay_check) {
        this.delay_check = delay_check;
    }

    public void setDelay_wait(long delay_wait) {
        this.delay_wait = delay_wait;
    }

    public void interruptListener() {
        try {
            logger.debug("收到检测消息 -> " + channels + " - > 无需重连接");
            connect = false;
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage(), e);
        }
    }

    public void close() {
        close = false;
        unsubscribe();
        reconnect();
        listener.interrupt();
    }

    @Override
    public void run() {
        logger.debug("启动监听 -> host-port" + hp + "\t channels=" + channels + "\tDB=" + db);
        //启动监听线程 - 自我检测 - 多久没有收到检测消息,
        listener.setDaemon(true);
        listener.start();
        //启动哪个啥
        while (close) {
            try {
                String pong = jedis.ping();
                if (pong != null && pong.equals("PONG")) {
                    //定位库
                    jedis.select(db);
                    if (sub) {
                        jedis.subscribe(jedisPubSub, channels);
                    } else {
                        jedis.psubscribe(jedisPubSub, channels);
                    }
                } else {
                    logger.info("PING BACK : " + pong);
                }
            } catch (Exception e) {
                logger.error("Exception : " + e.getMessage());
            }
            try {
                Thread.sleep(delay_connect);
            } catch (InterruptedException e1) {
                logger.error("Thread :" + e1.getMessage());
            }
        }
        logger.info("循环居然结速了----");
    }

    @Override
    public void onMessage(String channel, String message) {
        checkreconnect(channel, message);
    }

    protected boolean checkreconnect(String channel, String message) {
        logger.debug("channel = [" + channel + "], message = [" + message + "]");
        if (message.equals(channel)) {
            interruptListener();
            return true;
        }
        return false;
    }
}
