package com.pishtaz.mylastnews.networks;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pishtaz.mylastnews.R;

import java.util.HashMap;
import java.util.Map;

public class VolleyGeneric {
    //All this portion of code starting from <THIS>
    private Context context;
    public final static String TOKEN = "whGtOdJ560-EenGQMPZqQw";
    public final static String FILE = "/sdcard/download/.pishtaz.txt";
    public final static String NIGHT = "night";
    public final static String SIZE = "size";
    public final static String FIRST_RUN = "first";
    public final static double INCHES=8.7;
    public final static String APP_ID="1632";

    public VolleyGeneric(Context context) {
        this.context = context;
    }

    private void genericPOSTRequest(String serverURL, Map<String, String> parameters, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        GenericRequest gen = new GenericRequest();
        gen.makePOSTRequest(serverURL,parameters, responseListener, errorListener);
    }

    private void genericGETRequest(String serverURL, Map<String, String> parameters, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        GenericRequest gen = new GenericRequest();
        gen.makeGETRequest(serverURL,parameters, responseListener, errorListener);
    }

    private class GenericRequest {

        private void makePOSTRequest(String serverURL,final Map<String, String> parameters, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL, responseListener, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return parameters;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return super.getHeaders();

                }
            };
            queue.add(stringRequest);
        }

        private void makeGETRequest(String serverURL,final Map<String, String> parameters, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, serverURL, responseListener, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return parameters;
                }
            };
            queue.add(stringRequest);
        }
    }
    //to </THIS> are the major calls to communicate with the server.


    public void getJson(/*int page,*/String serverURL,Response.Listener<String> responseListener, Response.ErrorListener errorListener,HashMap<String,String > parameters) {
       /* Map<String, String> parameters = new HashMap<>();
        String VALUE = "1";
        parameters.put("PageNumber", page+"");*/
        genericPOSTRequest(serverURL,parameters, responseListener, errorListener);
    }


    public void getJsonGet(/*int page,*/String serverURL,Response.Listener<String> responseListener, Response.ErrorListener errorListener,HashMap<String,String > parameters) {
       /* Map<String, String> parameters = new HashMap<>();
        String VALUE = "1";
        parameters.put("PageNumber", page+"");*/
        genericGETRequest(serverURL,parameters, responseListener, errorListener);
    }






}