package com.dut.note.ui.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dut.note.App;
import com.dut.note.R;
import com.dut.note.net.sync.OnLoginOKListener;

/**
 * Created by noem on 11/11/2015.
 */
public class AccountFragment extends ContentFragment implements View.OnClickListener, OnLoginOKListener {
    private Button mLogoutButton;
    private TextView mAccNameLabel;

    public AccountFragment() {
        super(R.layout.layout_fragment_account, "Account");
    }

    @Override
    protected void onCreatedView(View root) {
        mAccNameLabel = (TextView) root.findViewById(R.id.fragment_account_tv_name);
        mLogoutButton = (Button) root.findViewById(R.id.fragment_account_btn_logout);

        mLogoutButton.setOnClickListener(this);

        setState(App.getSyncManager().isLogin());
    }

    private void setState(boolean login) {
        if(login) {
            mAccNameLabel.setText("Your are login with " + App.getSyncManager().getAccountName());
            mLogoutButton.setText("Logout");
        } else {
            mAccNameLabel.setText("");
            mLogoutButton.setText("Login");
            mLogoutButton.setOnClickListener(this);
            App.getSyncManager().requestLogin();
        }
    }

    @Override
    public void onClick(View v) {
        if(mLogoutButton.getText().equals("Logout")) {
            // request logout
            Toast.makeText(AccountFragment.this.getActivity(), "Logout...", Toast.LENGTH_SHORT).show();
            App.getSyncManager().logout();
            setState(false);
        } else {
            // request login
            Toast.makeText(AccountFragment.this.getActivity(), "Login...", Toast.LENGTH_SHORT).show();
            App.getSyncManager().setOnLoginOKListener(this);
            App.getSyncManager().requestLogin();
        }
    }

    @Override
    public void onDestroy() {
        mLogoutButton.setOnClickListener(null);
        mLogoutButton = null;
        App.getSyncManager().setOnLoginOKListener(null);
        super.onDestroy();
    }

    @Override
    public void onLoginOK() {
        setState(true);
        Toast.makeText(AccountFragment.this.getActivity(), "OK! Now you are logined", Toast.LENGTH_SHORT).show();
    }
}