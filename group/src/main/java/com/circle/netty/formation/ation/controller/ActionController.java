package com.circle.netty.formation.ation.controller;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.Redis;
import com.circle.core.util.CircleMD5;
import com.circle.core.util.Verification;
import com.circle.netty.formation.ation.service.CacheManager;
import com.circle.netty.formation.message.model.*;
import com.circle.netty.formation.message.model.struct.QuestionStruct;
import com.circle.netty.formation.message.model.struct.UserStruct;
import com.circle.netty.formation.message.service.MessageService;
import com.circle.netty.formation.util.AppConfig;
import com.circle.netty.formation.util.RedisTable;
import com.circle.netty.http.BaseControl;
import com.circle.netty.http.ErrorCode;
import com.circle.netty.http.HttpBack;
import com.circle.netty.http.JsonParams;
import io.netty.handler.codec.http.FullHttpResponse;
import kafka.producer.KeyedMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.schema.types.PInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * lib/kafka_2.8.0-0.8.0.jar lib/scala-compiler.jar lib/scala-library.jar
 *
 * @author Created by cxx on 15-8-7.
 */
@SuppressWarnings("unused")
@Controller("/action")
@Scope("prototype")
public class ActionController extends BaseControl {
    @Autowired
    private MessageService messageService;
    private Logger logger = LoggerFactory.getLogger(ActionController.class);

    /**
     * 用户编队服务 -
     * int uid;
     * int sex;
     * String city;
     * Long cdate;
     * String cha;
     * String sys;
     */
    public FullHttpResponse user() {
        String uid = strings(UserStruct.uid);
        if (uid == null) return HttpBack.back_error(ctx, ErrorCode.UID_NULL);
        User user = findUserByID(uid);
        if (user == null) return HttpBack.back_error(ctx, ErrorCode.USER_NOT_EXISTS);
        Set<String> groups = CacheManager.findGroups();
        List<KeyedMessage<String, String>> messages = new ArrayList<>();
        if (groups != null && !groups.isEmpty()) {//组列表不为空-则运行处理消息逻辑
            for (String group : groups) {
                if (group != null) {//如果组组名称不为空-加入到消息列表中
                    messages.add(new KeyedMessage<>(group, uid, uid));
                }
            }
        } else {
            logger.warn(req.uri() + " , no groups");
        }
        String on_register = strings(JsonParams.on_register);
        if (StringUtils.isNotEmpty(on_register)) {
            String topic = this.random_def_topic();
            String message = this.simple_user_info(user);
            if (StringUtils.isNotEmpty(topic))
                messages.add(new KeyedMessage<String, String>(topic, message));
            try {
                SysMessage sysmsg = new SysMessage();
                sysmsg.setTitle(SysMessage.SYSMSG);//系统消息
                sysmsg.setContext(SysMessage.first_sgin);//第一次登录
                sysmsg.setLogo(SysMessage.SYS_LOG);
                sysmsg.setType(SysMessage.type_sys);
                sysmsg.setAbout(SysMessage.about_sys);
                sysmsg.setQid("");
                messageService.addSystemMessage(user, sysmsg, String.valueOf(SysMessage.about_sys), false, 0);
            } catch (Exception e) {

            }
        }
        if (!messages.isEmpty()) {//如果消息内容不为空,则发送消息到kafka 队列中
            AppConfig.producerPool.send(messages);
        }
        //send coupon
        return HttpBack.back_error(ctx, ErrorCode.NO_GROUPS);
    }

    private String simple_user_info(User user) {
        User_min min = new User_min();
        min.setUid(user.uid());
        min.setCdate(user.getCdate());
        min.setCha(user.getChannel());
        min.setCity(user.getCity());
        min.setSex(user.getSex());
        min.setSys(user.getSystem());
        return Json.json(min);
    }

    private String random_def_topic() {
        return Redis.shard.srandmember(RedisTable.defcoupon);
    }

    private String random_pull_topic() {
        return Redis.shard.srandmember(RedisTable.pullcoupon);
    }

