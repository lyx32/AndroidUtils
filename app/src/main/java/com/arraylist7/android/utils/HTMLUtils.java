package com.arraylist7.android.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLUtils {
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式


    /**
     * 替换所有html标签保留标签内容（不包括script及style）
     *
     * @param htmlStr
     * @return
     */
    public static String replaceAllHTMLTag(String htmlStr) {
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签
        return htmlStr.trim(); // 返回文本字符串
    }

    /**
     * 基本功能：过滤指定标签
     *
     * @param str
     * @param tag               指定标签
     * @param isContainsContent 是否包含内容
     * @return String
     */
    public static String replaceHTMLTag(String str, String tag, boolean isContainsContent) {
        String patternString = "<\\s*" + tag + "\\s*([^>]*)>";
        if (isContainsContent)
            patternString = "<\\s*" + tag + "[^>]*?\\s*>.*?</\\s*" + tag + "\\s*>";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 找到html指定标签
     *
     * @param val               要查找的内容
     * @param tag               标签名
     * @param isContainsContent 是否包含内容
     * @return
     */
    public static List<String> findHtmlTag(String val, String tag, boolean isContainsContent) {
        return findHtmlTag(val, tag, null, null, isContainsContent);
    }

    /**
     * 根据标签和属性找到指定标签
     *
     * @param val               要查找的内容
     * @param tag               标签名
     * @param attrs             属性名（尽量可以的少）
     * @param attrVals          属性值
     * @param isContainsContent 是否包含内容
     * @return
     */
    public static List<String> findHtmlTag(String val, String tag, String[] attrs, String[] attrVals, boolean isContainsContent) {
        List<String> list = new ArrayList<>();
        StringBuffer patternString = new StringBuffer("<\\s*" + tag + "\\s*");
        if (StringUtils.len(attrs) > 0) {
            for (int i = 0; i < attrs.length; i++) {
                String attr = attrs[i];
                String attrVal = null;
                if (null != attrVals)
                    attrVal = attrVals[i];
                if (!StringUtils.isNullOrEmpty(attr))
                    patternString.append("[^<>]*?\\s+" + attr);
                if (null != attrVal)
                    patternString.append("=\\s*[\"']" + attrVal + "[\"']\\s*");
            }
        }
        if (isContainsContent)
            patternString.append("[^>]*?\\s*>.*?</\\s*" + tag + "\\s*>");
        else
            patternString.append("([^>]*)>");
        Pattern pattern = Pattern.compile(patternString.toString());
        Matcher matcher = pattern.matcher(val);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * 找到A标签
     *
     * @param val 要查找的内容
     * @return
     */
    public static List<String> findATag(String val) {
        return findHtmlTag(val, "a", false);
    }

    /**
     * 找到img标签
     *
     * @param val 要查找的内容
     * @return
     */
    public static List<String> findIMGTag(String val) {
        return findHtmlTag(val, "img", false);
    }

    /**
     * 找到Script标签
     *
     * @param val 要查找的内容
     * @return
     */
    public static List<String> findScriptTag(String val) {
        return findHtmlTag(val, "script", null, null, true);
    }

    /**
     * 找到input标签
     *
     * @param val 要查找的内容
     * @return
     */
    public static List<String> findInputTag(String val) {
        return findHtmlTag(val, "input", null, null, false);
    }


    /**
     * 通过指定属性及属性值找到input标签
     *
     * @param val 要查找的内容
     * @param attrs 指定的属性
     * @param attrVals 指定的值
     * @return
     */
    public static List<String> findInputTag(String val,String[] attrs,String[] attrVals) {
        return findHtmlTag(val, "input", attrs, attrVals, false);
    }

    /**
     * 提取img标签的src值
     *
     * @param val 要查找的内容
     * @return
     */
    public static List<String> filterImg_Src(String val) {
        return filterHtmlTag(val, "img", new String[]{"src"});
    }

    /**
     * 提取a标签的href值
     *
     * @param val 要查找的内容
     * @return
     */
    public static List<String> filterA_Href(String val) {
        return filterHtmlTag(val, "a", new String[]{"href"});
    }

    /**
     * 根据标签提取指定属性
     *
     * @param val   要查找的内容
     * @param tag   标签名
     * @param attrs 属性名（尽量可以的少）
     * @return
     */
    public static List<String> filterHtmlTag(String val, String tag, String[] attrs) {
        List<String> list = new ArrayList<>();
        String patternString = "<\\s*" + tag + "\\s*([^>]*)>";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(val);
        while (matcher.find()) {
            String htmlTag = (matcher.group());
            for (String attr : attrs) {
                Pattern attrPattern = Pattern.compile("\\s+" + attr + "\\s*=\\s*[\"'](.*?)[\"']");
                Matcher attrMatcher = attrPattern.matcher(htmlTag);
                if (attrMatcher.find() && attrMatcher.groupCount() > 1) {
                    list.add(attrMatcher.group(1));
                }
            }
        }
        return list;
    }
}
