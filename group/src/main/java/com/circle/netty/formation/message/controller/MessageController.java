package com.circle.netty.formation.message.controller;

import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.Redis;
import com.circle.netty.formation.message.model.*;
import com.circle.netty.formation.message.model.struct.QuestionStruct;
import com.circle.netty.formation.message.model.struct.UserStruct;
import com.circle.netty.formation.message.service.MessageService;
import com.circle.netty.formation.util.RedisTable;
import com.circle.netty.formation.util.SensitiveWordInit;
import com.circle.netty.formation.util.SensitivewordFilter;
import com.circle.netty.http.BaseControl;
import com.circle.netty.http.ErrorCode;
import com.circle.netty.http.HttpBack;
import com.circle.netty.http.JsonParams;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.FullHttpResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Set;

/**
 * @author Created by cxx on 15-8-7.
 */
@Controller("/message")
@Scope("prototype")
public class MessageController extends BaseControl {
    @Autowired
    private MessageService messageService;
    private Logger logger = LoggerFactory.getLogger(MessageController.class);
    private static final String type_call = "1";
    private static final String type_answer = "2";
    private static final String type_hanup = "3";
    private static final String type_send = "4";

    /**
     * 给用户添加有优惠券
     * 接口参数说明 ：
     * disid ： 优惠券ID
     * json ： 优惠券内容 内容详情请看源码
     * from ： 拉人券 ， 来源人
     * isend ： 是否发送默认推送 （默认0 发送消息，其不发送）
     * cmd : 是否推送 0 （默认 推送） 其他值 不推送
     * context : 消息内容
     *
     * @throws IOException
     */
    public FullHttpResponse addcoupon() throws IOException {
        String uid = strings(UserStruct.uid);
        User user = messageService.findUserByID(uid);
        if (user == null) {
            logger.error(req.uri() + ",Param=" + request.getBodyHttpDatas() + " ,addcoupon Error,Error message=" + ErrorCode.SYS_ERR);
            return HttpBack.back_error(ErrorCode.SYS_ERR);
        }
        String disid = strings(JsonParams.disid);
        String json = strings(JsonParams.json);
        String from = strings(JsonParams.from);
        String context = strings(JsonParams.context);
        int isend = integers(JsonParams.type, 0);
        int cmd = integers(JsonParams.cmd, 0);
        Coupon coupon = Coupon.create(disid, context);
        if (coupon == null)
            coupon = Coupon.create(disid, json);
        if (coupon == null) {
            logger.error(req.uri() + ",Param=" + request.getBodyHttpDatas() + " ,addcoupon Error,Error message=coupon == null");
            return HttpBack.back_error(ErrorCode.SYS_ERR);
        }
        long date = System.currentTimeMillis();
        if (coupon.getUtype() == Coupon.utype_days) {
            coupon.setBegin(date);
            coupon.setEndtime(date + 24L * 60L * 60L * 1000L * coupon.getDay());
        }
        coupon.setUid(uid);
        coupon.setCdate(date);
        coupon.setStatus(Coupon.status_nouse);
        Long back = Redis.shard.lpush(RedisTable.CPNL + user.uid(), coupon.disid());
        Long backh = Redis.shard.hset(RedisTable.CPNH + user.uid(), coupon.disid(), coupon.createValue());
        //TODO 测试DEBUG用
        logger.warn("拉人券 : 添加结果 UID=" + uid + "\tLIST=" + back + "\tHASH=" + backh);
        //处理拉人券逻辑
        if (coupon.getType() == Coupon.type_pull && StringUtils.isNotEmpty(from)) {
            //如果是拉人券
            int money = coupon.getDiscount().intValue();
            //获得拉人券 money 总数
            Redis.shard.hincrBy(RedisTable.TOTLECM + user.uid(), RedisTable.TOTLECM_COUPON, money);
            Long len = Redis.shard.lrem(RedisTable.LRL + user.uid(), 0, from + JsonParams.SPLIT + 0);
            if (len != null && len > 0) {
                Redis.shard.lpush(RedisTable.LRL + user.uid(), from + JsonParams.SPLIT + money + JsonParams.SPLIT + 0);
            }
        }
        Put put = coupon.createPut(disid, coupon.getBegin());
        CHbase.bean().put(Coupon.table, put);
        try {
            if (isend == 0) {
                messageService.addCouponSysMessage(coupon, user, cmd == 0, context);
            }
        } catch (Exception e) {
            logger.error(req.uri() + ",Param=" + request.getBodyHttpDatas() + " ,addcoupon Error,Error message=" + e.getMessage(), e);
        }
        return HttpBack.back_200();
    }

