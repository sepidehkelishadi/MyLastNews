package com.pishtaz.mylastnews.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.pishtaz.mylastnews.utils.TextViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailNewsFragment extends Fragment {


    CoordinatorLayout rootLayout;
    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences settings;
    AppBarLayout appBarLayout;
    ImageLoadingListener animateFirstListener;
    DisplayImageOptions options;
    public ImageLoader imageLoader = ImageLoader.getInstance();
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    ImageView imageViewPlayVideo;
    double diagonalInches;
    ArrayList<NewsTO> newsTOArrayList;
    ImageView ivCover;
    LinearLayout llContent, llBack;
    NewsTO newsTO;
    TextViewEx tvDescription;
    //TextView tvDescription;
    TextView tvTitle,tvNoNet;
    TextView tvNewsDate;
    TextView tvCategory;
    TextView tvWebNews;
    TextView tvSourceId;
    CardView cvCategory;
    String news_id, id;
    Animation rotation;
    Typeface myTypeface;
    ImageView ivRefresh;
    // CardView cvRefresh;
    ViewPager viewpagerNews;
    CardView cvNews, cvWeb;
    NewsDA newsDA;


    public static DetailNewsFragment newInstance(String news_id, String id) {


        DetailNewsFragment fragment = new DetailNewsFragment();
        Bundle args = new Bundle();
        args.putString("news_id", news_id);
        args.putString("id", id);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        news_id = getArguments().getString("news_id");
        id = getArguments().getString("id");
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        settings=getActivity().getSharedPreferences(PREFS_NAME,0);
        myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + settings.getString("my_font", ""));
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;
        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;

        diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));
        View view;
        if (diagonalInches >= VolleyGeneric.INCHES) {
            view = inflater.inflate(R.layout.activity_detail_news_land, container, false);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            toolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
            llBack = (LinearLayout) view.findViewById(R.id.llBack);

        } else {
            view = inflater.inflate(R.layout.activity_detail_news, container, false);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            toolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar);
            rootLayout = (CoordinatorLayout) view.findViewById(R.id.rootLayout);
            collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbarLayout);

            rootLayout.setStatusBarBackgroundResource(R.color.primary);
            collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#00ffffff"));
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffff"));
            collapsingToolbarLayout.setStatusBarScrimColor(R.color.primary);

        }


        imageViewPlayVideo = (ImageView) view.findViewById(R.id.imageViewPlayVideo);
        llContent = (LinearLayout) view.findViewById(R.id.llContent);

        initToolbar();

        cvNews = (CardView) view.findViewById(R.id.cvNews);
        cvWeb = (CardView) view.findViewById(R.id.cvWeb);

        tvDescription = (TextViewEx) view.findViewById(R.id.tvDescription);
        //tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvNewsDate = (TextView) view.findViewById(R.id.tvNewsDate);
        tvCategory = (TextView) view.findViewById(R.id.tvCategory);
        tvWebNews = (TextView) view.findViewById(R.id.tvWebNews);
        tvSourceId = (TextView) view.findViewById(R.id.tvSourceId);
        tvNoNet = (TextView) view.findViewById(R.id.tvNoNet);
        cvCategory = (CardView) view.findViewById(R.id.cvCategory);
        ivRefresh = (ImageView) view.findViewById(R.id.ivRefresh);
        rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        ivRefresh.startAnimation(rotation);
        tvDescription.setTypeface(myTypeface);
        tvTitle.setTypeface(myTypeface,Typeface.BOLD);

        llContent.setVisibility(View.GONE);
        ivCover = (ImageView) view.findViewById(R.id.ivCover);

        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (settings.getString(VolleyGeneric.NIGHT, "").equals("true")) {

            llContent.setBackgroundColor(Color.GRAY);
            try {
                llBack.setBackgroundColor(Color.BLACK);
            } catch (Exception e) {

            }
            tvTitle.setTextColor(Color.WHITE);
            tvDescription.setTextColor(Color.WHITE);
            cvNews.setCardBackgroundColor(Color.BLACK);
            cvWeb.setCardBackgroundColor(Color.BLACK);
            tvNewsDate.setTextColor(Color.parseColor("#d3c200"));
            tvSourceId.setTextColor(Color.parseColor("#d3c200"));


        } else {
            llContent.setBackgroundColor(Color.WHITE);
            cvNews.setCardBackgroundColor(Color.WHITE);
            cvWeb.setCardBackgroundColor(Color.WHITE);
            try {
                llBack.setBackgroundColor(Color.WHITE);
            } catch (Exception e) {

            }
            tvTitle.setTextColor(Color.BLACK);
            tvDescription.setTextColor(Color.BLACK);
        }


        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
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

        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rotation.start();
                ivRefresh.setAnimation(rotation);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        VolleyGeneric volley = new VolleyGeneric(getActivity());
                        volley.getJsonGet("http://mylastnews.ir/ws/news_detail?id=" + news_id, reviewResponseListenerGetApp(), reviewErrorListenerGetApp(), null);

                    }
                }, 1500);


            }
        });

        if (isNetworkAvailable(getActivity())) {
            HashMap<String, String> apkInfo = new HashMap<String, String>();
            //apkInfo.put("AppID", "1135");
            ivRefresh.setVisibility(View.VISIBLE);

            VolleyGeneric volley = new VolleyGeneric(getActivity());
            volley.getJsonGet("http://mylastnews.ir/ws/news_detail?id=" + news_id, reviewResponseListenerGetApp(), reviewErrorListenerGetApp(), apkInfo);
        } else {

            newsDA = new NewsDA();
            try {
                newsDA = getNews(id).get(0);
                tvNoNet.setVisibility(View.GONE);
                Log.e("aaaaaaaa", newsDA.title);
                settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
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
        return view;


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            getFragmentManager().popBackStack();
            getActivity().finish();
        }


        return super.onOptionsItemSelected(item);
    }


    private Response.Listener<String> reviewResponseListenerGetApp() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    ivCover.clearAnimation();
                    //   pbRefresh.setVisibility(View.GONE);
                    ivRefresh.setVisibility(View.GONE);
                    //   cvRefresh.setVisibility(View.GONE);


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
                        //   newsTO.setId(obj.getString("id"));
                        newsTO.setCategory_id(obj.getString("category_id"));
                        newsTO.setCategory(obj.getString("category"));
                        newsTO.setCategory_color(obj.getString("category_color"));
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
                        ivCover.clearAnimation();
                        ivCover.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }

                    settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

                  // tvDescription.setTextSize(settings.getInt(VolleyGeneric.SIZE, 0));
                    tvDescription.setText((Html.fromHtml(newsTO.getDescription()) + ""), true);
                    //tvDescription.setText((Html.fromHtml(newsTO.getDescription()) + ""));
                    tvTitle.setText(newsTO.getTitle());
                    try {
                        collapsingToolbarLayout.setTitle("    " + newsTO.getOwner() + "   ");
                    } catch (Exception e) {

                    }
                    tvNewsDate.setText(newsTO.getNews_date());
                    tvCategory.setText(newsTO.getCategory());
                    tvSourceId.setText(getString(R.string.source_id) + (newsTO.getNews_id().trim() + ""));

                    tvWebNews.setText(R.string.see_news_web);
                    tvWebNews.setMovementMethod(LinkMovementMethod.getInstance());

                    cvCategory.setCardBackgroundColor(Color.parseColor(newsTO.getCategory_color().trim()));


                    tvWebNews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsTO.getTarget_url().trim()));
                            startActivity(intent);
                        }
                    });
                    // tvWebNews.setText(newsTO.gets());

                    if (!newsTO.getImage_address().equals("")) {
                        imageLoader.displayImage(newsTO.getImage_address().trim(), ivCover, options, animateFirstListener);
                    } else {
                        ivCover.setImageResource(android.R.color.transparent);
                        ivCover.setBackgroundResource(R.color.primary);
                    }

                    ivCover.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivity(new Intent(getActivity(), DetailImagesActivity.class)
                                    .putExtra("picsList", newsTO.getImage_address()));

                        }
                    });


                    Log.d("TAB CONTENT :", response + "");
                } catch (Exception ex) {
                    ivCover.clearAnimation();
                    ivCover.setVisibility(View.VISIBLE);


                    //  cvRefresh.setVisibility(View.VISIBLE);

                    Log.e("ERROR REGISTER :", ex + "");
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
                ivCover.clearAnimation();
                rotation.cancel();
                ivCover.setVisibility(View.VISIBLE);
                // cvRefresh.setVisibility(View.VISIBLE);


                Toast.makeText(getActivity(), R.string.error_json_error_listener, Toast.LENGTH_SHORT).show();

            }
        };
    }


    public void initToolbar() {

        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_right_white_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);

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
