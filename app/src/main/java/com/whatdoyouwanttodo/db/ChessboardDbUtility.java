package com.whatdoyouwanttodo.db;

import java.util.Set;
import java.util.TreeSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.db.ChessboardDbContract.ActiveListeningEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.ActiveListeningMusicPathsEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.CellEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.ChessboardEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.AbrakadabraEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.AbrakadabraImagePathsEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.SettingsEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.VideoPlaylistEntry;
import com.whatdoyouwanttodo.db.ChessboardDbContract.VideoPlaylistVideoUrlEntry;
import com.whatdoyouwanttodo.utils.ImageLoader;


/**
 * Classe di utilita' che espone metodi di comodo per aggiornare il database.
 */
public class ChessboardDbUtility {
	private ChessboardDbHelper dbHelper;
	private SQLiteDatabase db;

	public ChessboardDbUtility(Context context) {
		dbHelper = new ChessboardDbHelper(context);
		db = null;
	}

	public void openWritable() {
		db = dbHelper.getWritableDatabase();
	}

	public void openReadable() {
		db = dbHelper.getReadableDatabase();
	}

	public void close() {
		if(ChessboardApplication.DEBUG_DB) {
			if(db == null) {
				Log.d("DATABASE", "close on null!!!");
			}
		}
		
		if (db != null) {
			db.close();
			db = null;
		}
		
		if (ChessboardApplication.DEBUG_DB_CHECK) {
			db = dbHelper.getWritableDatabase();
			checkDatabaseConsistency();
			db.close();
		}
	}

	public long addChessboard(long parentId, String name, int nRows,
			int nColumns, int backgroundColor, int borderWidth) {
		ContentValues cbValues = new ContentValues();
		cbValues.put(ChessboardEntry.COLUMN_NAME_NAME, name);
		cbValues.put(ChessboardEntry.COLUMN_NAME_PARENT, parentId);
		cbValues.put(ChessboardEntry.COLUMN_NAME_N_ROWS, nRows);
		cbValues.put(ChessboardEntry.COLUMN_NAME_N_COLUMNS, nColumns);
		cbValues.put(ChessboardEntry.COLUMN_NAME_BG_COLOR, backgroundColor);
		cbValues.put(ChessboardEntry.COLUMN_NAME_BORDER_WIDTH, borderWidth);

		long rowId = db.insert(ChessboardEntry.TABLE_NAME, null, cbValues);

		if(ChessboardApplication.DEBUG_DB) {
			if(rowId == 0) {
				Log.d(getClass().getName(), "problem on add chessboard");
			}
		}

		return rowId;
	}

	public long addCell(long chessboard, String name, int row, int column,
			int backgroundColor, int borderWidth, int borderColor, String text,
			int textWidth, int textColor, String imagePath, String audioPath,
			int activityType, long activityParam) {
		ContentValues clValues = new ContentValues();
		clValues.put(CellEntry.COLUMN_NAME_NAME, name);
		clValues.put(CellEntry.COLUMN_NAME_CHESSBOARD, chessboard);
		clValues.put(CellEntry.COLUMN_NAME_ROW, row);
		clValues.put(CellEntry.COLUMN_NAME_COLUMN, column);
		clValues.put(CellEntry.COLUMN_NAME_BACKGROUND_COLOR, backgroundColor);
		clValues.put(CellEntry.COLUMN_NAME_BORDER_WIDTH, borderWidth);
		clValues.put(CellEntry.COLUMN_NAME_BORDER_COLOR, borderColor);
		clValues.put(CellEntry.COLUMN_NAME_TEXT, text);
		clValues.put(CellEntry.COLUMN_NAME_TEXT_WIDTH, textWidth);
		clValues.put(CellEntry.COLUMN_NAME_TEXT_COLOR, textColor);
		clValues.put(CellEntry.COLUMN_NAME_IMAGE_PATH, imagePath);
		clValues.put(CellEntry.COLUMN_NAME_AUDIO_PATH, audioPath);
		clValues.put(CellEntry.COLUMN_NAME_ACTIVITY_TYPE, activityType);
		clValues.put(CellEntry.COLUMN_NAME_ACTIVITY_PARAM, activityParam);

		long rowId =  db.insert(CellEntry.TABLE_NAME, null, clValues);

		if(ChessboardApplication.DEBUG_DB) {
			if(rowId == 0) {
				Log.d(getClass().getName(), "problem on add cell");
			}
		}
		
		return rowId;
	}

