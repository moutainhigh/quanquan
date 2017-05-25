package com.circle.netty.formation.ation.service;


import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.circle.netty.formation.message.model.Classify;
import com.circle.netty.formation.util.RedisTable;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CacheManager {
    public static Logger logger = LoggerFactory.getLogger(CacheManager.class);
    public static final String key_user_groups = "user_groups_set";
    public static Map<String,Set<String>> string_set = new HashedMap();
    public static Map<String,String> string_version = new HashedMap();
    private static final String  key_user_groups_version = "user_groups_version";
    public static BaseCache<List<String>> string_list = new BaseCache<>(99999);
    private static final String classifys_version = "classifys_version";
    public static Map<String, Classify> classifys = new HashMap<>();

    public static Set<String> findGroups(){
        if(groupVersionIsnew(key_user_groups_version)){
            return string_set.get(key_user_groups);
        }else {//如果不是新的,更新到新的 组信息
            return udpateGroups(key_user_groups);
        }
    }

    /**
     * 判断 缓存版本是否最新版本-如果是 返回 true
     * @return 最新 true , 不是最新 返回false
     */
    public static boolean groupVersionIsnew(String key){
        String cache_version = findGroupsversion(key);
        if(StringUtils.isEmpty(cache_version)){//读取最新version，写入缓存
            String version = Redis.shard.get(key);
            string_version.put(key_user_groups_version,version);
            return false;
        }else {
            String version = Redis.shard.get(key);
            boolean isnew = cache_version.equalsIgnoreCase(version);
            if(!isnew){
                string_version.put(key_user_groups_version,version);
            }
            return isnew;
        }
    }

    public static String findGroupsversion(String key) {
        return string_version.get(key);
    }

    public static Set<String> udpateGroups(String key){
        Set<String> list = Redis.shard.smembers(key);
        if(list==null&&list.isEmpty()) {
            logger.warn("没有编组队列");
            return null;
        }
        string_set.put(key,list);
        return list;
    }

    public static Classify findClassifyById(String id) {
        if(id==null ) return null;
        Map<String,Classify> map = checkclassifys_version();
        if(map.isEmpty()){
            map = updateClassfiy();
        }
        return map.get(id.trim());
    }

    private static Map<String, Classify> updateClassfiy() {
        //加载缓存
        Map<String, Classify> map = new HashMap<>();
        List<String> list = Redis.shard.lrange(RedisTable.classfiy_all, 0, -1);
        for (String value : list) {
            Classify classify = (Classify) Json.jsonParser(value, Classify.class);
            if (classify != null)
                map.put(classify.getCid(), classify);
        }
        classifys.putAll(map);
        return map;
    }

    private static Map<String, Classify> checkclassifys_version() {
        String ver = Redis.shard.get(classifys_version);
        if(ver==null){
            ver = String.valueOf(Redis.shard.incr(classifys_version));
        }
        String cache_ver = string_version.get(classifys_version);
        if(cache_ver==null||!cache_ver.equals(ver)){
            string_version.put(classifys_version,ver);
            return updateClassfiy();
        }
        return classifys;
    }
}