package com.whatdoyouwanttodo.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.whatdoyouwanttodo.application.Abrakadabra;
import com.whatdoyouwanttodo.db.ChessboardDbContract.AbrakadabraEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.AbrakadabraImagePathsEntry;

/**
 * Cursore di playlist di immagini
 */
public class AbrakadabraCursor {
	private Cursor cursor;
	private SQLiteDatabase db;

	public AbrakadabraCursor(Cursor cursor, SQLiteDatabase db) {
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

	public Abrakadabra getAbrakadabra() {	    
		long id = cursor.getLong(
				cursor.getColumnIndex(AbrakadabraEntry._ID));
		String name = cursor.getString(
				cursor.getColumnIndex(AbrakadabraEntry.COLUMN_NAME_NAME));
		String soundPath = cursor.getString(
				cursor.getColumnIndex(AbrakadabraEntry.COLUMN_NAME_SOUND_PATH));
		String musicPath = cursor.getString(
				cursor.getColumnIndex(AbrakadabraEntry.COLUMN_NAME_MUSIC_PATH));
		int imageEffect = cursor.getInt(
				cursor.getColumnIndex(AbrakadabraEntry.COLUMN_NAME_IMAGE_EFFECT));
		
		String[] projection = { 
				AbrakadabraImagePathsEntry.COLUMN_NAME_IMAGE_PATH_ID,
				AbrakadabraImagePathsEntry.COLUMN_NAME_IMAGE_PATH,
				AbrakadabraImagePathsEntry.COLUMN_NAME_ROW };

		Cursor cursor = db.query(AbrakadabraImagePathsEntry.TABLE_NAME, projection,
				AbrakadabraImagePathsEntry.COLUMN_NAME_IMAGE_PATH_ID + " = " + id, new String[0], null,
				null, null);

		List<ImagePath> imagePaths = new ArrayList<ImagePath>();
		while(cursor.moveToNext()) {
			String imagePath = cursor.getString(
					cursor.getColumnIndex(AbrakadabraImagePathsEntry.COLUMN_NAME_IMAGE_PATH));
			int imageRow = cursor.getInt(
					cursor.getColumnIndex(AbrakadabraImagePathsEntry.COLUMN_NAME_ROW));
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
		
		return new Abrakadabra(id, name, imagePathOrdered, soundPath, musicPath, imageEffect);
	}

	public void close() {
		cursor.close();
	}

}