	public long addAbrakadabra(String name, String[] imagePaths,
			String soundPath, String musicPath, int imageEffect) {
		ContentValues clValues = new ContentValues();
		clValues.put(AbrakadabraEntry.COLUMN_NAME_NAME, name);
		clValues.put(AbrakadabraEntry.COLUMN_NAME_SOUND_PATH, soundPath);
		clValues.put(AbrakadabraEntry.COLUMN_NAME_MUSIC_PATH, musicPath);
		clValues.put(AbrakadabraEntry.COLUMN_NAME_IMAGE_EFFECT, imageEffect);
	    
		long rowId =  db.insert(AbrakadabraEntry.TABLE_NAME, null, clValues);

		if(ChessboardApplication.DEBUG_DB) {
			if(rowId == 0) {
				Log.d(getClass().getName(), "problem on add music slides");
			}
		}
		
		for(int i = 0; i < imagePaths.length; i++) {
			clValues = new ContentValues();
			clValues.put(AbrakadabraImagePathsEntry.COLUMN_NAME_IMAGE_PATH_ID, rowId);
			clValues.put(AbrakadabraImagePathsEntry.COLUMN_NAME_IMAGE_PATH, imagePaths[i]);
			clValues.put(AbrakadabraImagePathsEntry.COLUMN_NAME_ROW, i);
		   
			long rowId1 =  db.insert(AbrakadabraImagePathsEntry.TABLE_NAME, null, clValues);
			if(ChessboardApplication.DEBUG_DB) {
				if(rowId1 == 0) {
					Log.d(getClass().getName(), "problem on add music slides");
				}
			}
		}
		
		return rowId;
	}
	
	public long addActiveListening(String name, String background, String[] musicPaths, int interval, String registrationPath, int pause, int pauseInterval) {
		ContentValues clValues = new ContentValues();
		clValues.put(ActiveListeningEntry.COLUMN_NAME_NAME, name);
		clValues.put(ActiveListeningEntry.COLUMN_NAME_BACKGROUND, background);
		clValues.put(ActiveListeningEntry.COLUMN_NAME_INTERVAL, interval);
		clValues.put(ActiveListeningEntry.COLUMN_NAME_REGISTRATION_PATH, registrationPath);
		clValues.put(ActiveListeningEntry.COLUMN_NAME_PAUSE, pause);
		clValues.put(ActiveListeningEntry.COLUMN_NAME_PAUSE_INTERVAL, pauseInterval);

		long rowId =  db.insert(ActiveListeningEntry.TABLE_NAME, null, clValues);

		if(ChessboardApplication.DEBUG_DB) {
			if(rowId == 0) {
				Log.d(getClass().getName(), "problem on add active listening");
			}
		}
		
		for(int i = 0; i < musicPaths.length; i++) {
			clValues = new ContentValues();
			clValues.put(ActiveListeningMusicPathsEntry.COLUMN_NAME_MUSIC_PATH_ID, rowId);
			clValues.put(ActiveListeningMusicPathsEntry.COLUMN_NAME_MUSIC_PATH, musicPaths[i]);
			clValues.put(ActiveListeningMusicPathsEntry.COLUMN_NAME_ROW, i);
		   
			long rowId1 =  db.insert(ActiveListeningMusicPathsEntry.TABLE_NAME, null, clValues);
			if(ChessboardApplication.DEBUG_DB) {
				if(rowId1 == 0) {
					Log.d(getClass().getName(), "problem on add active listening");
				}
			}
		}
		
		return rowId;
	}
	
