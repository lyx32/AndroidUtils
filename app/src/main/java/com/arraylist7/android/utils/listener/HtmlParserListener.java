package com.arraylist7.android.utils.listener;


public interface HtmlParserListener {

    /**
     * 点击的是链接
     *
     * @param href href
     * @param text a标签的Text
     */
    public void onLinkClick(String href, String text);

    /**
     * 点击的是电话号码
     *
     * @param telephone
     * @param telephoneName
     */
    public void onTelephoneClick(String telephone, String telephoneName);

    /**
     * 点击的是email
     *
     * @param email
     * @param eMailName
     */
    public void onEmailClick(String email, String eMailName);

    /**
     * 点击的是@xxx
     *
     * @param id           @用户的唯一标识
     * @param userIdOrName @用户的唯一标识或名称
     */
    public void onAtuserClick(String id, String userIdOrName);

    /**
     * 点击的是话题
     *
     * @param id            话题的唯一标识
     * @param topicIdOrName 标题的唯一标识或名称
     */
    public void onTopicClick(String id, String topicIdOrName);

    /**
     * 点击的是图片
     *
     * @param src
     * @param alt
     */
    public void onImageClick(String src, String alt);

    /**
     * 不知道点击的是什么
     *
     * @param group0 不知道
     * @param group1 不知道
     */
    public void onOtherClick(String group0, String group1);

}
