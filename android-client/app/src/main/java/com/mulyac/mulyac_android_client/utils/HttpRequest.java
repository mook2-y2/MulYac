package com.mulyac.mulyac_android_client.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest extends Request<String> {
	
	private HashMap<String, String> mHashMap;
	private Listener<String> mListener;

	public HttpRequest(String url, HashMap<String, String> mHashParams, Listener<String> successListener, ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		this.mListener = successListener;
		this.mHashMap = mHashParams;
		setRetryPolicy(new DefaultRetryPolicy(10*1000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	@Override
	protected Response<String> parseNetworkResponse(
			NetworkResponse response) {
		// TODO Auto-generated method stub
		String parsed;
		try {
			parsed = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed,
				HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(String paramT) {
		// TODO Auto-generated method stub
		mListener.onResponse(paramT);
	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mHashMap;
	};
	


}




