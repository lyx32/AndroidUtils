package com.arraylist7.android.utils.listener;

import android.database.sqlite.SQLiteDatabase;

public interface SQLUpdateListener {


    public void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion);
}
