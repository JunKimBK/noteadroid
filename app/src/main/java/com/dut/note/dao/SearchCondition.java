package com.dut.note.dao;

import com.blogspot.sontx.libex.DateTime;
import com.dut.note.bean.Alarm;
import com.dut.note.bean.Check;

import java.util.List;

/**
 * Created by TrongNV on 9/12/2015.
 */
public class SearchCondition {
    private int _id = -1;
    private String mTitle;
    private String mText;
    private String mChecks;
    private String mImages;
    private String mSounds;
    private String mAlarm;
    private String mTags;
    private String mCreated;

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
        this.mTitle = mTitle;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getChecks() {
        return mChecks;
    }

    public void setChecks(List<Check> mChecks) {
        this.mChecks = mChecks.toString();
    }

    public String getImages() {
        return mImages;
    }

    public void setImages(List<String> mImages) {
        this.mImages = mImages.toString();
    }

    public String getSounds() {
        return mSounds;
    }

    public void setSounds(List<String> mSounds) {
        this.mSounds = mSounds.toString();
    }

    public String getAlarm() {
        return mAlarm;
    }

    public void setAlarm(Alarm mAlarm) {
        this.mAlarm = mAlarm.toString();
    }

    public String getTags() {
        return mTags;
    }

    public void setTags(List<String> mTags) {
        this.mTags = mTags.toString();
    }

    public String getCreated() {
        return mCreated;
    }

    public void setCreated(DateTime mCreated) {
        this.mCreated = mCreated.toString();
    }

    public boolean isNoCondition() {
        if (_id != -1 || mTitle != null || mText != null || mChecks != null ||
                mImages != null || mSounds != null || mAlarm != null || mTags != null ||
                mCreated != null) {
            return false;
        } else {
            return true;
        }
    }
}
