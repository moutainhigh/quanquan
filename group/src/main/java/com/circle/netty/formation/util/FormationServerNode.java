package com.circle.netty.formation.util;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormationServerNode implements Watcher {
    private static LogUtils logger = LogUtils.getInstance();
    @Override
    public void process(WatchedEvent event) {
        logger.info("TYPE-"+event.getType().name() + " PATH=" + event.getPath());
    }
}