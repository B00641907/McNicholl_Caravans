package com.example.b00641907.mcnicholl_caravans.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class OpenSansButton extends android.support.v7.widget.AppCompatButton {

    public OpenSansButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenSansButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenSansButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
            setTypeface(tf);
        }
    }

}