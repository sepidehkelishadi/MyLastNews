package com.pishtaz.mylastnews.ui.fragments.register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.pishtaz.mylastnews.MainActivity;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.networks.VolleyGeneric;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;


public class LoginFragment extends Fragment {

    EditText editTextUsername;
    EditText editTextPassword;
    TextView buttonLogin;
    TextView textViewRegisterFromLogin;
    TextView textViewLostPassword;
    ProgressDialog pDialog;
    VolleyGeneric volley;
    boolean passShowing = false;
    //------------------------------------------
    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences.Editor editor;
    SharedPreferences settings;
    EditText editsearch;
    public static String userID;
    String userName, password, username, token;
    View view;
    ImageView ivShowPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.login, container, false);

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

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

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.pleaseWait));
            pDialog.setCancelable(false);

            editTextUsername = (EditText) view.findViewById(R.id.editTextUserName);
            editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
            textViewRegisterFromLogin = (TextView) view.findViewById(R.id.textViewRegisterFromLogin);
            textViewLostPassword = (TextView) view.findViewById(R.id.textViewLostPassword);
            buttonLogin = (TextView) view.findViewById(R.id.buttonLogin);
            ivShowPass = (ImageView) view.findViewById(R.id.ivShowPass);

            ivShowPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (passShowing) {
                        passShowing = false;
                        editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        ivShowPass.setImageResource(R.drawable.no_pass);
                    } else {
                        passShowing = true;
                        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        ivShowPass.setImageResource(R.drawable.eye);
                    }

                }
            });

            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (editTextPassword.getText().toString().trim().length() > 0 || editTextUsername.getText().toString().trim().length() > 0) {
                        login(editTextUsername.getText().toString().trim(), editTextPassword.getText().toString().trim());
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.wrong_username_pass), Toast.LENGTH_SHORT).show();
                    }

                }
            });

            textViewRegisterFromLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentById(R.id.content);
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(fragment);
                    ft.add(R.id.content, new RegisterFragment());
                    ft.addToBackStack(RegisterFragment.class.getName());
                    ft.commit();
                }
            });

            textViewLostPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                 /*   int[] startingLocation = new int[2];
                    textViewLostPassword.getLocationOnScreen(startingLocation);
                    startingLocation[0] += textViewLostPassword.getWidth() / 2;
*/

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentById(R.id.content);
                  /*  Bundle b = new Bundle();
                    b.putIntArray("ARG_REVEAL_START_LOCATION", startingLocation);
                    fragment.setArguments(b);
                   */
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(fragment);
                    ft.add(R.id.content, new LostPasswordFragment());
                    ft.addToBackStack(LoginFragment.class.getName());
                    ft.commit();


                    //  getActivity().overridePendingTransition(0, 0);


                }
            });
        } else {

        }
        return view;
    }

    public void login(final String username, final String password) {


        this.username = username;
        this.password = password;
        volley = new VolleyGeneric(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("Username", username);
        params.put("Password", password);
        //   params.put("token", VolleyGeneric.TOKEN);
        Log.e("rrrrrrrrrr", username + "---" + password);
        volley.getJson("http://accounts.pishtaz.ir/api/token/ValidateAndGetToken", reviewResponseListenerToken(), reviewErrorListenerToken(), params);




      /*

      */
        showpDialog();

    }

    private Response.Listener<String> reviewResponseListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // showpDialog();
                    Log.d("RESPONSE LOGIN", response + "");
                    //   hidepDialog();

                    if (!response.equals("false")) {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("EmailConfirmed").equals("false")) {
                            userID = jsonObject.getString("Id");
                            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.content);
                            ChooseActivationFragment chooseActivationFragment = new ChooseActivationFragment();
                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment)
                                    .replace(R.id.content, chooseActivationFragment)
                                    .addToBackStack(MainActivity.class.getName())
                                    .commit();
                            hidepDialog();
                            Toast.makeText(getActivity(), R.string.not_active_your_account, Toast.LENGTH_SHORT).show();

                        } else if (jsonObject.getString("EmailConfirmed").equals("true")) {

                            settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                            editor = settings.edit();
                            editor.putString("User_Id", jsonObject.getString("Id"));
                            editor.putString("UserName", jsonObject.getString("Username"));
                            //   editor.putString("Password", jsonObject.getString("Password"));
                            // editor.putString("Image", jsonObject.getString("Image"));
                            editor.apply();

                            File file = new File(VolleyGeneric.FILE);
                            if (file.exists()) {
                                file.delete();
                            }
                            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                            writer.write("[{\"User_Id\":" + "\"" + jsonObject.getString("Id") + "\"" + ",\"UserName\":" + "\"" + jsonObject.getString("Username") + "\"" + ",\"Token\":" + token + "}]");
                            Log.e("lllllllllll", "[{\"User_Id\":" + "\"" + jsonObject.getString("Id") + "\"" + ",\"UserName\":" + "\"" + jsonObject.getString("Username") + "\"" + ",\"Token\":" + token + "}]");
                            // writer.write("[{\"User_Id\":"+ jsonObject.getString("Id")+"},{\"UserName\":"+jsonObject.getString("Username")+"}]");
                            writer.flush();
                            writer.close();

                            userName = settings.getString("UserName", "");
                            Log.e("aaaaaaaaaaaaa", userName);

                            Toast.makeText(getActivity(), userName + getString(R.string.welcome_messeage_user), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();

                        }

                    } else {
                        hidepDialog();
                        Toast.makeText(getActivity(), getResources().getString(R.string.wrong_username_pass), Toast.LENGTH_SHORT).show();
                    }

                    // Log.d("TAB CONTENT :", response + "");
                } catch (Exception ex) {
                    Log.e("ERROR REGISTER :", ex + "");
                    hidepDialog();
                    Toast.makeText(getActivity(), getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();
                }

            }

            ;

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


    private Response.Listener<String> reviewResponseListenerToken() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    pDialog.show();
                    Log.e("RESPONSE REGISTER TOKEN", response + "");

                    settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                    editor = settings.edit();
                    editor.putString("token", response);
                    editor.apply();
                    token = response;

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("UserName", username);
                    params.put("Password", password);
                    volley.getJson("http://accounts.pishtazgroup.net/api/token/login", reviewResponseListener(), reviewErrorListener(), params);



                } catch (Exception ex) {
                    Log.e("ERROR REGISTER :", ex + "");
                    hidepDialog();
                    Toast.makeText(getActivity(), getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();

                }
            }

            ;

        };
    }

    private Response.ErrorListener reviewErrorListenerToken() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();


            }
        };
    }


}
