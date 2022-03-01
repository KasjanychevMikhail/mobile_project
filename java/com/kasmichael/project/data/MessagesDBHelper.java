package com.kasmichael.project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MessagesDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MessagesDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "messages.db";
    private static final int DATABASE_VERSION = 1;

    public MessagesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_MESSAGES_TABLE = "CREATE TABLE " + MessagesContract.MessagesTable.TABLE_NAME + " ("
                + MessagesContract.MessagesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MessagesContract.MessagesTable.COLUMN_TITLE + " STRING, "
                + MessagesContract.MessagesTable.COLUMN_TEXT + " STRING, "
                + MessagesContract.MessagesTable.COLUMN_TIME + " INTEGER DEFAULT 0);";
        sqLiteDatabase.execSQL(SQL_CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w("SQLite", "Обновление с версии " + i + " на версию " + i1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
    }
}
