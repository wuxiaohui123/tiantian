package com.yinhai.sysframework.util;


import com.yinhai.sysframework.dto.ParamDTO;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ValidateUtil {

    public static final String module = ValidateUtil.class.getName();

    public static final boolean defaultEmptyOK = true;

    public static final String digits = "0123456789";

    public static final String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";

    public static final String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String whitespace = " \t\n\r";

    public static final String decimalCommaDelimiter = ",";

    public static final String decimalPointDelimiter = ".";

    public static final String decimalColonDelimiter = ":";

    public static final String decimalSpritDelimiter = "/";

    public static final String phoneNumberDelimiters = "()- ";

    public static final String validUSPhoneChars = "0123456789()- ";

    public static final String validWorldPhoneChars = "0123456789()- +";

    public static final String SSNDelimiters = "- ";

    public static final String validSSNChars = "0123456789- ";

    public static final int digitsInSocialSecurityNumber = 9;

    public static final int digitsInUSPhoneNumber = 10;

    public static final int digitsInUSPhoneAreaCode = 3;

    public static final int digitsInUSPhoneMainNumber = 7;

    public static final String ZipCodeDelimiters = "-";

    public static final String ZipCodeDelimeter = "-";

    public static final String validZipCodeChars = "0123456789-";

    public static final int digitsInZipCode1 = 5;

    public static final int digitsInZipCode2 = 9;

    public static final String creditCardDelimiters = " -";

    public static final int[] daysInMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static final String USStateCodeDelimiter = "|";

    public static final String USStateCodes = "AL|AK|AS|AZ|AR|CA|CO|CT|DE|DC|FM|FL|GA|GU|HI|ID|IL|IN|IA|KS|KY|LA|ME|MH|MD|MA|MI|MN|MS|MO|MT|NE|NV" +
            "|NH|NJ|NM|NY|NC|ND|MP|OH|OK|OR|PW|PA|PR|RI|SC|SD|TN|TX|UT|VT|VI|VA|WA|WV|WI|WY|AE|AA|AE|AE|AP";

    public static final String ContiguousUSStateCodes = "AL|AZ|AR|CA|CO|CT|DE|DC|FL|GA|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM" +
            "|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY";

    public static boolean areEqual(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        }
        return obj.equals(obj2);
    }

    public static boolean areEqualIgnoreCase(String obj, String obj2) {
        if (obj == null) {
            return obj2 == null;
        }
        return obj.equalsIgnoreCase(obj2);
    }

    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).length() == 0;
        } else if (value instanceof Collection) {
            return ((Collection) value).size() == 0;
        } else if (value instanceof Map) {
            return ((Map) value).size() == 0;
        } else return value instanceof String[] && ((String[]) value).length == 0;
    }

    public static boolean isEmpty(String s) {
        return (s == null) || (s.length() == 0);
    }

    public static boolean isEmptyValue(ParamDTO dto, String key) {
        return isEmpty(dto.get(key));
    }

    public static boolean isNotEmptyValue(ParamDTO dto, String key) {
        return isNotEmpty(dto.get(key));
    }

    public static boolean isEmptyValue(Map dto, String key) {
        return isEmpty(dto.get(key));
    }

    public static boolean isNotEmptyValue(Map dto, String key) {
        return isNotEmpty(dto.get(key));
    }

    public static boolean isEmpty(Collection c) {
        return (c == null) || (c.size() == 0);
    }

    public static boolean isNotEmpty(String s) {
        return (s != null) && (s.length() > 0);
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isNotEmpty(Collection c) {
        return (c != null) && (c.size() > 0);
    }

    public static boolean isString(Object obj) {
        return (obj instanceof String);
    }

    public static boolean isWhitespace(String s) {
        if (isEmpty(s)) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (" \t\n\r".indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    public static String stripCharsInBag(String s, String bag) {
        StringBuffer returnString = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (bag.indexOf(c) == -1) returnString.append(c);
        }
        return returnString.toString();
    }

    public static String stripCharsNotInBag(String s, String bag) {
        StringBuffer returnString = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (bag.indexOf(c) != -1) returnString.append(c);
        }
        return returnString.toString();
    }

    public static String stripWhitespace(String s) {
        return stripCharsInBag(s, " \t\n\r");
    }

    public static boolean charInString(char c, String s) {
        return s.indexOf(c) != -1;
    }

    public static String stripInitialWhitespace(String s) {
        int i = 0;
        while ((i < s.length()) && (charInString(s.charAt(i), " \t\n\r"))) i++;
        return s.substring(i);
    }

    public static boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    public static boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    public static boolean isLetterOrDigit(char c) {
        return Character.isLetterOrDigit(c);
    }

    public static boolean isInteger(String s) {
        if (isEmpty(s)) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLong(String s) {
        if (isEmpty(s)) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSignedInteger(String s) {
        if (isEmpty(s)) return true;
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSignedLong(String s) {
        if (isEmpty(s)) return true;
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isPositiveInteger(String s) {
        if (isEmpty(s)) return true;
        try {
            long temp = Long.parseLong(s);

            return temp > 0L;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isNonnegativeInteger(String s) {
        if (isEmpty(s)) return true;
        try {
            int temp = Integer.parseInt(s);

            return temp >= 0;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isNegativeInteger(String s) {
        if (isEmpty(s)) return true;
        try {
            int temp = Integer.parseInt(s);

            return temp < 0;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isNonpositiveInteger(String s) {
        if (isEmpty(s)) return true;
        try {
            int temp = Integer.parseInt(s);

            return temp <= 0;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isFloat(String s) {
        if (isEmpty(s)) {
            return true;
        }
        boolean seenDecimalPoint = false;
        if (s.startsWith(".")) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ".".charAt(0)) {
                if (!seenDecimalPoint) {
                    seenDecimalPoint = true;
                } else {
                    return false;
                }
            } else if (!isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSignedFloat(String s) {
        if (isEmpty(s)) return true;
        try {
            return Float.parseFloat(s) <= 0.0F;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isSignedDouble(String s) {
        if (isEmpty(s)) return true;
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isAlphabetic(String s) {
        if (isEmpty(s)) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphanumeric(String s) {
        if (isEmpty(s)) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSSN(String s) {
        if (isEmpty(s)) {
            return true;
        }
        String normalizedSSN = stripCharsInBag(s, SSNDelimiters);
        return (isInteger(normalizedSSN)) && (normalizedSSN.length() == 9);
    }

    public static boolean isUSPhoneNumber(String s) {
        if (isEmpty(s)) return true;
        String normalizedPhone = stripCharsInBag(s, phoneNumberDelimiters);
        return (isInteger(normalizedPhone)) && (normalizedPhone.length() == 10);
    }

    public static boolean isUSPhoneAreaCode(String s) {
        if (isEmpty(s)) return true;
        String normalizedPhone = stripCharsInBag(s, phoneNumberDelimiters);
        return (isInteger(normalizedPhone)) && (normalizedPhone.length() == 3);
    }

    public static boolean isUSPhoneMainNumber(String s) {
        if (isEmpty(s)) return true;
        String normalizedPhone = stripCharsInBag(s, phoneNumberDelimiters);
        return (isInteger(normalizedPhone)) && (normalizedPhone.length() == 7);
    }

    public static boolean isInternationalPhoneNumber(String s) {
        if (isEmpty(s)) {
            return true;
        }
        return isPositiveInteger(stripCharsInBag(s, phoneNumberDelimiters));
    }

    public static boolean isZipCode(String s) {
        if (isEmpty(s)) {
            return true;
        }
        String normalizedZip = stripCharsInBag(s, "-");
        return (isInteger(normalizedZip)) && ((normalizedZip.length() == 5) || (normalizedZip.length() == 9));
    }

    public static boolean isContiguousZipCode(String s) {
        boolean retval = false;
        if (isZipCode(s)) {
            if (isEmpty(s)) {
                retval = true;
            } else {
                String normalizedZip = s.substring(0, 5);
                int iZip = Integer.parseInt(normalizedZip);
                retval = ((iZip < 96701) || (iZip > 96898)) && ((iZip < 99501) || (iZip > 99950));
            }
        }
        return retval;
    }

    public static boolean isStateCode(String s) {
        if (isEmpty(s)) return true;
        return USStateCodes.contains(s) && !s.contains(USStateCodeDelimiter);
    }

    public static boolean isContiguousStateCode(String s) {
        if (isEmpty(s)) return true;
        return ContiguousUSStateCodes.contains(s) && !s.contains(USStateCodeDelimiter);
    }

    public static boolean isEmail(String s) {
        if (isEmpty(s)) {
            return true;
        }
        if (isWhitespace(s)) {
            return false;
        }
        int i = 1;
        int sLength = s.length();
        while ((i < sLength) && (s.charAt(i) != '@')) {
            i++;
        }
        return (i < sLength - 1) && (s.charAt(i) == '@');
    }

    public static boolean isUrl(String s) {
        if (isEmpty(s)) return true;
        return s.contains("://");
    }

    public static boolean isYear(String s) {
        if (isEmpty(s)) {
            return true;
        }
        if (!isNonnegativeInteger(s)) return false;
        return s.length() == 2 || s.length() == 4;
    }

    public static boolean isIntegerInRange(String s, int a, int b) {
        if (isEmpty(s)) {
            return true;
        }
        if (!isSignedInteger(s)) {
            return false;
        }
        int num = Integer.parseInt(s);
        return (num >= a) && (num <= b);
    }

    public static boolean isMonth(String s) {
        if (isEmpty(s)) return true;
        return isIntegerInRange(s, 1, 12);
    }

    public static boolean isDay(String s) {
        if (isEmpty(s)) return true;
        return isIntegerInRange(s, 1, 31);
    }

    public static int daysInFebruary(int year) {
        return (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)) ? 29 : 28;
    }

    public static boolean isHour(String s) {
        if (isEmpty(s)) return true;
        return isIntegerInRange(s, 0, 23);
    }

    public static boolean isMinute(String s) {
        if (isEmpty(s)) return true;
        return isIntegerInRange(s, 0, 59);
    }

    public static boolean isSecond(String s) {
        if (isEmpty(s)) return true;
        return isIntegerInRange(s, 0, 59);
    }

    public static boolean isDate(String year, String month, String day) {
        if ((!isYear(year)) || (!isMonth(month)) || (!isDay(day))) {
            return false;
        }
        int intMonth = Integer.parseInt(month);
        int intDay = Integer.parseInt(day);
        if (intDay > daysInMonth[(intMonth - 1)]) return false;
        return (intMonth != 2) || (intDay <= daysInFebruary(Integer.parseInt(year)));
    }

    public static boolean isDate(String date) {
        if (isEmpty(date)) {
            return true;
        }
        int dateSlash1 = date.indexOf("-");
        int dateSlash2 = date.lastIndexOf("-");
        if ((dateSlash1 <= 0) || (dateSlash1 == dateSlash2)) return false;
        return isDate(date.substring(0, dateSlash1), date.substring(dateSlash1 + 1, dateSlash2), date.substring(dateSlash2 + 1));
    }

    public static boolean isTime(String hour, String minute, String second) {
        return (isHour(hour)) && (isMinute(minute)) && (isSecond(second));
    }

    public static boolean isTime(String time) {
        if (isEmpty(time)) {
            return true;
        }
        int timeColon1 = time.indexOf(":");
        int timeColon2 = time.lastIndexOf(":");
        if (timeColon1 <= 0) return false;
        String hour = time.substring(0, timeColon1);
        String minute;
        String second;
        if (timeColon1 == timeColon2) {
            minute = time.substring(timeColon1 + 1);
            second = "0";
        } else {
            minute = time.substring(timeColon1 + 1, timeColon2);
            second = time.substring(timeColon2 + 1);
        }
        return isTime(hour, minute, second);
    }

    public static boolean isValueLinkCard(String stPassed) {
        if (isEmpty(stPassed)) return true;
        String st = stripCharsInBag(stPassed, " -");
        return (st.length() == 16) && ((st.startsWith("7")) || (st.startsWith("6")));
    }

    public static boolean isGiftCard(String stPassed) {
        return isValueLinkCard(stPassed);
    }

    public static int getLuhnSum(String stPassed) {
        stPassed = stPassed.replaceAll("\\D", "");
        int len = stPassed.length();
        int sum = 0;
        int mul = 1;
        for (int i = len - 1; i >= 0; i--) {
            int digit = Character.digit(stPassed.charAt(i), 10);
            digit *= (mul == 1 ? mul++ : mul--);
            sum += (digit >= 10 ? digit % 10 + 1 : digit);
        }
        return sum;
    }

    public static int getLuhnCheckDigit(String stPassed) {
        int sum = getLuhnSum(stPassed);
        int mod = ((sum / 10 + 1) * 10 - sum) % 10;
        return 10 - mod;
    }

    public static boolean sumIsMod10(int sum) {
        return sum % 10 == 0;
    }

    public static String appendCheckDigit(String stPassed) {
        String checkDigit = Integer.valueOf(getLuhnCheckDigit(stPassed)).toString();
        return stPassed + checkDigit;
    }

    public static boolean isCreditCard(String stPassed) {
        if (isEmpty(stPassed)) return true;
        String st = stripCharsInBag(stPassed, " -");
        if (st.length() > 19) return false;
        return sumIsMod10(getLuhnSum(st));
    }

    public static boolean isVisa(String cc) {
        if ((cc.length() == 16 || cc.length() == 13) && cc.substring(0, 1).equals("4")) return isCreditCard(cc);
        return false;
    }

    public static boolean isMasterCard(String cc) {
        int firstdig = Integer.parseInt(cc.substring(0, 1));
        int seconddig = Integer.parseInt(cc.substring(1, 2));
        if (cc.length() == 16 && firstdig == 5 && seconddig >= 1 && seconddig <= 5) return isCreditCard(cc);
        return false;
    }

    public static boolean isAmericanExpress(String cc) {
        int firstdig = Integer.parseInt(cc.substring(0, 1));
        int seconddig = Integer.parseInt(cc.substring(1, 2));
        if ((cc.length() == 15) && (firstdig == 3) && ((seconddig == 4) || (seconddig == 7))) return isCreditCard(cc);
        return false;
    }

    public static boolean isDinersClub(String cc) {
        int firstdig = Integer.parseInt(cc.substring(0, 1));
        int seconddig = Integer.parseInt(cc.substring(1, 2));
        if ((cc.length() == 14) && (firstdig == 3) && ((seconddig == 0) || (seconddig == 6) || (seconddig == 8))) return isCreditCard(cc);
        return false;
    }

    public static boolean isCarteBlanche(String cc) {
        return isDinersClub(cc);
    }

    public static boolean isDiscover(String cc) {
        String first4digs = cc.substring(0, 4);
        if ((cc.length() == 16) && (first4digs.equals("6011"))) return isCreditCard(cc);
        return false;
    }

    public static boolean isEnRoute(String cc) {
        String first4digs = cc.substring(0, 4);
        if ((cc.length() == 15) && ((first4digs.equals("2014")) || (first4digs.equals("2149")))) return isCreditCard(cc);
        return false;
    }

    public static boolean isJCB(String cc) {
        String first4digs = cc.substring(0, 4);
        if ((cc.length() == 16) && ((first4digs.equals("3088")) || (first4digs.equals("3096")) || (first4digs.equals("3112")) || (first4digs.equals("3158")) || (first4digs.equals("3337")) || (first4digs.equals("3528")))) {
            return isCreditCard(cc);
        }
        return false;
    }

    public static boolean isAnyCard(String ccPassed) {
        if (isEmpty(ccPassed)) {
            return true;
        }
        String cc = stripCharsInBag(ccPassed, " -");
        if (!isCreditCard(cc)) return false;
        return (isMasterCard(cc)) || (isVisa(cc)) || (isAmericanExpress(cc)) || (isDinersClub(cc)) || (isDiscover(cc)) || (isEnRoute(cc)) || (isJCB(cc));
    }

    public static String getCardType(String ccPassed) {
        if (isEmpty(ccPassed)) return "Unknown";
        String cc = stripCharsInBag(ccPassed, " -");
        if (!isCreditCard(cc)) {
            return "Unknown";
        }
        if (isMasterCard(cc)) return "MasterCard";
        if (isVisa(cc)) return "Visa";
        if (isAmericanExpress(cc)) return "AmericanExpress";
        if (isDinersClub(cc)) return "DinersClub";
        if (isDiscover(cc)) return "Discover";
        if (isEnRoute(cc)) return "EnRoute";
        if (isJCB(cc)) return "JCB";
        return "Unknown";
    }

    public static boolean isCardMatch(String cardType, String cardNumberPassed) {
        if (isEmpty(cardType)) return true;
        if (isEmpty(cardNumberPassed)) return true;
        String cardNumber = stripCharsInBag(cardNumberPassed, " -");
        if (cardType.equalsIgnoreCase("VISA") && isVisa(cardNumber)) return true;
        if ((cardType.equalsIgnoreCase("MASTERCARD")) && (isMasterCard(cardNumber))) return true;
        if (((cardType.equalsIgnoreCase("AMERICANEXPRESS")) || (cardType.equalsIgnoreCase("AMEX"))) && (isAmericanExpress(cardNumber))) return true;
        if ((cardType.equalsIgnoreCase("DISCOVER")) && (isDiscover(cardNumber))) return true;
        if ((cardType.equalsIgnoreCase("JCB")) && (isJCB(cardNumber))) return true;
        if (((cardType.equalsIgnoreCase("DINERSCLUB")) || (cardType.equalsIgnoreCase("DINERS"))) && (isDinersClub(cardNumber))) return true;
        if ((cardType.equalsIgnoreCase("CARTEBLANCHE")) && (isCarteBlanche(cardNumber))) return true;
        return (cardType.equalsIgnoreCase("ENROUTE")) && (isEnRoute(cardNumber));
    }

    /**
     * 大陆号码或香港号码均可
     */
    public static boolean isPhoneLegal(String str) throws PatternSyntaxException {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        return Pattern.compile("^((13[0-9])|(15[0-3, 5-9])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$").matcher(str).matches();
    }

    /**
     * 香港手机号码8位数，5|6|8|9开头+7位任意数
     */
    public static boolean isHKPhoneLegal(String str) throws PatternSyntaxException {
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        return Pattern.compile("^(5|6|8|9)\\d{7}$").matcher(str).matches();
    }

}
