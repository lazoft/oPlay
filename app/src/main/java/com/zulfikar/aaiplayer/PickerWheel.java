package com.zulfikar.aaiplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class PickerWheel extends NumberPicker {
    public PickerWheel(Context context) {
        super(context);
    }

    public PickerWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        processXmlAttributes(attrs, 0, 0);
    }

    public PickerWheel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processXmlAttributes(attrs, defStyleAttr, 0);
    }

    public PickerWheel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        processXmlAttributes(attrs, defStyleAttr, defStyleRes);
    }

    private void processXmlAttributes(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PickerWheel, defStyleAttr, defStyleRes);

        setMinValue(attributes.getInt(R.styleable.PickerWheel_minVal, 0));
        setMaxValue(attributes.getInt(R.styleable.PickerWheel_maxVal, 0));
        setValue(attributes.getInt(R.styleable.PickerWheel_defaultValue, 0));

        attributes.recycle();
    }
}
