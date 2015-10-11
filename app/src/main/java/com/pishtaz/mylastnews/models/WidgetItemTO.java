package com.pishtaz.mylastnews.models;

import android.graphics.Bitmap;

/**
 * Created by Jalal a on 8/12/2015.
 */
public class WidgetItemTO {
    public WidgetItemTO(){}

    public Bitmap getNewsThumbnail() {
        return newsThumbnail;
    }

    public void setNewsThumbnail(Bitmap newsThumbnail) {
        this.newsThumbnail = newsThumbnail;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    Bitmap newsThumbnail;
    String newsTitle;
}
