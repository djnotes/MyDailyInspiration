package com.mehdi.memo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mehdi.memo.data.MemoContract.MemoEntry;
/**
 * Created by john on 6/17/17.
 */

public class MemoDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME="memo.db";
    public static final int DB_VERSION=1;

    public MemoDbHelper(Context context){
        super(context,DB_NAME,null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MEMO_TABLE="CREATE TABLE " + MemoEntry.TABLE_NAME
                + "("+ MemoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MemoEntry.COLUMN_MEMO_TITLE + " TEXT, "
                + MemoEntry.COLUMN_MEMO_AUTHOR + " TEXT, "
                + MemoEntry.COLUMN_MEMO_NOTE+ " TEXT, "
                + MemoEntry.COLUMN_MEMO_LAST_MODIFIED + " TEXT, "
                + MemoEntry.COLUMN_MEMO_PRIORITY+ " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_MEMO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
