package com.whatdoyouwanttodo.settings;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.ActiveListening;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.application.MusicSlides;
import com.whatdoyouwanttodo.application.VideoPlaylist;

/**
 * Impostazioni interne del programma visibili all'utente
 */
public final class Constants {
	private static Constants instance = null;

	private Constants(Context context) {
		Resources res = context.getResources();
		
		String newCellStr = res.getString(R.string.constant_new_cell);
		NEW_CELL = new Cell(0, 0, newCellStr, 0, 0, Color.WHITE,
				Cell.BORDER_NO_BORDER, Color.BLACK, newCellStr,
				Cell.TEXT_NORMAL, Color.BLACK, Configurations.IMAGE_NEW_NAME,
				Configurations.TTS_PREFIX + newCellStr,
				Cell.ACTIVITY_TYPE_NONE, 0);

		String newTableStr = res.getString(R.string.constant_new_table);
		NEW_CHESSBOARD = new Chessboard(0, 0, newTableStr, 2, 3,
				Color.BLACK, Chessboard.BORDER_SMALL);
		
		BACK_CHESSBOARD_NAME = res.getString(R.string.constant_back_table_name);

		String newActiveListeningStr = res.getString(R.string.constant_new_active_listening);
		NEW_ACTIVE_LISTENING = new ActiveListening(0, newActiveListeningStr,
				"", new String[0], 10, "", 10, 5);

		String newImagePlaylistStr = res.getString(R.string.constant_new_image_playlist);
		NEW_MUSIC_SLIDES = new MusicSlides(0, newImagePlaylistStr,
				new String[0], "");

		String newVideoPlaylistStr = res.getString(R.string.constant_new_video_playlist);
		NEW_VIDEO_PLAYLIST = new VideoPlaylist(0, newVideoPlaylistStr,
				new String[0]);

		String dirNameStr = res.getString(R.string.constant_dir_name);
		FILE_DIR = dirNameStr;
		IMAGE_PREFIX = dirNameStr + "_";

		String exportDirStr = res.getString(R.string.constant_export_dir_name);
		EXPORT_DIR = exportDirStr;
		
		String importDirStr = res.getString(R.string.constant_import_dir_name);
		IMPORT_DIR = importDirStr;
		
		String thumbnailDir = res.getString(R.string.constant_thumbnail_dir_name);
		THUMBNAIL_DIR = thumbnailDir;
	}

	public static Constants getInstance(Context context) {
		if (instance == null)
			instance = new Constants(context);
		return instance;
	}

	// User visible constants
	public final Cell NEW_CELL;
	public final Chessboard NEW_CHESSBOARD;
	public final String BACK_CHESSBOARD_NAME;
	public final ActiveListening NEW_ACTIVE_LISTENING;
	public final MusicSlides NEW_MUSIC_SLIDES;
	public final VideoPlaylist NEW_VIDEO_PLAYLIST;
	public final String FILE_DIR;
	public final String THUMBNAIL_DIR;
	public final String EXPORT_DIR;
	public final String IMPORT_DIR;
	public final String IMAGE_PREFIX;
}
