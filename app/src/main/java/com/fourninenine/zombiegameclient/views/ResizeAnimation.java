package com.fourninenine.zombiegameclient.views;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.fourninenine.zombiegameclient.models.utilities.Globals;

/**
 * Created by morganebridges on 7/20/16.
 */
public class ResizeAnimation extends Animation {
    final int closedHeight;
    View view;
    int openHeight;

    public boolean isCollapsed() {
        return isCollapsed;
    }

    boolean isCollapsed;

    public ResizeAnimation(View view, int openHeight, int closedHeight, boolean startCollapsed) {
        this.view = view;
        this.closedHeight = closedHeight;
        this.openHeight = openHeight;
        this.isCollapsed = startCollapsed;

    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        int newHeight = 0;
        if(view.getHeight() == openHeight)
            newHeight = closedHeight;
        else newHeight = openHeight;

        Log.d("ResizeAnimation", "applyTransformation");
        view.getLayoutParams().height = newHeight;
        view.requestLayout();

    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

