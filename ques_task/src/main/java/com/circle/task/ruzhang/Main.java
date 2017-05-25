package com.circle.task.ruzhang;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.util.Config;
import com.circle.core.util.Verification;
import com.circle.task.ruzhang.util.EmailUtils;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.PropertyConfigurator;
import org.apache.phoenix.schema.types.PInteger;
import org.apache.phoenix.schema.types.PLong;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数 ：
 * 0 ： path 配置文件目录
 * 1 ： 读取时间开始（默认=0）
 * 2 ： 读取时间结束（默认=系统当前时间）
 *
 * @author Created by cxx on 15-10-2.
 */
public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    public static String path="config";
    public static long start = 0;
//    public static long end = System.currentTimeMillis()-24*3600*1000;
    public static long end = System.currentTimeMillis();
    public static int pageCount = 100;
    public static int index = 0;//默认 100个
    public static long fail = 0;//默认 失败数量
    public static long count = 0;//默认 100个

    public static void main(String[] args) throws IOException {
//        args = new String[1];
//        args[0]="config";
        //初始化配置
        initalconfig(args);
        //执行任务
        runtask();
        logger.info("task run successfully");
        EmailUtils.sendEmailAsk("定时任务:即将入账->用户余额",new StringBuffer("com.circle.task.ruzhang.Main " +
                "\r成功数量:" + index +
                "\r失败数量 :" + fail +
                "\r总数数量 :" + count +
                "\rtask run successfully......"));
    }

    /**
     * 执行任务
     */
    private static void runtask() throws IOException {
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        //设置返回字段
        //builder.addFields("cash", "disid", "discount", "cdate");
        //设置排序 ， 时间最远的优先打款
        builder.addSort("cdate", SortOrder.DESC);
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        //状态 5 -6 可进行转账
        bool.must(QueryBuilders.rangeQuery("status").from(5).to(6));
        bool.must(QueryBuilders.rangeQuery("cdate").from(start).to(end));
        //锁定
        bool.must(QueryBuilders.matchQuery("lock", 0));
        bool.must(QueryBuilders.matchQuery("ispay", 0));
        //不是刷单的
        //bool.must(QueryBuilders.matchQuery("sdan",0));
        builder.setQuery(bool);

        builder.setSize(pageCount);
        builder.setFrom(index);
        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        count = hits.getTotalHits();
//        index += hits.getHits().length;
        for (SearchHit hit : hits) {
            dealquestion(hit);
            index++;
        }
        //如果没有执行完成,继续执行
        if(index<count){
            runtask();
        }
    }

    private static void dealquestion(SearchHit hit) {
        try {
            if (hit.isSourceEmpty()) return;
            //创建对象
            Question question = (Question) Json.jsonParser(hit.getSourceAsString(), Question.class);
            question.qid(hit.getId());
            //更改状态 , 修改 Hbase=======================
            Put put = new Put(Bytes.toBytes(question.qid()));
            put.addColumn(BaseLog.family, Bytes.toBytes("ISPAY"), PInteger.INSTANCE.toBytes(1));
            put.addColumn(BaseLog.family, Bytes.toBytes("RDATE"), PLong.INSTANCE.toBytes(System.currentTimeMillis()));
            CHbase.bean().put(Question.table, put);
            //更改状态 , 修改 Elastic======================
            Map<String, Object> hash = new HashMap<>();
            hash.put("ispay", 1);
            hash.put("rdate", System.currentTimeMillis());
            CElastic.elastic().update("art_que", question.qid(), hash);
            //转入到余额中 =============================
            payintoCash(question);
        } catch (IOException e) {
            logger.info("cash into qid=" + hit.getId());
            fail++;
        }
    }

    private static void payintoCash(Question question) throws IOException {
        //讲即将入账余额中Hbase
        //===获取钱包数据===============================
        UserPacket packet = new UserPacket();
        Result result = CHbase.bean().get(UserPacket.table, new Get(Bytes.toBytes(question.getAuid())));
        packet.create(result);
        packet.uid(question.getAuid());
        if (packet.getCash() == null)
            packet.setCash(new BigDecimal(0));
        if (packet.getPrecash() == null)
            packet.setPrecash(new BigDecimal(0));
        if (packet.getTotle() == null)
            packet.setTotle(new BigDecimal(0));
        //===================================
        packet.setPrecash(packet.getPrecash().subtract(question.getCash()));
        packet.setCash(packet.getCash().add(question.getCash()));
        packet.setTotle(packet.getTotle().add(question.getCash()));
        //=====保存钱包信息====================
        Put put = packet.createPut(question.getAuid());
        CHbase.bean().put(UserPacket.table, put);
        try {
            Map<String,Object> maps = new HashMap<>();
            //ELC_TAB_REALUSER
            maps.put("income",packet.getTotle().doubleValue());
            CElastic.elastic().update("ELC_TAB_REALUSER",question.getAuid(),maps);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        logger.info("cash into auser=" + question.getAuid() + ", question=" + question.qid() + ", cash=" + question.getCash());
        //===================================e
    }

    private static void initalconfig(String[] args) throws IOException {
        if (args != null) {
            int length = args.length;
            if (length >= 1) {
                path = args[0];
            } else if (length >= 2) {
                start = Verification.getLong(0, args[1]);
            } else if (length >= 3) {
                end = Verification.getLong(System.currentTimeMillis()-24*3600*1000, args[2]);
//                end = Verification.getLong(System.currentTimeMillis(), args[2]);
            } else {
                System.exit(0);
            }
            PropertyConfigurator.configure(path + File.separator + "log4j.properties");
            Config config = new Config(path + File.separator + "app.properties");
            //初始化 email 配置
            logger.info("@@@@@@@@@@@@@@@ inital email ............");
            EmailUtils.intail(config);
            logger.info("@@@@@@@@@@@@@@@ inital email success ....");
            logger.info("=================================================");
            CElastic.inital(config);
            CHbase.instance(path + File.separator + "hbase-site.xml");
        } else {
            logger.error("args is null no noconfig ");
            System.exit(0);
        }
    }
}