	public long addVideoPlaylist(String name, String[] videoUrl) {
		ContentValues clValues = new ContentValues();
		clValues.put(VideoPlaylistEntry.COLUMN_NAME_NAME, name);

		long rowId =  db.insert(VideoPlaylistEntry.TABLE_NAME, null, clValues);

		if(ChessboardApplication.DEBUG_DB) {
			if(rowId == 0) {
				Log.d(getClass().getName(), "problem on add video playlist");
			}
		}
		
		for(int i = 0; i < videoUrl.length; i++) {
			clValues = new ContentValues();
			clValues.put(VideoPlaylistVideoUrlEntry.COLUMN_NAME_VIDEO_URL_ID, rowId);
			clValues.put(VideoPlaylistVideoUrlEntry.COLUMN_NAME_VIDEO_URL_PATH, videoUrl[i]);
			clValues.put(VideoPlaylistVideoUrlEntry.COLUMN_NAME_ROW, i);
		   
			long rowId1 =  db.insert(VideoPlaylistVideoUrlEntry.TABLE_NAME, null, clValues);
			if(ChessboardApplication.DEBUG_DB) {
				if(rowId1 == 0) {
					Log.d(getClass().getName(), "problem on add active listening");
				}
			}
		}
		
		return rowId;
	}

	public long updateChessboard(long id, int parentId, String name, int nRows,
			int nColumns, int backgroundColor, int borderWidth) {
		ContentValues cbValues = new ContentValues();
		cbValues.put(ChessboardEntry.COLUMN_NAME_NAME, name);
		cbValues.put(ChessboardEntry.COLUMN_NAME_PARENT, parentId);
		cbValues.put(ChessboardEntry.COLUMN_NAME_N_ROWS, nRows);
		cbValues.put(ChessboardEntry.COLUMN_NAME_N_COLUMNS, nColumns);
		cbValues.put(ChessboardEntry.COLUMN_NAME_BG_COLOR, backgroundColor);
		cbValues.put(ChessboardEntry.COLUMN_NAME_BORDER_WIDTH, borderWidth);

		long rowId = db.update(ChessboardEntry.TABLE_NAME, cbValues,
				ChessboardEntry._ID + " = " + id, null);

		if(ChessboardApplication.DEBUG_DB) {
			if(rowId == 0) {
				Log.d(getClass().getName(), "problem on update chessboard " + id);
			}
		}

		return rowId;
	}

	public long updateCell(long id, long chessboard, String name, int row,
			int column, int backgroundColor, int borderWidth, int borderColor,
			String text, int textWidth, int textColor, String imagePath,
			String audioPath, int activityType, long activityParam) {
		ContentValues cbValues = new ContentValues();
		cbValues.put(CellEntry.COLUMN_NAME_NAME, name);
		cbValues.put(CellEntry.COLUMN_NAME_CHESSBOARD, chessboard);
		cbValues.put(CellEntry.COLUMN_NAME_ROW, row);
		cbValues.put(CellEntry.COLUMN_NAME_COLUMN, column);
		cbValues.put(CellEntry.COLUMN_NAME_BACKGROUND_COLOR, backgroundColor);
		cbValues.put(CellEntry.COLUMN_NAME_BORDER_WIDTH, borderWidth);
		cbValues.put(CellEntry.COLUMN_NAME_BORDER_COLOR, borderColor);
		cbValues.put(CellEntry.COLUMN_NAME_TEXT, text);
		cbValues.put(CellEntry.COLUMN_NAME_TEXT_WIDTH, textWidth);
		cbValues.put(CellEntry.COLUMN_NAME_TEXT_COLOR, textColor);
		cbValues.put(CellEntry.COLUMN_NAME_IMAGE_PATH, imagePath);
		cbValues.put(CellEntry.COLUMN_NAME_AUDIO_PATH, audioPath);
		cbValues.put(CellEntry.COLUMN_NAME_ACTIVITY_TYPE, activityType);
		cbValues.put(CellEntry.COLUMN_NAME_ACTIVITY_PARAM, activityParam);

		long rowId = db.update(CellEntry.TABLE_NAME, cbValues,
				CellEntry._ID + " = " + id, null);

		if(ChessboardApplication.DEBUG_DB) {
			if(rowId == 0) {
				Log.d(getClass().getName(), "problem on update cell " + id);
			}
		}

		return rowId;
	}
	
