package com.circle.wena.task.huanxin;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.circle.core.util.CircleMD5;
import com.circle.core.util.Config;
import com.circle.core.util.Verification;
import com.circle.wena.task.huanxin.comm.PropertiesUtils;
import com.circle.wena.task.huanxin.httpclient.apidemo.EasemobIMUsers;
import com.circle.wena.task.util.EmailUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Created by cxx15 on 2015/12/2.
 */
public class DealHaunxin {
    private static String CONFIG_PATH = "config";
    private static String CONFIG_PATH_HUANXIN_INDEX = "CONFIG_PATH_HUANXIN_INDEX";
    private static Logger logger = LoggerFactory.getLogger(DealHaunxin.class);
    public static int size = 100;//默认 100个
    public static double hour = 40;//暂停多少毫秒,继续
    public static int index = 0;//默认 100个
    public static int start = 0;//开始数量
    public static long fail = 0;//默认 失败数量
    public static long count = 0;//默认 100个

    public static void main(String[] args) throws IOException {
        //加载配置文件
        if (args != null && args.length >= 1) {
            CONFIG_PATH = args[0];
        }
        if (args != null && args.length >= 3) {
            size = Integer.valueOf(args[2]);
        }
        if (args != null && args.length >= 2) {
            hour = Double.valueOf(args[1]);
        }
        //初始化环信修改密码
        logger.info("加载环信配置文件中 文件: huanxin.properties ............");
        PropertiesUtils.getProperties(CONFIG_PATH + "/huanxin.properties");
        logger.info("加载环信配置文件成功 .............. ");
        logger.info("加载应用连接配置文件 文件 : app.properties.............. ");
        Config config = new Config(CONFIG_PATH + "/app.properties");
        logger.info("加载应用连接配置文件 成功.............. ");
        logger.info("@@@@@@@@@@@@@@@ inital email ............");
        EmailUtils.intail(config);
        logger.info("@@@@@@@@@@@@@@@ inital email success ....");
        logger.info("初始化ElasticSearch 连接.............. ");
        CElastic.inital(config);
        logger.info("初始化ElasticSearch 连接成功.............. ");
        Redis.initialShard(config);
        index = Verification.getInt(0, Redis.shard.get(CONFIG_PATH_HUANXIN_INDEX));
        start = index;
        logger.info("开始任务-- 开始用户: " + start + ".............. ");
        runtask();
        //记录,处理数量
        Redis.shard.set(CONFIG_PATH_HUANXIN_INDEX, String.valueOf(index));
        boolean back = Redis.shard.exists("EMAIL_REMIND_MANAGER");
        if (!back) {
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>task run successfully - send email <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            //4小时间隔,发送一次邮件
            EmailUtils.sendEmailAsk("定时任务:用户环信账号注册", new StringBuffer("com.circle.wena.task.dead.TaskClearDeahQuestion" +
                    "\r开始数量:" + start +
                    "\r失败数量 :" + fail +
                    "\r总数数量 :" + count +
                    "\rtask run successfully......"));
            Redis.shard.setex("EMAIL_REMIND_MANAGER", 4 * 3600, String.valueOf(index));
            Redis.shard.setex("EMAIL_REMIND_MANAGER_cache", 8 * 3600, String.valueOf(index));
        }
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>task run successfully <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    private static void runtask() {
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        builder.setTypes("ELC_TAB_REALUSER");
        builder.setFrom(index);
        builder.setSize(size);
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        builder.setQuery(bool);
        builder.addSort("cdate", SortOrder.ASC);
        SearchResponse response = builder.get();
        count = response.getHits().getTotalHits();
        for (SearchHit hit : response.getHits()) {
            index++;
            JsonNode node = Json.jsonParser(hit.getSourceAsString());
            JsonNode mob = node.get("mobile");
            String uid = hit.getId();
            ObjectNode back;
            JsonNode err = null;
            ObjectNode user = JsonNodeFactory.instance.objectNode();
            try {
                String passwd = Redis.shard.hget(CircleMD5.encodeSha1(mob.asText()), "pass");
                if (passwd != null && StringUtils.isNotEmpty(uid)) {
                    user.put("username", uid);
                    user.put("password", passwd);
                    back = EasemobIMUsers.createNewIMUserSingle(user);
                    err = back.get("error");
                } else {
                    fail++;
                }
                logger.info("uid:" + uid + "\tindex:" + index + "\tUserInfo:" + user + "\tError:" + err);
                Thread.sleep((long) hour);
            } catch (Exception e) {
                fail++;
            }
        }
        if (index < count) {
            runtask();
        }
    }
}
