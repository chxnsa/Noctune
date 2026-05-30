package com.noctune.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "Noctune.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE =
            String.format(
                    "CREATE TABLE %s" +
                            " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " %s TEXT NOT NULL," +
                            " %s TEXT NOT NULL," +
                            " %s TEXT," +
                            " %s TEXT," +
                            " %s TEXT," +
                            " %s TEXT)",
                    DatabaseContract.TABLE_NAME,
                    DatabaseContract.FavColumns._ID,
                    DatabaseContract.FavColumns.TRACK_NAME,
                    DatabaseContract.FavColumns.ARTIST_NAME,
                    DatabaseContract.FavColumns.PLAYCOUNT,
                    DatabaseContract.FavColumns.LISTENERS,
                    DatabaseContract.FavColumns.DURATION,
                    DatabaseContract.FavColumns.IMAGE_URL
            );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        onCreate(db);
    }
}