	public long deleteChessboard(long id) {
		long rowId = db.delete(ChessboardEntry.TABLE_NAME,
				ChessboardEntry._ID + " = " + id, null);

		if(ChessboardApplication.DEBUG_DB) {
			if(rowId == 0) {
				Log.d(getClass().getName(), "problem on delete chessboard " + id);
			}
		}

		return rowId;
	}

	public long deleteCell(long id) {
		long rowId = db.delete(CellEntry.TABLE_NAME,
				CellEntry._ID + " = " + id, null);
		
		if(ChessboardApplication.DEBUG_DB) {
			if(rowId == 0) {
				Log.d(getClass().getName(), "problem on delete cell " + id);
			}
		}

		return rowId;
	}

	public CellCursor getCursorOnCell(long id) {
		String[] projection = { CellEntry._ID,
				CellEntry.COLUMN_NAME_NAME,
				CellEntry.COLUMN_NAME_CHESSBOARD, 
				CellEntry.COLUMN_NAME_ROW,
				CellEntry.COLUMN_NAME_COLUMN,
				CellEntry.COLUMN_NAME_BACKGROUND_COLOR,
				CellEntry.COLUMN_NAME_BORDER_WIDTH,
				CellEntry.COLUMN_NAME_BORDER_COLOR,
				CellEntry.COLUMN_NAME_TEXT,
				CellEntry.COLUMN_NAME_TEXT_WIDTH,
				CellEntry.COLUMN_NAME_TEXT_COLOR,
				CellEntry.COLUMN_NAME_IMAGE_PATH,
				CellEntry.COLUMN_NAME_AUDIO_PATH,
				CellEntry.COLUMN_NAME_ACTIVITY_TYPE,
				CellEntry.COLUMN_NAME_ACTIVITY_PARAM };

		Cursor cursor = db.query(CellEntry.TABLE_NAME, projection,
				CellEntry.COLUMN_NAME_CHESSBOARD + " = " + id,
				new String[0], null, null, null);

		return new CellCursor(cursor);
	}

	public ChessboardCursor getCursorOnChessboard(long id) {
		String[] projection = { ChessboardEntry._ID,
				ChessboardEntry.COLUMN_NAME_NAME,
				ChessboardEntry.COLUMN_NAME_PARENT,
				ChessboardEntry.COLUMN_NAME_N_ROWS,
				ChessboardEntry.COLUMN_NAME_N_COLUMNS,
				ChessboardEntry.COLUMN_NAME_BG_COLOR,
				ChessboardEntry.COLUMN_NAME_BORDER_WIDTH };

		Cursor cursor = db.query(ChessboardEntry.TABLE_NAME, projection,
				ChessboardEntry._ID + " = " + id, new String[0], null,
				null, null);

		return new ChessboardCursor(cursor);
	}
	
	public AbrakadabraCursor getCursorOnAbrakadabra(long id) {
		String[] projection = { AbrakadabraEntry._ID,
				AbrakadabraEntry.COLUMN_NAME_NAME,
				AbrakadabraEntry.COLUMN_NAME_SOUND_PATH,
				AbrakadabraEntry.COLUMN_NAME_MUSIC_PATH,
				AbrakadabraEntry.COLUMN_NAME_IMAGE_EFFECT };
		
		Cursor cursor = db.query(AbrakadabraEntry.TABLE_NAME, projection,
				AbrakadabraEntry._ID + " = " + id, new String[0], null,
				null, null);

		return new AbrakadabraCursor(cursor, db);
	}

