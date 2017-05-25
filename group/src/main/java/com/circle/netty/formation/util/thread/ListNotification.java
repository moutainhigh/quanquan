package com.circle.netty.formation.util.thread;

import com.circle.netty.formation.message.service.MessageService;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Created by cxx on 15-11-3.
 */
public class ListNotification implements Runnable {
    private Logger logger = LoggerFactory.getLogger(ListNotification.class);
    private List<String> cids;
    private String title;
    private String context;
    private String msg;
    private int time;

    public ListNotification(Set<String> cids, String title, String context, String msg, int time) {
        this.cids = new ArrayList<>(cids);
        this.title = title;
        this.context = context;
        this.msg = msg;
        this.time = time;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        if (cids == null || cids.isEmpty()) return;
        int size = cids.size();
        int count = 800;
        int db = size / count + (size % count == 0 ? 0 : 1);
        for (int i = 0; i < db; i++) {
            int start = i * count;
            int end = (i + 1) * count;
            if (count > (size - start)) {
                end = size;
            }
            logger.debug("[ListNotification]Start = " + start + "\tEnd = " + end + "\tSize = " + size + "\tCount = " + count);
            sendtogetui(cids.subList(start, end));
        }
    }

    private void sendtogetui(List<String> cids) {
        try {
            if (cids == null || cids.isEmpty()) {
                logger.warn("[ListNotification] Cids isEmpty cids=" + (cids==null?"null":cids.size()) + ", title=" + title + " , context=" + context + ", msg=" + msg);
                return;
            }
            List<Target> targets = MessageService.create(cids);
            ListMessage message = new ListMessage();
            message.setData(MessageService.notificationTemplate(title, context, msg));
            //置消息离线，并且置离线时间
            message.setOffline(true);
            //离线有效时间，单位为毫秒，
            message.setOfflineExpireTime(time);
            String taskId = MessageService.push.getContentId(message);
            //使用taskID对目标进行推送
            IPushResult ret;
            try {
                ret = MessageService.push.pushMessageToList(taskId, targets);
            } catch (RequestException e) {
                ret = MessageService.push.pushMessageToList(taskId, targets);
                logger.error("RequestException cids=" + cids.size() + ", title=" + title + " , context=" + context + ", msg=" + msg);
            }
            if (ret != null) {
                logger.info("[ListNotification]" + ret.getResponse().toString() + ",cids=" + cids.size() + ", title=" + title + " , context=" + context + ", msg=" + msg);
            } else {
                logger.warn("服务器响应异常 cids=" + cids.size());
            }
        } catch (Exception e) {
            logger.error("ErrorCode=" + e.getMessage(), e);
        }
    }
}
