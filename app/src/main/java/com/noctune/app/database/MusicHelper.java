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

    // Buat playlist baru
    public long insertPlaylist(ContentValues values) {
        return database.insert(DatabaseContract.TABLE_PLAYLIST, null, values);
    }

    // Ambil semua playlist
    public Cursor queryAllPlaylists() {
        return database.query(
                DatabaseContract.TABLE_PLAYLIST,
                null, null, null, null, null,
                DatabaseContract.PlaylistColumns._ID + " ASC"
        );
    }

    // Hapus playlist by id
    public int deletePlaylist(String id) {
        // Hapus semua track dalam playlist dulu
        database.delete(
                DatabaseContract.TABLE_PLAYLIST_TRACKS,
                DatabaseContract.PlaylistTrackColumns.PLAYLIST_ID + " = ?",
                new String[]{id}
        );
        // Baru hapus playlist
        return database.delete(
                DatabaseContract.TABLE_PLAYLIST,
                DatabaseContract.PlaylistColumns._ID + " = ?",
                new String[]{id}
        );
    }

    // Tambah track ke playlist
    public long insertTrackToPlaylist(ContentValues values) {
        return database.insert(
                DatabaseContract.TABLE_PLAYLIST_TRACKS, null, values);
    }

    // Ambil semua track dalam playlist
    public Cursor queryTracksByPlaylist(String playlistId) {
        return database.query(
                DatabaseContract.TABLE_PLAYLIST_TRACKS,
                null,
                DatabaseContract.PlaylistTrackColumns.PLAYLIST_ID + " = ?",
                new String[]{playlistId},
                null, null,
                DatabaseContract.PlaylistTrackColumns._ID + " ASC"
        );
    }

    // Hapus track dari playlist
    public int deleteTrackFromPlaylist(String trackId) {
        return database.delete(
                DatabaseContract.TABLE_PLAYLIST_TRACKS,
                DatabaseContract.PlaylistTrackColumns._ID + " = ?",
                new String[]{trackId}
        );
    }

    // Hitung jumlah track dalam playlist
    public int countTracksInPlaylist(String playlistId) {
        Cursor cursor = database.query(
                DatabaseContract.TABLE_PLAYLIST_TRACKS,
                null,
                DatabaseContract.PlaylistTrackColumns.PLAYLIST_ID + " = ?",
                new String[]{playlistId},
                null, null, null
        );
        int count = cursor.getCount();
        cursor.close();
        return count;
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