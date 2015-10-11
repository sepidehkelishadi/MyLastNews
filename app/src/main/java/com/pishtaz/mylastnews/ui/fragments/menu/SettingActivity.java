package com.pishtaz.mylastnews.ui.fragments.menu;


import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pishtaz.mylastnews.MainActivity;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.db_model.OwnersDA;
import com.pishtaz.mylastnews.models.News2TO;
import com.pishtaz.mylastnews.models.OwnerTO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.pishtaz.mylastnews.utils.MultiSelectionSpinner;
import com.pishtaz.mylastnews.utils.SystemBarTintManager;
import com.pishtaz.mylastnews.utils.Utils;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Switch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences.Editor editor;
    SharedPreferences settings;
    TextView textView,
            tvSaveSetting, tvCancleSetting, tvFontType;
    Typeface myTypeface;
    com.rey.material.widget.Switch switchNightMode;
    LinearLayout linearSetting;
    com.rey.material.widget.CheckBox checkBoxBig, checkBoxNormal;
    MultiSelectionSpinner multiSelectionSpinner;
    StringBuilder my_interestID = new StringBuilder();
    StringBuilder my_interest = new StringBuilder();
    List<String> s = new ArrayList<String>();
    public ArrayList<OwnerTO> ownerTOs = new ArrayList<>();
    public ArrayList<String> ownerTOstring = new ArrayList<>();
    SystemBarTintManager tintManager;
    String resultID = "";
    String result = "";
    List<String> list = new ArrayList<>();
    Toolbar toolbar;
    public static boolean isEdit = false;
    private static final String POST_Url_GET_FILTERS = "http://mylastnews.ir/ws/get_owners_filter?user=";
    private static final String POST_Url_SET_FILTERS = "http://mylastnews.ir/ws/give_owners_filter?user=";
    VolleyGeneric volleyGeneric;
    double diagonalInches;
    Dialog dialog;
    int screenWidth, screenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        DisplayMetrics metrics2 = getResources().getDisplayMetrics();
        screenWidth = (int) (metrics2.widthPixels * 0.60);
        screenHeight = (int) (metrics2.heightPixels * 0.28);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;
        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;

        diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));

        if (diagonalInches >= VolleyGeneric.INCHES) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_setting);

        textView = (TextView) findViewById(R.id.textView1);
        //  tvFontSize = (TextView) findViewById(R.id.tvFontSize);
        tvSaveSetting = (TextView) findViewById(R.id.tvSaveSetting);
        tvCancleSetting = (TextView) findViewById(R.id.tvCancleSetting);
        tvFontType = (TextView) findViewById(R.id.tvFontType);
        // tvNightMode = (TextView) findViewById(R.id.tvNightMode);
        checkBoxNormal = (CheckBox) findViewById(R.id.checkBoxNormal);
        checkBoxBig = (CheckBox) findViewById(R.id.checkBoxBig);
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.multiSelectionSpinner);


        switchNightMode = (com.rey.material.widget.Switch) findViewById(R.id.switchNightMode);
        linearSetting = (LinearLayout) findViewById(R.id.linearSetting);
        settings = getSharedPreferences(PREFS_NAME, 0);
        textView.setTextSize(settings.getInt(VolleyGeneric.SIZE, 0));
        if (settings.getBoolean("normal", false)) {
            checkBoxNormal.setChecked(true);
            checkBoxBig.setChecked(false);
        } else if (settings.getBoolean("big", false)) {
            checkBoxNormal.setChecked(false);
            checkBoxBig.setChecked(true);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("تنظیمات");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_right_white_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.primary);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));


        setOwners();


        multiSelectionSpinner.setItems(ownerTOstring);
        Log.e("444444444", getAllOwners().get(0).owner);

        settings = getSharedPreferences(PREFS_NAME, 0);
        try {
            tvFontType.setText(settings.getString("my_font_name", ""));
        } catch (Exception e) {
            tvFontType.setText("انتخاب فونت");
        }
        volleyGeneric = new VolleyGeneric(getApplicationContext());
        volleyGeneric.getJsonGet(POST_Url_GET_FILTERS + settings.getString("UserName", ""), reviewResponseListenerGetFilters(), reviewErrorListenerGetFilters(), null);


        myTypeface = Typeface.createFromAsset(getAssets(), "fonts/" + settings.getString("my_font", ""));

        tvFontType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChooseFontActivity.class));
            }
        });


        tvCancleSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvSaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();

            }
        });

        if (settings.getBoolean("big", false)) {
            checkBoxBig.setChecked(true);
        }


        if (settings.getBoolean("normal", false)) {
            checkBoxNormal.setChecked(true);
        }

        if (settings.getString(VolleyGeneric.NIGHT, "").equals("true")) {
            switchNightMode.setChecked(true);
            linearSetting.setBackgroundColor(Color.BLACK);
            textView.setTextColor(Color.WHITE);
         /*   tvFontSize.setTextColor(Color.parseColor("#d3c200"));
            tvNightMode.setTextColor(Color.parseColor("#d3c200"));
          */
            checkBoxNormal.setTextColor(Color.parseColor("#d3c200"));
            checkBoxBig.setTextColor(Color.parseColor("#d3c200"));
            multiSelectionSpinner.setBackgroundColor(Color.parseColor("#FFFFFF"));


        } else {
            switchNightMode.setChecked(false);
            textView.setTextColor(Color.BLACK);
           /* tvFontSize.setTextColor(Color.parseColor("#ababab"));
            tvNightMode.setTextColor(Color.parseColor("#ababab"));
           */
            checkBoxNormal.setTextColor(Color.parseColor("#ababab"));
            checkBoxBig.setTextColor(Color.parseColor("#ababab"));
            multiSelectionSpinner.setBackgroundColor(Color.parseColor("#DBDBDB"));
        }


        switchNightMode.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch aSwitch, boolean b) {


                if (b) {
                    switchOn();
                } else {
                    switchOff();
                }

            }
        });

        checkBoxBig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    checkBoxNormal.setChecked(false);
                    if (diagonalInches <= 7) {
                        textView.setTextSize(26);

                    } else if (diagonalInches > 7 && diagonalInches < 8.7) {
                        textView.setTextSize(40);

                    } else {
                        textView.setTextSize(48);

                    }
                }

            }
        });


        checkBoxNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    checkBoxBig.setChecked(false);
                    if (diagonalInches <= 7) {
                        textView.setTextSize(18);

                    } else if (diagonalInches > 7 && diagonalInches < 8.7) {
                        textView.setTextSize(22);

                    } else {
                        textView.setTextSize(24);

                    }


                }
            }
        });


    }

    public void setOwners() {
        ChooseOwnerActivity.ownerTOs.clear();
        for (int i = 0; i < 5; i++) {
            OwnerTO ownerTO = new OwnerTO();
            ownerTO.setOwner(getResources().getString(Utils.owners[i]));
            ownerTO.setOwnerId(Utils.ownersID[i]);
            ownerTOs.add(ownerTO);
            ownerTOstring.add(getResources().getString(Utils.owners[i]));
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ChooseFontActivity.status) {
            myTypeface = Typeface.createFromAsset(getAssets(), "fonts/" + settings.getString("my_font", ""));
            textView.setTypeface(myTypeface);
            ChooseFontActivity.status = false;
            tvFontType.setText(ChooseFontActivity.font_name);
        }





    }


    public static List<OwnersDA> getAllOwners() {
        return new Select()
                .from(OwnersDA.class)
                .execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);

    }


    public void switchOnConfirm() {
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        editor.putString(VolleyGeneric.NIGHT, "true");
        editor.apply();
        //  linearSetting.setBackgroundColor(Color.BLACK);
        //  textView.setTextColor(Color.WHITE);
       /* tvFontSize.setTextColor(Color.parseColor("#d3c200"));
        tvNightMode.setTextColor(Color.parseColor("#d3c200"));
      */  //checkBoxNormal.setTextColor(Color.parseColor("#d3c200"));
        // checkBoxBig.setTextColor(Color.parseColor("#d3c200"));
        // multiSelectionSpinner.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    public void switchOn() {
        linearSetting.setBackgroundColor(Color.BLACK);
        textView.setTextColor(Color.WHITE);
       /* tvFontSize.setTextColor(Color.parseColor("#d3c200"));
        tvNightMode.setTextColor(Color.parseColor("#d3c200"));
       */
        checkBoxNormal.setTextColor(Color.parseColor("#d3c200"));
        checkBoxBig.setTextColor(Color.parseColor("#d3c200"));
        multiSelectionSpinner.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    public void switchOff() {

        linearSetting.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);
     /*   tvFontSize.setTextColor(Color.parseColor("#ababab"));
        tvNightMode.setTextColor(Color.parseColor("#ababab"));
      */
        checkBoxNormal.setTextColor(Color.parseColor("#ababab"));
        checkBoxBig.setTextColor(Color.parseColor("#ababab"));
        multiSelectionSpinner.setBackgroundColor(Color.parseColor("#DBDBDB"));
    }

    public void switchOffConfirm() {
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        editor.putString(VolleyGeneric.NIGHT, "false");
        editor.apply();
        //   linearSetting.setBackgroundColor(Color.WHITE);
        //  textView.setTextColor(Color.BLACK);
     /*   tvFontSize.setTextColor(Color.parseColor("#ababab"));
        tvNightMode.setTextColor(Color.parseColor("#ababab"));
     */  // checkBoxNormal.setTextColor(Color.parseColor("#ababab"));
        //  checkBoxBig.setTextColor(Color.parseColor("#ababab"));
        //  multiSelectionSpinner.setBackgroundColor(Color.parseColor("#DBDBDB"));
    }

    public void checkNormal() {
        settings = getSharedPreferences(PREFS_NAME, 0);

        if (diagonalInches <= 7) {
            //  textView.setTextSize(18);

            editor = settings.edit();
            editor.putInt(VolleyGeneric.SIZE, 18);

        } else if (diagonalInches > 7 && diagonalInches < 8.7) {
            // textView.setTextSize(22);

            editor = settings.edit();
            editor.putInt(VolleyGeneric.SIZE, 22);

        } else {
            // textView.setTextSize(24);

            editor = settings.edit();
            editor.putInt(VolleyGeneric.SIZE, 24);

        }

        editor.putBoolean("normal", true);
        editor.putBoolean("big", false);
        editor.apply();
    }


    public void checkBig() {
        settings = getSharedPreferences(PREFS_NAME, 0);

        if (diagonalInches <= 7) {
            textView.setTextSize(22);

            editor = settings.edit();
            editor.putInt(VolleyGeneric.SIZE, 22);

        } else if (diagonalInches > 7 && diagonalInches < 8.7) {
            textView.setTextSize(27);

            editor = settings.edit();
            editor.putInt(VolleyGeneric.SIZE, 27);

        } else {
            textView.setTextSize(30);

            editor = settings.edit();
            editor.putInt(VolleyGeneric.SIZE, 30);

        }

        editor.putBoolean("big", true);
        editor.putBoolean("normal", false);
        editor.apply();

    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    Log.v("qqqqqq", String.valueOf(i));
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.v("qqqqq", "connected!");
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private Response.Listener<String> reviewResponseListenerGetFilters() {

        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.e("zzzzzzzzzzz", response);
                    String str = "25,23,30,31,32";
                    // String newStr="";
                    StringBuilder str3 = new StringBuilder();
                    String str2 = "";
                    //  String strNew=getAllOwners().get(0).owner;
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() == 5 || response.equals("[]")) {

                        list.clear();
                        list.add(getResources().getString(R.string.owner1));
                        list.add(getResources().getString(R.string.owner2));
                        list.add(getResources().getString(R.string.owner3));
                        list.add(getResources().getString(R.string.owner4));
                        list.add(getResources().getString(R.string.owner5));

                        multiSelectionSpinner.setSelection(list);
                    }


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Log.e("FILTERS FROM SITE: ", object.getString("owner_id"));
                        if (str.contains(object.getString("owner_id").trim())) {

                            str = str.replace(object.getString("owner_id").trim(), "");
                            Log.e("tttt", str);
                        }

                    }
                    try {
                        str = str.replace(",,,,", ",");
                    } catch (Exception e) {
                        Log.e("11111", e + "");
                    }

                    try {
                        str = str.replace(",,,", ",");
                    } catch (Exception e) {

                    }

                    try {
                        str = str.replace(",,", ",");
                    } catch (Exception e) {

                    }

                  /*  if (str.charAt(str.length()) == ',') {
                        str = str.substring(0, str.length() - 1);
                    }
                    if (str.charAt(0) == ',') {
                        str = str.substring(1, str.length());
                    }*/
                    Log.e("eeeeeeeeeeeeeeeeee", str);
                    str3.append(" ");
                    if (str.contains("25")) {
                        str3.append(getResources().getString(R.string.owner1));
                        str3.append(",");
                    }
                    if (str.contains("23")) {
                        str3.append(getResources().getString(R.string.owner2));
                        str3.append(",");
                    }
                    if (str.contains("30")) {
                        str3.append(getResources().getString(R.string.owner3));
                        str3.append(",");
                    }
                    if (str.contains("31")) {
                        str3.append(getResources().getString(R.string.owner4));
                        str3.append(",");
                    }
                    if (str.contains("32")) {
                        str3.append(getResources().getString(R.string.owner5));

                    }
                    str2 = str3.toString();
                    Log.e("vvvvvvvvvvvvvv", str2 + "!!!!");
                    new Delete().from(OwnersDA.class).where("category = ?", "all").execute();

                    OwnersDA ownersDA = new OwnersDA();
                    ownersDA.ownerID = str;
                    ownersDA.owner = str2.trim();
                    ownersDA.category = "all";
                    ownersDA.save();

                    String[] str5 = getAllOwners().get(0).owner.split(",");

                    for (String s : str5) {
                        Log.e("uuuuuuuuu", s);
                        list.add(s);

                    }

                    multiSelectionSpinner.setSelection(list);

                } catch (Exception e) {


                }


            }
        };
    }

    ;

    private Response.ErrorListener reviewErrorListenerGetFilters() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        };
    }

    private Response.Listener<String> reviewResponseListenerSetFilters() {

        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("aaaaaaaaa", response);

            }
        };
    }

    ;

    private Response.ErrorListener reviewErrorListenerSetFilters() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        };
    }


    @Override
    public void onBackPressed() {
        cancleSetting();


    }


    public void cancleSetting() {
        dialog = new Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.seting_update);


        TextView tvNoUpdate = (TextView) dialog.findViewById(R.id.tvNoUpdate);
        TextView tvYesUpdate = (TextView) dialog.findViewById(R.id.tvYesUpdate);
        LinearLayout llUpdate = (LinearLayout) dialog.findViewById(R.id.llUpdate);


        llUpdate.getLayoutParams().height = screenHeight;
        llUpdate.getLayoutParams().width = screenWidth;

        dialog.show();


        tvNoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvYesUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                save();
            }
        });

    }


    public void save() {
        my_interest = null;
        my_interestID = null;

        my_interest = new StringBuilder();
        my_interestID = new StringBuilder();
        s = multiSelectionSpinner.getSelectedStrings();


        if (s.size() == 0) {

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_something), Toast.LENGTH_SHORT).show();

        } else {

            for (int i = 0; i < s.size(); i++) {
                my_interest.append(s.get(i));

                if (s.get(i).equals(getResources().getString(R.string.owner1))) {
                    my_interestID.append("25");
                } else if (s.get(i).equals(getResources().getString(R.string.owner2))) {
                    my_interestID.append("23");
                } else if (s.get(i).equals(getResources().getString(R.string.owner3))) {
                    my_interestID.append("30");
                } else if (s.get(i).equals(getResources().getString(R.string.owner4))) {
                    my_interestID.append("31");
                } else if (s.get(i).equals(getResources().getString(R.string.owner5))) {
                    my_interestID.append("32");
                }


                my_interest.append(",");
                my_interestID.append(",");

            }

            result = my_interest.toString();
            resultID = my_interestID.toString();

            Log.e("rrrrrrrr", result);
            Log.e("rrrrrrrr", resultID);

            String give_filters = "25,23,30,31,32";
            if (resultID.contains("25")) {
                give_filters = give_filters.replace("25", "");
            }
            if (resultID.contains("23")) {
                give_filters = give_filters.replace("23", "");
            }
            if (resultID.contains("30")) {
                give_filters = give_filters.replace("30", "");
            }
            if (resultID.contains("31")) {
                give_filters = give_filters.replace("31", "");
            }
            if (resultID.contains("32")) {
                give_filters = give_filters.replace("32", "");
            }

            if (give_filters.charAt(0) == ',') {
                give_filters = give_filters.substring(1, give_filters.length());
            }
            try {
                give_filters = give_filters.replace(",,,,", ",");
            } catch (Exception e) {

            }
            try {
                give_filters = give_filters.replace(",,,", ",");
            } catch (Exception e) {

            }
            try {
                give_filters = give_filters.replace(",,", ",");
            } catch (Exception e) {

            }


            if (give_filters.startsWith(",")) {
                give_filters = give_filters.substring(1, give_filters.length());
            }
            if (give_filters.endsWith(",")) {
                give_filters = give_filters.substring(0, give_filters.length() - 1);
            }
            Log.e("tttttttttttt", give_filters);
            if (isNetworkAvailable(getApplicationContext())) {

                Log.e("MY FILTERS:", POST_Url_SET_FILTERS + settings.getString("UserName", "") + "&filter=" + give_filters);
                volleyGeneric.getJsonGet(POST_Url_SET_FILTERS + settings.getString("UserName", "") + "&filter=" + give_filters, reviewResponseListenerSetFilters(), reviewErrorListenerSetFilters(), null);

                new Delete().from(OwnersDA.class).where("category = ?", "all").execute();

                OwnersDA ownersDA = new OwnersDA();
                ownersDA.ownerID = resultID.substring(0, (resultID.length() - 1));
                ownersDA.owner = result.substring(0, (result.length() - 1));
                ownersDA.category = "all";
                ownersDA.save();


            }


            if (switchNightMode.isChecked()) {
                switchOnConfirm();
            }

            if (!switchNightMode.isChecked()) {
                switchOffConfirm();

            }

            if (checkBoxNormal.isChecked()) {
                checkNormal();
            }

            if (checkBoxBig.isChecked()) {

                checkBig();

            }
            isEdit = true;
            settings = getSharedPreferences(PREFS_NAME, 0);
            editor = settings.edit();
            editor.putString("my_font_name", ChooseFontActivity.font_name);
            editor.apply();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.change_saved), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}