package com.noctune.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
            synchronized (MusicHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MusicHelper(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public synchronized void open() throws SQLException {
        if (database == null || !database.isOpen()) {
            database = databaseHelper.getWritableDatabase();
        }
    }

    public void close() {
        // Jangan menutup database secara eksplisit di sini karena MusicHelper adalah Singleton.
        // Menutupnya akan menyebabkan crash (IllegalStateException) pada thread latar belakang
        // saat terjadi perubahan konfigurasi (seperti ganti mode Dark/Light).
    }

    // Insert favorit baru
    public long insert(ContentValues values) {
        open();
        return database.insert(DATABASE_TABLE, null, values);
    }

    // Hapus favorit by id
    public int deleteById(String id) {
        open();
        return database.delete(DATABASE_TABLE,
                DatabaseContract.FavColumns._ID + " = ?",
                new String[]{id});
    }

    // Buat playlist baru
    public long insertPlaylist(ContentValues values) {
        open();
        return database.insert(DatabaseContract.TABLE_PLAYLIST, null, values);
    }

    // Ambil semua playlist
    public Cursor queryAllPlaylists() {
        open();
        return database.query(
                DatabaseContract.TABLE_PLAYLIST,
                null, null, null, null, null,
                DatabaseContract.PlaylistColumns._ID + " ASC"
        );
    }

    // Hapus playlist by id
    public int deletePlaylist(String id) {
        open();
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
        open();
        return database.insert(
                DatabaseContract.TABLE_PLAYLIST_TRACKS, null, values);
    }

    // Ambil semua track dalam playlist
    public Cursor queryTracksByPlaylist(String playlistId) {
        open();
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
        open();
        return database.delete(
                DatabaseContract.TABLE_PLAYLIST_TRACKS,
                DatabaseContract.PlaylistTrackColumns._ID + " = ?",
                new String[]{trackId}
        );
    }

    // Hitung jumlah track dalam playlist
    public int countTracksInPlaylist(String playlistId) {
        open();
        Cursor cursor = database.query(
                DatabaseContract.TABLE_PLAYLIST_TRACKS,
                null,
                DatabaseContract.PlaylistTrackColumns.PLAYLIST_ID + " = ?",
                new String[]{playlistId},
                null, null, null
        );
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    // Ambil semua favorit
    public Cursor queryAll() {
        open();
        return database.query(
                DATABASE_TABLE,
                null, null, null, null, null,
                DatabaseContract.FavColumns._ID + " ASC"
        );
    }

    // Cek apakah track sudah di favorit
    public boolean isFavorite(String trackName, String artistName) {
        open();
        Cursor cursor = database.query(
                DATABASE_TABLE,
                null,
                DatabaseContract.FavColumns.TRACK_NAME + " = ? AND " +
                        DatabaseContract.FavColumns.ARTIST_NAME + " = ?",
                new String[]{trackName, artistName},
                null, null, null
        );
        boolean exists = false;
        if (cursor != null) {
            exists = cursor.getCount() > 0;
            cursor.close();
        }
        return exists;
    }

    public boolean isTrackInPlaylist(String playlistId, String trackName, String artistName) {
        open();
        String table = DatabaseContract.TABLE_PLAYLIST_TRACKS;

        String selection = DatabaseContract.PlaylistTrackColumns.PLAYLIST_ID + " = ? AND " +
                DatabaseContract.PlaylistTrackColumns.TRACK_NAME + " = ? AND " +
                DatabaseContract.PlaylistTrackColumns.ARTIST_NAME + " = ?";
        String[] selectionArgs = {playlistId, trackName, artistName};

        Cursor cursor = database.query(table, null, selection, selectionArgs, null, null, null);
        boolean exists = false;
        if (cursor != null) {
            exists = cursor.getCount() > 0;
            cursor.close();
        }
        return exists;
    }
}
