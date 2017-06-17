package mehdihaghgoo.mydailyinspiration.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import mehdihaghgoo.mydailyinspiration.data.MemoContract.MemoEntry;
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
                + MemoEntry.AUTHOR + " TEXT, "
                + MemoEntry.NOTE + " TEXT, "
                + MemoEntry.DATE + " TEXT, "
                + MemoEntry.PRIORITY + "INTEGER NOT NULL DEFAULT 1);";
        db.execSQL(SQL_CREATE_MEMO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
