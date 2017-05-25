package com.circle.netty.formation.ation.service;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

import java.util.Date;

/**
 * @author  chenxx
 */
public class BaseCache<T> extends GeneralCacheAdministrator {
    //过期时间(单位为秒);
    private int refreshPeriod;

    private static final long serialVersionUID = -4397192926052141162L;

    public BaseCache(int refreshPeriod){
        super();
        this.refreshPeriod = refreshPeriod;
    }

    //添加被缓存的对象;
    public void put(String key,T value){
        this.putInCache(key,value);
    }
    //删除被缓存的对象;
    public void remove(String key){
        this.flushEntry(key);
    }
    //删除所有被缓存的对象;
    public void removeAll(Date date){
        this.flushAll(date);
    }

    public void removeAll(){
        this.flushAll();
    }
    //获取被缓存的对象;
    public T get(String key) throws NeedsRefreshException {
        try{
            return (T) this.getFromCache(key,this.refreshPeriod);
        } catch (NeedsRefreshException e) {
            this.cancelUpdate(key);
            throw e;
        }
    }
    public T getremove(String key){
        try{
            return (T) this.getFromCache(key,this.refreshPeriod);
        } catch (NeedsRefreshException e) {
            this.remove(key);
            return null;
        }
    }
}