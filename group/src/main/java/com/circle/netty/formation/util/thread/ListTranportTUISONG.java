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
 * @author Created by cxx15 on 2015/11/6.
 */
public class ListTranportTUISONG implements Runnable {
    private Logger logger = LoggerFactory.getLogger(ListTranportTUISONG.class);
    List<String> cids;
    String json;
    int times;

    public ListTranportTUISONG(Set<String> cids, String json, int times) {
        this.cids = new ArrayList<>(cids);
        this.json = json;
        this.times = times;
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
            logger.debug("[ListTranportTUISONG] Start = " + start + "\tEnd = " + end + "\tSize = " + size + "\tCount = " + count);
            sendtogetui(cids.subList(start, end));
        }
    }

    private void sendtogetui(List<String> cids) {
        try {
            if (cids == null || cids.isEmpty()) {
                logger.warn("[ListTranportTUISONG] Cids isEmpty cids=" + (cids==null?"null":cids.size()) + ", json=" + json);
                return;
            }
            List<Target> targets = MessageService.create(cids);
            ListMessage message = new ListMessage();
            message.setData(MessageService.testSendIOS(json));
            //设置消息离线，并设置离线时间
            message.setOffline(true);
            //离线有效时间，单位为毫秒，可选
            message.setOfflineExpireTime(times);
            String taskId = MessageService.push.getContentId(message);
            //使用taskID对目标行推送
            //打印服器返回信息
            IPushResult ret;
            try {
                ret = MessageService.push.pushMessageToList(taskId, targets);
            } catch (RequestException e) {
                ret = MessageService.push.pushMessageToList(taskId, targets);
                logger.error("[ListTranportTUISONG]RequestException cids=" + cids.size() + ", json=" + json);
            }
            if (ret != null) {
                logger.info("[ListTranportTUISONG] " + ret.getResponse().toString() + ",cids=" + cids.size() + ", json=" + json);
            } else {
                logger.error("[ListTranportTUISONG]服务器响应异常 cids=" + cids.size());
            }
        } catch (Exception e) {
            logger.error("[ListTranportTUISONG]ErrorCode=" + e.getMessage(), e);
        }
    }
}
