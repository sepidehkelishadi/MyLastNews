package com.pishtaz.mylastnews.update;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pishtaz.mylastnews.MainActivity;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.networks.VolleyGeneric;

import java.util.HashMap;


public class MyWebService extends IntentService {

    private static final String LOG_TAG = "MyWebService";
    public static final String PACKAGE_NAME = "myPackageName";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";
    private static final String POST_Url = "http://webservice.myappstores.ir/app.asmx/CheckVersion";

    public MyWebService() {
        super("MyWebService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e(LOG_TAG, "started");

        if (isNetworkAvailable(getApplicationContext())) {
            String packageName = intent.getStringExtra(PACKAGE_NAME);
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("PackageName", packageName);
            Log.e(LOG_TAG, packageName);
            VolleyGeneric volley = new VolleyGeneric(getApplicationContext());
            volley.getJson(POST_Url, reviewResponseListenerGetApp(), reviewErrorListenerGetApp(), hashMap);

        }


    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    // Log.v(LOG_TAG,String.valueOf(i));
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        //  Log.v(LOG_TAG, "connected!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Response.Listener<String> reviewResponseListenerGetApp() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    Log.e(LOG_TAG, response);
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(MainActivity.MyWebReceiver.PROCESS_RESPONSE);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra(RESPONSE_MESSAGE, response);
                    sendBroadcast(broadcastIntent);
                } catch (Exception e) {

                }
            }
        };
    }



    private Response.ErrorListener reviewErrorListenerGetApp() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), R.string.error_json_error_listener, Toast.LENGTH_LONG).show();

            }
        };
    }


}