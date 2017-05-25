package com.circle.netty.test;

import com.circle.core.elastic.CElastic;
import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.circle.core.util.Verification;
import com.circle.netty.formation.message.model.FreshFilterRule;
import com.circle.netty.formation.message.model.QueRole;
import com.circle.netty.formation.message.model.UserPt;
import com.circle.netty.formation.message.model.struct.QuestionStruct;
import com.circle.netty.formation.message.model.struct.UserStruct;
import com.circle.netty.formation.util.RedisTable;
import com.circle.netty.http.JsonParams;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * @author Created by Administrator on 2015/10/13.
 */
public class GisTest {
    Config config;

    @Before
    public void before() throws IOException {
        config = new Config("config/app.properties");
        CElastic.inital(config);
    }

    @Test
    public void 查询用户手机号() {
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch("faq").setTypes(UserStruct.table_EL);
        builder.setQuery(QueryBuilders.matchQuery(UserStruct.mobile, "13323499191\n" +
                "18622618050\n" +
                "18622503136\n" +
                "13512279010\n" +
                "18513583823\n" +
                "13821711535\n" +
                "18612419694\n" +
                "18637287918\n" +
                "17001100880\n" +
                "13752618917\n" +
                "15922292101\n" +
                "18686203071\n" +
                "18602222097\n" +
                "13752507840\n" +
                "13512221013\n" +
                "13820754734\n" +
                "13810865238\n" +
                "13821455241\n" +
                "15122148107\n" +
                "18602232105\n" +
                "18512213887\n" +
                "13622121113\n" +
                "18622181461\n" +
                "15232202696\n" +
                "15054109920\n" +
                "18322696455\n" +
                "18322695762\n" +
                "18822362031\n" +
                "18222958102\n" +
                "18502663854\n" +
                "18575387997"));
        builder.addField(UserStruct.cid);
        builder.addField(UserStruct.mobile);
        builder.setSize(9999);
        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        Map<Object, Object> strings = new HashMap<>();
        for (SearchHit hit : hits) {
            strings.put(hit.getFields().get(UserStruct.mobile).getValues().get(0), hit.getFields().get(UserStruct.cid).getValues().get(0));
        }
        System.out.println(strings);
    }


    @Test
    public void testSearchGis() {
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch("faq").setTypes("art_que");
//        GeoDistanceSortBuilder geo = SortBuilders.geoDistanceSort("location");
//        geo.point(1,1);
//        geo.sortMode("min");
//        geo.order(SortOrder.DESC);
//        builder.addSort(geo);
        GeoDistanceRangeFilterBuilder filter = FilterBuilders.geoDistanceRangeFilter("posxy");
        filter.filterName("posxy");
        filter.point(0, 0);
        filter.from("0.2km");
        filter.to("999999km");
        builder.setPostFilter(filter);
        System.out.println(builder);
        System.out.println(builder.get());
    }

    @Test
    public void testInsertGisInfo() {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            map.put("location", (new Random().nextFloat()) + "," + (new Random().nextFloat()));
            CElastic.elastic().client.prepareIndex("test2", "geo_test", String.valueOf(i + 1)).setSource(map).get();
        }
    }

    @Test
    public void testInsertGisInfo2() {
        SearchRequestBuilder builder = CElastic.elastic().client.prepareSearch(CElastic.circle_index);
        //设置查询的表,用户表
        builder.setTypes(UserStruct.table_EL);
        //设置返回记录条数
        builder.setSize(100);//设置返回数量
        FreshFilterRule freshFilterRule = new FreshFilterRule();
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        //设置分段
        bool.must(QueryBuilders.rangeQuery("incr").from(0).to(9999));
        //黑白名单 过滤
        bool.must(QueryBuilders.rangeQuery(UserStruct.access).from(0).to(1));
        //不能包含提问者本身
        //bool.mustNot(QueryBuilders.matchQuery(QuestionStruct.quid, que.getQuid()));
//        bool.mustNot(QueryBuilders.termQuery("_id",que.getQuid()));
        bool.should(QueryBuilders.matchQuery(UserStruct.auth, "Question Type"));
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
        price.must(QueryBuilders.rangeQuery("start").to(5));
        price.must(QueryBuilders.rangeQuery("end").from(5));
        bool.must(QueryBuilders.hasChildQuery(UserPt.table_el, price));
        Map<String, String> nosee = new HashMap<>();
        nosee.put(RedisTable.NOSEE_IMEI,"alskjdlaksjd");
        nosee.put(RedisTable.NOSEE_ACC,"alskjdlaksjd");
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
        System.out.println(builder);
    }

}
