package com.circle.netty.formation.util.thread;

import com.circle.netty.formation.message.service.MessageService;
import com.circle.netty.formation.util.AppConfig;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Created by cxx on 15-11-3.
 */
public class SingleTranport implements Runnable{
    private Logger logger  = LoggerFactory.getLogger(SingleTranport.class);
    private String cid;
    private String json;
    public SingleTranport(String cid, String json) {
        this.cid = cid;
        this.json = json;
    }

    @Override
    public void run() {
        try{
            if (StringUtils.isEmpty(cid)) {
                logger.warn("singleTranport Cid isEmpty cid=" + cid + ", msg=" + json);
                return;
            }
            SingleMessage message = new SingleMessage();
            message.setOffline(true);
            //离线有效时间，单位为毫秒，可选 10 分钟过期
            message.setOfflineExpireTime(600000);
            message.setData(MessageService.testSendIOS(json));
            message.setPushNetWorkType(0); //可选。判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
            Target target = new Target();
            target.setAppId(AppConfig.igexin_appid);
            target.setClientId(cid);
            //用户别名推送，cid和用户别名只能2者选其一
            //String alias = "个";
            //target.setAlias(alias);
            IPushResult ret;
            try {
                ret = MessageService.push.pushMessageToSingle(message, target);
            } catch (RequestException e) {
                ret = MessageService.push.pushMessageToSingle(message, target, e.getRequestId());
                logger.error("RequestException cid=" + cid + ", msg=" + json, e);
            }
            if (ret != null) {
                logger.info(ret.getResponse().toString() + ", cid=" + cid + ", msg=" + json);
            } else {
                logger.error("服务器响应异常 singleTranport Cids isEmpty cid=" + cid + ", msg=" + json);
            }
        }catch (Exception e){
            logger.error("服务器响应异常 singleTranport Cids isEmpty cid=" + cid + ", msg=" + json);
        }
    }
}
