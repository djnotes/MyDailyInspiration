package com.mehdi.memo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by john on 6/17/17.
 */

public final class MemoContract {
    //defines constant for insert and edit modes

    //To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor
    private MemoContract() {
    }

    //Define content authority
    public static final String CONTENT_AUTHORITY="com.mehdi.memo";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String PATH_MEMO="memo";

    //Inner class that defines constants for the memo database
    public static final class MemoEntry implements BaseColumns {

        //content Uri
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_MEMO);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEMO;




        /* Name of the table */
        public static final String TABLE_NAME = "memo";
        /* unique ID of the memo
         * Type: INTEGER
          * */
        public static final String _ID = BaseColumns._ID;
        /*
        name of the author of note
        Type: TEXT
         */
        public static final String COLUMN_MEMO_TITLE= "title";
        public static final String COLUMN_MEMO_AUTHOR = "author";
        /*
        Date of saving or updating the note
        Type: TEXT
        Format "YYYY-MM-DD HH:MM:SS.SSS"
         */
        public static final String COLUMN_MEMO_LAST_MODIFIED="last_modified";
        /*
        Note content
        Type: TEXT
         */
        public static final String COLUMN_MEMO_NOTE = "note";

        public static final String COLUMN_MEMO_PRIORITY = "priority";



        /* Possible values for priority */
        public static final int PRIORITY_HIGH=0;
        public static final int PRIORITY_MEDIUM=1;
        public static final int PRIORITY_LOW=2;
        public static final int PRIORITY_UNKNOWN = 3;

    }
}
