package com.circle.netty.formation.util.thread;

import com.circle.netty.formation.message.service.MessageService;
import com.circle.netty.formation.util.AppConfig;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cxx on 15-11-3.
 */
public class TransportationToApp implements Runnable {
    private Logger logger = LoggerFactory.getLogger(TransportationToApp.class);
    private String tag;
    private String phone;
    private String province;
    private String json;

    public TransportationToApp(String tag, String phone, String province, String json) {
        this.tag = tag;
        this.phone = phone;
        this.province = province;
        this.json = json;
    }

    @Override
    public void run() {
        try{
            TransmissionTemplate template = MessageService.testSendIOS(json);
            AppMessage message = new AppMessage();
            message.setData(template);
            //设置消息离线，并设置离线时间
            message.setOffline(true);
            //离线有效时间，单位为毫秒，可选 10 分钟
            message.setOfflineExpireTime(600000);
            //设置推送目标条件过滤
            List<String> appIdList = new ArrayList<>();
            List<String> phoneTypeList = new ArrayList<>();
            List<String> provinceList = new ArrayList<>();
            List<String> tagList = new ArrayList<>();
            appIdList.add(AppConfig.igexin_appid);
            message.setAppIdList(appIdList);
            //设置机型
            if (phone != null) {
                phoneTypeList.add(phone);
                message.setPhoneTypeList(phoneTypeList);
            }
            //phoneTypeList.add("ANDROID");
            //设置省份
            if (province != null) {
                provinceList.add(province);
                message.setProvinceList(provinceList);
            }
            //设置标签内容
            if (tag != null) {
                tagList.add(tag);
                message.setTagList(tagList);
            }
            IPushResult ret = MessageService.push.pushMessageToApp(message);
            logger.info(ret.getResponse().toString());
        }catch (Exception e){
            logger.info("ErrorMessage="+e.getMessage(),e);
        }
    }
}
