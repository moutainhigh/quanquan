package com.circle.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Created by cxx on 15-8-31.
 */
public class LocalUtil {

    public static InetAddress getInetAddress(){

        try{
            return InetAddress.getLocalHost();
        }catch(UnknownHostException e){
            System.out.println("unknown host!");
        }
        return null;

    }

    public static String getHostIp(){
        InetAddress netAddress = getInetAddress();
        if(null == netAddress){
            return null;
        }
        String ip = netAddress.getHostAddress(); //get the ip address
        return ip;
    }

    public static String getHostName(){
        InetAddress netAddress = getInetAddress();
        if(null == netAddress){
            return null;
        }
        String name = netAddress.getHostName(); //get the host address
        return name;
    }
}
