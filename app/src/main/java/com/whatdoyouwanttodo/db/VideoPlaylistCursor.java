package com.whatdoyouwanttodo.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.whatdoyouwanttodo.application.VideoPlaylist;
import com.whatdoyouwanttodo.db.ChessboardDbContract.VideoPlaylistEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.VideoPlaylistVideoUrlEntry;

/**
 * Cursore di playlist di video
 */
public class VideoPlaylistCursor {
	private Cursor cursor;
	private SQLiteDatabase db;

	public VideoPlaylistCursor(Cursor cursor, SQLiteDatabase db) {
		this.cursor = cursor;
		this.db = db;
	}

	public boolean moveToNext() {
		return cursor.moveToNext();
	}
	
	private static class VideoUrl {
		public String videoUrl;
		public int videoRow;
		
		public VideoUrl(String videoUrl, int videoRow) {
			this.videoUrl = videoUrl;
			this.videoRow = videoRow;
		}

		@Override
		public String toString() {
			return "VideoUrl [videoUrl=" + videoUrl + ", videoRow=" + videoRow
					+ "]";
		}
	}

	public VideoPlaylist getVideoPlaylist() {
		long id = cursor.getLong(
				cursor.getColumnIndex(VideoPlaylistEntry._ID));
		String name = cursor.getString(
				cursor.getColumnIndex(VideoPlaylistEntry.COLUMN_NAME_NAME));
		
		String[] projection = { 
				VideoPlaylistVideoUrlEntry.COLUMN_NAME_VIDEO_URL_ID,
				VideoPlaylistVideoUrlEntry.COLUMN_NAME_VIDEO_URL_PATH,
				VideoPlaylistVideoUrlEntry.COLUMN_NAME_ROW };

		Cursor cursor = db.query(VideoPlaylistVideoUrlEntry.TABLE_NAME, projection,
				VideoPlaylistVideoUrlEntry.COLUMN_NAME_VIDEO_URL_ID + " = " + id, new String[0], null,
				null, null);

		List<VideoUrl> videoUrls = new ArrayList<VideoUrl>();
		while(cursor.moveToNext()) {
			String videoUrl = cursor.getString(
					cursor.getColumnIndex(VideoPlaylistVideoUrlEntry.COLUMN_NAME_VIDEO_URL_PATH));
			int videoRow = cursor.getInt(
					cursor.getColumnIndex(VideoPlaylistVideoUrlEntry.COLUMN_NAME_ROW));
			videoUrls.add(new VideoUrl(videoUrl, videoRow));
		}
		Collections.sort(videoUrls, new Comparator<VideoUrl>() {
			@Override
			public int compare(VideoUrl v1, VideoUrl v2) {
				return v1.videoRow - v2.videoRow;
			}
		});
		
		cursor.close();
		
		String[] videoUrlOrdered = new String[videoUrls.size()];
		for(int i = 0; i < videoUrlOrdered.length; i++) {
			videoUrlOrdered[i] = videoUrls.get(i).videoUrl;
		}
		
		return new VideoPlaylist(id, name, videoUrlOrdered);
	}

	public void close() {
		cursor.close();
	}
}
