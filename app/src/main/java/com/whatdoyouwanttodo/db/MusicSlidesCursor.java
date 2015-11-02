package com.whatdoyouwanttodo.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.whatdoyouwanttodo.application.MusicSlides;
import com.whatdoyouwanttodo.db.ChessboardDbContract.MusicSlidesEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.MusicSlidesImagePathsEntry;

/**
 * Cursore di playlist di immagini
 */
public class MusicSlidesCursor {
	private Cursor cursor;
	private SQLiteDatabase db;

	public MusicSlidesCursor(Cursor cursor, SQLiteDatabase db) {
		this.cursor = cursor;
		this.db = db;
	}

	public boolean moveToNext() {
		return cursor.moveToNext();
	}
	
	private static class ImagePath {
		public String imagePath;
		public int imageRow;
		
		public ImagePath(String imagePath, int imageRow) {
			this.imagePath = imagePath;
			this.imageRow = imageRow;
		}

		@Override
		public String toString() {
			return "ImagePath [imagePath=" + imagePath + ", imageRow="
					+ imageRow + "]";
		}
	}

	public MusicSlides getMusicSlides() {	    
		long id = cursor.getLong(
				cursor.getColumnIndex(MusicSlidesEntry._ID));
		String name = cursor.getString(
				cursor.getColumnIndex(MusicSlidesEntry.COLUMN_NAME_NAME));
		String musicPath = cursor.getString(
				cursor.getColumnIndex(MusicSlidesEntry.COLUMN_NAME_MUSIC_PATH));
		
		String[] projection = { 
				MusicSlidesImagePathsEntry.COLUMN_NAME_IMAGE_PATH_ID,
				MusicSlidesImagePathsEntry.COLUMN_NAME_IMAGE_PATH,
				MusicSlidesImagePathsEntry.COLUMN_NAME_ROW };

		Cursor cursor = db.query(MusicSlidesImagePathsEntry.TABLE_NAME, projection,
				MusicSlidesImagePathsEntry.COLUMN_NAME_IMAGE_PATH_ID + " = " + id, new String[0], null,
				null, null);

		List<ImagePath> imagePaths = new ArrayList<ImagePath>();
		while(cursor.moveToNext()) {
			String imagePath = cursor.getString(
					cursor.getColumnIndex(MusicSlidesImagePathsEntry.COLUMN_NAME_IMAGE_PATH));
			int imageRow = cursor.getInt(
					cursor.getColumnIndex(MusicSlidesImagePathsEntry.COLUMN_NAME_ROW));
			imagePaths.add(new ImagePath(imagePath, imageRow));
		}
		Collections.sort(imagePaths, new Comparator<ImagePath>() {
			@Override
			public int compare(ImagePath i1, ImagePath i2) {
				return i1.imageRow - i2.imageRow;
			}
		});
		
		cursor.close();
		
		String[] imagePathOrdered = new String[imagePaths.size()];
		for(int i = 0; i < imagePathOrdered.length; i++) {
			imagePathOrdered[i] = imagePaths.get(i).imagePath;
		}
		
		return new MusicSlides(id, name, imagePathOrdered, musicPath);
	}

	public void close() {
		cursor.close();
	}

}
