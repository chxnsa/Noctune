package com.noctune.app.database;

import com.noctune.app.model.FavoriteTrack;
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
}