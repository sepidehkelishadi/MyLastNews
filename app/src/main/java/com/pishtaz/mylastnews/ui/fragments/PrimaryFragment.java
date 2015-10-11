package com.pishtaz.mylastnews.ui.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.adapters.NewsListAdapter;
import com.pishtaz.mylastnews.adapters.NewsListAdapterHeader;
import com.pishtaz.mylastnews.models.ItemTO;
import com.pishtaz.mylastnews.models.WidgetItemTO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.pishtaz.mylastnews.ui.activities.DetailNews;
import com.pishtaz.mylastnews.ui.activities.PlayVideoNews;
import com.pishtaz.mylastnews.utils.MyGridView;


public class PrimaryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {




    public PrimaryFragment() {

    }
    // >>> -----------------------------------------------------------------

    private GridView gvHeaderNews1;
    private GridView gvHeaderNews2;
    //  private VideoView vvHeaderNews3;
    private MyGridView gvMoreNews;
    double diagonalInches;
    private ArrayList<ItemTO> itemTOHeaderNews1 = new ArrayList<>();
    private ArrayList<ItemTO> itemTOHeaderNews2 = new ArrayList<>();
    private ItemTO itemTOHeaderNews3 = new ItemTO();
    private ArrayList<ItemTO> itemTOMoreNews = new ArrayList<>();

    private static final String POST_Url = "http://mylastnews.ir/ws/homepage";
    private static final String POST_Url_Video = "http://newwebservice.mp4.ir/AndroidService.asmx/getLastNewsDirectPath";

    private NewsListAdapterHeader itemListAdapterHeaderNews1;
    private NewsListAdapter itemListAdapterHeaderNews2;
    private NewsListAdapter itemListAdapterMoreNews;

    private SwipeRefreshLayout swipeRefreshLayout;

    /*----- video elements -------*/
    ImageView ivPlayVideo;
    FrameLayout rlVideo;
    ImageView ivVideoThumbnail;
    TextView tvVideoTitle;

    String videoUrl;
    String videoThumbnail;
    String videoTitle;
    View rootView;

    /*----- widget elements -------*/
    public static ArrayList<ItemTO> allItemTOs = new ArrayList<>();
    public static ArrayList<WidgetItemTO> allWidgetItemTOs = new ArrayList<>();
    public static Bitmap lastNewsThumbnail;
    public static String lastNewsTitle;
    ImageView ivTemp;



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {


        if (rootView == null) {

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;
            float widthDpi = metrics.xdpi;
            float heightDpi = metrics.ydpi;
            float widthInches = widthPixels / widthDpi;
            float heightInches = heightPixels / heightDpi;

            diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));

