package com.pishtaz.mylastnews.ui.fragments.register;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pishtaz.mylastnews.MainActivity;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.models.CountryTO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.pishtaz.mylastnews.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by sepid on 11/20/2014.
 */
public class RegisterFragment extends Fragment {

    EditText editTextUsername, editTextEmail, editTextPassword, editTextState;
    com.rey.material.widget.CheckBox  checkBoxHeart;
    RadioGroup radioGroupGender;
    Spinner spinnerCountry, spinnerState, spinnerYear, spinnerMonth, spinnerDay;
    TextView buttonRegister;
    TextView textViewGoToLogin;
    //----------------------------------------------------------

    public String username, email, password, country, stateIran, stateOther, countryCode = "";
    public boolean heart;
    public boolean gender;
    boolean isChecked;
    ImageView imageViewShowPass;
    LinearLayout linearStateEditText, linearStateSpinner;
    public List<CountryTO> countryTOList = new ArrayList<>();
    public List<String> list;
    VolleyGeneric volley;
    ProgressDialog pDialog;
    ArrayList<String> states = new ArrayList<>();
    View view;

    //  android.support.v7.widget.Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null) {
            view = inflater.inflate(R.layout.register_user, container, false);

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.pleaseWait));
            pDialog.setCancelable(false);

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

            editTextUsername = (EditText) view.findViewById(R.id.editTextUserName);
            editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
            editTextState = (EditText) view.findViewById(R.id.editTextState);
            editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
            checkBoxHeart = (com.rey.material.widget.CheckBox ) view.findViewById(R.id.chekBoxHeart);
            radioGroupGender = (RadioGroup) view.findViewById(R.id.radioGroupGender);
            spinnerState = (Spinner) view.findViewById(R.id.spinnerState);
            spinnerDay = (Spinner) view.findViewById(R.id.spinnerDay);
            spinnerYear = (Spinner) view.findViewById(R.id.spinnerYear);
            spinnerMonth = (Spinner) view.findViewById(R.id.spinnerMonth);
            spinnerCountry = (Spinner) view.findViewById(R.id.spinnerCountry);
            buttonRegister = (TextView) view.findViewById(R.id.buttonRegister);
            textViewGoToLogin = (TextView) view.findViewById(R.id.textViewGoToLogin);
            linearStateEditText = (LinearLayout) view.findViewById(R.id.linearStateEditText);
            linearStateSpinner = (LinearLayout) view.findViewById(R.id.linearStateSpinner);


            states.clear();
            for (int state : Utils.states) {
                states.add(getResources().getString(state));
            }


            textViewGoToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentById(R.id.content);
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(fragment);
                    ft.add(R.id.content, new LoginFragment());
                    ft.addToBackStack(LoginFragment.class.getName());
                    ft.commit();
                }
            });
            // >>> show-password button
            imageViewShowPass = (ImageView) view.findViewById(R.id.imageViewShowPass);
    /*    imageViewShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChecked) {
                    // show password
                    isChecked = true;
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    isChecked = false;
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
*/
            spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //   stateIran = getString(Utils.states[i]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    stateIran = getResources().getString(R.string.tehran);
                }
            });

            spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (list.get(i).equals(getResources().getString(R.string.iran))) {
                        linearStateSpinner.setVisibility(View.VISIBLE);
                        linearStateEditText.setVisibility(View.GONE);

                        SpinAdapterState adapter = new SpinAdapterState(getActivity(), R.layout.spinner_country, states);
                        adapter.setDropDownViewResource(R.layout.spinner_country);
                        spinnerState.setAdapter(adapter);

                        CountryTO countryTO = (CountryTO) spinnerCountry.getSelectedItem();
                        countryCode = countryTO.getCountryID();

                    } else {
                        linearStateSpinner.setVisibility(View.GONE);
                        linearStateEditText.setVisibility(View.VISIBLE);
                    }
                    country = list.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    country = getResources().getString(R.string.iran);
                    countryCode = "1";
                }
            });

            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            ReadJsonAsync readJsonAsync = new ReadJsonAsync();
            readJsonAsync.execute();

            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkBoxHeart.isChecked()) {

                        username = editTextUsername.getText().toString();
                        email = editTextEmail.getText().toString();
                        password = editTextPassword.getText().toString();
                        country = String.valueOf(spinnerCountry.getSelectedItem());
                        if (country.equals(getResources().getString(R.string.iran))) {
                            countryCode = "1";
                            stateIran = String.valueOf(spinnerState.getSelectedItem());
                        } else {
                            stateOther = String.valueOf(spinnerState.getSelectedItem());
                        }
                        heart = checkBoxHeart.isChecked();
                        int id = radioGroupGender.getCheckedRadioButtonId();
                        RadioButton radioButtonGender = (RadioButton) view.findViewById(id);
                        try {
                            if (radioButtonGender.getText().toString().equals(getResources().getString(R.string.female)))
                                gender = true;
                            else
                                gender = false;
                        } catch (Exception ex) {

                        }


                        if (username.trim().length() > 3 && password.trim().length() > 5) {
                            if (checkEmail(editTextEmail.getText().toString().trim())) {
                                register();

                            } else {
                                Toast.makeText(getActivity(), getResources().getString(R.string.wrong_email), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.wrong_username_pass), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.policy), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else{

        }
        return view;
    }


    private class ReadJsonAsync extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {


            try {
                InputStream is = getResources().openRawResource(R.raw.countries);
                Writer writer = new StringWriter();
                char[] buffer = new char[1024];
                try {
                    Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    int n;
                    while ((n = reader.read(buffer)) != -1) {
                        writer.write(buffer, 0, n);
                    }
                } finally {
                    is.close();
                }

                String jsonString = writer.toString();


                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CountryTO countryTO = new CountryTO();
                    countryTO.setCountryID(jsonObject.getString("CountryID"));
                    countryTO.setName(jsonObject.getString("Name"));
                    countryTO.setCountryCallingCode(jsonObject.getString("CountryCallingCode"));
                    countryTO.setInternationalCallPrefix(jsonObject.getString("InternationalCallPrefix"));
                    countryTO.setTrunkPrefix(jsonObject.getString("TrunkPrefix"));

                    countryTOList.add(countryTO);

                }

                list = new ArrayList<>();
                list.clear();
                for (CountryTO countryTO : countryTOList) {
                    list.add(countryTO.getName());
                }

            } catch (Exception e) {

            }

            return null;
        }

        protected void onPostExecute(String result) {


            SpinAdapter adapter = new SpinAdapter(getActivity(), R.layout.spinner_country, countryTOList);
            adapter.setDropDownViewResource(R.layout.spinner_country);
            spinnerCountry.setAdapter(adapter);
            spinnerCountry.setSelection(25);
        }
    }


    public void register() {

        showpDialog();

        volley = new VolleyGeneric(getActivity());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Username", username + "");
        params.put("Password", password + "");
        params.put("EmailAddress", email + "");
        params.put("IsMale", gender + "");
        params.put("BirthDate_Year", spinnerYear.getSelectedItem() + "");
        params.put("BirthDate_Month", spinnerMonth.getSelectedItem() + "");
        params.put("BirthDate_Day", spinnerDay.getSelectedItem() + "");
        params.put("HostName", "http://mp4.ir");
        params.put("CountryID", countryCode + "");
        params.put("ProvinceName", editTextState.getText().toString() + "");
        params.put("ProvinceNameIran", spinnerState.getSelectedItem().toString() + "");
        volley.getJson("http://accounts.pishtazgroup.net/api/token/register", reviewResponseListener(), reviewErrorListener(), params);

        showpDialog();

    }

    private Response.Listener<String> reviewResponseListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.d("RESPONSE REGISTER", response + "");

                    // JSONObject jsonObject = new JSONObject(response);
                    //  String UserIsValid = jsonObject.getString("UserIsValid");
                    // String Register = jsonObject.getString("Register");
                    if (response.equals("true")) {
                        // Toast.makeText(getActivity(), getResources().getString(R.string.welcome), Toast.LENGTH_LONG).show();



                        login(username, password);


                    } else {

                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray array = object.getJSONArray("Errors");
                            String msg = array.toString();
                            msg = msg.replace("[", "").replace("]", "");
                            String[] msgs = msg.split(",");
                            StringBuilder toast = new StringBuilder();
                            for (String str : msgs) {
                                toast.append(str);
                                toast.append("\n");
                            }
                            Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                            Log.e("11111111111111", msg);

                        } catch (Exception e) {
                            //  Log.e("11111111111111",e+"");
                        }


                    }

                    hidepDialog();
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

                Toast.makeText(getActivity(), getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();


            }
        };
    }




    public void login(final String username, final String password) {


        volley = new VolleyGeneric(getActivity());
        HashMap<String, String> params = new HashMap<>();
        params.put("UserName", username);
        params.put("Password", password);
        volley.getJson("http://accounts.pishtazgroup.net/api/token/login", reviewResponseListenerLogin(), reviewErrorListenerLogin(), params);


    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


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

    private Response.Listener<String> reviewResponseListenerLogin() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    showpDialog();
                    Log.d("RESPONSE LOGIN", response + "");
                    hidepDialog();

                    if (!response.equals("false")) {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("EmailConfirmed").equals("false")) {
                            LoginFragment.userID = jsonObject.getString("Id");
                            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.content);
                            ChooseActivationFragment chooseActivationFragment = new ChooseActivationFragment();
                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment)
                                    .replace(R.id.content, chooseActivationFragment)
                                    .addToBackStack(MainActivity.class.getName())
                                    .commit();
                            Toast.makeText(getActivity(), R.string.not_active_your_account, Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.wrong_username_pass), Toast.LENGTH_SHORT).show();
                    }

                    Log.d("TAB CONTENT :", response + "");
                } catch (Exception ex) {
                    Log.e("ERROR REGISTER :", ex + "");
                    hidepDialog();
                    Toast.makeText(getActivity(), getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();
                }

            }

            ;

        };
    }

    private Response.ErrorListener reviewErrorListenerLogin() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Toast.makeText(getActivity(), getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();
            }
        };
    }

}
