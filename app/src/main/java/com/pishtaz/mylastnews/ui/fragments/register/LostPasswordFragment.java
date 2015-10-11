package com.pishtaz.mylastnews.ui.fragments.register;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.networks.VolleyGeneric;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * Created by Arash on 03/16/2015.
 */
public class LostPasswordFragment extends Fragment  {

    EditText editTextEmail;
    TextView buttonLogin;
    String email;
    ProgressDialog pDialog;
    int[] str;

    //------------------------------------------
    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences.Editor editor;
    SharedPreferences settings;
    VolleyGeneric volley;
    EditText editsearch;
    View view;
    TextView backToLogin;
    ImageView textViewTitleActionbar;
    // TextView textViewLoginActionbar;
    ImageView imageViewSearch;
   // RevealBackgroundView revealLayout;


    public static LostPasswordFragment newInstance() {
        LostPasswordFragment fragment = new LostPasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.login, container, false);
    */
    // android.support.v7.widget.Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        str = getArguments().getIntArray("ARG_REVEAL_START_LOCATION");

        if (view==null) {
            view = inflater.inflate(R.layout.forget_password, container, false);

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.pleaseWait));
            pDialog.setCancelable(false);

            TextView textViewGoLogin;
            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;

            float scaleFactor = metrics.density;
            float widthDp = widthPixels / scaleFactor;
            float heightDp = heightPixels / scaleFactor;
            float smallestWidth = Math.min(widthDp, heightDp);

            if (smallestWidth >= 720) {
                //Device is a 10" tablet
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (smallestWidth < 700) {
                //Device is a 7" tablet
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            buttonLogin = (TextView) view.findViewById(R.id.buttonLogin);
            textViewGoLogin = (TextView) view.findViewById(R.id.textViewGoLogin);
            editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
          //  revealLayout= (RevealBackgroundView) view.findViewById(R.id.revealLayout);
       /*
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewRegisterFromLogin = (TextView) findViewById(R.id.textViewRegisterFromLogin);
     */
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkEmail(editTextEmail.getText().toString().trim())) {

                        email = editTextEmail.getText().toString().trim();
                        forgetPassword();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.wrong_email), Toast.LENGTH_SHORT).show();
                    }


                }
            });


            textViewGoLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentById(R.id.content);
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(fragment);
                    ft.add(R.id.content, new LoginFragment());
                    ft.addToBackStack(LoginFragment.class.getName());
                    ft.commit();
                }
            });
        }else{

        }
        return view;
    }

    public void forgetPassword() {


        volley = new VolleyGeneric(getActivity());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("emailAddress", email);
        settings=getActivity().getSharedPreferences(PREFS_NAME,0);
        if (settings.getString("User_Id","").equals("")){
            params.put("token", ".");
        }
        else{
            params.put("token", settings.getString("token",""));
        }
        volley.getJson("http://accounts.pishtazgroup.net/api/token/forgotPass/", reviewResponseListener(), reviewErrorListener(), params);


    }

    private Response.Listener<String> reviewResponseListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.d("RESPONSE FORGOT", response + "");
                    hidepDialog();

                    if (response.equals("true")) {

                        Toast.makeText(getActivity(),getResources().getString(R.string.activateEmaie), Toast.LENGTH_SHORT).show();


                    } else if (response.equals("false")) {

                        Toast.makeText(getActivity(),getResources().getString(R.string.email_not_exist), Toast.LENGTH_SHORT).show();

                    } /*else {
                        Toast.makeText(getActivity(), "????? ???? ??? ???? ????", Toast.LENGTH_SHORT).show();
                    }*/


                } catch (Exception ex) {
                    Log.e("ERROR REGISTER :", ex + "");
                    hidepDialog();
                    Toast.makeText(getActivity(),  getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();

                }
            };

        };
    }

    private Response.ErrorListener reviewErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Toast.makeText(getActivity(),  getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();

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

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            String text = editsearch.getText().toString()
                    .toLowerCase(Locale.getDefault());
            //   adapter.filter(text);
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
            // TODO Auto-generated method stub

        }

    };

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }




  /*  private void startIntroAnimation() {
        vUpperPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
        vLowerPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR).start();
    }*/
}
