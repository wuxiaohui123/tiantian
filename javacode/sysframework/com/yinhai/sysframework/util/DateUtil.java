package com.yinhai.sysframework.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.yinhai.sysframework.exception.SysLevelException;

public class DateUtil extends DateUtils {

    public static final String TIMESTAMPFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMEFORMAT = "HH:mm:ss";
    public static final String DATEFORMAT = "yyyy-MM-dd";
    public static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final int COMPUTE_YEAR = 1;
    public static final int COMPUTE_MONTH = 2;
    public static final int COMPUTE_DAY = 5;

    public static String getFullCurrentTime() {
        return DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static Date getCurrentDateTime() {
        return new Date(System.currentTimeMillis());
    }

    public static int getWeekOfYear() {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static String getCurDateTime() {
        return new SimpleDateFormat(TIMESTAMPFORMAT).format(Calendar.getInstance().getTime());
    }

    public static Date getCurDate() {
        return java.sql.Date.valueOf(new SimpleDateFormat(DATEFORMAT).format(Calendar.getInstance().getTime()));
    }

    public static java.sql.Date getDate() {
        Calendar oneCalendar = Calendar.getInstance();
        return getDate(oneCalendar.get(Calendar.YEAR), oneCalendar.get(Calendar.MONTH) + 1, oneCalendar.get(Calendar.DATE));
    }

    public static java.sql.Date getDate(int yyyy, int MM, int dd) {
        if (!verityDate(yyyy, MM, dd)) {
            throw new IllegalArgumentException("This is illegimate date!");
        }
        Calendar oneCalendar = Calendar.getInstance();
        oneCalendar.clear();
        oneCalendar.set(yyyy, MM - 1, dd);
        return new java.sql.Date(oneCalendar.getTime().getTime());
    }

    public static java.sql.Date getDate(String year, String month, String day) {
        return getDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
    }

    public static Date getDate(int month, int day, int year, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.set(year, month - 1, day, hour, minute, second);
        } catch (Exception e) {
            return null;
        }
        return new Date(calendar.getTime().getTime());
    }

    public static Date getDate(String month, String day, String year, String hour, String minute, String second) {
        return getDate(Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(year), Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));
    }

