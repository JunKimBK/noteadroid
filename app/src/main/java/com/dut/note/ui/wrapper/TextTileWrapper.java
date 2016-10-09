package com.dut.note.ui.wrapper;

import android.view.ViewStub;
import android.widget.TextView;

import com.dut.note.R;
import com.dut.note.bean.Note;

public class TextTileWrapper extends TileWrapper {
    private TextView labTitle;
    private TextView labContent;

    public TextTileWrapper(ViewStub viewStub) {
        super(viewStub, R.layout.layout_view_tile_text);
        labTitle = (TextView) findViewById(R.id.view_tile_text_tv_title);
        labContent = (TextView) findViewById(R.id.view_tile_text_tv_content);
    }

    @Override
    public void applyNote(Note note) {
        super.applyNote(note);
        labTitle.setText(note.getTitle());
        labContent.setText(note.getText());
    }
}
