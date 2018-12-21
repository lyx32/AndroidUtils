package com.arraylist7.android.utils.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arraylist7.android.utils.BitmapUtils;
import com.arraylist7.android.utils.IOUtils;
import com.arraylist7.android.utils.LogUtils;

/**
 * 通用的ListView和GridView的adapter。
 * 
 * @author lin
 *
 * @param <T>
 */
@SuppressLint("UseSparseArrays")
public abstract class NListAdapter<T> extends BaseAdapter {

	protected List<T> data;
	protected LayoutInflater flater;
	protected Context context;
	protected View currView;
	protected int layoutId = -1;
	protected Map<Integer, SoftReference<View>> viewCaches;
	private Map<String, WeakReference<Bitmap>> assetsCaches = new HashMap<>();

	public NListAdapter(Context context) {
		super();
		init(-1, context, new ArrayList<T>());
	}

	public NListAdapter(int layoutId, Context context) {
		super();
		init(layoutId, context, new ArrayList<T>());
	}

	public NListAdapter(Context context, List<T> data) {
		super();
		init(-1, context, data);
	}

	public NListAdapter(Context context, T[] arrays) {
		super();
		List<T> list = new ArrayList<T>();
		for (T t : arrays)
			list.add(t);
		init(-1, context, list);
	}

	public NListAdapter(int layoutId, Context context, List<T> data) {
		super();
		init(layoutId, context, data);
	}

	public NListAdapter(int layoutId, Context context, T[] arrays) {
		super();
		List<T> list = new ArrayList<T>();
		for (T t : arrays)
			list.add(t);
		init(layoutId, context, list);
	}

	private void init(int layoutId, Context context, List<T> list) {
		if (-1 != layoutId)
			this.layoutId = layoutId;
		this.context = context;
		this.data = list;
		if (null != context)
			flater = LayoutInflater.from(context);
	}

	/**
	 * 简单清除缓存
	 */
	public void clear() {
		data.clear();
	}

	/**
	 * 设置该adapter渲染的item布局（此方法要在setAdapter()之前调用）
	 * 
	 * @param layoutId
	 */
	public void setLayoutId(int layoutId) {
		this.layoutId = layoutId;
	}

	/**
	 * 更新UI
	 */
	public void updateUI() {
		this.notifyDataSetChanged();
	}

	/**
	 * 局部刷新
	 *
	 * @param listView
	 * @param position
	 */
	public static void updateUI(AbsListView listView, int position) {
		updateUI(listView,position,false);
	}
	/**
	 * 局部刷新
	 * 
	 * @param listView
	 * @param position
	 * @param isAutoScrollBottom 如果当前显示的是底部，刷新之后是否自动滚动到底部
	 */
	public static void updateUI(AbsListView listView, int position,boolean isAutoScrollBottom) {
		if (null == listView) {
			LogUtils.e("局部刷新 listView 不能为空");
			return;
		}
		ListAdapter adapter = listView.getAdapter();
		if (null == adapter) {
			LogUtils.e("listView.getAdapter() 为空");
			return;
		}
		int first = listView.getFirstVisiblePosition();
		int last = listView.getLastVisiblePosition();
		int dataAll = adapter.getCount();
		int listAll = listView.getCount();
		// 修改某个项
		if (first <= position && position <= last) {
			View convertView = listView.getChildAt(position);
			adapter.getView(position, convertView, listView);
			// 如果是刷新新增的
		} else if (dataAll > listAll) {
			adapter.getView(position, null, listView);
			// 如果当前在滚动到最后，并且也有新增的需要显示，则刷新显示
			if ((last + 1) == listAll && isAutoScrollBottom)
				listView.setSelection(listView.getBottom());
		}
	}

	/**
	 * 添加数据
	 * 
	 * @param t
	 */
	public void addData(T t) {
		this.data.add(t);
	}

	/**
	 * 添加数据
	 * 
	 * @param data
	 */
	public void addData(List<T> data) {
		this.data.addAll(data);
	}

	/**
	 * 添加数据
	 * 
	 * @param data
	 */
	public void addData(T[] data) {
		for (T t : data)
			this.data.add(t);
	}

	public void remove(int position) {
		this.data.remove(position);
	}

