package com.circle.netty.formation.message.model.struct;


import com.circle.netty.formation.message.model.UserPacket;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @author Created by cxx on 15-7-29.
 */
@SuppressWarnings("unused")
public class PackedStruct {

    public static final String table = "art_packet";
    public static final byte[] table_byte = Bytes.toBytes(table);
    public static final byte[] family = Bytes.toBytes("info");
    public static final String uid = "uid";//用户ID
    public static final byte[] uid_byte = Bytes.toBytes(uid);//用户ID
    public static final String totle = "totle";//累计收入
    public static final byte[] totle_byte = Bytes.toBytes(totle);//累计收入
    public static final String cash = "cash";//余额
    public static final byte[] cash_byte = Bytes.toBytes(cash);//余额
    public static final String get = "get";//已经提现
    public static final byte[] get_byte = Bytes.toBytes(get);//已经提现
    public static final String precash = "precash";//即将入帐
    public static final byte[] precash_byte = Bytes.toBytes(precash);//即将入帐

    public static Put put(UserPacket packet){
        Put put =  new Put(Bytes.toBytes(packet.uid()));
        put.addColumn(family, cash_byte, Bytes.toBytes(packet.getCash()));
        put.addColumn(family, get_byte, Bytes.toBytes(packet.getGet()));
        put.addColumn(family, totle_byte, Bytes.toBytes(packet.getTotle()));
        put.addColumn(family, precash_byte, Bytes.toBytes(packet.getPrecash()));
        return put;
    }

}
