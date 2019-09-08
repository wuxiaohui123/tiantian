package com.yinhai.sysframework.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("all")
public class StringUtil extends StringUtils {

    private static final String FOLDER_SEPARATOR = "/";

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static final String TOP_PATH = "..";

    private static final String CURRENT_PATH = ".";

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        String lcStr = str.substring(0, prefix.length()).toLowerCase();
        String lcPrefix = prefix.toLowerCase();
        return lcStr.equals(lcPrefix);
    }

    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int pos = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            count++;
            pos = idx + sub.length();
        }
        return count;
    }

    public static String delete(String inString, String pattern) {
        return replace(inString, pattern, "");
    }

    public static String deleteAny(String inString, String charsToDelete) {
        if (inString == null || charsToDelete == null) {
            return inString;
        }
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                out.append(c);
            }
        }
        return out.toString();
    }

    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, '.');
    }

    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    public static String getFilename(String path) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
    }

    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath = newPath + FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        }
        return relativePath;
    }

    public static String cleanPath(String path) {
        String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
        String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
        List<String> pathElements = Lists.newLinkedList();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Arrays.stream(pathArray).forEach(s -> {
            if (CURRENT_PATH.equals(s)) {
                return;
            }
            if (TOP_PATH.equals(s)) {
                atomicInteger.getAndIncrement();
            } else if (atomicInteger.get() > 0) {
                atomicInteger.getAndDecrement();
            } else {
                pathElements.add(0, s);
            }
        });
        return collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
    }

    public static boolean pathEquals(String path1, String path2) {
        return cleanPath(path1).equals(cleanPath(path2));
    }

    public static Locale parseLocaleString(String localeString) {
        String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
        String language = parts.length > 0 ? parts[0] : "";
        String country = parts.length > 1 ? parts[1] : "";
        String variant = parts.length > 2 ? parts[2] : "";
        return language.length() > 0 ? new Locale(language, country, variant) : null;
    }

    public static String[] addStringToArray(String[] arr, String str) {
        String[] newArr = new String[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = str;
        return newArr;
    }

    public static String[] sortStringArray(String[] source) {
        if (source == null) {
            return new String[0];
        }
        Arrays.sort(source);
        return source;
    }

    public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
        return splitArrayElementsIntoProperties(array, delimiter, null);
    }

    public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter, String charsToDelete) {
        if (array == null || array.length == 0) {
            return null;
        }
        Properties result = new Properties();
        Arrays.stream(array).forEach(s -> {
            if (charsToDelete != null) {
                s = deleteAny(s, charsToDelete);
            }
            String[] splittedElement = split(s, delimiter);
            if (splittedElement != null) {
                result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
            }
        });
        return result;
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return (String[]) tokens.toArray(new String[tokens.size()]);
    }

    public static String[] delimitedListToStringArray(String str, String delimiter) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }
        List<String> result = Lists.newArrayList();
        int pos = 0;
        int delPos = 0;
        while ((delPos = str.indexOf(delimiter, pos)) != -1) {
            result.add(str.substring(pos, delPos));
            pos = delPos + delimiter.length();
        }
        if (str.length() > 0 && pos <= str.length()) {
            result.add(str.substring(pos));
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public static String[] commaDelimitedListToStringArray(String str) {
        return delimitedListToStringArray(str, ",");
    }

    public static Set<String> commaDelimitedListToSet(String str) {
        Set<String> set = Sets.newTreeSet();
        String[] tokens = commaDelimitedListToStringArray(str);
        Arrays.stream(tokens).forEach(s -> {
            set.add(s);
        });
        return set;
    }

    public static String arrayToDelimitedString(Object[] arr, String delim) {
        if (arr == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static String collectionToDelimitedString(Collection<String> coll, String delim, String prefix, String suffix) {
        if (coll == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Iterator<String> it = coll.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(prefix).append(it.next()).append(suffix);
            i++;
        }
        return sb.toString();
    }

    public static String collectionToDelimitedString(Collection<String> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    public static String arrayToCommaDelimitedString(Object[] arr) {
        return arrayToDelimitedString(arr, ",");
    }

    public static String collectionToCommaDelimitedString(Collection<String> coll) {
        return collectionToDelimitedString(coll, ",");
    }

    public static String encodePassword(String password, String algorithm) {
        byte[] unencodedPassword = password.getBytes();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            return password;
        }
        md.reset();
        md.update(unencodedPassword);
        byte[] encodedPassword = md.digest();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < encodedPassword.length; i++) {
            if ((encodedPassword[i] & 0xFF) < 16) {
                buf.append("0");
            }
            buf.append(Long.toString(encodedPassword[i] & 0xFF, 16));
        }
        return buf.toString();
    }

    public static String encodeString(String str) {
        Base64 encoder = new Base64();
        try {
            return ((String) encoder.encode(str)).trim();
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decodeString(String str) {
        return new String(new Base64().decode(str));
    }

    public static String toChinese(String strvalue) {
        try {
            if (strvalue == null) {
                return null;
            }
            return new String(strvalue.getBytes("ISO8859_1"), "GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final int compareTo(String szStr1, String szStr2) {
        return szStr1.compareTo(szStr2);
    }

    public static final String rightGBKBytePad(String str, int len, char pad) {
        try {
            byte[] bt = str.getBytes("GBK");
            return new String(bt) + StringUtils.rightPad("", len - bt.length, pad);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String leftGBKBytePad(String str, int len, char pad) {
        try {
            byte[] bt = str.getBytes("GBK");
            return StringUtils.leftPad("", len - bt.length, pad) + new String(bt);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPYString(String str) {
        StringBuffer tempStr = new StringBuffer("");
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= '!' && c <= '~') {
                tempStr.append(String.valueOf(c));
            } else {
                tempStr.append(getPYChar(String.valueOf(c)));
            }
        }
        return tempStr.toString();
    }

    public static String getPYChar(String c) {
        if (null == c || 0 == c.trim().length()) return c;
        byte[] array = String.valueOf(c).getBytes();
        if (2 > array.length) return c;
        int i = (short) (array[0] - 0 + 256) * 256 + (short) (array[1] - 0 + 256);
        if (i < 45217) return "*";
        if (i < 45253) return "a";
        if (i < 45761) return "b";
        if (i < 46318) return "c";
        if (i < 46826) return "d";
        if (i < 47010) return "e";
        if (i < 47297) return "f";
        if (i < 47614) return "g";
        if (i < 48119) return "h";
        if (i < 49062) return "j";
        if (i < 49324) return "k";
        if (i < 49896) return "l";
        if (i < 50371) return "m";
        if (i < 50614) return "n";
        if (i < 50622) return "o";
        if (i < 50906) return "p";
        if (i < 51387) return "q";
        if (i < 51446) return "r";
        if (i < 52218) return "s";
        if (i < 52698) return "t";
        if (i < 52980) return "w";
        if (i < 53689) return "x";
        if (i < 54481) return "y";
        if (i < 55290) return "z";
        return "*";
    }

    public static String delUrlParam(String url) {
        if (url != null) {
            int i = url.indexOf("?");
            if (i > 0) {
                url = url.substring(0, i);
            }
        }
        return url;
    }


}
