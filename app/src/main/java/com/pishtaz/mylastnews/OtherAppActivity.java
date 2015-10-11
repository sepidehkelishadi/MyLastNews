package com.pishtaz.mylastnews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.StackView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pishtaz.mylastnews.db_model.OtherAppDA;
import com.pishtaz.mylastnews.models.ApkTO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class OtherAppActivity extends Activity {


    StackView stackView;
    ImageView imageViewApp;
    TextView textViewApp;
    ImageLoader imageLoader = ImageLoader.getInstance();
    List<OtherAppDA> otherAppDAs = new ArrayList<>();
    //    OtherAppDA appCenterDA = new OtherAppDA();
    DisplayImageOptions options;
    ImageAdapter adapter;
    public static ArrayList<ApkTO> apkTOs = new ArrayList<>();
    double diagonalInches;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("111111111", "1");
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;
        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;

        diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));
        Log.e("111111111", "2");
        if (diagonalInches >= VolleyGeneric.INCHES) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Log.e("111111111", "3");
        setContentView(R.layout.activity_other_app);
        Log.e("111111111", "4");

        otherAppDAs.clear();
        otherAppDAs = getOtherApps();
        MainActivity.appCenterDA = getAppCenterInfo();


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
        stackView = (StackView) this.findViewById(R.id.stack_view);
        adapter = new ImageAdapter(getApplicationContext());
        stackView.setAdapter(adapter);

        // >>> this will happen when I get bored from coding offLine-mode
        Typeface typeNazanin = Typeface.createFromAsset(getAssets(), "fonts/BNazanin.ttf");
        TextView tvOurProducts = (TextView) findViewById(R.id.tvOurProducts);
        TextView tvComingSoon = (TextView) findViewById(R.id.tvComingSoon);
        tvOurProducts.setTypeface(typeNazanin);
        tvComingSoon.setTypeface(typeNazanin);

        Log.e("rrrrrrrr", otherAppDAs.size() + "");

    }


    public class ImageAdapter extends BaseAdapter {
        private Context contxt;


        public ImageAdapter(Context c) {
            contxt = c;
        }

        public int getCount() {
            return otherAppDAs.size();
        }

        public Object getItem(int position) {

            return position;

        }

        @SuppressLint("NewApi")
        @SuppressWarnings("deprecation")
        public View getView(final int position, View view, ViewGroup
                parent) {
            if (view == null) {
                LayoutInflater vi = (LayoutInflater)
                        getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.stack_item, null, false);
            }

            imageViewApp = (ImageView) view.findViewById(R.id.imageViewApp);
            textViewApp = (TextView) view.findViewById(R.id.textViewApp);

            Log.e("sssssssss", otherAppDAs.get(position).Title);
            Log.e("sssssssss", otherAppDAs.get(position).ThumIcon);
            Log.e("$$$", "APK: (" + position + ")" + otherAppDAs.get(position).apkFileName
                            + " >>> APK ID: " + otherAppDAs.get(position).AppID
                            + " >>> APK TITLE: " + otherAppDAs.get(position).Title
                            + " >>> APK size: " + otherAppDAs.get(position).apkSize
            );
            Log.e("$$$", otherAppDAs.get(position).apkFile);
            textViewApp.setText(otherAppDAs.get(position).Title);



            /*imageViewApp.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Log.e("image click", "IMAGE CLICK");
                }
            });*/


            try {

                imageLoader.displayImage("http://myappstores.ir" + otherAppDAs.get(position).ThumIcon, imageViewApp, options, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        //  holder.pbLoading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // holder.pbLoading.setVisibility(View.GONE);
                    }
                });

            } catch (Exception e) {


            }

            imageViewApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // >>> added here from
                    Log.e("image click", "IMAGE CLICK");
                    MainActivity.selectedAppID = otherAppDAs.get(position).AppID;

                    boolean installed = appInstalledOrNot(otherAppDAs.get(position).apkFileName);
                    if (installed) {

                        Intent LaunchIntent = getPackageManager()
                                .getLaunchIntentForPackage(otherAppDAs.get(position).apkFileName);
                        startActivity(LaunchIntent);

                    } else {

                        // >>> TODO: if (AppCenter is NOT installed then first install it) else (move to the corresponding page)
                        if (appInstalledOrNot(MainActivity.appCenterDA.apkFileName)) {

                            Log.d("$$$$", "app center is installed");

                            Intent intentMoveToApp = new Intent("appstore.pishtaz.com.appstore.ui.fragments.ApkInfoActivity");
                            intentMoveToApp.putExtra("AppID", otherAppDAs.get(position).AppID);
                            intentMoveToApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intentMoveToApp);

                        } else {

                            LayoutInflater li = LayoutInflater.from(OtherAppActivity.this);
                            View customInstallApkView = li.inflate(R.layout.install_app_center_dialog_layout, null);

                            AlertDialog.Builder installApkDialog = new AlertDialog.Builder(OtherAppActivity.this);
                            installApkDialog.setView(customInstallApkView);

                            ImageView ivFileIcon = (ImageView) customInstallApkView.findViewById(R.id.ivFileIconIa);
                            TextView tvInstallAppCenterMsg = (TextView) customInstallApkView.findViewById(R.id.tvFileNameIa);
                            final ProgressView pbDownloadProgress = (ProgressView) customInstallApkView.findViewById(R.id.pvDownloadProgressIa);
                            Button btnCancel = (Button) customInstallApkView.findViewById(R.id.btnCancelIa);
                            Button btnInstallApk = (Button) customInstallApkView.findViewById(R.id.btnInstallApkIa);

                            tvInstallAppCenterMsg.setText(getApplicationContext().getString(R.string.install_app_center_msg));
                           // tvInstallAppCenterMsg.setTypeface(MainActivity.typeNazanin);

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

                            MainActivity.alertDialogForInstallingAppcenter = installApkDialog.create();
                            MainActivity.alertDialogForInstallingAppcenter.show();

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    MainActivity.alertDialogForInstallingAppcenter.dismiss();
//                                    MainActivity.manager.remove(MainActivity.downloadAppCenterReference);

                                }
                            });

                            btnInstallApk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    pbDownloadProgress.setVisibility(View.VISIBLE);
                                    Log.d("$$$$", "app center is NOT installed");
                                    MainActivity.request = new DownloadManager.Request(Uri.parse("http://myappstores.ir/myappstores/Files/com.pishtaz.appcenter.apk"));
                                    MainActivity.request.setDescription("در حال دانلود");
                                    MainActivity.request.setTitle(MainActivity.appCenterDA.Title);

                                    File file = new File(Environment.DIRECTORY_DOWNLOADS + MainActivity.appCenterDA.apkFileName + ".apk");
                                    if (file.exists()) {
                                        file.delete();
                                    }

                                    MainActivity.request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, MainActivity.appCenterDA.apkFileName + ".apk");
                                    MainActivity.request.setVisibleInDownloadsUi(true);
                                    MainActivity.request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                    MainActivity.manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    MainActivity.manager.enqueue(MainActivity.request);

                                    MainActivity.downloadAppCenterReference = MainActivity.manager.enqueue(MainActivity.request);
                                    Log.d("OthetApp", " AppCenterReference>>> " + MainActivity.downloadAppCenterReference);


                            /*Intent moveToIaService = new Intent(OtherAppActivity.this, DownloadService.class);
                            moveToIaService.putExtra("receiver", new DownReceiverIa(new Handler()));
                            moveToIaService.putExtra("url", downloadUrl);
                            moveToIaService.putExtra("name", fileTOs.get(position).getFileName());
//        moveToDwService.putExtra("dir", getOutputMediaDir());
                            moveToIaService.putExtra("dir", getCurrentPathOnStorageForDw() *//*+ File.separator*//*);

                            moveToIaService.putExtra("resultCode", Integer.valueOf(fileTOs.get(position).getFileSize()));
                            editor.putString(apkFileKey, fileTOs.get(position).getFileSize());
                            editor.putString(apkFileKey + "1", apkPath);
//                editor.putString(fileTOs.get(position).getFileUniqueId(), fileTOs.get(position).getFileType());// delete it
                            editor.apply();

                            Log.d("MYSD_PATH_Dw", getCurrentPathOnStorageForDw() + File.separator + fileTOs.get(position).getFileName());
                            Log.d("MYSD_PATH_Dw", downloadUrl);

                            startService(moveToIaService);*/

                                    ApkTO apkTO = new ApkTO();

                                    Log.e("eeeeeeeeee", MainActivity.appCenterDA.apkFileName);

                                    apkTO.setPackageName(MainActivity.appCenterDA.apkFileName);
                                    apkTO.setSize(MainActivity.appCenterDA.apkSize);
                                    apkTOs.add(apkTO);


                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.downloading), Toast.LENGTH_SHORT).show();
//                                    finish();

                                }
                            });





                        }


                    }

                }
            });


            return view;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            Log.e("position", "" + position);
            return position;
        }
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


}