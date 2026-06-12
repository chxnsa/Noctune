package com.noctune.app.database;

import com.noctune.app.model.FavoriteTrack;
import com.noctune.app.model.PlaylistTrack;
import com.noctune.app.model.Playlist;
import android.database.Cursor;
import java.util.ArrayList;

public class MappingHelper {

    public static ArrayList<FavoriteTrack> mapCursorToArrayList(Cursor cursor) {
        ArrayList<FavoriteTrack> favorites = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(
                    DatabaseContract.FavColumns._ID));
            String trackName = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.FavColumns.TRACK_NAME));
            String artistName = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.FavColumns.ARTIST_NAME));
            String playcount = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.FavColumns.PLAYCOUNT));
            String listeners = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.FavColumns.LISTENERS));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.FavColumns.DURATION));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.FavColumns.IMAGE_URL));

            favorites.add(new FavoriteTrack(
                    id, trackName, artistName,
                    playcount, listeners, duration, imageUrl
            ));
        }
        return favorites;
    }

    public static ArrayList<Playlist> mapCursorToPlaylists(Cursor cursor) {
        ArrayList<Playlist> playlistList = new ArrayList<>();

        // Menggunakan while (cursor.moveToNext()) jauh lebih aman dan bersih
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistColumns._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistColumns.PLAYLIST_NAME));

                // Menghindari crash jika kolom CREATED_AT sewaktu-waktu bernilai null/kosong
                int createdAtIndex = cursor.getColumnIndex(DatabaseContract.PlaylistColumns.CREATED_AT);
                String createdAt = (createdAtIndex != -1) ? cursor.getString(createdAtIndex) : "";

                String cover = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlaylistColumns.PLAYLIST_COVER));

                Playlist playlist = new Playlist();
                playlist.setId(id);
                playlist.setName(name);
                playlist.setCreatedAt(createdAt);
                playlist.setCover(cover);

                playlistList.add(playlist);
            }
        }
        return playlistList;
    }

    public static ArrayList<PlaylistTrack> mapCursorToPlaylistTracks(Cursor cursor) {
        ArrayList<PlaylistTrack> tracks = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistTrackColumns._ID));
            int playlistId = cursor.getInt(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistTrackColumns.PLAYLIST_ID));
            String trackName = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistTrackColumns.TRACK_NAME));
            String artistName = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistTrackColumns.ARTIST_NAME));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistTrackColumns.DURATION));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistTrackColumns.IMAGE_URL));

            // --- PERBAIKAN: Ambil data statistik dari cursor SQLite ---
            String playcount = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistTrackColumns.PLAYCOUNT));
            String listeners = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistTrackColumns.LISTENERS));

            // Masukkan variabel playcount dan listeners ke dalam Constructor model object
            tracks.add(new PlaylistTrack(
                    id, playlistId, trackName, artistName, duration, imageUrl, playcount, listeners));
        }
        return tracks;
    }
}