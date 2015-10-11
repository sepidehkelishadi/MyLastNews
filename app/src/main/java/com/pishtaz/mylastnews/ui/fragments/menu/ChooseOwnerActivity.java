package com.pishtaz.mylastnews.ui.fragments.menu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.mikepenz.materialdrawer.model.interfaces.OnCheckedChangeListener;
import com.pishtaz.mylastnews.MainActivity;
import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.db_model.NewsDA;
import com.pishtaz.mylastnews.db_model.OwnersDA;
import com.pishtaz.mylastnews.models.OwnerTO;

import java.util.ArrayList;
import java.util.List;

public class ChooseOwnerActivity extends Activity {


    ListView lvOwners;
    OwnerAdapter ownerAdapter;
    String cat_id;
    TextView tvSetOwner, tvCancle;
    List<OwnersDA> ownersDAs = new ArrayList<>();
    public static ArrayList<OwnerTO> ownerTOs=new ArrayList<>();
    CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_owner);

        cat_id = getIntent().getStringExtra("cat_id");
        ownersDAs = getAllOwners();


        lvOwners = (ListView) findViewById(R.id.lvOwners);
        tvCancle = (TextView) findViewById(R.id.tvCancle);
        tvSetOwner = (TextView) findViewById(R.id.tvSetOwner);

        ownerAdapter = new OwnerAdapter(getApplicationContext());
        lvOwners.setAdapter(ownerAdapter);
        lvOwners.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvOwners.setItemsCanFocus(false);

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  finish();
                for (int x = 0; x<lvOwners.getChildCount();x++){
                    cb = (CheckBox)lvOwners.getChildAt(x).findViewById(R.id.cbOwner);
                    if(cb.isChecked()){
                       Log.e("22222222",ownerTOs.get(x).getOwner());
                    }
                }
            }
        });




    }

    public class OwnerAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;

        public OwnerAdapter(Context context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return ownerTOs.size();
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
            CheckBox cbOwner;
            LinearLayout llOwner;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.owner_item, null);


            textViewTitle = (TextView) itemView.findViewById(R.id.textView);
            cbOwner = (CheckBox) itemView.findViewById(R.id.cbOwner);
            //  llOwner = (LinearLayout) itemView.findViewById(R.id.llOwner);

            textViewTitle.setTag(position);
            cbOwner.setTag(position);

            textViewTitle.setText(ownerTOs.get(position).getOwner());


            return itemView;
        }

    }

    public static List<OwnersDA> getAllOwners() {
        return new Select()
                .from(OwnersDA.class)
                        //  .where("news_id =?", id)
                .execute();
    }
}
