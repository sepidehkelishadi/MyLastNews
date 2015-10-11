package com.pishtaz.mylastnews.ui.fragments.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.db_model.NewsDA;
import com.pishtaz.mylastnews.models.News2TO;
import com.pishtaz.mylastnews.networks.VolleyGeneric;
import com.pishtaz.mylastnews.ui.activities.DetailNewsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailNewsPagerSearch extends AppCompatActivity {


    TabsPagerAdapter adapter;
    ViewPager newsPager;
    int position;
    String news_id, SearchKey;
    ArrayList<News2TO> newsTOArrayList = new ArrayList<>();
    VolleyGeneric volley;
    private static int page = 1;
    //  String cat_id;
    private static final String POST_Url = "http://websrc.pishtaz.ir/android.asmx/Search";
    VolleyGeneric volleyGeneric;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news_pager);

        volleyGeneric = new VolleyGeneric(getApplicationContext());

        String positionStr = getIntent().getStringExtra("position");
        SearchKey = getIntent().getStringExtra("SearchKey");

        Log.d("UUUUUUUUUU", positionStr + "");
        position = Integer.parseInt(positionStr);

        news_id = getIntent().getStringExtra("news_id");
        newsTOArrayList = (ArrayList<News2TO>) getIntent().getSerializableExtra("arraylist");

        newsPager = (ViewPager) findViewById(R.id.newsPager);
        volley = new VolleyGeneric(this);

        adapter = new TabsPagerAdapter(getSupportFragmentManager(), news_id);
        newsPager.setAdapter(adapter);
        newsPager.setCurrentItem(position);
        //newsPager.setOffscreenPageLimit(2);
        newsPager.setPageTransformer(true, new ParallaxViewTransformer(R.id.ivCover));
        Log.d("POSITIONCURENT", position + "");
        Log.d("NEWSID", news_id + "");


        newsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                NewsDA newsDA = new NewsDA();
                newsDA.news_id = newsTOArrayList.get(position).getId();
                //  newsDA.Id = newsTO.get(position).getId();
                newsDA.title = newsTOArrayList.get(position).getTitle();
                newsDA.image_address = newsTOArrayList.get(position).getImage_address();
                newsDA.save();

                page = page + 1;
                String pageStr = String.valueOf(page);
                if (position == newsTOArrayList.size() - 2) {

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("token", VolleyGeneric.TOKEN);
                    hashMap.put("TypeId", "6");
                    hashMap.put("SearchKey", SearchKey);
                    hashMap.put("pagenumber", pageStr);

                    volleyGeneric.getJson(POST_Url, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), hashMap);

                    //  volley.getJson("http://mylastnews.ir/ws/category/?cat_id=" + cat_id + "&page=" + page, reviewResponseListenerGetNews(), reviewErrorListenerGetNews(), null);

                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_detail_news_pager, menu);
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


    public class TabsPagerAdapter extends FragmentPagerAdapter {

        int get_position;
        String news_id;

        public TabsPagerAdapter(FragmentManager fm, String news_id) {
            super(fm);
            this.news_id = news_id;
        }

        @Override
        public Fragment getItem(int position) {

            Log.d("NEWSIDADAPTER", newsTOArrayList.get(position).getId() + "");
            return DetailNewsFragment.newInstance(newsTOArrayList.get(position).getId(),newsTOArrayList.get(position).getNews_id());

        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return newsTOArrayList.size();
        }

   /* @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }*/

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position >= getCount()) {
                FragmentManager manager = ((Fragment) object).getFragmentManager();
                FragmentTransaction trans = manager.beginTransaction();
                trans.remove((Fragment) object);
                trans.commit();
            }
        }
    }

    private Response.Listener<String> reviewResponseListenerGetNews() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {

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
                        //   itemTO.setId(obj.getString("id"));
                        //   itemTO.setCategory(obj.getString("category"));
                        //    itemTO.setCategory_color(obj.getString("category_color"));


                        // allItemTOs.add(itemTO);
                        newsTOArrayList.add(itemTO);
                    }

                    // >>> header news 2


                    //     populateWidgetItemTOs();

                    //  rlVideo.setVisibility(View.VISIBLE);

                    adapter.notifyDataSetChanged();
                    // progressbarList.setVisibility(View.GONE);

                    Log.d("REFRESH TAB CONTENT :", response + "");


                } catch (Exception e) {
                    Log.e("ERRPRRRRRRRRRRRR", e + "");
//                    Toast.makeText(getActivity(), "try fetching url Failed", Toast.LENGTH_LONG).show();
                }


            }
        };
    }

    ;

    private Response.ErrorListener reviewErrorListenerGetNews() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), R.string.error_json_error_listener, Toast.LENGTH_LONG).show();


            }
        };
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

}
