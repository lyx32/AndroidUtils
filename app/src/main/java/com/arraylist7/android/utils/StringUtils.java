package com.arraylist7.android.utils;

import android.graphics.Color;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    private final static SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    private final static Pattern URL = Pattern.compile("^(https|http)://.*?$(net|com|.com.cn|org|me|info|top|cn|cc|tv|)");


    StringUtils() {
    }

    public static String trim(Object val) {
        if (isNullOrEmpty(val))
            return "";
        return val.toString().trim();
    }

    public static boolean isInt(Object obj) {
        if (!isNullOrEmpty(obj)) {
            return obj.toString().matches("^\\d+$");
        }
        return false;
    }

    public static boolean isDouble(Object obj, boolean hasDecimal) {
        if (!isNullOrEmpty(obj)) {
            String regex = "^\\d+$";
            if (hasDecimal)
                regex = "^\\d+(\\.\\d+)?$";
            return obj.toString().matches(regex);
        }
        return false;
    }

    public static int getInt(Object obj, int def) {
        if (!isNullOrEmpty(obj))
            obj = obj.toString().trim();
        if (isInt(obj)) {
            return Integer.parseInt(obj.toString());
        } else {
            String val = obj.toString().replaceAll("\\D", "");
            if (isInt(val))
                return Integer.parseInt(val);
        }
        return def;
    }

    public static long getLong(Object obj, long def) {
        if (!isNullOrEmpty(obj))
            obj = obj.toString().trim();
        if (isInt(obj)) {
            return Long.parseLong(obj.toString());
        } else {
            String val = obj.toString().replaceAll("\\D", "");
            if (isInt(val))
                return Long.parseLong(val);
        }
        return def;
    }

    public static double getDouble(Object obj, double def) {
        if (!isNullOrEmpty(obj))
            obj = obj.toString().trim();
        if (isDouble(obj, false)) {
            return Integer.parseInt(obj.toString());
        }
        if (isDouble(obj, true)) {
            return Double.parseDouble(obj.toString());
        }
        return def;
    }

    public static float getFloat(Object obj, float def) {
        if (!isNullOrEmpty(obj))
            obj = obj.toString().trim();
        if (isDouble(obj, false)) {
            return Integer.parseInt(obj.toString());
        }
        if (isDouble(obj, true)) {
            return Float.parseFloat(obj.toString());
        }
        return def;
    }

    public static boolean equals(Object left, Object right) {
        if (isAllNullOrEmpty(left, right)) return true;
        if (!isAllNotNullOrEmpty(left, right)) return false;
        if (left instanceof String)
            return left.toString().equalsIgnoreCase(right.toString());
        return left.equals(right);
    }


    public static boolean isAllEquals(int[] arrays) {
        if (null == arrays || 0 == arrays.length)
            return true;
        for (int i = 1; i < arrays.length; i++) {
            if (arrays[0] != arrays[i])
                return false;
        }
        return true;
    }

    public static boolean isAllEquals(double[] arrays) {
        if (null == arrays || 0 == arrays.length)
            return true;
        for (int i = 1; i < arrays.length; i++) {
            if (arrays[0] != arrays[i])
                return false;
        }
        return true;
    }

    public static boolean isAllEquals(String[] arrays) {
        if (null == arrays || 0 == arrays.length)
            return true;
        if (null == arrays[0])
            throw new NullPointerException("第一个字符串不能为null！");
        if (null == arrays[1])
            return false;
        for (int i = 1; i < arrays.length; i++) {
            if (!arrays[0].equals(arrays[i]))
                return false;
        }
        return true;
    }

    public static <T> T get(List<T> list, int index) {
        if (list.size() > index)
            return list.get(index);
        if (0 != StringUtils.len(list) && list.get(0) instanceof String)
            return (T) "";
        return null;
    }


    public static boolean contains(Object left, Object right) {
        if (isAllNullOrEmpty(left, right)) return true;
        if (!isAllNotNullOrEmpty(left, right)) return false;
        if (left instanceof String)
            return left.toString().contains(right.toString());
        if (left instanceof Collection)
            return ((Collection) left).contains(right);
        return left.equals(right);
    }

    public static int len(Object value) {
        if (null == value)
            return 0;
        if (value instanceof Map) {
            return ((Map) value).keySet().size();
        } else if (value instanceof Dictionary) {
            return ((Dictionary) value).size();
        } else if (value instanceof Collection) {
            return ((Collection) value).size();
        } else if (value instanceof Iterable) {
            int j = 0;
            Iterator i = ((Iterable) value).iterator();
            while (null != i.next()) {
                j++;
            }
            return j;
        } else if (value.getClass().isArray()) {
            return Array.getLength(value);
        } else {
            return value.toString().length();
        }
    }

    /**
     * 格式化
     *
     * @param val
     * @param formart # 表示有则显示没有则不显示 0 表示有就显示没有则显示0
     * @return
     */
    public static String format(double val, String formart) {
        return new DecimalFormat(formart).format(val);
    }

    public static String subString(String str, int start, int end) {
        if (isNullOrEmpty(str) || start > end)
            return "";
        if (start < 0)
            start = 0;
        int l = len(str);
        if (end > l)
            end = l;
        if (start == end)
            return str.charAt(start) + "";
        return str.substring(start, end);
    }

    public static boolean startsWith(String left, String right) {
        if (isAllNullOrEmpty(left, right))
            return true;
        if (null == left)
            throw new NullPointerException("left 参数不能为空！");
        if (null == right)
            return false;
        return left.startsWith(right);
    }

    public static boolean startsWith(String left, String right, int charLen) {
        if (null == left)
            throw new NullPointerException("left 参数不能为空！");
        if (null == right)
            return false;
        if (!left.startsWith(right))
            return false;
        if (charLen > left.length())
            charLen = left.length();
        String nl = left.substring(0, charLen);
        String nr = right.length() > charLen ? right.substring(0, charLen) : right;
        return nl.equals(nr);
    }

    public static boolean endsWith(String left, String right) {
        if (isAllNullOrEmpty(left, right))
            return true;
        if (null == left)
            throw new NullPointerException("left 参数不能为空！");
        if (null == right)
            return false;
        return left.endsWith(right);
    }

    public static boolean endsWith(String left, String right, int charLen) {
        if (null == left)
            throw new NullPointerException("left 参数不能为空！");
        if (null == right)
            return false;
        if (!left.endsWith(right))
            return false;
        if (charLen > left.length())
            charLen = left.length();
        String nl = left.substring(left.length() - charLen, charLen);
        String nr = right.length() > charLen ? right.substring(right.length() - charLen, charLen) : right;
        return nl.equals(nr);
    }


    public static int min(int... ints) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] < min)
                min = ints[i];
        }
        return min;
    }

    public static int max(int... ints) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] > max)
                max = ints[i];
        }
        return max;
    }

    public static <T> boolean in(T t, T... ts) {
        boolean isIn = false;
        for (T item_t : ts) {
            if (item_t instanceof String) {
                if (item_t.equals(t)) {
                    isIn = true;
                    break;
                }
            } else {
                if (t == item_t) {
                    isIn = true;
                    break;
                }
            }
        }
        return isIn;
    }

    public static String join(String[] arrays, String separator) {
        if (StringUtils.isNullOrEmpty(arrays))
            return "";
        StringBuffer sb = new StringBuffer(separator.length() * arrays.length * 5);
        for (String item : arrays) {
            if (null == item)
                item = "";
            sb.append(item + separator);
        }
        if (sb.length() > separator.length())
            sb.setLength(sb.length() - separator.length());
        return sb.toString();
    }


    public static boolean isNullOrEmpty(Object value) {
        if (null == value) {
            return true;
        } else if (value instanceof Map) {
            return ((Map) value).isEmpty();
        } else if (value instanceof Dictionary) {
            return ((Dictionary) value).isEmpty();
        } else if (value instanceof Iterable) {
            return !((Iterable) value).iterator().hasNext();
        } else if (value.getClass().isArray()) {
            return 0 == Array.getLength(value);
        } else {
            return "".equals(value.toString());
        }
    }

    public static boolean isAllNullOrEmpty(Object... values) {
        boolean isAllNullOrEmpty = true;
        for (Object obj : values) {
            if (!isNullOrEmpty(obj)) {
                isAllNullOrEmpty = false;
                break;
            }
        }
        return isAllNullOrEmpty;
    }

    public static boolean isAllNotNullOrEmpty(Object... values) {
        boolean isAllNotNullOrEmpty = true;
        for (Object obj : values) {
            if (isNullOrEmpty(obj)) {
                isAllNotNullOrEmpty = false;
                break;
            }
        }
        return isAllNotNullOrEmpty;
    }

    /**
     * 补位
     *
     * @param val        要补位的val
     * @param isLeft     true 从前面补，false 从后面补
     * @param fillLength 补位数量
     * @param fill       补位字符
     * @return
     */
    public static String fillValue(String val, boolean isLeft, int fillLength, String fill) {
        if (isNullOrEmpty(val)) return "";
        int end = val.length();
        int allLen = fillLength + end + fill.length();
        StringBuffer fillString = new StringBuffer(allLen);
        for (int i = 0; i < fillLength; i++) {
            fillString.append(fill);
        }
        return isLeft ? fillString.append(val).toString() : val + fillString.toString();
    }

    public static boolean isUrl(String str) {
        if (str == null || str.trim().length() == 0)
            return false;
        return URL.matcher(str).matches();
    }

    /**
     * 使用sdk自带json库判断是否是json对象（验证比较严格）
     *
     * @param string
     * @return
     */
    public static boolean isJSONObject(String string) {
        if (isNullOrEmpty(string))
            return false;
        try {
            new JSONObject(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 使用sdk自带json库判断是否是json数据（验证比较严格）
     *
     * @param string
     * @return
     */
    public static boolean isJSONArray(String string) {
        if (isNullOrEmpty(string))
            return false;
        try {
            new JSONArray(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 使用sdk自带json库判断是否是json字符串（验证比较严格）
     *
     * @param string
     * @return
     */
    public static boolean isJSONString(String string) {
        if (isNullOrEmpty(string))
            return false;
        if (isJSONObject(string))
            return true;
        if (isJSONArray(string))
            return true;
        return false;
    }


    public static <T> List<T> asList(T... t) {
        List<T> list = new ArrayList<>();
        if (null == t)
            return list;
        for (T item : t) {
            list.add(item);
        }
        return list;
    }

    public static <T> List<T> asList(Set<T> t) {
        if (null == t)
            return new ArrayList<T>();
        return asList((T[]) t.toArray());
    }

    public static <T> T[] asArray(T... t) {
        return t;
    }

    /**
     * 将 T 转换成map ，key 从0开始递增
     *
     * @param args
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> asMap(T... args) {
        Map<String, T> map = new HashMap<String, T>();
        int i = 0;
        if (null != args) {
            for (T t : args) {
                map.put(i + "", t);
                i++;
            }
        }
        return map;
    }

    /**
     * 将key，val转换为map
     *
     * @param key
     * @param val
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> asMap(String key, T val) {
        Map<String, T> map = new HashMap<String, T>();
        map.put(key, val);
        return map;
    }

    public static long random(int min, int max) {
        return Math.round(Math.random() * (max - min) + min);
    }

    public static double random(float min, float max) {
        return Math.round(Math.random() * (max - min) + min);
    }

    public static int randomColor() {
        int r = (int) StringUtils.random(0, 255);
        int g = (int) StringUtils.random(0, 255);
        int b = (int) StringUtils.random(0, 255);
        return Color.rgb(r, g, b);
    }

    public static String encodeBase64(byte[] b) {
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    public static byte[] decodeBase64(String base64String) throws Exception {
        return Base64.decode(base64String.getBytes("UTF-8"), Base64.NO_WRAP);
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String byteToHex(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexToByte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static String md5(String code, int length) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(code.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            if (16 == length)
                md5 = buf.toString().substring(8, 24);
            else
                md5 = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String md5Hex(String password) {
        return md5Hex(password.getBytes());
    }

    public static String md5Hex(byte[] datas) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(datas);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
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
            return "";
        }
    }

    public static String encode(String value) {
        return encode(value, "UTF-8");
    }

    public static String encode(String value, String charsetName) {
        try {
            return URLEncoder.encode(value, charsetName);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("编码失败：" + value + "。charsetName=" + charsetName, e);
            return value;
        }
    }

    public static String decode(String value) {
        return decode(value, "UTF-8");
    }

    public static String decode(String value, String charsetName) {
        try {
            return URLDecoder.decode(value, charsetName);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("解码失败：" + value + "。charsetName=" + charsetName, e);
            return value;
        }
    }


    public static String getDateTime(Calendar calendar, String format) {
        return new SimpleDateFormat(format).format(calendar.getTime());
    }

    /**
     * 获取时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTimeNow() {
        return getDateTimeNow("yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateTimeNow(String format) {
        return new SimpleDateFormat(format).format(Calendar.getInstance(Locale.CHINA).getTime());
    }

    /**
     * 以友好的方式显示时间
     *
     * @param dateStr
     * @return
     */
    public static String friendly_time(String dateStr) {
        Date time = toDate(dateStr);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();
        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        int months = (days / 30) + (days % 30 > 0 ? 1 : 0) + days > 30 ? 0 : -1;
        int year = (months / 12) + (months % 12 > 0 ? 1 : 0) + months > 12 ? 0 : -1;
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && months == 0) {
            ftime = days + "天前";
        } else if (months > 1 && months < 12) {
            ftime = months + "个月前";
        } else if (year > 1) {
            ftime = year + "年前";
        } else {
            ftime = date.format(time);
        }
        return ftime;
    }

    /**
     * 获取某年某月有多少天
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month);
        a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
        return a.get(Calendar.DATE);
    }

    public static Date toDate(String dateStr) {
        try {
            return datetime.parse(dateStr);
        } catch (ParseException e) {
            try {
                return date.parse(dateStr);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }


    public static String numToHex(int n) {
        String s = Integer.toHexString(n);
        return n <= 15 ? "0" + s : s;
    }

    public static int hexToNum(String hex) {
        return Integer.parseInt(hex, 16);
    }


    public static String unicodeToString(String str) {
        if (isNullOrEmpty(str)) return "";
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static String stringToUnicode(String s) {
        try {
            StringBuffer out = new StringBuffer("");
            byte[] bytes = s.getBytes("unicode");
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                out.append(Integer.toHexString(bytes[i] & 0xff)).append(str);
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return s;
        }
    }
}
