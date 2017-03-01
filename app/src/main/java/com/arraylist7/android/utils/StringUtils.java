package com.arraylist7.android.utils;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class StringUtils {

    private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static Pattern phone = Pattern.compile("1[3\\d|4[5|7]|5[0|1|5-9]|7[0|6|7|8]|8\\d]|\\d{8}");
    private final static SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");


    private final static Pattern URL = Pattern.compile("^(https|http)://.*?$(net|com|.com.cn|org|me|info|top|cn|cc|tv|)");


    private static DecimalFormat df = new DecimalFormat("#.##");
    private static DecimalFormat df2 = new DecimalFormat("#.00");

    StringUtils() {
    }


    /**
     * 取消小数
     *
     * @param val
     * @return
     */
    public static String doubleFormat0(double val) {
        return df.format(val);
    }

    /**
     * 保留2位小数。不足用0补位
     *
     * @param val
     * @return
     */
    public static String doubleFormat2(double val) {
        return df2.format(val);
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
        }else if(value.getClass().isArray()){
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

    public static boolean isPhone(Object value) {
        if (isNullOrEmpty(value))
            return false;
        return phone.matcher(value.toString()).matches();
    }

    public static boolean isUrl(String str) {
        if (str == null || str.trim().length() == 0)
            return false;
        return URL.matcher(str).matches();
    }

    public static boolean isEmail(Object email) {
        if (isNullOrEmpty(email))
            return false;
        return emailer.matcher(email.toString()).matches();
    }

    public static boolean isJSONString(String string) {
        if (isNullOrEmpty(string))
            return false;
        try {
            new JSONObject(string);
        } catch (Exception e) {
            try {
                new JSONArray(string);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static long random(int min, int max) {
        return Math.round(Math.random() * (max - min) + min);
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
        return decode(value,"UTF-8");
    }

    public static String decode(String value, String charsetName) {
        try {
            return URLDecoder.decode(value, charsetName);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("解码失败：" + value + "。charsetName=" + charsetName, e);
            return value;
        }
    }


    public static String getString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            while (null != (line = read.readLine())) {
                res.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(isr);
            IOUtils.close(read);
            IOUtils.close(is);
        }
        return res.toString();
    }


    public static String getDateTimeNow(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String friendly_time(String dateStr, int day) {
        if (1 > day)
            day = 1;
        Date time = toDate(dateStr);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();
        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1 && 1 <= day) {
            ftime = "昨天";
        } else if (days == 2 && 2 <= day) {
            ftime = "前天";
        } else if (days > 2 && days <= day) {
            ftime = days + "天前";
        } else if (days > day) {
            ftime = date.format(time);
        }
        return ftime;
    }

    public static int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month);
        a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        return friendly_time(sdate, 5);
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


    public static String trim(String value) {
        if (isNullOrEmpty(value))
            return "";
        return value.trim();
    }

    public static <T> T[] toArray(T... t) {
        return t;
    }

    public static <T> List<T> toList(T... args) {
        List<T> list = new ArrayList<T>(args.length);
        for (T t : args)
            list.add(t);
        return list;
    }

    public static <T> Map<String, T> toMap(T... args) {
        Map<String, T> map = new HashMap<String, T>();
        int i = 0;
        for (T t : args) {
            map.put(i + "", t);
        }
        return map;
    }

    public static String numToHex(int n) {
        String s = Integer.toHexString(n);
        return n <= 15 ? "0" + s : s;
    }

    public static int hexToNum(String hex) {
        return Integer.parseInt(hex, 16);
    }


    public static String unicodeToString(String str) {
    if(isNullOrEmpty(str)) return "";
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

    /**
     * 将内容中的关键字替换掉。主要用于处理json格式错误，网页展示问题，以及sql关键字
     * @param val
     * @return
     */
    public static String filterKeywords(String val){
        if (!isNullOrEmpty(val)) {
            if (val.matches("\\([^u])"))
                val = val.replace(val, "\\\\$1");
            if (val.contains("'"))
                val = val.replace("'", "\\u0027");
            if (val.contains("\r\n"))
                val = val.replace("\r\n", "\\u000a");
            if (val.contains("\n"))
                val = val.replace("\n", "\\u000a");
            if (val.contains("\r"))
                val = val.replace("\r", "\\u000a");
            if (val.contains("\t"))
                val = val.replace("\t", "　　");
            if (val.contains("\""))
                val = val.replace("\"", "\\u0022");
            if (val.contains("'"))
                val = val.replace("'", "\\u0027");
            if (val.contains("<"))
                val = val.replace("<", "\\u003c");
            if (val.contains(">"))
                val = val.replace(">", "\\u003e");
            if (val.contains("/"))
                val = val.replace("/", "\\u002f");
        }
        return val;
    }
}
