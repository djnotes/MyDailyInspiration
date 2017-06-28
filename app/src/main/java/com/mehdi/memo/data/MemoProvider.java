package com.mehdi.memo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.mehdi.memo.data.MemoContract.MemoEntry;

/**
 * Created by john on 6/17/17.
 */

public class MemoProvider extends ContentProvider {
    private static final String LOG_TAG = MemoProvider.class.getSimpleName();
    private SQLiteOpenHelper mDbHelper;

    /* Uri matcher code for the content URI for the memo table
    */
    private static final int MEMO = 100;
    private static final int MEMO_ID = 101;
    /*
    UriMatcher object to match a content URI to a corresponding code
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //The calls to addURI() go here, for all the content URI patterns the provider
        //should recognize. All paths added to the UriMatcher have a corresponding code to return
        sUriMatcher.addURI(MemoContract.CONTENT_AUTHORITY, MemoContract.PATH_MEMO, MEMO);
        sUriMatcher.addURI(MemoContract.CONTENT_AUTHORITY, MemoContract.PATH_MEMO + "/#", MEMO_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new MemoDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MEMO:
                cursor = db.query(MemoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case MEMO_ID:
                //A specific row is requested, so we query the db with that id in selection criteria
                selection = MemoEntry._ID + "=?";
                long id = ContentUris.parseId(uri);
                selectionArgs = new String[]{String.valueOf(id)};
                cursor = db.query(MemoEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                cursor = null;

        }


        //Set notification URI on the cursor,
        //so that if the data at uri changes then we know we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {


        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMO:
                getContext().getContentResolver().notifyChange(uri, null);
                return insertMemo(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[]
            selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case MEMO:
                rowsDeleted = db.delete(MemoEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;

            case MEMO_ID:
                rowsDeleted = db.delete(MemoEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;

            default:
                throw new IllegalArgumentException("Invalid uri");
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String
            selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEMO:
                return updateMemo(uri, values, selection, selectionArgs);

            case MEMO_ID:
                //pass the id to the update method
                long id = ContentUris.parseId(uri);
                selection = MemoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(id)};
                return updateMemo(uri, values, selection, selectionArgs);
            default:
                //an invalid uri was passed
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateMemo(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Sanity-check the values
        if (values.containsKey(MemoEntry.COLUMN_MEMO_NOTE)) {
            if (values.getAsString(MemoEntry.COLUMN_MEMO_NOTE) == null)
                throw new IllegalArgumentException("Memo note is required");
        }
        if (values.containsKey(MemoEntry.COLUMN_MEMO_TITLE)) {
            if (values.getAsString(MemoEntry.COLUMN_MEMO_TITLE) == null)
                throw new IllegalArgumentException("Memo requires a title");
        }
        if (values.containsKey(MemoEntry.COLUMN_MEMO_PRIORITY)) {
            Integer priority = values.getAsInteger(MemoEntry.COLUMN_MEMO_PRIORITY);
            if (!(priority == MemoEntry.PRIORITY_HIGH ||
                    priority == MemoEntry.PRIORITY_MEDIUM ||
                    priority == MemoEntry.PRIORITY_LOW ||
                    priority == MemoEntry.PRIORITY_UNKNOWN)) {
                throw new IllegalArgumentException("Memo priority " + String.valueOf(priority) + " is not valid");
            }
        }


        //execute update on the database and return the number of rows updated
        int rowsUpdated = db.update(MemoEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;


    }


    private Uri insertMemo(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String memoNote = values.getAsString(MemoEntry.COLUMN_MEMO_NOTE);


        if (TextUtils.isEmpty(memoNote)) {
            throw new IllegalArgumentException("Memo requires a text");
        }

        long newRow = db.insert(MemoEntry.TABLE_NAME, null, values);

//        long newRow=db.execSQL("");
        //Return Uri of the newly inserted data
        return ContentUris.withAppendedId(uri, newRow);
    }
}
