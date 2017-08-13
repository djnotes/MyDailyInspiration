package com.mehdi.memo;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import java.text.DateFormat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mehdi.memo.data.MemoContract.MemoEntry;
import com.mehdi.memo.data.MemoDbHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by john on 6/17/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int EXISTING_MEMO_LOADER_URL = 0; //identifies the loader used inside this component
    //define user input fields
    private EditText mMemoNoteET;
    private EditText mMemoTitleET;
    private EditText mMemoAuthorET;
    private Spinner mMemoPrioritySpinner; //Here, priorities are shown to the user to choose from
    Toolbar mToolbar;



    /*
     Define mPriority. Valid values are in the MemoContract.java file
    **/
    private int mMemoPriority = MemoEntry.PRIORITY_UNKNOWN;
    public Uri mCurrentMemoUri;
    private Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Setup action bar
        mToolbar = findViewById(R.id.toolbar_editor);
        setSupportActionBar(mToolbar);

        /*Initialize edit texts and the spinner*/
        mMemoNoteET = (EditText) findViewById(R.id.text_memo_note);
        mMemoTitleET = (EditText) findViewById(R.id.text_memo_title);
        mMemoAuthorET = (EditText) findViewById(R.id.text_memo_author);
        //Set up spinner
        setupSpinner();

        //get intent
        Intent intent = getIntent();
        mCurrentMemoUri = intent.getData();//Get the incoming intent

        mActivity = EditorActivity.this;
        mActivity.setTitle(R.string.adding_new_memo);

        //if in edit mode
        //set title to editing existing memo and
        //fire the cursor loader

        if (mCurrentMemoUri != null) {
            mActivity.setTitle(R.string.editing_existing_memo);
            getLoaderManager().initLoader(EXISTING_MEMO_LOADER_URL, null, this);
        }


    }

    private void setupSpinner() {
        // Define array adapter
        ArrayAdapter prioritySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.memo_priority_values,
                R.layout.support_simple_spinner_dropdown_item);

        // set the dropdown style
        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //find the spinner view
        mMemoPrioritySpinner = (Spinner) findViewById(R.id.spinner_priority);
        //apply the adapter to the spinner
        mMemoPrioritySpinner.setAdapter(prioritySpinnerAdapter);

        //set onClickListener and then identify the selected value
        mMemoPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.priority_high))) {
                        mMemoPriority = MemoEntry.PRIORITY_HIGH;
                    } else if (selection.equals(getString(R.string.priority_medium))) {
                        mMemoPriority = MemoEntry.PRIORITY_MEDIUM;
                    } else if (selection.equals(getString(R.string.priority_low))) {
                        mMemoPriority = MemoEntry.PRIORITY_LOW;
                    } else {
                        mMemoPriority = MemoEntry.PRIORITY_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMemoPriority = MemoEntry.PRIORITY_UNKNOWN;

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Create an alert dialog builder
        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.Theme_AppCompat_Dialog);
        builder.setIcon(R.drawable.alert);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //create selection
                String selection = MemoEntry._ID + "=?";
                String [] selectionArgs= new String[] {
                        String.valueOf(ContentUris.parseId(mCurrentMemoUri))
                };
                int rowsDeleted=getContentResolver().delete(mCurrentMemoUri,selection,selectionArgs);
                if(rowsDeleted >0 ) {
                    Toast.makeText(getApplicationContext(),R.string.delete_successful,Toast.LENGTH_SHORT).show();
                    clearInputs();
                }
                else {
                    Toast.makeText(getApplicationContext(),R.string.delete_failed,Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setMessage(R.string.are_you_sure);

        //Create the alert dialog
        AlertDialog dialog=builder.create();

        switch (item.getItemId()) {
            case R.id.action_save:
                if (!controlInputs()) {
                    Toast.makeText(getApplicationContext(), R.string.please_check_input, Toast.LENGTH_SHORT).show();
                }
                //Check to see if in insert or edit mode.
                //Insert if in insert mode, update otherwise.
                if (mCurrentMemoUri == null) {
                    saveMemo();
                    return true;
                } else {
                    updateMemo();
                }


                break;
            case R.id.action_delete:
                //we only delete in edit mode, so check uri
                if(mCurrentMemoUri == null) {
                    throw new IllegalArgumentException("Invalid mode");

                }
                //Show alert to confirm delete

                dialog.show();
                                break;
        }
        return true;
    }

    private void clearInputs() {
        mMemoNoteET.setText(null);
        mMemoAuthorET.setText(null);
        mMemoTitleET.setText(null);
        mMemoPrioritySpinner.setSelection(MemoEntry.PRIORITY_UNKNOWN);
    }

    private boolean controlInputs() {
        if (mMemoNoteET.getText().toString() == null) return false;
        return true;
    }

    private void updateMemo() {

        String note = mMemoNoteET.getText().toString();
        String title = mMemoTitleET.getText().toString();
        String author = mMemoAuthorET.getText().toString();

        //Use a ContentValues object to update the table
        ContentValues values = new ContentValues();
        values.put(MemoEntry.COLUMN_MEMO_NOTE, note);
        values.put(MemoEntry.COLUMN_MEMO_TITLE, title);
        values.put(MemoEntry.COLUMN_MEMO_AUTHOR, author);
        values.put(MemoEntry.   COLUMN_MEMO_PRIORITY, mMemoPriority);

        int rowsUpdated=getContentResolver().update(mCurrentMemoUri, values, null, null);
        if(rowsUpdated >0 ) {
            clearInputs();
            Toast.makeText(getApplicationContext(),R.string.update_successful,Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),R.string.update_failed,Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMemo() {
        ContentValues values = new ContentValues();



        /*
        * Get User input
        * */
        String memoNote = mMemoNoteET.getText().toString();
        String memoTitle = mMemoTitleET.getText().toString();
        String memoAuthor = mMemoAuthorET.getText().toString();
        //Get system date
        //format date with long style and time with short formatting style i.e. hh:mm.
        //Use US locale
        //This is Java code not Android. Notice the java.text.DateFormat import
        Date now = Calendar.getInstance().getTime();
        String formattedDateTime = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.SHORT,
                Locale.US).format(now);



        /*
        * Add ContentValue items
        * */
        values.put(MemoEntry.COLUMN_MEMO_NOTE, memoNote);
        values.put(MemoEntry.COLUMN_MEMO_TITLE, memoTitle);
        values.put(MemoEntry.COLUMN_MEMO_AUTHOR, memoAuthor);
        values.put(MemoEntry.COLUMN_MEMO_PRIORITY, mMemoPriority);
        values.put(MemoEntry.COLUMN_MEMO_LAST_MODIFIED, formattedDateTime);


        Uri insertResult = getContentResolver().insert(MemoEntry.CONTENT_URI, values);
        if (insertResult == null) {
            Toast.makeText(getApplicationContext(), R.string.insert_failed, Toast.LENGTH_SHORT).show();
        } else {
            clearInputs();
            Toast.makeText(getApplicationContext(), R.string.save_successful, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Define a projection to query
        String[] projection = {
                MemoEntry._ID,
                MemoEntry.COLUMN_MEMO_NOTE,
                MemoEntry.COLUMN_MEMO_TITLE,
                MemoEntry.COLUMN_MEMO_AUTHOR,
                MemoEntry.COLUMN_MEMO_PRIORITY
        };

        //decide based on the id of the loader
        switch (id) {
            case EXISTING_MEMO_LOADER_URL:
                return new CursorLoader(getApplicationContext(),
                        mCurrentMemoUri,
                        projection,
                        null,
                        null,
                        null);
            default:
                //Invalid loader id
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Important: move cursor to index 0 before doing anything
        data.moveToFirst();
        if(data.getCount() == 0) { return;} //we do not use an empty cursor

        //get column indexes for columns we are interested in
        int noteIndex = data.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_NOTE);
        int titleIndex = data.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_TITLE);
        int authorIndex = data.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_AUTHOR);
        int priorityIndex = data.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_PRIORITY);

        //set view values from the loader
        String noteText = data.getString(noteIndex);
        String titleText = data.getString(titleIndex);
        String authorText = data.getString(authorIndex);
        int priority = data.getInt(priorityIndex);

        //Set view values from local variables
        mMemoNoteET.setText(noteText);
        mMemoTitleET.setText(titleText);
        mMemoAuthorET.setText(authorText);
        mMemoPrioritySpinner.setSelection(priority);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Reset fields
        mMemoNoteET.setText(null);
        mMemoTitleET.setText(null);
        mMemoAuthorET.setText(null);
        mMemoPrioritySpinner.setSelection(MemoEntry.PRIORITY_UNKNOWN);

    }
}
