package com.pishtaz.mylastnews.ui.fragments.register;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.networks.VolleyGeneric;

import java.util.HashMap;



public class ChooseActivationFragment extends Fragment {

    RadioGroup radioGroupActivation;
    RadioButton radioButtonActivationEmail, radioButtonActivationsms;
    TextView textViewSendActivation;
    EditText editTextMobile;
    LinearLayout linearActiveSms;
    ProgressDialog pDialog;
    public static String mobile;
    VolleyGeneric volley;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null) {
            view = inflater.inflate(R.layout.activity_choose_activation, container, false);

            radioGroupActivation = (RadioGroup) view.findViewById(R.id.radioGroupActivation);
            textViewSendActivation = (TextView) view.findViewById(R.id.textViewSendActivation);
            radioButtonActivationEmail = (RadioButton) view.findViewById(R.id.radioButtonActivationEmail);
            radioButtonActivationsms = (RadioButton) view.findViewById(R.id.radioButtonActivationsms);
            editTextMobile = (EditText) view.findViewById(R.id.editTextMobile);
            linearActiveSms = (LinearLayout) view.findViewById(R.id.linearActiveSms);

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.pleaseWait));
            pDialog.setCancelable(false);

            radioGroupActivation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    if (radioButtonActivationEmail.isChecked()) {
                        linearActiveSms.setVisibility(View.GONE);
                    } else {
                        linearActiveSms.setVisibility(View.VISIBLE);
                    }

                }
            });

            textViewSendActivation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (radioButtonActivationEmail.isChecked()) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.activateEmaie), Toast.LENGTH_SHORT).show();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Fragment fragment = fm.findFragmentById(R.id.content);
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(fragment);
                        ft.add(R.id.content, new LoginFragment());
                        ft.addToBackStack(LoginFragment.class.getName());
                        ft.commit();

                    } else {
                        if (editTextMobile.getText().toString().trim().length() == 11) {
                            mobile = editTextMobile.getText().toString().trim();
                            chooseSms();
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.wrongPhone), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }else{

        }
        return view;
    }

    public void chooseSms() {
        volley = new VolleyGeneric(getActivity());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userID", LoginFragment.userID);
        params.put("phoneNumber", mobile);
        volley.getJson("http://accounts.pishtazgroup.net/api/token/sendSMS", reviewResponseListener(), reviewErrorListener(), params);
        showpDialog();
    }

    private Response.Listener<String> reviewResponseListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // showpDialog();
                    Log.d("RESPONSE LOGINNNNNNN", response + "");
                    hidepDialog();

                    if (response.equals("true")) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Fragment fragment = fm.findFragmentById(R.id.content);
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(fragment);
                        ft.add(R.id.content, new ActivateBySmsFragment());
                        ft.addToBackStack(ActivateBySmsFragment.class.getName());
                        ft.commit();

                    } else {
                        Toast.makeText(getActivity(),getResources().getString(R.string.wrongPhone), Toast.LENGTH_SHORT).show();
                    }

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
                Toast.makeText(getActivity(),getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();
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

