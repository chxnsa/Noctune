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
        ArrayList<Playlist> playlists = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistColumns._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistColumns.PLAYLIST_NAME));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.PlaylistColumns.CREATED_AT));

            playlists.add(new Playlist(id, name, createdAt, 0));
        }
        return playlists;
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

            tracks.add(new PlaylistTrack(
                    id, playlistId, trackName, artistName, duration, imageUrl));
        }
        return tracks;
    }
}