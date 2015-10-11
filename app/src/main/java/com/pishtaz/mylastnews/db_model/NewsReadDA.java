package com.pishtaz.mylastnews.db_model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "News")
public class NewsReadDA extends Model {

    /* @Column(name = "Id")
     public String Id;
 */
    @Column(name = "news_id")
    public String news_id;

    @Column(name = "title")
    public String title;

    @Column(name = "image_address")
    public String image_address;




    public NewsReadDA() {
        super();
    }


}
