package com.zulfikar.aaiplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

public class ImageViewButton extends androidx.appcompat.widget.AppCompatImageView {

    public ImageViewButton(Context context) {
        super(context);
    }

    public ImageViewButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) performClick();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
