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
    private static final Pattern CONTENT_ATTR_PATTERN = Pattern.compile(">([^<]*?)<");

    /**
     * html 编码
     *
     * @param html
     * @return
     */
    public static String htmlDecoding(String html) {
        return html.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&amp;", "&");
    }

    /**
     * html 解码
     *
     * @param html
     * @return
     */
    public static String htmlEncoding(String html) {
        return html.replace("<", "&lt;")
                .replace("<", "&gt;")
                .replace("\"", "&quot;")
                .replace("&", "&amp;");
    }


    /**
     * 清楚html标签保留内容
     *
     * @param htmlStr
     * @return
     */
    public static String clearHTMLTag(String htmlStr) {
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
     * @param html
     * @param tag            指定标签
     * @param isClearContent 是否清理标签内容
     * @return String
     */
    public static String clearHTMLTag(String html, String tag, boolean isClearContent) {
        String patternString = "<\\s*" + tag + "\\s*([^>]*)>";
        if (isClearContent)
            patternString = "<\\s*" + tag + "\\s*([^>]*)>[\\s\\S]*?</\\s*" + tag + "\\s*>";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(html);
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
     * @param html              要查找的内容
     * @param tag               标签名
     * @param isContainsContent 是否包含内容
     * @return
     */
    public static List<String> findHtmlTag(String html, String tag, boolean isContainsContent) {
        return findHtmlTag(html, tag, "", "", isContainsContent);
    }


    /**
     * 根据标签和属性找到指定标签
     *
     * @param html              要查找的内容
     * @param tag               标签名
     * @param attr              属性名（尽量可以的少）
     * @param attrVal           属性值（与attrs一一对应）
     * @param isContainsContent 是否包含内容（注：如果你要find的tag下还有该同名tag，那么数据则会不完整）
     * @return
     */
    public static List<String> findHtmlTag(String html, String tag, String attr, String attrVal, boolean isContainsContent) {
        return findHtmlTag(html, tag, new String[]{attr}, new String[]{attrVal}, isContainsContent);
    }

    /**
     * 根据标签和属性找到指定标签
     *
     * @param html              要查找的内容
     * @param tag               标签名
     * @param attrs             属性名（尽量可以的少）
     * @param attrVals          属性值（与attrs一一对应）
     * @param isContainsContent 是否包含内容（注：如果你要find的tag下还有该同名tag，那么数据则会不完整）
     * @return
     */
    public static List<String> findHtmlTag(String html, String tag, String[] attrs, String[] attrVals, boolean isContainsContent) {
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
                if (!StringUtils.isNullOrEmpty(attrVal))
                    patternString.append("=\\s*[\"']" + attrVal + "[\"']\\s*");
            }
        }
        if (isContainsContent)
            patternString.append("[^>]*?\\s*>[\\s\\S]*?</\\s*" + tag + "\\s*>");
        else
            patternString.append("([^>]*)>");
        Pattern pattern = Pattern.compile(patternString.toString());
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * 找到A标签
     *
     * @param html 要查找的内容
     * @return
     */
    public static List<String> findATag(String html) {
        return findHtmlTag(html, "a", false);
    }

    /**
     * 找到img标签
     *
     * @param html 要查找的内容
     * @return
     */
    public static List<String> findIMGTag(String html) {
        return findHtmlTag(html, "img", false);
    }

    /**
     * 找到Script标签
     *
     * @param html 要查找的内容
     * @return
     */
    public static List<String> findScriptTag(String html) {
        return findHtmlTag(html, "script", "", "", true);
    }

    /**
     * 找到input标签
     *
     * @param html 要查找的内容
     * @return
     */
    public static List<String> findInputTag(String html) {
        return findHtmlTag(html, "input", "", "", false);
    }


    /**
     * 通过指定属性及属性值找到input标签
     *
     * @param html     要查找的内容
     * @param attrs    指定的属性
     * @param attrVals 指定的值
     * @return
     */
    public static List<String> findInputTag(String html, String[] attrs, String[] attrVals) {
        return findHtmlTag(html, "input", attrs, attrVals, false);
    }

    /**
     * 提取img标签的src值
     *
     * @param html 要查找的内容
     * @return
     */
    public static List<String> findImg_Src(String html) {
        return findAttrValue(html, "img", new String[]{"src"});
    }

    /**
     * 提取a标签的href值
     *
     * @param html 要查找的内容
     * @return
     */
    public static List<String> findA_Href(String html) {
        return findAttrValue(html, "a", new String[]{"href"});
    }


    /**
     * 根据标签提取指定属性
     *
     * @param html  要查找的内容
     * @param tag   标签名
     * @param attrs 属性名（尽量可以的少）
     * @return
     */
    public static List<String> findAttrValue(String html, String tag, String[] attrs) {
        List<String> list = new ArrayList<>();
        String patternString = "<\\s*" + tag + "\\s*([^>]*)>";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            String htmlTag = (matcher.group());
            for (String attr : attrs) {
                Pattern attrPattern = Pattern.compile("\\s+" + attr + "\\s*=\\s*[\"'](.*?)[\"']");
                Matcher attrMatcher = attrPattern.matcher(htmlTag);
                if (attrMatcher.find()) {
                    list.add(attrMatcher.group(1));
                }
            }
        }
        return list;
    }

    /**
     * 找到<div>xxxxxxx</div>中xxxxx的值(如果div内还包含其他html标签，则会出现错误)
     *
     * @param html
     * @return
     */
    public static String findContent(String html) {
        Matcher matcher = CONTENT_ATTR_PATTERN.matcher(html);
        if (matcher.find())
            return matcher.group(1);
        return "";
    }
}
