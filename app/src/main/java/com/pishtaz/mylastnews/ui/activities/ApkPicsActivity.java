package com.pishtaz.mylastnews.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import com.pishtaz.mylastnews.R;

/**************************************************
 This activity is responsible for representing APK-Pics in View-Pager
 from Horizontal-List-View in APKInfoFragment.
 *************************************************/

public class ApkPicsActivity extends Activity {

    ViewPager vpApkPics;
    ApkPicsAdapter vpApkPicsAdapter;

    ArrayList<String> apkPicUrls;
    // >>> position of item that clicked in horizontal-list-view of Apk-Pics
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_pics);

        // >>> retrieving argument from NewsInfoFragment
        Intent intent = getIntent();
        apkPicUrls = intent.getStringArrayListExtra("apkPicsUrls");
        position   = intent.getIntExtra("position", 0);

        // >>> set up view-pager
        vpApkPics = (ViewPager) findViewById(R.id.vpApkPics);

        vpApkPicsAdapter = new ApkPicsAdapter(apkPicUrls);

        vpApkPics.setAdapter(vpApkPicsAdapter);
        vpApkPics.setCurrentItem(position);
        vpApkPics.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    }

    private class ApkPicsAdapter extends PagerAdapter {
        ArrayList<String> apkPics;
        LayoutInflater inflater;

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options;

        ApkPicsAdapter(ArrayList<String> inputApkPics) {
            apkPics = inputApkPics;
            inflater = LayoutInflater.from(getApplicationContext());

            if (!imageLoader.isInited()) {
                imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
            }
            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(600))
                    .build();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public Object instantiateItem(ViewGroup container, final int position) {
            final ViewHolder holder;

            View view = inflater.inflate(R.layout.apk_pics_vp_item_layout, container, false);
            holder = new ViewHolder();

            holder.ivApkPicView = (ImageView) view.findViewById(R.id.ivApkPicView);
            holder.pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);


            imageLoader.displayImage(apkPics.get(position), holder.ivApkPicView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.pbLoading.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.pbLoading.setVisibility(View.GONE);
                }
            });
            // add view to ViewGroup
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return apkPics.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public class ViewHolder {
            ImageView ivApkPicView;
            ProgressBar pbLoading;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_apk_pics, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
