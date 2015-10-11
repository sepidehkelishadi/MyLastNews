package com.pishtaz.mylastnews.update;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pishtaz.mylastnews.MainActivity;
import com.pishtaz.mylastnews.R;

import java.io.File;

public class UpdateActivity extends Activity {

   TextView tvYesUpdate,tvNoUpdate;
    private DownloadManager downloadManager;
    String appURI = "";
    private long downloadReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        int screenHeight = (int) (metrics.heightPixels * 0.30);
        setContentView(R.layout.activity_update);
        getWindow().setLayout(screenWidth, screenHeight);

        tvNoUpdate= (TextView) findViewById(R.id.tvNoUpdate);
        tvYesUpdate= (TextView) findViewById(R.id.tvYesUpdate);

        tvYesUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Uri Download_Uri = Uri.parse(MainActivity.appURI);
                DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                request.setAllowedOverRoaming(false);
                request.setTitle("دانلود اپلیکیشن NEWS");
                //request.setDestinationInExternalFilesDir(MainActivity.this, "/sdcard/", "arashhhhhh.apk");
                File file = new File("/sdcard/" + Environment.DIRECTORY_DOWNLOADS + "/MyLastNews.apk");
                if (file.exists()) {
                    file.delete();
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MyLastNews.apk");
                downloadReference = downloadManager.enqueue(request);
                finish();
            }
        });

        tvNoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {

                Log.v("pppppp", "Downloading of the new app version complete");
                //start the installation of the latest version
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadReference),
                        "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installIntent);

            }
        }
    };


}
