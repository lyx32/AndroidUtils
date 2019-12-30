package com.arraylist7.android.utils.widget.parser;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import com.arraylist7.android.utils.HTMLUtils;
import com.arraylist7.android.utils.LogUtils;
import com.arraylist7.android.utils.StringUtils;
import com.arraylist7.android.utils.aenum.HtmlParserTypeEnum;
import com.arraylist7.android.utils.listener.HtmlParserListener;
import com.arraylist7.android.utils.listener.HtmlParserReplaceCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextViewHtmlParser {


    // 链接 <a href='xxxx'>我是链接</a>
    private static final Pattern PATTERN_LINK = Pattern.compile("<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>([^<>]*)</a>");
    // 话题 <a href='xxxx'>#我是话题#</a>
    private static final Pattern PATTERN_TOPIC = Pattern.compile("<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>(#[^#@<>\\s]+#)</a>");
    // @用户 <a href='xxxx'>@张三三</a>
    private static final Pattern PATTERN_ATUSER = Pattern.compile("<a\\s+href=['\"]([^'\"]*)['\"][^<>]*>(@[^#@<>\\s]+)</a>");
    // 邮箱 <a href='mailto:11233@qq.com'>我的邮箱</a>
    private static final Pattern PATTERN_EMAIL = Pattern.compile("<a\\s+href=['\"](mailto:[^'\"]*)['\"][^<>]*>([a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})</a>");
    // 电话号码  <a href='tel:136888888'>点我拨号</a>     13666666666  010-87878787 01087878787
    private static final Pattern PATTERN_TELEPHONE = Pattern.compile("<a\\s+href=['\"](tel:[^'\"]*)['\"][^<>]*>([1[3456789]\\d{9}]|[\\d{3,4}\\-?\\d{6,8}])</a>");
    // 图片 <img src='xxx' alt='我是图片' />
    private static final Pattern PATTERN_IMAGE = Pattern.compile("<img\\s+src=['\"]([^'\"]*)['\"]\\s*alt=['\"]([^'\"]*)['\"]\\s*[/]?>");

    private static SpanShowConfig linksConfig = new SpanShowConfig();
    private static SpanShowConfig topicConfig = new SpanShowConfig();
    private static SpanShowConfig atUserConfig = new SpanShowConfig();
    private static SpanShowConfig emailConfig = new SpanShowConfig();
    private static SpanShowConfig telephoneConfig = new SpanShowConfig();
    private static SpanShowConfig imageConfig = new SpanShowConfig();
    // 默认替换内容
    private static HtmlParserReplaceCallback defaultReplace = new HtmlParserReplaceCallback() {
        @Override
        public CharSequence replaceHtml(Context context, HtmlParserTypeEnum htmlType, String href, String text) {
            return text;
        }
    };

    /**
     * 解析
     *
     * @param context context
     * @param content content
     * @return Spannable
     */
    public static Spannable parse(Context context, String content, HtmlParserListener listener) {
        if (TextUtils.isEmpty(content))
            return null;
        content = HTMLUtils.htmlDecoding(content);
        Spannable spannable = parserLink(context, content, listener);
        spannable = parserImage(context, spannable, listener);
        return spannable;
    }

    /**
     * @param sequence       文本
     * @param pattern        正则
     * @param usedGroupIndex 使用的组号
     * @param showGroupIndex 显示的组号
     * @param listener       点击回掉
     * @return 匹配后的文本
     */
    @SuppressWarnings("all")
    private static Spannable assimilate(SpanShowConfig config, Context context, CharSequence sequence, Pattern pattern, int usedGroupIndex, int showGroupIndex, final HtmlParserListener listener) {
        SpannableStringBuilder builder = new SpannableStringBuilder(sequence);
        Matcher matcher;

        while (true) {
            matcher = pattern.matcher(builder.toString());
            if (matcher.find()) {
                final String group = matcher.group(0);
                final String group0 = matcher.group(usedGroupIndex);
                final String group1 = matcher.group(showGroupIndex);
                HtmlParserTypeEnum htmlType = null;
                if (group0.startsWith("mailto:"))// email
                    htmlType = HtmlParserTypeEnum.EMAIL;
                else if (group1.startsWith("@"))// @用户
                    htmlType = HtmlParserTypeEnum.ATUSER;
                else if (group1.startsWith("#") && group1.endsWith("#")) // 话题
                    htmlType = HtmlParserTypeEnum.TOPIC;
                else if (group0.startsWith("tel:")) // 电话
                    htmlType = HtmlParserTypeEnum.TEL;
                else if (group.startsWith("<a") && group0.startsWith("http")) // 链接
                    htmlType = HtmlParserTypeEnum.LINK;
                else if (group.startsWith("<img")) // 图片
                    htmlType = HtmlParserTypeEnum.IMAGE;
                else
                    htmlType = HtmlParserTypeEnum.UNKNOWN;

                CharSequence showText = defaultReplace.replaceHtml(context, htmlType, group0, group1);
                builder.replace(matcher.start(), matcher.end(), showText);
                ClickableSpan span = new QMUITouchableSpan(config) {
                    @Override
                    public void onSpanClick(View widget) {
                        if (StringUtils.isNullOrEmpty(group0) && StringUtils.isNullOrEmpty(group1)) {
                            return;
                        }
                        LogUtils.d("TextViewHtmlParser", "group0=[" + group0 + "]    group1=[" + group1 + "]");
                        if (null == listener)
                            return;
                        if (group0.startsWith("mailto:"))// email
                            listener.onEmailClick(group0, group1);
                        else if (group1.startsWith("@"))// @用户
                            listener.onAtuserClick(group0, group1);
                        else if (group1.startsWith("#") && group1.endsWith("#")) // 话题
                            listener.onTopicClick(group0, group1);
                        else if (group0.startsWith("tel:")) // 电话
                            listener.onTelephoneClick(group0, group1);
                        else if (group.startsWith("<a") && group0.startsWith("http")) // 链接
                            listener.onLinkClick(group0, group1);
                        else if (group.startsWith("<img")) // 图片
                            listener.onImageClick(group0, group1);
                        else
                            listener.onOtherClick(group0, group1);

                    }
                };
                builder.setSpan(span, matcher.start(), matcher.start() + group1.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                continue;
            }
            break;
        }
        return builder;
    }


    /**
     * 格式化<a href="xxxxx" >@用户</a>
     *
     * @param context context
     * @param content content
     * @return Spannable
     */
    public static Spannable parseAtUser(final Context context, CharSequence content, final HtmlParserListener listener) {
        return assimilate(atUserConfig, context, content, PATTERN_ATUSER, 1, 2, listener);
    }

    /**
     * 格式化链接 <a href="xxxxx" >链接</a>
     *
     * @param context
     * @param content
     * @return
     */
    public static Spannable parserLink(final Context context, CharSequence content, final HtmlParserListener listener) {
        return assimilate(linksConfig, context, content, PATTERN_LINK, 1, 2, listener);
    }

    /**
     * 格式化话题 <a href="xxxxx" >#话题#</a>
     *
     * @param context
     * @param content
     * @return
     */
    public static Spannable parserTopic(final Context context, CharSequence content, final HtmlParserListener listener) {
        return assimilate(topicConfig, context, content, PATTERN_TOPIC, 1, 2, listener);
    }


    /**
     * 格式化邮箱 <a href='mailto:11233@qq.com'>我的邮箱</a>
     *
     * @param context
     * @param content
     * @return
     */
    public static Spannable parserEMail(final Context context, CharSequence content, final HtmlParserListener listener) {
        return assimilate(emailConfig, context, content, PATTERN_EMAIL, 1, 2, listener);
    }


    /**
     * 格式电话 <a href='tel:136888888'>点我拨号</a>     13666666666  010-87878787 01087878787
     *
     * @param context
     * @param content
     * @return
     */
    public static Spannable parserTelePhone(final Context context, CharSequence content, final HtmlParserListener listener) {
        return assimilate(telephoneConfig, context, content, PATTERN_TELEPHONE, 1, 2, listener);
    }


    /**
     * 格式图片 <img src='xxxx' alt='我是图片' />
     *
     * @param context
     * @param content
     * @return
     */
    public static Spannable parserImage(final Context context, CharSequence content, final HtmlParserListener listener) {
        return assimilate(imageConfig, context, content, PATTERN_IMAGE, 1, 2, listener);
    }


    public static void setDefaultReplace(HtmlParserReplaceCallback defaultReplace) {
        TextViewHtmlParser.defaultReplace = defaultReplace;
    }

    public static SpanShowConfig getLinksConfig() {
        return linksConfig;
    }

    public static SpanShowConfig getTopicConfig() {
        return topicConfig;
    }

    public static SpanShowConfig getAtUserConfig() {
        return atUserConfig;
    }

    public static SpanShowConfig getEmailConfig() {
        return emailConfig;
    }

    public static SpanShowConfig getTelephoneConfig() {
        return telephoneConfig;
    }

    public static class SpanShowConfig {
        public int normalTextColor;
        public int pressedTextColor;
        public int normalBackgroundColor;
        public int pressedBackgroundColor;
        public boolean isShowUnderline = false;

        private static int defaultNormalTextColor = Color.parseColor("#31BDF3");
        private static int defaultPressedTextColor = Color.parseColor("#7F31BDF3");
        private static int defaultNormalBackgroundColor = Color.TRANSPARENT;
        private static int defaultPressedBackgroundColor = Color.parseColor("#efefef");

        public SpanShowConfig() {
            this.normalTextColor = defaultNormalTextColor;
            this.pressedTextColor = defaultPressedTextColor;
            this.normalBackgroundColor = defaultNormalBackgroundColor;
            this.pressedBackgroundColor = defaultPressedBackgroundColor;
        }

        public SpanShowConfig(int normalTextColor, int pressedTextColor, int normalBackgroundColor, int pressedBackgroundColor, boolean isShowUnderline) {
            this.normalTextColor = normalTextColor;
            this.pressedTextColor = pressedTextColor;
            this.normalBackgroundColor = normalBackgroundColor;
            this.pressedBackgroundColor = pressedBackgroundColor;
            this.isShowUnderline = isShowUnderline;
        }
    }


}
