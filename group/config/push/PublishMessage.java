package push;

import com.circle.core.util.Config;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.APNTemplate;
import javapns.back.PushNotificationManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.Device;
import javapns.data.PayLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

public class PublishMessage {
    public static final String IOS_NO_SOUND = "nosound.wav";
    public static String IOS_PATH = "/home/cxx/java/FormationServer/config/ios/key.p12";
    public static String IOS_PASSWD = "quanquan";
    public static int IOS_PORT = 2195;
    public static String IOS_HOST = "gateway.sandbox.push.apple.com";
    public static String IOS_DEVICE_TOKEN = "e44e71bf3313a0c1e612278cc5f933072b11c16056b8768decd7020dd69e6c10";
    public static String IOS_DEF_SOUND = "default";
//    public static String IOS_DEF_SOUND = "test2.wav";
    //	public static String IOS_DEVICE_TOKEN="91dfca65679605c45ca6fee96ebeed7c33cf8216ffdeb23394b9128701b2eee3";
    public static String SYS_IOS = "ios";
    public static String SYS_ANDROID = "android";
    public static String ANDROID_MASTER_SCRECT = "ANDROID_MASTER_SCRECT";
    public static String ANDROID_APPKEY = "ANDROID_APPKEY";
    public static PushNotificationManager iOS_pushManager;
    private static Logger logger = LoggerFactory.getLogger(PublishMessage.class);
    public static void main(String[] args) throws Exception {
        Config config = new Config("config/app.properties");
        PublishMessage.initial(config);
        PublishMessage.iosSend(IOS_DEVICE_TOKEN, IOS_DEVICE_TOKEN, IOS_NO_SOUND, 10, new HashMap<String, String>(1), "test test 111");
    }



    public static void apnpush() throws Exception {
        IGtPush push = new IGtPush(url, appKey, masterSecret);
        APNTemplate t = new APNTemplate();
        APNPayload apnpayload = new APNPayload();
        apnpayload.setSound("");
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        alertMsg.setTitle("aaaaaa");
        alertMsg.setBody("bbbb");
        alertMsg.setTitleLocKey("ccccc");
        alertMsg.setActionLocKey("ddddd");
        apnpayload.setAlertMsg(alertMsg);

        t.setAPNinfo(apnpayload);
        SingleMessage sm = new SingleMessage();
        sm.setData(t);
        IPushResult ret0 = push.pushAPNMessageToSingle(appId, dt, sm);
        System.out.println(ret0.getResponse());

    }




    private static PushNotificationManager createConnection() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException, NoSuchProviderException, IOException {
        iOS_pushManager = PushNotificationManager.getInstance();
        iOS_pushManager.initializeConnection(IOS_HOST, IOS_PORT, IOS_PATH, IOS_PASSWD, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);
        return iOS_pushManager;
    }


    public static void initial(Config cfs) throws Exception {
        IOS_PATH = cfs.getAsString("IOS_PATH");
        IOS_PASSWD = cfs.getAsString("IOS_PASSWD");
        IOS_PORT = cfs.getAsInteger("IOS_PORT");
        IOS_HOST = cfs.getAsString("IOS_HOST");
        ANDROID_APPKEY = cfs.getAsString("ANDROID_APPKEY");
        ANDROID_MASTER_SCRECT = cfs.getAsString("ANDROID_MASTER_SCRECT");
        createConnection();
    }

    /**
     * @param device   还不知道是啥呢
     * @param token    用户的ID
     * @param sound    声音 使用默认
     * @param bage     显示标记,数量
     * @param keyValue kv 数据
     * @param alert    弹出内容
     */
    public static void iosSend(String device, String token, String sound, int bage, Map<String, String> keyValue, String alert) throws Exception {
        iosSend_(device, token, sound, bage, keyValue, alert);
    }

    public static void iosSend_(String device, String token, String sound, int bage, Map<String, String> keyValue, String alert) {
        try {
            PayLoad payLoad = new PayLoad();
            if (alert != null) {
                payLoad.addAlert(alert);
            }
            for (String key : keyValue.keySet()) {
                payLoad.addCustomDictionary(key, keyValue.get(key));
            }
            payLoad.addBadge(bage);// 消息推送标记数，小红圈中显示的数字。
            payLoad.addSound(sound);
            iOS_pushManager.addDevice(device, token);
            Device client = iOS_pushManager.getDevice(device);
            iOS_pushManager.sendNotification(client, payLoad);
            iOS_pushManager.removeDevice(device);
        } catch (Exception e) {
            logger.error("device = [" + device + "], token = [" + token + "], sound = [" + sound + "], bage = [" + bage + "], keyValue = [" + keyValue + "], alert = [" + alert + "]" + "ios tuisong error , errorMessage=" + e.getMessage());
        }
    }

//    public static void androidSingleSend(String device_tokens, Object custom) throws Exception {
//        androidSingleSend(Integer.toString((int) (System.currentTimeMillis() / 1000)), device_tokens, custom);
//    }
//
//    public static void androidSingleSend(String timestamp, String device_tokens, Object custom) throws Exception {
//        AndroidUnicast unicast = new AndroidUnicast();
//        unicast.setAppMasterSecret(ANDROID_MASTER_SCRECT);
//        unicast.setPredefinedKeyValue("appkey", ANDROID_APPKEY);
//        unicast.setPredefinedKeyValue("timestamp", timestamp);
//        // Set your device token
//        unicast.setPredefinedKeyValue("device_tokens", device_tokens);
//        unicast.setPredefinedKeyValue("after_open", "go_app");
//        unicast.setPredefinedKeyValue("alias_type", "alias1");
//        unicast.setPredefinedKeyValue("alias", "alias1");
//        unicast.setPredefinedKeyValue("custom", custom);
//        unicast.setPredefinedKeyValue("display_type", "message");
//        // For how to register a test device, please see the developer doc.
//        unicast.setPredefinedKeyValue("production_mode", "true");
//        // Set customized fields
//        unicast.setExtraField("test", "helloworld");
//        unicast.send();
//    }
}
