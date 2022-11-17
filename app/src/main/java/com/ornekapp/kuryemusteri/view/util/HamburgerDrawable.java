package com.ornekapp.kuryemusteri.view.util;

import android.content.Context;
import android.graphics.Canvas;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

import com.ornekapp.kuryemusteri.R;

public class HamburgerDrawable extends DrawerArrowDrawable {

    public HamburgerDrawable(Context context){
        super(context);
        setColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        setBarLength(74.0f);
        setBarThickness(8.0f);
        setGapSize(10.0f);
    }
}