package com.pishtaz.mylastnews.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.db_model.NewsDA;
import com.pishtaz.mylastnews.models.NewsTO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.pishtaz.mylastnews.utils.SystemBarTintManager;
import com.pishtaz.mylastnews.utils.TextViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DetailNews extends AppCompatActivity {


    CoordinatorLayout rootLayout;


    ImageLoadingListener animateFirstListener;
    DisplayImageOptions options;
    public ImageLoader imageLoader = ImageLoader.getInstance();
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    ImageView imageViewPlayVideo;

    ArrayList<NewsTO> newsTOArrayList;
    ImageView ivCover;
    LinearLayout llContent,llBack;
    NewsTO newsTO;
    TextViewEx tvDescription;
    //TextView tvDescription;
    TextView tvTitle,tvNoNet;
    TextView tvNewsDate;
    TextView tvCategory;
    TextView tvWebNews;
    TextView tvSourceId;
    CardView cvCategory;
    CardView cvNews, cvWeb;
    String news_id,id;
    double diagonalInches;
    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences settings;
    Typeface myTypeface;
    NewsDA newsDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;
        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;

        diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));

        if (diagonalInches >= VolleyGeneric.INCHES) {
            setContentView(R.layout.activity_detail_news_land);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
            llBack = (LinearLayout) findViewById(R.id.llBack);

        } else {

            setContentView(R.layout.activity_detail_news);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
            rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);

            rootLayout.setStatusBarBackgroundResource(R.color.primary);
            collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#00ffffff"));
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffff"));
            collapsingToolbarLayout.setStatusBarScrimColor(R.color.primary);


        }


        initToolbar();
        news_id = getIntent().getStringExtra("news_id");
        id = getIntent().getStringExtra("id");

        imageViewPlayVideo = (ImageView) findViewById(R.id.imageViewPlayVideo);
        llContent = (LinearLayout) findViewById(R.id.llContent);

        tvDescription = (TextViewEx) findViewById(R.id.tvDescription);
       // tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvNewsDate = (TextView) findViewById(R.id.tvNewsDate);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvNoNet = (TextView) findViewById(R.id.tvNoNet);
        tvWebNews = (TextView) findViewById(R.id.tvWebNews);
        tvSourceId = (TextView) findViewById(R.id.tvSourceId);
        cvCategory = (CardView) findViewById(R.id.cvCategory);
        cvNews = (CardView) findViewById(R.id.cvNews);
        cvWeb = (CardView) findViewById(R.id.cvWeb);
        llContent.setVisibility(View.GONE);
       //llBack.setVisibility(View.GONE);
        ivCover = (ImageView) findViewById(R.id.ivCover);
        settings=getSharedPreferences(PREFS_NAME,0);
        myTypeface = Typeface.createFromAsset(getAssets(), "fonts/" + settings.getString("my_font", ""));
        tvDescription.setTypeface(myTypeface);
        tvTitle.setTypeface(myTypeface);

        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        }
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageOnLoading(android.R.drawable.screen_background_light_transparent)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ALPHA_8)
                .considerExifParams(true)
                .build();


        imageViewPlayVideo.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View arg0) {


                                                      // String path = apkTO.getVideoURL();
                       /* startActivity(new Intent(getApplicationContext(), PlayVideoNews.class)
                                .putExtra("PATH", path));*/


                                                  }
                                              }

        );

        if (isNetworkAvailable(getApplicationContext())) {
            HashMap<String, String> apkInfo = new HashMap<String, String>();
            //apkInfo.put("AppID", "1135");

            VolleyGeneric volley = new VolleyGeneric(this);
            volley.getJsonGet("http://mylastnews.ir/ws/news_detail?id=" + news_id, reviewResponseListenerGetApp(), reviewErrorListenerGetApp(), apkInfo);

        } else {

            newsDA = new NewsDA();
            try {
                newsDA = getNews(id).get(0);
                tvNoNet.setVisibility(View.GONE);
                Log.e("aaaaaaaa", newsDA.title);
                settings = getSharedPreferences(PREFS_NAME, 0);
                llContent.setVisibility(View.VISIBLE);

                tvDescription.setText((Html.fromHtml(newsDA.description) + ""));
                tvTitle.setText(newsDA.title);
                try {
                    collapsingToolbarLayout.setTitle("    " + newsDA.owner + "   ");
                } catch (Exception e) {

                }
                tvNewsDate.setText(newsDA.news_date);
                tvCategory.setText(newsDA.category);
                tvSourceId.setText(getString(R.string.source_id) + (newsDA.news_id.trim() + ""));

                tvWebNews.setText(R.string.see_news_web);
                tvWebNews.setMovementMethod(LinkMovementMethod.getInstance());

                cvCategory.setCardBackgroundColor(Color.parseColor(newsDA.category_color.trim()));

                if (!newsDA.image_address.equals("")) {
                    imageLoader.displayImage(newsDA.image_address.trim(), ivCover, options, animateFirstListener);
                } else {
                    ivCover.setImageResource(android.R.color.transparent);
                    ivCover.setBackgroundResource(R.color.primary);
                }

            } catch (Exception e) {
                tvNoNet.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


    private Response.Listener<String> reviewResponseListenerGetApp() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("RESPONSE NEWS", response + "");
                    llContent.setVisibility(View.VISIBLE);
                    //   pbContent.cancel();
                    newsTOArrayList = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    try {
                        JSONObject obj = jsonArray.getJSONObject(0);

                        try {
                            Log.e("aaaaaaabbb", obj.getString("news_id"));
                            new Delete().from(NewsDA.class).where("news_id = ?", obj.getString("news_id")).execute();
                        } catch (Exception e) {

                        }
                        NewsDA newsDA = new NewsDA();
                        newsDA.news_id = obj.getString("news_id");
                        newsDA.title = obj.getString("full_title");
                        newsDA.image_address = obj.getString("image_address");
                        newsDA.description = obj.getString("description");
                        newsDA.category_id = obj.getString("category_id");
                        newsDA.category = obj.getString("category");
                        newsDA.category_color = obj.getString("category_color");
                        newsDA.full_title = obj.getString("full_title");
                        newsDA.owner = obj.getString("owner");
                        newsDA.target_url = obj.getString("target_url");
                        newsDA.news_date = obj.getString("news_date");
                        newsDA.save();
                        newsTO = new NewsTO();

                        newsTO.setNews_id(obj.getString("news_id"));
                        newsTO.setCategory_id(obj.getString("category_id"));
                        newsTO.setCategory(obj.getString("category"));
                        newsTO.setCategory_color(obj.getString("category_color").trim());
                        newsTO.setSub_category_id(obj.getString("sub_category_id"));
                        newsTO.setSub_category(obj.getString("sub_category"));
                        newsTO.setTitle(obj.getString("title"));
                        newsTO.setFull_title(obj.getString("full_title"));
                        newsTO.setDescription(obj.getString("description"));
                        newsTO.setImage_address(obj.getString("image_address"));
                        newsTO.setView_count(obj.getString("view_count"));
                        newsTO.setRate(obj.getString("rate"));
                        newsTO.setTarget_url(obj.getString("target_url"));
                        newsTO.setNews_date(obj.getString("news_date"));
                        newsTO.setCreated_at(obj.getString("created_at"));
                        newsTO.setUpdated_at(obj.getString("updated_at"));
                        newsTO.setOwner(obj.getString("owner"));

                        newsTOArrayList.add(newsTO);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    settings = getSharedPreferences(PREFS_NAME, 0);
                    if (settings.getString(VolleyGeneric.NIGHT, "").equals("true")) {

                        llContent.setBackgroundColor(Color.GRAY);
                        try {
                            llBack.setBackgroundColor(Color.BLACK);
                        }catch (Exception e){

                        }
                        tvTitle.setTextColor(Color.WHITE);
                        tvDescription.setTextColor(Color.WHITE);
                        cvNews.setCardBackgroundColor(Color.BLACK);
                        cvWeb.setCardBackgroundColor(Color.BLACK);
                        tvNewsDate.setTextColor(Color.parseColor("#d3c200"));
                        tvSourceId.setTextColor(Color.parseColor("#d3c200"));


                    } else {
                        try{
                        llBack.setBackgroundColor(Color.WHITE);
                        }catch (Exception e){

                        }
                        llContent.setBackgroundColor(Color.WHITE);
                        cvNews.setCardBackgroundColor(Color.WHITE);
                        cvWeb.setCardBackgroundColor(Color.WHITE);
                        tvTitle.setTextColor(Color.BLACK);
                        tvDescription.setTextColor(Color.BLACK);
                    }


                  //  tvDescription.setTextSize(settings.getInt(VolleyGeneric.SIZE, 0));

                    tvDescription.setText((Html.fromHtml(newsTO.getDescription())+""),true);
                  //  tvDescription.setText((Html.fromHtml(newsTO.getDescription())+""));
                    tvTitle.setText(newsTO.getTitle());
                    try {
                        collapsingToolbarLayout.setTitle("    " + newsTO.getOwner() + "   ");
                    } catch (Exception e) {

                    }
                    tvNewsDate.setText(newsTO.getNews_date());
                    tvCategory.setText(newsTO.getCategory());
                    tvSourceId.setText(getString(R.string.source_id) + (newsTO.getNews_id() + ""));

                    tvWebNews.setText(R.string.see_news_web);
                    tvWebNews.setMovementMethod(LinkMovementMethod.getInstance());

                    cvCategory.setCardBackgroundColor(Color.parseColor(newsTO.getCategory_color()));


                    tvWebNews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsTO.getTarget_url()));
                            startActivity(intent);
                        }
                    });
                    // tvWebNews.setText(newsTO.gets());

                    if (!newsTO.getImage_address().equals("")) {
                        imageLoader.displayImage(newsTO.getImage_address(), ivCover, options, animateFirstListener);
                    } else {
                        ivCover.setImageResource(android.R.color.transparent);
                        ivCover.setBackgroundResource(R.color.primary);
                    }

                    ivCover.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivity(new Intent(getApplicationContext(), DetailImagesActivity.class)
                                    .putExtra("picsList", newsTO.getImage_address()));

                        }
                    });


                    Log.d("TAB CONTENT :", response + "");
                } catch (Exception ex) {
                    Log.e("ERROR DETAIL NEWS :", ex + "");
                    // >>> TODO: needs to be Omitted in case no Accesses exist.
                    //    Toast.makeText(getActivity(), R.string.error_json_reponse, Toast.LENGTH_LONG).show();
                }


            }

        };
    }

    private Response.ErrorListener reviewErrorListenerGetApp() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //  Toast.makeText(getActivity(), R.string.error_json_error_listener, Toast.LENGTH_LONG).show();

            }
        };
    }


    public void initToolbar() {

        toolbar.setTitle("");
        // toolbar.setLogo(R.drawable.my_last_news_widget);
        // toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        // toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }*/
        // >>> toolbar:: set drawer-icon to Toolbar
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_right_white_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        toolbar.getContentInsetEnd();


    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    // Log.v(LOG_TAG,String.valueOf(i));
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        //  Log.v(LOG_TAG, "connected!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<NewsDA> getNews(String news_id) {
        return new Select()
                .from(NewsDA.class)
                .where("news_id =?", news_id)
                .execute();
    }
}
