package com.dut.note.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dut.note.R;
import com.dut.note.lib.ViewUtils;

public class SquareLinearLayout extends RelativeLayout implements View.OnTouchListener {
    private ImageView mImageView = null;
    private int mBackgroundColor;
    private int mBackgroundPressedColor;
    private int mImageFilterColor;

    public SquareLinearLayout(Context context) {
        super(context);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initialize() {
        ViewUtils.hookEvent(this, new ViewUtils.IFunc() {
            @Override
            public void forView(View view) {
                view.setOnTouchListener(SquareLinearLayout.this);
                if(view instanceof ImageView)
                    mImageView = (ImageView) view;
            }
        });
        setClickable(true);
        setFocusable(true);
        mBackgroundColor = getResources().getColor(R.color.tile_title_background);
        mBackgroundPressedColor = getResources().getColor(R.color.tile_title_background_pressed);
        mImageFilterColor = (mBackgroundPressedColor & 0x00ffffff) | 0x64000000;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initialize();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (mImageView != null) {
                    mImageView.getDrawable().setColorFilter(mImageFilterColor, PorterDuff.Mode.SRC_ATOP);
                    mImageView.invalidate();
                }
                setBackgroundColor(mBackgroundPressedColor);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mImageView != null) {
                    mImageView.getDrawable().clearColorFilter();
                    mImageView.invalidate();
                }
                setBackgroundColor(mBackgroundColor);
                break;
            }
        }
        return false;
    }
}
