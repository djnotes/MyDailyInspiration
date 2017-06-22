package com.mehdi.memo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mehdi.memo.data.MemoContract.MemoEntry;

/**
 * Created by john on 6/20/17.
 */

public class MemoCursorAdapter extends CursorAdapter {


    public MemoCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context c, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(c).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //find list_item views
        TextView title,author,note;
        title=(TextView) view.findViewById(R.id.tv_list_title);
        author=(TextView) view.findViewById(R.id.tv_list_author);
        note=(TextView) view.findViewById(R.id.tv_list_note);

        //get column indices
        int title_index=cursor.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_TITLE);
        int author_index=cursor.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_AUTHOR);
        int note_index=cursor.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_NOTE);

        //get column values
        String titleString=cursor.getString(title_index);
        String authorString=cursor.getString(author_index);
        String noteString=cursor.getString(note_index);


        //attach views to column values
        title.setText(titleString);
        author.setText(authorString);
        note.setText(noteString);
    }
}
