package com.dut.note.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.dut.note.R;
import com.dut.note.bean.Note;
import com.dut.note.ui.wrapper.CheckAndImageTileWrapper;
import com.dut.note.ui.wrapper.CheckTileWrapper;
import com.dut.note.ui.wrapper.ImageTileWrapper;
import com.dut.note.ui.wrapper.TextTileWrapper;
import com.dut.note.ui.wrapper.TileWrapper;

import java.util.List;

public class TileAdapter extends ListAdapter<Note> {
    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public TileAdapter(Context context, List<Note> list) {
        super(context, list);
    }

    @Override
    public int getViewTypeCount() {
        return TileWrapper.TILE_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Note note = (Note) getItem(position);
        if(note.getChecks() != null && note.getImages() != null)
            return TileWrapper.TILE_TYPE_BOTH;
        else if(note.getChecks() != null)
            return TileWrapper.TILE_TYPE_CHECK;
        else if(note.getImages() != null)
            return TileWrapper.TILE_TYPE_IMAGE;
        return TileWrapper.TILE_TYPE_TEXT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int type = getItemViewType(position);
        if(convertView == null) {
            convertView = getInflater().inflate(R.layout.layout_view_tile, null);
            ViewStub viewStub = (ViewStub)convertView.findViewById(R.id.view_tile_stub);
            TextView labTime = (TextView)convertView.findViewById(R.id.view_tile_time);
            holder = new ViewHolder();
            holder.labTime = labTime;
            switch (type) {
                case TileWrapper.TILE_TYPE_BOTH:
                    holder.tileWrapper = new CheckAndImageTileWrapper(viewStub);
                    break;
                case TileWrapper.TILE_TYPE_CHECK:
                    holder.tileWrapper = new CheckTileWrapper(viewStub);
                    break;
                case TileWrapper.TILE_TYPE_IMAGE:
                    holder.tileWrapper = new ImageTileWrapper(viewStub);
                    break;
                case TileWrapper.TILE_TYPE_TEXT:
                    holder.tileWrapper = new TextTileWrapper(viewStub);
                    break;
            }

            convertView.setTag(holder);

            convertView.setOnClickListener(mOnClickListener);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Note note = mList.get(position);
        holder.position = position;
        holder.labTime.setText(note.getCreated().getEstimateDateTime());
        holder.tileWrapper.applyNote(note);
        return convertView;
    }

    public static class ViewHolder {
        public TileWrapper tileWrapper;
        public TextView labTime;
        public int position;
    }
}
