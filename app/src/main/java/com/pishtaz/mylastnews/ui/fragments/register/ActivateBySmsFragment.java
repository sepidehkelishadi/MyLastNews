package com.pishtaz.mylastnews.ui.fragments.register;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.networks.VolleyGeneric;

import java.util.HashMap;



public class ActivateBySmsFragment extends Fragment {

    VolleyGeneric volley;
    ProgressDialog pDialog;
    TextView textViewActivate;
    EditText edittextMobile;
    String code;
    View view;
    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences.Editor editor;
    SharedPreferences settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_activate_by_sms, container, false);

            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.pleaseWait));
            pDialog.setCancelable(false);

            textViewActivate = (TextView) view.findViewById(R.id.textViewActivate);
            edittextMobile = (EditText) view.findViewById(R.id.edittextMobile);

            textViewActivate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (edittextMobile.getText().toString().trim().length() > 0) {
                        code = edittextMobile.getText().toString().trim();
                        activate();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.wrongActivationCode), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {

        }
        return view;
    }

    public void activate() {
        volley = new VolleyGeneric(getActivity());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userID", LoginFragment.userID);
        params.put("phoneNumber", ChooseActivationFragment.mobile);
        params.put("smsCode", code);

        volley.getJson("http://accounts.pishtazgroup.net/api/token/applySMSCode", reviewResponseListener(), reviewErrorListener(), params);
        showpDialog();
    }

    private Response.Listener<String> reviewResponseListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hidepDialog();
                try {
                    if (response.equals("true")) {

                        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                        editor = settings.edit();
                        editor.putString("User_Id", LoginFragment.userID);
                        editor.apply();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Fragment fragment = fm.findFragmentById(R.id.content);
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(fragment);
                        ft.add(R.id.content, new LoginFragment());
                        ft.addToBackStack(ActivateBySmsFragment.class.getName());
                        ft.commit();

                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.wrongActivationCode), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception ex) {
                    Log.e("ERROR REGISTER :", ex + "");
                    hidepDialog();
                    Toast.makeText(getActivity(), getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private Response.ErrorListener reviewErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Toast.makeText(getActivity(), getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();

            }
        };
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