	public ActiveListeningCursor getCursorOnActiveListening(long id) {
		String[] projection = { ActiveListeningEntry._ID,
				ActiveListeningEntry.COLUMN_NAME_NAME,
				ActiveListeningEntry.COLUMN_NAME_BACKGROUND,
				ActiveListeningEntry.COLUMN_NAME_INTERVAL,
				ActiveListeningEntry.COLUMN_NAME_REGISTRATION_PATH,
				ActiveListeningEntry.COLUMN_NAME_PAUSE,
				ActiveListeningEntry.COLUMN_NAME_PAUSE_INTERVAL };
		
		Cursor cursor = db.query(ActiveListeningEntry.TABLE_NAME, projection,
				ActiveListeningEntry._ID + " = " + id, new String[0], null,
				null, null);

		return new ActiveListeningCursor(cursor, db);
	}

	public VideoPlaylistCursor getCursorOnVideoPlaylist(long id) {
		String[] projection = { VideoPlaylistEntry._ID,
				VideoPlaylistEntry.COLUMN_NAME_NAME };
		
		Cursor cursor = db.query(VideoPlaylistEntry.TABLE_NAME, projection,
				VideoPlaylistEntry._ID + " = " + id, new String[0], null,
				null, null);

		return new VideoPlaylistCursor(cursor, db);
	}

	public CellCursor getCursorOnEveryCell() {
		String[] projection = { CellEntry._ID,
				CellEntry.COLUMN_NAME_NAME,
				CellEntry.COLUMN_NAME_CHESSBOARD,
				CellEntry.COLUMN_NAME_ROW,
				CellEntry.COLUMN_NAME_COLUMN,
				CellEntry.COLUMN_NAME_BACKGROUND_COLOR,
				CellEntry.COLUMN_NAME_BORDER_WIDTH,
				CellEntry.COLUMN_NAME_BORDER_COLOR,
				CellEntry.COLUMN_NAME_TEXT,
				CellEntry.COLUMN_NAME_TEXT_WIDTH,
				CellEntry.COLUMN_NAME_TEXT_COLOR,
				CellEntry.COLUMN_NAME_IMAGE_PATH,
				CellEntry.COLUMN_NAME_AUDIO_PATH,
				CellEntry.COLUMN_NAME_ACTIVITY_TYPE,
				CellEntry.COLUMN_NAME_ACTIVITY_PARAM };

		Cursor cursor = db.query(CellEntry.TABLE_NAME, projection, null,
				new String[0], null, null, null);

		return new CellCursor(cursor);
	}

	public ChessboardCursor getCursorOnEveryChessboard() {
		String[] projection = { ChessboardEntry._ID,
				ChessboardEntry.COLUMN_NAME_NAME,
				ChessboardEntry.COLUMN_NAME_PARENT,
				ChessboardEntry.COLUMN_NAME_N_ROWS,
				ChessboardEntry.COLUMN_NAME_N_COLUMNS,
				ChessboardEntry.COLUMN_NAME_BG_COLOR,
				ChessboardEntry.COLUMN_NAME_BORDER_WIDTH };

		Cursor cursor = db.query(ChessboardEntry.TABLE_NAME, projection, null,
				new String[0], null, null, null);

		return new ChessboardCursor(cursor);
	}

	public long getRootChessboardId() {
		return Long.parseLong(getSettingValue("rootcb", "1"));
	}

	public void setRootChessboardId(long rootId) {
		setSettingValue("rootcb", Long.toString(rootId));
	}

	public boolean getWifyCheck() {
		return Boolean.parseBoolean(getSettingValue("wifycheck", "true"));
	}

	public void setWifyCheck(boolean state) {
		setSettingValue("wifycheck", Boolean.toString(state));
	}

	public boolean getFullscreenMode() {
		return Boolean.parseBoolean(getSettingValue("fullscreen", "false"));
	}

	public void setFullscreenMode(boolean state) {
		setSettingValue("fullscreen", Boolean.toString(state));
	}

	public boolean getDisableLockMode() {
		return Boolean.parseBoolean(getSettingValue("lockmode", "true"));
	}

	public void setDisableLockMode(boolean state) {
		setSettingValue("lockmode", Boolean.toString(state));
	}

