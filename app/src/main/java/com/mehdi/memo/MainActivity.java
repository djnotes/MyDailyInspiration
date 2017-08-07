package com.mehdi.memo;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.FragmentTransaction;

import com.mehdi.memo.data.MemoContract.MemoEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final int LOADER_URL = 0;
    private static final String FRAGTAG = "AlarmFragment";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static ListView mMemoListView;
    MemoCursorAdapter mMemoAdapter;

    SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Sets fullscreen-related flags for the display
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        SharedPreferences shP=getSharedPreferences(getString(R.string.preferences_filename), 0);


        super.onCreate(savedInstanceState);
        setContentView(com.mehdi.memo.R.layout.activity_main);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        /* Set up the listview */
        mMemoListView = (ListView) findViewById(R.id.listview);

        //Set empty view
        View emptyView = findViewById(R.id.empty_view);
        mMemoListView.setEmptyView(emptyView);
        mMemoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //prepare a uri for the item clicked by the user on the list view
                Uri uri = ContentUris.withAppendedId(MemoEntry.CONTENT_URI, id);

                //Create an intent to start EditorActivity
                Intent editorIntent = new Intent(getApplicationContext(), EditorActivity.class);
                editorIntent.setData(uri);

                //Fire the intent
                startActivity(editorIntent);


            }
        });


        //Create a MemoCursorAdapter. set it to null because we have not yet queried the db
        mMemoAdapter = new MemoCursorAdapter(this, null);

        //set list's adapter
        mMemoListView.setAdapter(mMemoAdapter);

        //Fire the loader
        getLoaderManager().initLoader(LOADER_URL, null, this);

        //Add an AlarmFragment to the activity
        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            AlarmFragment fragment = new AlarmFragment();
            transaction.add(fragment , FRAGTAG);
            transaction.commit();
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MemoEntry._ID,
                MemoEntry.COLUMN_MEMO_TITLE,
                MemoEntry.COLUMN_MEMO_AUTHOR,
                MemoEntry.COLUMN_MEMO_NOTE,
                MemoEntry.COLUMN_MEMO_LAST_MODIFIED
        };

        switch (id) {
            case LOADER_URL:
                return new CursorLoader(getApplicationContext(),
                        MemoEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
            default:
                return null;
        }

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMemoAdapter.swapCursor(data); //Change the cursor providing our adapter's data

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMemoAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Open settings page (a new layout)
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            default:
                //Do nothing
        }

        return super.onOptionsItemSelected(item);
    }

    
}

