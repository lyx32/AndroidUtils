package com.arraylist7.android.utils.widget;

public interface Callback<T> {
	
	public void onComplete(T t);
	
	public void onFial(T t, Throwable e);
	
	public void onStart();
	
	public void onCancel();
	
	public void onPregress(long current, long lave, long count);
}
