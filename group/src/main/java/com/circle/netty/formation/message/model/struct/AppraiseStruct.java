package com.circle.netty.formation.message.model.struct;

import com.circle.netty.formation.message.model.User;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @author Created by cxx on 15-8-10.
 */
public class AppraiseStruct {
    public static String table_ans = "CIRCLE.ACHECK";//
    public static String table_que =  "CIRCLE.QCHECK";//
    public static byte[] family = Bytes.toBytes("info");
    public static byte[] score = Bytes.toBytes("score");
    public static byte[] time = Bytes.toBytes("time");
    public static byte[] context = Bytes.toBytes("context");
    public static byte[] qid = Bytes.toBytes("qid");
    private static byte[] att = Bytes.toBytes("att");
    private static byte[] deep = Bytes.toBytes("deep");
    private static byte[] speed = Bytes.toBytes("speed");


    public static Put create_qcheck(User quser,String value, long date) {
        Put put = new Put(Bytes.toBytes(quser.uid()));
        put.addColumn(family, Bytes.toBytes(date), Bytes.toBytes(value));
        return put;
    }

    public static Put create_acheck(User quser, String value,long date) {
        Put put = new Put(Bytes.toBytes(quser.uid()));
        put.addColumn(family, Bytes.toBytes(date), Bytes.toBytes(value));
        return put;
    }
}
