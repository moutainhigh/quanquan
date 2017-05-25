package com.circle.core.util;

import sun.security.x509.AlgorithmId;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created by Clicoy on 2015/2/8.
 */
public class CircleMD5 {
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
    public final static String encodeSha1(String string) {
        return encodeSha1(safecode(string));
    }
    public final static String encodeSha1(byte[] btInput) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA1");
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
//        try {
//            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
//            messageDigest.update(btInput);
//            return getFormattedText(messageDigest.digest());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//      }
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(hexDigits[(bytes[j] << 4) & 0x0f]);
            buf.append(hexDigits[bytes[j] & 0x0f]);
        }
        return buf.toString();
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
