package com.circle.netty.formation.util;

import com.circle.core.hbase.CHbase;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.schema.types.PDouble;
import org.apache.phoenix.schema.types.PInteger;

import java.io.IOException;

/**
 * Created by zhoujia on 2016/4/9.
 */
public class HBaseUtils {

    public static void increment(String table,String rowKey,String family,String column) throws IOException {
        CHbase bean = CHbase.bean();
        Get get = new Get(Bytes.toBytes(rowKey));
        Result result = bean.get(table, get);
        byte[] clfidValue = result.getValue(Bytes.toBytes(family), Bytes.toBytes(column.toUpperCase()));
        int o = 0;
        if(clfidValue != null) {
            o = (int) PInteger.INSTANCE.toObject(clfidValue);
        }
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(family),Bytes.toBytes(column.toUpperCase()), PInteger.INSTANCE.toBytes(o+1));
        CHbase.bean().put(table, put);
    }
    public static void incrementN(String table,String rowKey,String family,String column,double n) throws IOException {
        CHbase bean = CHbase.bean();
        Get get = new Get(Bytes.toBytes(rowKey));
        Result result = bean.get(table, get);
        byte[] clfidValue = result.getValue(Bytes.toBytes(family), Bytes.toBytes(column.toUpperCase()));
        double o = 0;
        if(clfidValue!=null){
            o = (double) PDouble.INSTANCE.toObject(clfidValue);
        }
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(family),Bytes.toBytes(column.toUpperCase()), PDouble.INSTANCE.toBytes(o+n));
        CHbase.bean().put(table, put);
    }
}
