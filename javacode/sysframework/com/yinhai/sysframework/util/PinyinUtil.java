package com.yinhai.sysframework.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Pattern;

public class PinyinUtil {

    public static String converterToFirstSpell(String chines, boolean UPPERCASE) {
        if (!Pattern.matches("[\\u4e00-\\u9fa5]", chines)) {
            return chines;
        }
        String pinyinName = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType((UPPERCASE) ? HanyuPinyinCaseType.UPPERCASE : HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : chines.toCharArray()) {
            try {
                String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat);
                pinyinName = (pinyin != null) ? (pinyinName + pinyin[0].charAt(0)) : (pinyinName + c);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return pinyinName;
    }

    public static String converterToSpell(String chines, boolean UPPERCASE) {
        if (Pattern.matches("[\\u4e00-\\u9fa5]", chines)) {
            return chines;
        }
        String pinyinName = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType((UPPERCASE) ? HanyuPinyinCaseType.UPPERCASE : HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : chines.toCharArray()) {
            try {
                String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat);
                pinyinName = (pinyin != null) ? (pinyinName + pinyin[0].charAt(0)) : (pinyinName + c);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return pinyinName;
    }
}
