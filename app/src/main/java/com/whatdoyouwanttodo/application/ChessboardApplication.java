package com.whatdoyouwanttodo.application;

import android.app.Application;

/**
 * Rappresenta l'intera applicazione e contiene le proprieta' globali
 */
public class ChessboardApplication extends Application {
	public static final boolean DEBUG_MODE = true;
	public static final boolean DEBUG_MODE_IN_OUT_ACTIVITY = false;
	public static final boolean DEBUG_DB = true;
	public static final boolean DEBUG_REFRESH_DB = false;
	public static final boolean DEBUG_DB_CHECK = false;
	public static final boolean DEBUG_RAPID_DOUBLE_CELL_FIX = true;
	public static final boolean DEBUG_HEAP_LOG = true;
	public static final boolean DEBUG_IMPORT_EXPORT = true;
	public static final boolean PROFILING_START_DB_REFRESH = true;
	
	private static boolean fullscreenMode = false;

	public static void setFullscreenMode(boolean fullscreenMode) {
		ChessboardApplication.fullscreenMode = fullscreenMode;
	}

	public static boolean getFullscreenMode() {
		return ChessboardApplication.fullscreenMode;
	}

	private static boolean wifyCheck = true;

	public static void setWifyCheck(boolean wifyCheck) {
		ChessboardApplication.wifyCheck = wifyCheck;
	}

	public static boolean getWifyCheck() {
		return ChessboardApplication.wifyCheck;
	}
	
	private static boolean disableLockMode = true;

	public static void setDisableLockMode(boolean disableLockMode) {
		ChessboardApplication.disableLockMode = disableLockMode;
	}

	public static boolean getDisableLockMode() {
		return ChessboardApplication.disableLockMode;
	}

	/*
	private static boolean gridTutorial = true;

	public static boolean getGridTuturial() {
		return gridTutorial;
	}

	public static void setGridTutorial(boolean gridTutorial) {
		ChessboardApplication.gridTutorial = gridTutorial;
	}

	private static boolean cellTutorial = true;

	public static boolean getCellTuturial() {
		return cellTutorial;
	}

	public static void setCellTutorial(boolean cellTutorial) {
		ChessboardApplication.cellTutorial = cellTutorial;
	}
	*/
}