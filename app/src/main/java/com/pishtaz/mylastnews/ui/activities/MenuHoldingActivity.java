package com.pishtaz.mylastnews.ui.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pishtaz.mylastnews.AppController;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.ui.fragments.register.LoginFragment;
import com.pishtaz.mylastnews.ui.fragments.register.RegisterFragment;
import com.pishtaz.mylastnews.ui.fragments.register.RoleFragment;
import com.pishtaz.mylastnews.utils.SystemBarTintManager;

public class MenuHoldingActivity extends AppCompatActivity {

    Toolbar toolbar;
    String what_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_holding);

        what_fragment = getIntent().getStringExtra("what_fragment");
        initToolbar();

        if (what_fragment.equals("login")) {
            //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
            LoginFragment loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()//.remove(fragment)
                    .replace(R.id.content, loginFragment)
                            //.addToBackStack(MainActivity.class.getName())
                    .commit();

        } else if (what_fragment.equals("register")) {
            //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
            RegisterFragment registerFragment = new RegisterFragment();
            getSupportFragmentManager().beginTransaction()//.remove(fragment)
                    .replace(R.id.content, registerFragment)
                            // .addToBackStack(MainActivity.class.getName())
                    .commit();
        }  else if (what_fragment.equals("role")) {
            //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
            RoleFragment roleFragment = new RoleFragment();
            getSupportFragmentManager().beginTransaction()//.remove(fragment)
                    .replace(R.id.content, roleFragment)
                    //.addToBackStack(MainActivity.class.getName())
                    .commit();
        }
        else if (what_fragment.equals("mainpage")) {

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_holding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==android.R.id.home)
        {
            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_right_white_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        toolbar.getContentInsetEnd();

        // >>> toolbar:: set status-bar color
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.primary);
    }
}
