package com.circle.core.elastic;

import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Created by fomky on 15-6-23.
 */
@SuppressWarnings("unused")
public class ElasticBack<T> {
    public static final int START = 0;
    public static final int COUNT = 30;

    private List<T> list;
    private Map<String, String> mustQuery;
    private Map<String, String> mustnotQuery;
    private List<String> sort; //排序的列
    private List<SearchRange> rangeQuery; //排序的列
    private SortOrder order = SortOrder.DESC;//默认倒排序
    private String id_name;
    private long totle;
    private int page;
    private int size;
    private Class<T> tclass;
    private static Logger logger = LoggerFactory.getLogger(ElasticBack.class);

    public ElasticBack(Class<T> tclass) {
        this(new ArrayList<T>(), null, tclass, START, COUNT);
    }

    public ElasticBack(List<T> list, Map<String, String> mustQuery, Class<T> tclass, int page, int size) {
        this.setList(list);
        this.setMustQuery(mustQuery);
        this.setPage(page);
        this.setSize(size);
        this.setTclass(tclass);
    }

    /**
     * Add one json object . if json error wile cont
     *
     * @param json 数据
     */
    public boolean addSource( String json,String id) {
        try {
            if(json==null) return false;
            T obj = (T) Json.jsonParser(json, tclass);
            Method method = tclass.getMethod(id_name,String.class);
            method.invoke(obj,id);
            if (obj != null) {
                getList().add(obj);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("json case to class error.", e);
            return false;
        }
    }

    /**
     * Add one object
     *
     * @param obj 要添加的类
     * @return 是否添加成功
     */
    public boolean addSource(T obj) {
        try {
            if (obj != null) {
                getList().add(obj);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("json case to class error.", e);
            return false;
        }
    }

    public T object(String json) {
        try {
            return  (T) Json.jsonParser(json, tclass);
        } catch (Exception e) {
            logger.error("json case to class error.", e);
            return null;
        }
    }

    public List<SearchRange> getRangeQuery() {
        return rangeQuery;
    }

    public void setRangeQuery(List<SearchRange> rangeQuery) {
        this.rangeQuery = rangeQuery;
    }

    public void setId_name(String id_name) {
        this.id_name = id_name;
    }

    public Map<String, String> getMustnotQuery() {
        return mustnotQuery;
    }

    public void setMustnotQuery(Map<String, String> mustnotQuery) {
        this.mustnotQuery = mustnotQuery;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public Class<T> getTclass() {
        return tclass;
    }

    public void setTclass(Class<T> tclass) {
        this.tclass = tclass;
    }

    public Map<String, String> getMustQuery() {
        return mustQuery;
    }

    public void setMustQuery(Map<String, String> mustQuery) {
        this.mustQuery = mustQuery;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotle() {
        return totle;
    }

    public void setTotle(long totle) {
        this.totle = totle;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
