package com.circle.wena.task.dead;

import com.circle.core.elastic.CElastic;
import com.circle.core.util.Config;
import com.circle.imhxin.service.MsgService;
import com.circle.wena.task.util.EmailUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 计划任务   清理死题 ， 给用户退款
 * 从elastic中查询到 已经死掉的问题（订单）
 * 生成退款流水
 * 将钱钱 退回到用户账户中去
 * 发送-自动退款-系统消息
 *
 * @author Created by cxx on 15-11-4.
 */
public class TaskClearDeahQuestion {
    //前坠 - 列表前缀 哈哈啊 哈哈哈
    public static String def_config_path = "config";//所有分类列表
    public static String group_lvs = "group.lvs.server";//所有分类列表
    public static Config config;
    public static Logger logger = LoggerFactory.getLogger("TaskClearDeahQuestion ->>");
    //缓存各个类型打题目，问题数目不定， 有可能一道题都没有
    public static int size = 100;//默认 100个
    public static double hour = 1;//默认 100个
    public static int index = 0;//默认 100个
    public static long fail = 0;//默认 失败数量
    public static long count = 0;//默认 100个

    public static void main(String[] args) throws IOException {
        if (args != null && args.length >= 1) {
            def_config_path = args[0];
        }
        if (args != null && args.length >= 3) {
            size = Integer.valueOf(args[2]);
        }
        if (args != null && args.length >= 2) {
            hour = Double.valueOf(args[1]);
        }
        try {
            config = new Config(def_config_path + "/app.properties");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
        group_lvs = config.getAsString("group.lvs.server");
        //初始化 email 配置
        logger.info("@@@@@@@@@@@@@@@ inital email ............");
        EmailUtils.intail(config);
        logger.info("@@@@@@@@@@@@@@@ inital email success ....");
        logger.info("=================================================");
        //初始化 Elastc 配置
        logger.info("@@@@@@@@@@@@@@@ inital elastic ............");
        CElastic.inital(config);
        logger.info("@@@@@@@@@@@@@@@ inital elastic success ....");
        logger.info("=================================================");
//        logger.info("@@@@@@@@@@@@@@@ inital Hbase success ....");
//        CHbase.instance(def_config_path + "/hbase-site.xml");
//        logger.info("@@@@@@@@@@@@@@@ inital Hbase success ....");
//        logger.info("=================================================");
        //执行 task ..............
        runtask();
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>task run successfully <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        EmailUtils.sendEmailAsk("定时任务:处理死题->自动取消", new StringBuffer("com.circle.wena.task.dead.TaskClearDeahQuestion" +
                "\r成功数量:" + index +
                "\r失败数量 :" + fail +
                "\r总数数量 :" + count +
                "\rtask run successfully......"));
    }

    private static void runtask() {
        //查询 100 个 。。。。固定值
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        //
        builder.setTypes("art_que");
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        //非隐藏到题目
//        bool.must(QueryBuilders.matchQuery("hide", 0));
        //还是 不可抢答的
//        bool.must(QueryBuilders.matchQuery("rob", "1"));
        bool.must(QueryBuilders.rangeQuery("status").from(0).to(4));
        //过去 hour 小时前的死题
        bool.must(QueryBuilders.rangeQuery("cdate").from(0).to(System.currentTimeMillis() - hour * 3600000L));
        //必须是可以刷新到的
//        bool.must(QueryBuilders.matchQuery("refurbish", 0));
        builder.setQuery(bool);
        builder.setSize(size);
        builder.setFrom(index);
        //问题的状态足够咯
        builder.addSort("cdate", SortOrder.ASC);
//        logger.info(builder.toString());
        SearchResponse response = builder.get();
//        logger.info(response.toString());
        SearchHits hits = response.getHits();
        count = hits.getTotalHits();
        for (SearchHit hit : hits) {
            cancelQuestion(hit.getId());
        }
        logger.info("Index : " + index + "\t Count : " + count + "\tFail : " + fail);
        if (index < count) {
            index++;
            runtask();
        }
    }

    private static void cancelQuestion(String qid) {
        //发送http 请求 , 让 group 取消订单
        try {
            Map<String, String> map = new HashMap<>();
            map.put("qid", qid);
            map.put("status", "7");
            sendPost("http://" + group_lvs + "/message/question", map);
            map.remove(qid);
            CElastic.elastic().udpate("art_que", qid, map);
        } catch (IOException e) {
            logger.error("IOException >>" + e.getMessage(), e);
            fail++;

        }
    }

    private static void sendPost(String url, Map<String, String> param) throws IOException {
//        MsgService.create().client.post(url,param);
        CloseableHttpResponse response = null;
        try {
            response = MsgService.create().client.post(url, param);
            logger.info("Params : " + param + "\tBack Response: " + EntityUtils.toString(response.getEntity()) + "\t Index=" + index);
        } finally {
            if (response != null)
                response.close();
        }
    }
}
