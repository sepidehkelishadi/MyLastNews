package com.pishtaz.mylastnews.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pishtaz.mylastnews.MainActivity;

/**
 * Created by Jalal a on 10/10/2015.
 */
public class NewPackageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        if (MainActivity2.selectedAppID.equals(VolleyGeneric.APP_ID)){
//
//        } else {

            Intent intentMoveToApp = new Intent("appstore.pishtaz.com.appstore.ui.fragments.ApkInfoActivity");
            intentMoveToApp.putExtra("AppID", MainActivity.selectedAppID);
            intentMoveToApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMoveToApp);
//        }

    }
}