	public String getTtsLanguage() {
		return getSettingValue("ttslanguage", null);
	}

	public void setTtsLanguage(String language) {
		setSettingValue("ttslanguage", language);
	}

	/*
	public boolean getCellTutorial() {
		return Boolean.parseBoolean(getSettingValue("celltutorial", "true"));
	}

	public void setCellTutorial(boolean state) {
		setSettingValue("celltutorial", Boolean.toString(state));
	}

	public boolean getGridTutorial() {
		return Boolean.parseBoolean(getSettingValue("gridtutorial", "true"));
	}

	public void setGridTutorial(boolean state) {
		setSettingValue("gridtutorial", Boolean.toString(state));
	}
	*/

	private void setSettingValue(String key, String value) {
		db.delete(SettingsEntry.TABLE_NAME, SettingsEntry.COLUMN_NAME_KEY + " = '" + value + "'", new String[0]);
		ContentValues clValues = new ContentValues();
		clValues.put(SettingsEntry.COLUMN_NAME_KEY, key);
		clValues.put(SettingsEntry.COLUMN_NAME_VALUE, value);
		db.insert(SettingsEntry.TABLE_NAME, null, clValues);
	}

	private String getSettingValue(String key, String defaultValue) {
		String[] projection = { SettingsEntry.COLUMN_NAME_VALUE };

		Cursor cursor = db.query(SettingsEntry.TABLE_NAME, projection,
				SettingsEntry.COLUMN_NAME_KEY + " = '" + key + "'",
				new String[0], null, null, null);

		String check = defaultValue;
		while (cursor.moveToNext()) {
			int colIndex = cursor
					.getColumnIndex(SettingsEntry.COLUMN_NAME_VALUE);
			check = cursor.getString(colIndex);
		}
		cursor.close();

		return check;
	}

	public boolean isEmpty() {
		boolean empty = true;

		String[] projection = { ChessboardEntry.COLUMN_NAME_PARENT };
		
		try {
			Cursor cursor = db.query(ChessboardEntry.TABLE_NAME, projection,
					null, new String[0], null, null, null);

			if (cursor != null) {
				while (cursor.moveToNext()) {
					empty = false;
				}
				cursor.close();
			}
		} catch (Exception ex) {
			return empty;
		}

		return empty;
	}

	public void clear() {
		// delete all tables
        db.execSQL(ChessboardDbContract.SQL_DELETE_CHESSBOARDS);
        db.execSQL(ChessboardDbContract.SQL_DELETE_CELLS);
        db.execSQL(ChessboardDbContract.SQL_DELETE_MUSIC_SLIDES);
        db.execSQL(ChessboardDbContract.SQL_DELETE_MUSIC_SLIDES_IMAGE_PATHS);
        db.execSQL(ChessboardDbContract.SQL_DELETE_ACTIVE_LISTENING);
        db.execSQL(ChessboardDbContract.SQL_DELETE_ACTIVE_LISTENING_MUSIC_PATH);
        db.execSQL(ChessboardDbContract.SQL_DELETE_VIDEO_PLAYLIST);
        db.execSQL(ChessboardDbContract.SQL_DELETE_VIDEO_PLAYLIST_VIDEO_URL);
        db.execSQL(ChessboardDbContract.SQL_DELETE_SETTINGS);
		
		// recreate all tables
        db.execSQL(ChessboardDbContract.SQL_CREATE_CHESSBOARDS);
        db.execSQL(ChessboardDbContract.SQL_CREATE_CELLS);
        db.execSQL(ChessboardDbContract.SQL_CREATE_ABRAKADABRA);
        db.execSQL(ChessboardDbContract.SQL_CREATE_ABRAKADABRA_IMAGE_PATH);
        db.execSQL(ChessboardDbContract.SQL_CREATE_ACTIVE_LISTENING);
        db.execSQL(ChessboardDbContract.SQL_CREATE_ACTIVE_LISTENING_IMAGE_PATH);
        db.execSQL(ChessboardDbContract.SQL_CREATE_VIDEO_PLAYLIST);
        db.execSQL(ChessboardDbContract.SQL_CREATE_VIDEO_PLAYLIST_VIDEO_URL);
        db.execSQL(ChessboardDbContract.SQL_CREATE_SETTINGS);
	}
	
