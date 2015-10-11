package com.pishtaz.mylastnews.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.activeandroid.query.Select;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pishtaz.mylastnews.MainActivity;
import com.pishtaz.mylastnews.db_model.OtherAppDA;
import com.pishtaz.mylastnews.models.OwnerTO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


/**
 * Created by SEPIDEH on 9/15/2015.
 */
public class DateChangedReceiver extends BroadcastReceiver {

    private static final String POST_Url = "http://webservice.myappstores.ir/app.asmx/PishtazApp";
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        if (isNetworkAvailable(context)) {
            refreshList();
        }

    }

    public void refreshList() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("AppId", VolleyGeneric.APP_ID);
        VolleyGeneric volley = new VolleyGeneric(context);
        volley.getJson(POST_Url, reviewResponseListenerGetApp(), reviewErrorListenerGetApp(), hashMap);

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

                    Log.d("RESPONSE get app", response + "");

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            OtherAppDA otherAppDA = new OtherAppDA();

                            otherAppDA.apkFileName = obj.getString("apkFileName");
                            otherAppDA.Title = obj.getString("Title");
                            otherAppDA.apkFile = obj.getString("apkFile");
                            otherAppDA.AppID = obj.getString("Id");
                            otherAppDA.ThumIcon = obj.getString("ThumIcon");
                            otherAppDA.apkSize = obj.getString("Size");

                            if (getOtherAppsByID(obj.getString("Id")).size() == 0) {
                                otherAppDA.save();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception ex) {
                    Log.e("ERROR REGISTER :", ex + "");

                }


            }

            ;

        };
    }

    private Response.ErrorListener reviewErrorListenerGetApp() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
    }


    public List<OtherAppDA> getOtherAppsByID(String AppID) {

        return new Select()
                .from(OtherAppDA.class)
                .where("AppID = ?", AppID)
                .execute();

    }


}