    /**
     * 自定义透传发送<br>
     * 参数说明 :<br>
     * uid : 用户ID<br>
     * type : 透传数据类型 , 详细请看 - AndroidMessage 类中定义信息<br>
     * json : 需要透传的数据, 数据为标准的Json数据<br>
     *
     * @link AndroidMessage(com.circle.netty.formation.message.model.AndroidMessage)
     */
    public FullHttpResponse transcid() {
        String uid = strings(UserStruct.uid);
        User user = messageService.findUserByID(uid);
        if (user == null) {
            logger.warn(req.uri() + " , Params : [" + request.getBodyHttpDatas() + "] ErrorCode=" + ErrorCode.USER_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.USER_NOT_EXISTS);
        }
        String cid = strings(user.getCid());
        String type = strings(JsonParams.type);
        String json = strings(JsonParams.json);
        JsonNode jsonNode = Json.jsonParser("{}");
        if (StringUtils.isNotEmpty(json)) {
            jsonNode = Json.jsonParser(json);
        }
        messageService.sendDataByCid(cid, type, jsonNode);
        return HttpBack.back_200();
    }

    /**
     * 接口参数 ： /message/transapp <br>
     * type ： 透传 数据类型   classify()<br>
     * json ： 透传打数据<br>
     * 给App 发送透传<br>
     */
    public FullHttpResponse transapp() {
        logger.info(req.uri() + ",Param=" + request.getBodyHttpDatas());
        String tag = strings(JsonParams.tag);
        String phone = strings(JsonParams.phone);
        String province = strings(JsonParams.province);
        String type = strings(JsonParams.type);
        String json = strings(JsonParams.json);
        JsonNode jsonNode = Json.jsonParser("{}");
        if (StringUtils.isNotEmpty(json)) {
            jsonNode = Json.jsonParser(json);
        }
        messageService.transportationApp(tag, phone, province, type, jsonNode);
        return HttpBack.back_200();
    }

    /**
     * 自定义推送(iOS)/通知(Android) <br>
     * 推送URL : /message/pushcid <br>
     * 参数 : <br>
     * uid : 用户ID
     * type : 对应 系统消息的 type
     * context : 显示内容 , 即标题 "问啊后面的内容"
     * data : 推送携带关键数据 , qid uid , url 等 根据不同的type而定
     */
    public FullHttpResponse pushcid() {
        String uid = strings(UserStruct.uid);
        User user = messageService.findUserByIdFromElk(uid);
        if (user == null) {
            logger.warn(req.uri() + " , Params : [" + request.getBodyHttpDatas() + "] ErrorCode=" + ErrorCode.USER_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.USER_NOT_EXISTS);
        }
        String type = strings(JsonParams.type);
        String context = strings(JsonParams.context);
        String data = strings(AndroidMessage.att_data);
        messageService.pushByCid(user, context, type, data);
        return HttpBack.back_200();
    }

    /**
     * 目前只支持 Android
     * url
     * <p/>
     * 自定义推送发送
     */
    public FullHttpResponse pushapp() {
        String cmd = strings(AndroidMessage.att_cmd);
        String sys = strings(JsonParams.sys);
        String context = strings(JsonParams.context);
        String data = strings(AndroidMessage.att_data);
        messageService.pushByApp(context, cmd, sys, data);
        return HttpBack.back_200();
    }


    public FullHttpResponse alipay() throws IOException {
        logger.info(req.uri() + ",Param=" + request.getBodyHttpDatas());
        String qid = strings(JsonParams.QUESTION.qid);
        String uid = strings(UserStruct.uid);
        User user = messageService.findUserByID(uid);
        int status = integers(JsonParams.QUESTION.status, 1);
        messageService.sendAlipaySttatus(user, qid, status);
        return HttpBack.back_200();
    }

    public UserPacket findUserPacket(User user) throws IOException {
        UserPacket packet = new UserPacket();
        packet.create(CHbase.bean().get(UserPacket.table, new Get(Bytes.toBytes(user.uid()))));
        packet.uid(user.uid());
        return packet;
    }