	// XXX: this for debug
	// non aggiornato a music slides and active listening
	public void checkDatabaseConsistency() {
		db = dbHelper.getReadableDatabase();

		// read chessboards
		String[] projection = { ChessboardEntry._ID,
				ChessboardEntry.COLUMN_NAME_PARENT,
				ChessboardEntry.COLUMN_NAME_NAME,
				ChessboardEntry.COLUMN_NAME_N_ROWS,
				ChessboardEntry.COLUMN_NAME_N_COLUMNS,
				ChessboardEntry.COLUMN_NAME_BG_COLOR,
				ChessboardEntry.COLUMN_NAME_BORDER_WIDTH };
		Cursor cursor = db.query(ChessboardEntry.TABLE_NAME, projection, null,
				new String[0], null, null, null);
		ChessboardCursor ccursor = new ChessboardCursor(cursor);
		Set<Long> chessboardIds = new TreeSet<Long>();
		while (ccursor.moveToNext()) {
			Chessboard chessboard = ccursor.getChessboard();

			StringBuilder chessboardString = new StringBuilder();

			if (chessboardIds.contains(chessboard.getId())) {
				chessboardString
						.append("(chessboardIds.contains(chessboard.getId()))");
			}
			chessboardIds.add(chessboard.getId());

			if (chessboard.getParentId() < 0)
				chessboardString.append((chessboard.getParentId() < 0));
			if (chessboard.getColumnCount() < 0)
				chessboardString.append("(chessboard.getColumnCount() < 0)");
			if (chessboard.getRowCount() < 0)
				chessboardString.append("(chessboard.getRowCount() < 0)");
			if (!(chessboard.getBorderWidth() == Chessboard.BORDER_NO_BORDER
					|| chessboard.getBorderWidth() == Chessboard.BORDER_SMALL
					|| chessboard.getBorderWidth() == Chessboard.BORDER_MEDIUM || chessboard
						.getBorderWidth() == Chessboard.BORDER_LARGE))
				chessboardString
						.append("(chessboard.getBorderWidth() != <values>)");
			if (!(chessboard.getBackgroundColor() == Color.BLACK
					|| chessboard.getBackgroundColor() == Color.GRAY
					|| chessboard.getBackgroundColor() == Color.WHITE
					|| chessboard.getBackgroundColor() == Color.RED
					|| chessboard.getBackgroundColor() == Color.GREEN
					|| chessboard.getBackgroundColor() == Color.BLUE
					|| chessboard.getBackgroundColor() == Color.YELLOW || chessboard
						.getBackgroundColor() == Color.MAGENTA))
				chessboardString
						.append("(chessboard.getBackgroundColor() != <values>)");

			if (chessboardString.length() > 0) {
				Log.d("DB_CONSISTENCY", chessboardString.toString());
			}
		}
		ccursor.close();

		// read cells
		String[] projectionCell = { CellEntry._ID,
				CellEntry.COLUMN_NAME_CHESSBOARD, CellEntry.COLUMN_NAME_NAME,
				CellEntry.COLUMN_NAME_ROW, CellEntry.COLUMN_NAME_COLUMN,
				CellEntry.COLUMN_NAME_BACKGROUND_COLOR,
				CellEntry.COLUMN_NAME_BORDER_WIDTH,
				CellEntry.COLUMN_NAME_BORDER_COLOR, CellEntry.COLUMN_NAME_TEXT,
				CellEntry.COLUMN_NAME_TEXT_WIDTH,
				CellEntry.COLUMN_NAME_TEXT_COLOR,
				CellEntry.COLUMN_NAME_IMAGE_PATH,
				CellEntry.COLUMN_NAME_AUDIO_PATH,
				CellEntry.COLUMN_NAME_ACTIVITY_TYPE,
				CellEntry.COLUMN_NAME_ACTIVITY_PARAM };
		Cursor cursorCell = db.query(CellEntry.TABLE_NAME, projectionCell,
				null, new String[0], null, null, null);
		CellCursor cellCursor = new CellCursor(cursorCell);
		Set<Long> cellIds = new TreeSet<Long>();
		while (cellCursor.moveToNext()) {
			Cell cell = cellCursor.getCell();
			StringBuilder cellString = new StringBuilder();

			if (cellIds.contains(cell.getId())) {
				cellString.append("(cellIds.contains(cell.getId()))");
			}
			cellIds.add(cell.getId());

			if (ImageLoader.getInstance().existImage(cell.getImagePath()) == false)
				cellString
						.append("(ImageUtils.existImage(cell.getImagePath()) == false)");

			if (cell.getActivityType() == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD
					&& chessboardIds.contains(cell.getActivityParam()) == false)
				cellString
						.append("(OPEN_CHESSBOARD && cbIds not contains(cellDestId)");

			if (cellString.length() > 0) {
				Log.d(getClass().getName(), cellString.toString());
			}
		}
		cellCursor.close();

		db.close();
	}
	 
