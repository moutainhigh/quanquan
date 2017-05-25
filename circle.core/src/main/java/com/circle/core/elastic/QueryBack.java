package com.circle.core.elastic;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
/**
 * @author Created by fomky on 15-6-23.
 */
@SuppressWarnings("unused")
public class QueryBack<T> {
    public static final int START = 0;
    public static final int COUNT = 30;

    private List<T> list;
    private SortBuilder sort;
    private QueryBuilder query;
    private String id_name;
    private long totle;
    private int start=START;
    private int size=COUNT;
    private Class<T> tclass;
    private static Logger logger = LoggerFactory.getLogger(QueryBack.class);

    /**
     * Add one json object . if json error wile cont
     *
     * @param json 数据
     */
    public boolean addSource(String json,String id) {
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

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public SortBuilder getSort() {
        return sort;
    }

    public void setSort(SortBuilder sort) {
        this.sort = sort;
    }

    public String getId_name() {
        return id_name;
    }

    public void setId_name(String id_name) {
        this.id_name = id_name;
    }

    public QueryBuilder getQuery() {
        return query;
    }

    public void setQuery(QueryBuilder query) {
        this.query = query;
    }

    public long getTotle() {
        return totle;
    }

    public void setTotle(long totle) {
        this.totle = totle;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Class<T> getTclass() {
        return tclass;
    }

    public void setTclass(Class<T> tclass) {
        this.tclass = tclass;
    }
}
