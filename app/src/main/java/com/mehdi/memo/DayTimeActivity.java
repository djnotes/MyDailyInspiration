package com.mehdi.memo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by john on 7/13/17.
 */

public class DayTimeActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(com.mehdi.memo.R.layout.activity_day_time);
        this.setTitle("Set Time and Day");

    }
}