    /**
     * 画板透传以及提示相关<br>
     * 参数:<br>
     * type : 透传类型<br>
     * uid : 用户ID<br>
     * cid : 接收透传用户的CID<br>
     */
    public FullHttpResponse board() {
        String type = strings(JsonParams.type);
        String cid = strings(UserStruct.cid);
        String uid = strings(UserStruct.uid);
        if (StringUtils.isEmpty(cid) || StringUtils.isEmpty(uid) || StringUtils.isEmpty(type))
            return HttpBack.back_error(ErrorCode.TYPE_ERR);
        switch (type) {
            case type_call:
                messageService.sendBoardCall(cid, uid, 1);
                break;
            case type_answer:
                messageService.sendBoardAnswer(cid, uid, 2);
                break;
            case type_hanup:
                messageService.sendBoardHangup(cid, uid, 3);
                break;
            case type_send:
                String data = strings(JsonParams.context);
                messageService.sendBoardSend(cid, uid, 4, data);
                break;
        }
        return HttpBack.back_200();
    }

    /**
     * 抢答推送 : <br>
     * 地址URL: /message/robque
     * 参数<br>
     * qid : 问题ID<br>
     * uid : 用户ID<br>
     * words : 捎句话<br>
     * context : 用户数据<br>
     */
    public FullHttpResponse robque() throws IOException {
        String qid = strings(JsonParams.QUESTION.qid);
        String uid = strings(UserStruct.uid);
        String words = strings(JsonParams.msg);
        String context_json = strings(JsonParams.context);
        //获取数据
        Question question = messageService.findQuestionByQid(qid);
        if (question == null || (StringUtils.isEmpty(question.qid()))) {
            //问题不存在
            logger.error(req.uri() + ",Param=" + request.getBodyHttpDatas() + ",ErrorCode=" + ErrorCode.QUE_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.QUE_NOT_EXISTS);
        }
        User user = messageService.findUserByID(uid);
        User quer = messageService.findUserByID(question.getQuid());
        if (user == null) {
            logger.error(req.uri() + ",Param=" + request.getBodyHttpDatas() + ",ErrorCode=" + ErrorCode.USER_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.USER_NOT_EXISTS);
        }
        if (quer == null) {
            logger.error(req.uri() + ",Param=" + request.getBodyHttpDatas() + ",ErrorCode=" + ErrorCode.USER_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.USER_NOT_EXISTS);
        }
        //状态 - 抢答人数
        //获取要推送的人-
        Set<String> cids = Redis.shard.smembers(RedisTable.QPL + qid);
        messageService.sendRobPubTtanMsg(cids, question, user, words, quer, context_json);
        return HttpBack.back_200();
    }

    /**
     * 透传,通知用户有系统消息
     *
     * @throws IOException
     */
    public FullHttpResponse sysmsg() throws IOException {
        String cid = strings(UserStruct.cid);
        //获取数据
        int size = integers(JsonParams.size, -1);
        if (size == -1) return HttpBack.back_error(ErrorCode.SIZE_ERR);
        messageService.sendSysMessageNumber(cid, size);
        return HttpBack.back_200();
    }

    /**
     * 透传,通知用户有系统消息
     * 参数 uid 用户ID
     * json : 系统消息 数据 可以json 也可以 create 拼接数据
     * type : 是否推送 默认推送 0,  1
     *
     * @throws IOException
     */
    public FullHttpResponse addsysmsg() throws IOException {
        String uid = strings(UserStruct.uid);
        User user = messageService.findUserByIdFromElk(uid);
        if (user == null) {
            logger.info(req.uri() + ",Param=" + request.getBodyHttpDatas() + ",ErrorCode=" + ErrorCode.USER_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.USER_NOT_EXISTS);
        }
        String json = strings(JsonParams.json);
        int type = integers(JsonParams.type, 0);
        SysMessage message = SysMessage.create(json);
        if (message == null) {
            message = (SysMessage) Json.jsonParser(json, SysMessage.class);
        }
        //message.setType(SysMessage.type_sys);
        //获取数据
        messageService.addSystemMessage(user, message, String.valueOf(message.getAbout()), type == 0, 0);
//        AddSystemMessage thread = new AddSystemMessage(user, message, String.valueOf(message.getAbout()), type==0,1,messageService);
//        GROUP.executor.execute(thread);
        return HttpBack.back_200();
    }

