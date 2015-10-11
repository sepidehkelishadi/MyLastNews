package com.pishtaz.mylastnews.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.utils.Utils;


public class MenuListAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    int[] mTitle;

    int[] mIcon;
    LayoutInflater inflater;
    public static final String PREFS_NAME = "NewsShared";
    SharedPreferences.Editor editor;
    SharedPreferences settings;

    public MenuListAdapter(Context context, int[] title, int[] icon) {
        this.context = context;
        this.mTitle = title;

        this.mIcon = icon;
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        holder = new ViewHolder();
        holder.txtTitle = (TextView) itemView.findViewById(R.id.title);
        holder.imgIcon = (ImageView) itemView.findViewById(R.id.icon);

        holder.txtTitle.setTag(position);
        holder.imgIcon.setTag(position);
        settings = context.getSharedPreferences(PREFS_NAME, 0);

        // >>> read different menu-titles and menu-icons based on User-Status
        if (settings.getString("User_Id", "").equals("")) {
            holder.txtTitle.setText(context.getString(mTitle[position]));
            holder.imgIcon.setImageResource(mIcon[position]);
        } else {
            holder.txtTitle.setText(context.getString(Utils.menuTitleName_login[position]));
            holder.imgIcon.setImageResource(mIcon[position]);
        }

        return itemView;
    }

    public class ViewHolder {
        TextView txtTitle;
        ImageView imgIcon;
    }

}
