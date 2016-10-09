package com.dut.note.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dut.note.R;

/**
 * Created by noem on 11/11/2015.
 */
public class SettingFragment extends ContentFragment {

    private static String[] SET_OF_COLOR = {"Red", "Orange", "Pink", "Green", "Blue", "Indigo", "Purple"};
    public static int TILE_BACKCOLOR_REMAIN = R.color.choose_green;
    public static int TILE_BACKCOLOR_DONE = R.color.choose_blue;
    static int BUTTON_REMAIN = 0;
    static int BUTTON_DONE = 1;
    private Button mBtnColorDone;
    private Button mBtnColorRemain;

    public SettingFragment() {
        super(R.layout.layout_fragment_setting, "Setting");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_setting, container, false);
        mBtnColorDone = (Button) view.findViewById(R.id.button_change_color_done);
        mBtnColorRemain = (Button) view.findViewById(R.id.button_change_color_remain);

        mBtnColorDone.setBackgroundColor(getResources().getColor(TILE_BACKCOLOR_DONE));
        mBtnColorRemain.setBackgroundColor(getResources().getColor(TILE_BACKCOLOR_REMAIN));

        mBtnColorRemain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorDialog(BUTTON_REMAIN);
            }
        });
        mBtnColorDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorDialog(BUTTON_DONE);
            }
        });
        return view;
    }

    public void createColorDialog(int button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (button == BUTTON_REMAIN) {
            builder.setTitle("Pick a remain color")
                    .setItems(SET_OF_COLOR, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getActivity(),"Choose " + ((Integer) which).toString(), Toast.LENGTH_SHORT);
                            switch (which) {
                                case 0:
                                    TILE_BACKCOLOR_REMAIN = R.color.choose_red;
                                    break;
                                case 1:
                                    TILE_BACKCOLOR_REMAIN = R.color.choose_orange;
                                    break;
                                case 2:
                                    TILE_BACKCOLOR_REMAIN = R.color.choose_pink;
                                    break;
                                case 3:
                                    TILE_BACKCOLOR_REMAIN = R.color.choose_green;
                                    break;
                                case 4:
                                    TILE_BACKCOLOR_REMAIN = R.color.choose_blue;
                                    break;
                                case 5:
                                    TILE_BACKCOLOR_REMAIN = R.color.choose_indigo;
                                    break;
                                case 6:
                                    TILE_BACKCOLOR_REMAIN = R.color.choose_purple;
                                    break;
                            }
                            mBtnColorRemain.setBackgroundColor(getResources().getColor(TILE_BACKCOLOR_REMAIN));
                        }
                    });
            builder.create();
            builder.show();
        }
        if (button == BUTTON_DONE) {
            builder.setTitle("Pick a done color")
                    .setItems(SET_OF_COLOR, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getActivity(),"Choose " + ((Integer) which).toString(), Toast.LENGTH_SHORT);
                            switch (which) {
                                case 0:
                                    TILE_BACKCOLOR_DONE = R.color.choose_red;
                                    break;
                                case 1:
                                    TILE_BACKCOLOR_DONE = R.color.choose_orange;
                                    break;
                                case 2:
                                    TILE_BACKCOLOR_DONE = R.color.choose_pink;
                                    break;
                                case 3:
                                    TILE_BACKCOLOR_DONE = R.color.choose_green;
                                    break;
                                case 4:
                                    TILE_BACKCOLOR_DONE = R.color.choose_blue;
                                    break;
                                case 5:
                                    TILE_BACKCOLOR_DONE = R.color.choose_indigo;
                                    break;
                                case 6:
                                    TILE_BACKCOLOR_DONE = R.color.choose_purple;
                                    break;
                            }
                            mBtnColorDone.setBackgroundColor(getResources().getColor(TILE_BACKCOLOR_DONE));
                        }
                    });
            builder.create();
            builder.show();
        }
    }
}
