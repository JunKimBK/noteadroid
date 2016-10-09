package com.dut.note.ui.wrapper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.dut.note.R;
import com.dut.note.bean.Note;

public abstract class TileWrapper {
    public static final int TILE_TYPE_COUNT = 4;
    public static final int TILE_TYPE_TEXT = 0;
    public static final int TILE_TYPE_IMAGE = 1;
    public static final int TILE_TYPE_CHECK = 2;
    public static final int TILE_TYPE_BOTH = 3;
    private final View mGenView;

    protected void setBackgroundResource(int id) {
        mGenView.setBackgroundResource(id);
    }

    public TileWrapper(ViewStub viewStub, int subTree) {
        viewStub.setLayoutResource(subTree);
        mGenView = viewStub.inflate();
    }

    protected View findViewById(int id) {
        return mGenView.findViewById(id);
    }

    protected Context getContext() {
        return mGenView.getContext();
    }

    public void applyNote(Note note) {
        //TextView labTime = (TextView) ((ViewGroup)mGenView.getParent()).findViewById(R.id.view_tile_time);
        //labTime.setText(note.getCreated().getEstimateDateTime());
    }
}
