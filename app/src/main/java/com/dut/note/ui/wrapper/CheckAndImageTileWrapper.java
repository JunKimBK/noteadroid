package com.dut.note.ui.wrapper;

import android.view.ViewStub;

import com.dut.note.App;
import com.dut.note.R;
import com.dut.note.bean.Note;
import com.dut.note.net.sync.FileInputStreamSync;
import com.dut.note.ui.view.ImageStreamView;

/**
 * Created by noem on 10/10/2015.
 */
public class CheckAndImageTileWrapper extends CheckTileWrapper {
    private ImageStreamView imgSingle;
    public CheckAndImageTileWrapper(ViewStub viewStub) {
        super(viewStub, R.layout.layout_view_tile_check_image);
        imgSingle = (ImageStreamView) findViewById(R.id.view_tile_check_image_iv_singleimage);
    }

    @Override
    public void applyNote(Note note) {
        super.applyNote(note);
        if (note.getText() != null && note.getText().length() > 0)
            mIsDispMorePrevRemain = false;
        //FileInputStreamSync inputStreamSync = App.getSyncManager().getFileForRead(note.getImages().get(0));
        imgSingle.setSyncImageResource(note.getImages().get(0), App.getSyncManager());
    }
}
