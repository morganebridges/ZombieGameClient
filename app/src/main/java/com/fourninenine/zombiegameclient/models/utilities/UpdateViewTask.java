package com.fourninenine.zombiegameclient.models.utilities;

import android.view.View;
import android.widget.TextView;

import java.util.TimerTask;



class UpdateTimeTask extends TimerTask {
    public static View mView;
    public static String newText;
    public void run()
    {
        ((TextView)(mView)).setText(newText);
    }
    public class UpdateViewTask {

        UpdateViewTask(View view){
            mView = view;
        }
    }
}