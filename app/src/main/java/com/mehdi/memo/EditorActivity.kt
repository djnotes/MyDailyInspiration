package com.mehdi.memo

import android.app.LoaderManager
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import com.mehdi.memo.data.AppDb
import com.mehdi.memo.data.MemoContract.MemoEntry
import com.mehdi.memo.data.Note
import com.mehdi.memo.data.NoteDao
import java.text.DateFormat
import java.util.*

/**
 * Created by john on 6/17/17.
 */
class EditorActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var mAppDb: AppDb
    private lateinit var  mNoteDao: NoteDao

    //define user input fields
    private var mMemoNoteET: EditText? = null
    private var mMemoTitleET: EditText? = null
    private var mMemoAuthorET: EditText? = null
    private var mMemoPrioritySpinner //Here, priorities are shown to the user to choose from
            : Spinner? = null
    var mToolbar: Toolbar? = null

    /**
     Define mPriority. Valid values are in the MemoContract.java file
    */
    private var mMemoPriority = MemoEntry.PRIORITY_UNKNOWN
    var mCurrentMemoUri: Uri? = null
    private val mActivity: EditorActivity? = this
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        mAppDb = Room.databaseBuilder(applicationContext, AppDb::class.java, "mdi.db").build()
        mNoteDao = mAppDb.noteDao()

        //Setup action bar
        mToolbar = findViewById(R.id.toolbar_editor)
        setSupportActionBar(mToolbar)
        /*Initialize edit texts and the spinner*/
        mMemoNoteET = findViewById<View>(R.id.text_memo_note) as EditText
        mMemoTitleET = findViewById<View>(R.id.text_memo_title) as EditText
        mMemoAuthorET = findViewById<View>(R.id.text_memo_author) as EditText
        //Set up spinner
        setupSpinner()
        //get intent
        val intent = intent
        mCurrentMemoUri = intent.data //Get the incoming intent
//        mActivity = this@EditorActivity
        mActivity!!.setTitle(R.string.adding_new_memo)
        //if in edit mode