    /**
     * 推送 ，
     * uid ： 0 单个用户推送 1 多个用户推送
     * json ： 系统消息
     * type ： 0
     */
    @Deprecated
    public FullHttpResponse tuisong() throws IOException {
        String uid = strings(UserStruct.uid);
        User user = messageService.findUserByID(uid);
        if (user == null) {
            logger.info(req.uri() + ",Param=" + request.getBodyHttpDatas() + ",ErrorCode=" + ErrorCode.USER_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.USER_NOT_EXISTS);
        }
        String json = strings(JsonParams.json);
        int type = integers(JsonParams.type, 0);
        SysMessage message = SysMessage.create(json);
        if (message == null) {
            message = (SysMessage) Json.jsonParser(json, SysMessage.class);
        }
        //message.setType(SysMessage.type_sys);
        //获取数据
        messageService.addSystemMessage(user, message, String.valueOf(message.getAbout()), type == 0, 0);
//        AddSystemMessage thread = new AddSystemMessage(user, message, String.valueOf(message.getAbout()), type==0,1,messageService);
//        GROUP.executor.execute(thread);
        return HttpBack.back_200();
    }

    /**
     * 问题状态处理方法 : <br>
     * 参数说明 : <br>
     * qid : 问题ID<br>
     * is_app : 0 表示前端,即HTTP服务请求;1 表示其他服务调用(一般需要修改题目状态,默认)<br>
     * status : 状态-操作指令<br>
     * uid : 用户ID<br>
     * cid : 用户个推的Client id<br>
     * size : 大小<br>
     * start : 分段数开始<br>
     * score : 分段总数<br>
     * deal : 处理方开始<br>
     * ********************************************************
     * 以上关于发题推送选项废弃
     *
     * @return
     * @throws IOException
     */
    public FullHttpResponse question() throws IOException {
        String qid = strings(JsonParams.QUESTION.qid);
        int is_app = integers(JsonParams.type, 1);//标识前端还是后端
        if (StringUtils.isEmpty(qid)) {
            logger.warn(req.uri() + ", Params=" + request.getBodyHttpDatas() + ",ErrorCode=" + ErrorCode.QUE_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.QUE_NOT_EXISTS);
        }
        Question question = messageService.findQuestionByQid(qid);
        if (question == null) return HttpBack.back_error(ErrorCode.QUE_NOT_EXISTS);
        int status = integers(JsonParams.QUESTION.status, -1);
        String uid = strings(UserStruct.uid);
        if (status == -1) return HttpBack.back_error(ErrorCode.STATUS_ERR);
        switch (status) {
            case QuestionStruct.stu_read://发题 - 进入读题阶段 - 通知用户群 - 发送推送 - 告诉用户有新题  - [前端]
                int size = integers(JsonParams.size, 0);
                int start = integers(JsonParams.start, 0);//分段数-也就是页数
                int subtotle = integers(JsonParams.score, 0);//分段总数
//                messageService.sendMsgQuesPublish(question, start, size, subtotle);  //50%
                /*****************************************************************************
                 *
                 *****************************************************************************/
                if (question.getHide() == 0 && question.getRob() == 0) {
                    question.setTitle(SensitivewordFilter.filter.replaceSensitiveWord(question.getTitle(), 1, JsonParams.xixi));
                    messageService.sendMsgQuesPublish_Thread(question, 800);  //50%
                }
                break;
            case QuestionStruct.stu_rob://读题完成 - 进入抢题阶段 - 通知刷新到题目的用户 可以抢答了 [佳佳调用]
                messageService.sendQueInRobStep(question);//OK 100%
                break;
            case QuestionStruct.stu_chose://OK - 筛选 - 通知提问人 筛选 [前端/佳佳] 0
                messageService.sendQueInChoseStep(question, is_app);//OK 100%
                if (is_app == 0) {
                    messageService.sendQuesTimer(question, QuestionStruct.stu_chose, false);
//                    Redis.CONNECT.publish(question.qid(), String.valueOf(QuestionStruct.stu_chose));
                }
                break;
            case QuestionStruct.stu_answer://回答 - 通知答题者 回答问题 [前端(选择答题人)]
                if (is_app == 0) {
                    if (messageService.sendQueInAnswer(question)) {
                        messageService.sendQuesTimer(question, QuestionStruct.stu_answer, false);
                        //Redis.CONNECT.publish(question.qid(), String.valueOf(QuestionStruct.stu_answer));
                    } //OK 100%
                }
                break;
            case QuestionStruct.stu_delay://延时 - 通知双方延时 [前端(申请延时)]
                String cid = strings(UserStruct.cid);
                messageService.sendQueDelay(question, is_app, cid);//OK 100%
                break;
            case QuestionStruct.stu_finish://完成-未评价 - 到完成时间 - 自动完成 - 通知 双方 [前端(手动完成)/佳佳(自动完成)]
                messageService.sendQueFinish(question, is_app);//OK 100%
                messageService.finishAnsAndQueRank(question); //完成后处理用户的排行榜数据
                if (is_app == 0) {
                    messageService.sendQuesTimer(question, QuestionStruct.stu_finish, false);
//                    Redis.CONNECT.publish(question.qid(), String.valueOf(QuestionStruct.stu_finish));
                }
                break;
            case QuestionStruct.stu_success://评价完成 - 到评价时间 - 自动评价 [前端(评价-通知对方)/佳佳(自动评价)]
                messageService.sendQueAppratise(question, is_app, uid);//OK 100%
                break;
            case QuestionStruct.stu_cancel://取消订单 - 用户已经取消订单 此处不处理 [前端/佳佳(自动取消订单)]
                int deal = integers(JsonParams.deal, 0);
                messageService.sendQueCancelQues(question, is_app, deal); //100%
                if (is_app == 0) {
                    messageService.sendQuesTimer(question, QuestionStruct.stu_cancel, false);
//                    Redis.CONNECT.publish(question.qid(), String.valueOf(QuestionStruct.stu_cancel));
                }
                break;
            case 8:// 可以取消订单 - 发送透传给提问者  [佳佳]
                messageService.sendQueCancel(question); //100%
                break;
            case 9:// 提醒延时 - 提问者/回答者 都发透传 [佳佳]
                messageService.sendQueRemandDelay(question); //100%
                break;
            case 10:// 接收/拒绝 延时
                cid = strings(UserStruct.cid);
                deal = integers(JsonParams.deal, -1);
                if (deal != -1) {
                    messageService.sendDelayDeal(question, cid, deal); //100%
                } else {
                    logger.warn(req.uri() + " Params : [ " + request.getBodyHttpDatas() + " ] 不知道怎么处理");
                }
                break;
            case 15:// 收取抢答人 - 延时3秒 -- 发题进程调用
                messageService.dealSelectRobUser(question); //100%
                break;
        }
        return HttpBack.back_200();
    }

