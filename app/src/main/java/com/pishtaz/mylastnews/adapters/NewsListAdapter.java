package com.pishtaz.mylastnews.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.models.ItemTO;

public class NewsListAdapter extends BaseAdapter{
    Context contextInAdapter;
    ArrayList<ItemTO> itemTOs;
    LayoutInflater inflater;

    ImageLoader imageLoader = ImageLoader.getInstance();

    ImageLoadingListener animateFirstListener;
    DisplayImageOptions options;
    ImageLoaderConfiguration config;

    public NewsListAdapter(Context inputContext, ArrayList<ItemTO> inputItemTOs){
        contextInAdapter = inputContext;
        itemTOs = inputItemTOs;
        inflater = LayoutInflater.from(contextInAdapter);
//

        options = new DisplayImageOptions.Builder()

                //.resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                        //.considerExifParams(true)
                        //.displayer(new FadeInBitmapDisplayer(600))

                .build();

         config = new ImageLoaderConfiguration.Builder(contextInAdapter)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .imageDownloader(new ImageDownloader() {
                    @Override
                    public InputStream getStream(String imageUri, Object extra) throws IOException {
                        return null;
                    }
                })
                .tasksProcessingOrder(QueueProcessingType.LIFO)
//          .enableLogging() // Not necessary in common
                .build();


        if (!imageLoader.isInited()){
            imageLoader.init(config);
        }



    }

    @Override
    public int getCount() {

        return itemTOs.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final ViewHolder holder;

        if (view==null)
        {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.news_item_list_row_layout, null);
            holder.tvNewsTitleCategory = (TextView) view.findViewById(R.id.tvNewsTitleCategory);
            holder.cvCategory = (CardView) view.findViewById(R.id.cvCategory);

            holder.tvNewsTitle = (TextView) view.findViewById(R.id.tvNewsTitle);
            holder.pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
            holder.ivNewsPic = (ImageView) view.findViewById(R.id.ivNewsPic);

            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvNewsTitle.setText(itemTOs.get(position).getTitle());
        holder.tvNewsTitleCategory.setText(itemTOs.get(position).getCategory());
        try {
            holder.cvCategory.setCardBackgroundColor(Color.parseColor(itemTOs.get(position).getCategory_color().trim()));
        }catch (Exception e){

        }

        imageLoader.displayImage(itemTOs.get(position).getImage_address(), holder.ivNewsPic, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.pbLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.pbLoading.setVisibility(View.GONE);
            }
        });

        /*imageLoader.loadImage(itemTOs.get(position).getImage_address(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                //ivTemp.setImageBitmap(loadedImage);
                //Bitmap b = ((BitmapDrawable) ivTemp.getDrawable()).getBitmap();
                Bitmap bmp = imageLoader.loadImageSync(imageUri);
                loadedImage = Bitmap.createScaledBitmap(bmp, 128, 128, true);
                holder.ivNewsPic.setImageBitmap(bmp);
//                    widgetItemTO.setNewsThumbnail(((BitmapDrawable) ivTemp.getDrawable()).getBitmap());
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
*/
        return view;
    }

    private class ViewHolder{
        TextView tvNewsTitle;
        ImageView ivNewsPic;
//        ImageView ivNewscategory;
        ProgressBar pbLoading;
        CardView cvCategory;
        TextView tvNewsTitleCategory;
    }
}
