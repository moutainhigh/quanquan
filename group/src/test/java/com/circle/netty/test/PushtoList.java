package com.circle.netty.test;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.APNTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PushtoList {

    static String appId = "lKqaY0NdFJ6OP25ZysfEE4";
    static String appkey = "Dz01FOEtDb66nvRXsO9jr";
    static String appKey = "Dz01FOEtDb66nvRXsO9jr";
    static String master = "rwAcWZzJNk9igejxwmMoi4";
    static String masterSecret = "rwAcWZzJNk9igejxwmMoi4";
    static String CID1 = "31b5ccd16b8ef7ba43715aab7849ee06";
    static String host = "http://sdk.open.api.igexin.com/serviceex";
    static String devicetoken = "ac6b37477b3220bc46424d6adda8e8d9f7c74f10ebd48cae20985d4e0e774b4b";
    static String url ="http://sdk.open.api.igexin.com/serviceex";

    @Test
    public void apnpush() throws Exception {
        IGtPush push = new IGtPush(url, appKey, masterSecret);

        APNTemplate t = new APNTemplate();
        APNPayload apnpayload = new APNPayload();
        apnpayload.setSound("haveSound.wav");
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setTitle("aaaaaa");
        alertMsg.setBody("bbbb");
        alertMsg.setTitleLocKey("ccccc");
        alertMsg.setActionLocKey("ddddd");
        apnpayload.setAlertMsg(alertMsg);
        t.setAPNInfo(apnpayload);
        ListMessage message = new ListMessage();
        message.setData(t);
        String contentId = push.getAPNContentId(appId, message);
        System.out.println(contentId);
        List<String> dtl = new ArrayList<String>();
        dtl.add(devicetoken);
        System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
        IPushResult ret = push.pushAPNMessageToList(appId, contentId, dtl);
        System.out.println(ret.getResponse());
    }

    public  void test(String[] args) throws Exception {
        //配置返回每个用户返回用户状态，可选
        System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
        IGtPush push = new IGtPush(host, appkey, master);

        //通知透传模板
        NotificationTemplate template = notificationTemplateDemo();
        ListMessage message = new ListMessage();
        message.setData(template);

        //设置消息离线，并设置离线时间
        message.setOffline(true);
        //离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 1000 * 3600);

        //配置推送目标
        List targets = new ArrayList();
        Target target1 = new Target();
        Target target2 = new Target();
        target1.setAppId(appId);
        //用户别名推送，cid和用户别名2者只能选其一
        //String alias1 = "个";
        //target1.setAlias(alias1);
        target1.setClientId(CID1);
        target2.setAppId(appId);
        //用户别名推送，cid和用户别名2者只能选其一
        //String alias2 = "个推";
        //target2.setAlias(alias2);
        targets.add(target1);
        targets.add(target2);
        //获取taskID
        String taskId = push.getContentId(message);
        //使用taskID对目标进行推送
        IPushResult ret = push.pushMessageToList(taskId, targets);
        //打印服务器返回信息
        System.out.println(ret.getResponse().toString());
    }

    public static NotificationTemplate notificationTemplateDemo() {
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appkey);
        // 设置通知栏标题与内容
        template.setTitle("请输入通知栏标题");
        template.setText("请输入通知栏内容");
        // 配置通知栏图标
        template.setLogo("icon.png");
        // 配置通知栏网络图标
        template.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        template.setIsRing(true);
        template.setIsVibrate(true);
        template.setIsClearable(true);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        template.setTransmissionContent("请输入您要透传的内容");
        return template;
    }
}