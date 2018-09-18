package com.arraylist7.android.utils.listener;

public interface PermissionListener {

    public void permissionRequestSuccess(String[] permissions);

    public void permissionRequestFail(String[] permissions);

}
