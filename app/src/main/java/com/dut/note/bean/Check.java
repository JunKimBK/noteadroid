package com.dut.note.bean;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Check implements Serializable {
    private String mText;
    private boolean mChecked;

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mIsChecked) {
        this.mChecked = mIsChecked;
    }

    public static Check parse(String str){
        StringTokenizer tokenizer = new StringTokenizer(str, "</?>");
        Check check = new Check();
        if(!tokenizer.hasMoreTokens())
            return null;
        check.mText = tokenizer.nextToken();
        if(!tokenizer.hasMoreTokens())
            return null;
        check.mChecked = Integer.parseInt(tokenizer.nextToken()) != 0;
        return check;
    }

    @Override
    public String toString() {
        return mText.concat(mChecked ? "</?>1" : "</?>0");
    }
}
