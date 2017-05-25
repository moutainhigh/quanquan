package com.circle.netty.formation.util.thread;

import com.circle.netty.formation.message.service.MessageService;
import com.circle.netty.formation.util.AppConfig;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.template.APNTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author Created by cxx on 15-11-3.
 */
public class New_tuisong_app implements Runnable {
    private Logger logger = LoggerFactory.getLogger(New_tuisong_app.class);
    List<String> devicetoken;
    String context;
    Map<String, Object> msg;
    boolean sound;

    public New_tuisong_app(List<String> devicetoken, String context, Map<String, Object> msg, boolean sound) {
        this.devicetoken = devicetoken;
        this.context = context;
        this.msg = msg;
        this.sound = sound;
    }

    @Override
    public void run() {
        try{
            APNTemplate t = new APNTemplate();
            APNPayload apnpayload = new APNPayload();
            if (sound) {
                apnpayload.setSound("haveSound.wav");
            } else {
                apnpayload.setSound("nosound.wav");
            }
            APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
            alertMsg.setTitle("");
            alertMsg.setBody(context);
            alertMsg.setTitleLocKey("问啊");
            alertMsg.setActionLocKey("");
            for (String key : msg.keySet()) {
                apnpayload.addCustomMsg(key, msg.get(key));
            }
            apnpayload.setAlertMsg(alertMsg);
            t.setAPNInfo(apnpayload);
            ListMessage message = new ListMessage();
            message.setData(t);
            String contentId = MessageService.push.getAPNContentId(AppConfig.igexin_appid, message);
            System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
            IPushResult ret = MessageService.push.pushAPNMessageToList(AppConfig.igexin_appid, contentId, devicetoken);
            logger.info("result=" + ret.getResponse());
        }catch (Exception e){
            logger.error("ErrorMessage=" + e.getMessage(),e);
        }
    }
}
