package com.circle.wena.task.quelist;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.circle.wena.task.util.EmailUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * 问题列表列表列表
 * 100+1个列表 ， 前端随机抽取 一个队列
 * 取出 问题ID 问题ID
 * 再从HBASE    中查询问题详情 -- 好就这样 说定联
 *
 * @author Created by cxx on 15-10-26.
 */
public class TaskQueList {
    //前坠 - 列表前缀 哈哈啊 哈哈哈
    public static final String que_index_list = "que_index_list_";
    public static final String classfiy_all = "classfiy_all";//所有分类列表
    public static String def_config_path = "config";//所有分类列表
    public static Config config;
    public static Logger logger = LoggerFactory.getLogger("TaskQueList ->>");
    //缓存各个类型打题目，问题数目不定， 有可能一道题都没有
    public static Map<Integer, Set<String>> cache_ques_id = new HashMap<>();
    public static List<String> classifyid;
    public static int size = 100;//默认 100个

    public static void main(String[] arg) {
        if (arg != null && arg.length >= 1) {
            def_config_path = arg[0];
        }
        if (arg != null && arg.length == 2) {
            size = Integer.valueOf(arg[1]);
        }
        try {
            config = new Config(def_config_path + "/app.properties");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
        //初始化 email 配置
        logger.info("@@@@@@@@@@@@@@@ inital email ............");
        EmailUtils.intail(config);
        logger.info("@@@@@@@@@@@@@@@ inital email success ....");
        logger.info("=================================================");
        //初始化redis 配置
        logger.info("@@@@@@@@@@@@@@@ inital reids ............");
        Redis.initialShard(config);
        logger.info("@@@@@@@@@@@@@@@ inital reids success ....");
        logger.info("=================================================");
        //初始化 Elastc 配置
        logger.info("@@@@@@@@@@@@@@@ inital elastic ............");
        CElastic.inital(config);
        logger.info("@@@@@@@@@@@@@@@ inital elastic success ....");
        logger.info("=================================================");
        //查询 分类 ， 从redis 中
        logger.info("@@@@@@@@@@@@@@@ search Classify from redis cache ....");
        classifyid = new ArrayList<>(updateClassfiy().keySet());
        logger.info("classifyid : " + classifyid.toString());
        logger.info("=================================================");
        //缓存已经存在的
        for (int i = 0; i < size; i++) {
            Set<String> cache = Redis.shard.smembers(que_index_list+i);
            logger.info(":::::::::::: cache index " + i + " <<"+cache.size());
            cache_ques_id.put(i, cache);
        }

        //开始执行任务 ==================================================
        //index
        for (String cid : classifyid) {
            //查询出N 多个ID
            List<String> qids = runtask(cid);
            //将这些ID 均匀打分配到 各个 列表中 。。
            put_qid_to_sets(qids);
        }
        // 开始执行删除任务 ==============================================
        for(Integer index:cache_ques_id.keySet()){
            Set<String> cacheids = cache_ques_id.get(index);
            if(cacheids==null || cacheids.size()==0 ) continue;
            long s = Redis.shard.srem(que_index_list + index,cache_ques_id.get(index).toArray(new String[cacheids.size()]));
            logger.info("delete old <<"+index+">> number=" + s);
            //删除 已经缓存到 id
        }
        EmailUtils.sendEmailAsk("定时任务:更新问题列表->缓存到redis",new StringBuffer("com.circle.wena.task.quelist.TaskQueList" +
                " task run successfully......"));
    }

    private static void put_qid_to_sets(List<String> qids) {
        if (qids == null || qids.isEmpty()) return;
        int index = qids.size();
        for (int i = 0; i < size; i++) {
            String qid = qids.get(i % index);
            logger.debug("Add qid=" + qid + " --> Index List[" + que_index_list + i + "]");
            cache_ques_id.get(i).remove(qid);
            Redis.shard.sadd(que_index_list + i, qid);
        }
    }

    private static List<String> runtask(String cid) {
        //查询 100 个 。。。。固定值
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        //
        builder.setTypes("art_que");
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        //非隐藏到题目
//        bool.must(QueryBuilders.matchQuery("hide", 0));
        bool.must(QueryBuilders.rangeQuery("hide").from(0).to(1));
        //指定类型到 - 题目
        bool.must(QueryBuilders.matchQuery("type", cid));
        //还是 不可抢答的
        bool.must(QueryBuilders.rangeQuery("status").from(2).to(6));
        //必须是可以刷新到的
//        bool.must(QueryBuilders.matchQuery("refurbish", 0));
        builder.setQuery(bool);
        //仅仅返回 问题的状态足够咯
        builder.addField("status");
        //随机排序
        ScriptSortBuilder scriptSortBuilder = SortBuilders.scriptSort("Math.random()", "number");
        scriptSortBuilder.order(SortOrder.ASC);
        scriptSortBuilder.sortMode("max");
        builder.addSort(scriptSortBuilder);
//        logger.info(builder.toString());
        SearchResponse response = builder.get();
//        logger.info(response.toString());
        SearchHits hits = response.getHits();
        List<String> back  = new ArrayList<>();
        for (SearchHit hit:hits){
            back.add(hit.getId());
        }
        logger.info("find type:" + cid + "\t backSize=" +back.size());
        return back;
    }

    private static Map<String, Classify> updateClassfiy() {
        //加载缓存
        Map<String, Classify> map = new HashMap<>();
        List<String> list = Redis.shard.lrange(classfiy_all, 0, -1);
        for (String value : list) {
            Classify classify = (Classify) Json.jsonParser(value, Classify.class);
            if (classify != null)
                map.put(classify.getCid(), classify);
        }
        return map;
    }
}
