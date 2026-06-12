package com.noctune.app.database;

import android.provider.BaseColumns;

public class DatabaseContract {

    public static String TABLE_NAME = "favorites";

    public static final class FavColumns implements BaseColumns {
        public static String TRACK_NAME = "track_name";
        public static String ARTIST_NAME = "artist_name";
        public static String PLAYCOUNT = "playcount";
        public static String LISTENERS = "listeners";
        public static String DURATION = "duration";
        public static String IMAGE_URL = "image_url";
    }

    public static String TABLE_PLAYLIST = "playlists";

    public static final class PlaylistColumns implements BaseColumns {
        public static String PLAYLIST_NAME = "playlist_name";
        public static String CREATED_AT = "created_at";
    }

    public static String TABLE_PLAYLIST_TRACKS = "playlist_tracks";

    public static final class PlaylistTrackColumns implements BaseColumns {
        public static String PLAYLIST_ID = "playlist_id";
        public static String TRACK_NAME = "track_name";
        public static String ARTIST_NAME = "artist_name";
        public static String DURATION = "duration";
        public static String IMAGE_URL = "image_url";
        public static final String PLAYCOUNT = "playcount";
        public static final String LISTENERS = "listeners";
    }
}