package com.dut.note.net.sync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;

public abstract class DropboxLinker extends AppCompatActivity {
    private static final int REQUEST_LINK_TO_DBX = 0;
    protected final static String TAG = "Dropbox";
    protected String mInitFolder;
    protected DbxAccountManager mDbxAcctMgr;

    protected boolean isLinked(){
        return mDbxAcctMgr.hasLinkedAccount();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbxAcctMgr = DropboxConfig.getAccountManager(this);
        if(mDbxAcctMgr.hasLinkedAccount())
            onLinked();
        else
            startLink();
    }

    protected abstract void onLinked();

    protected void startLink() {
        mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LINK_TO_DBX){
            if(resultCode == RESULT_OK)
                onLinked();
            else
                Toast.makeText(this, "Link to Dropbox failed or was cancelled.", Toast.LENGTH_SHORT).show();
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
