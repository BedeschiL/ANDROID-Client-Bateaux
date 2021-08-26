package com.example.application_bateau.Utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

public class UpdateEtat {
    public static void UpdateEtat(int color, TextView t, int delay, String text)
    {
        t.setText(text);
        t.setTextColor(color);

        if(delay!=0)
        {
            new CountDownTimer(delay, 1000) {
                public void onFinish() {
                    t.setText("");
                }
                public void onTick(long millisUntilFinished) {
                    // millisUntilFinished    The amount of time until finished.
                }
            }.start();
        }
    }
}