	public void remove(T t) {
		this.data.remove(t);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public T getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (0 == data.size())
			return convertView;
		synchronized (this) {
			if (0 == data.size())
				return convertView;
		}
		if (null == flater)
			return convertView;
		if (null == convertView) {
			if (-1 == layoutId) {
				LogUtils.e("通用adapter需要设置layoutID");
				return convertView;
			}
			currView = convertView = flater.inflate(this.layoutId, null);
			viewCaches = new HashMap<Integer, SoftReference<View>>();
		} else {
			currView = convertView;
			viewCaches = (HashMap<Integer, SoftReference<View>>) currView.getTag();
		}
		bindData(position, currView, parent, data.get(position));
		currView.setTag(viewCaches);
		return currView;
	}

	/**
	 * 绑定数据 convertView始终是最新的或者是已经获取到的缓存converView
	 */
	public abstract void bindData(int position, View convertView, ViewGroup parent, T t);

	/**
	 * 设置TextView的值
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public TextView setText(int viewId, CharSequence text) {
		TextView textView = getView(viewId, TextView.class);
		textView.setText(text);
		return textView;
	}



	public LinearLayout getLinearLayout(int id) {
		return getView(id, LinearLayout.class);
	}

	public RelativeLayout getRelativeLayout(int id) {
		return getView(id, RelativeLayout.class);
	}

	public Button getButton(int id) {
		return getView(id, Button.class);
	}

	public TextView getTextView(int id) {
		return getView(id, TextView.class);
	}

	public ImageView getImageView(int id) {
		return getView(id, ImageView.class);
	}

	public View visibility(int viewId) {
		View view = getView(viewId);
		view.setVisibility(View.VISIBLE);
		return view;
	}

	public View gone(int viewId) {
		View view = getView(viewId);
		view.setVisibility(View.GONE);
		return view;
	}

	/**
	 * 加载图片
	 *
	 * @param viewId
	 * @param url
	 * @return
	 */
	public ImageView setImage(int viewId, String url) {
		ImageView imgView = getView(viewId, ImageView.class);
		BitmapUtils.loadBitmap(url, imgView);
		return imgView;
	}
	/**
	 * 加载图片
	 * 
	 * @param viewId
	 * @param url
	 * @return
	 */
	public ImageView setImage(int viewId, String url, int width, int height) {
		ImageView imgView = getView(viewId, ImageView.class);
		BitmapUtils.loadBitmap(url, width, height, imgView);
		return imgView;
	}

	public ImageView setImageResource(int viewId, int resId) {
		ImageView imageView = getView(viewId, ImageView.class);
		imageView.setImageResource(resId);
		return imageView;
	}

	public ImageView setImageBitmap(int viewId, Bitmap bitmap) {
		ImageView imageView = getView(viewId, ImageView.class);
		imageView.setImageBitmap(bitmap);
		return imageView;
	}


	public ImageView setImageAssets(int viewId, String fileName) {
		ImageView imageView = getView(viewId, ImageView.class);
		synchronized (imageView) {
			WeakReference<Bitmap> soft = assetsCaches.get(viewId + "_" + fileName);
			if (null != soft && null != soft.get()) {
				LogUtils.i("setImageAssets(" + viewId + ",\"" + fileName + "\") reader cache!");
				imageView.setImageBitmap(soft.get());
			} else {
				LogUtils.i("setImageAssets(" + viewId + ",\"" + fileName + "\") no cache load assets file !");
				InputStream in = null;
				try {
					in = imageView.getContext().getAssets().open(fileName);
					Bitmap bitmap = BitmapFactory.decodeStream(in);
					imageView.setImageBitmap(bitmap);
					assetsCaches.remove(viewId + "_" + fileName);
					assetsCaches.put(viewId + "_" + fileName, new WeakReference<Bitmap>(bitmap));
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.close(in);
				}
			}
		}
		return imageView;
	}

	public View getView(int itemViewId) {
		return getView(itemViewId, View.class);
	}

	public <T extends View> T getView(int itemViewId, Class<T> c) {
		SoftReference<View> cacheView = viewCaches.get(itemViewId);
		T view = null;
		if (null == cacheView || null == cacheView.get()) {
			view = (T) currView.findViewById(itemViewId);
			viewCaches.put(itemViewId, new SoftReference<View>(view));
		} else {
			view = (T) cacheView.get();
		}
		return view;
	}

	/**
	 * 深度清理自定义缓存
	 */
	public void depthClearData() {
		data.clear();
		if (null != viewCaches) {
			synchronized (viewCaches) {
				Collection<SoftReference<View>> values = viewCaches.values();
				for (SoftReference<View> s : values) {
					s.clear();
				}
			}
		}
		System.gc();
	}
}
