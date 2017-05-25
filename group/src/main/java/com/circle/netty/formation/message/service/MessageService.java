package com.circle.netty.formation.message.service;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.Redis;
import com.circle.core.util.CircleMD5;
import com.circle.core.util.Verification;
import com.circle.imhxin.service.MsgService;
import com.circle.netty.formation.GROUP;
import com.circle.netty.formation.ation.service.CacheManager;
import com.circle.netty.formation.message.model.*;
import com.circle.netty.formation.message.model.struct.AppraiseStruct;
import com.circle.netty.formation.message.model.struct.QuestionStruct;
import com.circle.netty.formation.message.model.struct.UserJson;
import com.circle.netty.formation.message.model.struct.UserStruct;
import com.circle.netty.formation.util.AppConfig;
import com.circle.netty.formation.util.HBaseUtils;
import com.circle.netty.formation.util.RedisTable;
import com.circle.netty.formation.util.TimeUtil;
import com.circle.netty.formation.util.thread.*;
import com.circle.netty.http.JsonParams;
import com.fasterxml.jackson.databind.JsonNode;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import kafka.producer.KeyedMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.YouAreDeadException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.coprocessor.DoubleColumnInterpreter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.Time;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.phoenix.schema.types.PFloat;
import org.apache.phoenix.schema.types.PInteger;
import org.apache.phoenix.schema.types.PLong;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.collect.HppcMaps;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Tuple;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * @author Created by cxx on 15-8-10.
 */
@Service("messageService")
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    public static final IGtPush push = new IGtPush(AppConfig.igexin_host, AppConfig.igexin_appkey, AppConfig.igexin_master);
    private static final String SYS_IOS = "ios";
    private static final String SYS_ANDROID = "android";
    /***
     * 这两个变量用来计算时间权重，时间戳是long数字，（MaxLong-时间戳）/part 作为时间分数的权重的基数 再乘以权重作为分数
     * 越早完成，分数越高，所以，当时间戳越大 基数就越小，除以part是因为long型时间戳过大，超过其他参数的权重
     */
    public static long MaxLong = 1000000000000000L;
    public static long    part = 1000000000000L;

    public static List<Target> create(List<String> cids) {
        List<Target> targets = new ArrayList<>();
        for (String cid : cids) {
            Target target = new Target();
            target.setAppId(AppConfig.igexin_appid);
            target.setClientId(cid);
            targets.add(target);
        }
        return targets;
    }

    public static TransmissionTemplate testSendIOS(String json) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(AppConfig.igexin_appid);
        template.setAppkey(AppConfig.igexin_appkey);
        template.setTransmissionType(2);
        template.setTransmissionContent(json);
//        APNPayload payload = getApnPayload();
        //payload.setCategory("$由客户端定义");
        //payload.setAlertMsg(new APNPayload.SimpleAlertMsg("hello"));
        //字典模式使用下者
        //payload.setAlertMsg(getDictionaryAlertMsg());
