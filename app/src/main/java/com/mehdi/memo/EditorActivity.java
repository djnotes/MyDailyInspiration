package com.mehdi.memo;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mehdi.memo.data.MemoContract;
import com.mehdi.memo.data.MemoContract.MemoEntry;


/**
 * Created by john on 6/17/17.
 */

public class EditorActivity extends AppCompatActivity{


    public static final String LOG_TAG = EditorActivity.class.getSimpleName();
    //define user input fields
    private EditText mMemoNoteET;
    private EditText mMemoTitleET;
    private EditText mMemoAuthorET;
    private EditText mMemoDateET;
    private Spinner mMemoPrioritySpinner; //Here, priorities are shown to the user to choose from

    /*
     Define mPriority. Valid values are in the MemoContract.java file
    **/
    private int mMemoPriority=MemoEntry.PRIORITY_UNKNOWN;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState   ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        /*Initialize edit texts and the spinner*/
        mMemoNoteET=(EditText) findViewById(R.id.text_memo_note);
        mMemoTitleET=(EditText) findViewById(R.id.text_memo_title);
        mMemoAuthorET=(EditText) findViewById(R.id.text_author);
        //Set up spinner
        setupSpinner();


    }

    private void setupSpinner(){
        // Define array adapter
        ArrayAdapter prioritySpinnerAdapter=ArrayAdapter.createFromResource(this,
                R.array.memo_priority_values,
                R.layout.support_simple_spinner_dropdown_item);

        // set the dropdown style
        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //find the spinner view
        mMemoPrioritySpinner=(Spinner) findViewById(R.id.spinner_priority);
        //apply the adapter to the spinner
        mMemoPrioritySpinner.setAdapter(prioritySpinnerAdapter);

        //set onClickListener and then identify the selected value
        mMemoPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.priority_high))) {
                        mMemoPriority=MemoEntry.PRIORITY_HIGH;
                    } else if (selection.equals(getString(R.string.priority_medium))) {
                        mMemoPriority=MemoEntry.PRIORITY_MEDIUM;
                    } else if (selection.equals(getString(R.string.priority_low))){
                        mMemoPriority=MemoEntry.PRIORITY_LOW;
                    }
                    else {
                        mMemoPriority=MemoEntry.PRIORITY_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMemoPriority=MemoEntry.PRIORITY_UNKNOWN;

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                /* Save memo
                 */


                saveMemo();

                break;
            case R.id.action_delete:
                //To do: delete item
                break;
        }
        return true;
    }

    private void saveMemo() {
        ContentValues values=new ContentValues();


        if(!controlInput()) {
            Toast.makeText(getApplicationContext(), R.string.please_check_input,Toast.LENGTH_SHORT).show();
            return;
        }

        /*
        * Get User input
        * */
        String memoNote=mMemoNoteET.getText().toString();
        String memoTitle=mMemoTitleET.getText().toString();
        String memoAuthor=mMemoAuthorET.getText().toString();



        /*
        * Add ContentValue items
        * */
        values.put(MemoEntry.COLUMN_MEMO_NOTE,memoNote);
        values.put(MemoEntry.COLUMN_MEMO_TITLE, memoTitle);
        values.put(MemoEntry.COLUMN_MEMO_AUTHOR,memoAuthor);
        values.put(MemoEntry.COLUMN_MEMO_PRIORITY,mMemoPriority);


        Uri insertResult=getContentResolver().insert(MemoEntry.CONTENT_URI,values);
        if(insertResult==null){
            Toast.makeText(getApplicationContext(),R.string.insert_failed,Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),R.string.save_successful,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean controlInput() {
        return true;
    }
}
