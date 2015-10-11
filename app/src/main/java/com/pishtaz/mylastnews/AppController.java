package com.pishtaz.mylastnews;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.pishtaz.mylastnews.utils.FontsOverride;


public class AppController extends Application {
 
    public static final String TAG = AppController.class.getSimpleName();
 
    private RequestQueue mRequestQueue;
 
    private static AppController mInstance;
 
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/IRAN Sans Light.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/IRAN Sans Light.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/IRAN Sans Light.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/IRAN Sans Light.ttf");
        ActiveAndroid.initialize(this);
        mInstance = this;
    }
 
    public static synchronized AppController getInstance() {
        return mInstance;
    }
 
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
 
        return mRequestQueue;
    }
 
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
        Log.d("REQUEST :", getRequestQueue().add(req)+"");
        Log.d("REQUEST222 :", req+"");
    }
 
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}