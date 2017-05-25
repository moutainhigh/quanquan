package com.circle.netty.formation.message.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.schema.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * @author Created by Fomky on 2015/8/22 0022.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BaseLog<T> {
    private static final Logger logger = LoggerFactory.getLogger(BaseLog.class);
    private static final String rep_get = "get";
    private static final String rep_set = "set";
    public static final byte[] family = Bytes.toBytes("0");
    private static final String notin = "GETCLASS";

    //Modified by chenxx 2015年10月10日09:30:41
    //添加 年 月 日 时 分 秒 六个字段
    //  GYEAR GMONTH GDAY GHOUR GMINUTE GSEC
    // 对应格式 2014 201403 20140312 2014031212 201403121259 20140312125959

    public static final byte[] GYEAR = Bytes.toBytes("GYEAR");
    public static final byte[] GMONTH = Bytes.toBytes("GMONTH");
    public static final byte[] GDAY = Bytes.toBytes("GDAY");
    public static final byte[] GHOUR = Bytes.toBytes("GHOUR");
    public static final byte[] GMINUTE = Bytes.toBytes("GMINUTE");
    public static final byte[] GSEC = Bytes.toBytes("GSEC");

    public static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
    public static final SimpleDateFormat month = new SimpleDateFormat("yyyyMM");
    public static final SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat hour = new SimpleDateFormat("yyyyMMddHH");
    public static final SimpleDateFormat minute = new SimpleDateFormat("yyyyMMddHHmm");
    public static final SimpleDateFormat seconds = new SimpleDateFormat("yyyyMMddHHmmss");

    @JsonIgnore
    public T base;
    @JsonIgnore
    public Class<T> classs;

    public Put createPut(String id,long time){
        Put put = createPut(id);
        return addtime(put,time);
    }

    public Put addtime(Put put,long time){
        if(put==null)
            return null;
        put.addColumn(family,GYEAR, Bytes.toBytes(year.format(time)));
        put.addColumn(family,GMONTH, Bytes.toBytes(month.format(time)));
        put.addColumn(family,GDAY, Bytes.toBytes(day.format(time)));
        put.addColumn(family,GHOUR, Bytes.toBytes(hour.format(time)));
        put.addColumn(family,GMINUTE, Bytes.toBytes(minute.format(time)));
        put.addColumn(family, GSEC, Bytes.toBytes(seconds.format(time)));
        return put;
    }

    public T create(Result result){
        baseisnull();
        Method[] methods = classs.getMethods();
        String name;
        byte[] value;
        Class[] cs;
        Class c;
        for (Method method : methods) {
            name = method.getName();
            if(name.startsWith(rep_set)){
                name = name.replace(rep_set, "").toUpperCase();
                cs  = method.getParameterTypes();
                if(cs!=null&&cs.length==1){
                    value = result.getValue(family, Bytes.toBytes(name));
                    Object obj = null;
                    c = cs[0];
                    if(value!=null){
                        if (Integer.TYPE == c) {
                            obj = PInteger.INSTANCE.toObject(value);
                        } else if (Integer.class == c) {
                            obj = PInteger.INSTANCE.toObject(value);
                        } else if (Boolean.TYPE == c) {
                            obj = PBoolean.INSTANCE.toObject(value);
                        } else if (Boolean.class == c) {
                            obj = PBoolean.INSTANCE.toObject(value);
                        } else if (Double.TYPE == c) {
                            obj = PDouble.INSTANCE.toObject(value);
                        } else if (Double.class == c) {
                            obj = PDouble.INSTANCE.toObject(value);
                        } else if (Float.class == c) {
                            obj = PFloat.INSTANCE.toObject(value);
                        } else if (Float.TYPE == c) {
                            obj = PFloat.INSTANCE.toObject(value);
                        } else if (Long.class == c) {
                            obj = PLong.INSTANCE.toObject(value);
                        } else if (Long.TYPE == c) {
                            obj = PLong.INSTANCE.toObject(value);
                        } else if (String.class == c) {
                            obj = PVarchar.INSTANCE.toObject(value);
                        } else if (BigDecimal.class == c) {
                            obj = PDecimal.INSTANCE.toObject(value);
                        }
                        if(obj!=null){
                            try {
                                method.invoke(base,obj);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                logger.error(e.getMessage(),e);
                            }
                        }
                    }
                }

            }
        }
        return base;
    }

    private void baseisnull() {
        if(base==null){
            try {
                base = classs.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }


    public Put createPut(String id){
        Put put = new Put(Bytes.toBytes(id));
        Method[] methods = classs.getMethods();
        String name;
        for (Method method : methods) {
            name = method.getName();
            if (!notin.equals(name) && name.startsWith(rep_get)) {
                byte[] value = null;
                Object obj = null;
                try {
                    obj = method.invoke(base);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error(e.getMessage(),e);
                }
                name = name.replace(rep_get, "").toUpperCase();
                if (obj != null) {
                    if (Integer.TYPE == method.getReturnType()) {
                        value = PInteger.INSTANCE.toBytes(obj);
                    } else if (Integer.class == method.getReturnType()) {
                        value = PInteger.INSTANCE.toBytes(obj);
                    } else if (Boolean.TYPE == method.getReturnType()) {
                        value = PBoolean.INSTANCE.toBytes(obj);
                    } else if (Boolean.class == method.getReturnType()) {
                        value = PBoolean.INSTANCE.toBytes(obj);
                    } else if (Double.TYPE == method.getReturnType()) {
                        value = PDouble.INSTANCE.toBytes(obj);
                    } else if (Double.class == method.getReturnType()) {
                        value = PDouble.INSTANCE.toBytes(obj);
                    } else if (Float.class == method.getReturnType()) {
                        value = PFloat.INSTANCE.toBytes(obj);
                    } else if (Float.TYPE == method.getReturnType()) {
                        value = PFloat.INSTANCE.toBytes(obj);
                    } else if (Long.class == method.getReturnType()) {
                        value = PLong.INSTANCE.toBytes(obj);
                    } else if (Long.TYPE == method.getReturnType()) {
                        value = PLong.INSTANCE.toBytes(obj);
                    } else if (String.class == method.getReturnType()) {
                        value = PVarchar.INSTANCE.toBytes(obj);
                    } else if (BigDecimal.class == method.getReturnType()) {
                        value = PDecimal.INSTANCE.toBytes(obj);
                    }
                    if(value!=null){
                        put.addColumn(family, Bytes.toBytes(name),value);
                    }
                }
            }
        }
        return put;
    }
    public void create(){
        Method[] methods = classs.getMethods();
        String name;
        Class[] cs;
        Class c;
        for (Method method : methods) {
            name = method.getName().toUpperCase();
            if(name.startsWith(rep_set)){
                name = name.replace(rep_set, "");
                cs  = method.getParameterTypes();
                if(cs!=null&&cs.length==1){
                    c = cs[0];
                        if (Integer.TYPE == c) {
                            System.out.println( name + " INTEGER,");
                        } else if (Integer.class == c) {
                            System.out.println( name + " INTEGER,");
                        } else if (Boolean.TYPE == c) {
                            System.out.println( name + " BOOLEAN,");
                        } else if (Boolean.class == c) {
                            System.out.println( name + " BOOLEAN,");
                        } else if (Double.TYPE == c) {
                            System.out.println( name + " DOUBLE,");
                        } else if (Double.class == c) {
                            System.out.println( name + " DOUBLE,");
                        } else if (Float.class == c) {
                            System.out.println( name + " FLOAT,");
                        } else if (Float.TYPE == c) {
                            System.out.println( name + " FLOAT,");
                        } else if (Long.class == c) {
                            System.out.println( name + " BIGINT,");
                        } else if (Long.TYPE == c) {
                            System.out.println( name + " BIGINT,");
                        } else if (String.class == c) {
                            System.out.println( name + " VARCHAR(50),");
                        } else if (BigDecimal.class == c) {
                            System.out.println( name + " DECIMAL,");
                        }
                    }
                }

        }
    }
}
