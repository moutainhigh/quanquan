package com.circle.core.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.List;

/**
 * @author Created by clicoy on 15-6-10.
 */
public class CHbase {

    private HConnection CONNECTION;
    private Configuration HBASE_CONFIG;
    private static CHbase cHbase;

    public HConnection connection() {
        return CONNECTION;
    }

    public Configuration hbase_config() {
        return HBASE_CONFIG;
    }

    private CHbase() {
    }
    public static void instance() throws IOException {
        cHbase = new CHbase();
        cHbase.inital();
    }
    public static void instance(String config_path) throws IOException {
        cHbase = new CHbase();
        cHbase.inital(config_path);
    }

    private void inital(String config_path) throws IOException {
        HBASE_CONFIG = HBaseConfiguration.create();
        HBASE_CONFIG.addResource(new Path(config_path));
        CONNECTION = HConnectionManager.createConnection(HBASE_CONFIG);
    }

    public static final CHbase bean(){
        return cHbase;
    }

    public void inital() throws IOException {
        HBASE_CONFIG = HBaseConfiguration.create();
        CONNECTION = HConnectionManager.createConnection(HBASE_CONFIG);
    }

    /* ====Hbase 操作 开始=============================================== */
    public Result get(String table, Get get) throws IOException {
        HTableInterface htable = null;
        try {
            htable = htable(table);
            Result result = htable.get(get);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (htable != null)
                htable.close();
        }
    }

    public ResultScanner scan(String tableName, Scan scan) throws IOException {
        HTableInterface htable = null;
        try {
            htable = htable(tableName);
            ResultScanner result = htable.getScanner(scan);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (htable != null)
                htable.close();
        }
    }

    public void put(String tableName, Put put) throws IOException {
        HTableInterface htable = null;
        try {
            htable = htable(tableName);
            htable.put(put);
            htable.flushCommits();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (htable != null)
                htable.close();
        }
    }
    public void put(byte[] tableName, Put put) throws IOException {
        HTableInterface htable = null;
        try {
            htable = htable(tableName);
            htable.put(put);
            htable.flushCommits();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (htable != null)
                htable.close();
        }
    }

    public void put(String tableName, List<Put> puts)
            throws IOException {
        HTableInterface htable = null;
        try {
            htable = htable(tableName);
            htable.put(puts);
            htable.flushCommits();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (htable != null)
                htable.close();
        }
    }


    /**
     * 拿到Htable 的操作类
     *
     * @param tableName
     * @return
     */
    public HTableInterface htable(String tableName) {
        try {
            return CONNECTION.getTable(tableName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public HTableInterface htable(byte[] tableName) {
        try {
            return CONNECTION.getTable(tableName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	/* ====Hbase 操作 结束=============================================== */
}
