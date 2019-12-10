package com.arraylist7.android.utils.model;

/**
 * 读取手机卡信息
 */
public class SimInfo {
    // 当前拨号sim的simSerialNumber
    public String iccid1;
    // 当前拨号sim的subscriberId
    public String imsi1;
    // 当前拨号sim的手机号，不一定能取到
    public String line1Number1;
    // 当前拨号sim的运营商识别码
    public String networkOperator1;
    // 当前拨号sim的运营商名称
    public String networkOperatorName1;
    // sim2的simSerialNumber
    public String iccid2;
    // sim2的subscriberId
    public String imsi2;
    // sim2的手机号，不一定能取到
    public String line1Number2;
    // sim2的运营商识别码
    public String networkOperator2;
    // sim2的运营商名称
    public String networkOperatorName2;

    public SimInfo() {
    }


}
