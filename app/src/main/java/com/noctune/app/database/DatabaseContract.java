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
}