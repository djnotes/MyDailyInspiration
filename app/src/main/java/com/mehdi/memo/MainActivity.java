package com.mehdi.memo;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mehdi.memo.data.MemoContract.MemoEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final int LOADER_URL = 0;
    private static final String FRAGTAG = "AlarmFragment";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static ListView mMemoListView;
    MemoCursorAdapter mMemoAdapter;

    //Define preference-related variables
    private boolean mNotiEnabled; //Is notification enabled
    private String mName; //Name displayed on the navigation header
    private String mMotto; //Motto displayed on the navigation header
    private boolean mNotificationSwitch; //Turn notification on or off
    private boolean mRandomSwitch; //Turn random notifications on or off
    private long mInterval; //Notification interval
    private SharedPreferences mPrefs; //App preferences

    private AlarmFragment mFragment; //This is the important fragment doing magic like alarm setting

    //Define a toolbar
    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;

    NotificationManagerCompat notiManager;
    private static boolean alarmSet=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Check active notifications on start up and remove all
        NotificationManager notifMgr =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notifMgr != null) {
            notifMgr.cancelAll();
        }

        // Sets fullscreen-related flags for the display
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        SharedPreferences shP=getSharedPreferences(getString(R.string.preferences_filename), 0);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        //Get reference for the toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Find drawerlayout and navigationview
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Define a new intent
                Intent intent;
                int id = menuItem.getItemId();
                switch(id){
                    case R.id.add_new:
                        intent = new Intent (getApplicationContext(), EditorActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_settings:
                        intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.about_app:
                        intent = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.more_online:
                        comingSoon();
                        break;
                    case R.id.share:
                        comingSoon();
                        break;

                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                menuItem.setChecked(false);
                return true; //"True to display the item as the selected item." - Google Docs
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
            mFragment= new AlarmFragment();
            transaction.add(mFragment, FRAGTAG);
            transaction.commit();
        }

        /****
        Preference handling
        Register the preference listener
        ***/
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefs.registerOnSharedPreferenceChangeListener(this);

//        mFragment = (AlarmFragment) getSupportFragmentManager().findFragmentByTag(FRAGTAG);
        if(!alarmSet){
            mFragment = (AlarmFragment) getSupportFragmentManager().findFragmentByTag(FRAGTAG);
            if (mFragment!=null) {
                mFragment.setupAlarm();
                alarmSet=true;
            }

        }


        //Set name and motto
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Get new strings for name and motto if any
        String name = mPrefs.getString(getString(R.string.key_pref_name) , null);
        String motto = mPrefs.getString(
                getString(R.string.key_pref_motto),
                null
        );
        View header= mNavigationView.getHeaderView(0);
        //Find name and motto text views
        TextView yourName = header.findViewById(R.id.tv_your_name);
        TextView yourMotto = header.findViewById(R.id.tv_your_motto);
        //Set name and motto text views
        if(yourName != null) yourName.setText(name);
        if (yourMotto != null) yourMotto.setText(motto);


    }



    private void comingSoon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle(R.string.title_message)
                .setMessage(R.string.coming_soon)
                .setIcon(R.drawable.icons8_future)
                .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alertDialog;
        builder.create().show();
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

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Get new strings for name and motto if any
        mName = mPrefs.getString(getString(R.string.key_pref_name) , null);
        mMotto = mPrefs.getString(
                getString(R.string.key_pref_motto),null
        );

        //Find name and motto text views
        TextView yourName = mDrawerLayout.findViewById(R.id.tv_your_name);
        TextView yourMotto = mDrawerLayout.findViewById(R.id.tv_your_motto);
        Log.i(LOG_TAG,yourName.getText().toString());
        Log.i(LOG_TAG,yourMotto.getText().toString());
        //Set name and motto text views
        yourName.setText(mName);
        yourMotto.setText(mMotto);
       mFragment = (AlarmFragment) getSupportFragmentManager().findFragmentByTag(FRAGTAG);
       mFragment.setupAlarm();

    }
}