            if (diagonalInches >= VolleyGeneric.INCHES) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                rootView = inflater.inflate(R.layout.fragment_primary_land, container, false);

            } else {

                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                rootView = inflater.inflate(R.layout.fragment_primary, container, false);

            }

            ivVideoThumbnail = (ImageView) rootView.findViewById(R.id.ivVideoThumbnail);
            tvVideoTitle = (TextView) rootView.findViewById(R.id.tvVideoTitle);

            gvHeaderNews1 = (GridView) rootView.findViewById(R.id.gvHeaderNews1);
            gvHeaderNews2 = (GridView) rootView.findViewById(R.id.gvHeaderNews2);
            //   vvHeaderNews3 = (VideoView) rootView.findViewById(R.id.vvHeaderNews3);

            gvMoreNews = (MyGridView) rootView.findViewById(R.id.gvMoreNews);

            itemListAdapterHeaderNews1 = new NewsListAdapterHeader(getActivity(), itemTOHeaderNews1);
            itemListAdapterHeaderNews2 = new NewsListAdapter(getActivity(), itemTOHeaderNews2);
            itemListAdapterMoreNews = new NewsListAdapter(getActivity(), itemTOMoreNews);

            gvHeaderNews1.setAdapter(itemListAdapterHeaderNews1);
            gvHeaderNews2.setAdapter(itemListAdapterHeaderNews2);
            gvMoreNews.setAdapter(itemListAdapterMoreNews);

            gvHeaderNews1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    startActivity(new Intent(getActivity(), DetailNews.class)
                            .putExtra("news_id", itemTOHeaderNews1.get(i).getNews_id()));
                           // .putExtra("news_id", itemTOHeaderNews1.get(i).getNews_id()));

                }
            });

            gvHeaderNews2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    startActivity(new Intent(getActivity(), DetailNews.class)
                            .putExtra("news_id", itemTOHeaderNews2.get(i).getNews_id()));


                }
            });

            gvMoreNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    startActivity(new Intent(getActivity(), DetailNews.class)
                            .putExtra("news_id", itemTOMoreNews.get(i).getNews_id()));
                }
            });

            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);

                    fetchNews();
                    fetchVideo();

                }
            });

            ivTemp = (ImageView) rootView.findViewById(R.id.ivTemp);

            rlVideo = (FrameLayout) rootView.findViewById(R.id.rlVideo);
            ivPlayVideo = (ImageView) rootView.findViewById(R.id.ivPlayVideo);
            rlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intentToPlayVideo = new Intent(getActivity(), PlayVideoNews.class);
                    intentToPlayVideo.putExtra("videoURL", videoUrl);
                    startActivity(intentToPlayVideo);
                }
            });
        } else {
            Log.e("wwwwwwwwwww","fffffffffff");
        }
        return rootView;
    }



    @Override
    public void onRefresh() {
        fetchNews();
        fetchVideo();
    }

    private void fetchVideo() {
        swipeRefreshLayout.setRefreshing(true);

        VolleyGeneric volleyGeneric = new VolleyGeneric(getActivity());
        volleyGeneric.getJson(POST_Url_Video, reviewResponseListenerGetVideo(), reviewErrorListenerGetVideo(), null);

    }

    private Response.Listener<String> reviewResponseListenerGetVideo() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.d("RESPONSE get NEWS", response + "");
                    JSONObject jsonObject = new JSONObject(response);

                    videoUrl = jsonObject.getString("VideoDirectPath");
                    videoThumbnail = jsonObject.getString("VideoImagePath");
                    videoTitle = jsonObject.getString("VideoTitle");

                    tvVideoTitle.setText(videoTitle);
                    setVideoThumbnail(videoThumbnail);

                } catch (Exception e) {
                    Log.d("error get NEWS", "try fetching url Failed");

                }

                swipeRefreshLayout.setRefreshing(false);
            }
        };
    }

    ;

    private Response.ErrorListener reviewErrorListenerGetVideo() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        };
    }


    private void fetchNews() {
        swipeRefreshLayout.setRefreshing(true);

        VolleyGeneric volleyGeneric = new VolleyGeneric(getActivity());
        volleyGeneric.getJsonGet(POST_Url, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), null);

    }

    private Response.Listener<String> reviewResponseListenerGetNews() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

                    Log.d("REFRESH get NEWS", response + "");

                    JSONArray jsonArray = new JSONArray(response);



                    itemTOHeaderNews1.clear();
                    for (int i = 0; i < 1; i++) {

                        JSONObject obj = jsonArray.getJSONObject(i);
                        ItemTO itemTO = new ItemTO();

                        itemTO.setTitle(obj.getString("title"));
                        itemTO.setCategory(obj.getString("category"));
                        itemTO.setCategory_color(obj.getString("category_color"));
                        itemTO.setPriority_id(obj.getString("priority_id"));
                        itemTO.setImage_address(obj.getString("image_address"));
                        itemTO.setNews_id(obj.getString("id"));
                        itemTOHeaderNews1.add(itemTO);
                        allItemTOs.add(itemTO);
                    }

                    itemTOHeaderNews2.clear();
                    if (diagonalInches >= VolleyGeneric.INCHES) {
                        for (int i = 1; i < 5; i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);
                            ItemTO itemTO = new ItemTO();

                            itemTO.setTitle(obj.getString("title"));
                            itemTO.setCategory(obj.getString("category"));
                            itemTO.setCategory_color(obj.getString("category_color"));
                            itemTO.setPriority_id(obj.getString("priority_id"));
                            itemTO.setImage_address(obj.getString("image_address"));
                            itemTO.setNews_id(obj.getString("id"));
                            itemTOHeaderNews2.add(itemTO);
                            allItemTOs.add(itemTO);
                        }

                    } else {

                        for (int i = 1; i < 3; i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);
                            ItemTO itemTO = new ItemTO();

                            itemTO.setTitle(obj.getString("title"));
                            itemTO.setCategory(obj.getString("category"));
                            itemTO.setCategory_color(obj.getString("category_color"));
                            itemTO.setPriority_id(obj.getString("priority_id"));
                            itemTO.setImage_address(obj.getString("image_address"));
                            itemTO.setNews_id(obj.getString("id"));
                            itemTOHeaderNews2.add(itemTO);
                            allItemTOs.add(itemTO);
                        }
                    }



                    itemTOMoreNews.clear();

                    if (diagonalInches >= VolleyGeneric.INCHES) {

                        for (int i = 5; i < 9; i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);
                            ItemTO itemTO = new ItemTO();

                            itemTO.setTitle(obj.getString("title"));
                            itemTO.setCategory(obj.getString("category"));
                            itemTO.setCategory_color(obj.getString("category_color"));
                            itemTO.setPriority_id(obj.getString("priority_id"));
                            itemTO.setImage_address(obj.getString("image_address"));
                            itemTO.setNews_id(obj.getString("id"));


                            itemTOMoreNews.add(itemTO);
                            allItemTOs.add(itemTO);
                        }
                    } else {
                        for (int i = 3; i < jsonArray.length(); i++) {

                            JSONObject obj = jsonArray.getJSONObject(i);
                            ItemTO itemTO = new ItemTO();

                            itemTO.setTitle(obj.getString("title"));
                            itemTO.setCategory(obj.getString("category"));
                            itemTO.setCategory_color(obj.getString("category_color"));
                            itemTO.setPriority_id(obj.getString("priority_id"));
                            itemTO.setImage_address(obj.getString("image_address"));
                            itemTO.setNews_id(obj.getString("id"));


                            itemTOMoreNews.add(itemTO);
                            allItemTOs.add(itemTO);
                        }
                    }


                    populateWidgetItemTOs();

                    rlVideo.setVisibility(View.VISIBLE);

                    itemListAdapterHeaderNews1.notifyDataSetChanged();
                    itemListAdapterHeaderNews2.notifyDataSetChanged();
                    itemListAdapterMoreNews.notifyDataSetChanged();


                } catch (Exception e) {
                    Log.d("REFRESH get NEWS", "try fetching url Failed");
//                    Toast.makeText(getActivity(), "try fetching url Failed", Toast.LENGTH_LONG).show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        };
    }

    ;

    private Response.ErrorListener reviewErrorListenerGetNews() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //   Toast.makeText(getActivity(), R.string.error_json_error_listener, Toast.LENGTH_LONG).show();

                swipeRefreshLayout.setRefreshing(false);
            }
        };
    }


    private void populateWidgetItemTOs() {
        for (int i = 0; i < allItemTOs.size(); i++) {
            final WidgetItemTO widgetItemTO = new WidgetItemTO();
            widgetItemTO.setNewsTitle(allItemTOs.get(i).getTitle());

            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
            imageLoader.loadImage(allItemTOs.get(i).getImage_address(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    ivTemp.setImageBitmap(loadedImage);
                    Bitmap b = ((BitmapDrawable) ivTemp.getDrawable()).getBitmap();
                    b = Bitmap.createScaledBitmap(b, 128, 128, false);
                    widgetItemTO.setNewsThumbnail(b);
//                    widgetItemTO.setNewsThumbnail(((BitmapDrawable) ivTemp.getDrawable()).getBitmap());
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            allWidgetItemTOs.add(widgetItemTO);
        }
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
}
