package com.whatdoyouwanttodo.db;

import android.database.Cursor;

import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.db.ChessboardDbContract.ChessboardEntry;

/**
 * Cursore di tabelle
 */
public class ChessboardCursor {

	private Cursor cursor;

	public ChessboardCursor(Cursor cursor) {
		this.cursor = cursor;
	}

	public boolean moveToNext() {
		return cursor.moveToNext();
	}

	public Chessboard getChessboard() {
		long id = cursor.getLong(
				cursor.getColumnIndex(ChessboardEntry._ID));
		String name = cursor.getString(
				cursor.getColumnIndex(ChessboardEntry.COLUMN_NAME_NAME));
		int parent = cursor.getInt(
				cursor.getColumnIndex(ChessboardEntry.COLUMN_NAME_PARENT));
		int nRows = cursor.getInt(
				cursor.getColumnIndex(ChessboardEntry.COLUMN_NAME_N_ROWS));
		int nColumns = cursor.getInt(
				cursor.getColumnIndex(ChessboardEntry.COLUMN_NAME_N_COLUMNS));
		int backgroundColor = cursor.getInt(
				cursor.getColumnIndex(ChessboardEntry.COLUMN_NAME_BG_COLOR));
		int borderWidth = cursor.getInt(
				cursor.getColumnIndex(ChessboardEntry.COLUMN_NAME_BORDER_WIDTH));
		
		return new Chessboard(id, parent, name, nRows, nColumns, backgroundColor, borderWidth);
	}

	public void close() {
		cursor.close();
	}

}
