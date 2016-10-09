package com.dut.note.bean;

import android.support.annotation.Nullable;

import com.blogspot.sontx.libex.DateTime;

import java.io.Serializable;
import java.util.List;

public class Note implements Serializable {
    private int _id = -1;
    private String mTitle = null;
    private String mText = null;
    private List<Check> mChecks = null;
    private List<String> mImages = null;
    private List<String> mSounds = null;
    private Alarm mAlarm = null;
    private List<String> mTags = null;
    private DateTime mCreated = null;

    public Note(String title, String text) {
        mTitle = title;
        mText = text;
        mCreated = DateTime.now();
    }

    public Note() {
        mCreated = DateTime.now();
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        if (mTitle != null && mTitle.trim().length() == 0)
            this.mTitle = null;
        else
            this.mTitle = mTitle;
    }

    @Nullable
    public List<Check> getChecks() {
        return mChecks;
    }

    public void setChecks(List<Check> mChecks) {
        if (mChecks != null && mChecks.size() == 0)
            this.mChecks = null;
        else
            this.mChecks = mChecks;
    }

    @Nullable
    public List<String> getTags() {
        return mTags;
    }

    public void setTags(List<String> mTags) {
        if (mTags != null && mTags.size() == 0)
            this.mTags = null;
        else
            this.mTags = mTags;
    }

    public DateTime getCreated() {
        return mCreated;
    }

    public void setCreated(DateTime mCreated) {
        this.mCreated = mCreated;
    }

    @Nullable
    public String getText() {
        return mText;
    }

    public void setText(String mSrcHtml) {
        if (mSrcHtml != null && mSrcHtml.trim().length() == 0)
            this.mText = null;
        else
            this.mText = mSrcHtml;
    }

    @Nullable
    public List<String> getImages() {
        return mImages;
    }

    public void setImages(List<String> mImages) {
        if (mImages != null && mImages.size() == 0)
            this.mImages = null;
        else
            this.mImages = mImages;
    }

    public List<String> getSounds() {
        return mSounds;
    }

    public void setSounds(List<String> mSounds) {
        this.mSounds = mSounds;
    }

    public Alarm getAlarm() {
        return mAlarm;
    }

    public void setAlarm(Alarm mAlarm) {
        this.mAlarm = mAlarm;
    }

    public boolean isValid() {
        if (mTitle == null || mTitle.length() == 0)
            return false;
        if (mText != null)
            return true;
        if (mChecks == null && mImages == null)
            return false;
        return true;
    }
}
