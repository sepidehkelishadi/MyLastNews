package com.pishtaz.mylastnews.db_model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Arash on 2/22/2015.
 */
@Table(name = "owners")
public class OwnersDA extends Model {


    @Column(name = "owner")
    public String owner;

    @Column(name = "ownerID")
    public String ownerID;

    @Column(name = "category")
    public String category;


    public OwnersDA() {
        super();
    }


}
