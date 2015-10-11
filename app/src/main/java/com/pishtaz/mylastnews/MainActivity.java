package com.pishtaz.mylastnews;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pishtaz.mylastnews.db_model.OtherAppDA;
import com.pishtaz.mylastnews.db_model.OwnersDA;
import com.pishtaz.mylastnews.models.OwnerTO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.pishtaz.mylastnews.ui.activities.MenuHoldingActivity;
import com.pishtaz.mylastnews.ui.fragments.CategoriesPagerFragment;
import com.pishtaz.mylastnews.ui.fragments.PrimaryFragment;
import com.pishtaz.mylastnews.ui.fragments.menu.SettingActivity;
import com.pishtaz.mylastnews.ui.fragments.register.RegisterFragment;
import com.pishtaz.mylastnews.ui.fragments.search.SearchActivity;
import com.pishtaz.mylastnews.update.MyWebService;
import com.pishtaz.mylastnews.utils.SlidingTabLayout;
import com.pishtaz.mylastnews.utils.SystemBarTintManager;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    Dialog dialog;
    FragmentTransaction ft;
    CursorLoader cursorLoader;
    Toolbar toolbar;
    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences.Editor editor;
    SharedPreferences settings;
    private AccountHeader headerResult = null;
    Drawer result;
    IProfile profile;
    Bundle savedInstanceState;
    ViewPager pager;
    public static String selectedAppID = null;
    List<OtherAppDA> otherAppDAs = new ArrayList<>();
    private String titles[] = new String[]{

            "اجتماعی", //57
            "گردشگری", //61
            "فرهنگ و هنر", //56
            "دانش و فناوری",  //60
            "ورزشی",//58
            "اقتصادی",//55
            "سیاسی",//54
            "بین الملل",//59
            "خبرهای برگزیده",
            "صفحه اصلی"};
    SlidingTabLayout slidingTabLayout;
    private String versionName;
    private long downloadReference;
    private MyWebReceiver receiver;
    public static boolean isLogin = false;
    private DownloadManager downloadManager;
    public static String appURI = "";
    double diagonalInches;
    int screenWidth, screenHeight;
    private static final String POST_Url = "http://webservice.myappstores.ir/app.asmx/PishtazApp";
    TabsPagerAdapter adapter;
    public static OtherAppDA appCenterDA = new OtherAppDA();
    public static AlertDialog alertDialogForInstallingAppcenter;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    public static DownloadManager manager;
    public static DownloadManager.Request request;
    public static long downloadAppCenterReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;


        try {
            File file = new File("/sdcard/" + Environment.DIRECTORY_DOWNLOADS + "/MyLastNews.apk");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {

        }


        settings = getSharedPreferences(PREFS_NAME, 0);
        if (!settings.getString("first_run", "").equals("false")) {


            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("AppId", VolleyGeneric.APP_ID);
            VolleyGeneric volley = new VolleyGeneric(getApplicationContext());
            volley.getJson(POST_Url, reviewResponseListenerGetApp(), reviewErrorListenerGetApp(), hashMap);

            editor = settings.edit();
            editor.putString("first_run", "false");
            editor.apply();
            insertOwners();
        }

        DisplayMetrics metrics2 = getResources().getDisplayMetrics();
        screenWidth = (int) (metrics2.widthPixels * 0.70);
        screenHeight = (int) (metrics2.heightPixels * 0.35);


        try {

            if (!imageLoader.isInited()) {
                imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
            }
            options = new DisplayImageOptions.Builder()

                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(600))
                    .build();
        } catch (Exception ex) {

            Log.e("ERROR IMAGE LOADER : ", ex + "");

        }

        otherAppDAs.clear();
        otherAppDAs = getOtherApps();
        appCenterDA = getAppCenterInfo();

        settings = getSharedPreferences(PREFS_NAME, 0);
        if (!settings.getString(VolleyGeneric.FIRST_RUN, "").equals("false")) {
            editor = settings.edit();
            editor.putString(VolleyGeneric.FIRST_RUN, "false");
            editor.putString(VolleyGeneric.NIGHT, "false");
            editor.putString("my_font", "IRAN Sans Light.ttf");
            editor.putString("my_font_name", "فونت شماره یک");
            editor.putInt(VolleyGeneric.SIZE, 18);
            editor.putBoolean("normal", true);
            editor.putBoolean("normal", true);
            editor.apply();

        }


        File file = new File(VolleyGeneric.FILE);
        if (!file.exists()) {
            try {

                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("[{\"User_Id\":\"0\",\"UserName\":\"0\",\"token\":\"0\"}]");
                writer.flush();
                writer.close();
            } catch (Exception e) {

            }
        }

        initToolbar();
        setMenu(savedInstanceState);

        pager = (ViewPager) findViewById(R.id.pager);

        adapter = new TabsPagerAdapter(getSupportFragmentManager(), titles);
        pager.setAdapter(adapter);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(pager);
        pager.setCurrentItem(9);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        slidingTabLayout.setBackgroundColor(Color.parseColor("#FF000000"));
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.parseColor("#ffffff");
            }
        });


        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        versionName = pInfo.versionName;
        IntentFilter filter = new IntentFilter(MyWebReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyWebReceiver();
        registerReceiver(receiver, filter);
        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        try {
            registerReceiver(downloadReceiver, filter);
        } catch (Exception e) {

        }

        if (isNetworkAvailable(this)) {
            Intent msgIntent = new Intent(this, MyWebService.class);
            // msgIntent.putExtra(MyWebService.REQUEST_STRING, "http://newwebservice.mp4.ir/Update/UpdateMP4.txt");
            msgIntent.putExtra(MyWebService.PACKAGE_NAME, pInfo.packageName);
            startService(msgIntent);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == R.id.search) {

           /* SearchFragment searchFragment = new SearchFragment();
            Fragment fragment = fragmentManager.findFragmentById(R.id.content);
            fragmentManager.beginTransaction().remove(fragment)
                    .replace(R.id.content, searchFragment)
                    .addToBackStack(SearchFragment.class.getName())
                    .commit();*/
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));


        } else if (item.getItemId() == R.id.other_app) {
            try {
                Log.e("111111", "1111111111");
                startActivity(new Intent(MainActivity.this, OtherAppActivity.class));
            } catch (Exception e) {
                Log.e("11111111", e + "");
            }
        }

        return super.onOptionsItemSelected(item);
    }


    public void setMenu(final Bundle savedInstanceState) {

        StringBuilder builder = null;
        try {
            InputStream stream = new FileInputStream(VolleyGeneric.FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {

        }
        try {
            JSONArray array = new JSONArray(builder.toString());
            JSONObject object = array.getJSONObject(0);
            if (object.getString("User_Id").equals("0")) {
                isLogin = false;
            } else {
                isLogin = true;
                settings = getSharedPreferences(PREFS_NAME, 0);
                editor = settings.edit();
                editor.putString("User_Id", object.getString("User_Id"));
                editor.putString("UserName", object.getString("UserName"));
                editor.putString("token", object.getString("Token"));
                editor.apply();
            }
        } catch (Exception e) {

        }


        Log.e("ggggggggggggg", isLogin + "");
        settings = getSharedPreferences(PREFS_NAME, 0);
        if (!isLogin) {
            profile = new ProfileDrawerItem().withName("کاربر مهمان").withEmail("").withIdentifier(10);
        } else {

            if (settings.getString("User_Profile_Image", "").contains("http:")) {
                profile = new ProfileDrawerItem().withName(settings.getString("UserName", "")).withEmail("").withIdentifier(10).withIcon(settings.getString("User_Profile_Image", ""));
            } else {
                profile = new ProfileDrawerItem().withName(settings.getString("UserName", "")).withEmail("").withIdentifier(10).withIcon("http://www.imageplus.ir/Images/Icons/User.png");

            }

        }

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)

                .withHeaderBackground(R.drawable.back_menu_cover)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                                                 @Override
                                                 public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                                                     //sample usage of the onProfileChanged listener
                                                     //if the clicked item has the identifier 1 add a new profile ;)
                                                     if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == 10) {


                                                         if (!isLogin) {
                                                          /*   Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
                                                             LoginFragment loginFragment = new LoginFragment();
                                                             getSupportFragmentManager().beginTransaction().remove(fragment)
                                                                     .replace(R.id.content, loginFragment)
                                                                     .addToBackStack(MainActivity.class.getName())
                                                                     .commit();*/


                                                             startActivity(new Intent(getApplicationContext(), MenuHoldingActivity.class).putExtra("what_fragment", "login"));

                                                         } else {

                                                         }


                                                     }

                                                     //false if you have not consumed the event and it should close the drawer
                                                     return false;
                                                 }
                                             }

                )
                .

                        withSavedInstance(savedInstanceState)

                .

                        build();

        if (!isLogin)

        {
            result = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withTranslucentStatusBar(true)
                    .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                    .addDrawerItems(
                            //    new PrimaryDrawerItem().withName(R.string.safhe_asli).withIcon(R.drawable.home_icon).withIdentifier(1).withCheckable(false),
                            // new PrimaryDrawerItem().withName(R.string.mahsoolate_ma).withIcon(R.drawable.other_app).withIdentifier(2).withCheckable(false),
                            new PrimaryDrawerItem().withName(R.string.ghavanin).withIcon(R.drawable.rules_icon).withIdentifier(3).withCheckable(false),
                            new PrimaryDrawerItem().withName(R.string.about_app).withIcon(R.drawable.about_app).withIdentifier(50).withCheckable(false),
                            new SectionDrawerItem().withName("")
                    )

                    .addStickyDrawerItems(

                            new PrimaryDrawerItem().withName(R.string.setting).withIcon(R.drawable.setting).withIdentifier(20).withCheckable(false)

                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                            //check if the drawerItem is set.
                            //there are different reasons for the drawerItem to be null
                            //--> click on the header
                            //--> click on the footer
                            //those items don't contain a drawerItem

                            if (drawerItem != null) {

                                if (drawerItem.getIdentifier() == 1) {


                              /*    //  Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
                                    PrimaryFragment primaryFragment = new PrimaryFragment();
                                    getSupportFragmentManager().beginTransaction()//.remove(fragment)
                                            .replace(R.id.content, primaryFragment)
//                                            .addToBackStack(MainActivity.class.getName())
                                            .commit();*/


                                }
                                else if (drawerItem.getIdentifier()==50){
                                    startActivity(new Intent(getApplicationContext(),AboutAppActivity.class));
                                }
                                else if (drawerItem.getIdentifier() == 2) {

                                    startActivity(new Intent(getApplicationContext(), OtherAppActivity.class));


                                } else if (drawerItem.getIdentifier() == 3) {

                                    startActivity(new Intent(getApplicationContext(), MenuHoldingActivity.class).putExtra("what_fragment", "role"));

                                 /*   Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
                                    RoleFragment roleFragment = new RoleFragment();
                                    getSupportFragmentManager().beginTransaction().remove(fragment)
                                            .replace(R.id.content, roleFragment)
                                            .addToBackStack(MainActivity.class.getName())
                                            .commit();
*/
                                } else if (drawerItem.getIdentifier() == 5) {


                                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
                                    RegisterFragment registerFragment = new RegisterFragment();
                                    getSupportFragmentManager().beginTransaction().remove(fragment)
                                            .replace(R.id.content, registerFragment)
                                            .addToBackStack(MainActivity.class.getName())
                                            .commit();
                                } else if (drawerItem.getIdentifier() == 20) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                                }


                            }

                            return false;
                        }

                    }).
                            withSavedInstance(savedInstanceState)
                    .
                            withShowDrawerOnFirstLaunch(true)
                    .

                            build();

        } else

        {
            result = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
//.withTranslucentStatusBar(false)
                    .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                    .addDrawerItems(
                            //   new PrimaryDrawerItem().withName(R.string.safhe_asli).withIcon(R.drawable.home_icon).withIdentifier(1).withCheckable(false),
                            //  new PrimaryDrawerItem().withName(R.string.my_page).withIcon(R.drawable.home_icon).withIdentifier(2).withCheckable(false),
                            //  new PrimaryDrawerItem().withName(R.string.mahsoolate_ma).withIcon(R.drawable.other_app).withIdentifier(2).withCheckable(false),
                            new PrimaryDrawerItem().withName(R.string.ghavanin).withIcon(R.drawable.rules_icon).withIdentifier(3).withCheckable(false),
                            new PrimaryDrawerItem().withName(R.string.about_app).withIcon(R.drawable.about_app).withIdentifier(50).withCheckable(false),
                            new SectionDrawerItem().withName(""),
                            new SecondaryDrawerItem().withName(R.string.khoroj).withIcon(R.drawable.login_icon).withIdentifier(4).withCheckable(false)

                    )
                    .addStickyDrawerItems(
                            new PrimaryDrawerItem().withName(R.string.setting).withIcon(R.drawable.setting).withIdentifier(20).withCheckable(false)
                            //  new SwitchDrawerItem().withName(R.string.night_mode).withChecked(false).withOnCheckedChangeListener(onCheckedChangeListener)

                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                            //check if the drawerItem is set.
                            //there are different reasons for the drawerItem to be null
                            //--> click on the header
                            //--> click on the footer
                            //those items don't contain a drawerItem

                            if (drawerItem != null) {

                                if (drawerItem.getIdentifier() == 1) {


                                    //        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
                                    PrimaryFragment primaryFragment = new PrimaryFragment();
                                    getSupportFragmentManager().beginTransaction()//.remove(fragment)
                                            .replace(R.id.content, primaryFragment)
                                                    //    .addToBackStack(MainActivity.class.getName())
                                            .commit();


                                } else if (drawerItem.getIdentifier() == 2) {


                               /*     Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
                                    ProfileFragmentUser profileFragment = new ProfileFragmentUser();
                                    settings = getSharedPreferences(PREFS_NAME, 0);

                                    Bundle b = new Bundle();
                                    String username = settings.getString("UserName", "");
                                    b.putString("username", username);
                                    profileFragment.setArguments(b);

                                    getSupportFragmentManager().beginTransaction().remove(fragment)
                                            .replace(R.id.content, profileFragment)
                                            .addToBackStack(MainActivity.class.getName())
                                            .commit();
*/

                                } else if (drawerItem.getIdentifier() == 3) {


                                    startActivity(new Intent(getApplicationContext(), MenuHoldingActivity.class).putExtra("what_fragment", "role"));

                                } else if (drawerItem.getIdentifier() == 20) {
                                    startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                                }

                                else if (drawerItem.getIdentifier()==50){
                                    startActivity(new Intent(getApplicationContext(),AboutAppActivity.class));
                                }

                                else if (drawerItem.getIdentifier() == 4) {

                                    settings = getSharedPreferences(PREFS_NAME, 0);
                                    editor = settings.edit();
                                    editor.putString("User_Id", "");
                                    editor.putString("token", "");
                                    editor.apply();
                                    isLogin = false;


                                    File file = new File(VolleyGeneric.FILE);
                                    if (file.exists()) {
                                        file.delete();
                                        try {

                                            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                            writer.write("[{\"User_Id\":\"0\",\"UserName\":\"0\",\"token\":\"0\"}]");
                                            writer.flush();
                                            writer.close();
                                        } catch (Exception e) {

                                        }
                                    }


                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();

                                }
                            }

                            return false;
                        }

                    }).
                            withSavedInstance(savedInstanceState)
                    .
                            withShowDrawerOnFirstLaunch(true)
                    .

                            build();
        }

        if (savedInstanceState == null)

        {
            // set the selection to the item with the identifier 11
            result.setSelectionByIdentifier(11, false);

            //set the active profile
            headerResult.setActiveProfile(profile);
        }

    }


    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.logo_news);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }*/
        // >>> toolbar:: set drawer-icon to Toolbar
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_white_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        toolbar.getContentInsetEnd();

        // >>> toolbar:: set status-bar color
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.primary);
    }


    @Override
    protected void onResume() {

        super.onResume();


        try {
            if (SettingActivity.isEdit) {
                //    Log.e("qqqqqqqqqqqqqqq", "Qqqqqqqqqqq");
                adapter = new TabsPagerAdapter(getSupportFragmentManager(), titles);
                pager.setAdapter(adapter);
                // adapter.notifyDataSetChanged();
                pager.setCurrentItem(9);
                SettingActivity.isEdit = false;
            }
        } catch (Exception e) {

        }


        StringBuilder builder = null;
        try {
            InputStream stream = new FileInputStream(VolleyGeneric.FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {

        }

        try {
            JSONArray array = new JSONArray(builder.toString());
            JSONObject object = array.getJSONObject(0);
            if (object.getString("User_Id").equals("0")) {
                settings = getSharedPreferences(PREFS_NAME, 0);
                editor = settings.edit();
                editor.putString("User_Id", "");
                editor.apply();

                isLogin = false;

            } else {
                isLogin = true;
                settings = getSharedPreferences(PREFS_NAME, 0);
                editor = settings.edit();
                editor.putString("User_Id", object.getString("User_Id"));
                editor.putString("UserName", object.getString("UserName"));
                editor.apply();
            }


        } catch (Exception e) {

        }

    }

    public class TabsPagerAdapter extends FragmentStatePagerAdapter {

        String[] titles;


        public TabsPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:
                    // Top Rated fragment activity

                    return CategoriesPagerFragment.newInstance("57");
                case 1:
                    // Games fragment activity

                    return CategoriesPagerFragment.newInstance("61");
                case 2:
                    // Movies fragment activity

                    return CategoriesPagerFragment.newInstance("56");
                case 3:
                    // Movies fragment activity

                    return CategoriesPagerFragment.newInstance("60");
                case 4:
                    // Movies fragment activity

                    return CategoriesPagerFragment.newInstance("58");
                case 5:

                    // Movies fragment activity

                    return CategoriesPagerFragment.newInstance("55");
                case 6:
                    // Movies fragment activity

                    return CategoriesPagerFragment.newInstance("54");
                case 7:
                    // Movies fragment activity

                    return CategoriesPagerFragment.newInstance("59");
                case 8:
                    // Movies fragment activity

                    return CategoriesPagerFragment.newInstance("1");
                case 9:
                    // Movies fragment activity

                    return new PrimaryFragment();

            }

            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 10;
        }

   /* @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }*/

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position >= getCount()) {
                FragmentManager manager = ((Fragment) object).getFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove((Fragment) object);
                trans.commit();
            }
        }

       /* @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            ImageView imageView = (ImageView) view.findViewById(R.id.ivNewsPic);
            if (imageView != null) {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                bitmap.recycle();
                bitmap = null;
            }
            ((ViewPager) container).removeView(view);
            view = null;
        }*/


        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public class MyWebReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "com.example.update.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {

            String reponseMessage = intent.getStringExtra(MyWebService.RESPONSE_MESSAGE);
            Log.v("lllllllllllll", reponseMessage);


            try {
                JSONArray jsonArray = new JSONArray(reponseMessage);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                String latestVersion = jsonObject.getString("Version");
                //get the lastest application URI from the JSON string
                appURI = jsonObject.getString("Path");
                Log.d("kkkkkkk", appURI + "");
                //check if we need to upgrade?
                if (!latestVersion.equals(versionName)) {


                    try {
                        dialog.hide();
                    } catch (Exception e) {

                    }
                    try {
                        openDialog1();
                    } catch (Exception e) {

                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERRRRRRRR", e + "");
            }

        }

    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {

                Log.v("pppppp", "Downloading of the new app version complete");
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadReference),
                        "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(installIntent);

            }
            else if (downloadAppCenterReference == referenceId) {
                Log.d("OthetApp", " referenceId>>> " + referenceId);

                alertDialogForInstallingAppcenter.dismiss();

                Intent installAppCenterIntent = new Intent(Intent.ACTION_VIEW);
                installAppCenterIntent.setDataAndType(manager.getUriForDownloadedFile(downloadAppCenterReference),
                        "application/vnd.android.package-archive");
                installAppCenterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installAppCenterIntent);
