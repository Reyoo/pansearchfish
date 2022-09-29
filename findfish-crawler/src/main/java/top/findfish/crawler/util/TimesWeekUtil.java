package top.findfish.crawler.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * 传入一个任意日期 格式如yyyy-MM-dd 返回当周时间范围
 */
public class TimesWeekUtil {


    public static final String sdf2 = "yyyy-MM-dd";


    /**
     * 指定时间计算有几周，并返回每周起止日期
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return week：第几周 start：每周开始时间 end：每周结束时间
     */
    public static List<Map<String, String>> getWeekOfDate(String start, String end) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map;
        Date startDate = stringToDate(start, sdf2);
        Date endDate = stringToDate(end, sdf2);

        assert endDate != null;
        assert startDate != null;
        long howLong = howLong("d", startDate, endDate);
        int weekNum = Math.toIntExact(howLong / 7);
        if (howLong % 7 > 0) {
            weekNum++;
        }

        Calendar cal = Calendar.getInstance();
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(startDate);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);

        if(weekNum ==0){
            weekNum =1;
        }


        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        for (int i = 1; i <= weekNum; i++) {
            map = new HashMap<>();
            map.put("week", String.valueOf(i));

            if (i == 1) {//第一周以 start 开始
                LocalDate parse = LocalDate.parse(dateFormat(startDate, sdf2), DateTimeFormatter.ISO_DATE);
                int minDay = dayWeek - 2;

                LocalDate localDate = parse.minusDays(  Long.valueOf(minDay));
                String format = localDate.format(DateTimeFormatter.ISO_DATE);
                map.put("start", format);
            } else {
                map.put("start", dateFormat(cal.getTime(), sdf2));
            }

            if (i == weekNum) {//最后一周以 end 结束
                map.put("end", dateFormat(endDate, sdf2));
            } else {
                //设置这周的周日日期，1代表周日，取值范围1~7，设置1~7之外会从周日开始往前后推算，负前正后，DAY_OF_WEEK的日期变更范围只会是在当前日期的周
                cal.set(Calendar.DAY_OF_WEEK, 1);
                map.put("end", dateFormat(cal.getTime(), sdf2));
            }
            list.add(map);

            //调用 org.apache.commons.lang.time.DateUtils 包下的方法
            //新增一天到下一周的开始日期
//            cal.setTime(DateUtils.addDays(cal.getTime(), 1));
            LocalDateTime localDateTime = cal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1L);
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            cal.setTime(Date.from(zonedDateTime.toInstant()));


        }

        return list;
    }


    /**
     * 将date类型转为String类型
     *
     * @param date 时间
     * @param type 格式
     * @return
     */
    public static String dateFormat(Date date, String type) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        return sdf.format(date);
    }


    /**
     * 将Sting类型转为date类型
     *
     * @param str  待转换的字符
     * @param date 时间类型，如：yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String str, String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(date);
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 计算时间差 (时间单位,开始时间,结束时间)
     *
     * @param unit  时间单位 s - 秒,m - 分,h - 时,d - 天 ,M - 月 y - 年
     * @param begin 开始时间
     * @param end   结束时间
     * @return
     */
    public static long howLong(String unit, Date begin, Date end) {
        long ltime = begin.getTime() - end.getTime() < 0 ? end.getTime() - begin.getTime()
                : begin.getTime() - end.getTime();
        if (unit.equals("s")) {
            return ltime / 1000;// 返回秒
        } else if (unit.equals("m")) {
            return ltime / 60000;// 返回分钟
        } else if (unit.equals("h")) {
            return ltime / 3600000;// 返回小时
        } else if (unit.equals("d")) {
            return ltime / 86400000;// 返回天数
        } else if (unit.equals("y")) {
            long res = ltime / 86400000;
            return res / 365;
        } else if (unit.equals("M")) {
            long res = ltime / 86400000;
            return res / 30;
        } else {
            return 0;
        }
    }

}

