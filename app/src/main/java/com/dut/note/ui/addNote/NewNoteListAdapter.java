package com.dut.note.ui.addNote;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dut.note.R;
import com.dut.note.bean.Check;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VietHoang on 25/10/2015.
 */
public class NewNoteListAdapter extends ArrayAdapter<Check> {
    public static int deleteItemIndex[] = {0, 0};
    Context mContext;
    int mLayoutResourceId;
    List<Check> mData = null;

    public NewNoteListAdapter(Context context, int layoutResourceId, List<Check> data) {
        super(context, layoutResourceId, data);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mData = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        ListItemHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new ListItemHolder();

            holder.checkbox = (CheckBox) row.findViewById(R.id.checkbox_check);
            holder.image = (ImageView) row.findViewById(R.id.checkbox_image);
            holder.text = (EditText) row.findViewById(R.id.checkbox_text);

            row.setTag(holder);

        } else {
            row = convertView;
            holder = (ListItemHolder) row.getTag();
        }

        final Check item = mData.get(position);
        holder.checkbox.setChecked(item.isChecked());
        holder.text.setHint("Job description");
        holder.text.setText(item.getText());
        holder.text.setSingleLine(true);
        //holder.image.setImageResource(R.drawable.ic_close_black_24dp);
        holder.image.setTag(position);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Log.i("Position", String.valueOf(position));
                deleteItemIndex[0] = 1;
                deleteItemIndex[1] = position;*/
                Integer index = (Integer) v.getTag();
                mData.remove(index.intValue());
                notifyDataSetChanged();
            }
        });
        holder.text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    item.setText(((EditText) v).getText().toString());
            }
        });
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
            }
        });
        return row;
    }

    public void createNewCheckbox(View view) {
        /*ListItem newItem = new ListItem();
        mListItem.add(newItem);
        Log.i("NewNoteListAdapter", "Size " + mListItem.size());
        notifyDataSetChanged();*/
    }

    /*@Override
    public void add(ListItem item) {
        mData.add(item);
        notifyDataSetChanged();
    }*/

    public static class ListItemHolder {
        CheckBox checkbox;
        ImageView image;
        EditText text;
    }
}
