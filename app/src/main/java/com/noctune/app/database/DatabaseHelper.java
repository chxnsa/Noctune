package com.noctune.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "Noctune.db";
    private static final int DATABASE_VERSION = 2; // naikan versi!

    // Tabel favorites
    private static final String SQL_CREATE_FAVORITES =
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

    // Tabel playlists
    private static final String SQL_CREATE_PLAYLISTS =
            String.format(
                    "CREATE TABLE %s" +
                            " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " %s TEXT NOT NULL," +
                            " %s TEXT," + // <-- Menyimpan CREATED_AT
                            " %s TEXT)",  // <-- Menyimpan PLAYLIST_COVER
                    DatabaseContract.TABLE_PLAYLIST,
                    DatabaseContract.PlaylistColumns._ID,
                    DatabaseContract.PlaylistColumns.PLAYLIST_NAME,
                    DatabaseContract.PlaylistColumns.CREATED_AT,
                    DatabaseContract.PlaylistColumns.PLAYLIST_COVER
            );

    // Tabel playlist_tracks
    private static final String SQL_CREATE_PLAYLIST_TRACKS =
            String.format(
                    "CREATE TABLE %s" +
                            " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " %s INTEGER NOT NULL," +
                            " %s TEXT NOT NULL," +
                            " %s TEXT NOT NULL," +
                            " %s TEXT," +
                            " %s TEXT," +
                            " %s TEXT," +  // <-- TAMBAH %s UNTUK PLAYCOUNT
                            " %s TEXT)",   // <-- TAMBAH %s UNTUK LISTENERS
                    DatabaseContract.TABLE_PLAYLIST_TRACKS,
                    DatabaseContract.PlaylistTrackColumns._ID,
                    DatabaseContract.PlaylistTrackColumns.PLAYLIST_ID,
                    DatabaseContract.PlaylistTrackColumns.TRACK_NAME,
                    DatabaseContract.PlaylistTrackColumns.ARTIST_NAME,
                    DatabaseContract.PlaylistTrackColumns.DURATION,
                    DatabaseContract.PlaylistTrackColumns.IMAGE_URL,
                    // --- MASUKKAN DUA KOLOM BARU DI SINI ---
                    DatabaseContract.PlaylistTrackColumns.PLAYCOUNT,
                    DatabaseContract.PlaylistTrackColumns.LISTENERS
            );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAVORITES);
        db.execSQL(SQL_CREATE_PLAYLISTS);
        db.execSQL(SQL_CREATE_PLAYLIST_TRACKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_PLAYLIST_TRACKS);
        onCreate(db);
    }
}