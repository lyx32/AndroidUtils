package com.arraylist7.android.utils.inter;

import java.io.Serializable;
import java.util.Map;

public interface IOperator {

    public void onLoginSuccess(Map<String, Serializable> data);

    public void onLoginOut(Map<String, Serializable> data);

    public void onReceivedData(Map<String,Serializable> data);
}
