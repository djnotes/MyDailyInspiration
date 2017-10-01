package com.mehdi.memo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by johndoe on 8/5/17.
 */

public class DisplayActivity extends AppCompatActivity{
    String mTitle, mMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //Get intent and data
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(Const.EXTRA_DIALOG_TITLE);
        mMessage = intent.getStringExtra(Const.EXTRA_DIALOG_MESSAGE);

        //Find view references to set values for
        TextView tvTitle = findViewById(R.id.display_title);
        TextView tvFullText = findViewById(R.id.display_fulltext);

        //Set values
        tvTitle.setText(mTitle);
        tvFullText.setText(mMessage);
    }
}
