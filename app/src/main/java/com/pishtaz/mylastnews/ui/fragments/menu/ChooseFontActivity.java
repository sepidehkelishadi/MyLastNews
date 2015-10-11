package com.pishtaz.mylastnews.ui.fragments.menu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pishtaz.mylastnews.R;


public class ChooseFontActivity extends AppCompatActivity {

    ListView listViewChooseFont;
    String[] fontList = {"IRAN Sans Light.ttf", "QuranTaha.ttf", "BNazaninRegular.ttf"};
    String[] fontListMain = new String[3];
    MyFontAdapter myFontAdapter;
    public static boolean status = false;
    public static String font_name = "";
    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences.Editor editor;
    SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_font);


        listViewChooseFont = (ListView) findViewById(R.id.listViewChooseFont);
        fontListMain[0] = getResources().getString(R.string.font1);
        fontListMain[1] = getResources().getString(R.string.font2);
        fontListMain[2] = getResources().getString(R.string.font3);

        myFontAdapter = new MyFontAdapter(getApplicationContext());
        listViewChooseFont.setAdapter(myFontAdapter);

        listViewChooseFont.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                settings = getSharedPreferences(PREFS_NAME, 0);
                editor = settings.edit();
                editor.putString("my_font", fontList[position]);
                font_name = fontListMain[position];
                editor.apply();

                status = true;
                finish();

            }
        });


    }

    public class MyFontAdapter extends BaseAdapter {


        Context context;

        LayoutInflater inflater;

        public MyFontAdapter(Context context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            TextView textViewTitle;
            ImageView imageViewDefault;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.item_choose_font, null);

            textViewTitle = (TextView) itemView.findViewById(R.id.textViewChooseHotel);
            imageViewDefault = (ImageView) itemView.findViewById(R.id.imageViewDefault);

            textViewTitle.setTag(position);
            imageViewDefault.setTag(position);

            textViewTitle.setText(fontListMain[position]);

            settings = getSharedPreferences(PREFS_NAME, 0);

            if (fontList[position].equals(settings.getString("my_font", ""))) {
                imageViewDefault.setImageResource(R.drawable.tick_icon_circle_green);
                textViewTitle.setTextColor(Color.BLACK);
            } else {
                imageViewDefault.setImageResource(R.drawable.tick_icon_circle);
                textViewTitle.setTextColor(Color.parseColor("#bf000000"));
            }


            return itemView;
        }

    }


}