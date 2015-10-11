package com.pishtaz.mylastnews.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import com.pishtaz.mylastnews.R;

//import com.squareup.picasso.Picasso;


public class ApkPicsAdapter extends BaseAdapter {
    Context contextInAdapter;
    List<String> apkPics;
    LayoutInflater inflater;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    public ApkPicsAdapter(Context inputContext, List<String> inputApkPics){

        Log.d("RRRRRRRRRRR: ", inputApkPics + "");

        contextInAdapter = inputContext;
        apkPics = inputApkPics;

        // ------------------------------------------------
        for (String str: apkPics) {
            Log.d("APK ADPT PICS URL : ", str + "");
        }
        // ------------------------------------------------


        inflater = LayoutInflater.from(contextInAdapter);
        // >>> initializing image-loader
        //imageLoader.destroy();
       if (!imageLoader.isInited()){
            imageLoader.init(ImageLoaderConfiguration.createDefault(contextInAdapter));
        }
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
               // .showImageOnLoading(android.R.drawable.screen_background_light_transparent)
                //.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                //.imageScaleType(ImageScaleType.EXACTLY)
              //  .considerExifParams(true)
               // .displayer(new RoundedBitmapDisplayer(0))
                .build();
    }

    @Override
    public int getCount() {
        return apkPics.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.apk_pics_row_layout, null);

            holder.ivApkPic = (ImageView) view.findViewById(R.id.ivApkPic);
            holder.pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

       /* Picasso.with(contextInAdapter).
                load(apkPics.get(position)).
                into(holder.ivApkPic);*/

     imageLoader.displayImage(apkPics.get(position), holder.ivApkPic, options, new SimpleImageLoadingListener() {
           @Override
            public void onLoadingStarted(String imageUri, View view) {
               holder.pbLoading.setVisibility(View.VISIBLE);
           }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.pbLoading.setVisibility(View.GONE);
           }
        });

        return view;
    }

    private class ViewHolder{
        ImageView ivApkPic;
        ProgressBar pbLoading;
    }
}
