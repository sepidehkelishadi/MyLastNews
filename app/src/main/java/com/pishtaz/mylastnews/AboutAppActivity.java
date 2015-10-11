package com.pishtaz.mylastnews;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.pishtaz.mylastnews.utils.SystemBarTintManager;

import java.io.IOException;
import java.io.InputStream;


import uk.co.senab.photoview.PhotoView;

import static java.lang.String.format;

public class AboutAppActivity extends AppCompatActivity /*implements OnPageChangeListener */{

//    PDFView pdfView;
//    ImageViewTouch ivAboutApp;
//    ImageView ivAboutApp;
    PhotoView ivAboutApp;
    ImageView ivBack;
//    TextView tvPageNumber;
//    LinearLayout llPageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.primary);


//        pdfView = (PDFView) findViewById(R.id.pdfview);
//        ivAboutApp = (ImageViewTouch) findViewById(R.id.ivAboutApp);
//        ivAboutApp = (ImageView) findViewById(R.id.ivAboutApp);
        ivAboutApp = (PhotoView) findViewById(R.id.ivAboutApp);
        ivBack = (ImageView) findViewById(R.id.ivBack);

        try{

            InputStream ims = getAssets().open("news.jpg");
            Drawable d = Drawable.createFromStream(ims, null);

            ivAboutApp.setImageDrawable(d);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

//        tvPageNumber = (TextView) findViewById(R.id.tvPageNumber);
//        llPageNumber = (LinearLayout) findViewById(R.id.llPageNumber);

//        llPageNumber.setVisibility(View.GONE);

//        llPageNumber.setVisibility(View.VISIBLE);

//        pdfView.fromAsset("mysd.pdf")
//                .enableSwipe(false)
//                .swipeVertical(false)
//                .defaultPage(1)
//                .onPageChange(this)
//                .load();

    }


//    @Override
//    public void onPageChanged(int page, int pageCount) {
//
//        tvPageNumber.setText(format(" %s / %s ", page, pageCount));
//
//    }
}
