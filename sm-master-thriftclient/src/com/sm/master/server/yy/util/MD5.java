package com.sm.master.server.yy.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created by Clicoy on 2015/2/8.
 */
public class MD5 {
    private static char hexDigits[] = {'0', '1', '2', '3', '4',
                        '5', '6', '7', '8', '9',
                        'a', 'b', 'c', 'd', 'e', 'f'};
    public static String SAFE_CODE = "UTF-8";
    public final static String encode(byte[] btInput) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] safecode(String str){
        try {
            return str.getBytes(SAFE_CODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String encode(String str){
        return encode(safecode(str));
    }

    public static boolean compare(String src,String md5){
        return encode(src).equals(md5);
    }
    public static String uuid(){
    	return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