    public User findUserByID(String uid) {
        String mobilSha1 = Redis.shard.get(RedisTable.USER_UID + uid);
        if (StringUtils.isEmpty(mobilSha1)) {
            User user = new User();
            try {
                Result result = CHbase.bean().get(UserStruct.table, new Get(Bytes.toBytes(uid)));
                if (result.isEmpty()) return null;
                user.create(result);
                user.uid(uid);
                return user;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, String> hash = Redis.shard.hgetAll(mobilSha1);
        if (hash == null || hash.isEmpty()) return null;
        return UserStruct.create(hash);
    }

    public FullHttpResponse ping() {
        return HttpBack.back_200();
    }


    /**
     * 删除用户的一张优惠券
     * 参数说明;
     * disid : 及优惠券Id
     * uid : 用户Id
     * mobile : 用户手机号
     * 注意 : mobile 和 uid 只需上传其中一个即可
     *
     * @return 返回成功, 失败返回错误码
     */
    public FullHttpResponse coupon() {
        //用户ID
        String uid = strings(UserStruct.uid);
        //优惠券ID
        String disid = strings(Coupon.att_disid);
        if (StringUtils.isEmpty(disid)) {
            logger.warn("url=" + req.uri() + " ,Params=" + request.getBodyHttpDatas() + " ,ErrorCode=" + ErrorCode.DISID_NULL);
            return HttpBack.back_error(ErrorCode.DISID_NULL);
        }
        User user = null;
        if (StringUtils.isEmpty(uid))
            user = findUserByID(uid);
        String mobile = strings(UserStruct.mobile);
        if (user == null && StringUtils.isNotEmpty(mobile))
            user = findUserByMobile(mobile);
        if (user == null) {
            logger.warn("url=" + req.uri() + " ,Params=" + request.getBodyHttpDatas() + " ,ErrorCode=" + ErrorCode.USER_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.USER_NOT_EXISTS);
        }
        //从redis中删除
        Redis.shard.lrem(RedisTable.CPNL + uid, 0, disid);
        Redis.shard.hdel(RedisTable.CPNH + uid, disid);
        Put put = new Put(Bytes.toBytes(disid));
        put.addColumn(BaseLog.family, Bytes.toBytes(Coupon.att_status), PInteger.INSTANCE.toBytes(Coupon.status_abandon));
        try {
            CHbase.bean().put(Coupon.table, put);
        } catch (IOException e) {
            logger.error(req.uri() + ",ErrorMessage=" + e.getMessage());
            return HttpBack.back_error(e.getMessage());
        }
        return HttpBack.back_200();
    }

    /**
     * 参数 what ever
     * 地址 : /action/check
     * 如果服务正常 返回 200 状态
     * 如果服务不正常 返回 500 状态
     *
     * @return FullHttpResponse
     */
    public FullHttpResponse check() {
        try {
            //检测 redis key is any what ever
            Redis.shard.get(ErrorCode.DISID_NULL);
            Redis.CONNECT.get(ErrorCode.DISID_NULL);
            //检测 habse1
            if (CHbase.bean().connection().isClosed()) return HttpBack._500();
            //检测 elaskic
            CElastic.elastic().client.listedNodes();
        } catch (Exception e) {
            return HttpBack._500();
        }
        return HttpBack._200();
    }

    private User findUserByMobile(String mobile) {
        String mobilSha1 = CircleMD5.encodeSha1(mobile);
        Map<String, String> hash = Redis.shard.hgetAll(mobilSha1);
        if (hash == null || hash.isEmpty()) return null;
        return UserStruct.create(hash);
    }

    /**
     * number=13855555555,text=内容,passwd=123 写成这样
     */
    public FullHttpResponse sendmessage() {
        String number = strings("number");
        String text = strings("text");
        String passwd = strings("passwd");
        if (!passwd.equals(AppConfig.sms_passwd)) {
            return HttpBack.back_error("pwd");
        }
        List topics = Arrays.asList(AppConfig.sms_topics);
//        try {
//            topics = CacheManager.string_list.get(AppConfig.smstopics);
//        } catch (NeedsRefreshException e) {
//            try {
//                topics = GROUP.zooKeeper.getChildren(AppConfig.smstopics,true);
//            } catch (KeeperException | InterruptedException e1) {
//                logger.error(req.uri() + ",Params:" + request.getBodyHttpDatas() + "KeeperException | InterruptedException , errorMessage = " + e.getMessage());
//                return HttpBack.back_error(ctx,e1.getMessage());
//            }
//        }
        if (!topics.isEmpty()) {
            String topic = (String) topics.get((int) (System.currentTimeMillis() % topics.size()));
            if (StringUtils.isNotEmpty(topic)) {
//                AppConfig.producerPool.send(new KeyedMessage<>(topic, "constom", number + JsonParams.SPLIT + text+"【问啊】"));
                Redis.CONNECT.publish(topic, "constom" + JsonParams.SPLIT + number + JsonParams.SPLIT + text + "【问啊】");
            } else {
                logger.error(req.uri() + ",Params:" + request.getBodyHttpDatas() + "No SmsServer.......");
            }
        }
        return HttpBack.back_200();

    }

    /**
     * 新加接口 :
     * 给 用户加钱钱 :
     * 其他不说了 : 小意思
     */
    public FullHttpResponse addmoney() {
        //给用户加钱钱
        String cash_str = strings(QuestionStruct.cash);
        if (StringUtils.isEmpty(cash_str)) {
            logger.error(req.uri() + ",Params:" + request.getBodyHttpDatas() + "\tErrorMessage=" + ErrorCode.CASH_NULL);
            return HttpBack.back_error(ErrorCode.CASH_NULL);
        }
        BigDecimal cash = new BigDecimal(cash_str);
        String uid = strings(JsonParams.uid);
        User user = findUserByID(uid);
        if (user == null) {
            logger.error(req.uri() + ",Params:" + request.getBodyHttpDatas() + "\tuid=" + uid + "\tErrorMessage=" + ErrorCode.USER_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.USER_NOT_EXISTS);
        }
        String from_uid = strings(JsonParams.from);
        User from_user = findUserByID(from_uid);
        if (from_user == null) {
            logger.error(req.uri() + ",Params:" + request.getBodyHttpDatas() + "\tfrom_uid=" + from_uid + "\tErrorMessage=" + ErrorCode.FUSER_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.FUSER_NOT_EXISTS);
        }
        int money = cash.intValue();
        //获得拉人券 money 总数
        Redis.shard.hincrBy(RedisTable.TOTLECM + user.uid(), RedisTable.TOTLECM_COUPON, money);
        Long len = Redis.shard.lrem(RedisTable.LRL + user.uid(), 0, from_uid + JsonParams.SPLIT + 0);
        if (len != null && len > 0) {
            //如果是第一次 就加一个
            Redis.shard.lpush(RedisTable.LRL + user.uid(), from_uid + JsonParams.SPLIT + money + JsonParams.SPLIT + 1);
        } else {
            //160402修改 拉人返现金累加
            List<String> lrls = Redis.shard.lrange(RedisTable.LRL + user.uid(), 0, -1);
            boolean isSend = false;
            for (String lrl : lrls) {
                String[] arr = lrl.split(JsonParams.SPLIT_BACK);
                String fkid = arr[0];
                String fkm = arr[1];
                //如果以前拉过这个人
                if (fkid.equals(from_uid)) {
                    Long len0 = Redis.shard.lrem(RedisTable.LRL + user.uid(), 0, lrl);
                    money = money + Verification.getInt(0, fkm);
                    Redis.shard.lpush(RedisTable.LRL + user.uid(), from_uid + JsonParams.SPLIT + money + JsonParams.SPLIT + 1);
                    isSend = true;
                    break;
                }
            }
            if (!isSend) {
                Redis.shard.lpush(RedisTable.LRL + user.uid(), from_uid + JsonParams.SPLIT + money + JsonParams.SPLIT + 1);
            }
        }

        //是否发送系统消息
        int isend = integers(JsonParams.type, 0);
        //是否推送
        int istuisong = integers(JsonParams.code, 0);
        // 命令 - > 跳转命令 待定
        int cmd = integers(JsonParams.cmd, 0);
        String message = strings(JsonParams.msg);
        String disid = strings(JsonParams.disid);

        if (StringUtils.isEmpty(message)) {
            message = "恭喜您获得" + cash_str + "元现金奖励，您可以到我的钱包中查看余额";
        }
        //TODO 统计表数据        String json = strings(JsonParams.json);
        try {
            long date = System.currentTimeMillis();
            //存钱钱了  +  说给你发钱 就发钱
            //TODO 统计表 存表
            LCash lCash = new LCash();
            lCash.setParentId(disid);
            lCash.setCdate(date);
            lCash.setUid(uid);
            lCash.setFuid(from_uid);
            lCash.setCash(cash);
            lCash.setStatus(0);

            String lcashKey = JsonParams.LCASHQID + uid + JsonParams.SPLIT + from_uid;
            String lcashDateKey = JsonParams.LCASHQFDATE + uid + JsonParams.SPLIT + from_uid;
            lCash.setQid(Redis.shard.get(lcashKey));
            lCash.setQfdate(Verification.getLong(0, Redis.shard.get(lcashDateKey)));
            lCash.setVdate(0L);
            lCash.setVstatus(JsonParams.LCASH.normal);

            Put lput = lCash.createPut(uid + date, date);
            CHbase.bean().put(LCash.table, lput);
            //存钱到钱包
            //生成流水- 答题人的流水
            Stream stream = new Stream();
            stream.id(uid + date);
            stream.setUid(uid);
            stream.setMoney(cash);//收入-
            stream.setPay(cash);//实际-收入金额
            stream.setDisid(disid);
            stream.setStype(Stream.TY_1_profit);//收益
            stream.setPtype(Stream.PY_4_LAREN);//拉人 收益
            stream.setAccount(from_uid);
            stream.setSdate(date);
            stream.setVdate(date);
            stream.setFdate(date);
            stream.setDescs(message);
            stream.setStatus(0);
            stream.setQid("");
            Put sput = stream.createPut(stream.id(), stream.getSdate());
            //流水存Hbase
            CHbase.bean().put(Stream.table, sput);
            Redis.shard.lpush(RedisTable.STREAM_ALL + stream.getUid(), stream.id());
            Redis.shard.lpush(RedisTable.STREAM_PROFIT + stream.getUid(), stream.id());
            UserPacket packet = messageService.findUserPacket(uid);
            packet.setCash(packet.getCash().add(cash));
            packet.setTotle(packet.getTotle() == null ? cash : packet.getTotle().add(cash));
            Put pput = packet.createPut(packet.uid());
            //修改 提问数 - 修改回答数
            //存入钱包
            CHbase.bean().put(UserPacket.table, pput);//答题人,赏金入账
            if (isend == 0) {
                messageService.addCashSysMessage(user, istuisong == 0, message, cmd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HttpBack.back_200();
    }
}
