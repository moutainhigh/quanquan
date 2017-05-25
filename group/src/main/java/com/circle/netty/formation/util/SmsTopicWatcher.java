package com.circle.netty.formation.util;

import com.circle.netty.formation.GROUP;
import com.circle.netty.formation.ation.service.CacheManager;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Created by cxx on 15-8-20.
 */
public class SmsTopicWatcher implements Watcher {
    private static Logger logger = LoggerFactory.getLogger(SmsTopicWatcher.class);
    @Override
    public void process(WatchedEvent event) {
        if (event.getPath() != null && event.getPath().startsWith(AppConfig.smstopics)) try {
            List<String> list = GROUP.zooKeeper.getChildren(AppConfig.smstopics, this);
//            存入缓存中
            CacheManager.string_list.put(AppConfig.smstopics, list);
            logger.info("Add message server host:port = " + list);
        } catch (KeeperException | InterruptedException e) {
            logger.error("WatchedEvent", e);
        } else try {
            GROUP.zooKeeper.getChildren(AppConfig.smstopics, this);
        } catch (KeeperException | InterruptedException e) {
            logger.error("WatchedEvent", e);
        }
    }
}
