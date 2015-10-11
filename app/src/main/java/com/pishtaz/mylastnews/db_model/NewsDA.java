package com.pishtaz.mylastnews.db_model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "News")
public class NewsDA extends Model {

    /* @Column(name = "Id")
     public String Id;
 */
    @Column(name = "news_id")
    public String news_id;

    @Column(name = "title")
    public String title;

    @Column(name = "image_address")
    public String image_address;

    @Column(name = "description")
    public String description;

    @Column(name = "category_id")
    public String category_id;

    @Column(name = "category")
    public String category;

    @Column(name = "category_color")
    public String category_color;

    @Column(name = "full_title")
    public String full_title;

    @Column(name = "owner")
    public String owner;

    @Column(name = "target_url")
    public String target_url;

    @Column(name = "news_date")
    public String news_date;


    public NewsDA() {
        super();
    }


}
