package com.pishtaz.mylastnews.db_model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "OtherApps")
public class OtherAppDA extends Model {

    @Column(name = "AppID")
    public String AppID;

    @Column(name = "ThumIcon")
    public String ThumIcon;

    @Column(name = "Title")
    public String Title;

    @Column(name = "apkFileName")
    public String apkFileName;

    @Column(name = "apkFile")
    public String apkFile;

    @Column(name = "apkSize")
    public String apkSize;

}
