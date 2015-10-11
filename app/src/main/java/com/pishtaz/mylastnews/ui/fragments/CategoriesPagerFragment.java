package com.pishtaz.mylastnews.ui.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.marshalchen.ultimaterecyclerview.animators.BaseItemAnimator;
import com.marshalchen.ultimaterecyclerview.animators.FadeInAnimator;
import com.marshalchen.ultimaterecyclerview.animators.FadeInDownAnimator;
import com.marshalchen.ultimaterecyclerview.animators.FadeInLeftAnimator;
import com.marshalchen.ultimaterecyclerview.animators.FadeInRightAnimator;
import com.marshalchen.ultimaterecyclerview.animators.FadeInUpAnimator;
import com.marshalchen.ultimaterecyclerview.animators.FlipInBottomXAnimator;
import com.marshalchen.ultimaterecyclerview.animators.FlipInLeftYAnimator;
import com.marshalchen.ultimaterecyclerview.animators.FlipInRightYAnimator;
import com.marshalchen.ultimaterecyclerview.animators.FlipInTopXAnimator;
import com.marshalchen.ultimaterecyclerview.animators.LandingAnimator;
import com.marshalchen.ultimaterecyclerview.animators.OvershootInLeftAnimator;
import com.marshalchen.ultimaterecyclerview.animators.OvershootInRightAnimator;
import com.marshalchen.ultimaterecyclerview.animators.ScaleInAnimator;
import com.marshalchen.ultimaterecyclerview.animators.ScaleInBottomAnimator;
import com.marshalchen.ultimaterecyclerview.animators.ScaleInLeftAnimator;
import com.marshalchen.ultimaterecyclerview.animators.ScaleInRightAnimator;
import com.marshalchen.ultimaterecyclerview.animators.ScaleInTopAnimator;
import com.marshalchen.ultimaterecyclerview.animators.SlideInDownAnimator;
import com.marshalchen.ultimaterecyclerview.animators.SlideInLeftAnimator;
import com.marshalchen.ultimaterecyclerview.animators.SlideInRightAnimator;
import com.marshalchen.ultimaterecyclerview.animators.SlideInUpAnimator;
import com.marshalchen.ultimaterecyclerview.itemTouchHelper.SimpleItemTouchHelperCallback;
import com.marshalchen.ultimaterecyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.db_model.NewsReadDA;
import com.pishtaz.mylastnews.db_model.OwnersDA;
import com.pishtaz.mylastnews.models.ItemTO;
import com.pishtaz.mylastnews.models.News2TO;
import com.pishtaz.mylastnews.models.NewsTO;
import com.pishtaz.mylastnews.models.OwnerTO;
import com.pishtaz.mylastnews.models.WidgetItemTO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.pishtaz.mylastnews.ui.activities.DetailNews;
import com.pishtaz.mylastnews.ui.activities.DetailNewsPager;
import com.pishtaz.mylastnews.ui.activities.PlayVideoNews;
import com.pishtaz.mylastnews.ui.fragments.menu.ChooseOwnerActivity;
import com.pishtaz.mylastnews.ui.fragments.menu.SettingActivity;
import com.pishtaz.mylastnews.utils.MyGridView;
import com.pishtaz.mylastnews.utils.TextViewEx;
import com.pishtaz.mylastnews.utils.Utils;
import com.pishtaz.mylastnews.utils.drive_image.DriveImageModel;
import com.pishtaz.mylastnews.utils.drive_image.DriveImageView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import android.support.design.widget.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CategoriesPagerFragment extends Fragment {

    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences settings;
    ImageView ivVideoThumbnail;
    //private BusWrapper busWrapper;
    //private NetworkEvents networkEvents;
    UltimateRecyclerView lvReview;
    ProgressBar progressbarList;
    GridLayoutManager linearLayoutManager;
    SimpleAdapter simpleRecyclerViewAdapter;
    private HashMap<String, String> hashMap = new HashMap<>();
    static int retry;
    ArrayList<News2TO> newsTOAll = new ArrayList<>();
    ArrayList<NewsTO> newsTO = new ArrayList<>();
    double diagonalInches;
    TextView tvRefresh, tvNoMatch;
    CardView cvRefresh;
    VolleyGeneric volley;
    View rootView;
    DriveImageModel m;
    private ItemTouchHelper mItemTouchHelper;
    String cat_id;
    // FloatingActionButton fabBtn;
    private int page = 0;
    OwnersDA ownersDAs = new OwnersDA();
    boolean isFirst=false;

    public static CategoriesPagerFragment newInstance(String cat_id) {


        CategoriesPagerFragment fragment = new CategoriesPagerFragment();
        Bundle args = new Bundle();
        args.putString("cat_id", cat_id);

        fragment.setArguments(args);
        return fragment;
    }

    public CategoriesPagerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cat_id = getArguments().getString("cat_id");

        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;
        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;

        diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));
        try {
            ownersDAs = getAllOwners().get(0);
        } catch (Exception e) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {


        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_categories, container, false);
            //    busWrapper = getOttoBusWrapper(new Bus());
         /*   networkEvents = new NetworkEvents(getActivity(), busWrapper)
                    .enableWifiScan();*/

            volley = new VolleyGeneric(getActivity());

            lvReview = (UltimateRecyclerView) rootView.findViewById(R.id.list);
            progressbarList = (ProgressBar) rootView.findViewById(R.id.progressbarList);
            tvRefresh = (TextView) rootView.findViewById(R.id.tvRefresh);
            tvNoMatch = (TextView) rootView.findViewById(R.id.tvNoMatch);
            cvRefresh = (CardView) rootView.findViewById(R.id.cvRefresh);

            lvReview.setHasFixedSize(false);
            progressbarList.setVisibility(View.VISIBLE);

            if (diagonalInches >= VolleyGeneric.INCHES) {
                linearLayoutManager = new GridLayoutManager(getActivity(), 2);
            } else {
                linearLayoutManager = new GridLayoutManager(getActivity(), 1);
            }

            lvReview.setLayoutManager(linearLayoutManager);
            simpleRecyclerViewAdapter = new SimpleAdapter(newsTOAll);
            tvRefresh.setVisibility(View.GONE);
            cvRefresh.setVisibility(View.GONE);
            tvNoMatch.setVisibility(View.GONE);
            lvReview.enableLoadmore();

            simpleRecyclerViewAdapter.setCustomLoadMoreView(LayoutInflater.from(getActivity()).inflate(R.layout.custom_bottom_progressbar, null));
            lvReview.setAdapter(simpleRecyclerViewAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(simpleRecyclerViewAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);


            lvReview.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
                @Override
                public void loadMore(int itemsCount, final int maxLastVisiblePosition) {

                  //  if (itemsCount>5) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                page++;
                                String pageStr = String.valueOf(page);
                                //int intPage = Integer.parseInt(page) + 1;
                                // hashMap.put("page", intPage  + "");
                                Log.e("44444444444444444444", "http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr + /*"&owner=" + ownersDAs.ownerID + */"&user=" + settings.getString("UserName", ""));

                                VolleyGeneric volleyGeneric = new VolleyGeneric(getActivity());
                                volleyGeneric.getJsonGet("http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr + /*"&owner=" + ownersDAs.ownerID+*/"&user=" + settings.getString("UserName", ""), reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);

                            }
                        }, 3000);
                   // }
                }
            });


            lvReview.setRefreshing(true);
            lvReview.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            newsTOAll.clear();
                            newsTO.clear();
                            retry = 0;
                            page = 1;
                            String pageStr = String.valueOf(page);
                            //  String page2 = hashMap.get("page");
                            int intPage2 = 0;
                            hashMap.put("page", intPage2 + "");


                            VolleyGeneric volleyGeneric = new VolleyGeneric(getActivity());
                            volleyGeneric.getJsonGet("http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr/* + "&owner=" + ownersDAs.ownerID*/ + "&user=" + settings.getString("UserName", ""), reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);
                            Log.e("5555555555555555", "http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr /*+ "&owner=" + ownersDAs.ownerID*/ + "&user=" + settings.getString("UserName", ""));

                            //volley.getJson(mLayoutURL, reviewResponseListener(), reviewErrorListener(), hashMap);
                            simpleRecyclerViewAdapter.notifyDataSetChanged();
                            lvReview.setRefreshing(false);
                            //   ultimateRecyclerView.scrollBy(0, -50);
                            linearLayoutManager.scrollToPosition(0);
//                        ultimateRecyclerView.setAdapter(simpleRecyclerViewAdapter);
//                        simpleRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }, 3000);
                }
            });

            lvReview.setItemAnimator(Type.values()[1].getAnimator());
            lvReview.getItemAnimator().setAddDuration(600);
            lvReview.getItemAnimator().setRemoveDuration(600);

            hashMap.put("cat_id", "55");
            hashMap.put("page", "0");

            //int page = hashMap.get("page");
            //i/nt intPage = Integer.parseInt(page) + 1;
            // hashMap.put("page", intPage  + "");
            hashMap.put("page", "2");
            Log.d("1111111111", page + "");
            page = 1;
            String pageStr = String.valueOf(page);
            VolleyGeneric volleyGeneric = new VolleyGeneric(getActivity());

            Log.d("ERRRRRRR", hashMap.get("page") + "");
            Log.d("ERRRRRRR", hashMap.get("cat_id") + "");
            volleyGeneric.getJsonGet("http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr /*+ "&owner=" + ownersDAs.ownerID*/ + "&user=" + settings.getString("UserName", ""), reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);
            Log.e("22222222222222", "http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr /*+ "&owner=" + ownersDAs.ownerID*/ + "&user=" + settings.getString("UserName", ""));

            tvRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refreshList();

                }
            });

        } else {

        }
        return rootView;
    }


    public void refreshList() {
        progressbarList.setVisibility(View.VISIBLE);
        tvRefresh.setVisibility(View.GONE);
        cvRefresh.setVisibility(View.GONE);
        newsTOAll.clear();
        newsTO.clear();
        retry = 0;
        page = 1;
        String pageStr = String.valueOf(page);
        String page2 = hashMap.get("page");
        int intPage2 = 0;
        hashMap.put("page", intPage2 + "");

        VolleyGeneric volleyGeneric = new VolleyGeneric(getActivity());
        volleyGeneric.getJsonGet("http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr /*+ "&owner=" + ownersDAs.ownerID*/ + "&user=" + settings.getString("UserName", ""), reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);
        Log.e("33333333333333", "http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr /*+ "&owner=" + ownersDAs.ownerID*/ + "&user=" + settings.getString("UserName", ""));
        //volley.getJson(mLayoutURL, reviewResponseListener(), reviewErrorListener(), hashMap);
        simpleRecyclerViewAdapter.notifyDataSetChanged();
        lvReview.setRefreshing(false);
        //   ultimateRecyclerView.scrollBy(0, -50);
        linearLayoutManager.scrollToPosition(0);
    }


    private Response.Listener<String> reviewResponseListenerGetNews() {

        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

              //  lvReview.disableLoadmore();

                JSONArray jsonArray;
                try {
                    progressbarList.setVisibility(View.GONE);
                    lvReview.setVisibility(View.VISIBLE);
                    //  Log.e("wwwwwwwwwww", response);
                    jsonArray = new JSONArray(response);

                    if (jsonArray.length() == 0) {


                        tvNoMatch.setVisibility(View.VISIBLE);

                    } else {
                        tvNoMatch.setVisibility(View.GONE);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);


                            News2TO itemTO = new News2TO();
                            itemTO.setTitle(obj.getString("title"));
                            itemTO.setNews_id(obj.getString("news_id"));
                            itemTO.setOwner(obj.getString("owner"));
                            itemTO.setNews_date(obj.getString("news_date"));
                            itemTO.setImage_address(obj.getString("image"));
                            itemTO.setId(obj.getString("id"));
                            itemTO.setCategory(obj.getString("category"));
                            itemTO.setCategory_color(obj.getString("category_color"));

                            newsTOAll.add(itemTO);


                        }


                        simpleRecyclerViewAdapter.notifyDataSetChanged();
                        progressbarList.setVisibility(View.GONE);

                        Log.d("REFRESH TAB CONTENT :", response + "");

                       // lvReview.enableLoadmore();

                    }
                } catch (Exception e) {
                    Log.e("vvvvvvv", e + "");

                    tvRefresh.setVisibility(View.VISIBLE);
                    cvRefresh.setVisibility(View.VISIBLE);
                    progressbarList.setVisibility(View.GONE);
                    lvReview.setVisibility(View.GONE);


                }


            }
        };
    }

    ;

    private Response.ErrorListener reviewErrorListenerGetNews() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("llllllllll", error + "");
                lvReview.disableLoadmore();
                simpleRecyclerViewAdapter.notifyDataSetChanged();

            }
        };
    }


    public class SimpleAdapter extends UltimateViewAdapter<SimpleAdapter.SimpleAdapterViewHolder> {


        public final DisplayImageOptions options;
        public final DisplayImageOptions options2;
        public final ImageLoader imageLoader = ImageLoader.getInstance();
        public final ImageLoader imageLoader2 = ImageLoader.getInstance();
        ArrayList<News2TO> newsTO;

        public SimpleAdapter(ArrayList<News2TO> newsTO) {
            this.newsTO = newsTO;

            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.ALPHA_8)
                    .showImageOnLoading(android.R.color.transparent)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .considerExifParams(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .build();

            options2 = new DisplayImageOptions.Builder()
                    // .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .displayer(new SimpleBitmapDisplayer())
                    .considerExifParams(true)
                    .cacheOnDisk(true)
                    .build();


            if (!imageLoader.isInited()) {
                imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));

            }
            if (!imageLoader2.isInited()) {

                imageLoader2.init(ImageLoaderConfiguration.createDefault(getActivity()));
            }

        }


        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(final ViewGroup viewGroup) {
            return null;
        }

        @Override
        public void onBindHeaderViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {

        }

        @Override
        public int getItemCount() {
            return super.getItemCount();

        }

        @Override
        public void onBindViewHolder(final SimpleAdapterViewHolder holder, final int position) {

            if (position < getItemCount() && (customHeaderView != null ? position <= newsTOAll.size() : position < newsTOAll.size()) && (customHeaderView != null ? position > 0 : true)) {

                settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                if (settings.getString(VolleyGeneric.NIGHT, "").equals("true")) {

                    holder.linearAllNews.setBackgroundColor(Color.GRAY);
                    holder.textViewTitle.setTextColor(Color.WHITE);
                    holder.textViewDate.setTextColor(Color.parseColor("#d3c200"));

                } else {
                    holder.linearAllNews.setBackgroundColor(Color.WHITE);
                    holder.textViewTitle.setTextColor(Color.BLACK);
                    holder.textViewDate.setTextColor(Color.parseColor("#a3a3a3"));
                }

                holder.textViewTitle.setText(newsTO.get(position).getTitle());
                // Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/IRAN Sans Light.ttf");
                //   holder.textViewTitle.setTypeface(myTypeface);
                holder.textViewFromUser.setText(newsTO.get(position).getOwner());
                holder.textViewDate.setText(newsTO.get(position).getNews_date());
                holder.textViewSourceId.setText("شناسه خبر: " + newsTO.get(position).getNews_id());

                // holder.textViewFromUser.setTypeface(myTypeface);
                //// holder.textViewDate.setTypeface(myTypeface);
                //  holder.textViewSourceId.setTypeface(myTypeface);

                if (cat_id.equals("1")) {
                    holder.tvCategory.setText(newsTO.get(position).getCategory().trim());
                    holder.cvCategory.setCardBackgroundColor(Color.parseColor(newsTO.get(position).getCategory_color().trim()));
                } else {
                    holder.cvCategory.setVisibility(View.GONE);
                }

                imageLoader.displayImage(newsTO.get(position).getImage_address(), holder.imageViewVideoItem, options);

                try {
                    if (getAllNewsRead(newsTO.get(position).getId()).size() == 0) {
                        if (settings.getString(VolleyGeneric.NIGHT, "").equals("true")) {
                            holder.linearAllNews.setBackgroundColor(Color.BLACK);
                        } else {
                            holder.linearAllNews.setBackgroundColor(Color.WHITE);
                        }

                    } else {
                        if (settings.getString(VolleyGeneric.NIGHT, "").equals("true")) {
                            holder.linearAllNews.setBackgroundColor(Color.parseColor("#FF404040"));
                        } else {
                            holder.linearAllNews.setBackgroundColor(Color.parseColor("#eaeaea"));
                        }

                    }


                } catch (Exception ex)

                {
                    Log.e("DATABASE", ex + "");
                }


            }
        }

        @Override
        public int getAdapterItemCount() {
            return newsTOAll.size();
        }

        @Override
        public SimpleAdapterViewHolder getViewHolder(final View view) {
            return new SimpleAdapterViewHolder(view, false);
        }

        @Override
        public SimpleAdapterViewHolder onCreateViewHolder(final ViewGroup parent) {

            View v;
            if (diagonalInches >= VolleyGeneric.INCHES) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_land, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            }

            SimpleAdapterViewHolder vh = new SimpleAdapterViewHolder(v, true);
            return vh;
        }

        @Override
        public void toggleSelection(final int pos) {
            super.toggleSelection(pos);
        }

        @Override
        public void setSelected(final int pos) {
            super.setSelected(pos);
        }

        @Override
        public long generateHeaderId(final int position) {

            return position;
        }

        @Override
        public void clearSelection(final int pos) {
            super.clearSelection(pos);
        }


        public class SimpleAdapterViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {

            public ImageView image;
            public ImageView imageViewVideoItem;
            public ImageView ivTick;
            public ImageView ivUser;
            public TextView tvUserName;
            public TextView textViewTitle;
            public TextView textViewFromUser;
            public TextView textViewDate;
            public TextView textViewSourceId;
            public TextView tvCategory;
            public TextView txImageTitle;
            public CardView cvCategory;
            public LinearLayout linearAllNews;


            public SimpleAdapterViewHolder(final View itemView, final boolean isItem) {
                super(itemView);

                if (isItem) {

                    imageViewVideoItem = (ImageView) itemView.findViewById(R.id.imageViewVideoItem);
                    ivTick = (ImageView) itemView.findViewById(R.id.ivTick);
                    textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                    textViewFromUser = (TextView) itemView.findViewById(R.id.textViewFromUser);
                    textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
                    textViewSourceId = (TextView) itemView.findViewById(R.id.textViewSourceId);
                    tvCategory = (TextView) itemView.findViewById(R.id.tvCategory);
                    cvCategory = (CardView) itemView.findViewById(R.id.cvCategory);
                    linearAllNews = (LinearLayout) itemView.findViewById(R.id.linearAllNews);
                    linearAllNews = (LinearLayout) itemView.findViewById(R.id.linearAllNews);


                }
                itemView.setOnClickListener(this);
            }

            @Override
            public void onItemSelected() {
                itemView.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
            }

            @Override
            public void onClick(final View view) {
                try {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(getActivity(), DetailNewsPager.class);

                    intent.putExtra("position", position + "");
                    intent.putExtra("news_id", newsTO.get(position).getId());
                    intent.putExtra("id", newsTO.get(position).getNews_id());
                    intent.putExtra("arraylist", newsTO);
                    intent.putExtra("cat_id", cat_id);


                    NewsReadDA newsDA = new NewsReadDA();
                    newsDA.news_id = newsTO.get(position).getId();
                    newsDA.title = newsTO.get(position).getTitle();
                    newsDA.image_address = newsTO.get(position).getImage_address();
                    newsDA.save();
                    startActivity(intent);

                } catch (Exception e) {

                }
            }
        }

        public String getItem(final int position) {
            return null;
        }

    }

    enum Type {
        FadeIn(new FadeInAnimator()),
        FadeInDown(new FadeInDownAnimator()),
        FadeInUp(new FadeInUpAnimator()),
        FadeInLeft(new FadeInLeftAnimator()),
        FadeInRight(new FadeInRightAnimator()),
        Landing(new LandingAnimator()),
        ScaleIn(new ScaleInAnimator()),
        ScaleInTop(new ScaleInTopAnimator()),
        ScaleInBottom(new ScaleInBottomAnimator()),
        ScaleInLeft(new ScaleInLeftAnimator()),
        ScaleInRight(new ScaleInRightAnimator()),
        FlipInTopX(new FlipInTopXAnimator()),
        FlipInBottomX(new FlipInBottomXAnimator()),
        FlipInLeftY(new FlipInLeftYAnimator()),
        FlipInRightY(new FlipInRightYAnimator()),
        SlideInLeft(new SlideInLeftAnimator()),
        SlideInRight(new SlideInRightAnimator()),
        SlideInDown(new SlideInDownAnimator()),
        SlideInUp(new SlideInUpAnimator()),
        OvershootInRight(new OvershootInRightAnimator()),
        OvershootInLeft(new OvershootInLeftAnimator());

        private BaseItemAnimator mAnimator;

        Type(BaseItemAnimator animator) {
            mAnimator = animator;
        }

        public BaseItemAnimator getAnimator() {
            return mAnimator;
        }
    }


    public static List<NewsReadDA> getAllNewsRead(String id) {
        return new Select()
                .from(NewsReadDA.class)
                .where("news_id =?", id)
                .execute();
    }


    @Override
    public void onResume() {
        super.onResume();

        ownersDAs = getAllOwners().get(0);

        if (SettingActivity.isEdit) {


        } else {

            try {
                simpleRecyclerViewAdapter.notifyDataSetChanged();

            } catch (Exception ex) {
                Log.e("NOTIFY", ex + "");
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public static List<OwnersDA> getAllOwners() {
        return new Select()
                .from(OwnersDA.class)
                .execute();
    }

}
