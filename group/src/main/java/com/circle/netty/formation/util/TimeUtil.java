package com.circle.netty.formation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Created by cxx on 15-7-30.
 */
public class TimeUtil {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * 距离24点 还剩下多少秒
     */
    public static long timeEndToToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return (calendar.getTimeInMillis()-System.currentTimeMillis())/1000;
    }
    public static String formatLongToStr(Long longTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(longTime);
        return sdf.format(date);
    }

    public static String formatLongToStr(Long longTime, String type){
        if(type==null){
            return formatLongToStr( longTime);
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat(type);
            Date date = new Date(longTime);
            return sdf.format(date);
        }
    }


    /***
     * 这个时间是本年的第几周
     * @param timeLong 时间
     * @return
     */
    public static int weekOfYear(long timeLong){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeLong);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /***
     * 这个时间是本年的第几周
     * @param timeLong 时间
     * @return 返回类型为 2016_15 周 这样的类型
     */
    public static String weekOfYearAndWeek(long timeLong){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeLong);
        String yyyy = formatLongToStr(timeLong, "yyyy");
        return yyyy + "_" + calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /***
     * @param now 时间
     * @return 现在时间是当天的第几秒
     */
    public static long secondOfDay(long now){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return (now - calendar.getTimeInMillis())/1000;
    }



    public static void main(String[] args) throws ParseException {
        System.out.println(secondOfDay(System.currentTimeMillis()));
        System.out.println(timeEndToToday());
    }
}
