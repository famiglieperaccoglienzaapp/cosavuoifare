package com.whatdoyouwanttodo.db;

import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.db.ChessboardDbContract.CellEntry;

import android.database.Cursor;

/**
 * Cursore di celle
 */
public class CellCursor {
	private Cursor cursor;

	public CellCursor(Cursor cursor) {
		this.cursor = cursor;
	}

	public boolean moveToNext() {
		return cursor.moveToNext();
	}

	public Cell getCell() {
		long id = cursor.getLong(
				cursor.getColumnIndex(CellEntry._ID));
		String name = cursor.getString(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_NAME));
		int chessboard = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_CHESSBOARD));
		int row = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_ROW));
		int column = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_COLUMN));
		int backgroundColor = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_BACKGROUND_COLOR));
		int borderWidth = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_BORDER_WIDTH));
		int borderColor = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_BORDER_COLOR));
		String text = cursor.getString(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_TEXT));
		int textWidth = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_TEXT_WIDTH));
		int textColor = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_TEXT_COLOR));
		String imagePath = cursor.getString(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_IMAGE_PATH));
		String audioPath = cursor.getString(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_AUDIO_PATH));
		int activityType = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_ACTIVITY_TYPE));
		int activityParam = cursor.getInt(
				cursor.getColumnIndex(CellEntry.COLUMN_NAME_ACTIVITY_PARAM));
		
		return new Cell(id, chessboard, name, row, column,
				backgroundColor, borderWidth, borderColor,
				text, textWidth, textColor,
				imagePath, audioPath,
				activityType, activityParam);
	}

	public void close() {
		cursor.close();
	}
}
