package com.dut.note.ui.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dut.note.R;

/**
 * Created by VietHoang on 23/10/2015.
 */
public class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    String[] mListString;
    int[] mImage = {R.drawable.ic_note, R.drawable.ic_acount, R.drawable.ic_setting,
            R.drawable.ic_help, R.drawable.ic_about,
            R.drawable.ic_exit};

    public DrawerListAdapter(Context context) {
        mContext = context;
        mListString = context.getResources().getStringArray(R.array.navigation_drawer_items_array);
    }

    @Override
    public int getCount() {
        return mListString.length;
    }

    @Override
    public Object getItem(int position) {
        return mListString[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.drawer_list_custom_row, parent, false);
        } else {
            row = convertView;
        }
        TextView titleTextView = (TextView) row.findViewById(R.id.drawer_list_text);
        ImageView titleImageView = (ImageView) row.findViewById(R.id.drawer_list_image);
        titleTextView.setText(mListString[position]);
        titleImageView.setImageResource(mImage[position]);
        return row;
    }
}
