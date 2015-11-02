package com.whatdoyouwanttodo.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.whatdoyouwanttodo.application.ActiveListening;
import com.whatdoyouwanttodo.db.ChessboardDbContract.ActiveListeningEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.ActiveListeningMusicPathsEntry;

/**
 * Cursore di Ascolti Attivi
 */
public class ActiveListeningCursor {
	private Cursor cursor;
	private SQLiteDatabase db;

	public ActiveListeningCursor(Cursor cursor, SQLiteDatabase db) {
		this.cursor = cursor;
		this.db = db;
	}

	public boolean moveToNext() {
		return cursor.moveToNext();
	}
	
	private static class MusicPath {
		public String musicPath;
		public int musicRow;
		
		public MusicPath(String musicPath, int musicRow) {
			this.musicPath = musicPath;
			this.musicRow = musicRow;
		}

		@Override
		public String toString() {
			return "MusicPath [musicPath=" + musicPath + ", musicRow="
					+ musicRow + "]";
		}
	}

	public ActiveListening getActiveListening() {
		long id = cursor.getLong(
				cursor.getColumnIndex(ActiveListeningEntry._ID));
		String name = cursor.getString(
				cursor.getColumnIndex(ActiveListeningEntry.COLUMN_NAME_NAME));
		String background = cursor.getString(
				cursor.getColumnIndex(ActiveListeningEntry.COLUMN_NAME_BACKGROUND));
		int interval = cursor.getInt(
				cursor.getColumnIndex(ActiveListeningEntry.COLUMN_NAME_INTERVAL));
		String registrationPath = cursor.getString(
				cursor.getColumnIndex(ActiveListeningEntry.COLUMN_NAME_REGISTRATION_PATH));
		int pause = cursor.getInt(
				cursor.getColumnIndex(ActiveListeningEntry.COLUMN_NAME_PAUSE));
		int pauseInterval = cursor.getInt(
				cursor.getColumnIndex(ActiveListeningEntry.COLUMN_NAME_PAUSE_INTERVAL));
		
		String[] projection = { 
				ActiveListeningMusicPathsEntry.COLUMN_NAME_MUSIC_PATH_ID,
				ActiveListeningMusicPathsEntry.COLUMN_NAME_MUSIC_PATH,
				ActiveListeningMusicPathsEntry.COLUMN_NAME_ROW };

		Cursor cursor = db.query(ActiveListeningMusicPathsEntry.TABLE_NAME, projection,
				ActiveListeningMusicPathsEntry.COLUMN_NAME_MUSIC_PATH_ID + " = " + id, new String[0], null,
				null, null);

		List<MusicPath> musicPaths = new ArrayList<MusicPath>();
		while(cursor.moveToNext()) {
			String musicPath = cursor.getString(
					cursor.getColumnIndex(ActiveListeningMusicPathsEntry.COLUMN_NAME_MUSIC_PATH));
			int musicRow = cursor.getInt(
					cursor.getColumnIndex(ActiveListeningMusicPathsEntry.COLUMN_NAME_ROW));
			musicPaths.add(new MusicPath(musicPath, musicRow));
		}
		Collections.sort(musicPaths, new Comparator<MusicPath>() {
			@Override
			public int compare(MusicPath m1, MusicPath m2) {
				return m1.musicRow - m2.musicRow;
			}
		});
		
		cursor.close();
		
		String[] musicPathOrdered = new String[musicPaths.size()];
		for(int i = 0; i < musicPathOrdered.length; i++) {
			musicPathOrdered[i] = musicPaths.get(i).musicPath;
		}
		
		return new ActiveListening(id, name, background, musicPathOrdered, interval, registrationPath, pause, pauseInterval);
	}

	public void close() {
		cursor.close();
	}
}