//set title to editing existing memo and
//fire the cursor loader
        if (mCurrentMemoUri != null) {
            mActivity.setTitle(R.string.editing_existing_memo)
            loaderManager.initLoader(EXISTING_MEMO_LOADER_URL, null, this)
        }
    }

    private fun setupSpinner() { // Define array adapter
        val prioritySpinnerAdapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(this,
                R.array.memo_priority_values,
                R.layout.support_simple_spinner_dropdown_item)
        // set the dropdown style
        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        //find the spinner view
        mMemoPrioritySpinner = findViewById<View>(R.id.spinner_priority) as Spinner
        //apply the adapter to the spinner
        mMemoPrioritySpinner!!.adapter = prioritySpinnerAdapter
        //set onClickListener and then identify the selected value
        mMemoPrioritySpinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selection = parent.getItemAtPosition(position) as String
                if (!TextUtils.isEmpty(selection)) {
                    mMemoPriority = if (selection == getString(R.string.priority_high)) {
                        MemoEntry.PRIORITY_HIGH
                    } else if (selection == getString(R.string.priority_medium)) {
                        MemoEntry.PRIORITY_MEDIUM
                    } else if (selection == getString(R.string.priority_low)) {
                        MemoEntry.PRIORITY_LOW
                    } else {
                        MemoEntry.PRIORITY_UNKNOWN
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                mMemoPriority = MemoEntry.PRIORITY_UNKNOWN
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //Create an alert dialog builder
        val builder = AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog)
        builder.setIcon(R.drawable.alert)
        builder.setPositiveButton(R.string.delete) { dialog, which ->
            //create selection
            val selection = MemoEntry._ID + "=?"
            val selectionArgs = arrayOf(mCurrentMemoUri?.let { ContentUris.parseId(it).toString() })
            val rowsDeleted = contentResolver.delete(mCurrentMemoUri!!, selection, selectionArgs)
            if (rowsDeleted > 0) {
                Toast.makeText(applicationContext, R.string.delete_successful, Toast.LENGTH_SHORT).show()
                clearInputs()
            } else {
                Toast.makeText(applicationContext, R.string.delete_failed, Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })
        builder.setMessage(R.string.are_you_sure)
        //Create the alert dialog
        val dialog = builder.create()
        when (item.itemId) {
            R.id.action_save -> {
                if (!controlInputs()) {
                    Toast.makeText(applicationContext, R.string.please_check_input, Toast.LENGTH_SHORT).show()
                }
                //Check to see if in insert or edit mode.
//Insert if in insert mode, update otherwise.
                if (mCurrentMemoUri == null) {
                    saveNote()
                    return true
                } else {
                    updateMemo()
                }
            }
            R.id.action_delete -> {
                //we only delete in edit mode, so check uri
                requireNotNull(mCurrentMemoUri) { "Invalid mode" }
                //Show alert to confirm delete
                dialog.show()
            }
        }
        return true
    }

    private fun clearInputs() {
        mMemoNoteET!!.text = null
        mMemoAuthorET!!.text = null
        mMemoTitleET!!.text = null
        mMemoPrioritySpinner!!.setSelection(MemoEntry.PRIORITY_UNKNOWN)
    }

    private fun controlInputs(): Boolean {
        return !mMemoNoteET!!.text.toString().isEmpty()
    }

    private fun updateMemo() {
        val note = mMemoNoteET!!.text.toString()
        val title = mMemoTitleET!!.text.toString()
        val author = mMemoAuthorET!!.text.toString()
        //Use a ContentValues object to update the table
        val values = ContentValues()
        values.put(MemoEntry.COLUMN_MEMO_NOTE, note)
        values.put(MemoEntry.COLUMN_MEMO_TITLE, title)
        values.put(MemoEntry.COLUMN_MEMO_AUTHOR, author)
        values.put(MemoEntry.COLUMN_MEMO_PRIORITY, mMemoPriority)
        val rowsUpdated = contentResolver.update(mCurrentMemoUri!!, values, null, null)
        if (rowsUpdated > 0) {
            clearInputs()
            Toast.makeText(applicationContext, R.string.update_successful, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, R.string.update_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNote() {

        val values = ContentValues()
        /*
        * Get User input
        * */
        val noteText = mMemoNoteET!!.text.toString()
        val noteTitle = mMemoTitleET!!.text.toString()
        val noteAuthor = mMemoAuthorET!!.text.toString()
        //Get system date
//format date with long style and time with short formatting style i.e. hh:mm.
//Use US locale
//This is Java code not Android. Notice the java.text.DateFormat import
        val now = Calendar.getInstance().time
        val formattedDateTime = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.SHORT,
                Locale.US).format(now)
        /*
        * Add ContentValue items
        * */values.put(MemoEntry.COLUMN_MEMO_NOTE, noteText)
        values.put(MemoEntry.COLUMN_MEMO_TITLE, noteTitle)
        values.put(MemoEntry.COLUMN_MEMO_AUTHOR, noteAuthor)
        values.put(MemoEntry.COLUMN_MEMO_PRIORITY, mMemoPriority)
        values.put(MemoEntry.COLUMN_MEMO_LAST_MODIFIED, formattedDateTime)
        val insertResult = contentResolver.insert(MemoEntry.CONTENT_URI, values)

        if (insertResult == null) {
            Toast.makeText(applicationContext, R.string.insert_failed, Toast.LENGTH_SHORT).show()
        } else {
            clearInputs()
            Toast.makeText(applicationContext, R.string.save_successful, Toast.LENGTH_SHORT).show()
        }

        Runnable {
            mNoteDao.insertAll(Note(0, noteTitle, noteText, noteAuthor))

        }.run()
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor> { //Define a projection to query
        val projection = arrayOf(
                MemoEntry._ID,
                MemoEntry.COLUMN_MEMO_NOTE,
                MemoEntry.COLUMN_MEMO_TITLE,
                MemoEntry.COLUMN_MEMO_AUTHOR,
                MemoEntry.COLUMN_MEMO_PRIORITY
        )
        return when (id) {
            EXISTING_MEMO_LOADER_URL -> CursorLoader(applicationContext,
                    mCurrentMemoUri,
                    projection,
                    null,
                    null,
                    null)
            else ->  //Invalid loader id
                Loader<Cursor>(this)
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) { //Important: move cursor to index 0 before doing anything
        data.moveToFirst()
        if (data.count == 0) {
            return
        } //we do not use an empty cursor
        //get column indexes for columns we are interested in
        val noteIndex = data.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_NOTE)
        val titleIndex = data.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_TITLE)
        val authorIndex = data.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_AUTHOR)
        val priorityIndex = data.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_PRIORITY)
        //set view values from the loader
        val noteText = data.getString(noteIndex)
        val titleText = data.getString(titleIndex)
        val authorText = data.getString(authorIndex)
        val priority = data.getInt(priorityIndex)
        //Set view values from local variables
        mMemoNoteET!!.setText(noteText)
        mMemoTitleET!!.setText(titleText)
        mMemoAuthorET!!.setText(authorText)
        mMemoPrioritySpinner!!.setSelection(priority)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) { //Reset fields
        mMemoNoteET!!.text = null
        mMemoTitleET!!.text = null
        mMemoAuthorET!!.text = null
        mMemoPrioritySpinner!!.setSelection(MemoEntry.PRIORITY_UNKNOWN)
    }

    companion object {
        private val LOG_TAG = EditorActivity::class.java.simpleName
        private const val EXISTING_MEMO_LOADER_URL = 0 //identifies the loader used inside this component
    }
}