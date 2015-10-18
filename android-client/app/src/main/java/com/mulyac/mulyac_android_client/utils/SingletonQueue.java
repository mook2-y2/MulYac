package com.mulyac.mulyac_android_client.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingletonQueue {
	private static SingletonQueue mInstance;
	private RequestQueue mRequestQueue;
	private static Context mContext;

	private SingletonQueue(Context context) {
		mContext = context;
		mRequestQueue = getRequestQueue();
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext
					.getApplicationContext());
		}
		return mRequestQueue;
	}

	public static synchronized SingletonQueue getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SingletonQueue(context);
		}
		return mInstance;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

}
