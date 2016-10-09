package com.dut.note.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dut.note.R;
import com.dut.note.net.sync.OnLoginOKListener;

import java.lang.ref.WeakReference;

public class WelcomeActivity extends AppCompatActivity {
    public static WeakReference<WelcomeActivity> instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = new WeakReference<>(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
}
