package com.noctune.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicHelper {

    private static final String DATABASE_TABLE = DatabaseContract.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase database;
    private static volatile MusicHelper INSTANCE;

    private MusicHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public static MusicHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MusicHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
        if (database.isOpen()) {
            database.close();
        }
    }

    // Insert favorit baru
    public long insert(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    // Hapus favorit by id
    public int deleteById(String id) {
        return database.delete(DATABASE_TABLE,
                DatabaseContract.FavColumns._ID + " = ?",
                new String[]{id});
    }

    // Ambil semua favorit
    public Cursor queryAll() {
        return database.query(
                DATABASE_TABLE,
                null, null, null, null, null,
                DatabaseContract.FavColumns._ID + " ASC"
        );
    }

    // Cek apakah track sudah di favorit
    public boolean isFavorite(String trackName, String artistName) {
        Cursor cursor = database.query(
                DATABASE_TABLE,
                null,
                DatabaseContract.FavColumns.TRACK_NAME + " = ? AND " +
                        DatabaseContract.FavColumns.ARTIST_NAME + " = ?",
                new String[]{trackName, artistName},
                null, null, null
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}