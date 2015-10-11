package com.pishtaz.mylastnews.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.ui.fragments.PrimaryFragment;

/**
 * Implementation of App Widget functionality.
 */
public class MyLastNewsWidget extends AppWidgetProvider {

    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < appWidgetIds.length; i++) {

            Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.my_last_news_widget);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);

            rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            // >>> -------------------------------------------
            int appWidgetId = appWidgetIds[i];

//            Intent toastIntent = new Intent(context, MyLastNewsWidget.class);
//            toastIntent.setAction(MyLastNewsWidget.TOAST_ACTION);
//            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);


            // >>> ----------------------------------------
//            Intent intentToMainActivity = new Intent(context, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToMainActivity, 0);
//
//            // Get the layout for the App Widget and attach an on-click listener
//            // to the button
////            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_last_news_widget);
//            rv.setOnClickPendingIntent(R.id.rlMain, pendingIntent);
//
//            appWidgetManager.updateAppWidget(appWidgetId, rv);
            // >>> ----------------------------------------

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
//            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);

            // >>> -------------------------------------------

            int currentWidgetId = appWidgetIds[i];
//            String url = "http://www.tutorialspoint.com";

//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            intent.setData(Uri.parse(url));
//            PendingIntent pending = PendingIntent.getActivity(context, 0,intent, 0);
//            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.activity_main);
//
//            views.setOnClickPendingIntent(R.id.button, pending);
//            appWidgetManager.updateAppWidget(currentWidgetId,views);
//            Toast.makeText(context, "widget added", Toast.LENGTH_SHORT).show();

//            // ---------------------------------------------- (*)
//            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
//            Intent intentToMainActivity = new Intent(context, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToMainActivity, 0);
//
//            // Get the layout for the App Widget and attach an on-click listener
//            // to the button
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_last_news_widget);
//            views.setOnClickPendingIntent(R.id.rlMain, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);



//            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
//            // ------------------------------------------------

        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_last_news_widget);
        views.setImageViewBitmap(R.id.ivLastNews, PrimaryFragment.lastNewsThumbnail);
        views.setTextViewText(R.id.tvLastNews, PrimaryFragment.lastNewsTitle);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

