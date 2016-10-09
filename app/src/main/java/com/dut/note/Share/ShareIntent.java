package com.dut.note.Share;

/**
 * Created by VoTung246 on 2/10/2015.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.support.v4.app.ShareCompat;

import com.dut.note.App;
import com.dut.note.bean.Check;
import com.dut.note.bean.Note;
import com.dut.note.ui.ViewNote;

import static android.support.v4.app.ActivityCompat.startActivity;

interface IIntent {
    void SendIntent(Intent intent);
}

public class ShareIntent {
    private IIntent mintent;
    private Intent share;

    public Intent getShare() {
        return share;
    }

    public void SetIntent(IIntent intent) {
        mintent = intent;
    }

    public void share(Note note) {
        share = new Intent(Intent.ACTION_SEND);
        String text = new String();
        text = text + note.getTitle() + "\n";
        text = text + note.getText() + "\n";
        Integer index = new Integer(0);
        if (note.getChecks() != null) {
            for (Check check : note.getChecks()) {
                index++;
                text = text + index.toString() + ". " + check.getText() + "\n";
            }
        }
        // Method to share either text
        if (note.getImages() == null) {
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
            share.putExtra(Intent.EXTRA_TEXT, text);
            //share.putExtra(Intent.EXTRA_TEXT, note.getTitle());
            //share.putExtra(Intent.EXTRA_TEXT, note.getText());
            // startActivity(Intent.createChooser(share,"Share Text!"));
            if (mintent != null) {
                Intent intent = new Intent();
                mintent.SendIntent(Intent.createChooser(share, "Share Text!"));
            }
        }
        // Method to share any image.
        if (note.getImages() != null) {
            // If you want to share a png image only, you can do:
            // setType("image/png"); OR for jpeg: setType("image/jpeg");
            share.setType("image/*");

            // Make sure you put example png image named myImage.png in your
            // directory
            // path temporary (duong dan tam thoi) khi nao Sync xong lam tiep
            String imagePath = Environment.getExternalStorageDirectory()
                    + "myImage.png";

            File imageFileToShare = new File(imagePath);

            Uri uri = Uri.fromFile(imageFileToShare);
            share.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.putExtra(Intent.EXTRA_TEXT, text);
            if (mintent != null) {
                Intent intent = new Intent();
                mintent.SendIntent(Intent.createChooser(share, "Share Image!"));
            }
        }
    }
}