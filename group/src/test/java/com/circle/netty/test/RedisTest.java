package com.circle.netty.test;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.circle.core.util.CircleMD5;
import com.circle.core.util.Config;
import com.circle.netty.formation.message.model.Question;
import com.circle.netty.formation.message.model.UserPt;
import com.circle.netty.formation.message.model.struct.QuestionStruct;
import com.circle.netty.formation.message.model.struct.UserStruct;
import com.circle.netty.formation.util.RedisTable;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Created by cxx on 15-8-29.
 */
public class RedisTest {
    @Before
    public void before() throws Exception {
//        Redis.initial(new Config("config/app.properties"));
    }


    @Test
    public void testScripts1() throws IOException {
        CElastic.inital(new Config("config/app.properties"));
        Map<String, Object> hash = new HashMap<>();
        hash.put("arrays", new String[]{"1231-2200|0700-1100"});
        //CElastic.elastic().index()
        System.out.println(CElastic.elastic().client.prepareIndex("test", "test", "114").setSource(Json.json(hash)).get().isCreated());
    }

    @Test
    public void testScripts3() throws IOException {
        CElastic.inital(new Config("config/app.properties"));
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch("test");
        builder.setTypes("test");
        builder.addScriptField("size", "doc['arrays'].values.size>=0?doc['arrays'].values.size:0");
        builder.addScriptField("arrays", "doc['arrays']");
//        builder.setQuery(QueryBuilders.functionScoreQuery().add(ScoreFunctionBuilders.scriptFunction("doc['arrays'].values.size/2")));
        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        SearchHit hit;
        SearchHitField field;
        System.out.println(response);
        int len = hits.getHits().length;
        for (int i = 0; i < len; i++) {
            hit = hits.getAt(i);
            field = hit.field("arrays");
            if (field != null) {
                List<Object> value = field.getValues();
                System.out.println(value);
            }
        }
    }
    @Test
    public void testScripts5() throws IOException {
        CElastic.inital(new Config("config/app.properties"));
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        //设置查询的表,用户表
        builder.setTypes(UserStruct.table_EL);
        Question que = new Question();
        que.setCash(new BigDecimal(100));
        //设置返回记录条数
            builder.setSize(100);//设置返回数量
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        //设置分段
        bool.must(QueryBuilders.rangeQuery("incr").from(0).to(100));
        //黑白名单 过滤
        bool.must(QueryBuilders.rangeQuery(UserStruct.access).from(0).to(1));
        //不能包含提问者本身
//        bool.mustNot(QueryBuilders.matchQuery(QuestionStruct.quid, que.getQuid()));
//        bool.must(QueryBuilders.matchQuery(UserStruct.auth, que.getType()));
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
        Set<String> devicetokens = new HashSet<>();
        Set<String> devicetokens_nosound = new HashSet<>();
        SearchHit hit;
        int len = hits.getHits().length;
        for (int i = 0; i < len; i++) {
            hit = hits.getAt(i);
            String cid = fieldString(hit, UserStruct.cid);
            String syst = fieldString(hit, UserStruct.system);
            String sound = fieldString(hit, UserStruct.sound);
            String device = fieldString(hit, UserStruct.device);
            /**答题人，收到问题推送 T	*/
            if (StringUtils.isNotEmpty(syst) && syst.equalsIgnoreCase("IOS")) {
                if ("0".equalsIgnoreCase(sound) && !devicetokens.contains(device)) {
                    System.out.println("NO SOUND uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + sound + "\tDEV:" + device);
                    devicetokens_nosound.add(device);
                } else if(!devicetokens_nosound.contains(device)){
                    System.out.println("HAVE SOUND uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + sound + "\tDEV:" + device);
                    devicetokens.add(device);
                }
            } else {
                System.out.println("ANDROID uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + sound + "\tCID:" + cid);
            }
        }
    }


    @Test
    public void testParentCreate() throws IOException {
        CElastic.inital(new Config("config/app.properties"));
        UserPt pt = new UserPt();
        for (int i = 0; i < 10; i++) {
            int rand = new Random().nextInt(999);
            pt.setEnd(2000 - rand);
            pt.setStart(1000 - rand);
            pt.setType(rand % 2);
            CElastic.elastic().client.prepareIndex("faq",UserPt.table_el,CircleMD5.uuid()).setSource(Json.json(pt)).setParent("df89f0382ecf45798ed920cc3ae72762").get();
        }
        CElastic.elastic().client.prepareIndex("faq",UserPt.table_el,CircleMD5.uuid()).setSource(Json.json(pt)).setParent("1df89f0382ecf45798ed920cc3ae72762").get();
    }

    @Test
    public void testParentDelete() throws IOException, ExecutionException, InterruptedException {
        CElastic.inital(new Config("config/app.properties"));
        String parent = "df89f0382ecf45798ed920cc3ae72762";
        DeleteByQueryRequestBuilder request  = CElastic.elastic().client.prepareDeleteByQuery(CElastic.circle_index)
                .setTypes(UserPt.table_el);
        request.setQuery(QueryBuilders.matchQuery("type",null));
        System.out.println(request);
        System.out.println(request.get());

    }

    @Test
    public void testParent() throws IOException {
        CElastic.inital(new Config("config/app.properties"));
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        builder.setTypes(UserStruct.table_EL);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder time = QueryBuilders.boolQuery();
        BoolQueryBuilder price = QueryBuilders.boolQuery();
        int price_ps = 1000;
        int time_ps = 1000;
        time.must(QueryBuilders.rangeQuery("start").to(time_ps));
        time.must(QueryBuilders.rangeQuery("end").from(time_ps));
        time.must(QueryBuilders.matchQuery("type",0));
        price.must(QueryBuilders.matchQuery("type", 1));
        price.must(QueryBuilders.rangeQuery("start").to(price_ps));
        price.must(QueryBuilders.rangeQuery("end").from(price_ps));
        boolQueryBuilder.must(QueryBuilders.hasChildQuery(UserPt.table_el, price));
        boolQueryBuilder.must(QueryBuilders.hasChildQuery(UserPt.table_el, time));
        builder.setQuery(boolQueryBuilder);
//        filterBuilder.must(time);
        System.out.println(builder);
        System.out.println("===================================================");
        for (int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            System.out.println(builder.get());
            System.out.println(System.currentTimeMillis()-start);
        }

    }

    @Test
    public void testScripts2() throws IOException {
        CElastic.inital(new Config("config/app.properties"));
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        //设置查询的表,用户表
        builder.setTypes(UserStruct.table_EL);
        //设置返回记录条数
        builder.setSize(100);//设置返回数量
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        //黑白名单 过滤
        bool.must(QueryBuilders.rangeQuery(UserStruct.access).from(0).to(1));
        //不能包含提问者本身
        //TODO 推送提醒过滤
        Calendar calendar = Calendar.getInstance();
        bool.mustNot(QueryBuilders.matchQuery(UserStruct.pub, 0));
        bool.must(QueryBuilders.matchQuery(UserStruct.mobile, "18513583823"));
        BoolFilterBuilder parentFilter = FilterBuilders.boolFilter();
        String uid = "";
        parentFilter.must(FilterBuilders.queryFilter(QueryBuilders.matchQuery(UserStruct.uid, uid)));
        parentFilter.must(FilterBuilders.scriptFilter("").addParam("time", 1400));
        builder.setPostFilter(FilterBuilders.hasChildFilter(UserPt.table_el, parentFilter));
        //随机抽取
        ScriptSortBuilder sort = SortBuilders.scriptSort("Math.random()", "number");
        sort.order(SortOrder.ASC);
        builder.addSort(sort);
        //设置返回数据 ,用户CID
        builder.addFields("cid");
        builder.addFields("device");
        builder.addFields("system");
        builder.addFields("sound");
        builder.addFields("day");
        builder.setQuery(bool);
        //TODO 添加 地理位置筛选
        SearchResponse response = builder.get();
        System.out.println(response);
        SearchHits hits = response.getHits();
        SearchHit hit;
        Set<String> cids = new HashSet<>();
        int len = hits.getHits().length;
        for (int i = 0; i < len; i++) {
            hit = hits.getAt(i);
//            System.out.println("UID:" + hit.getId());
            //时间过滤
//            if (hit.field("time").getValues() != null) {
//                //当前时间
//                Object obj = hit.field("time").getValues().get(0);
//                if (obj != null && obj instanceof ArrayList) {
//                    ArrayList<String> times = (ArrayList<String>) obj;
//                    if (!times.isEmpty() && times.size() > 1) {
//                        int time = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);
//                        int size1 = times.size();
//                        int cache;
//                        int cache_before = 0;
//                        boolean isOK = false;
//                        for (int j = 0; j < size1; j++) {
//                            cache = Verification.getInt(0, times.get(j));
//                            if (j % 2 == 0) {
//                                cache_before = cache;
//                            } else if (cache_before < time && time < cache) {
//                                isOK = true;
//                                break;
//                            }
//                        }
//                        //如果可以
//                        if (!isOK)
//                            continue;
//                    }
//                }
//            }
//            //星期过滤
//            if (hit.field("day").getValues().get(0) != null) {
//                int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
//                Object obj = hit.field("day").getValues().get(0);
//                if (obj != null && obj.getClass() == ArrayList.class) {
//                    ArrayList<String> list = (ArrayList<String>) obj;
//                    if (!list.isEmpty() && list.size() > 1) {
//                        if (!list.contains(String.valueOf(day))) {
//                            continue;
//                        }
//                    }
//                }
//            }
//            //价格过滤
//            if (hit.field("price").getValues().get(0) != null) {
//                Object obj = hit.field("price").getValues().get(0);
//                if (obj != null && obj.getClass() == ArrayList.class) {
//                    ArrayList<String> times = (ArrayList<String>) obj;
//                    if (!times.isEmpty() && times.size() > 1) {
//                        int size1 = times.size();
//                        int cache;
//                        int cache_before = 0;
//                        boolean isOK = false;
//                        for (int j = 0; j < size1; j++) {
//                            cache = Verification.getInt(0, times.get(j));
//                            if (j % 2 == 0) {
//                                cache_before = cache;
//                                if (size1 - 1 == j && cache <= 100) {//最后一个
//                                    isOK = true;
//                                    break;
//                                }
//                            } else if (cache_before <= 100 && 100 < cache) {
//                                isOK = true;
//                                break;
//                            }
//                        }
//                        //如果可以
//                        if (!isOK) {
//                            continue;
//                        }
//                    }
//                }
//            }
            String cid = fieldString(hit, UserStruct.cid);
            String syst = fieldString(hit, "system");
            if (StringUtils.isNotEmpty(syst) && syst.equalsIgnoreCase("ios")) {
                /**答题人，收到问题推送 T	*/
                System.out.println("uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + fieldString(hit, "sound") + "\tDEV:" + fieldString(hit, "device"));
            } else {
                System.out.println("uid:" + hit.getId() + "\tsystem:" + syst + "\tsound:" + fieldString(hit, "sound") + "\tCID:" + cid);
                cids.add(cid);
            }
        }
        //将该用户加入到 可看列表中
        //给用户发送推送_
        System.out.println("CIDS : " + cids);
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

    @Test
    public void testScripts() throws IOException {
        CElastic.inital(new Config("config/app.properties"));
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        //���ò�ѯ�ı�,�û���
        builder.setTypes(UserStruct.table_EL);
        //���÷��ؼ�¼����
        builder.setSize(10);//���÷�������
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        //���÷ֶ�
        bool.must(QueryBuilders.rangeQuery("incr").from(1).to(20));
        builder.setQuery(bool);
        //���ò�ѯ�ı�,�û���
        builder.setTypes(UserStruct.table_EL);
        //���÷��ؼ�¼����
        //���÷ֶ�
        builder.addScriptField("cid", "doc['cid'].value");
        builder.addScriptField("device", "doc['device'].value");
        builder.addScriptField("system", "doc['system'].value");
        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        //System.out.println(response);
        SearchHit hit;
        SearchHitField field;
        int len = hits.getHits().length;
        for (int i = 0; i < len; i++) {
            hit = hits.getAt(i);
            field = hit.field("cid");
            if (field != null) {
                List<Object> value = field.getValues();
                if (!value.isEmpty()) {
                    System.out.println(value.get(0));
                }
//            } else {
//                logger.warn("���û�û��CID : " + hit.id());
            }
            //�����û����뵽 �ɿ��б���
        }
    }


    @Test
    public void testBefore() throws Exception {
        Redis.initial(new Config("config/app.properties"));
        long start = System.currentTimeMillis();
        System.out.println("Start = " + start);
        Jedis jedis = new Jedis("10.2.138.76", 8000);
        for (int i = 0; i < 100000; i++) {
            jedis.lpush("ssss", "ssss");
        }
        System.out.println("end = " + System.currentTimeMillis());
        System.out.println((System.currentTimeMillis() - start));
    }
}
