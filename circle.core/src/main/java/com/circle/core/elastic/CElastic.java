package com.circle.core.elastic;

import com.circle.core.util.Config;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author cxx  2015-6-10.
 */
public class CElastic {
    public static String[] ES_HOSTS;
    @Deprecated
    public static Integer ES_PORT;
    public TransportClient client;
    public static String ES_CLUSTER_NAME;
    public Config config;
    public static String circle_index;
    public static String[] INDEX;
    private static CElastic elastic;
    private static Logger logger = LoggerFactory.getLogger(CElastic.class);
    private static final String _parent = "_parent";

    private CElastic(Config config) {
        this.config = config;
    }

    public static CElastic elastic() {
        return elastic;
    }

    /**
     * Inital ElasticSearch connection .
     */
    @SuppressWarnings("resource")
    public static CElastic inital(Config cfs) {
        elastic = new CElastic(cfs);
        // init config config.properties
        elastic.config = cfs;
        ES_HOSTS = elastic.config.getAsString("eshosts").split("_");
        //ES_PORT = elastic.config.getAsInteger("es_port");
        ES_CLUSTER_NAME = elastic.config.getAsString("cluster.name");
        circle_index = elastic.config.getAsString("es_index");
        INDEX = new String[]{circle_index};
        // inital ElasticSearch cluster client.
        logger.info("== inital ElasticSearch cluster connect...====================");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", true)
                .put("cluster.name", ES_CLUSTER_NAME).build();
        TransportClient client = new TransportClient(settings);
        InetSocketTransportAddress[] addresses = new InetSocketTransportAddress[ES_HOSTS.length];
        for (int i = 0; i < ES_HOSTS.length; i++) {
            String[] hps = ES_HOSTS[i].split(":");
            addresses[i] = new InetSocketTransportAddress(hps[0], Integer.valueOf(hps[1]));
        }
        elastic.client = client.addTransportAddresses(addresses);
        logger.info("== ES client init success ! ===================================");
        return elastic;
    }


    public GetResponse get(String table, String id) {
        return client.prepareGet(circle_index, table, id).get();
    }

    public boolean index(String table, String id, Object object) {
        IndexResponse response = client.prepareIndex(circle_index, table, id).setSource(Json.json(object)).get();
        return response.isCreated();
    }
    public boolean index(String table, String id, Object object,String parent) {
        IndexResponse response = client.prepareIndex(circle_index, table, id).setSource(Json.json(object)).setParent(parent).get();
        return response.isCreated();
    }
    @Deprecated
    public void deleteFromParent(String table,String parent){
        DeleteByQueryRequestBuilder request  = CElastic.elastic().client.prepareDeleteByQuery(CElastic.circle_index)
                .setTypes(table);
        request.setQuery(QueryBuilders.termQuery(_parent,parent));
        request.get();
    }
    @Deprecated
    public void deleteFromParent(String table,String parent, String fild,int type){
        DeleteByQueryRequestBuilder request  = CElastic.elastic().client.prepareDeleteByQuery(CElastic.circle_index)
                .setTypes(table);
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        bool.must(QueryBuilders.termQuery(_parent,parent));
        bool.must(QueryBuilders.matchQuery(fild, type));
        request.setQuery(bool);
        request.get();
    }
    @Deprecated
    public void deleteFromParent(String table,Map<String,Object> hash){
        DeleteByQueryRequestBuilder request  = CElastic.elastic().client.prepareDeleteByQuery(CElastic.circle_index)
                .setTypes(table);
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        for(String key:hash.keySet()){
            bool.must(QueryBuilders.termQuery(key,hash.get(key)));
        }
        request.setQuery(bool);
        request.get();
    }



    public boolean udpate(String table, String id, Map<String, String> hash) {
        UpdateRequestBuilder builder = client.prepareUpdate(circle_index, table, id);
        builder.setDoc(hash);
        builder.setRetryOnConflict(5);
        return builder.get().isCreated();
    }

    public boolean update(String table, String id, Map<String, Object> hash) {
        UpdateRequestBuilder builder = client.prepareUpdate(circle_index, table, id);
        builder.setDoc(hash);
        builder.setRetryOnConflict(5);
        return builder.get().isCreated();
    }
    public boolean delete(String table, String id) {
        return delete(circle_index,table,id);
    }

    public boolean delete(String index,String table, String id) {
        DeleteRequestBuilder builder = client.prepareDelete(index, table, id);
        return builder.get().isFound();
    }

    public ElasticBack search(String table, ElasticBack back) {
        SearchRequestBuilder builder = client.prepareSearch(circle_index).setTypes(table);
        if (back.getSort() != null) {//排序
            for (Object sort : back.getSort()) {
                builder.addSort(sort.toString(), back.getOrder());
            }
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();//必须包含
        if (back.getMustQuery() != null && !back.getMustQuery().isEmpty()) {
            for (Object key : back.getMustQuery().keySet()) {
                Object value = back.getMustQuery().get(key);
                if (value != null) {
                    boolQuery.must(QueryBuilders.multiMatchQuery(value, key.toString()));
                }
            }
        }
        if (back.getMustnotQuery() != null && !back.getMustnotQuery().isEmpty()) {//不能包含的
            for (Object key : back.getMustnotQuery().keySet()) {
                Object value = back.getMustnotQuery().get(key);
                if (value != null) {
                    boolQuery.mustNot(QueryBuilders.multiMatchQuery(value, key.toString()));
                }
            }
        }
        //Add range query
        if (back.getRangeQuery() != null && !back.getRangeQuery().isEmpty()) {
            int len = back.getRangeQuery().size();
            SearchRange range;
            for (int j = 0; j < len; j++) {
                range = (SearchRange) back.getRangeQuery().get(j);
                if (range != null && range.getField() != null) {
                    boolQuery.must(QueryBuilders.rangeQuery(range.getField()).from(range.getFrom()).to(range.getTo()));
                }
            }
        }
        builder.setQuery(boolQuery);
        //设置开始返回,返回数量
        builder.setFrom(back.getPage() * back.getSize()).setSize(back.getSize());
        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        int len = hits.getHits().length;
        back.setTotle(hits.totalHits());
        for (int i = 0; i < len; i++) {
            SearchHit hit = hits.getAt(i);
            if (!hit.isSourceEmpty()) {
                String json = hit.getSourceAsString();
                back.addSource(json, hit.id());
            }
        }
        return back;
    }

    public QueryBack search_or(String table, QueryBack back) {
        SearchRequestBuilder builder = client.prepareSearch(circle_index).setTypes(table);
        if (back.getSort() != null)
            builder.addSort(back.getSort());
        if (back.getQuery() != null)
            builder.setQuery(back.getQuery());
        //设置开始返回,返回数量
        builder.setFrom(back.getStart()).setSize(back.getSize());
        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        int len = hits.getHits().length;
        back.setTotle(hits.totalHits());
        for (int i = 0; i < len; i++) {
            SearchHit hit = hits.getAt(i);
            if (!hit.isSourceEmpty()) {
                String json = hit.getSourceAsString();
                back.addSource(json, hit.id());
            }
        }
        return back;
    }
}
