package com.dut.note.ui.wrapper;

import android.view.ViewStub;
import android.widget.TextView;

import com.dut.note.App;
import com.dut.note.R;
import com.dut.note.bean.Note;
import com.dut.note.net.sync.FileInputStreamSync;
import com.dut.note.ui.view.ImageStreamView;

public class ImageTileWrapper extends TileWrapper {
    private ImageStreamView imageView;
    private TextView labTitle;
    private TextView labContent;

    public ImageTileWrapper(ViewStub viewStub) {
        super(viewStub, R.layout.layout_view_tile_image);
        imageView = (ImageStreamView) findViewById(R.id.view_tile_image_iv_singleimage);
        labTitle = (TextView) findViewById(R.id.view_tile_image_tv_title);
        labContent = (TextView) findViewById(R.id.view_tile_image_tv_content);
    }

    @Override
    public void applyNote(Note note) {
        super.applyNote(note);
        //FileInputStreamSync inputStreamSync = App.getSyncManager().getFileForRead(note.getImages().get(0));
        imageView.setSyncImageResource(note.getImages().get(0), App.getSyncManager());
        labTitle.setText(note.getTitle());
        labContent.setText(note.getText());
    }
}
