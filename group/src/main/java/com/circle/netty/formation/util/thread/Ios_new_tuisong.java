package com.circle.netty.formation.util.thread;

import com.circle.netty.formation.message.service.MessageService;
import com.circle.netty.formation.util.AppConfig;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.template.APNTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Created by cxx on 15-11-3.
 */
public class Ios_new_tuisong implements Runnable {
    private Logger logger = LoggerFactory.getLogger(Ios_new_tuisong.class);
    String device;
    String context;
    boolean hashsound;
    int badge;
    Map<String, Object> msg;

    public Ios_new_tuisong(String device, String context, boolean hashsound, int badge, Map<String, Object> msg) {
        this.device = device;
        this.context = context;
        this.hashsound = hashsound;
        this.badge = badge;
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            APNTemplate t = new APNTemplate();
            APNPayload apnpayload = new APNPayload();
            if (hashsound) {
                apnpayload.setSound("haveSound.wav");
            } else {
                apnpayload.setSound("nosound.wav");
            }
            apnpayload.setBadge(badge);
            APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
            alertMsg.setTitle("");
            alertMsg.setBody(context);
            alertMsg.setTitleLocKey("问啊");
            alertMsg.setActionLocKey("");
            apnpayload.setAlertMsg(alertMsg);
            for (String key : msg.keySet()) {
                apnpayload.addCustomMsg(key, msg.get(key));
            }
            t.setAPNInfo(apnpayload);
            SingleMessage sm = new SingleMessage();
            sm.setData(t);
            IPushResult ret0 = MessageService.push.pushAPNMessageToSingle(AppConfig.igexin_appid, device, sm);
            logger.info("BackMessage=" + ret0.getResponse());
        } catch (Exception e) {
            logger.error("ErrorMessage=" + e.getMessage(),e);

        }
    }
}