//        template.setAPNInfo(payload);
        return template;
    }

    private APNPayload getApnPayload() {
        APNPayload payload = new APNPayload();
        payload.setContentAvailable(1);
        payload.setBadge(1);
        payload.setSound("default");
        return payload;
    }

    /**
     * 给单个人发送透传消息
     */
    public void singleTranport(String cid, String json) {
        if (StringUtils.isEmpty(cid)) {
            logger.warn("singleTranport Cid isEmpty cid=" + cid + ", msg=" + json);
            return;
        }
        SingleTranport singleTranport = new SingleTranport(cid, json);
        Executor executor = GROUP.executor;
        executor.execute(singleTranport);
//        SingleMessage message = new SingleMessage();
//        message.setOffline(true);
//        //离线有效时间，单位为毫秒，可选 1 小时
//        message.setOfflineExpireTime(3600000);
//        message.setData(testSendIOS(json));
//        message.setPushNetWorkType(0); //可选。判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
//        Target target = new Target();
//        target.setAppId(AppConfig.igexin_appid);
//        target.setClientId(cid);
//        //用户别名推送，cid和用户别名只能2者选其一
//        //String alias = "个";
//        //target.setAlias(alias);
//        IPushResult ret = null;
//        try {
//            ret = push.pushMessageToSingle(message, target);
//        } catch (RequestException e) {
//            ret = push.pushMessageToSingle(message, target, e.getRequestId());
//            logger.error("RequestException cid=" + cid + ", msg=" + json, e);
//        }
//        if (ret != null) {
//            logger.info(ret.getResponse().toString() + ", cid=" + cid + ", msg=" + json);
//        } else {
//            logger.error("服务器响应异常 singleTranport Cids isEmpty cid=" + cid + ", msg=" + json);
//        }
    }

    public void transportationToApp(String tag, String phone, String province, String json) {
//        IGtPush push = new IGtPush(AppConfig.igexin_host, AppConfig.igexin_appkey, AppConfig.igexin_master);
        //透传模板
        TransportationToApp transportationToApp = new TransportationToApp(tag, phone, province, json);
        GROUP.executor.execute(transportationToApp);
//        TransmissionTemplate template = testSendIOS(json);
//        AppMessage message = new AppMessage();
//        message.setData(template);
//        //设置消息离线，并设置离线时间
//        message.setOffline(true);
//        //离线有效时间，单位为毫秒，可选
//        message.setOfflineExpireTime(3 * 24 * 1000 * 3600);
//        //设置推送目标条件过滤
//        List<String> appIdList = new ArrayList<>();
//        List<String> phoneTypeList = new ArrayList<>();
//        List<String> provinceList = new ArrayList<>();
//        List<String> tagList = new ArrayList<>();
//        appIdList.add(AppConfig.igexin_appid);
//        message.setAppIdList(appIdList);
//        //设置机型
//        if (phone != null) {
//            phoneTypeList.add(phone);
//            message.setPhoneTypeList(phoneTypeList);
//        }
//        //phoneTypeList.add("ANDROID");
//        //设置省份
//        if (province != null) {
//            provinceList.add(province);
//            message.setProvinceList(provinceList);
//        }
//        //设置标签内容
//        if (tag != null) {
//            tagList.add(tag);
//            message.setTagList(tagList);
//        }
//        IPushResult ret = push.pushMessageToApp(message);
//        logger.info(ret.getResponse().toString());
    }

    private void pushToApp(String title, String context, String tag, String phone, String province, String json) {
        IGtPush push = new IGtPush(AppConfig.igexin_host, AppConfig.igexin_appkey, AppConfig.igexin_master);
        //推送模板
        NotificationTemplate template = notificationTemplate(title, context, json);
        AppMessage message = new AppMessage();
        message.setData(template);
        //设置消息离线，并设置离线时间
        message.setOffline(true);
        //离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(3 * 24 * 1000 * 3600);
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
//        phoneTypeList.add("ANDROID");
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
        IPushResult ret = push.pushMessageToApp(message);
        logger.info(ret.getResponse().toString());
    }

    /**
     * 苹果单独推送
     *
     * @param device
     */
    public void ios_new_tuisong(String device, String context, boolean hashsound, int badge, Map<String, Object> msg) {
        Ios_new_tuisong ios_new_tuisong = new Ios_new_tuisong(device, context, hashsound, badge, msg);
        GROUP.executor.execute(ios_new_tuisong);
//        APNTemplate t = new APNTemplate();
//        APNPayload apnpayload = new APNPayload();
//        if (hashsound) {
//            apnpayload.setSound("haveSound.wav");
//        } else {
//            apnpayload.setSound("nosound.wav");
//        }
//        apnpayload.setBadge(badge);
//        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
//        alertMsg.setTitle("");
//        alertMsg.setBody(context);
//        alertMsg.setTitleLocKey("问啊");
//        alertMsg.setActionLocKey("");
//        apnpayload.setAlertMsg(alertMsg);
//        for (String key : msg.keySet()) {
//            apnpayload.addCustomMsg(key, msg.get(key));
//        }
//        t.setAPNInfo(apnpayload);
//        SingleMessage sm = new SingleMessage();
//        sm.setData(t);
//        IPushResult ret0 = push.pushAPNMessageToSingle(AppConfig.igexin_appid, device, sm);
//        logger.info("BackMessage=" + ret0.getResponse());
    }

    /**
     * 苹果单独推送
     */
    public void new_tuisong_app(List<String> devicetoken, String context, Map<String, Object> msg, boolean sound) {
        if (devicetoken == null || devicetoken.isEmpty()) return;
        New_tuisong_app new_tuisong_app = new New_tuisong_app(devicetoken, context, msg, sound);
        GROUP.executor.execute(new_tuisong_app);
//        APNTemplate t = new APNTemplate();
//        APNPayload apnpayload = new APNPayload();
//        if (sound) {
//            apnpayload.setSound("haveSound.wav");
//        } else {
//            apnpayload.setSound("nosound.wav");
//        }
//        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
//        alertMsg.setTitle("");
//        alertMsg.setBody(context);
//        alertMsg.setTitleLocKey("问啊");
//        alertMsg.setActionLocKey("");
//        for (String key : msg.keySet()) {
//            apnpayload.addCustomMsg(key, msg.get(key));
//        }
//        apnpayload.setAlertMsg(alertMsg);
//        t.setAPNInfo(apnpayload);
//        ListMessage message = new ListMessage();
//        message.setData(t);
//        String contentId = push.getAPNContentId(AppConfig.igexin_appid, message);
//        System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
//        IPushResult ret = push.pushAPNMessageToList(AppConfig.igexin_appid, contentId, devicetoken);
//        logger.info("result=" + ret.getResponse());

    }

    private void singleNotification(String cid, String title, String context, String json) {
        if (StringUtils.isEmpty(cid)) {
            logger.warn("singleNotification Cids isEmpty cid=" + cid + ", title=" + title + " , context=" + context + ", msg=" + json);
            return;
        }
        SingleNotification singleNotification = new SingleNotification(cid, title, context, json);
        GROUP.executor.execute(singleNotification);

//        SingleMessage message = new SingleMessage();
//        message.setOffline(true);
//        //离线有效时间，单位为毫秒，可选
//        message.setOfflineExpireTime(3 * 24 * 3600 * 1000);
//        message.setData(notificationTemplate(title, context, json));
//        message.setPushNetWorkType(0); //可选。判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
//        Target target = new Target();
//        target.setAppId(AppConfig.igexin_appid);
//        target.setClientId(cid);
//        //用户别名推送，cid和用户别名只能2者选其一
//        //String alias = "个";
//        //target.setAlias(alias);
//        IPushResult ret = null;
//        try {
//            ret = push.pushMessageToSingle(message, target);
//        } catch (RequestException e) {
//            ret = push.pushMessageToSingle(message, target, e.getRequestId());
//            logger.warn("RequestException cid=" + cid + " , title=" + title + " , context=" + context + ", msg=" + json);
//        }
//        if (ret != null) {
//            logger.info(ret.getResponse().toString() + " , cid=" + cid + ", title=" + title + " , context=" + context + ", msg=" + json);
//        } else {
//            logger.warn("服务器响应异常 cid=" + cid);
//        }
    }

    /**
     * 0 正常剔除答题者
     *
     * @param is_app 0
     */
    public void rejectOtherAnswer(Question question, String auid, int is_app) {
        Map<String, String> answer = Redis.shard.hgetAll(RedisTable.A + question.qid());
        if (!answer.isEmpty() && StringUtils.isNotEmpty(auid)) {
            answer.remove(auid);
        }
        for (String uid : answer.keySet()) {
            //todo 需要优化
            String qid = Redis.shard.get(RedisTable.TI + uid);
            if (StringUtils.isNotEmpty(qid) && qid.equalsIgnoreCase(question.qid())) {
                //判断用户是否,未本题目
                Redis.shard.hdel(RedisTable.A + question.qid(), uid);
                Redis.shard.del(RedisTable.TI + uid);
                //RedisTable.A + qid 通知未入围用户
                addNotNominated(uid, question, is_app);
            }
        }
    }

    public static NotificationTemplate notificationTemplate(String title, String context, String json) {
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(AppConfig.igexin_appid);
        template.setAppkey(AppConfig.igexin_appkey);
        // 设置通知栏标题与内容
        template.setTitle(title);
        template.setText(context);
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
        template.setTransmissionContent(json);
//        template.setAPNInfo(getApnPayload());//IOS
        // 设置定时展示时间
        // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
        return template;
    }

    public void sendMsgQuesPublish(Question que, int start, int size, int subtotle) {
        //随机取对应权限的用户数- 发送推送
        String tot_user = Redis.shard.get(RedisTable.USER_INCR_SORT);
        //
        int _totle = Verification.getInt(0, tot_user);
        if (_totle == 0) return;
        //计算范围
        int pc = _totle / subtotle;
        int _start = start * pc;
        int _to = (start + 1) * pc;
        QueRole role = QueRole.create(que.getPow());
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        //设置查询的表,用户表
        builder.setTypes(UserStruct.table_EL);
        //设置返回记录条数
        if (role != null) {//默认 10 人
            builder.setSize(size == 0 ? role.getPubnumber() : size);//设置返回数量
        }
        FreshFilterRule freshFilterRule = findFreshFilterRule();
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        //设置分段
        bool.must(QueryBuilders.rangeQuery("incr").from(_start).to(_to));
        //黑白名单 过滤
        bool.must(QueryBuilders.rangeQuery(UserStruct.access).from(0).to(1));
        if (freshFilterRule.getA() == FreshFilterRule.a_yes && StringUtils.isNotEmpty(que.getPkey())) {
            bool.mustNot(QueryBuilders.matchQuery(UserStruct.device, que.getPkey()));
        }
        //过滤规则 过滤
        Map<String, String> nosee = findUserNoSee(findUserByID(que.getQuid()));
        addNoSee(nosee, bool);
        //不能包含提问者本身
        //bool.mustNot(QueryBuilders.matchQuery(QuestionStruct.quid, que.getQuid()));
        //bool.mustNot(QueryBuilders.termQuery("_id",que.getQuid()));
        bool.must(QueryBuilders.matchQuery(UserStruct.auth, que.getType()));
        //必须是登录状态的
        bool.must(QueryBuilders.matchQuery(UserStruct.login, 0));
        //TODO 推送提醒过滤
        Calendar calendar = Calendar.getInstance();
        //推送设置过滤
        bool.must(QueryBuilders.matchQuery(UserStruct.pub, 1));
        //星期过滤
        bool.must(QueryBuilders.matchQuery(UserStruct.day, String.valueOf(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        //时间 - 关联过滤
        long time_ps = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);
        BoolQueryBuilder time = QueryBuilders.boolQuery();
        time.must(QueryBuilders.rangeQuery("start").to(time_ps));
        time.must(QueryBuilders.rangeQuery("end").from(time_ps));
        time.must(QueryBuilders.matchQuery("type", UserPt.type_time));
        bool.must(QueryBuilders.hasChildQuery(UserPt.table_el, time));
        // 价格 - 关联过滤
        BoolQueryBuilder price = QueryBuilders.boolQuery();
        price.must(QueryBuilders.matchQuery("type", UserPt.type_price));
        price.must(QueryBuilders.rangeQuery("start").to(que.getCash().longValue()));
        price.must(QueryBuilders.rangeQuery("end").from(que.getCash().longValue()));
        bool.must(QueryBuilders.hasChildQuery(UserPt.table_el, price));
        //系统过滤 - 保证选出来掉都有效果
        BoolFilterBuilder system = FilterBuilders.boolFilter();
        BoolFilterBuilder ios = FilterBuilders.boolFilter();
        ios.must(FilterBuilders.queryFilter(QueryBuilders.matchQuery(UserStruct.system, "ios")));
        ios.mustNot(FilterBuilders.missingFilter(UserStruct.device));
        BoolFilterBuilder android = FilterBuilders.boolFilter();
        android.must(FilterBuilders.queryFilter(QueryBuilders.matchQuery(UserStruct.system, "android")));
        android.mustNot(FilterBuilders.missingFilter(UserStruct.cid));
        system.should(ios);
        system.should(android);
        addGisFilter(que, freshFilterRule, system);
        builder.setPostFilter(system);
        //随机抽取
        ScriptSortBuilder sort = SortBuilders.scriptSort("Math.random()", "number");
        sort.order(SortOrder.ASC);
        builder.addSort(sort);
        //设置返回数据 ,用户CID
        builder.addField(UserStruct.cid);
        builder.addField(UserStruct.device);
        builder.addField(UserStruct.system);
        builder.addField(UserStruct.sound);
        builder.setQuery(bool);
        //TODO 添加 地理位置筛选
        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        SearchHit hit;
        Set<String> cids = new HashSet<>();
        Set<String> devicetokens = new HashSet<>();
        Set<String> devicetokens_nosound = new HashSet<>();

        int len = hits.getHits().length;
        for (int i = 0; i < len; i++) {
            hit = hits.getAt(i);
            String cid = fieldString(hit, UserStruct.cid);
            String syst = fieldString(hit, UserStruct.system);
            String sound = fieldString(hit, UserStruct.sound);
            String device = fieldString(hit, UserStruct.device);
            if (que.getQuid().equals(hit.getId())) {
                devicetokens.remove(device);
                devicetokens_nosound.remove(device);
                cids.remove(cid);
                continue;
            }
            /**答题人，收到问题推送 T	*/
            if (StringUtils.isNotEmpty(syst) && syst.equalsIgnoreCase(SYS_IOS)) {
                try {
                    logger.info("uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + fieldString(hit, "sound") + "\tDEV:" + fieldString(hit, "device"));
                    doknowisDeviceToken(devicetokens, devicetokens_nosound, sound, device);
                } catch (Exception e) {
                    logger.error("send pubios error ,ErrorMessage=" + e.getMessage());
                }
            } else {
                logger.info("uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + sound + "\tCID:" + cid);
                cids.add(cid);
            }
            if (StringUtils.isNotEmpty(cid)) {
                Redis.shard.sadd(RedisTable.QPL + que.qid(), cid);
            }
        }
        Classify classify = CacheManager.findClassifyById(que.getType());
        Map<String, Object> hash = new HashMap<>();
        hash.put(AndroidMessage.att_cmd, AndroidMessage.que_iusse);
        hash.put(AndroidMessage.att_data, que.qid());
        String title = "【" + classify.getName() + "】" + SysMessage.ans_new_que + que.getTitle() + "\"等你来答";
        new_tuisong_app(new ArrayList<>(devicetokens), title, hash, true);
        //将该用户加入到 可看列表中
        this.sendTuiSong(cids, que, title);
        new_tuisong_app(new ArrayList<>(devicetokens_nosound), title, hash, false);
        logger.info("CIDS : " + cids.size());
    }

    public Map<String, String> findUserNoSee(User user) {
        Map<String, String> nosee = new HashMap<>();
        if (user == null) return nosee;
        String acc = redischeckTyep(RedisTable.NOSEE + user.uid());
        String imei = redischeckTyep(RedisTable.NOSEE + user.getImei());
        String ip = redischeckTyep(RedisTable.NOSEE + user.getIp());
        if (StringUtils.isNotEmpty(acc))
            nosee.put(RedisTable.NOSEE_ACC, acc);
        if (StringUtils.isNotEmpty(imei))
            nosee.put(RedisTable.NOSEE_IMEI, imei);
        if (StringUtils.isNotEmpty(ip))
            nosee.put(RedisTable.NOSEE_IP, ip);
        return nosee;
    }

    private String redischeckTyep(String key) {
        if (StringUtils.isEmpty(key)) return null;
        String type = Redis.shard.type(key);
        if (type != null && type.equalsIgnoreCase("string")) {
            return Redis.shard.get(key);
        }
        return null;
    }

    public void doknowisDeviceToken(Set<String> devicetokens, Set<String> devicetokens_nosound, String sound, String device) {
        if (StringUtils.isNotEmpty(device) && device.length() == 64) {
            if ("0".equalsIgnoreCase(sound) && !devicetokens.contains(device)) {
                devicetokens_nosound.add(device);
            } else if (!devicetokens_nosound.contains(device)) {
                devicetokens.add(device);
            }
        }
    }

    public void sendMsgQuesPublish_Thread(final Question que, int size) {
        try {
            //随机取对应权限的用户数- 发送推送
            String tot_user = Redis.shard.get(RedisTable.USER_INCR_SORT);
            //当前用户总数
            int _totle = Verification.getInt(0, tot_user);
            if (_totle == 0) return;
            int _start = 0;
//          0  一般情况，推送人数小于符合用户，小于每页推送人数
//          1  发送人数大于 实际人数 小于等于一次性发送人数，这个时候全部查询用户，发送出去(初期，用户较少可能出现这种情况)
//          2  发送人数大于 一次性最大发送数（高额问答可能出现这种情况，因为高额问答推送的人数较多，可能大于一次性推送的人数）
            //  3  白名单全部推送

            QueRole role = QueRole.create(que.getPow());
            //过滤规则 过滤
            User user = findUserByID(que.getQuid());
            final int max = Verification.getInt(10, Redis.shard.hget(RedisTable.cache_http_sys_config, RedisTable.SYS_CONFIG.pubsp));
            final Map<String, String> nosee = findUserNoSee(findUserByID(que.getQuid()));
            if (role != null) {
                int pubnumber = role.getPubnumber();
                int _size = pubnumber;
                long sendAllUserCount = this.countUser(que, nosee); //符合推送条件用户的总数
                Random ra = new Random();
                List<Integer> listInt = new ArrayList<>();//保存随机发送到的页
                if(pubnumber <= size){ // 不分页
                    /**
                     * 白名单用户推送全部 -- --
                     * @author Modified by chenxx
                     * @Date 2016-03-18 11:04:54
                     */
                    if (pubnumber > sendAllUserCount || user.getAccess() == 1) {//推送人数大于查询到的符合条件的人数，则查询到的所有符合条件的用户都推送
                        pubnumber = (int)sendAllUserCount;
                        _size = (int)sendAllUserCount;
                    }else if(pubnumber < sendAllUserCount){//如果推送人数小于每页人数 且小于查询到符合条件的用户数
                        int randomMax = (int)sendAllUserCount - pubnumber;

                        int randomInt = 0;
                        if(randomMax > 0) {
                            randomInt = ra.nextInt(randomMax);
                        }
                        _start = randomInt;
                        _size = pubnumber;
                        pubnumber = _start + pubnumber;
                    }
                }else if (pubnumber > size) {//如果大于每页的最大值，则需要分页发送
                    if(sendAllUserCount > size){//如果符合条件的总数大于每页最多发送用户，则分页
                        if(sendAllUserCount >= pubnumber){//如果符合条件的用户大于要发的总数
                            //分段总数,即页数
                            int db = pubnumber / size + (pubnumber % size == 0 ? 0 : 1);
                            _size = pubnumber / db; // 每页发送数量
                            _start = 0;
                            int totaldb = (int)sendAllUserCount/size +(sendAllUserCount%size==0?0:1); //总页数
                            if(totaldb>db && db>0){
                                for(int i=0;i<db;i++){
                                    while(true){
                                        int page = ra.nextInt(totaldb);
                                        if(!listInt.contains(page)){
                                            listInt.add(page);
                                            break;
                                        }
                                    }
                                }
                            }
                        }else {//如果符合条件的小于要发的总数
                            //分段总数,即页数
                            int db = pubnumber / size + (pubnumber % size == 0 ? 0 : 1);
                            //_size = pubnumber / db; // 每页发送数量
                            _start = 0;
                            int totaldb = (int)sendAllUserCount/size +(sendAllUserCount%size==0?0:1); //总页数
                            _size = (int)sendAllUserCount/totaldb;
                            if(totaldb>0){
                                for(int i=0;i<totaldb;i++){
                                    while(true){
                                        int page = ra.nextInt(totaldb);
                                        if(!listInt.contains(page)){
                                            listInt.add(page);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }else{//不分页
                        pubnumber = (int)sendAllUserCount;
                        _size = (int)sendAllUserCount;
                    }
                }
                //这些人必推送
                this.sendPubMustSend(que,max);
                //分段查询,每段范围数量
                logger.info("send by page : size=" + size + "\t_size=" + _size + "\t_totle=" + _totle + "\tpubnumber=" + pubnumber + "\t" + " _start="+_start + "\t sendAllUserCount="+sendAllUserCount);
                if(listInt.size()>0){
                    for(int pagei:listInt){

                        final int final_start = _start + pagei * _size;
                        final int final_to = final_start + _size;
                        final int final_size = _size;
                        logger.info("loop send  pagei=" + pagei+ " final_size:"+final_size + " final_start:"+final_start+" final_to:"+final_to);
                        GROUP.executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                sendPub(que, final_size, final_start, final_to, nosee, max);
                            }
                        });
                    }
                }else{
                    final int final_start = _start;
                    final int final_to = final_start + _size;
                    final int final_size = _size;
                    logger.info("send once no pages"  +  " final_size:"+final_size + " final_start:"+final_start+" final_to:"+final_to);
                    GROUP.executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            sendPub(que, final_size, final_start, final_to, nosee, max);
                        }
                    });
                }

            }
        }catch (Exception e){
            logger.error("sendMsgQuesPublish_Thread",e);
        }


    }
/*    private long countUserNums(Question que, int _size, int _start, int _to, Map<String, String> nosee, int max,int type,int max_size){
        //设置返回记录条数
        FreshFilterRule freshFilterRule = findFreshFilterRule();
        BoolQueryBuilder bool = this.setQuery(que, freshFilterRule, nosee, _start, _to);
        BoolFilterBuilder system = this.setSystemQuery(que, freshFilterRule);
        CountRequestBuilder countRequestBuilder =  CElastic.elastic().client.prepareCount(CElastic.circle_index);
        countRequestBuilder.setQuery(bool);
        countRequestBuilder.setPostFilter(system);
        return countRequestBuilder.get().getCount();
    }*/

    /***
     * 统计符合推送条件的用户总数
     * @param que 问题
     * @param nosee 不可见规则
     */
    private long countUser(Question que, Map<String, String> nosee) {
        SearchHits searchHits = this.getSearchHits(que, nosee,0,0,0);
        return searchHits.getTotalHits();
    }

    private SearchHits getSearchHits(Question que, Map<String, String> nosee,int from,int to,int size){
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        //设置查询的表,用户表
        builder.setTypes(UserStruct.table_EL);
        //设置返回记录条数
        FreshFilterRule freshFilterRule = findFreshFilterRule();

        //设置查询条件
        BoolQueryBuilder bool = this.setQuery(que, freshFilterRule, nosee);
        BoolFilterBuilder system = this.setSystemQuery(que, freshFilterRule);
        bool.mustNot(QueryBuilders.termQuery("robot",1));
        bool.mustNot(QueryBuilders.termQuery("robot",2));
        bool.mustNot(QueryBuilders.termQuery("robot",3));//123是机器人
        builder.setPostFilter(system);
        //随机抽取
//      ScriptSortBuilder sort = SortBuilders.scriptSort("Math.random()", "number");
//      sort.order(SortOrder.ASC);
//      builder.addSort(sort);
        //设置返回数据 ,用户CID
//        ScriptSortBuilder sort = SortBuilders.scriptSort(UserStruct.randomSort, "number");
//        sort.order(SortOrder.ASC);
//        builder.addSort(sort);
        builder.addSort(UserStruct.randomSort,SortOrder.ASC);
        builder.addField(UserStruct.cid);//只去这四个数据
        builder.addField(UserStruct.device);
        builder.addField(UserStruct.system);
        builder.addField(UserStruct.sound);
        builder.setQuery(bool);
        builder.setFrom(from);
        builder.setSize(size);
        logger.debug("[current builder query json is]: " + builder.toString());
        //TODO 添加 地理位置筛选
        SearchResponse response = builder.get();
        return response.getHits();
    }

    /***
     *
     * @param que 问题
     * @param _size 发送总数 根据金额权限决定的发送人数
     * @param _start 发送起始位置
     * @param _to 发送end
     * @param nosee 不可见规则
     * @param max 大额上限，超过则加“元”
     */
    public void sendPub(Question que, int _size, int _start, int _to, Map<String, String> nosee, int max) {
        try {
            logger.debug("sendPub  _size = [" + _size + "], _start = [" + _start + "], _to = [" + _to + "], nosee = [" + nosee + "]");
            SearchHits hits = this.getSearchHits(que, nosee, _start, _to, _size);
            SearchHit hit;
            Set<String> cids = new HashSet<>();
            Set<String> devicetokens = new HashSet<>();
            Set<String> devicetokens_nosound = new HashSet<>();

            int len = hits.getHits().length;
            logger.debug("[cid totle size]: " + len);
            for (int i = 0; i < len; i++) {
                hit = hits.getAt(i);
                String cid = fieldString(hit, UserStruct.cid);
                String syst = fieldString(hit, UserStruct.system);
                String sound = fieldString(hit, UserStruct.sound);
                String device = fieldString(hit, UserStruct.device);
                if (que.getQuid().equals(hit.getId())) {
                    devicetokens.remove(device);
                    devicetokens_nosound.remove(device);
                    cids.remove(cid);
                    logger.debug("[removed device is]: " + cid + "\t qid is: " + que.qid());
                    continue;
                }
                /**答题人，收到问题推送 T	*/
                if (StringUtils.isNotEmpty(syst) && syst.equalsIgnoreCase(SYS_IOS)) {
                    try {
                        logger.debug("uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + fieldString(hit, "sound") + "\tDEV:" + fieldString(hit, "device"));
                        doknowisDeviceToken(devicetokens, devicetokens_nosound, sound, device);
                    } catch (Exception e) {
                        logger.error("send pubios error ,ErrorMessage=" + e.getMessage());
                    }
                } else {
                    logger.debug("uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + sound + "\tCID:" + cid);
                    cids.add(cid);
                }
//            if (StringUtils.isNotEmpty(cid)) {
//                Redis.shard.sadd(RedisTable.QPL + que.qid(), cid);
//            }
            }
            //查找分类
            Classify classify = CacheManager.findClassifyById(que.getType());
            Map<String, Object> hash = new HashMap<>();
            hash.put(AndroidMessage.att_cmd, AndroidMessage.que_iusse);
            hash.put(AndroidMessage.att_data, que.qid());
            ArrayList<String> dts = new ArrayList<>(devicetokens);
            String title = "【" + classify.getName() + "】" + SysMessage.ans_new_que + que.getTitle() + "\"等你来答";
            if (que.getCash().intValue() > max) {
                title = "【" + classify.getName() + "】【" + que.getCash().intValue() + "元】" + SysMessage.ans_new_que + que.getTitle() + "\"等你来答";
            }
            StringBuffer logsb = new StringBuffer();
            logsb.append("qid = "+ que.qid() + "  ");
            for (int i1 = 0; i1 < dts.size(); i1++) {
                logsb.append(dts.get(i1) + ",");
            }
            for (String strcid : cids) {
                logsb.append(strcid + ",");
            }
            for (String devicetokens_nosoundstr : devicetokens_nosound) {
                logsb.append(devicetokens_nosoundstr + ",");
            }
//        if (_start == 0) {
//            sendFirst(que, hash, title);
//        }
            //-================================================
            logger.debug("[pub cids are]: " + logsb.toString());
            new_tuisong_app(dts, title, hash, true);
            //将该用户加入到 可看列表中
            this.sendTuiSong(cids, que, title);
            new_tuisong_app(new ArrayList<>(devicetokens_nosound), title, hash, false);

            logger.info("[NEW_QUESTION_PUB_INFO] Qid=" + que.qid() + " devicetokens_nosound : " + devicetokens_nosound.size() + "\tdevicetokens_havesound = " + dts.size() + "\tcids = " + cids.size());
        }catch (Exception e){
            logger.error("发送推送异常：",e);
        }
    }


    /***
     *必推送方法
     */
    private void sendPubMustSend(Question que, int max) {
        //查找分类
        Classify classify = CacheManager.findClassifyById(que.getType());
        Map<String, Object> hash = new HashMap<>();
        hash.put(AndroidMessage.att_cmd, AndroidMessage.que_iusse);
        hash.put(AndroidMessage.att_data, que.qid());
        String title = "【" + classify.getName() + "】" + SysMessage.ans_new_que + que.getTitle() + "\"等你来答";
        if (que.getCash().intValue() > max) {
            title = "【" + classify.getName() + "】【" + que.getCash().intValue() + "元】" + SysMessage.ans_new_que + que.getTitle() + "\"等你来答";
        }
        sendFirst(que, hash, title);
    }




    private void sendFirst(Question que, Map<String, Object> hash, String title) {
        /********************************************************************************
         * 有题必须推送 :  15575834360 13810865238 13512827458 18602232105  18512213887 15122148107
         * Modified by chenxx 2016-3-21 18:43:45
         ********************************************************************************/
        Set<String> firstSend = new HashSet<>();
        Set<String> iOSFirstSet = new HashSet<>();

        List<String> userPhones = Redis.shard.lrange(RedisTable.MUST_SEND_USERS, 0, -1);
        List<User> uList = new ArrayList<>();

        for(String mobile:userPhones){
            User user = findUserByMobile(mobile);
            uList.add(user);
        }

//        User user1 = findUserByMobile("15575834360");
//        User user2 = findUserByMobile("13810865238");
//        User user3 = findUserByMobile("13512827458");
//        User user4 = findUserByMobile("18602232105");
//        User user5 = findUserByMobile("18512213887");
//        User user6 = findUserByMobile("15122148107");
//        User user7 = findUserByMobile("18622618050");
//        User user8 = findUserByMobile("13212039999");
//        User user9 = findUserByMobile("13032210117");
//        User user10 = findUserByMobile("13516215800");
//        User user11 = findUserByMobile("13032210117");
//        User user12 = findUserByMobile("18920189685");
//        uList.add(user1);
//        uList.add(user2);
//        uList.add(user3);
//        uList.add(user4);
//        uList.add(user5);
//        uList.add(user6);
//        uList.add(user7);
//        uList.add(user8);
//        uList.add(user9);
//        uList.add(user10);
//        uList.add(user11);
//        uList.add(user12);

        logger.info("must send  logs : qid=" +que.qid()  + " mobiles ：" +uList);
        for (User user : uList){
            if(user!=null ){
                logger.info(" send all question every body mobile ==== = == == = =" + user.getMobile());
                if (StringUtils.isNotEmpty(user.getSystem()) && user.getSystem().equalsIgnoreCase(SYS_IOS)) {
                    try {
                        //logger.debug("uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + fieldString(hit, "sound") + "\tDEV:" + fieldString(hit, "device"));
                        //doknowisDeviceToken(devicetokens, devicetokens_nosound, String.valueOf(user.getSound()), user.getDevice());
                        if(user.getDevice()!=null && user.getDevice().length() == 64){
                            iOSFirstSet.add(user.getDevice());
                        }else {
                            logger.error("device 错误" + user.getDevice());
                        }
                    } catch (Exception e) {
                        logger.error("send pubios error ,ErrorMessage=" + e.getMessage());
                    }
                } else {
                    firstSend.add(user.getCid());
                }
            }

        }
        logger.info("=======推送所有问题到指定用户==========");
        if(iOSFirstSet.size() > 0){
            new_tuisong_app(new ArrayList<>(iOSFirstSet), title, hash, true); // iOS ， 有声音
            //logger.info("is contain  chunchun " + iOSFirstSet.contains("4120f91bfe1dd5b268a1ba2cab2172da4eda7c6f3b81f5c782820d3da6cad1ba"));
            //logger.info("is contain xiaochen " + iOSFirstSet.contains("8be4f3cc4ad85054325b34b9d9a0daf360a6422e8e36f0c6290dcc1ad222459f"));
        }
        //将该用户加入到 可看列表中
        if(firstSend.size() > 0){
            this.sendTuiSong(firstSend, que, title); //Android
        }
        //new_tuisong_app(new ArrayList<>(devicetokens_nosound), title, hash, false); // iOS 没有声音
    }

    public BoolFilterBuilder setSystemQuery(Question que, FreshFilterRule freshFilterRule){
        //系统过滤 - 保证选出来掉都有效果
        BoolFilterBuilder system = FilterBuilders.boolFilter();
        BoolFilterBuilder ios = FilterBuilders.boolFilter();
        ios.must(FilterBuilders.queryFilter(QueryBuilders.termQuery(UserStruct.system, "ios")));
        ios.mustNot(FilterBuilders.missingFilter(UserStruct.device));
        BoolFilterBuilder android = FilterBuilders.boolFilter();
        android.must(FilterBuilders.queryFilter(QueryBuilders.termQuery(UserStruct.system, "android")));
        android.mustNot(FilterBuilders.missingFilter(UserStruct.cid));
        system.should(ios);
        system.should(android);
        addGisFilter(que, freshFilterRule, system);
        return system;
    }

    public BoolQueryBuilder setQuery(Question que, FreshFilterRule freshFilterRule, Map<String, String> nosee){
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
//        //设置分段
//        bool.must(QueryBuilders.rangeQuery("incr").from(_start).to(_to));
        //黑白名单 过滤
        bool.must(QueryBuilders.rangeQuery(UserStruct.access).from(0).to(1));
        if (freshFilterRule.getA() == FreshFilterRule.a_yes && StringUtils.isNotEmpty(que.getPkey())) {
            bool.mustNot(QueryBuilders.termQuery(UserStruct.device, que.getPkey()));
        }
        if (freshFilterRule.getIpEqual().equals(FreshFilterRule.IP_YES) && StringUtils.isNotEmpty(que.getIp())) {
            bool.mustNot(QueryBuilders.termQuery(UserStruct.ip, que.getIp()));
        }
        addNoSee(nosee, bool);
        //不能包含提问者本身
        //bool.mustNot(QueryBuilders.matchQuery(QuestionStruct.quid, que.getQuid()));
        //bool.mustNot(QueryBuilders.termQuery("_id",que.getQuid()));
        bool.must(QueryBuilders.termQuery(UserStruct.auth, que.getType()));
        //必须是登录状态的
        bool.must(QueryBuilders.termQuery(UserStruct.login, 0));
        //TODO 推送提醒过滤
        Calendar calendar = Calendar.getInstance();
        //推送设置过滤
        //bool.must(QueryBuilders.matchQuery(UserStruct.pub, 1));
        bool.must(QueryBuilders.termQuery(UserStruct.pub, 1));
        //星期过滤
        bool.must(QueryBuilders.termQuery(UserStruct.day, String.valueOf(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        //时间 - 关联过滤
        long time_ps = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);
        BoolQueryBuilder time = QueryBuilders.boolQuery();
        time.must(QueryBuilders.rangeQuery("start").to(time_ps));
        time.must(QueryBuilders.rangeQuery("end").from(time_ps));
        time.must(QueryBuilders.termQuery("type", UserPt.type_time));
        bool.must(QueryBuilders.hasChildQuery(UserPt.table_el, time));
        // 价格 - 关联过滤
        BoolQueryBuilder price = QueryBuilders.boolQuery();
        price.must(QueryBuilders.termQuery("type", UserPt.type_price));
        price.must(QueryBuilders.rangeQuery("start").to(que.getCash().longValue()));
        price.must(QueryBuilders.rangeQuery("end").from(que.getCash().longValue()));
        bool.must(QueryBuilders.hasChildQuery(UserPt.table_el, price));
        return  bool;
    }

    private void addNoSee(Map<String, String> nosee, BoolQueryBuilder bool) {
        if (nosee != null && !nosee.isEmpty()) {
            String imei = nosee.get(RedisTable.NOSEE_IMEI);
            if (StringUtils.isNotEmpty(imei)) {
                bool.mustNot(QueryBuilders.matchQuery(UserStruct.imei, imei));
            }
            String acc = nosee.get(RedisTable.NOSEE_ACC);
            if (StringUtils.isNotEmpty(acc)) {
                String[] accs = acc.split(JsonParams.SPLIT_BACK);
                for (String uid : accs) {
                    bool.mustNot(QueryBuilders.termQuery("_id", uid));
                }
            }
        }
    }

    private void addGisFilter(Question que, FreshFilterRule freshFilterRule, BoolFilterBuilder system) {
        if (StringUtils.isNotEmpty(que.getPosxy()) && freshFilterRule.getDistance() > 0) {
            String[] pos = que.getPosxy().split(",");
            if (pos.length == 2) {
                GeoDistanceRangeFilterBuilder filter = FilterBuilders.geoDistanceRangeFilter(QuestionStruct.posxy);
                filter.filterName(QuestionStruct.posxy);
                filter.point(Verification.getDoule(0, pos[0]), Verification.getDoule(0, pos[0]));
                filter.from(freshFilterRule.getDistance() / 1000.0 + "km");
                filter.to("99999999km");
                system.must(filter);
            }
        }
    }

    private FreshFilterRule findFreshFilterRule() {
        FreshFilterRule freshFilterRule = new FreshFilterRule();
        String json = Redis.shard.get(RedisTable.FRESHFILTERRULE_KEY);
        if (StringUtils.isNotEmpty(json)) {
            freshFilterRule = (FreshFilterRule) Json.jsonParser(json, FreshFilterRule.class);
        }
        return freshFilterRule;
    }

    private String fieldString(SearchHit hit, String key) {
        String value = null;
        SearchHitField field = hit.field(key);
        List<Object> list = field.getValues();
        if (!list.isEmpty() && list.get(0) != null) {
            value = list.get(0).toString();
        }
        return value;
    }


    private void sendTuiSong(Set<String> cids, Question que, String title) {
        if (cids == null || cids.isEmpty()) {
            logger.warn("listNotification Cids isEmpty cids=" + cids + ", questeion=" + que.qid());
            return;
        }
        Msg msg = new Msg();
        Map<String, Object> kvs = new HashMap<>();
        kvs.put(AndroidMessage.att_cmd, AndroidMessage.que_iusse);
        kvs.put(AndroidMessage.att_title, "问啊");
        kvs.put(AndroidMessage.att_context, title);
        kvs.put(AndroidMessage.att_data, que.qid());
        kvs.put(JsonParams.type, JsonParams.cmd);
        //设置消息离线，并设置离线时间
        //发送推送
        /**答题人，收到问题推送 T*/
        QueTimeAxis axis = new QueTimeAxis();
        axis.create(que.getConf());
        listTranport(cids, kvs, (axis.getRead() + axis.getRobCount()) * 1000);
//        listTranport(cids, "问啊", SysMessage.ans_new_que + que.getTitle(), msg);
    }

    public void sendQueInRobStep(Question question) {
        //修改题目状态 - 进入抢答阶段
        //判断题目状态是都小于1 - 1
        if (question.getStatus() >= QuestionStruct.stu_rob) return;
        question.setStatus(QuestionStruct.stu_rob);
        this.updateQuestionStatus(question, QuestionStruct.stu_rob, null);
        Set<String> cids = Redis.shard.smembers(RedisTable.QPL + question.qid());
        //========================================
        Msg msg = new Msg();
        msg.setType(Msg.type_quedetail);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());//问题ID
        map.put(QuestionStruct.status, QuestionStruct.stu_rob);//状态
        QueTimeAxis axis = new QueTimeAxis();
        axis.create(question.getConf());
        map.put(QuestionStruct.time, axis.getRobCount());//
        msg.setStatus(QuestionStruct.stu_rob);
        //状态
        msg.setList(map);
        //========================================
        listTranport(cids, msg);
        User user = findUserByID(question.getQuid());
        msg.setType(Msg.type_myque);
        singleTranport(user.getCid(), Json.json(msg));
    }

    public void sendQueInChoseStep(Question question, int is_app) throws IOException {
        //判斷是否有有人搶答
        if (question == null || question.getStatus() > QuestionStruct.stu_chose) return;
        //记录进入筛选阶段的时间
        //判断状态
        //透传通知提问人答题进入
        User user = findUserByID(question.getQuid());
        if (is_app == 1) {
            //如果不是前端
            if (question.getStatus() == QuestionStruct.stu_chose) return;
            //没有抢答人, 并且 问题处于抢答阶段
            if (question.getRobnum() <= 0) {
                logger.info("@@@@@######################@@@@@@@@@@@@@ question.robnumb=" + question.getRobnum());
                //通知计时,进程订单已经取消
//                Redis.CONNECT.publish(question.qid(), String.valueOf(QuestionStruct.stu_cancel));
                this.sendQuesTimer(question, QuestionStruct.stu_cancel, true);
                return;
            }
            question.setStatus(QuestionStruct.stu_chose);
            Put put = new Put(Bytes.toBytes(question.qid()));
            put.addColumn(BaseLog.family, QuestionStruct.status_byte, PInteger.INSTANCE.toBytes(question.getStatus()));
            CHbase.bean().put(Question.table, put);
            Map<String, Object> hash = new HashMap<>();
            hash.put(QuestionStruct.status, question.getStatus());
            CElastic.elastic().update(QuestionStruct.table_el, question.qid(), hash);
            //推送选择人
            this.sendSoundMessageRemindChose(user);
        }
        Set<String> cids = Redis.shard.smembers(RedisTable.QPL + question.qid());
        Msg msg = new Msg();
        msg.setType(Msg.type_quedetail);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());
        map.put(QuestionStruct.status, question.getStatus());
        QueTimeAxis axis = new QueTimeAxis();
        axis.create(question.getConf());
        //返回筛选时间
        map.put(QuestionStruct.time, axis.getScreen() * 60);
        //记录进入筛选的时间
        Redis.shard.setex(RedisTable.QTM + question.qid(), axis.getScreen() * 60 + 60, String.valueOf(System.currentTimeMillis()));
        msg.setStatus(QuestionStruct.stu_chose);
        msg.setList(map);
        listTranport(cids, msg);
        msg.setType(Msg.type_myque);
        if (user != null) singleTranport(user.getCid(), Json.json(msg));
        //TODO 推送通知提问人
        if (is_app == 0) {
            this.sendSoundMessageRemindChose(user);
        }
        try {
            SysMessage qmessage = new SysMessage();
            qmessage.setTitle(SysMessage.SYSMSG);//系统消息
            /**发题人，题目进入到筛选 */
            qmessage.setContext(SysMessage.que_into_chose);
            qmessage.setLogo(SysMessage.SYS_LOG);
            qmessage.setType(SysMessage.type_task);
            qmessage.setAbout(Integer.parseInt(AndroidMessage.cmd_que_12));
            qmessage.setQid(question.qid());
            this.addSystemMessage(user, qmessage, AndroidMessage.cmd_que_12, true, 0);
//            AddSystemMessage thread = new AddSystemMessage(user, qmessage, String.valueOf(qmessage.getAbout()), true,0,this);
//            GROUP.executor.execute(thread);
        } catch (Exception e) {
            logger.error("sendQueInChoseStep->addSystemMessage,message=" + e.getMessage());
        }
    }

    private void updateQuestionStatus(Question question, int status, String desc) {
        //if(question.getStatus()>=QuestionStruct.stu_rob) return;
        Map<String, Object> hash = new HashMap<>();
        hash.put(QuestionStruct.status, String.valueOf(status));
        //修改Elastic
        //修改Hbase
        Put put = new Put(Bytes.toBytes(question.qid()));
        long date = System.currentTimeMillis();
        switch (status) {
            case QuestionStruct.stu_rob:
                put.addColumn(QuestionStruct.family_time, Bytes.toBytes(String.valueOf(date)), Bytes.toBytes(status + "|报名中|"));
                break;
            case QuestionStruct.stu_success:
                put.addColumn(QuestionStruct.family_time, Bytes.toBytes(String.valueOf(date)), Bytes.toBytes(status + "|自动评价|auto"));
                put.addColumn(QuestionStruct.family, QuestionStruct.appsdate_byte, PLong.INSTANCE.toBytes(System.currentTimeMillis()));
                put.addColumn(QuestionStruct.family, QuestionStruct.app_byte, PInteger.INSTANCE.toBytes(question.getApp()));
                //已经转发红包 - 自动评价的题目不能转发红包
                put.addColumn(QuestionStruct.family, QuestionStruct.red_byte, PInteger.INSTANCE.toBytes(1));
                hash.put(QuestionStruct.app, QuestionStruct.app_all);
                break;
            case QuestionStruct.stu_cancel:
                put.addColumn(QuestionStruct.family_time, Bytes.toBytes(String.valueOf(date)), Bytes.toBytes(status + desc));
                break;
        }
        put.addColumn(QuestionStruct.family, QuestionStruct.status_byte, PInteger.INSTANCE.toBytes(status));
        try {
            CHbase.bean().put(Question.table, put);
            CElastic.elastic().update(QuestionStruct.table_el, question.qid(), hash);
        } catch (IOException e) {
            logger.error("updateQuestionStatus error", e);
        }
    }

    private void sendSoundMessageRemindChose(User user) {
        /***************************************
         * Add the user is in app , 0 out 1 in
         * so if the value is 0 need call user , else not
         * @author Modified by chenxx
         * @date 2016-1-12 10:25:44
         ***************************************/
        String value = Redis.shard.get(RedisTable.ISINAPP + user.uid());
        int type = Verification.getInt(0, value);
        if (type == 1) return;
        String filename = "saixuan.wav";
        String message = "sc" + JsonParams.SPLIT + user.getMobile() + JsonParams.SPLIT + filename;
        try {
            sendPost(AppConfig.sms_url, message);
        } catch (IOException e) {
            logger.error("SendSmsError message: " + e.getMessage(), e);
        }
    }

    public User findUserByMobile(String mobile) {
        String mob_sha1 = CircleMD5.encodeSha1(mobile);
        return findUserfromRedisBySha1(mob_sha1);
    }

    public User findUserfromRedisBySha1(String mob_sha1) {
        if (StringUtils.isEmpty(mob_sha1)) return null;
        Map<String, String> hash = Redis.shard.hgetAll(mob_sha1);
        if (hash == null || hash.isEmpty()) return null;
        return UserStruct.create(hash);
    }

    private void sendSoundMessageRemindAnswer(User user) {
        /***************************************
         * Add the user is in app , 0 out 1 in
         * so if the value is 0 need call user , else not
         * @author Modified by chenxx
         * @date 2016-1-12 10:25:44
         ***************************************/
        String value = Redis.shard.get(RedisTable.ISINAPP + user.uid());
        int type = Verification.getInt(0, value);
        if (type == 1) return;
        String filename = "answer.wav";
        String message = "sc" + JsonParams.SPLIT + user.getMobile() + JsonParams.SPLIT + filename;
        try {
            sendPost(AppConfig.sms_url, message);
        } catch (IOException e) {
            logger.error("SendSmsError message: " + e.getMessage(), e);
        }
    }

    private void listTranport(Set<String> cids, Map<String, Object> msg, int times) {
        if (cids.isEmpty()) {
            logger.warn("ListTranport Cids isEmpty cids=" + cids + ", msg=" + msg);
            return;
        }
        ListTranportTUISONG listTranport = new ListTranportTUISONG(cids, Json.json(msg), times);
        GROUP.executor.execute(listTranport);
    }

    /**
     * 发送透传
     */
    private void listTranport(Set<String> cids, Msg msg) {
        if (cids == null || cids.isEmpty()) {
            logger.warn("ListTranport Cids isEmpty cids=" + (cids == null ? "null" : cids.size()) + ", msg=" + msg);
            return;
        }
        ListTranport listTranport = new ListTranport(cids, Json.json(msg));
        GROUP.executor.execute(listTranport);
    }

    public void sendPost(String url, String message) throws IOException {
        CloseableHttpResponse response = null;
        try {
            Map<String, String> hash = new HashMap<>();
            hash.put(JsonParams.msg, message);
            response = MsgService.create().client.post(url, hash);
            //关闭流
            EntityUtils.consume(response.getEntity());
        } finally {
            if (response != null)
                response.close();
        }
    }

    public void sendQuesTimer(Question question, int status, boolean callback) throws IOException {
        //随机取值- 同一份配置多个key 保存, 保证分布在不同节点上
        //总计 100 个Key 0 - 99
        int index = (int) (question.getCdate() % 100);
        String fati = Redis.shard.hget(RedisTable.config_timer.QUESTION_CONFIG_TIMER + index, RedisTable.config_timer.FATI);
        if (StringUtils.isNotEmpty(fati) && fati.equals("1")) {
            CloseableHttpResponse response = null;
            try {
                Map<String, String> hash = new HashMap<>();
                hash.put(QuestionStruct.qid, question.qid());
                hash.put(QuestionStruct.status, String.valueOf(status));
                hash.put(QuestionStruct.conf, question.getConf());
                hash.put(JsonParams.back, String.valueOf(callback ? 1 : 0));
                response = MsgService.create().client.post(AppConfig.ques_timer_server, hash);
                //关闭流
                EntityUtils.consume(response.getEntity());
            } finally {
                if (response != null)
                    response.close();
            }
        } else {
            Redis.CONNECT.publish(question.qid(), String.valueOf(status));
        }
    }

    private void listNotification(Set<String> cids, String title, String context, Msg msg) {
        //离线 一分钟
        listNotification(cids, title, context, msg, 60000);
    }

    private void listNotification(Set<String> cids, String title, String context, Msg msg, int time) {
        if (cids.isEmpty()) {
            logger.warn("listNotification Cids isEmpty cids=" + cids + ", title=" + title + " , context=" + context + ", msg=" + msg);
            return;
        }

        ListNotification notification = new ListNotification(cids, title, context, Json.json(msg), time);
        GROUP.executor.execute(notification);

//        List<Target> targets = new ArrayList<>();
//        ListMessage message = new ListMessage();
//        message.setData(notificationTemplate(title, context, Json.json(msg)));
//        //置消息离线，并且置离线时间
//        message.setOffline(true);
//        //离线有效时间，单位为毫秒，
//        message.setOfflineExpireTime(time);
//        for (String cid : cids) {
//            Target target = new Target();
//            target.setAppId(AppConfig.igexin_appid);
//            target.setClientId(cid);
//            targets.add(target);
//        }
//        String taskId = push.getContentId(message);
//        //使用taskID对目标进行推送
//        IPushResult ret;
//        try {
//            ret = push.pushMessageToList(taskId, targets);
//        } catch (RequestException e) {
//            ret = push.pushMessageToList(taskId, targets);
//            logger.error("RequestException cids=" + cids + ", title=" + title + " , context=" + context + ", msg=" + msg);
//        }
//        if (ret != null) {
//            logger.info(ret.getResponse().toString() + ",cids=" + cids + ", title=" + title + " , context=" + context + ", msg=" + msg);
//        } else {
//            logger.warn("服务器响应异常 cids=" + cids);
//        }
    }

    public Question findQuestionByQid(String qid) throws IOException {
        Get get = new Get(Bytes.toBytes(qid));
        Result result = CHbase.bean().get(Question.table, get);
        if (result.isEmpty()) {
            GetResponse response = CElastic.elastic().get(QuestionStruct.table_el, qid);
            if (response.isSourceEmpty()) {
                return null;
            }
            Question question = (Question) Json.jsonParser(response.getSourceAsString(), Question.class);
            question.qid(response.getId());
            return question;
        }
        Question question = new Question();
        question.create(result);
        question.qid(qid);
        return question;
    }

    public boolean sendQueInAnswer(Question question) throws IOException {
        if (question.getStatus() > QuestionStruct.stu_answer) {
            if (StringUtils.isEmpty(question.getAuid())) {
                //通知计时,进程订单已经取消
//                Redis.CONNECT.publish(question.qid(), String.valueOf(QuestionStruct.stu_cancel));
                this.sendQuesTimer(question, QuestionStruct.stu_cancel, true);
            }
            return false;
        }
        Set<String> cids = Redis.shard.smembers(RedisTable.QPL + question.qid());
        Msg msg = new Msg();
        msg.setType(Msg.type_quedetail);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());
        map.put(QuestionStruct.status, QuestionStruct.stu_answer);
        map.put(UserStruct.uid, question.getAuid());
        msg.setStatus(QuestionStruct.stu_answer);
        msg.setList(map);
        listTranport(cids, msg);
        //透传通知答题人可以开始答题
        User quser = findUserByID(question.getQuid());
        msg.setType(Msg.type_myque);//通知你答题人
        if (quser != null) {
            singleTranport(quser.getCid(), Json.json(msg));
        }
        User auser = findUserByID(question.getAuid());
        msg.setType(Msg.type_myans);//通知你已经被选中
        if (auser != null) {
            singleTranport(auser.getCid(), Json.json(msg));
        }
        //剔除抢答人
        this.rejectOtherAnswer(question, question.getAuid(), 1);
        //TODO 语音提示 已经被选中
        sendSoundMessageRemindAnswer(auser);
        //提问人
        SysMessage qmessage = new SysMessage();
        qmessage.setTitle(SysMessage.SYSMSG);//系统消息
        /**发题人，题目进入到进行阶段	*/
        qmessage.setContext(SysMessage.que_into_answer);
        qmessage.setLogo(SysMessage.SYS_LOG);
        qmessage.setType(SysMessage.type_task);
        qmessage.setAbout(Integer.parseInt(AndroidMessage.cmd_que_11));
        qmessage.setQid(question.qid());
        this.addSystemMessage(quser, qmessage, AndroidMessage.cmd_que_11, false, 0);
//        AddSystemMessage thread = new AddSystemMessage(quser, qmessage, String.valueOf(qmessage.getAbout()), false,0,this);
//        GROUP.executor.execute(thread);
        //答题人
//        SysMessage amessage = new SysMessage();
//        amessage.setTitle(SysMessage.SYSMSG);//系统消息
//        /**答题人，题目已入围，进入到进行阶段*/
//        amessage.setContext(SysMessage.ans_rob_yes);
//        amessage.setLogo(SysMessage.SYS_LOG);
//        amessage.setType(SysMessage.type_task);
//        amessage.setAbout(SysMessage.about_question);
//        amessage.setQid(question.qid());
//        this.addSystemMessage(auser, amessage, AndroidMessage.cmd_que_10, false, 0);
        return true;
    }


    /**
     * 通知为入围的人,系统消息
     *
     * @param uid    用户ID
     * @param is_app 0 取消
     */
    private void addNotNominated(String uid, Question question, int is_app) {
        User user = findUserByID(uid);
        SysMessage message = new SysMessage();
        message.setTitle(SysMessage.TASK_MSG);//任务消息
        //您抢答的问题，对方已经取消
        if (is_app == 0) {
            message.setContext(SysMessage.ans_rob_cencel);
        } else {
            message.setContext(SysMessage.ans_rob_no);
        }
        //系统消息的LOGO
        message.setLogo(SysMessage.SYS_LOG);
        message.setType(SysMessage.type_task);
        message.setAbout(Integer.parseInt(AndroidMessage.cmd_que_2));
        message.setQid(question.qid());
        addSystemMessage(user, message, AndroidMessage.cmd_que_2, true, 0);
//        AddSystemMessage thread = new AddSystemMessage(user, message, String.valueOf(message.getAbout()), true,0,this);
//        GROUP.executor.execute(thread);
    }

    public void sendQueDelay(Question question, int is_app, String cid) {
        //不是处于回答阶段 -- 申请延时
        if (question.getStatus() != QuestionStruct.stu_answer) return;
        Msg msg = new Msg();
        msg.setType(Msg.type_delay_request);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());
        map.put(JsonParams.STA, 1);
        msg.setStatus(QuestionStruct.stu_delay);
        msg.setList(map);
        singleTranport(cid, Json.json(msg));
        addSystemRequestionDelay(question);
    }

    private void addSystemRequestionDelay(Question question) {
        //申请延时
        User user = findUserByID(question.getAuid());
        SysMessage message = new SysMessage();
        message.setTitle(SysMessage.TASK_MSG);//任务消息
        /**答题人，对方申请延时	T|S */
        message.setContext(SysMessage.ans_req_delay);
        //系统消息的LOGO
        message.setLogo(SysMessage.SYS_LOG);
        message.setType(SysMessage.type_task);
        message.setAbout(Integer.parseInt(AndroidMessage.cmd_que_3));
        message.setQid(question.qid());
        addSystemMessage(user, message, AndroidMessage.cmd_que_3, true, 0);
//        AddSystemMessage thread = new AddSystemMessage(user, message, String.valueOf(message.getAbout()), true,0,this);
//        GROUP.executor.execute(thread);
    }

    public void sendDelayDeal(Question question, String cid, int type) {
        //不是处于回答阶段
//        if (question.getStatus() != QuestionStruct.stu_delay) return;
        User quser = findUserByID(question.getQuid());
        User auser = findUserByID(question.getAuid());
        if (type == 1) {//接收延时,收到系统消息
            /**发题人，题目进入到延时阶段	仅仅推送 */
            Long size = Redis.shard.llen(RedisTable.SMSG + quser.uid());
            Map<String, Object> hash = new HashMap<>();
            hash.put(AndroidMessage.att_cmd, AndroidMessage.cmd_que_6);
            hash.put(AndroidMessage.att_title, SysMessage.TASK_MSG);
            hash.put(AndroidMessage.att_context, SysMessage.que_into_delay);
            hash.put(AndroidMessage.att_data, question.qid());
            sendTuiSong_new(quser, SysMessage.que_into_delay, size.intValue(), hash, 0);
            /**答题人，题目进入到延时阶段	*/
            SysMessage message = new SysMessage();
            message.setTitle(SysMessage.TASK_MSG);//任务消息
            message.setContext(SysMessage.ans_info_delay);
            //系统消息的LOGO
            message.setLogo(SysMessage.SYS_LOG);
            message.setType(SysMessage.type_task);
            message.setAbout(Integer.parseInt(AndroidMessage.cmd_que_7));
            message.setQid(question.qid());
            addSystemMessage(auser, message, AndroidMessage.cmd_que_7, true, 0);
//            AddSystemMessage thread = new AddSystemMessage(auser, message, String.valueOf(message.getAbout()), true,0,this);
//            GROUP.executor.execute(thread);
        } else {
            SysMessage message = new SysMessage();
            message.setTitle(SysMessage.TASK_MSG);//任务消息
            /**发题人，对方拒绝延时	*/
            message.setContext(SysMessage.que_no_delay);
            //系统消息的LOGO
            message.setLogo(SysMessage.SYS_LOG);
            message.setType(SysMessage.type_task);
            message.setAbout(Integer.parseInt(AndroidMessage.cmd_que_15));
            message.setQid(question.qid());
            addSystemMessage(quser, message, AndroidMessage.cmd_que_15, true, 0);
//            AddSystemMessage thread = new AddSystemMessage(quser, message, String.valueOf(message.getAbout()), true,0,this);
//            GROUP.executor.execute(thread);
        }
        Msg msg = new Msg();
        msg.setType(Msg.type_delay_request);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());
        map.put(JsonParams.STA, type == 1 ? 2 : 3);// type =1 接收延时 : sta=2, type=0 ,拒绝延时 . sta=3
//        //计算时间
//        QueTimeAxis axis = new QueTimeAxis();
//        axis.create(question.getConf());
//        String time  = Redis.shard.get(RedisTable.QTM+question.qid());
//        long syst = Verification.getLong(System.currentTimeMillis(),time);
//        int dt =(axis.getAnswer()+axis.getDelay())*60-(int) ((System.currentTimeMillis()-syst)/1000);
//        map.put(JsonParams.time, dt<0?axis.getDelay()*60:dt);// type =1 接收延时 : sta=2, type=0 ,拒绝延时 . sta=3
        msg.setStatus(QuestionStruct.stu_delay);
        msg.setList(map);
        singleTranport(quser.getCid(), Json.json(msg));
    }

    /**
     *
     * @param question 问题
     */
    public void finishAnsAndQueRank(Question question){
        Redis.shard.incr(RedisTable.RANGE_ANSQUES_TIMES+question.getAuid());//答题者总回答次数加1
        Redis.shard.incr(RedisTable.RANGE_QUES_TIMES+question.getQuid());  //提问者总提问次数加1
        long nowLong = System.currentTimeMillis();
        String yyyyMMdd = TimeUtil.formatLongToStr(nowLong, "yyyyMMdd");
        String todayAnsTimesKey = RedisTable.RANGE_ANSQUES_DAY_TIMES + yyyyMMdd + "|"+question.getAuid();
        String todayAskTimesKey = RedisTable.RANGE_QUES_DAY_TIMES + yyyyMMdd + "|"+question.getQuid();


        Redis.shard.incrBy(todayAnsTimesKey,getAQTimes(question.getAuid(),0));//用户今天回答次数+1
        Redis.shard.expire(todayAnsTimesKey,24*3600); //设置用户今天答题计数器过期时间为1天
        Redis.shard.incrBy(todayAskTimesKey,getAQTimes(question.getQuid(),1));//用户今天的提问次数 +1
        Redis.shard.expire(todayAskTimesKey,24*3600);

        Redis.shard.set(RedisTable.LAST_ANS_TIME + question.getAuid(), String.valueOf(nowLong)); //答题者最后的答题时间
        Redis.shard.set(RedisTable.LAST_QUE_TIME + question.getQuid(), String.valueOf(nowLong)); //提问者最后的提问时间


//        //计录周排行榜数据
//        int week = TimeUtil.weekOfYear(nowLong);
//        String thisWeekAnsKey = RedisTable.RANGE_ANSQUES_DAY_TIMES_WEEK + week + "|"+question.getAuid();//每周回答
//        String thisWeekQusKey = RedisTable.RANGE_QUES_DAY_TIMES_WEEK + week + "|"+question.getQuid(); // 每周提问
//        Redis.shard.incr(thisWeekAnsKey);//用户本周回答次数+1
//        Redis.shard.expire(thisWeekAnsKey,7*24*3600); //设置用户本周答题计数器过期时间为1周
//        Redis.shard.incr(thisWeekQusKey);//用户本周的提问次数 +1
//        Redis.shard.expire(thisWeekQusKey,7*24*3600);//用户本周提问次数+1
//
//        Redis.shard.set(RedisTable.LAST_ANS_TIME_WEEK + question.getAuid(), String.valueOf(nowLong)); //本周答题者最后的答题时间
//        Redis.shard.set(RedisTable.LAST_QUE_TIME_WEEK + question.getQuid(), String.valueOf(nowLong)); //本周提问者最后的提问时间

        String year_week = TimeUtil.weekOfYearAndWeek(System.currentTimeMillis());
        String rowKeyA = year_week + "_"+ question.getAuid();
        String rowKeyQ = year_week + "_"+ question.getQuid();
        long now = System.currentTimeMillis();
        try {
            HBaseUtils.increment("CIRCLE.WEEKRANK",rowKeyA,"0","rangeAnsQuesDayTimesWeek");//用户本周回答次数+1
            Put putA = new Put(Bytes.toBytes(rowKeyA));
            putA.addColumn(Bytes.toBytes("0"),Bytes.toBytes("LAST_ANS_TIME_WEEK".toUpperCase()), PLong.INSTANCE.toBytes(now));//本周答题者最后的答题时间
            putA.addColumn(Bytes.toBytes("0"),Bytes.toBytes("USERID".toUpperCase()), Bytes.toBytes(question.getAuid()));//本周提问者最后的提问时间
            putA.addColumn(Bytes.toBytes("0"),Bytes.toBytes("YEAR_WEEK".toUpperCase()), Bytes.toBytes(year_week));//本周提问者最后的提问时间
            CHbase.bean().put("CIRCLE.WEEKRANK", putA);

            HBaseUtils.increment("CIRCLE.WEEKRANK",rowKeyQ,"0","rangeQuesDayTimesWeek");//用户本周提问次数+1
            Put putQ = new Put(Bytes.toBytes(rowKeyQ));
            putQ.addColumn(Bytes.toBytes("0"),Bytes.toBytes("LAST_QUE_TIME_WEEK".toUpperCase()), PLong.INSTANCE.toBytes(now));//本周提问者最后的提问时间
            putQ.addColumn(Bytes.toBytes("0"),Bytes.toBytes("USERID".toUpperCase()), Bytes.toBytes(question.getQuid()));//本周提问者最后的提问时间
            putQ.addColumn(Bytes.toBytes("0"),Bytes.toBytes("YEAR_WEEK".toUpperCase()), Bytes.toBytes(year_week));//本周提问者最后的提问时间
            CHbase.bean().put("CIRCLE.WEEKRANK", putQ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.rankChange(question,yyyyMMdd); //计算每天排行榜数据
    }

    /**
     *
     * @param userId 用户id
     * @param type 0 答题， 1 问题
     * @return
     */
    private int getAQTimes(String userId,int type){
        int times = 1;
        Map<String, String> map = null;
        if(type == 0){
            map = Redis.shard.hgetAll(RedisTable.RANKOOANS);
        }else if(type==1){
            map = Redis.shard.hgetAll(RedisTable.RANKOOQUE);
        }
        if(map==null || map.size()==0) return times;
        User user = findUserByID(userId);
        String staticGroup = user.getStaticGroupId();
        String sc = null;
        if(staticGroup!=null && !staticGroup.isEmpty()){
            String[] groupArr = staticGroup.split("\\|");
            for(String group:groupArr){
                if(map.containsKey(group)){
                    sc = map.get(group);
                    if(sc!=null && !sc.isEmpty()){
                        String[] timesArr = sc.split("\\|");
                        double min = Double.parseDouble(timesArr[0]);
                        double max = Double.parseDouble(timesArr[1]);
                        Random r = new Random();
                        if(max <=1){//次数压缩
                            double dTimes =  (r.nextInt((int)(max*10+1-min*10)) + (int)(min*10))/10d;
                            times = (int)(dTimes);
                            return times;
                        }else if(min >= 1){//次数翻倍
                            times = r.nextInt((int)(max-min+1)) + (int)min;
                            return times;
                        }
                    }
                }
            }
        }
        return times ;
    }

    /***
     * 当问题完成时调用，重新计算排行榜分数
     * @param question 问题对象
     */
    public void rankChange(Question question,String yyyyMMdd){
        logger.info(question.qid() + " 重新计算排行榜数据 yyyyMMdd=" + yyyyMMdd );
        Map<String, String> tutorRules = Redis.shard.hgetAll(RedisTable.rankRulesKey+ "|" + RedisTable.tutor);//导师
        Map<String, String> superscholarRules = Redis.shard.hgetAll(RedisTable.rankRulesKey+ "|" + RedisTable.superscholar);//导师
        Map<String, String> mazeRules = Redis.shard.hgetAll(RedisTable.rankRulesKey+ "|" + RedisTable.maze);//导师
        //导师
        if(tutorRules != null ){
            long tutorAuidScore = getScore(question.getAuid(), yyyyMMdd, tutorRules);
            long tutorQuidScore = getScore(question.getQuid(), yyyyMMdd, tutorRules);
            logger.info("用户" +question.getAuid()+" 回答者导师排行榜分数："+tutorAuidScore);
            logger.info("用户" +question.getQuid()+" 提问者导师排行榜分数："+tutorQuidScore);
            recursionInsert(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor, tutorAuidScore,question.getAuid());
            recursionInsert(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor, tutorQuidScore,question.getQuid());
//            Long zcountA = Redis.shard.zcount(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor, tutorAuidScore, tutorAuidScore);
//            Long zcountQ = Redis.shard.zcount(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor, tutorQuidScore, tutorQuidScore);
//            if(zcountA==0){
//                Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor  ,tutorAuidScore,question.getAuid()); //最强导师每日排行榜
//            }else if(zcountA > 0){
//                Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor  ,tutorAuidScore-zcountA,question.getAuid()); //最强导师每日排行榜
//            }
//            if(zcountQ == 0){
//                Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor  ,tutorQuidScore,question.getQuid()); //最强导师每日排行榜
//            }else if(zcountQ > 0){
//                Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor  ,tutorQuidScore-zcountQ,question.getQuid()); //最强导师每日排行榜
//            }
//            //每周
//            double tutorAuidScoreWeek = getWeekScoreByUser(question.getAuid(), week, tutorRules);
//            double tutorQuidScoreWeek = getWeekScoreByUser(question.getQuid(), week, tutorRules);
//            logger.info("用户" +question.getAuid()+" 导师周排行榜分数："+tutorAuidScoreWeek);
//            logger.info("用户" +question.getQuid()+" 导师周排行榜分数："+tutorQuidScoreWeek);
//            Redis.shard.zadd(RedisTable.WEEKRANKING + week + "|" + RedisTable.tutor ,tutorAuidScoreWeek,question.getAuid()); //最强导师每周排行榜
//            Redis.shard.zadd(RedisTable.WEEKRANKING + week + "|" + RedisTable.tutor ,tutorQuidScoreWeek,question.getQuid()); //最强导师每周排行榜
        }
        //学霸
        if(superscholarRules != null){
            long superscholarAuidScore =  getScore(question.getAuid(), yyyyMMdd, superscholarRules);
            long superscholarQuidScore =  getScore(question.getQuid(), yyyyMMdd, superscholarRules);
            logger.info("用户" +question.getAuid()+" 回答者学霸排行榜分数："+superscholarAuidScore);
            logger.info("用户" +question.getQuid()+" 提问者学霸排行榜分数："+superscholarQuidScore);
            recursionInsert(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar,superscholarAuidScore,question.getAuid());
            recursionInsert(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar,superscholarQuidScore,question.getQuid());

//            Long zcountA = Redis.shard.zcount(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar, superscholarAuidScore, superscholarAuidScore);
//            Long zcountQ = Redis.shard.zcount(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar, superscholarQuidScore, superscholarQuidScore);
//            if(zcountA==0){
//                Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar ,superscholarAuidScore,question.getAuid()); //学霸每日排行榜
//            }else if(zcountA > 0){
//                Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar ,superscholarAuidScore-zcountA,question.getAuid()); //学霸每日排行榜
//            }
//            if(zcountQ == 0){
//                Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar ,superscholarQuidScore,question.getQuid()); //学霸每日排行榜
//            }else if(zcountQ > 0){
//                Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar ,superscholarQuidScore-zcountQ,question.getQuid()); //学霸每日排行榜
//            }

//            //每周
//            double superscholarAuidScoreWeek =  getWeekScoreByUser(question.getAuid(), week, superscholarRules);
//            double superscholarQuidScoreWeek =  getWeekScoreByUser(question.getQuid(), week, superscholarRules);
//            logger.info("用户" +question.getAuid()+" 学霸周排行榜分数："+superscholarAuidScoreWeek);
//            logger.info("用户" +question.getQuid()+" 学霸周排行榜分数："+superscholarQuidScoreWeek);
//            Redis.shard.zadd(RedisTable.WEEKRANKING + week + "|" + RedisTable.superscholar,superscholarAuidScoreWeek,question.getAuid()); //学霸每周排行榜
//            Redis.shard.zadd(RedisTable.WEEKRANKING + week + "|" + RedisTable.superscholar,superscholarQuidScoreWeek,question.getQuid()); //学霸每周排行榜
        }
        //if 回答者是妹子，究极学妹
        String s = CircleMD5.encodeSha1(question.getAphone());
        String sex = Redis.shard.hget(s, "sex");
        if(mazeRules != null){
            if("1".equals(sex)){//如果是女神
                //每天
                long mazeAudiScore = getScore(question.getAuid(), yyyyMMdd, mazeRules);
                logger.info("用户" +question.getAuid()+" 回答者究极妹子排行榜分数："+mazeAudiScore);
                recursionInsert(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.maze,mazeAudiScore,question.getAuid());
//                if(zcountA > 0){
//                    Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.maze, mazeAudiScore, question.getAuid());//究极妹子每日排行榜
//                }else if(zcountA > 0){
//                    Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.maze, mazeAudiScore-zcountA, question.getAuid());//究极妹子每日排行榜
//                }

//                //每周
//                double mazeAudiScoreWeek = getWeekScoreByUser(question.getAuid(), week, mazeRules);
//                logger.info("用户" +question.getAuid()+" 究极妹子周排行榜分数："+mazeAudiScoreWeek);
//                Redis.shard.zadd(RedisTable.WEEKRANKING + week + "|" + RedisTable.maze, mazeAudiScoreWeek, question.getAuid());//究极妹子每周排行榜
            }
            //if 提问者是妹子，究极学妹
            String s1 = CircleMD5.encodeSha1(question.getQphone());
            String sex1 = Redis.shard.hget(s1, "sex");
            if("1".equals(sex1)){
                long mazeQuidScore = getScore(question.getQuid(), yyyyMMdd, mazeRules);
                logger.info("用户" +question.getQuid()+" 提问者究极妹子排行榜分数："+mazeQuidScore);
                recursionInsert(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.maze,mazeQuidScore,question.getQuid());
//                if(zcountQ==0){
//                    Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.maze, mazeQuidScore, question.getQuid());//究极妹子每日排行榜
//                }else if(zcountQ>0){
//                    Redis.shard.zadd(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.maze, mazeQuidScore-zcountQ, question.getQuid());//究极妹子每日排行榜
//                }
//                double mazeQuidScoreWeek = getWeekScoreByUser(question.getQuid(), week, mazeRules);
//                logger.info("用户" +question.getQuid()+" 究极妹子周排行榜分数："+mazeQuidScoreWeek);
//                Redis.shard.zadd(RedisTable.WEEKRANKING + week + "|" + RedisTable.maze, mazeQuidScoreWeek, question.getQuid());//究极妹子每周排行榜
            }
        }
        logger.info("排行榜数据计算完毕");

    }

//    private void recursionInsert(String key,long score,String userId){
//        Long zcount = Redis.shard.zcount(key, score, score);
//        if(zcount>0){
//            recursionInsert(key,score-1,userId);
//        }else if(zcount == 0){
//            Redis.shard.zadd(key, score, userId);
//        }
//    }

    private void recursionInsert(String key,long score,String userId){
        for(int i =0;;i++){
            long zcount = Redis.shard.zcount(key, score+i*10, score+(i+1)*10);
            if(zcount >= 10){
                i++;
            }else if(zcount<10 && zcount>0){
                Set<Tuple> tuples = Redis.shard.zrevrangeByScoreWithScores(key, score + (i+1) * 10, score + i * 10);
                long minScore = 999999999999999999L;
                for(Tuple t: tuples){
                    if(t.getScore() < minScore){
                        minScore = Math.round(t.getScore());
                    }
                }
                Redis.shard.zadd(key, minScore - 1, userId);
                logger.info(zcount + "循环了"+i+"次，插入排行榜分数");
                break;
            }else{
                Redis.shard.zadd(key, score, userId);
                logger.info(zcount + "循环了"+i+"次，插入排行榜分数");
                break;
            }
        }
    }



    /**
     * 计算分数
     * @param uid 用户id
     * @param yyyyMMdd 日期，必须是指定格式
     * @param rules     规则
     * @return 分数
     */
    private long getScore(String uid, String yyyyMMdd, Map<String, String> rules) {
        String s  = RedisTable.RANGE_ANSQUES_DAY_TIMES + yyyyMMdd + "|" + uid;
        String s1 = RedisTable.RANGE_MARK_TIMES + yyyyMMdd + "|" + uid;
        String s2 = RedisTable.RANGE_MARK_SCORE + yyyyMMdd + "|" + uid;
//        String s3 = RedisTable.LAST_ANS_TIME + "|" + uid;
//        String s4 = RedisTable.LAST_COMMOT_TIME + "|" + uid;
        String s5 = RedisTable.RANGE_QUES_DAY_TIMES + yyyyMMdd + "|" + uid;
        String s6 = RedisTable.RANGE_MARK_OHTER_TIMES + yyyyMMdd + "|" + uid;
        String s7 = RedisTable.RANGE_MARK_OTHER_SCORE + yyyyMMdd + "|" + uid;
//        String s8 = RedisTable.LAST_QUE_TIME + "|" + uid;
//        String s9 = RedisTable.LAST_COMMOT_OTHTER_TIME + "|" + uid;
        logger.info("计算 uid=========" + uid);
        logger.info("回答次数 ==="+Double.parseDouble(Redis.shard.get(s)==null?"0":Redis.shard.get(s)) + " 权重===" + weight(rules, RankRulesStruct.rulesAnsNum));
        logger.info("回答被评价次数 ==="+Double.parseDouble(Redis.shard.get(s1)==null?"0":Redis.shard.get(s1)) + " 权重===" + weight(rules, RankRulesStruct.rulesAAppNum));
        logger.info("回答被评价分数 ==="+Double.parseDouble(Redis.shard.get(s2)==null?"0":Redis.shard.get(s2)) + " 权重===" + weight(rules, RankRulesStruct.rulesAAppScore));
        logger.info("提问次数 ==="+Double.parseDouble(Redis.shard.get(s5)==null?"0":Redis.shard.get(s5)) + " 权重===" + weight(rules, RankRulesStruct.rulesQueNum));
        logger.info("提问被评价次数 ==="+Double.parseDouble(Redis.shard.get(s6)==null?"0":Redis.shard.get(s6)) + " 权重===" + weight(rules, RankRulesStruct.rulesQAppNum));
        logger.info("提问被评价分数 ==="+Double.parseDouble(Redis.shard.get(s7)==null?"0":Redis.shard.get(s7)) + " 权重===" + weight(rules, RankRulesStruct.rulesQAppScore));
        return Math.round(Double.parseDouble(Redis.shard.get(s)==null?"0":Redis.shard.get(s)) * weight(rules, RankRulesStruct.rulesAnsNum) //回答数
                + Double.parseDouble(Redis.shard.get(s1)==null?"0":Redis.shard.get(s1)) * weight(rules,RankRulesStruct.rulesAAppNum)     //回答评价次数
                + Double.parseDouble(Redis.shard.get(s2)==null?"0":Redis.shard.get(s2)) * weight(rules,RankRulesStruct.rulesAAppScore)   //回答评价分数
                + Double.parseDouble(Redis.shard.get(s5)==null?"0":Redis.shard.get(s5)) * weight(rules,RankRulesStruct.rulesQueNum) //提问数
                + Double.parseDouble(Redis.shard.get(s6)==null?"0":Redis.shard.get(s6)) * weight(rules,RankRulesStruct.rulesQAppNum)
                + Double.parseDouble(Redis.shard.get(s7)==null?"0":Redis.shard.get(s7)) * weight(rules,RankRulesStruct.rulesQAppScore));
//              + (MaxLong - Double.parseDouble(Redis.shard.get(s3)==null?"0":Redis.shard.get(s3)))/part * weight(rules,RankRulesStruct.rulesAnsNumTime)  //回答到到次数时间
//              + (MaxLong - Double.parseDouble(Redis.shard.get(s4)==null?"0":Redis.shard.get(s4)))/part * weight(rules,RankRulesStruct.rulesAAppScoreTime) //回答到评价分数的时间
//              + (MaxLong - Double.parseDouble(Redis.shard.get(s8)==null?"0":Redis.shard.get(s8)))/part * weight(rules,RankRulesStruct.rulesQueNumTime)
//              + (MaxLong - Double.parseDouble(Redis.shard.get(s9)==null?"0":Redis.shard.get(s9)))/part * weight(rules,RankRulesStruct.rulesQAppScoreTime);
    }

    private double weight(Map<String, String> map, String key){
        return Math.pow(10, Double.parseDouble(map.get(key)));
    }



//    /**
//     * 计算分数
//     * @param uid 用户id
//     * @param week 本年的第几周
//     * @param rules     规则
//     * @return
//     */
//    private double getWeekScoreByUser(String uid , int week, Map<String, String> rules) {
//        String s = RedisTable.RANGE_ANSQUES_DAY_TIMES_WEEK + week + "|" + uid;
//        String s1 = RedisTable.RANGE_MARK_TIMES_WEEK + week + "|" + uid;
//        String s2 = RedisTable.RANGE_MARK_SCORE_WEEK + week + "|" + uid;
//        String s3 = RedisTable.LAST_ANS_TIME_WEEK + "|" + uid;
//        String s4 = RedisTable.LAST_COMMOT_TIME_WEEK + "|" + uid;
//        String s5 = RedisTable.RANGE_QUES_DAY_TIMES_WEEK + week + "|" + uid;
//        String s6 = RedisTable.RANGE_MARK_OHTER_TIMES_WEEK + week + "|" + uid;
//        String s7 = RedisTable.RANGE_MARK_OTHER_SCORE_WEEK + week + "|" + uid;
//        String s8 = RedisTable.LAST_QUE_TIME_WEEK + "|" + uid;
//        String s9 = RedisTable.LAST_COMMOT_OTHTER_TIME_WEEK + "|" + uid;
//        return Double.parseDouble(Redis.shard.get(s)==null?"0":Redis.shard.get(s)) * weight(rules, RankRulesStruct.rulesAnsNum) //回答数
//                + Double.parseDouble(Redis.shard.get(s1)==null?"0":Redis.shard.get(s1)) * weight(rules,RankRulesStruct.rulesAAppNum)     //回答评价次数
//                + Double.parseDouble(Redis.shard.get(s2)==null?"0":Redis.shard.get(s2)) * weight(rules,RankRulesStruct.rulesAAppScore)   //回答评价分数
//                + (MaxLong - Double.parseDouble(Redis.shard.get(s3)==null?"0":Redis.shard.get(s3)))/part * weight(rules,RankRulesStruct.rulesAnsNumTime)  //回答到到次数时间
//                + (MaxLong - Double.parseDouble(Redis.shard.get(s4)==null?"0":Redis.shard.get(s4)))/part * weight(rules,RankRulesStruct.rulesAAppScoreTime) //回答到评价分数的时间
//
//                + Double.parseDouble(Redis.shard.get(s5)==null?"0":Redis.shard.get(s5)) * weight(rules,RankRulesStruct.rulesQueNum) //提问
//                + Double.parseDouble(Redis.shard.get(s6)==null?"0":Redis.shard.get(s6)) * weight(rules,RankRulesStruct.rulesQAppNum)
//                + Double.parseDouble(Redis.shard.get(s7)==null?"0":Redis.shard.get(s7)) * weight(rules,RankRulesStruct.rulesQAppScore)
//                + (MaxLong - Double.parseDouble(Redis.shard.get(s8)==null?"0":Redis.shard.get(s8)))/part * weight(rules,RankRulesStruct.rulesQueNumTime)
//                + (MaxLong - Double.parseDouble(Redis.shard.get(s9)==null?"0":Redis.shard.get(s9)))/part * weight(rules,RankRulesStruct.rulesQAppScoreTime);
//    }

    /**
     * 完成 - 评价
     *
     * @param question question infomtion
     * @param is_app   is http server send request, 1 is backserver
     */
    public void sendQueFinish(Question question, int is_app) {
        User quser = findUserByID(question.getQuid());
        User auser = findUserByID(question.getAuid());
        //自动完成,
        if (is_app == 1) {
            if (question.getStatus() >= QuestionStruct.stu_finish) {
                return;
            }
            //自动到账
            try {
                Redis.shard.del(question.qid());
                confirmFinish(question, quser);
                //Modified 2015年12月14日10:13:18 个回答人和答题人添加小红点
                if (auser != null && quser != null) {
                    Redis.shard.hincrBy(RedisTable.QUEN + quser.uid(), RedisTable.QUENQ, 1);
                    Redis.shard.hincrBy(RedisTable.QUEN + auser.uid(), RedisTable.QUENA, 1);
                }
            } catch (IOException e) {
                logger.error("autoQueFinish error qid" + question.qid(), e);
            }
        }
        //解锁答题人
        Redis.shard.del(RedisTable.TI + question.getAuid());
        Msg msg = new Msg();
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());
        map.put(JsonParams.QUESTION.status, QuestionStruct.stu_finish);
        msg.setStatus(QuestionStruct.stu_finish);
        msg.setList(map);
        msg.setType(Msg.type_myans);
        if (auser != null) singleTranport(auser.getCid(), Json.json(msg));//我的回答
        msg.setType(Msg.type_myque);
        if (quser != null) singleTranport(quser.getCid(), Json.json(msg));//我的提问
        Set<String> cids = Redis.shard.smembers(RedisTable.QPL + question.qid());
        msg.setType(Msg.type_quedetail);
        cids.remove(auser.getCid());//不包含答题人
        cids.remove(quser.getCid());//不包含提问人
        listTranport(cids, msg);
        //===========================================<<<<<<<<<<<<开始了
        // Modified by chenxx  2015年10月14日18:08:02
        // 我的回答-添加系统消息
        //===========================================
        SysMessage message = new SysMessage();
        message.setTitle(SysMessage.SYSMSG);//系统消息
        /**答题人，题目完成，进入评价阶段*/
        message.setContext(SysMessage.ans_into_app);
        message.setLogo(SysMessage.SYS_LOG);
        message.setType(SysMessage.type_task);
        message.setAbout(Integer.parseInt(AndroidMessage.cmd_que_8));
        message.setQid(question.qid());
        this.addSystemMessage(auser, message, AndroidMessage.cmd_que_8, true, 0);
//        AddSystemMessage thread = new AddSystemMessage(auser, message, String.valueOf(message.getAbout()), true,0,this);
//        GROUP.executor.execute(thread);
        //提问人
        SysMessage qmessage = new SysMessage();
        qmessage.setTitle(SysMessage.SYSMSG);//系统消息
        /**发题人，题目进入到评价阶段*/
        qmessage.setContext(SysMessage.que_into_app);
        qmessage.setLogo(SysMessage.SYS_LOG);
        qmessage.setType(SysMessage.type_task);
        qmessage.setAbout(Integer.parseInt(AndroidMessage.cmd_que_9));
        qmessage.setQid(question.qid());
        this.addSystemMessage(quser, qmessage, AndroidMessage.cmd_que_9, true, 0);
//        AddSystemMessage thread1 = new AddSystemMessage(quser, qmessage, String.valueOf(qmessage.getAbout()), true,0,this);
//        GROUP.executor.execute(thread1);
        //透传列表-清除
        Redis.shard.del(RedisTable.QPL + question.qid());
        //======================================华丽的结束标记>>>>>>>
    }

    public void confirmFinish(Question que, User user) throws IOException {
        //确认给钱 -
        if (que.getStatus() >= QuestionStruct.stu_finish) return;
        Put put = new Put(Bytes.toBytes(que.qid()));
        if (que.getStatus() == QuestionStruct.stu_answer) {
            putcashToUserPackect(que, put);
        }
        long fdate = System.currentTimeMillis();
        que.setStatus(QuestionStruct.stu_finish);
        String value = "5|完成题目" + JsonParams.SPLIT + "确认方式:自动确认";
        put.addColumn(QuestionStruct.family_time, Bytes.toBytes(String.valueOf(System.currentTimeMillis())), Bytes.toBytes(value));
        put.addColumn(BaseLog.family, QuestionStruct.status_byte, PInteger.INSTANCE.toBytes(que.getStatus()));
        put.addColumn(BaseLog.family, QuestionStruct.fdate_byte, PLong.INSTANCE.toBytes(fdate));
        CHbase.bean().put(Question.table, put);
        Map<String, Object> hash = new HashMap<>();
        hash.put(QuestionStruct.status, QuestionStruct.stu_finish);
        hash.put(QuestionStruct.ispay, 0);//是否转入已经回答人余额中
        hash.put(QuestionStruct.fdate, fdate);//完成时间
        CElastic.elastic().update(QuestionStruct.table_el, que.qid(), hash);
        User auser = findUserByID(que.getAuid());
        int before_qnum = user.getQnum();
        int before_anum = auser.getAnum();
        addUserAnsNumber(auser);
        addUserQuesNumber(user);
//        Redis.shard.rpop(RedisTable.ontime + que.getQuid());
        if (StringUtils.isNotEmpty(que.getDisid())) { // 优惠券 总包-----
            try {
                Coupon coupon = findCouponById(que.getDisid());
                if (coupon != null && StringUtils.isNotEmpty(coupon.getParent())) {
                    Redis.shard.incr(RedisTable.COUPON_U + coupon.getParent());
                }
            } catch (Exception e) {
                logger.warn("set coupon use to question ");
            }
        }
        //告诉佳佳答题完成 - 但是没有评价
        //拉人券判断
        if (StringUtils.isNotEmpty(user.getLaren())) {
            //提问者拉人券,回答超过一个&&提问等于1个 或者 回答等于1个
            if (before_qnum == 0 && user.getAnum() == 0 && user.getQnum() == 1) {
                sendLaren(user, findUserByID(user.getLaren()));
            }
        }
        if (StringUtils.isNotEmpty(auser.getLaren())) {
            //回答者拉人券
            if (before_anum == 0 && auser.getAnum() == 1 && auser.getQnum() == 0) {
                sendLaren(auser, findUserByID(auser.getLaren()));
            }
        }
    }

    public void putcashToUserPackect(Question que, Put put) throws IOException {
        BigDecimal cash = que.getCash();
        //生成流水- 答题人的流水
        long date = System.currentTimeMillis();
        Stream stream = new Stream();
        stream.id(que.getAuid() + date);
        stream.setUid(que.getAuid());
        stream.setMoney(cash);//收入-
        stream.setPay(cash);//实际-收入金额
        stream.setDisid("");
        stream.setStype(Stream.TY_1_profit);//收益
        stream.setPtype(Stream.PY_2_USER);//系统转账
        stream.setAccount(que.getQuid());
        stream.setSdate(date);
        stream.setVdate(date);
        stream.setFdate(date);
        stream.setDescs("回答 : " + que.getTitle());
        stream.setStatus(Stream.STATUS_SUCCESS);
        stream.setQid(que.qid());
        Put sput = stream.createPut(stream.id(), stream.getSdate());
        CHbase.bean().put(Stream.table, sput);//流水存Hbase
        que.setFdate(date);
        Redis.shard.lpush(RedisTable.STREAM_ALL + stream.getUid(), stream.id());
        Redis.shard.lpush(RedisTable.STREAM_PROFIT + stream.getUid(), stream.id());
        put.addColumn(QuestionStruct.family_time, Bytes.toBytes(String.valueOf(que.getFdate())), Bytes.toBytes("5|完成题目|确认方式:自动完成"));
        put.addColumn(BaseLog.family, QuestionStruct.ispay_byte, PInteger.INSTANCE.toBytes(0));
        UserPacket packet = findUserPacket(que.getAuid());
        packet.setPrecash(packet.getPrecash().add(que.getCash()));
        //packet.setTotle(packet.getPrecash()==null?que.getCash():packet.getPrecash().add(que.getCash()));
        Put pput = packet.createPut(packet.uid());
        //修改 提问数 - 修改回答数
        CHbase.bean().put(UserPacket.table, pput);//答题人,赏金入账
    }

    private void sendLaren(User user, User laren) {
        //TODO 判断获得拉人券,
//        int lre = Verification.getInt(10, Redis.shard.get(RedisTable.larenGroupKeyPerfix + laren.uid()));
//        if (Redis.shard.llen(RedisTable.LRL + laren.uid()) >= lre) return;
        //Step1:从redis 中取 , 如果没有 固定限额 10
        //发送拉人券
        if (user == null) return;
        String topic = random_pull_topic();
        if (StringUtils.isNotEmpty(topic)) {
            String message = simple_user_info(laren, user.uid());
            KeyedMessage<String, String> msg = new KeyedMessage<>(topic, message);
            AppConfig.producerPool.send(msg);
        }
    }

    private String random_pull_topic() {
        return Redis.shard.srandmember(RedisTable.pullcoupon);
    }

    private static String simple_user_info(User user, String from) {
        User_min min = new User_min();
        min.setUid(user.uid());
        min.setCdate(user.getCdate());
        min.setCha(user.getChannel());
        min.setCity(user.getCity());
        min.setSex(user.getSex());
        min.setSys(user.getSystem());
        min.setSys(user.getSystem());
        min.setFrom(from);
        return Json.json(min);
    }

    public void addUserAnsNumber(User auser) {
        String auid = auser.uid();
        String mobilSha1 = Redis.shard.get(RedisTable.USER_UID + auid);
        long number = Redis.shard.hincrBy(mobilSha1, UserStruct.anum, 1);
        auser.setAnum((int) number);
        Map<String, String> map = new HashMap<>(1);
        map.put(UserStruct.anum, String.valueOf(number));
        CElastic.elastic().udpate(UserStruct.table_EL, auid, map);
        Put put = new Put(Bytes.toBytes(auid));
        put.addColumn(UserStruct.family_info, UserStruct.anum_byte, PInteger.INSTANCE.toBytes(number));
    }

    public void addUserQuesNumber(User user) {
        String mobilSha1 = CircleMD5.encodeSha1(user.getMobile());
        long number = Redis.shard.hincrBy(mobilSha1, UserStruct.qnum, 1);
        user.setQnum((int) number);
        Map<String, String> map = new HashMap<>(1);
        map.put(UserStruct.qnum, String.valueOf(number));
        CElastic.elastic().udpate(UserStruct.table_EL, user.uid(), map);
        Put put = new Put(Bytes.toBytes(user.uid()));
        put.addColumn(UserStruct.family_info, UserStruct.qnum_byte, PInteger.INSTANCE.toBytes(number));
    }

    public void sendQueAppratise(Question question, int is_app, String uid) {
        Msg msg = new Msg();
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());
        map.put(JsonParams.QUESTION.status, QuestionStruct.stu_success);
        msg.setStatus(QuestionStruct.stu_success);
        msg.setList(map);
        User auser = findUserByID(question.getAuid());
        User quser = findUserByID(question.getQuid());
        if (is_app == 1 && question.getStatus() == QuestionStruct.stu_finish) {//佳佳 自动评价
            try {
                if (question.getStatus() >= QuestionStruct.stu_success || question.getApp() == QuestionStruct.app_all) {
                    logger.warn("question=" + question.qid() + " is_app=" + is_app + ",uid=" + uid);
                    return;
                }
                long date = System.currentTimeMillis();
                switch (question.getApp()) {
                    case QuestionStruct.app_no:
                        //提问者自动评价
                        autoAppAnswer(question, auser, date, msg);
                        //回答者自动评分
                        autoAppQueser(question, quser, date, msg);
                        break;
                    case QuestionStruct.app_ans:
                        //提问人自动评价
                        autoAppAnswer(question, auser, date, msg);
                        break;
                    case QuestionStruct.app_que:
                        //回答人自动评分
                        autoAppQueser(question, quser, date, msg);
                        break;
                }
                question.setApp(QuestionStruct.app_all);
                question.setStatus(QuestionStruct.stu_success);
                updateQuestionStatus(question, QuestionStruct.stu_success, null);
            } catch (IOException e) {
                logger.error("auto apprise error,", e);
            }
        } else if (StringUtils.isNotEmpty(uid)) {
            if (uid.equalsIgnoreCase(question.getQuid())) { //通知提问人,已经评价
                SysMessage sysMessage = new SysMessage();
                sysMessage.setAbout(Integer.parseInt(AndroidMessage.cmd_que_4));
                /**发题人，对方已评价	*/
                sysMessage.setContext(SysMessage.que_ready_app);
                sysMessage.setLogo(SysMessage.SYS_LOG);
                sysMessage.setQid(question.qid());
                sysMessage.setTitle(SysMessage.TASK_MSG);
                sysMessage.setType(SysMessage.type_task);
                sysMessage.setUrl("");
                addSystemMessage(quser, sysMessage, AndroidMessage.cmd_que_4, true, 0);
//                AddSystemMessage thread = new AddSystemMessage(quser, sysMessage, String.valueOf(sysMessage.getAbout()), true,0,this);
//                GROUP.executor.execute(thread);
            } else if (uid.equalsIgnoreCase(question.getAuid())) {//通知回答人, 提问人已经评价
                SysMessage sysMessage = new SysMessage();
                sysMessage.setAbout(Integer.parseInt(AndroidMessage.cmd_que_5));
                /**答题人，对方已评价	*/
                sysMessage.setContext(SysMessage.ans_queapp);
                sysMessage.setLogo(SysMessage.SYS_LOG);
                sysMessage.setQid(question.qid());
                sysMessage.setTitle(SysMessage.TASK_MSG);
                sysMessage.setType(SysMessage.type_task);
                sysMessage.setUrl("");
                addSystemMessage(auser, sysMessage, AndroidMessage.cmd_que_5, true, 0);
//                AddSystemMessage thread = new AddSystemMessage(auser, sysMessage, String.valueOf(sysMessage.getAbout()), true,0,this);
//                GROUP.executor.execute(thread);
            } else {
                logger.warn("unknown user of this question,qid=" + question.qid() + ",uid=" + uid);
            }
        }
    }

    /**
     * 推送
     *
     * @param user    ,用户信息
     * @param context 显示内容
     * @param bage    显示数字标示
     * @param kvs     传输数据
     * @param sound   0 水滴声 1 无声
     */
    public void sendTuiSong_new(User user, String context, int bage, Map<String, Object> kvs, int sound) {
        if (StringUtils.isEmpty(user.getSystem())) {
            logger.warn("user no system not ios|android , uid=" + user.uid() + ",context=" + context + ",kvs=" + kvs);
        } else if (user.getSystem().equalsIgnoreCase(SYS_IOS)) {
            //IOS 推送
            if (StringUtils.isNotEmpty(user.getDevice()) && user.getDevice().length() == 64) {
                //iosSend(String device ,String token,String sound,int bage,Map<String, String> keyValue,String alert)
                try {
                    //苹果推送去除,这两个字段
                    kvs.remove(AndroidMessage.att_title);
                    kvs.remove(AndroidMessage.att_context);
                    //推送
                    logger.info("sendTuiSong_new userid="+user.uid()+" device="+user.getDevice()+" "+Json.json(kvs));
                    ios_new_tuisong(user.getDevice(), context, sound == 0, bage, kvs);
                } catch (Exception e) {
                    logger.error("苹果推送异常 : 异常信息:[" + e.getMessage() + "]");
                }
            } else {
                logger.warn("user device is empty , uid=" + user.uid() + ",context=" + context + ",device=" + user.getDevice());
            }
        } else if (user.getSystem().toLowerCase().equals(SYS_ANDROID)) {
            logger.warn("todo android tuisong , uid=" + user.uid() + ",context=" + context + ",kvs=" + kvs);
            //TODO Android 推送
            kvs.put(JsonParams.type, JsonParams.cmd);
            kvs.put(AndroidMessage.att_title, "问啊");
            kvs.put(AndroidMessage.att_context, context);
            logger.info("sendTuiSong_new userid="+user.uid()+" cid="+user.getCid()+" "+Json.json(kvs));
            singleTranport(user.getCid(), Json.json(kvs));
        } else {
            logger.warn("unknow user system not ios|android , uid=" + user.uid() + ",context=" + context + ",kvs=" + kvs);
        }
    }

    private void autoAppQueser(Question question, User quser, long date, Msg msg) throws IOException {
//        String value = question.qid() +
//                JsonParams.SPLIT + 5 +
//                JsonParams.SPLIT + JsonParams.SPACE +
//                JsonParams.SPLIT + date;
        Redis.shard.lrem(RedisTable.SQ + quser.uid(), 0, question.qid());
        long size = Redis.shard.lpush(RedisTable.SQ + quser.uid(), question.qid());
        Qcheck qcheck = new Qcheck();
        qcheck.setQid(question.qid());
        qcheck.setContext("系统默认五星好评");
        qcheck.setScore(5);
        qcheck.setTime(date);
        qcheck.setAuid(question.getAuid());
        qcheck.setUid(question.getQuid());
        Put qput = qcheck.createPut(question.qid());
        CHbase.bean().put(AppraiseStruct.table_que, qput);
        updateQuesScore(5, quser, size);
        msg.setType(Msg.type_myque);
        singleTranport(quser.getCid(), Json.json(msg));//我的提问
        SysMessage sysMessage = new SysMessage();
        sysMessage.setAbout(Integer.parseInt(AndroidMessage.cmd_que_4));
        /**发题人，对方已评价*/
        sysMessage.setContext(SysMessage.que_ready_app);
        sysMessage.setLogo(SysMessage.SYS_LOG);
        sysMessage.setQid(question.qid());
        sysMessage.setTitle(SysMessage.TASK_MSG);
        sysMessage.setType(SysMessage.type_task);
        sysMessage.setUrl("");
        addSystemMessage(quser, sysMessage, AndroidMessage.cmd_que_4, true, 0);
//        AddSystemMessage thread = new AddSystemMessage(quser, sysMessage, String.valueOf(sysMessage.getAbout()), true,0,this);
//        GROUP.executor.execute(thread);

        //计算提问被评价之后的分数变动
        long nowLong = System.currentTimeMillis();
        String yyyyMMdd = TimeUtil.formatLongToStr(nowLong, "yyyyMMdd");
        String todayTimesKey = RedisTable.RANGE_MARK_OHTER_TIMES + yyyyMMdd + "|" + quser.uid();
        String todayScoreKey = RedisTable.RANGE_MARK_OTHER_SCORE + yyyyMMdd + "|" + quser.uid();
        String lastCommitTimeKey = RedisTable.LAST_COMMOT_OTHTER_TIME + yyyyMMdd + "|" + quser.uid();
        appQueserScores(5, nowLong, todayTimesKey, todayScoreKey, lastCommitTimeKey,1);

        String rowKey = TimeUtil.weekOfYearAndWeek(System.currentTimeMillis())+"_"+quser.uid();
        HBaseUtils.increment("CIRCLE.WEEKRANK",rowKey,"0","rangeMarkOhterTimesWeek");//评价次数+1
        HBaseUtils.incrementN("CIRCLE.WEEKRANK",rowKey,"0","rangeMarkOhterScoreWeek",5);//评价分数自增
        Put putRank = new Put(Bytes.toBytes(rowKey));
        putRank.addColumn(Bytes.toBytes("0"),Bytes.toBytes("LAST_COMMOT_OTHTER_TIME_WEEK".toUpperCase()), PLong.INSTANCE.toBytes(nowLong));//最后一次提问评价的时间
        CHbase.bean().put("CIRCLE.WEEKRANK", putRank);
        //重新计算分数
        rankChange(question,yyyyMMdd);
    }
    /***
     * 记录答题者评价提问者 评价之后的数据
     * @param source 分数
     * @param nowLong 时间
     * @param timesKey 评价次数key
     * @param scoreKey 分数key
     * @param lastCommitTimeKey 最后评价时间
     * @param days 1每天，7每周
     */
    private void appQueserScores(float source, long nowLong, String timesKey, String scoreKey, String lastCommitTimeKey,int days) {
        Redis.shard.incr(timesKey); //评价次数 +1
        Redis.shard.expire(timesKey,days*24*3600); //过期
        String T = Redis.shard.get(timesKey);
        if(T==null || T.isEmpty()) T = "0";
        int todayTimes = Integer.parseInt(T); //增长过之后的次数
        String avgScore = Redis.shard.get(scoreKey);
        if(avgScore==null || avgScore.isEmpty()){
            avgScore = "0";
        }
        String newAvg = String.valueOf((Double.parseDouble(avgScore)*(todayTimes-1) + source)/todayTimes);
        Redis.shard.set(scoreKey,newAvg); // 今天提问被评价分数平均分
        Redis.shard.expire(scoreKey,days*24*3600); // 设置过期时间
        Redis.shard.set(lastCommitTimeKey, String.valueOf(nowLong)); //最后一次提问评价的时间
        Redis.shard.expire(lastCommitTimeKey, days*24*3600); //设置过期时间
    }

    private void autoAppAnswer(Question question, User auser, long date, Msg msg) throws IOException {
        msg.setType(Msg.type_myans);
        singleTranport(auser.getCid(), Json.json(msg));//我的回答
//        String value = question.qid() +
//                JsonParams.SPLIT + 5 +
//                JsonParams.SPLIT + 5 +
//                JsonParams.SPLIT + 5 +
//                JsonParams.SPLIT + JsonParams.SPACE +
//                JsonParams.SPLIT + date;
        Redis.shard.lrem(RedisTable.SA + auser.uid(), 0, question.qid());
        long size = Redis.shard.lpush(RedisTable.SA + auser.uid(), question.qid());
        Acheck acheck = new Acheck();
        acheck.setTime(date);
        acheck.setUid(question.getAuid());
        acheck.setQuid(question.getQuid());
        acheck.setContext("系统默认五星好评");
        acheck.setAtt(5);
        acheck.setSpeed(5);
        acheck.setDeep(5);
        acheck.setScore(5);
        acheck.setQid(question.qid());
        Put put = acheck.createPut(question.qid());
        CHbase.bean().put(AppraiseStruct.table_ans, put);
        updateAnswerScore(5, 5, 5, auser, size);
        //系统消息
        SysMessage sysMessage = new SysMessage();
        sysMessage.setAbout(Integer.parseInt(AndroidMessage.cmd_que_5));
        /**答题人，对方已评价	*/
        sysMessage.setContext(SysMessage.ans_queapp);
        sysMessage.setLogo(SysMessage.SYS_LOG);
        sysMessage.setQid(question.qid());
        sysMessage.setTitle(SysMessage.TASK_MSG);
        sysMessage.setType(SysMessage.type_task);
        sysMessage.setUrl("");
        addSystemMessage(auser, sysMessage, AndroidMessage.cmd_que_5, true, 0);
//        AddSystemMessage thread = new AddSystemMessage(auser, sysMessage, String.valueOf(sysMessage.getAbout()), true,0,this);
//        GROUP.executor.execute(thread);

        //计算回答被评价之后分数的变动
        long nowLong = System.currentTimeMillis();
        String yyyyMMdd = TimeUtil.formatLongToStr(nowLong, "yyyyMMdd");
        String todayTimesKey = RedisTable.RANGE_MARK_TIMES + yyyyMMdd + "|" + auser.uid();
        String todayScoreKey = RedisTable.RANGE_MARK_SCORE + yyyyMMdd + "|" + auser.uid();
        String commitTimeKey = RedisTable.LAST_COMMOT_TIME + yyyyMMdd + "|" + auser.uid();
        appAnserScores(5, 5, 5, nowLong,  todayTimesKey,todayScoreKey, commitTimeKey,1);

        String rowKey = TimeUtil.weekOfYearAndWeek(System.currentTimeMillis())+"_"+auser.uid();
        HBaseUtils.increment("CIRCLE.WEEKRANK",rowKey,"0","rangeMarkTimesWeek");//评价次数+1
        HBaseUtils.incrementN("CIRCLE.WEEKRANK",rowKey,"0","rangeMarkScoreWeek",5+5+5);//评价分数自增
        Put putRank = new Put(Bytes.toBytes(rowKey));
        putRank.addColumn(Bytes.toBytes("0"),Bytes.toBytes("LAST_COMMOT_TIME_WEEK".toUpperCase()), PLong.INSTANCE.toBytes(nowLong));//最后一次提问评价的时间
        CHbase.bean().put("CIRCLE.WEEKRANK", putRank);
        //重新计算分数
        rankChange(question,yyyyMMdd);
    }

    /**
     *
     * @param deep 分数
     * @param att 分数
     * @param speed 分数
     * @param nowLong 时间
     * @param timesKey 评价次数key
     * @param scoreKey 评价分数key
     * @param commitTimeKey 评价最后时间key
     * @param day 1每天，7是每周
     */
    private void appAnserScores(double deep, double att, double speed, long nowLong, String timesKey, String scoreKey,  String commitTimeKey,int day) {
        Redis.shard.incr(timesKey); //被评价次数 +1
        Redis.shard.expire(timesKey,day*24*3600);
        String t = Redis.shard.get(timesKey);
        if(t==null || t.isEmpty()) t = "0";
        int todayTimes = Integer.parseInt(t); //增长过之后的次数
        String avgScore = Redis.shard.get(scoreKey);
        if(avgScore==null || avgScore.isEmpty()){
            avgScore = "0";
        }
        String newAvg = String.valueOf((Double.parseDouble(avgScore)*3*(todayTimes-1) + deep+att+speed)/3/todayTimes);
        Redis.shard.set(scoreKey,newAvg );
        Redis.shard.expire(scoreKey,day*24*3600);
        Redis.shard.set(commitTimeKey, String.valueOf(nowLong) ); //最后一次被评价的时间
        Redis.shard.expire(commitTimeKey, day*24*3600 );
    }

    public void updateQuesScore(float source, User user, long size) throws IOException {
        float score = user.getQscore();
//        if (user.getQnum() > 0)
        score += ((source - score) / size);//计算总分
        user.setQscore(score);
        //更新redis
        Redis.shard.hset(CircleMD5.encodeSha1(user.getMobile()), UserStruct.qscore, String.valueOf(user.getQscore()));
        //更新Hbase
        Put put = new Put(Bytes.toBytes(user.uid()));
        put.addColumn(UserStruct.family_info, UserStruct.qscore_byte, PFloat.INSTANCE.toBytes(user.getQscore()));
        CHbase.bean().put(UserStruct.table, put);
    }

    private void updateAnswerScore(float deep, float att, float speed, User user, long size) throws IOException {
        float score = user.getAscore();
        float _deep = user.getDeep();
        float _att = user.getAtt();
        float _speed = user.getSpeed();
        float _score = (speed + att + deep) / 3;
        if (size > 0) {
            _att += (att - _att) / size;//计算问题解决态度平均分
            _deep += (deep - _deep) / size;//计算问题解决程度平均分
            _speed += (speed - _speed) / size;//计算问题解决速度平均分
            score += (_score - score) / size;//计算总分平均分
        }
        user.setSpeed(_speed);
        user.setDeep(_deep);
        user.setAtt(_att);
        user.setAscore(score);
        //更新redis
        Map<String, String> hash = new HashMap<>(3);
        hash.put(UserStruct.deep, String.valueOf(user.getDeep()));
        hash.put(UserStruct.att, String.valueOf(user.getAtt()));
        hash.put(UserStruct.speed, String.valueOf(user.getSpeed()));
        hash.put(UserStruct.ascore, String.valueOf(user.getAscore()));
        Redis.shard.hmset(CircleMD5.encodeSha1(user.getMobile()), hash);
        //更新Hbase
        Put put = new Put(Bytes.toBytes(user.uid()));
        put.addColumn(UserStruct.family_info, UserStruct.speed_byte, PFloat.INSTANCE.toBytes(user.getSpeed()));
        put.addColumn(UserStruct.family_info, UserStruct.deep_byte, PFloat.INSTANCE.toBytes(user.getDeep()));
        put.addColumn(UserStruct.family_info, UserStruct.att_byte, PFloat.INSTANCE.toBytes(user.getAtt()));
        put.addColumn(UserStruct.family_info, UserStruct.ascore_byte, PFloat.INSTANCE.toBytes(user.getAscore()));
        CHbase.bean().put(UserStruct.table, put);
    }

    /**
     * 可以取消订单,告诉发问者可以取消订单
     */
    public void sendQueCancel(Question question) {
        User quser = findUserByID(question.getQuid());
        if (quser == null) {
            logger.error("[Can't find user by uid] UID=" + question.getQuid());
            return;
        }
        Msg msg = new Msg();
        msg.setType(Msg.type_cancael);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());//
        map.put(QuestionStruct.status, QuestionStruct.stu_do_cancel);
        msg.setStatus(QuestionStruct.stu_do_cancel);
        msg.setList(map);
        singleTranport(quser.getCid(), Json.json(msg));//我的提问
    }

    /**
     * 提醒用户申请延时了
     */
    public void sendQueRemandDelay(Question question) {
        if (question.getStatus() > QuestionStruct.stu_answer) return;
        User quser = findUserByID(question.getQuid());
        //User auser = findUserByID(question.getAuid());
        Msg msg = new Msg();
        msg.setType(Msg.type_delay_remind);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());
        QueTimeAxis axis = new QueTimeAxis();
        axis.create(question.getConf());
        map.put(JsonParams.time, axis.getRemain());
        msg.setStatus(QuestionStruct.stu_delay_remind);
        Redis.shard.setex(RedisTable.QDEL + question.qid(), axis.getRemain() * 60, String.valueOf(1));
//        map.put(QuestionStruct.status, QuestionStruct.stu_delay_remind);
        msg.setList(map);
        //singleTranport(auser.getCid(), Json.json(msg));//我的回答
        singleTranport(quser.getCid(), Json.json(msg));//我的提问
        //添加延时提醒 系统消息  ~
        addSystemDelayMessage(quser, question);
        //addSystemDelayMessage(auser, question);
    }

    private void updateStatus(Question que) {
        String value = que.getStatus() + JsonParams.SPLIT + System.currentTimeMillis();
        Redis.shard.set(que.qid(), value);
    }

    public void addSystemDelayMessage(User user, Question question) {
        //您的问题"",可以申请延时.
//        SysMessage message = new SysMessage();
//        message.setTitle(SysMessage.TASK_MSG);//任务消息
//        message.setContext("您的问题\"" + question.getTitle() + "\",可以申请延时.");
//        //系统消息的LOGO
//        message.setLogo(SysMessage.SYS_LOG);
//        message.setType(SysMessage.type_task);
//        message.setAbout(SysMessage.about_question);
//        message.setQid(question.qid());
        //this.addSystemMessage(user, message);
//        addSystemMessage(user, message, AndroidMessage.cmd_que_remind_delay, false, 0);
        Map<String, Object> hash = new HashMap<>();
        hash.put(AndroidMessage.att_cmd, AndroidMessage.cmd_que_remind_delay);
        hash.put(AndroidMessage.att_data, question.qid());
//        hash.put(QuestionStruct.title, question.qid());
        Long size = Redis.shard.llen(RedisTable.SMSG + user.uid());
        this.sendTuiSong_new(user, SysMessage.que_can_delay, size.intValue(), hash, 0);
    }

    /**
     * @param user      user
     * @param message   message
     * @param cmd       command of tongzhi
     * @param istuisong use this system message pubsub to user phone or not
     * @param hassound  默认 0 有声音 1 静音
     */
    public void addSystemMessage(User user, SysMessage message, String cmd, boolean istuisong, int hassound) {
        message.setTime(System.currentTimeMillis());
        Long size = Redis.shard.lpush(RedisTable.SMSG + user.uid(), message.create());
        sendSysMessageNumber(user, size.intValue(), message.getType());
        Map<String, Object> hash = new HashMap<>();
        hash.put(AndroidMessage.att_cmd, cmd);
        hash.put(AndroidMessage.att_title, message.getTitle());
        hash.put(AndroidMessage.att_context, message.getContext());
        if (message.getType() == SysMessage.type_task) {
            hash.put(AndroidMessage.att_data, message.getQid() == null ? "" : message.getQid());
        } else if (message.getAbout() == SysMessage.about_url) {
            hash.put(AndroidMessage.att_data, message.getUrl() == null ? "" : message.getUrl());
        } else {
            hash.put(AndroidMessage.att_data, message.getQid() == null ? "" : message.getQid());
        }
        if (istuisong) {
            this.sendTuiSong_new(user, message.getContext(), size.intValue(), hash, hassound);
        }
    }


    public User findUserByID(String uid) {
        if (StringUtils.isEmpty(uid)) {
            logger.error("[the Param uid is null] -" + uid);
            return null;
        }
        String mobilSha1 = Redis.shard.get(RedisTable.USER_UID + uid);
        Map<String, String> hash;
        if (!StringUtils.isEmpty(mobilSha1)) {
            hash = Redis.shard.hgetAll(mobilSha1);
            if (hash == null || hash.isEmpty()) {
                return findUserFromHbase(uid);
            }
            return UserStruct.create(hash);
        }
        return findUserFromHbase(uid);
    }

    /**
     * 只包含id，cid，device，system字段
     * @param uid
     * @return
     */
    public User findUserByIdFromElk(String uid){
        if (StringUtils.isEmpty(uid)) {
            logger.error("[the Param uid is null] -" + uid);
            return null;
        }
        GetResponse questionResponse = CElastic.elastic().get(UserStruct.table_EL, uid);
        Object cid = questionResponse.getSource().get("cid");
        Object device = questionResponse.getSource().get("device");
        Object system = questionResponse.getSource().get("system");
        User user = new User();
        user.uid(uid);
        user.setCid(String.valueOf(cid));
        user.setDevice(String.valueOf(device));
        user.setSystem(String.valueOf(system));
        return user;
    }

    private User findUserFromHbase(String uid) {
        try {
            User user = new User();
            user.create(CHbase.bean().get(UserStruct.table, new Get(Bytes.toBytes(uid))));
            user.uid(uid);
            Redis.shard.set(RedisTable.USER_UID + uid, CircleMD5.encodeSha1(user.getMobile()));
            return user;
        } catch (IOException e) {
            logger.error("[find User by Id error] " + e.getMessage(), e);
            return null;
        }
    }

    public void sendRobPubTtanMsg(Set<String> cids, Question question, User user, String words, User quer, String json) {
        //========================================
        Msg msg = new Msg();
        msg.setType(Msg.type_quedetail);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.status, QuestionStruct.stu_rob);//rob status -
        map.put(JsonParams.QUESTION.qid, question.qid());//问题ID
        map.put(JsonParams.QUESTION.robnum, question.getRobnum());//问题ID
        /************************************************************************
         *  Modified by chenxx  2015年10月9日16:01:18 ======
         *  添加 抢答上后理筛选结束还剩下多久
         *  先判断状态-
         *  1. 如果是筛选阶段,读取进入筛选阶段时间(缓存的里面)
         *  2. 如果是抢答阶段
         *      当前时间-(创建时间+读题时间+抢答时间+筛选时间)
         *************************************************************************/
        int time = quesRobTime(question);
        map.put(QuestionStruct.time, time < 0 ? 0 : time);
        //Add rob user
        JsonNode node = Json.jsonParser(json);
        map.put(UserStruct.usr, node);
        //状态
        msg.setList(map);
        msg.setStatus(QuestionStruct.stu_add_rob);
        //========================================
        //发送透传
        listTranport(cids, msg);
        msg.setType(Msg.type_myque);
        singleTranport(quer.getCid(), Json.json(msg));
        //answer have system message
        SysMessage message = new SysMessage();
        message.setTitle(SysMessage.SYSMSG);//系统消息
        /**答题人，题目报名	*/
        message.setContext(SysMessage.ans_ready_rob);
        message.setLogo(SysMessage.SYS_LOG);
        message.setType(SysMessage.type_task);
        message.setAbout(Integer.parseInt(AndroidMessage.cmd_que_13));
        message.setQid(question.qid());
        this.addSystemMessage(user, message, AndroidMessage.cmd_que_13, true, 0);
        //Quester have 推送
        if (question.getRobnum() == 1) {
            SysMessage qmessage = new SysMessage();
            qmessage.setTitle(SysMessage.SYSMSG);//系统消息
            /**发题人，发布问题有新抢答者*/
            qmessage.setContext(SysMessage.que_have_rober);
            qmessage.setLogo(SysMessage.SYS_LOG);
            qmessage.setType(SysMessage.type_task);
            qmessage.setAbout(Integer.parseInt(AndroidMessage.cmd_que_14));
            qmessage.setQid(question.qid());
            this.addSystemMessage(quer, qmessage, AndroidMessage.cmd_que_14, true, 0);
        }
    }

    private int quesRobTime(Question question) {
        QueTimeAxis axis = new QueTimeAxis();
        axis.create(question.getConf());
        return (int) (((question.getCdate() + (axis.getRead() + axis.getRobCount() + axis.getScreen() * 60) * 1000) - System.currentTimeMillis()) / 1000);
    }

    public void backMoney(Question que, User user) throws IOException {
        UserPacket packet = findUserPacket(user.uid());
        cancelOrder(que, packet);
        //记录将钱钱给提问人
        Put qput = new Put(Bytes.toBytes(que.qid()));
        qput.addColumn(BaseLog.family, QuestionStruct.lock_byte, PInteger.INSTANCE.toBytes(2));
        CHbase.bean().put(Question.table, qput);
        //扣除答题方,即将入账金额
        UserPacket ap = findUserPacket(que.getAuid());
        if (que.getIspay() == 0) {//扣除即将入账 , ispay == 0 还没有进入余额, 1 进入余额
            ap.setPrecash(ap.getPrecash().subtract(que.getCash()));
        } else {//扣除余额
            ap.setCash(ap.getCash().subtract(que.getCash()));
        }
        Stream stream = new Stream();
        long date = System.currentTimeMillis();
        stream.id(que.getAuid() + date);
        stream.setUid(que.getAuid());
        //违规收入为该题目 赏金
        stream.setMoney(que.getCash());//题目 设置为违规收入
        stream.setPay(que.getCash());
        stream.setDisid(que.getDisid());
        stream.setStype(Stream.TY_5_goincome);//违规收入
        stream.setPtype(Stream.PY_1_SYS);//系统转账
        stream.setSdate(date);
        stream.setVdate(date);
        stream.setFdate(date);
        stream.setDescs("提问 : " + que.getTitle());
        stream.setStatus(Stream.STATUS_SUCCESS);
        stream.setQid(que.qid());
        Put sput = stream.createPut(stream.id(), stream.getSdate());
        CHbase.bean().put(Stream.table, sput);//添加流水
        Redis.shard.lpush(RedisTable.STREAM_ALL + stream.getUid(), stream.id());
        Redis.shard.lpush(RedisTable.STREAM_GOINCOME + stream.getUid(), stream.id());
        //存储钱包信息
        Put put = ap.createPut(ap.uid());
        CHbase.bean().put(UserPacket.table, put);
    }

    public void sendQueCancelQues(Question question, int is_app, int type) {
        User quser = findUserByID(question.getQuid());
        Msg msg = new Msg();
        msg.setType(Msg.type_myque);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.QUESTION.qid, question.qid());
        map.put(QuestionStruct.status, QuestionStruct.stu_cancel);
        msg.setStatus(QuestionStruct.stu_cancel);
        //状态
        msg.setList(map);
        if (is_app == 1) {//自动取消订单
            //进入答题阶段，后就不能自动取消订单了
            if (question.getStatus() >= QuestionStruct.stu_answer) {
                logger.warn("Ready finish question=" + question.qid() + ",is_app=" + is_app + ",type=" + type);
                return;
            }
            UserPacket packet = findUserPacket(question.getQuid());
            try {
                //退款
                cancelOrder(question, packet);
                //通知,用户
                if (question.getRobnum() <= 0) {
                    updateQuestionStatus(question, QuestionStruct.stu_cancel, "|题目已取消|说明：未选择任何报名者，题目自动取消");
                } else {
                    updateQuestionStatus(question, QuestionStruct.stu_cancel, "|题目已取消|说明：没有进行筛选答题者，题目自动取消");
                }
                //修改问题状态
                //告诉用户取消订单
                singleTranport(quser.getCid(), Json.json(msg));
                if (question.getRobnum() > 0) {//没有筛选
                    Long time = Redis.shard.incr(RedisTable.CACEL + question.getQuid());
                    if (time == 1) {// 2015年10月30日17:21:58 bug 1520
                        Redis.shard.expire(RedisTable.CACEL + question.getQuid(), (int) TimeUtil.timeEndToToday());
                    }
                    this.addSystemNoChoseMessage(question, quser);
                } else {//无人抢答
                    if (question.getReissue() > 0) {
                        Redis.shard.decr(RedisTable.CACEL + question.getQuid());
                    }
                    this.addSystemNoRobMessage(question, quser);
                }
            } catch (IOException e) {
                logger.error("cancel question error qid=" + question.qid(), e);
            }
        }

        //将答题人解锁
        if (StringUtils.isNotEmpty(question.getAuid())) {
            Redis.shard.del(RedisTable.TI + question.getAuid());
        }
        //剔除抢答者
        String key = RedisTable.QPL + question.qid();
        Set<String> cids = Redis.shard.smembers(key);
        rejectOtherAnswer(question, null, 0);
        //解锁答题人
        if (StringUtils.isNotEmpty(question.getAuid())) {
            Redis.shard.del(RedisTable.TI + question.getAuid());
        }
        cids.remove(quser.getCid());//排除提问人
        //透传 - 取消问题
        msg.setType(Msg.type_quedetail);
        listTranport(cids, msg);
        //删除 , 抢答列表
        Redis.shard.del(key);
        //回答者推送
        if (StringUtils.isNotEmpty(question.getAuid())) {
            /**答题人，题目对方已经取消*/
            User auser = findUserByID(question.getAuid());
            msg.setType(Msg.type_myans);
            singleTranport(auser.getCid(), Json.json(msg));
            SysMessage message = new SysMessage();
            message.setTitle(SysMessage.TASK_MSG);//任务消息
            message.setContext(SysMessage.ans_que_cencal);
            //系统消息的LOGO
            message.setLogo(SysMessage.SYS_LOG);
            message.setType(SysMessage.type_task);
            message.setAbout(Integer.parseInt(AndroidMessage.cmd_que_7));
            message.setQid(question.qid());
            this.addSystemMessage(auser, message, AndroidMessage.cmd_que_7, true, 0);
//            AddSystemMessage thread = new AddSystemMessage(auser, message, String.valueOf(message.getAbout()), true,0,this);
//            GROUP.executor.execute(thread);
        }
    }

    private void addSystemNoChoseMessage(Question question, User user) {
        //您的问题"",可以申请延时.未选择答题人.已自动取消问题
        SysMessage message = new SysMessage();
        message.setTitle(SysMessage.TASK_MSG);//任务消息
        /**发题人，题目没有筛选*/
        message.setContext(SysMessage.que_no_chose);
        //系统消息的LOGO
        message.setLogo(SysMessage.SYS_LOG);
        message.setType(SysMessage.type_task);
        message.setAbout(Integer.parseInt(AndroidMessage.cmd_que_1));
        message.setQid(question.qid());
        this.addSystemMessage(user, message, AndroidMessage.cmd_que_1, true, 0);
//        AddSystemMessage thread = new AddSystemMessage(user, message, String.valueOf(message.getAbout()), true,0,this);
//        GROUP.executor.execute(thread);
    }

    public void addCouponSysMessage(Coupon coupon, User user, boolean istuisong, String context) {
        //您的问题"",赶快去提问吧!
        SysMessage message = new SysMessage();
        message.setTitle(SysMessage.SYSMSG);//系统消息
        if (StringUtils.isNotEmpty(context)) {
            message.setContext(context);
        } else {
            message.setContext("您获得\"" + coupon.getDiscount().intValue() + "元优惠券\",赶快去提问吧!");
        }
        //系统消息的LOGO
        message.setLogo(SysMessage.SYS_LOG);
        message.setType(SysMessage.type_sys);
        message.setAbout(Integer.parseInt(AndroidMessage.cmd_red));
        message.setQid("");
        this.addSystemMessage(user, message, AndroidMessage.cmd_red, istuisong, 0);
//        AddSystemMessage thread = new AddSystemMessage(user, message, String.valueOf(message.getAbout()), istuisong,0,this);
//        GROUP.executor.execute(thread);
    }

    public void addCashSysMessage(User user, boolean istuisong, String context, int cmd) {
        //您的问题"",赶快去提问吧!
        SysMessage message = new SysMessage();
        message.setTitle(SysMessage.SYSMSG);//系统消息
        message.setContext(context);
        //系统消息的LOGO
        message.setLogo(SysMessage.SYS_LOG);
        message.setType(SysMessage.type_sys);//系统消息
        message.setAbout(cmd);
        message.setQid("");
        this.addSystemMessage(user, message, String.valueOf(cmd), istuisong, 0);
    }

    private void addSystemNoRobMessage(Question question, User user) {
        //您的问题"",可以申请延时.
        SysMessage message = new SysMessage();
        message.setTitle(SysMessage.TASK_MSG);//任务消息
        /**发题人，题目无人抢答*/
        message.setContext(SysMessage.que_no_rober);
        //系统消息的LOGO
        message.setLogo(SysMessage.SYS_LOG);
        message.setType(SysMessage.type_task);
        message.setAbout(Integer.parseInt(AndroidMessage.cmd_que_0));
        message.setQid(question.qid());
        this.addSystemMessage(user, message, AndroidMessage.cmd_que_0, true, 0);
//        AddSystemMessage thread = new AddSystemMessage(user, message, String.valueOf(message.getAbout()), true,0,this);
//        GROUP.executor.execute(thread);
    }

    public UserPacket findUserPacket(String uid) {
        try {
            Result result = CHbase.bean().get(UserPacket.table, new Get(Bytes.toBytes(uid)));
            UserPacket packet = new UserPacket();
            packet.create(result);
            packet.uid(uid);
            if (packet.getCash() == null) {
                packet.setCash(new BigDecimal(0));
            }
            if (packet.getPrecash() == null) {
                packet.setPrecash(new BigDecimal(0));
            }
            if (packet.getTotle() == null) {
                packet.setTotle(new BigDecimal(0));
            }
            if (packet.getGet() == null) {
                packet.setGet(new BigDecimal(0));
            }
            return packet;
        } catch (IOException e) {
            logger.error("find user packet error uid=" + uid, e);
            return new UserPacket();
        }
    }

    private boolean cancelOrder(Question que, UserPacket packet) throws IOException {
        Stream stream = new Stream();
        long date = System.currentTimeMillis();
        stream.id(que.getQuid() + date);
        stream.setUid(que.getQuid());
        stream.setMoney(que.getCash());//题目退款 -
        stream.setPay(que.getDiscount() != null ? que.getCash().subtract(que.getDiscount()) : que.getCash());//计算实际消费,实际退款 , 减去优惠券
        stream.setDisid(que.getDisid());
        stream.setStype(Stream.TY_4_back);//退款
        stream.setPtype(Stream.PY_1_SYS);//系统转账
        stream.setSdate(date);
        stream.setVdate(date);
        stream.setFdate(date);
        stream.setDescs("提问 : " + que.getTitle());
        stream.setStatus(Stream.STATUS_SUCCESS);
        stream.setQid(que.qid());
        Put put = stream.createPut(stream.id(), stream.getSdate());
        CHbase.bean().put(Stream.table, put);//添加流水
        Redis.shard.lpush(RedisTable.STREAM_ALL + stream.getUid(), stream.id());
        Redis.shard.lpush(RedisTable.STREAM_BACK + stream.getUid(), stream.id());
        packet.setCash(packet.getCash().add(stream.getPay()));
        Put pput = packet.createPut(packet.uid());
        CHbase.bean().put(UserPacket.table, pput);//更新钱包数量
        //优惠券修改状态- 将之前使用过的优惠券修改回来
        if (StringUtils.isNotEmpty(que.getDisid())) {
            //修改优惠券信息
            Coupon coupon = findCouponById(que.getDisid());
            if (coupon != null) {
                coupon.setStatus(Coupon.status_nouse);
                coupon.setUtime(que.getCdate());
                CHbase.bean().put(Coupon.table, coupon.createPut(que.getDisid()));
                Redis.shard.hset(RedisTable.CPNH + que.getQuid(), coupon.disid(), coupon.createValue());
//                Redis.shard.decr(RedisTable.COUPON_U+coupon.getParent());
            }
        }
        return true;
    }

    private Coupon findCouponById(String disid) throws IOException {
        Result result = CHbase.bean().get(Coupon.table, new Get(Bytes.toBytes(disid)));
        if (result != null && !result.isEmpty()) {
            Coupon coupon = new Coupon();
            coupon.create(result);
            coupon.disid(disid);
            return coupon;
        }
        return null;
    }


    public void sendBoardCall(String cid, String uid, int i) {
        Msg msg = new Msg();
        msg.setType(Msg.type_board);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.cmd, i);//捎句话
        map.put(UserStruct.uid, uid);//问题ID
        map.put(JsonParams.context, "");
        //状态
        msg.setList(map);
        singleTranport(cid, Json.json(msg));
    }

    public void sendBoardAnswer(String cid, String uid, int i) {
        Msg msg = new Msg();
        msg.setType(Msg.type_board);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.cmd, i);//cmd
        map.put(UserStruct.uid, uid);//问题ID
        map.put(JsonParams.context, "");
        //状态
        msg.setList(map);
        singleTranport(cid, Json.json(msg));
    }

    public void sendBoardHangup(String cid, String uid, int i) {
        Msg msg = new Msg();
        msg.setType(Msg.type_board);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.cmd, i);//捎句话
        map.put(UserStruct.uid, uid);//问题ID
        map.put(JsonParams.context, "");
        //状态
        msg.setList(map);
        singleTranport(cid, Json.json(msg));
    }

    public void sendBoardSend(String cid, String uid, int i, String data) {
        Msg msg = new Msg();
        msg.setType(Msg.type_board);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.cmd, i);//捎句话
        map.put(UserStruct.uid, uid);//问题ID
        map.put(JsonParams.context, data);
        //状态
        msg.setList(map);
        singleTranport(cid, Json.json(msg));
    }

    public void sendDataByCid(String cid, String type, JsonNode json) {
        Msg msg = new Msg();
        msg.setType(type);
        msg.setList(json);
        singleTranport(cid, Json.json(msg));
    }

    /**
     * 给单个用户发送推送
     *
     * @param user    用户信息
     * @param context 显示内容
     * @param type    cmd 类型
     * @param data    ID 或其他信息
     */
    public void pushByCid(User user, String context, String type, String data) {
        Map<String, Object> kvs = new HashMap<>();
        kvs.put(AndroidMessage.att_cmd, type);
        kvs.put(AndroidMessage.att_data, data == null ? "" : data);
        sendTuiSong_new(user, context, 0, kvs, 0);
    }

    public void transportationApp(String tag, String phone, String province, String type, JsonNode jsonNode) {
        Msg msg = new Msg();
        msg.setType(type);
        msg.setList(jsonNode);
        transportationToApp(tag, phone, province, Json.json(msg));
    }

    /**
     * @param title    1
     * @param context  1
     * @param type     1
     * @param jsonNode 1
     * @param sys      1
     * @param tag      1
     * @param phone    1
     * @param province 1
     * @deprecated see pushByApp
     */
    @Deprecated
    public void pushByApp(String title, String context, String type, JsonNode jsonNode, String sys, String tag, String phone, String province) {
        if (sys.toLowerCase().equalsIgnoreCase("ios")) {
            //TODO 苹果APP 全部推送
        } else {
            Msg msg = new Msg();
            msg.setType(type);
            msg.setList(jsonNode);
            pushToApp(title, context, tag, phone, province, Json.json(msg));
        }
    }

    public void pushByApp(String context, String cmd, String sys, String data) {
        Map<String, Object> kvs = new HashMap<>();
        kvs.put(AndroidMessage.att_data, data);
        kvs.put(AndroidMessage.att_cmd, cmd);
        if (sys.toLowerCase().equalsIgnoreCase("ios")) {
            //TODO 苹果APP 全部推送
        } else {
            kvs.put(AndroidMessage.att_title, "问啊");
            kvs.put(AndroidMessage.att_context, context);
            transportationToApp(null, "ANDROID", null, Json.json(kvs));
        }
    }

    public void sendAlipaySttatus(User user, String qid, int status) {
        Msg msg = new Msg();
        msg.setType(Msg.type_issue);
        msg.setStatus(0);
        HashMap<String, java.io.Serializable> map = new HashMap<String, java.io.Serializable>();
        map.put(JsonParams.QUESTION.qid, qid);
        map.put(JsonParams.QUESTION.status, status);
        msg.setList(map);
        singleTranport(user.getCid(), Json.json(msg));
    }

    public void sendSysMessageNumber(User user, int size, int type) {
        Msg msg = new Msg();
        msg.setType(Msg.type_sysmsg);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.num, size);
        msg.setList(map);
        msg.setMtype(type);
        this.singleTranport(user.getCid(), Json.json(msg));

    }

    public void sendSysMessageNumber(String cid, int size) {
        Msg msg = new Msg();
        msg.setType(Msg.type_sysmsg);
        Map<String, Object> map = new HashMap<>();
        map.put(JsonParams.num, size);
        msg.setList(map);
        this.singleTranport(cid, Json.json(msg));
    }

    public void dealSelectRobUser(Question question) throws IOException {
        //仅仅 抢答阶段的题目可进入此
        if (question.getStatus() != QuestionStruct.stu_rob) {
            logger.warn("dealSelectRobUser not in QuestionStruct.stu_rob");
            return;
        }
        List<String> set = Redis.shard.srandmember(RedisTable.ROBL + question.qid(), question.getRobtot());
        User quser = findUserByID(question.getQuid());
        if (set == null || set.isEmpty()) return;
        Map<String, Integer> pck = findQuestionPackage(question.qid());
        QueTimeAxis axis = new QueTimeAxis();
        axis.create(question.getConf());
        Set<String> cids = Redis.shard.smembers(RedisTable.QPL + question.qid());
        for (String uid : set) {
            User user = findUserByID(uid);
            if (user == null) continue;
            String pck_number = getPackNumber(question.qid(), pck);
            //抢答已满,没有取到抢答包,进入筛选阶段
            Long number = Redis.shard.decr(question.qid() + pck_number);
            //如果值减小到小于0 则删除这个包
            if (number >= 0) {//表示抢答成功
                Long totle = Redis.shard.incr(RedisTable.QRINCR + question.qid());
                if (totle == 1) Redis.shard.expire(RedisTable.QRINCR + question.qid(), axis.getRobCount());
                //Redis.shard.srem(RedisTable.QRP+qid,pck_number);
                //写进抢答者列表中
                String msg = Redis.shard.hget(RedisTable.ROBH + question.qid(), uid);
                Redis.shard.hset(RedisTable.A + question.qid(), uid, msg);
                //计算锁定时间
                long cdate_rob_time = 2000 + question.getCdate() + (axis.getRead() + axis.getRobCount() + axis.getScreen() * 60L) * 1000L;
                Long lockTime = (cdate_rob_time - System.currentTimeMillis()) / 1000;
                //锁定该用户
                Redis.shard.setex(RedisTable.TI + uid, Math.abs(lockTime.intValue()), question.qid());
                //更新我的回答
                updateMyAnsQuestion(question);
                //添加到抢答者列族中
                Put put = new Put(Bytes.toBytes(question.qid()));
                String value = TimeUtil.timeFormat.format(System.currentTimeMillis()) +
                        ":" + user.getName() + "/" +
                        user.uid() + "/" + user.getMobile() + "/" + msg;
                //抢答人数 +1
                question.setRobnum(question.getRobnum() + 1);
                put.addColumn(QuestionStruct.family_rob, Bytes.toBytes(user.uid()), Bytes.toBytes(value));
                put.addColumn(QuestionStruct.family, QuestionStruct.robnum_byte, PInteger.INSTANCE.toBytes(totle.intValue()));
                CHbase.bean().put(Question.table, put);
                //Step : 通知刷到题目的人,有抢答
                Map<String, Object> usr = UserJson.create().simple_rob_user_info(user).getMap();
                usr.put(JsonParams.msg, msg);
                sendRobPubTtanMsg(cids, question, user, msg, quser, Json.json(usr));
                if (totle >= question.getRobtot()) {//抢答者已满自动进入筛选阶段
                    logger.info("in chose status quesiton qid=" + question.qid());
                    sendQueInChoseStep(question, 1);
//                    Redis.CONNECT.publish(question.qid(), String.valueOf(QuestionStruct.stu_chose));
                    return;
                }
            }
        }
    }

    private void updateMyAnsQuestion(Question que) {
        QueTimeAxis axis = new QueTimeAxis();
        axis.create(que.getConf());
        Redis.shard.setex(RedisTable.MYANS + que.getAuid(), axis.getCont() - axis.getRead(), que.qid());
    }

    public void setQuestionStatus(Question question, int status) throws IOException {
        Put put = new Put(Bytes.toBytes(question.qid()));
        put.addColumn(QuestionStruct.family, QuestionStruct.status_byte, PInteger.INSTANCE.toBytes(status));
        put.addColumn(QuestionStruct.family_time, Bytes.toBytes(String.valueOf(System.currentTimeMillis())),
                Bytes.toBytes("1|进入筛选阶段|抢答已满"));
        CHbase.bean().put(Question.table, put);
        Map<String, String> map = new HashMap<>();
        question.setStatus(status);
        map.put(QuestionStruct.status, String.valueOf(status));
        CElastic.elastic().udpate(QuestionStruct.table, question.qid(), map);
        if (status != QuestionStruct.stu_delay) {
            updateStatus(question);
        }
        sendQueInChoseStep(question, 1);
    }

    private void deleteCacheAndRedis(Question que) {
        //删除Redis缓存
        Redis.shard.srem(RedisTable.CLASSIFY + que.getType(), que.qid());//在该分类添加问题
        //删除抢答包
        String psetkey = RedisTable.QRP + que.qid();
        Redis.shard.del(psetkey);//添加包
    }

    private String getPackNumber(String qid, Map<String, Integer> pck) {
        String pck_number = null;
        for (String key : pck.keySet()) {
            Integer value = pck.get(key);
            if (value == null || value == 0) continue;
            pck_number = key;
            break;
        }
        return pck_number;
    }

    //在缓存中查询抢答包
    public Map<String, Integer> findQuestionPackage(String qid) {
        Map<String, Integer> integerMap = new HashMap<>();
        Set<String> keys = Redis.shard.smembers(RedisTable.QRP + qid);
        for (String key : keys) {
            String[] num = key.split(JsonParams.SPLIT_BACK);
            integerMap.put(key, Integer.valueOf(num[1]));
        }
        return integerMap;
    }

    public void backMoneyToAuser(Question question, User user) throws IOException {
        //将金额给答题人,只需问题中的, lock 状态修改为0即可,
        Put put = new Put(Bytes.toBytes(question.qid()));
        put.addColumn(BaseLog.family, QuestionStruct.lock_byte, PInteger.INSTANCE.toBytes(0));
        CHbase.bean().put(Question.table, put);
        Map<String, Object> hash = new HashMap<>();
        hash.put(QuestionStruct.lock, 0);
        CElastic.elastic().update(QuestionStruct.table_el, question.qid(), hash);
    }
}
