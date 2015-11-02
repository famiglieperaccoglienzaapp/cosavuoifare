package com.whatdoyouwanttodo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
/**
 * Crea le tabelle alla prima apertura del databese
 */
public class ChessboardDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Chessboard.db";
     
    public ChessboardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
     
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ChessboardDbContract.SQL_CREATE_CHESSBOARDS);
        db.execSQL(ChessboardDbContract.SQL_CREATE_CELLS);
        db.execSQL(ChessboardDbContract.SQL_CREATE_MUSIC_SLIDES);
        db.execSQL(ChessboardDbContract.SQL_CREATE_MUSIC_SLIDES_IMAGE_PATH);
        db.execSQL(ChessboardDbContract.SQL_CREATE_ACTIVE_LISTENING);
        db.execSQL(ChessboardDbContract.SQL_CREATE_ACTIVE_LISTENING_IMAGE_PATH);
        db.execSQL(ChessboardDbContract.SQL_CREATE_VIDEO_PLAYLIST);
        db.execSQL(ChessboardDbContract.SQL_CREATE_VIDEO_PLAYLIST_VIDEO_URL);
        db.execSQL(ChessboardDbContract.SQL_CREATE_SETTINGS);
    }
     
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: more intelligent upgrade policy
    	/* do nothing
        db.execSQL(ChessboardDbContract.SQL_DELETE_CHESSBOARDS);
        db.execSQL(ChessboardDbContract.SQL_DELETE_CELLS);
        db.execSQL(ChessboardDbContract.SQL_DELETE_MUSIC_SLIDES);
        db.execSQL(ChessboardDbContract.SQL_DELETE_MUSIC_SLIDES_IMAGE_PATHS);
        db.execSQL(ChessboardDbContract.SQL_DELETE_ACTIVE_LISTENING);
        db.execSQL(ChessboardDbContract.SQL_DELETE_ACTIVE_LISTENING_MUSIC_PATH);
        db.execSQL(ChessboardDbContract.SQL_DELETE_VIDEO_PLAYLIST);
        db.execSQL(ChessboardDbContract.SQL_DELETE_VIDEO_PLAYLIST_VIDEO_URL);
        db.execSQL(ChessboardDbContract.SQL_DELETE_SETTINGS);
        onCreate(db);
        */
    }
     
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: more intelligent downgrade policy
        onUpgrade(db, oldVersion, newVersion);
    }
}

