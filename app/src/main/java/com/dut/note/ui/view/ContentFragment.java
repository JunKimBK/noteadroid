package com.dut.note.ui.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dut.note.ui.MainActivity;

public abstract class ContentFragment extends Fragment {
    protected static final MainActivity mActivity;

    private int mLayoutId;

    protected void runItOnBackground() {
    }

    protected void runItWhenDone() {
    }

    protected void startWorker() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runItOnBackground();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runItWhenDone();
                    }
                });
            }
        }).start();
    }

    protected View findViewById(int id) {
        return mActivity.findViewById(id);
    }

    public ContentFragment(int layoutId, String title){
        mLayoutId = layoutId;
        mActivity.setTitle(title);
    }

    protected void onCreatedView(View root) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(mLayoutId, container, false);
        onCreatedView(rootView);
        return rootView;
    }

    static {
        mActivity = MainActivity.getInstance();
    }
}
