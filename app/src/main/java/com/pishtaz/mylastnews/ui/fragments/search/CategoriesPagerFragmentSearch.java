package com.pishtaz.mylastnews.ui.fragments.search;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.Toast;

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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.db_model.NewsDA;
import com.pishtaz.mylastnews.models.ItemTO;
import com.pishtaz.mylastnews.models.News2TO;
import com.pishtaz.mylastnews.models.NewsTO;
import com.pishtaz.mylastnews.models.WidgetItemTO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.pishtaz.mylastnews.ui.activities.DetailNewsPager;
import com.pishtaz.mylastnews.utils.drive_image.DriveImageModel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CategoriesPagerFragmentSearch extends Fragment {


    private static final String POST_Url = "http://websrc.pishtaz.ir/android.asmx/Search";


    ImageView ivVideoThumbnail;
    TextView tvVideoTitle;
    String SearchKey;

    UltimateRecyclerView lvReview;
    ProgressBar progressbarList;
    LinearLayoutManager linearLayoutManager;
    SimpleAdapter simpleRecyclerViewAdapter;
    private HashMap<String, String> hashMap = new HashMap<>();
    static int retry;
    ArrayList<News2TO> newsTOAll = new ArrayList<>();
    ArrayList<NewsTO> newsTO = new ArrayList<>();
    VolleyGeneric volleyGeneric;
    TextView tvRefresh;
    CardView cvRefresh, cvNoResult;
    View rootView;
    private ItemTouchHelper mItemTouchHelper;
    double diagonalInches;
    // String cat_id;

    private static int page = 0;



    public CategoriesPagerFragmentSearch() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;
        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;

        diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));


        SearchKey = getArguments().getString("SearchKey");
        Log.e("ooooooooo", SearchKey + "");
        volleyGeneric = new VolleyGeneric(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {


        if (rootView == null) {
            if (diagonalInches >= VolleyGeneric.INCHES) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
            else{
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }

            rootView = inflater.inflate(R.layout.fragment_categories, container, false);



            lvReview = (UltimateRecyclerView) rootView.findViewById(R.id.list);
            progressbarList = (ProgressBar) rootView.findViewById(R.id.progressbarList);
            tvRefresh = (TextView) rootView.findViewById(R.id.tvRefresh);
            cvRefresh = (CardView) rootView.findViewById(R.id.cvRefresh);
            cvNoResult = (CardView) rootView.findViewById(R.id.cvNoResult);
            lvReview.setHasFixedSize(false);
            progressbarList.setVisibility(View.VISIBLE);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            lvReview.setLayoutManager(linearLayoutManager);
            simpleRecyclerViewAdapter = new SimpleAdapter(newsTOAll);

            if (diagonalInches >= VolleyGeneric.INCHES) {

                linearLayoutManager = new GridLayoutManager(getActivity(), 2);
            }
            else{

                linearLayoutManager = new GridLayoutManager(getActivity(), 1);
            }

            tvRefresh.setVisibility(View.GONE);
            cvRefresh.setVisibility(View.GONE);
            cvNoResult.setVisibility(View.GONE);
            //  StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(simpleRecyclerViewAdapter);
            //lvReview.addItemDecoration(headersDecor);

            lvReview.enableLoadmore();

            simpleRecyclerViewAdapter.setCustomLoadMoreView(LayoutInflater.from(getActivity()).inflate(R.layout.custom_bottom_progressbar, null));
            lvReview.setAdapter(simpleRecyclerViewAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(simpleRecyclerViewAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);

            lvReview.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
                @Override
                public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {

                            page++;
                            String pageStr = String.valueOf(page);
                            //int intPage = Integer.parseInt(page) + 1;
                            // hashMap.put("page", intPage  + "");
                            // Log.d("RRRRRRRRRRRRRRR", page + "");

                          /*  VolleyGeneric volleyGeneric = new VolleyGeneric(getActivity());
                            volleyGeneric.getJsonGet("http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);
*/

                            // Log.e("qqqqqqqqqqqq", pageStr + "");
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("key", VolleyGeneric.TOKEN);
                            hashMap.put("TypeId", "6");
                            hashMap.put("SearchKey", SearchKey);
                            hashMap.put("pagenumber", pageStr);

                            volleyGeneric.getJson(POST_Url, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);


                        }
                    }, 3000);
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
                            String page2 = hashMap.get("page");
                            int intPage2 = 0;
                            hashMap.put("page", intPage2 + "");

                            //page ++;
                            // int intPage = Integer.parseInt(page) + 1;
                            // hashMap.put("page", intPage + "");
                            // Log.d("RRRRRRRRRRRRRRR", page + "");

                        /*    VolleyGeneric volleyGeneric = new VolleyGeneric(getActivity());
                            volleyGeneric.getJsonGet("http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);
*/


                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("key", VolleyGeneric.TOKEN);
                            hashMap.put("TypeId", "6");
                            hashMap.put("SearchKey", SearchKey);
                            hashMap.put("pagenumber", pageStr);

                            volleyGeneric.getJson(POST_Url, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);

                            //volley.getJson(mLayoutURL, reviewResponseListener(), reviewErrorListener(), hashMap);
                            simpleRecyclerViewAdapter.notifyDataSetChanged();
                            lvReview.setRefreshing(false);
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
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("key", VolleyGeneric.TOKEN);
            hashMap.put("TypeId", "6");
            hashMap.put("SearchKey", SearchKey);
            hashMap.put("pagenumber", "1");

            volleyGeneric.getJson(POST_Url, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);


        /*    hashMap.put("cat_id", "55");
            hashMap.put("page", "0");

            //int page = hashMap.get("page");
            //i/nt intPage = Integer.parseInt(page) + 1;
            // hashMap.put("page", intPage  + "");
            hashMap.put("page", "2");
            Log.d("RRRRRRRRRRRRRRR", page + "");
            page = 1;
            String pageStr = String.valueOf(page);
          //  VolleyGeneric volleyGeneric = new VolleyGeneric(getActivity());

            Log.d("ERRRRRRR", hashMap.get("page") + "");
            Log.d("ERRRRRRR", hashMap.get("cat_id") + "");
            volleyGeneric.getJsonGet("http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + pageStr, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);

*/
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

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("key", VolleyGeneric.TOKEN);
        hashMap.put("TypeId", "6");
        hashMap.put("SearchKey", SearchKey);
        hashMap.put("pagenumber", pageStr);

        volleyGeneric.getJson(POST_Url, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);

        simpleRecyclerViewAdapter.notifyDataSetChanged();
        lvReview.setRefreshing(false);
        //   ultimateRecyclerView.scrollBy(0, -50);
        linearLayoutManager.scrollToPosition(0);
    }


    private Response.Listener<String> reviewResponseListenerGetNews() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (response.trim().length() == 0) {

                        cvNoResult.setVisibility(View.VISIBLE);
                        progressbarList.setVisibility(View.GONE);

                    } else {

                        cvNoResult.setVisibility(View.GONE);
                        progressbarList.setVisibility(View.GONE);
                        lvReview.setVisibility(View.VISIBLE);
                        Log.d("REFRESH get NEWS", response + "");

                        JSONArray jsonArray = new JSONArray(response);
                        Log.d("REFRESH get NEWS", "JSONArray is OK.");


                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);
                            News2TO itemTO = new News2TO();

                            itemTO.setTitle(obj.getString("title"));
                            itemTO.setNews_id(obj.getString("news_id"));
                            itemTO.setOwner(obj.getString("owner"));
                            itemTO.setNews_date(obj.getString("date"));
                            itemTO.setImage_address(obj.getString("image"));
                            itemTO.setId(obj.getString("id"));
                            itemTO.setCategory(obj.getString("category"));
                            itemTO.setCategory_color(obj.getString("color"));

                            newsTOAll.add(itemTO);
                        }


                        simpleRecyclerViewAdapter.notifyDataSetChanged();
                        progressbarList.setVisibility(View.GONE);

                        Log.d("REFRESH TAB CONTENT :", response + "");

                    }
                } catch (Exception e) {
                    Log.e("ERRPRRRRRRRRRRRR", e + "");

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


                tvRefresh.setVisibility(View.VISIBLE);
                cvRefresh.setVisibility(View.VISIBLE);
                progressbarList.setVisibility(View.GONE);
                lvReview.setVisibility(View.GONE);

                //    Toast.makeText(getActivity(), R.string.error_json_error_listener, Toast.LENGTH_SHORT).show();


            }
        };
    }


    private void setVideoThumbnail(String url) {
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if (loadedImage == null)
//                    Log.d("1111", "loadedImage is NULL");
//                else
//                    Log.d("1111", "loadedImage is OK");

                ivVideoThumbnail.setImageBitmap(loadedImage);
//                b = loadedImage;
//                lastNewsThumbnail = ((BitmapDrawable)ivTemp.getDrawable()).getBitmap();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
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
                    .build();

            options2 = new DisplayImageOptions.Builder()
                    // .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
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

                Log.d("RRRRRRRR", "RRRRRRr");

                holder.textViewTitle.setText(newsTO.get(position).getTitle());
                holder.textViewFromUser.setText(newsTO.get(position).getOwner());
                // holder.textViewDate.setText(newsTO.get(position).getNews_date());
                holder.textViewSourceId.setText("شناسه خبر: " + newsTO.get(position).getNews_id());
                holder.tvCategory.setText(newsTO.get(position).getCategory());
                try {
                    holder.cvCategory.setCardBackgroundColor(Color.parseColor(newsTO.get(position).getCategory_color().trim()));
                } catch (Exception e) {

                }

                imageLoader.displayImage(newsTO.get(position).getImage_address(), holder.imageViewVideoItem, options);


                try {
                    if (getAllNewsRead(newsTO.get(position).getId()).size() == 0) {
                        holder.linearAllNews.setBackgroundColor(Color.parseColor("#ffffff"));
                        holder.ivTick.setVisibility(View.GONE);

                    } else {
                        // final ColorMatrix matrix = new ColorMatrix();
                        /// matrix.setSaturation(0);

                        //final  ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                        //holder.imageViewVideoItem.setColorFilter(filter);

                        //   holder.ivTick.setVisibility(View.VISIBLE);
                        holder.linearAllNews.setBackgroundColor(Color.parseColor("#f6f6f6"));
                    }


                      /*  for ( int i = 0; i < newsDAs.size(); i++) {
                    Log.d("WWWWWWWW", newsTO.get(position).getId() + "----"+newsDAs.get(i).news_id);
                        //Log.d("WWWWWWWW", newsDAs.get(i).news_id + "  EEEEEEEE");
                        if (newsDAs.get(i).news_id.equals(newsTO.get(position).getId())) {


                        }


                    }*/

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


            View item_view;


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


                    //txImageTitle = (TextView) itemView.findViewById(R.id.txImageTitle);

                    //image = (ImageView) itemView.findViewById(R.id.image);
                    //item_view = itemView.findViewById(R.id.item_view);
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
                    Log.d("{{{{{{{{{{{", position + "");
                    Intent intent = new Intent(getActivity(), DetailNewsPagerSearch.class);

                    intent.putExtra("position", position + "");
                    intent.putExtra("news_id", newsTO.get(position).getId());
                    intent.putExtra("arraylist", newsTO);
                    intent.putExtra("SearchKey", SearchKey);
                    //  intent.putExtra("cat_id", cat_id);


                    NewsDA newsDA = new NewsDA();
                    newsDA.news_id = newsTO.get(position).getId();
                    // newsDA.Id = newsTO.get(position).getId();
                    newsDA.title = newsTO.get(position).getTitle();
                    newsDA.image_address = newsTO.get(position).getImage_address();
                    newsDA.save();


                    startActivity(intent);
                }catch (Exception e){

                }
            }
        }

        public String getItem(final int position) {
            // if (customHeaderView != null)
            //     position--;
            // if (position < newsTOAll.size())
            // return newsTOAll.get(position).getUserName();

            // else return "";

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






    public static List<NewsDA> getAllNewsRead(String id) {
        return new Select()
                .from(NewsDA.class)
                .where("news_id =?", id)
                .execute();
    }


    @Override
    public void onResume() {
        super.onResume();


        try {
            simpleRecyclerViewAdapter.notifyDataSetChanged();

        } catch (Exception ex) {
            Log.e("NOTIFY", ex + "");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
