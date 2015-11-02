package com.whatdoyouwanttodo.config;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.db.CellCursor;
import com.whatdoyouwanttodo.db.ChessboardCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.utils.ActivityUtils;

/**
 * Attivita' che mostra tutte le tabelle contemporaneamente e permette di sceglierne una.
 * E' anche usata per mostrare la destinazione di un link, in questo caso mostra anche una
 * tabella "Nuova Tabella"
 */
public class AllGridViewActivity extends ActionBarActivity {
	public static final String SELECTION = "com.whatdoyouwanttodo.config.AllGridViewActivity.SELECTION";
	
	public static final int SELECTED_NO_CHESSBOARD = -1;
	public static final long SELECTED_NEW_CHESSBOARD = -2;
	public static final int SELECTED_BACK_CHESSBOARD = -3;
	public static final int SELECTED_NO_SELECTION = -4;
	
	public static AllGridViewReturn ret = null;

	public static boolean haveReturnParams() {
		if (ret == null)
			return false;
		return true;
	}

	public static AllGridViewReturn getReturnParams() {
		AllGridViewReturn clone = ret.clone();
		ret = null;
		return clone;
	}
	
	public static class AllGridViewReturn implements Cloneable {
		private long id;
		private String name;
		
		public AllGridViewReturn(long id) {
			this.id = id;
			this.name = null;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}
		
		public AllGridViewReturn clone() {
			AllGridViewReturn ret = new AllGridViewReturn(id);
			ret.name = name;
			return ret;
		}
	}
	
	public static Intent getStartIntentForOverview(Activity caller, long chessboardId) {
		Intent intent = new Intent(caller, AllGridViewActivity.class);
		ret = null;
		return intent;
	}

	public static Intent getStartIntentForSelection(Activity caller) {
		Intent intent = new Intent(caller, AllGridViewActivity.class);
		intent.putExtra(AllGridViewActivity.SELECTION, true);
		ret = null;
		return intent;
	}

	private boolean selection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_all_grid_view);
		
		// show new and back chessboard?
		Intent intent = getIntent();
		selection = intent.getBooleanExtra(SELECTION, false);
		
		// read from database
		ChessboardDbUtility dbu = new ChessboardDbUtility(this);
		dbu.openReadable();
		LinkedList<Chessboard> cbList = readAllChessboards(dbu);
		LinkedList<Cell> cellList = readAllCells(dbu);
		dbu.close();
		
		Chessboard[] chessboards = cbList.toArray(new Chessboard[cbList.size()]);
		Cell[] cells = cellList.toArray(new Cell[cellList.size()]);

		if (savedInstanceState == null) {
			AllGridViewFragment allGridViewFragment = AllGridViewFragment
					.newAllGridViewFragment(chessboards, cells, selection);

			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.add(R.id.grids_container, allGridViewFragment);
			transaction.commit();
		}
	}
	
	private LinkedList<Chessboard> readAllChessboards(ChessboardDbUtility dbu) {
		LinkedList<Chessboard> chessboards = new LinkedList<Chessboard>();
		ChessboardCursor cursorCb = dbu.getCursorOnEveryChessboard();
		if (cursorCb != null) {
			while (cursorCb.moveToNext()) {
				chessboards.add(cursorCb.getChessboard());
			}
			cursorCb.close();
		}
		return chessboards;
	}

	private LinkedList<Cell> readAllCells(ChessboardDbUtility dbu) {
		LinkedList<Cell> cells = new LinkedList<Cell>();
		CellCursor cursor = dbu.getCursorOnEveryCell();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				cells.add(cursor.getCell());
			}
			cursor.close();
		}
		return cells;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (selection) {
			getMenuInflater().inflate(R.menu.all_grid_view_selection, menu);
			String titleStr = getResources().getString(R.string.activity_all_grid_view_title_selection);
			ActivityUtils.changeActionBarTitle(this, titleStr);
		} else {
			getMenuInflater().inflate(R.menu.all_grid_view, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_back) {
			ret = null;
			finish();
			return true;
		} else if (id == R.id.action_no_selection) {
			ret = new AllGridViewReturn(SELECTED_NO_CHESSBOARD);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}