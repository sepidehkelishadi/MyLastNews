package com.pishtaz.mylastnews.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import com.pishtaz.mylastnews.R;

import com.pishtaz.mylastnews.models.Pictures;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.pishtaz.mylastnews.utils.HackyViewPager;


import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class DetailImagesActivity extends AppCompatActivity {


   // private String[] mThumbIds;
    private static final String STATE_POSITION = "STATE_POSITION";
    public ImageLoader imageLoader = ImageLoader.getInstance();
    public ImageLoader imageLoader2 = ImageLoader.getInstance();
    ViewPager pager;
    DisplayImageOptions options;
    DisplayImageOptions options2;
    public static final String EXTRA_URL = "url";
    //ArrayList<Pictures> Pics;

    String image_address;
    //  @InjectView(R.id.image)
    //  ImageView mImageView;
    ImagePagerAdapter adapter;
    static boolean fullPic = true;
    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences.Editor editor;
    SharedPreferences settings;
    String user_Id;
    String user_name;
    ProgressDialog pDialog;
    VolleyGeneric volley;

    String imageId;
    int pos;

    private HashMap<String, String> hashMap;
    private String mLayoutURL;
    private int current_page = 1;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    //ArrayList<Pictures> pictures = new ArrayList<>();
    public ArrayList<Pictures> picturesAll = new ArrayList<>();
    JSONArray jsonArray;

    String url;

    private Toast mToast;
    private int mLayoutId;
   // String likeCount;
    //public static int comment_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        volley = new VolleyGeneric(DetailImagesActivity.this);
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        pDialog = new ProgressDialog(getApplicationContext());
      //  pDialog.setMessage("???? ????? ??????");
        pDialog.setCancelable(false);
        //   mBackground = mImageView;
        String imageUrl = getIntent().getExtras().getString(EXTRA_URL);
        hashMap = (HashMap<String, String>) getIntent().getSerializableExtra("hash");
        Intent intent = getIntent();
       // Pics = (ArrayList<Pictures>) intent.getSerializableExtra("picsList");
        image_address =  intent.getStringExtra("picsList");

        url = intent.getStringExtra("url");
        //   Pics=ReviewFragment.picturesAll;
        //final String position = intent.getStringExtra("position");
       // int currNumber = Integer.parseInt(position);

       // mThumbIds = new String[Pics.size()];
        //for (int i = 0; i < Pics.size(); i++) {

            //  mThumbIds[i] = "http://www.ifilmtv.ir/images/english/slide/new/213/" + i + ".jpg";
            //mThumbIds[i] = Pics.get(i).getImageAddress();
          //  mThumbIds[i] = image_address;

        //}
        if (!imageLoader.isInited())
            imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        if (!imageLoader2.isInited())
            imageLoader2.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        options = new DisplayImageOptions.Builder()

                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(600))
                .build();

        options2 = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.ic_stub)
                //.showImageForEmptyUri(R.drawable.ic_empty)
                //.showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                        //.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                        //     .considerExifParams(true)
                .build();


        adapter = new ImagePagerAdapter();
        pager = (HackyViewPager) findViewById(R.id.pagerPics);
        pager.setAdapter(adapter);
        pager.setPageTransformer(true, new ParallaxViewTransformer(R.id.ivPhoto));
       // pager.setCurrentItem(currNumber);


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private class ImagePagerAdapter extends PagerAdapter {



        private LayoutInflater inflater;

        ImagePagerAdapter() {

            inflater = getLayoutInflater();

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            View imageLayout = inflater.inflate(R.layout.photo_full_pic, view, false);
            final ViewHolder holder;
            assert imageLayout != null;
            holder = new ViewHolder();

            holder.ivPhoto = (PhotoView) imageLayout.findViewById(R.id.ivPhoto);
            holder.imComment = (LinearLayout) imageLayout.findViewById(R.id.llComment);
            holder.ivLikePhoto = (ImageView) imageLayout.findViewById(R.id.ivLikePhoto);
            holder.linearUserName = (LinearLayout) imageLayout.findViewById(R.id.linearUserName);
            holder.linearFooter = (LinearLayout) imageLayout.findViewById(R.id.linearFooter);
            holder.imageViewShare = (ImageView) imageLayout.findViewById(R.id.imageViewShare);
            holder.loadingPhotos = (ProgressBar) imageLayout.findViewById(R.id.loadingPhotos);
            holder.textViewLike = (TextView) imageLayout.findViewById(R.id.textViewLike);
            holder.textViewComment = (TextView) imageLayout.findViewById(R.id.textViewComment);
            holder.tvTitle = (TextView) imageLayout.findViewById(R.id.tvTitle);
            holder.tvUserName = (TextView) imageLayout.findViewById(R.id.tvUserName);
            com.makeramen.roundedimageview.RoundedImageView ivUser = (com.makeramen.roundedimageview.RoundedImageView) imageLayout.findViewById(R.id.ivUser);

            holder.ivPhoto.setTag(position);


            /*try {
              //  Log.d("llllllllll,", settings.getString("lik_count", Pics.get(position).getImageLikeCount()) + "");
               // String liked = settings.getString("like_video_" + Pics.get(position).getImageId(), "0");
               // String likedCount = settings.getString("lik_count" + Pics.get(position).getImageId(), Pics.get(position).getImageLikeCount());
                if (liked.equals("0")) {
                    holder.ivLikePhoto.setImageResource(R.drawable.like_icon);
                    holder.textViewLike.setText(likedCount);
                } else if (liked.equals("1")) {
                    holder.ivLikePhoto.setImageResource(R.drawable.liked_icon);
                    holder.textViewLike.setText(likedCount);
                    Log.d("ooooo", likedCount + "");
                }
            } catch (Exception e) {
//            settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                editor = settings.edit();
                editor.putString("like_video_" + Pics.get(position).getImageId(), "0");
                editor.apply();
            }*/


            imageLoader.displayImage(image_address, holder.ivPhoto, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.loadingPhotos.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.loadingPhotos.setVisibility(View.GONE);
                }
            });


            /*holder.linearUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
                    ft.remove(fragment);

                    ProfileFragment profileFragment = new ProfileFragment();
                    Bundle b = new Bundle();
                    String username = Pics.get(position).getUserNickName();
                    Log.d("YYYYYY",Pics.get(position).getUserNickName() + "");
                    b.putString("username", username);
                    profileFragment.setArguments(b);


                    ft.add(R.id.content, profileFragment);
                    ft.addToBackStack(ProfileFragment.class.getName());
                    ft.commit();

                    finish();
                }
            });
*/
            if (!fullPic) {

                holder.linearUserName.setVisibility(View.GONE);
                holder.linearFooter.setVisibility(View.GONE);
                //holder.ivPhoto.setZoomable(true);


            } else if (fullPic) {
                holder.linearUserName.setVisibility(View.VISIBLE);
                holder.linearFooter.setVisibility(View.VISIBLE);
                //holder.ivPhoto.setZoomable(false);

            }

            holder.ivPhoto.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (fullPic) {
                        holder.linearUserName.setVisibility(View.GONE);
                        holder.linearFooter.setVisibility(View.GONE);
                        fullPic = false;
                        // holder.ivPhoto.setZoomable(true);
                        adapter.notifyDataSetChanged();

                    } else {
                        holder.linearUserName.setVisibility(View.VISIBLE);
                        holder.linearFooter.setVisibility(View.VISIBLE);
                        fullPic = true;
                        // holder.ivPhoto.setZoomable(false);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            /*editor = settings.edit();
            editor.putString("comment_video_" + Pics.get(position).getImageId(), Pics.get(position).getImageCommentCount() + "");
            editor.apply();*/

            //String comment =  settings.getString("comment_video_" + Pics.get(position).getImageId(), "");

          //  Log.d("COMENTCOUNT1", comment + "");
          //  comment_count = Integer.parseInt(Pics.get(position).getImageCommentCount());
            //Log.d("COMENTCOUNT2",comment_count+"");
            //holder.textViewLike.setText(Pics.get(position).getImageLikeCount());
           // holder.textViewComment.setText(comment+"");
            holder.tvTitle.setText((position+1)+getString(R.string.az)+getCount());
          //  holder.tvUserName.setText(Pics.get(position).getUserNickName());
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //startActivity(new Intent(DetailImagesActivity.this,ZoomPhotoActivity.class).putExtra("Photo",Pics).putExtra("pos",position));

                    if (fullPic) {
                        holder.linearUserName.setVisibility(View.GONE);
                        holder.linearFooter.setVisibility(View.GONE);
                        fullPic = false;
                        // holder.ivPhoto.setZoomable(true);
                        adapter.notifyDataSetChanged();

                    } else {
                        holder.linearUserName.setVisibility(View.VISIBLE);
                        holder.linearFooter.setVisibility(View.VISIBLE);
                        fullPic = true;
                        // holder.ivPhoto.setZoomable(false);
                        adapter.notifyDataSetChanged();
                    }

                }
            });










            holder.imageViewShare.setTag(position);
            holder.imageViewShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);


                   // share.putExtra(Intent.EXTRA_TEXT, Pics.get(position).getImageAddress());
                    share.putExtra(Intent.EXTRA_TEXT, image_address);

                    startActivity(Intent.createChooser(share, "Share link!"));

                }
            });

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


        public class ViewHolder {
            //ImageView ivPhoto;
            PhotoView ivPhoto;
            LinearLayout imComment;
            ImageView ivLikePhoto;
            LinearLayout linearUserName;
            LinearLayout linearFooter;
            ImageView imageViewShare;
            ProgressBar loadingPhotos;
            TextView textViewLike;
            TextView textViewComment;
            TextView tvTitle;
            TextView tvUserName;
            com.makeramen.roundedimageview.RoundedImageView ivUser;
        }


        private Response.ErrorListener likeErrorListener() {
            return new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    try {
                        Toast.makeText(getApplicationContext(), error + "error darim inja", Toast.LENGTH_LONG).show();

                    } catch (Exception ex)

                    {
                        Log.e("ERROR", ex + "");
                    }

                }
            };
        }

    }

    public class ParallaxViewTransformer implements ViewPager.PageTransformer {

        private int mImageId;

        /**
         * The constructor takes a target Id as param, later on we'll use the Id to get the target
         * view and animate only that view instead of the entire page.
         *
         * @param mImageId Target View ID.
         */
        public ParallaxViewTransformer(int mImageId) {
            this.mImageId = mImageId;

        }

        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            // Find target View.
            if (view.findViewById(mImageId) != null) {
                view = view.findViewById(mImageId);
            }


            if (position <= 0) { // [-1,0]
                if (view.getId() == mImageId) {
                    view.setTranslationX(pageWidth * -position / 1.4f);
                } else {
                    // Use the default slide transition when moving to the left page if the target view
                    // is not found.
                    view.setTranslationX(0);
                }

            } else if (position <= 1) { // (0,1]
                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position / 1.4f);


            }


        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public boolean checkInternetConnection() {
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            //if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting())
            if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {

                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {

            return false;
        }
    }






    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

 /*   private Response.Listener<String> reviewResponseListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.d("RESPONSE", response + "");
                    jsonArray = new JSONArray(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    //  showpDialog();
                    pictures = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject movieObject = (JSONObject) jsonArray.get(i);


                        Pictures picturesTO = new Pictures();
                        picturesTO.setId(i);
                        picturesTO.setImageId(movieObject.getString("ImageId"));
                        picturesTO.setImageTitle(movieObject.getString("ImageTitle"));
                        picturesTO.setImageCommentCount(movieObject.getString("imageCommentCount"));
                        picturesTO.setImageAlbumName(movieObject.getString("imageAlbumCount"));
                        picturesTO.setImageThumbnail(movieObject.getString("imageThumbnail"));

                        picturesTO.setUserProfileImage(movieObject.getString("userProfileImage"));

                        picturesTO.setImageURL480(movieObject.getString("imageURL480"));

                        picturesTO.setImageLikeCount(movieObject.getString("imageLikeCount"));
                        picturesTO.setImageUploadDate(movieObject.getString("imageUploadDate"));
                        picturesTO.setUserImageCount(movieObject.getString("userImageCount"));
                        picturesTO.setUserAlbumCount(movieObject.getString("imageAlbumCount"));
                        picturesTO.setUserNickName(movieObject.getString("userNickName"));


                        pictures.add(picturesTO);
                        picturesAll.add(picturesTO);
                        Pics.add(pictures.get(i));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //   Toast.makeText(getActivity(), "???????? ???? ?? ?????? ??????? ??? ????", Toast.LENGTH_LONG).show();
                }

                mThumbIds = new String[Pics.size()];
                for (int i = 0; i < Pics.size(); i++) {

                    //  mThumbIds[i] = "http://www.ifilmtv.ir/images/english/slide/new/213/" + i + ".jpg";
                    mThumbIds[i] = Pics.get(i).getImageURL480();

                }
                adapter.notifyDataSetChanged();

                loading = true;

            }
        };
    }
*/

 /*   private Response.ErrorListener reviewErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.errorConnect), Toast.LENGTH_LONG).show();

                } catch (Exception ex)

                {
                    Log.e("ERROR", ex + "");
                }
//                Toast.makeText(getActivity(), "??????? ?? ???? ????? ???? ??? ????", Toast.LENGTH_LONG).show();

            }
        };
    }*/


    public void DownloadFromUrl(String fileAddress,String fileName) {
        try {
            URL url = new URL(fileAddress); //you can write here any link
            File file = new File(Environment.getExternalStorageDirectory()+"/"+fileName);

            long startTime = System.currentTimeMillis();
           // tv.setText("Starting download......from " + url);
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
                    /*
                     * Read bytes to the Buffer until there is nothing more to read(-1).
                     */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();
           // tv.setText("Download Completed in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
        } catch (IOException e) {
           // tv.setText("Error: " + e);
        }
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();

        super.onResume();

    }
}


