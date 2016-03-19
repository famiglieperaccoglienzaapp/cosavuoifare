package com.whatdoyouwanttodo.db;
 
import android.provider.BaseColumns;
 
/**
 * Contratto del database, contiene le definizioni dei nomi usati e delle query SQL eseguite
 */
public final class ChessboardDbContract {
    public ChessboardDbContract() {}
 
    public static abstract class ChessboardEntry implements BaseColumns {
        public static final String TABLE_NAME = "chessboard";
        public static final String COLUMN_NAME_NAME = "cb_name";
        public static final String COLUMN_NAME_PARENT = "parent";
        public static final String COLUMN_NAME_N_ROWS = "n_rows";
        public static final String COLUMN_NAME_N_COLUMNS = "n_columns";
        public static final String COLUMN_NAME_BG_COLOR = "cb_background_color";
        public static final String COLUMN_NAME_BORDER_WIDTH = "cb_border_width";
    }
     
    public static abstract class CellEntry implements BaseColumns {
        public static final String TABLE_NAME = "cell";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CHESSBOARD = "chessboard";
        public static final String COLUMN_NAME_ROW = "row";
        public static final String COLUMN_NAME_COLUMN = "column";
        public static final String COLUMN_NAME_BACKGROUND_COLOR = "background_color";
        public static final String COLUMN_NAME_BORDER_WIDTH = "border_width";
        public static final String COLUMN_NAME_BORDER_COLOR = "border_color";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_TEXT_WIDTH = "text_width";
        public static final String COLUMN_NAME_TEXT_COLOR = "text_color";
        public static final String COLUMN_NAME_IMAGE_PATH = "image_path";
        public static final String COLUMN_NAME_AUDIO_PATH = "audio_path";    
        public static final String COLUMN_NAME_ACTIVITY_TYPE = "activity_type";
        public static final String COLUMN_NAME_ACTIVITY_PARAM = "activity_param";
    }
    
    public static abstract class AbrakadabraEntry implements BaseColumns {
        public static final String TABLE_NAME = "abrakadabra";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SOUND_PATH = "sound_path";
        public static final String COLUMN_NAME_MUSIC_PATH = "music_path";
        public static final String COLUMN_NAME_MUSIC_DURATION_TIME = "music_duration_time";
        public static final String COLUMN_NAME_IMAGE_EFFECT = "image_effect";
    }
    
    public static abstract class AbrakadabraImagePathsEntry implements BaseColumns {
        public static final String TABLE_NAME = "abrakadabra_image_paths";
        public static final String COLUMN_NAME_IMAGE_PATH_ID = "image_path_id";
        public static final String COLUMN_NAME_IMAGE_PATH = "image_path";
        public static final String COLUMN_NAME_ROW = "row";
    }
    
    public static abstract class ActiveListeningEntry implements BaseColumns {
        public static final String TABLE_NAME = "active_listening";
        public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_BACKGROUND = "background";
        public static final String COLUMN_NAME_INTERVAL = "interval";
        public static final String COLUMN_NAME_REGISTRATION_PATH = "registration_path";
        public static final String COLUMN_NAME_PAUSE = "pause";
		public static final String COLUMN_NAME_PAUSE_INTERVAL = "pause_interval";
    }
    
    public static abstract class ActiveListeningMusicPathsEntry implements BaseColumns {
        public static final String TABLE_NAME = "active_listening_music_paths";
        public static final String COLUMN_NAME_MUSIC_PATH_ID = "music_path_id";
        public static final String COLUMN_NAME_MUSIC_PATH = "music_path";
        public static final String COLUMN_NAME_ROW = "row";
    }
    
    public static abstract class VideoPlaylistEntry implements BaseColumns {
        public static final String TABLE_NAME = "video_playlist";
        public static final String COLUMN_NAME_NAME = "name";
    }
    
    public static abstract class VideoPlaylistVideoUrlEntry implements BaseColumns {
        public static final String TABLE_NAME = "video_playlist_video_url";
        public static final String COLUMN_NAME_VIDEO_URL_ID = "video_url_id";
        public static final String COLUMN_NAME_VIDEO_URL_PATH = "video_url";
        public static final String COLUMN_NAME_ROW = "row";
    }
    
