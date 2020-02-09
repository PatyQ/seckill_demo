package com.cy.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    /**
     * 获得当前时间的下N个小时的Date对象
     * @param n
     * @return
     */
    public static Date getNextNDate(int n){

        //获得日历对象 -- Calendar 日历  //getInstance 获得实例
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.HOUR_OF_DAY, n);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //获得日历对应的时间 ↑
        Date time = calendar.getTime();
        return time;
    }

    /**
     * 根据固定的格式, 返回时间字符串
     * @param date
     * @param pattern
     * @return
     */
    public static String date2String(Date date,String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
