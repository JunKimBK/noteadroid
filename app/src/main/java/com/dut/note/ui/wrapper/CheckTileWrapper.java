package com.dut.note.ui.wrapper;

import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.dut.note.App;
import com.dut.note.R;
import com.dut.note.bean.Check;
import com.dut.note.bean.Note;
import com.dut.note.ui.view.SettingFragment;

import java.util.ArrayList;
import java.util.List;

public class CheckTileWrapper extends TileWrapper {
    protected boolean mIsDispMorePrevRemain = true;
    private TextView labTitle;
    private TextView labContent;

    private void init() {
        labTitle = (TextView) findViewById(R.id.view_tile_check_tv_title);
        labContent = (TextView) findViewById(R.id.view_tile_check_tv_content);
    }

    public CheckTileWrapper(ViewStub viewStub) {
        super(viewStub, R.layout.layout_view_tile_check);
        init();
    }

    protected CheckTileWrapper(ViewStub viewStub, int layoutId) {
        super(viewStub, layoutId);
        init();
    }

    protected void setRemainList(Note note) {
        TextView[] tvRemains = new TextView[3];
        tvRemains[0] = (TextView) findViewById(R.id.view_tile_check_tv_remain1);
        tvRemains[1] = (TextView) findViewById(R.id.view_tile_check_tv_remain2);
        tvRemains[2] = (TextView) findViewById(R.id.view_tile_check_tv_remain3);

        for (TextView tv : tvRemains) {
            tv.setVisibility(View.GONE);
        }

        TextView labRemain = (TextView) findViewById(R.id.view_tile_check_tv_remain);

        int remain = 0;
        List<Check> checks = note.getChecks();
        List<String> remainChecks = new ArrayList<String>();
        for (int i = 0, count = checks.size(); i < count; i++) {
            if (!checks.get(i).isChecked()) {
                remain++;
                remainChecks.add(String.format("%d. %s", i + 1, checks.get(i).getText()));
            }
        }

        if (remain > 0) {
            labRemain.setText(String.format("%d", remain));
            int dispCount = Math.min(remainChecks.size(), tvRemains.length);
            for (int i = 0; i < dispCount; i++) {
                tvRemains[i].setText(remainChecks.get(i));
                tvRemains[i].setVisibility(View.VISIBLE);
            }
            if (!mIsDispMorePrevRemain)
                tvRemains[2].setVisibility(View.GONE);
            setBackgroundResource(SettingFragment.TILE_BACKCOLOR_REMAIN);
            App.getDbManager().setRemainColor(SettingFragment.TILE_BACKCOLOR_REMAIN + "");
        } else {
            labRemain.setText("Done!");
            setBackgroundResource(SettingFragment.TILE_BACKCOLOR_DONE);
            App.getDbManager().setDoneColor(SettingFragment.TILE_BACKCOLOR_DONE + "");
        }
    }

    @Override
    public void applyNote(Note note) {
        super.applyNote(note);
        labTitle.setText(note.getTitle());
        labContent.setText(note.getText());
        setRemainList(note);
    }
}