	// XXX: this for debug
	// non aggiornato a music slides and active listening
	public void printAllDatabaseForDebug() {
		db = dbHelper.getReadableDatabase();

		String[] projection = { ChessboardEntry._ID,
				ChessboardEntry.COLUMN_NAME_PARENT,
				ChessboardEntry.COLUMN_NAME_NAME,
				ChessboardEntry.COLUMN_NAME_N_ROWS,
				ChessboardEntry.COLUMN_NAME_N_COLUMNS,
				ChessboardEntry.COLUMN_NAME_BG_COLOR,
				ChessboardEntry.COLUMN_NAME_BORDER_WIDTH };

		Cursor cursor = db.query(ChessboardEntry.TABLE_NAME, projection, null,
				new String[0], null, null, null);

		ChessboardCursor ccursor = new ChessboardCursor(cursor);

		while (ccursor.moveToNext()) {
			Chessboard chessboard = ccursor.getChessboard();
			String chessboardString = "cb " + chessboard.getId() + " " + chessboard.getName() + " "
					+ chessboard.getParentId();
			Log.d(getClass().getName(), chessboardString);
		}

		ccursor.close();

		String[] projectionCell = { 
				CellEntry._ID,
				CellEntry.COLUMN_NAME_CHESSBOARD,
				CellEntry.COLUMN_NAME_NAME,
				CellEntry.COLUMN_NAME_ROW,
				CellEntry.COLUMN_NAME_COLUMN,
				CellEntry.COLUMN_NAME_BACKGROUND_COLOR,
				CellEntry.COLUMN_NAME_BORDER_WIDTH,
				CellEntry.COLUMN_NAME_BORDER_COLOR,
				CellEntry.COLUMN_NAME_TEXT,
				CellEntry.COLUMN_NAME_TEXT_WIDTH,
				CellEntry.COLUMN_NAME_TEXT_COLOR,
				CellEntry.COLUMN_NAME_IMAGE_PATH,
				CellEntry.COLUMN_NAME_AUDIO_PATH,
				CellEntry.COLUMN_NAME_ACTIVITY_TYPE,
				CellEntry.COLUMN_NAME_ACTIVITY_PARAM };

		Cursor cursorCell = db.query(CellEntry.TABLE_NAME, projectionCell,
				null, new String[0], null, null, null);

		CellCursor cellCursor = new CellCursor(cursorCell);

		while (cellCursor.moveToNext()) {
			Cell cell = cellCursor.getCell();
			String cellString = "el " + cell.getId() + " " + cell.getChessboard() + " "
					+ cell.getName() + " " + cell.getRow() + " "
					+ cell.getColumn();
			Log.d(getClass().getName(), cellString);
		}

		cellCursor.close();

		db.close();
	}
}