    public static abstract class SettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "global_settings";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_VALUE = "value";
    }
     
    public static final String SQL_CREATE_CHESSBOARDS =
            "CREATE TABLE IF NOT EXISTS " + ChessboardEntry.TABLE_NAME + " (" +
                    ChessboardEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ChessboardEntry.COLUMN_NAME_NAME + " TEXT," +
                    ChessboardEntry.COLUMN_NAME_PARENT + " INTEGER," +
                    ChessboardEntry.COLUMN_NAME_N_ROWS + " INTEGER," +
                    ChessboardEntry.COLUMN_NAME_N_COLUMNS + " INTEGER," +
                    ChessboardEntry.COLUMN_NAME_BG_COLOR + " INTEGER," +
                    ChessboardEntry.COLUMN_NAME_BORDER_WIDTH + " INTEGER" +
            " )";
     
    public static final String SQL_CREATE_CELLS =
            "CREATE TABLE IF NOT EXISTS " + CellEntry.TABLE_NAME + " (" +
                    CellEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CellEntry.COLUMN_NAME_NAME + " TEXT," +
                    CellEntry.COLUMN_NAME_CHESSBOARD + " INTEGER," +
                    CellEntry.COLUMN_NAME_ROW + " INTEGER," +
                    CellEntry.COLUMN_NAME_COLUMN + " INTEGER," +
                    CellEntry.COLUMN_NAME_BACKGROUND_COLOR + " INTEGER," +
                    CellEntry.COLUMN_NAME_BORDER_WIDTH + " INTEGER," +
                    CellEntry.COLUMN_NAME_BORDER_COLOR + " INTEGER," +
                    CellEntry.COLUMN_NAME_TEXT + " TEXT," +
                    CellEntry.COLUMN_NAME_TEXT_WIDTH + " INTEGER," +
                    CellEntry.COLUMN_NAME_TEXT_COLOR + " INTEGER," +
                    CellEntry.COLUMN_NAME_IMAGE_PATH + " TEXT," +
                    CellEntry.COLUMN_NAME_AUDIO_PATH + " TEXT," +
                    CellEntry.COLUMN_NAME_ACTIVITY_TYPE + " INTEGER," +
                    CellEntry.COLUMN_NAME_ACTIVITY_PARAM + " INTEGER" +
            " )";
    
    public static final String SQL_CREATE_ABRAKADABRA =
            "CREATE TABLE IF NOT EXISTS " + AbrakadabraEntry.TABLE_NAME + " (" +
            		AbrakadabraEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            		AbrakadabraEntry.COLUMN_NAME_NAME + " TEXT," +
            		AbrakadabraEntry.COLUMN_NAME_SOUND_PATH + " TEXT," +
            		AbrakadabraEntry.COLUMN_NAME_MUSIC_PATH + " TEXT," +
                    AbrakadabraEntry.COLUMN_NAME_MUSIC_DURATION_TIME + " INTEGER," +
            		AbrakadabraEntry.COLUMN_NAME_IMAGE_EFFECT + " INTEGER" +
            " )";

    public static final String SQL_CREATE_ABRAKADABRA_IMAGE_PATH =
            "CREATE TABLE IF NOT EXISTS " + AbrakadabraImagePathsEntry.TABLE_NAME + " (" +
            		AbrakadabraImagePathsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            		AbrakadabraImagePathsEntry.COLUMN_NAME_IMAGE_PATH_ID + " INTEGER," +
            		AbrakadabraImagePathsEntry.COLUMN_NAME_IMAGE_PATH + " TEXT," +
            		AbrakadabraImagePathsEntry.COLUMN_NAME_ROW + " INTEGER" +
            " )";
    
    public static final String SQL_CREATE_ACTIVE_LISTENING =
            "CREATE TABLE IF NOT EXISTS " + ActiveListeningEntry.TABLE_NAME + " (" +
            		ActiveListeningEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            		ActiveListeningEntry.COLUMN_NAME_NAME + " TEXT," +
            		ActiveListeningEntry.COLUMN_NAME_BACKGROUND + " TEXT," +
            		ActiveListeningEntry.COLUMN_NAME_INTERVAL + " INTEGER," +
            		ActiveListeningEntry.COLUMN_NAME_REGISTRATION_PATH + " TEXT," +
            		ActiveListeningEntry.COLUMN_NAME_PAUSE + " INTEGER," +
            		ActiveListeningEntry.COLUMN_NAME_PAUSE_INTERVAL + " INTEGER" +
            " )";
    
    public static final String SQL_CREATE_ACTIVE_LISTENING_IMAGE_PATH =
            "CREATE TABLE IF NOT EXISTS " + ActiveListeningMusicPathsEntry.TABLE_NAME + " (" +
            		ActiveListeningMusicPathsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            		ActiveListeningMusicPathsEntry.COLUMN_NAME_MUSIC_PATH_ID + " INTEGER," +
            		ActiveListeningMusicPathsEntry.COLUMN_NAME_MUSIC_PATH + " TEXT," +
            		ActiveListeningMusicPathsEntry.COLUMN_NAME_ROW + " INTEGER" +
            " )";
    
    public static final String SQL_CREATE_VIDEO_PLAYLIST =
            "CREATE TABLE IF NOT EXISTS " + VideoPlaylistEntry.TABLE_NAME + " (" +
            		VideoPlaylistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            		VideoPlaylistEntry.COLUMN_NAME_NAME + " TEXT" +
            " )";
    
    public static final String SQL_CREATE_VIDEO_PLAYLIST_VIDEO_URL =
            "CREATE TABLE IF NOT EXISTS " + VideoPlaylistVideoUrlEntry.TABLE_NAME + " (" +
            		VideoPlaylistVideoUrlEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            		VideoPlaylistVideoUrlEntry.COLUMN_NAME_VIDEO_URL_ID + " INTEGER," +
            		VideoPlaylistVideoUrlEntry.COLUMN_NAME_VIDEO_URL_PATH + " TEXT," +
            		VideoPlaylistVideoUrlEntry.COLUMN_NAME_ROW + " INTEGER" +
            " )";
    
    public static final String SQL_CREATE_SETTINGS =
            "CREATE TABLE IF NOT EXISTS " + SettingsEntry.TABLE_NAME + " (" +
            		SettingsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            		SettingsEntry.COLUMN_NAME_KEY + " TEXT," +
            		SettingsEntry.COLUMN_NAME_VALUE + " TEXT" +
            " )";
     
    public static final String SQL_DELETE_CHESSBOARDS =
            "DROP TABLE IF EXISTS " + ChessboardEntry.TABLE_NAME;
 
    public static final String SQL_DELETE_CELLS =
            "DROP TABLE IF EXISTS " + CellEntry.TABLE_NAME;
    
    public static final String SQL_DELETE_MUSIC_SLIDES =
            "DROP TABLE IF EXISTS " + AbrakadabraEntry.TABLE_NAME;
    
    public static final String SQL_DELETE_MUSIC_SLIDES_IMAGE_PATHS =
            "DROP TABLE IF EXISTS " + AbrakadabraImagePathsEntry.TABLE_NAME;
    
    public static final String SQL_DELETE_ACTIVE_LISTENING =
            "DROP TABLE IF EXISTS " + ActiveListeningEntry.TABLE_NAME;
    
    public static final String SQL_DELETE_ACTIVE_LISTENING_MUSIC_PATH =
            "DROP TABLE IF EXISTS " + ActiveListeningMusicPathsEntry.TABLE_NAME;
    
    public static final String SQL_DELETE_VIDEO_PLAYLIST =
            "DROP TABLE IF EXISTS " + VideoPlaylistEntry.TABLE_NAME;
    
    public static final String SQL_DELETE_VIDEO_PLAYLIST_VIDEO_URL =
            "DROP TABLE IF EXISTS " + VideoPlaylistVideoUrlEntry.TABLE_NAME;
    
    public static final String SQL_DELETE_SETTINGS =
    		"DROP TABLE IF EXISTS " + SettingsEntry.TABLE_NAME;
}