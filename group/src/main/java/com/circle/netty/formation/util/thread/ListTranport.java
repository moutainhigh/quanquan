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
public class ListTranport implements Runnable {
    private Logger logger = LoggerFactory.getLogger(ListTranport.class);
    private List<String> cids;
    private String msg;

    public ListTranport(Set<String> cids, String msg) {
        this.cids = new ArrayList<>(cids);
        this.msg = msg;
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
            logger.debug("[Start] = " + start + "\tEnd = " + end + "\tSize = " + size + "\tCount = " + count);
            sendtogetui(cids.subList(start, end));
        }
    }

    private void sendtogetui(List<String> cids) {
        try {
            if (cids == null || cids.isEmpty()) {
                logger.warn("[listNotification] Cids isEmpty cids.size=" + (cids==null?"null":cids.size()) + ", msg=" + msg);
                return;
            }
            List<Target> targets = MessageService.create(cids);
            ListMessage message = new ListMessage();
            message.setData(MessageService.testSendIOS(msg));
            //设置消息离线，并设置离线时间
            message.setOffline(true);
            //离线有效时间，单位为毫秒，可选 10 分钟过期
            message.setOfflineExpireTime(60000 * 10);
            String taskId = MessageService.push.getContentId(message);
            //使用taskID对目标行推送
            //打印服器返回信息
            IPushResult ret;
            try {
                ret = MessageService.push.pushMessageToList(taskId, targets);
            } catch (RequestException e) {
                ret = MessageService.push.pushMessageToList(taskId, targets);
                logger.error("[listNotification] RequestException cids=" + cids.size() + ", msg=" + msg);
            }
            if (ret != null) {
                logger.info("[listNotification]" + ret.getResponse().toString() + ",cids=" + cids.size() + ", msg=" + msg);
            } else {
                logger.error("[listNotification]服务器响应异常 cids=" + cids.size());
            }
        } catch (Exception e) {
            logger.error("[listNotification] ErrorCode=" + e.getMessage(), e);
        }
    }
}