//                startActivityForResult(installAppCenterIntent, 5000);

            }
        }
    };


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
                            otherAppDA.save();


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


    public void insertOwners() {

        OwnersDA ownersDA = new OwnersDA();
        ownersDA.owner = "خبرگزاری فارس" + "," + "خبرگزاری مهر" + "," + "خبرگزاری ایرنا" + "," + "خبرگزاری تسنیم" + "," + "باشگاه خبرنگاران جوان";
        ownersDA.ownerID = "25,23,30,31,32";
        ownersDA.category = "all";
       /* settings=getSharedPreferences(PREFS_NAME,0);
        try {
            ownersDA.username = settings.getString("UserName", "");
        }catch (Exception e){

        }*/
        ownersDA.save();

    }


    public List<OtherAppDA> getOtherApps() {

        return new Select()
                .from(OtherAppDA.class)
                .execute();

    }

    public OtherAppDA getAppCenterInfo() {

        OtherAppDA appCenterDA = new OtherAppDA();

        for (int i = 0; i < otherAppDAs.size(); i++) {
            if (otherAppDAs.get(i).apkFileName.equals("com.pishtaz.appcenter")) {
                appCenterDA = otherAppDAs.get(i);
                break;
            }
        }

        return appCenterDA;

    }


    private BroadcastReceiver newPackageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("OtherApp", "app center has been installed");

            Intent intentMoveToApp = new Intent("appstore.pishtaz.com.appstore.ui.fragments.ApkInfoActivity");
            intentMoveToApp.putExtra("AppID", selectedAppID);
            intentMoveToApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intentMoveToApp);

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 5000) {
            if (resultCode == Activity.RESULT_OK) {
//                String result=data.getStringExtra("result");

                Log.d("OtherApp", "selectedAppID>>>" + selectedAppID);

                Intent intentMoveToApp = new Intent("appstore.pishtaz.com.appstore.ui.fragments.ApkInfoActivity");
                intentMoveToApp.putExtra("AppID", selectedAppID);
                intentMoveToApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intentMoveToApp);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    public void openDialog1() {

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_update);


        TextView tvUpdateMsg = (TextView) dialog.findViewById(R.id.tvUpdateMsg);
        TextView tvNoUpdate = (TextView) dialog.findViewById(R.id.tvNoUpdate);
        TextView tvYesUpdate = (TextView) dialog.findViewById(R.id.tvYesUpdate);
        LinearLayout llUpdate = (LinearLayout) dialog.findViewById(R.id.llUpdate);

       /* tvUpdateMsg.setTypeface(typeNazanin);
        tvNoUpdate.setTypeface(typeNazanin);
        tvYesUpdate.setTypeface(typeNazanin);*/


        llUpdate.getLayoutParams().height = screenHeight;
        llUpdate.getLayoutParams().width = screenWidth;

        dialog.show();


        tvNoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        tvYesUpdate.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {

                                               // >>> TODO: if (AppCenter is NOT installed then first install it) else (move to the corresponding page)
                                               if (appInstalledOrNot(appCenterDA.apkFileName)) {

                                                   Log.d("$$$$", "app center is installed");

                                                   Intent intentMoveToApp = new Intent("appstore.pishtaz.com.appstore.ui.fragments.ApkInfoActivity");
                                                   intentMoveToApp.putExtra("AppID", VolleyGeneric.APP_ID);
                                                   intentMoveToApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                   getApplicationContext().startActivity(intentMoveToApp);

                                               } else {

                                                   showDownloadAppCenterDialog(getApplicationContext());

                                               }

                /*downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Uri Download_Uri = Uri.parse(appURI);
                DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                request.setAllowedOverRoaming(false);
                request.setTitle("?????? ????????");
                //request.setDestinationInExternalFilesDir(MainActivity.this, "/sdcard/", "arashhhhhh.apk");
                File file = new File("/sdcard/" + Environment.DIRECTORY_DOWNLOADS + "/com.pishtaz.mysd.apk");
                if (file.exists()) {
                    file.delete();
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "com.pishtaz.mysd.apk");
                downloadReference = downloadManager.enqueue(request);
                Log.d("OthetApp", " downloadReference>>> " + downloadReference);*/


                                               dialog.hide();
                                           }
                                       }

        );

    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void showDownloadAppCenterDialog(Context context) {

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View customInstallApkView = li.inflate(R.layout.install_app_center_dialog_layout, null);

        AlertDialog.Builder installApkDialog = new AlertDialog.Builder(MainActivity.this);
        installApkDialog.setView(customInstallApkView);

        ImageView ivFileIcon = (ImageView) customInstallApkView.findViewById(R.id.ivFileIconIa);
        TextView tvInstallAppCenterMsg = (TextView) customInstallApkView.findViewById(R.id.tvFileNameIa);
        final ProgressView pbDownloadProgress = (ProgressView) customInstallApkView.findViewById(R.id.pvDownloadProgressIa);
        Button btnCancel = (Button) customInstallApkView.findViewById(R.id.btnCancelIa);
        Button btnInstallApk = (Button) customInstallApkView.findViewById(R.id.btnInstallApkIa);

        tvInstallAppCenterMsg.setText(context.getString(R.string.install_app_center_msg));
      //  tvInstallAppCenterMsg.setTypeface(MainActivity.typeNazanin);

        imageLoader.displayImage("http://myappstores.ir" + MainActivity.appCenterDA.ThumIcon, ivFileIcon, options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                //  holder.pbLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // holder.pbLoading.setVisibility(View.GONE);
            }
        });

        alertDialogForInstallingAppcenter = installApkDialog.create();
        alertDialogForInstallingAppcenter.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogForInstallingAppcenter.dismiss();
                manager.remove(MainActivity.downloadAppCenterReference);

            }
        });

        btnInstallApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pbDownloadProgress.setVisibility(View.VISIBLE);

                selectedAppID = VolleyGeneric.APP_ID;

                Log.d("$$$$", "app center is NOT installed");
//                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(otherAppDAs.get(position).apkFile));
                request = new DownloadManager.Request(Uri.parse("http://myappstores.ir/myappstores/Files/com.pishtaz.appcenter.apk"));

                request.setDescription(appCenterDA.Title);
                request.setTitle(appCenterDA.Title);

                File file = new File(Environment.DIRECTORY_DOWNLOADS + appCenterDA.apkFileName + ".apk");
                if (file.exists()) {
                    file.delete();
                }

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appCenterDA.apkFileName + ".apk");
                request.setVisibleInDownloadsUi(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                downloadAppCenterReference = manager.enqueue(request);
                Log.d("OthetApp", " AppCenterReference>>> " + downloadAppCenterReference);

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.downloading), Toast.LENGTH_SHORT).show();

            }
        });

    }

}