    public static boolean verityDate(int yyyy, int MM, int dd) {
        boolean flag = false;
        if (MM >= 1 && MM <= 12 && dd >= 1 && dd <= 31) {
            if (MM == 4 || MM == 6 || MM == 9 || MM == 11) {
                if (dd <= 30) {
                    flag = true;
                }
            } else if (MM == 2) {
                if ((yyyy % 100 != 0 && yyyy % 4 == 0) || yyyy % 400 == 0) {
                    if (dd <= 29) {
                        flag = true;
                    }
                } else if (dd <= 28) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(DATEFORMAT).format(date);
    }

    public static String datetimeToString(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(DATETIMEFORMAT).format(date);
    }

    public static String datetimeToString(Date date, String format) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    public static String timeToString(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(TIMEFORMAT).format(date);
    }

    public static Date stringToDate(String strDate, String srcDateFormat, String dstDateFormat) {
        Date rtDate = null;
        Date tmpDate = new SimpleDateFormat(srcDateFormat).parse(strDate, new ParsePosition(0));
        String tmpString = null;
        if (tmpDate != null) {
            tmpString = new SimpleDateFormat(dstDateFormat).format(tmpDate);
        }
        if (tmpString != null) {
            rtDate = new SimpleDateFormat(dstDateFormat).parse(tmpString, new ParsePosition(0));
        }
        return rtDate;
    }

    public static Date stringToDate(String strDate, String srcDateFormat) {
        return new SimpleDateFormat(srcDateFormat).parse(strDate, new ParsePosition(0));
    }

    public static Date stringToDate(String strDate) {
        return stringToDate(strDate, DATEFORMAT);
    }

    public static java.sql.Date utilDateToSqlDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public static Time utilDateToSqlTime(Date date) {
        if (date == null) {
            return null;
        }
        return new Time(date.getTime());
    }

    public static Timestamp utilDateToSqlTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    public static java.sql.Date stringToSqlDate(String strDate) {
        return stringToSqlDate(strDate, DATEFORMAT);
    }

    public static java.sql.Date stringToSqlDate(String strDate, String srcDateFormat) {
        return stringToSqlDate(strDate, srcDateFormat, DATEFORMAT);
    }

    public static java.sql.Date stringToSqlDate(String strDate, String srcDateFormat, String dstDateFormat) {
        return utilDateToSqlDate(stringToDate(strDate, srcDateFormat, dstDateFormat));
    }

    public static Time stringToSqlTime(String strDate) {
        return stringToSqlTime(strDate, TIMEFORMAT);
    }

    public static Time stringToSqlTime(String strDate, String srcDateFormat) {
        return stringToSqlTime(strDate, srcDateFormat, TIMEFORMAT);
    }

    public static Time stringToSqlTime(String strDate, String srcDateFormat, String dstDateFormat) {
        return utilDateToSqlTime(stringToDate(strDate, srcDateFormat, dstDateFormat));
    }

    public static Timestamp stringToSqlTimestamp(String strDate) {
        return stringToSqlTimestamp(strDate, TIMESTAMPFORMAT);
    }

    public static Timestamp stringToSqlTimestamp(String strDate, String srcDateFormat) {
        return stringToSqlTimestamp(strDate, srcDateFormat, TIMESTAMPFORMAT);
    }

    public static Timestamp stringToSqlTimestamp(String strDate, String srcDateFormat, String dstDateFormat) {
        return utilDateToSqlTimestamp(stringToDate(strDate, srcDateFormat, dstDateFormat));
    }

    public static boolean isYear(String year) {
        if (year == null)
            return false;
        if (year.length() != 4)
            return false;
        if (!StringUtil.isNumeric(year))
            return false;
        return true;
    }

    public static boolean isMonth(String month) {
        if (month == null)
            return false;
        if (month.length() != 1 && month.length() != 2)
            return false;
        if (!StringUtil.isNumeric(month))
            return false;
        int iMonth = Integer.parseInt(month);
        return (iMonth >= 1 && iMonth <= 12);
    }

    public static boolean isDay(String day) {
        if (day == null)
            return false;
        if (day.length() != 1 && day.length() != 2)
            return false;
        if (!StringUtil.isNumeric(day))
            return false;
        int iDay = Integer.parseInt(day);
        return (iDay >= 1) && (iDay <= 31);
    }

    public static boolean isHour(String hour) {
        if (hour == null)
            return false;
        if (hour.length() != 1 && hour.length() != 2)
            return false;
        if (!StringUtil.isNumeric(hour))
            return false;
        int iHour = Integer.parseInt(hour);
        return (iHour >= 0) && (iHour <= 23);
    }

    public static boolean isDate(String year, String month, String day) {
        if (!isYear(year))
            return false;
        if (!isMonth(month))
            return false;
        if (!isDay(day))
            return false;
        return verityDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
    }

    public static boolean isDate(String date) {
        if (StringUtil.isEmpty(date))
            return false;
        char divide = '-';
        int index = date.indexOf(divide);
        if (index < 0)
            return false;
        String year = date.substring(0, index);
        int index2 = date.indexOf(divide, index + 1);
        if (index2 < 0)
            return false;
        return isDate(year, date.substring(index + 1, index2), date.substring(index2 + 1));
    }

    private static int compute(String arg1, String arg2, int computeFlag, String format, boolean bExact) {
        SimpleDateFormat sdf = (format != null && format.trim().length() > 0) ? new SimpleDateFormat(format) : new SimpleDateFormat(DATEFORMAT);
        try {
            Date date1 = sdf.parse(arg1);
            Date date2 = sdf.parse(arg2);
            return compute(date1, date2, computeFlag, bExact);
        } catch (Exception e) {
            throw new SysLevelException(e.getMessage());
        }
    }

    private static int compute(Date arg1, Date arg2, int computeFlag, boolean bExact) {
        Calendar ca1 = Calendar.getInstance();
        ca1.setTime(arg1);
        ca1.set(Calendar.HOUR_OF_DAY, 0);
        ca1.set(Calendar.MINUTE, 0);
        ca1.set(Calendar.SECOND, 0);
        ca1.set(Calendar.MILLISECOND, 0);
        Calendar ca2 = Calendar.getInstance();
        ca2.setTime(arg2);
        ca2.set(Calendar.HOUR_OF_DAY, 0);
        ca2.set(Calendar.MINUTE, 0);
        ca2.set(Calendar.SECOND, 0);
        ca2.set(Calendar.MILLISECOND, 0);
        int elapsed = 0;
        if (ca1.after(ca2)) {
            while (ca1.after(ca2)) {
                ca1.add(computeFlag, -1);
                elapsed--;
            }
            if (bExact) {
                if (2 == computeFlag && ca1.get(Calendar.DATE) != ca2.get(Calendar.DATE)) {
                    elapsed += 1;
                }
                if (1 == computeFlag) {
                    if (ca1.get(Calendar.MONTH) != ca2.get(Calendar.MONTH)) {
                        elapsed += 1;
                    } else if (ca1.get(Calendar.DATE) != ca2.get(Calendar.DATE)) {
                        elapsed += 1;
                    }
                }
            }
            return -elapsed;
        }
        if (ca1.before(ca2)) {
            while (ca1.before(ca2)) {
                ca1.add(computeFlag, 1);
                elapsed++;
            }
            if (bExact) {
                if (2 == computeFlag && ca1.get(Calendar.DATE) != ca2.get(Calendar.DATE)) {
                    elapsed -= 1;
                }
                if (1 == computeFlag) {
                    if (ca1.get(Calendar.MONTH) > ca2.get(Calendar.MONTH)) {
                        elapsed -= 1;
                    } else if (ca1.get(Calendar.DATE) != ca2.get(Calendar.DATE)) {
                        elapsed -= 1;
                    }
                }
            }
            return -elapsed;
        }
        return 0;
    }

    public static int computeYear(String arg1, String arg2, String format, boolean bExact) {
        return compute(arg1, arg2, 1, format, bExact);
    }

    public static int computeYear(Date arg1, Date arg2, boolean bExact) {
        return compute(arg1, arg2, 1, bExact);
    }

    public static int computeMonth(String arg1, String arg2, String format, boolean bExact) {
        return compute(arg1, arg2, 2, format, bExact);
    }

    public static int computeMonth(Date arg1, Date arg2, boolean bExact) {
        return compute(arg1, arg2, 2, bExact);
    }

    public static int computeDay(String arg1, String arg2, String format) {
        return compute(arg1, arg2, 5, format, true);
    }

    public static int computeDay(Date arg1, Date arg2) {
        return compute(arg1, arg2, 5, true);
    }

    public static final String fnGetStr4Y2M(String szStr) {
        return szStr.replaceAll("[ \\|\\-:\\.]", "").substring(0, 6);
    }

    public static int getIntervalMonth(Date startDate, Date endDate) {
        return computeMonthOnly(startDate, endDate);
    }

    public static int getIntervalDay(java.sql.Date startDate, java.sql.Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / 86400000L);
    }

    public static int computeMonthOnly(Date dateBegin, Date dateEnd) {
        Calendar ca1 = Calendar.getInstance();
        ca1.setTime(dateBegin);
        ca1.set(Calendar.DATE, 1);
        ca1.set(Calendar.HOUR_OF_DAY, 0);
        ca1.set(Calendar.MINUTE, 0);
        ca1.set(Calendar.SECOND, 0);
        ca1.set(Calendar.MILLISECOND, 0);
        Calendar ca2 = Calendar.getInstance();
        ca2.setTime(dateEnd);
        ca2.set(Calendar.DATE, 1);
        ca2.set(Calendar.HOUR_OF_DAY, 0);
        ca2.set(Calendar.MINUTE, 0);
        ca2.set(Calendar.SECOND, 0);
        ca2.set(Calendar.MILLISECOND, 0);
        return compute(ca1.getTime(), ca2.getTime(), 2, true);
    }

    public static int computeDateOnly(Date dateBegin, Date dateEnd) {
        Calendar ca1 = Calendar.getInstance();
        ca1.setTime(dateBegin);
        ca1.set(Calendar.HOUR_OF_DAY, 0);
        ca1.set(Calendar.MINUTE, 0);
        ca1.set(Calendar.SECOND, 0);
        ca1.set(Calendar.MILLISECOND, 0);
        Calendar ca2 = Calendar.getInstance();
        ca2.setTime(dateEnd);
        ca2.set(Calendar.HOUR_OF_DAY, 0);
        ca2.set(Calendar.MINUTE, 0);
        ca2.set(Calendar.SECOND, 0);
        ca2.set(Calendar.MILLISECOND, 0);
        return compute(ca1.getTime(), ca2.getTime(), 5, true);
    }

    public static int computeDateOnly(Date dateBegin, Date dateEnd, boolean bWith) {
        int value = computeDateOnly(dateBegin, dateEnd);
        if (bWith) {
            if (dateBegin.after(dateEnd)) {
                value++;
            } else if (dateBegin.before(dateEnd)) {
                value--;
            } else {
                value = 1;
            }
        }
        return value;
    }

    public static Date nowDate() {
        return new Date();
    }

    /*
     * 毫秒转化
     */
    public static String formatTimeToDayHourMinSecond(long ms) {
        long day = ms / (1000 * 60 * 60 * 24);
        long hour = (ms - day * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minute = (ms - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60);
        long second = (ms - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;
        long milliSecond = ms - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - minute * (1000 * 60) - second * 1000;
        String strDay = day < 10 ? "0" + day : "" + day; // 天
        String strHour = hour < 10 ? "0" + hour : "" + hour;// 小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;// 分钟
        String strSecond = second < 10 ? "0" + second : "" + second;// 秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;// 毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
        return strDay + "天 " + strHour + "小时 " + strMinute + "分钟 " + strSecond + "秒 " + strMilliSecond + "毫秒";
    }

}