    public FullHttpResponse changeRanges() throws IOException{
        String qid = strings(JsonParams.QUESTION.qid);
        String yyyyMMdd = strings(JsonParams.RANKS.yyyyMMdd);
        //String status = strings(QuestionStruct.status);
        //int week = integers(JsonParams.RANKS.week,0);
        if (StringUtils.isEmpty(qid)) {
            logger.warn(req.uri() + ", Params=" + request.getBodyHttpDatas() + ",ErrorCode=" + ErrorCode.QUE_NOT_EXISTS);
            return HttpBack.back_error(ErrorCode.QUE_NOT_EXISTS);
        }
        Question question = messageService.findQuestionByQid(qid);
        messageService.rankChange(question,yyyyMMdd); //完成后处理用户的排行榜数据
        return HttpBack.back_200();

    }

    /**
     * 打钱钱 给谁
     * type  = 1 提问人 2 答题人
     * qid ： 问题ID
     *
     * @return 111
     * @throws IOException
     */
    public FullHttpResponse backMoney() throws IOException {
        String qid = strings(QuestionStruct.qid);
        int type = integers(JsonParams.type, 0);
        Question question = messageService.findQuestionByQid(qid);
        if (question == null) return HttpBack.back_error(ErrorCode.QUE_NOT_EXISTS);
        //只要 lock 不等于1  表示投诉已经处理过 - -
        if (question.getLock() != 1) return HttpBack.back_error(ErrorCode.REDY_DEAL);
        String uid;
        if (type == 1) {
            uid = question.getQuid();
        } else if (type == 2) {
            uid = question.getAuid();
        } else {
            return HttpBack.back_error(ErrorCode.TYPE_ERR);
        }
        User user = messageService.findUserByID(uid);
        if (user == null) return HttpBack.back_error(ErrorCode.USER_NOT_EXISTS);
        if (question.getQuid().equalsIgnoreCase(uid)) {
            messageService.backMoney(question, user);
        } else if (question.getAuid().equalsIgnoreCase(uid)) {
            messageService.backMoneyToAuser(question, user);
        }
        return HttpBack.back_200();
    }
}
