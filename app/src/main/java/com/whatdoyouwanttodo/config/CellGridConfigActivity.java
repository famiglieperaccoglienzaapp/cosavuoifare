package com.whatdoyouwanttodo.config;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.whatdoyouwanttodo.ChessboardFragment;
import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.config.ActiveListeningConfigActivity.ActiveListeningReturn;
import com.whatdoyouwanttodo.config.AllGridViewActivity.AllGridViewReturn;
import com.whatdoyouwanttodo.config.MusicSlidesConfigActivity.MusicSlidesReturn;
import com.whatdoyouwanttodo.config.VideoPlaylistConfigActivity.VideoPlaylistReturn;
import com.whatdoyouwanttodo.db.ChessboardCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.utils.ActivityUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;

/**
 * Attivita' che permette di configurare una singola cella
 */
public class CellGridConfigActivity extends ActionBarActivity implements
		CellGridConfigFragment.OnCellChangeListener {
	public static final String ROW = "com.whatdoyouwanttodo.config.CellGridConfigActivity.ROW";
	public static final String COLUMN = "com.whatdoyouwanttodo.config.CellGridConfigActivity.COLUMN";
	
	private static FlexibleCellGrid flexGridParam = null;

	private Chessboard configChessboard;
	
	private Cell cell;
	private Cell[] adaptedArray;
	private CellGridConfigFragment configFragment;
	
	public static Intent getStartIntent(Activity caller, int row, int column, FlexibleCellGrid flexGrid) {
		Intent intent = new Intent(caller, CellGridConfigActivity.class);
		intent.putExtra(CellGridConfigActivity.ROW, row);
		intent.putExtra(CellGridConfigActivity.COLUMN, column);
		flexGridParam  = flexGrid;
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		configChessboard = new Chessboard(-1, 0, "Config", 1, 1, Color.BLACK, Chessboard.BORDER_SMALL);

		// get edited cell
		Intent intent = getIntent();
		int row = intent.getIntExtra(ROW, -1);
		int column = intent.getIntExtra(COLUMN, -1);
		FlexibleCellGrid flexGrid = flexGridParam;
		flexGridParam = null;
		this.cell = flexGrid.getCell(row, column);
		
		if(ChessboardApplication.DEBUG_MODE_IN_OUT_ACTIVITY) {
			Log.d(getClass().getName(), "input: " + cell);
		}

		setContentView(R.layout.activity_cell_grid_config);

		if (savedInstanceState == null) {
			// prepare adapted cell
			Cell adaptedClone = cell.clone();
			adaptedClone.setRow(0);
			adaptedClone.setColumn(0);
//			adaptedClone.setActivityType(Cell.ACTIVITY_TYPE_NONE);
			this.adaptedArray = new Cell[1];
			adaptedArray[0] = adaptedClone;
			
			// create config fragment and chessboard fragment (with one cell)
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			configFragment = CellGridConfigFragment
					.newCellGridConfigFragment(cell.clone()); // adapted cell have activity type modified
			transaction.add(R.id.config_container, configFragment);
			ChessboardFragment chessboardFragment = ChessboardFragment
					.newChessboardFragmentWithConfigButtons(configChessboard, adaptedArray, false, true);
			transaction.add(R.id.chessboard_container, chessboardFragment);
			transaction.commit();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(AllGridViewActivity.haveReturnParams()) {
			AllGridViewReturn ret = AllGridViewActivity.getReturnParams();
			long id = ret.getId();
			
			if (id == AllGridViewActivity.SELECTED_NO_CHESSBOARD) {
				cell.setActivityType(Cell.ACTIVITY_TYPE_NONE);
				cell.setActivityParam(0);
				configFragment.setLinkToText(0, "");
				onCellChange(cell);
			} else if (id == AllGridViewActivity.SELECTED_NEW_CHESSBOARD) {
				// create new chessboard
				ChessboardDbUtility dbu = new ChessboardDbUtility(this);
				dbu.openWritable();

				Constants constants = Constants.getInstance(this);
				long chessboardId = dbu.addChessboard(cell.getChessboard(),
						ret.getName(),
						constants.NEW_CHESSBOARD.getRowCount(),
						constants.NEW_CHESSBOARD.getColumnCount(),
						constants.NEW_CHESSBOARD.getBackgroundColor(),
						constants.NEW_CHESSBOARD.getBorderWidth());

				dbu.close();

				// update view
				cell.setActivityType(Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD);
				cell.setActivityParam(chessboardId);
				configFragment.setLinkToText(chessboardId, ret.getName());
				onCellChange(cell);
			} else if (id == AllGridViewActivity.SELECTED_BACK_CHESSBOARD) {
				String destStr = getResources().getString(R.string.activity_cell_grid_config_link_to_back);
				cell.setActivityType(Cell.ACTIVITY_TYPE_CLOSE_CHESSBOARD);
				cell.setActivityParam(0);
				configFragment.setLinkToText(0, destStr);
				onCellChange(cell);
			} else {
				// get selected chessboard
				ChessboardDbUtility dbu = new ChessboardDbUtility(this);
				dbu.openReadable();

				ChessboardCursor cc = dbu.getCursorOnChessboard(id);
				cc.moveToNext();
				Chessboard cb = cc.getChessboard();
				cc.close();

				dbu.close();

				// update view
				cell.setActivityType(Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD);
				cell.setActivityParam(cb.getId());
				configFragment.setLinkToText(cb.getId(), cb.getName());
				onCellChange(cell);
			}
		}
		
		if(MusicSlidesConfigActivity.haveReturnParams()) {
			MusicSlidesReturn ret = MusicSlidesConfigActivity.getReturnParams();
			
			// update view
			cell.setActivityType(Cell.ACTIVITY_TYPE_ABRAKADABRA);
			cell.setActivityParam(ret.getId());
			configFragment.selectActionWithValue(Cell.ACTIVITY_TYPE_ABRAKADABRA, ret.getId(), ret.getName());
			onCellChange(cell);
		}
		
		if(ActiveListeningConfigActivity.haveReturnParams()) {
			ActiveListeningReturn ret = ActiveListeningConfigActivity.getReturnParams();
			
			// update view
			cell.setActivityType(Cell.ACTIVITY_TYPE_ACTIVE_LISTENING);
			cell.setActivityParam(ret.getId());
			configFragment.selectActionWithValue(Cell.ACTIVITY_TYPE_ACTIVE_LISTENING, ret.getId(), ret.getName());
			onCellChange(cell);
		}
		
		if(VideoPlaylistConfigActivity.haveReturnParams()) {
			VideoPlaylistReturn ret = VideoPlaylistConfigActivity.getReturnParams();
			
			// update view
			cell.setActivityType(Cell.ACTIVITY_TYPE_PLAY_VIDEO);
			cell.setActivityParam(ret.getId());
			configFragment.selectActionWithValue(Cell.ACTIVITY_TYPE_PLAY_VIDEO, ret.getId(), ret.getName());
			onCellChange(cell);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cell_grid_config, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_cell_configure) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCellChange(Cell cell) {
		// syncronize with view copy
		adaptedArray[0].set(cell);
		adaptedArray[0].setRow(0);
		adaptedArray[0].setColumn(0);
//		adaptedArray[0].setActivityType(Cell.ACTIVITY_TYPE_NONE);
		
		// syncronize cell with master copy
		this.cell.set(cell);
		
		// update chessboard fragment
		ImageLoader.getInstance().cleanPictures();
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		ChessboardFragment chessboardFragment = ChessboardFragment.newChessboardFragmentWithConfigButtons(configChessboard, adaptedArray, false, true);
		transaction.replace(R.id.chessboard_container, chessboardFragment);
		transaction.commit();
		if(ChessboardApplication.DEBUG_HEAP_LOG) {
			ActivityUtils.logHeap();
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		if(ChessboardApplication.DEBUG_MODE_IN_OUT_ACTIVITY) {
			Log.d(getClass().getName(), "output: " + cell);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		configFragment.onActivityResult(requestCode, resultCode, data);
	}
}

