package com.whatdoyouwanttodo.application;

import java.util.LinkedList;
import java.util.TreeSet;

import android.app.Activity;

import com.whatdoyouwanttodo.config.ChessboardSelectorActivity;
import com.whatdoyouwanttodo.config.ChessboardThumbnailManager;
import com.whatdoyouwanttodo.db.CellCursor;
import com.whatdoyouwanttodo.db.ChessboardCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;

/**
 * Modella un insieme di tabelle da selezionare
 */
public class SimpleChessboardSelectorModel implements ChessboardSelectorActivity.ChessboardSelectorModel {
	private Chessboard[] chessboards;
	private Cell[][] cbCells;
	private boolean[] selected;
	private Activity activity;
	private String title;
	private long chessboardId = -1;
	private Chessboard cb;
	private Cell[] cells;
	private SelectionListener callback;
	
	public static interface SelectionListener {
		void selected(Chessboard[] cb, Cell[][] cells, boolean[] selected);
	}

	public SimpleChessboardSelectorModel(Activity activity, String title) {
		this.activity = activity;
		this.title = title;

		LinkedList<Chessboard> cbs = new LinkedList<Chessboard>();
		ChessboardDbUtility dbu = new ChessboardDbUtility(activity);
		dbu.openReadable();

		ChessboardCursor cbCursor = dbu.getCursorOnEveryChessboard();
		while (cbCursor.moveToNext()) {
			cbs.add(cbCursor.getChessboard());
		}
		cbCursor.close();
		chessboards = new Chessboard[cbs.size()];
		for(int i = 0; i < cbs.size(); i++) {
			chessboards[i] = cbs.get(i);
		}
		cbs.clone();
		cbs = null;

		dbu.close();
		
		selected = new boolean[chessboards.length];
		cbCells = new Cell[chessboards.length][];
		for (int i = 0; i < selected.length; i++) {
			selected[i] = false;
		}
	}
	
	public void setCallback(SelectionListener callback) {
		this.callback = callback;
	}

	@Override
	public String getItemName(int i) {
		return chessboards[i].getName();
	}
	
	@Override
	public String getItemImage(int i) {
		updateValues(i);
		
		String path = ChessboardThumbnailManager.getInstance(activity).getThumbnailPathOf(
				activity, cb, cells);
		return path;
	}

	@Override
	public int getItemCount() {
		return chessboards.length;
	}
	
	@Override
	public String getHelpText() {
		return title;
	}
	
	private void updateValues(int i) {
		long newChessboardId = chessboards[i].getId();
		if(newChessboardId != chessboardId) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(activity);
			dbu.openReadable();
			chessboardId = chessboards[i].getId();
			cb = readChessboard(dbu, chessboardId);
			cells = readCells(dbu, chessboardId);
			cbCells[i] = cells;
			dbu.close();
		}
	}

	private Chessboard readChessboard(ChessboardDbUtility dbu, long chessboardId) {
		Chessboard cb = null;
		ChessboardCursor cursorCb = dbu.getCursorOnChessboard(chessboardId);
		if (cursorCb != null) {
			while (cursorCb.moveToNext()) {
				cb = cursorCb.getChessboard();
			}
			cursorCb.close();
		}
		return cb;
	}

	private Cell[] readCells(ChessboardDbUtility dbu, long chessboardId) {
		LinkedList<Cell> cells = new LinkedList<Cell>();
		CellCursor cursor = dbu.getCursorOnCell(chessboardId);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				cells.add(cursor.getCell());
			}
			cursor.close();
		}
		Cell[] ret = cells.toArray(new Cell[cells.size()]);
		cells.clear();
		cells = null;
		return ret;
	}

	@Override
	public void resetSelections() {
		for (int i = 0; i < selected.length; i++) {
			selected[i] = false;
		}
	}

	@Override
	public void setSelectedItem(int i, boolean sel) {
		selected[i] = sel;
	}

	@Override
	public boolean confirmSelected() {
		int selectedCount = 0;
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] == true) {
				selectedCount++;
			}
		}
		if (selectedCount > 0) {
			callback.selected(chessboards, cbCells, selected);
			return true;
		}
		return false;
	}

	/**
	 * Valore di ritorno di trimToSelected()
	 */
	public static final class TrimReturn {
		public Chessboard[] cbs;
		public Cell[][] cbCells;

		public TrimReturn(Chessboard[] cbs, Cell[][] cbCells) {
			this.cbs = cbs;
			this.cbCells = cbCells;
		}
		
	}
	
	public static TrimReturn trimToSelected(Chessboard[] chessboards, Cell[][] cbCells, boolean[] selected) {
		int selCount = 0;
		for(int i = 0 ; i < selected.length; i++) {
			if(selected[i] == true) {
				selCount++;
			}
		}
		
		Chessboard[] selCb = new Chessboard[selCount];
		Cell[][] selCell = new Cell[selCount][];
		int pos = 0;
		for(int i = 0 ; i < selected.length; i++) {
			if(selected[i] == true) {
				selCb[pos] = chessboards[i];
				selCell[pos] = cbCells[i];
				pos++;
			}
		}
		
		return new TrimReturn(selCb, selCell);
	}
	
	public static TrimReturn recurseFromSelected(Chessboard[] chessboards, Cell[][] cbCells, boolean[] selected) {
		// initialize
		TreeSet<Long> idToFind = new TreeSet<Long>();
		TreeSet<Long> knowedIds = new TreeSet<Long>();
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] == true) {
				knowedIds.add(chessboards[i].getId());
				Cell[] cells = cbCells[i];
				for (int j = 0; j < cells.length; j++) {
					Cell cell = cells[j];
					if (cell.getActivityType() == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD) {
						long newId = cell.getActivityParam();
						if (knowedIds.contains(newId) == false) {
							idToFind.add(newId);
							knowedIds.add(newId);
						}
					}
				}
			}
		}
		
		// find others
		int recurseLimit = 0;
		while(idToFind.isEmpty() == false || recurseLimit > 100) {
			for(int i = 0 ; i < selected.length; i++) {
				if(selected[i] == false && idToFind.remove(chessboards[i].getId()) == true) {
					Cell[] cells = cbCells[i];
					for(int j = 0; j < cells.length; j++) {
						Cell cell = cells[j];
						if(cell.getActivityType() == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD) {
							long newId = cell.getActivityParam();
							if (knowedIds.contains(newId) == false) {
								idToFind.add(newId);
								knowedIds.add(newId);
							}
						}
					}
					selected[i] = true;
				}
			}
			recurseLimit++;
		}
		
		int selCount = 0;
		for(int i = 0 ; i < selected.length; i++) {
			if(selected[i] == true) {
				selCount++;
			}
		}
		
		Chessboard[] selCb = new Chessboard[selCount];
		Cell[][] selCell = new Cell[selCount][];
		int pos = 0;
		for(int i = 0 ; i < selected.length; i++) {
			if(selected[i] == true) {
				selCb[pos] = chessboards[i];
				selCell[pos] = cbCells[i];
				pos++;
			}
		}
		
		return new TrimReturn(selCb, selCell);
	}
}
