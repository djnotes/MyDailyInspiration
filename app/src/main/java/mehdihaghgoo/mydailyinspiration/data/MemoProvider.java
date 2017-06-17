package mehdihaghgoo.mydailyinspiration.data;

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

import mehdihaghgoo.mydailyinspiration.data.MemoContract.MemoEntry;

/**
 * Created by john on 6/17/17.
 */

public class MemoProvider extends ContentProvider {
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
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        return null;
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
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String
            selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    private Uri insertMemo(Uri uri, ContentValues values) {
    SQLiteDatabase db=mDbHelper.getWritableDatabase();

        String memoNote=values.getAsString(MemoEntry.COLUMN_MEMO_NOTE);
        String memoTitle=values.getAsString(MemoEntry.COLUMN_MEMO_TITLE);
        String memoAuthor=values.getAsString(MemoEntry.COLUMN_MEMO_AUTHOR);


        if(TextUtils.isEmpty(memoNote)){
            throw new IllegalArgumentException("Memo requires a text");
        }

        long newRow=db.insert(MemoEntry.TABLE_NAME,null,values);
        if(newRow == -1){
            return null; //failure
        }

        //Return ID of the newly inserted data
        return ContentUris.withAppendedId(MemoEntry.CONTENT_URI,newRow);
    }
}
