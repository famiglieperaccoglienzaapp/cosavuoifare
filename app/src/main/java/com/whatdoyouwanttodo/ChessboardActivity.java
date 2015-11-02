package com.whatdoyouwanttodo;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.config.GridConfigActivity;
import com.whatdoyouwanttodo.db.CellCursor;
import com.whatdoyouwanttodo.db.ChessboardCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.utils.ActivityUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;
import com.whatdoyouwanttodo.utils.TtsSoundPool;

/**
 * Rappresenta una tabella AAC, puo' esisterne solo un'instanza alla volta
 */
public class ChessboardActivity extends ActionBarActivity {
	private final static String ID = "com.whatdoyouwanttodo.ChessboardActivity.ID";

	private static ChessboardActivity instance = null;
	
	private long[] chessboardIds;
	
	// true when chessboard need to change (return of from setting or new chessboard)
//	private boolean settingsOpened;
	
	private FrameLayout container = null;
	
	/**
	 * Non usare! usare getStartIntent()
	 */
	public ChessboardActivity() {
//		settingsOpened = false;
	}

	/**
	 * Crea un'attivita' che mostra una tabella AAC con dei bottoni.
	 * I dati sono caricati dal database.
	 * 
	 * @param caller L'attivita' chiamante
	 * @param id l'indentificatore della tabella nel database
	 * @return l'intento necessario per creare l'attivita'
	 */
	public static Intent getStartIntent(Activity caller, long id) {
		Intent intent = new Intent(caller, ChessboardActivity.class);
		intent.putExtra(ID, id);
		return intent;
	}
	
	/**
	 * Ottiene l'attivita' corrente
	 * 
	 * @return l'unica istanza dell'attivita', se creata
	 */
	public static Activity getActivity() {
		return instance;
	}

	/**
	 * Cambia la tabella visualizzata senza ricreare l'attivita'
	 * 
	 * @param id l'identificatore della nuova tabella o -1 per tornare indietro
	 * @param parentId l'identificatore della tabella corrente
	 */
	public static void changeChessboard(long id, long parentId) {
		// with -1 go to parent chessboard
		if(id == -1) {
			instance.doBack();
			return;
		}
		
		// detect cycle
		int cycleCheck = -1;
		for(int i = 0; i < instance.chessboardIds.length; i++) {
			long parentCbId = instance.chessboardIds[i];
			if(parentCbId == id) {
				if(cycleCheck == -1) {
					cycleCheck = i;
				}
			}
		}
		if(cycleCheck > -1) {
			// with cycle remove cycle path
			long[] prev = instance.chessboardIds;

			instance.chessboardIds = new long[cycleCheck + 1];
			for(int i = 0; i < cycleCheck + 1; i++) {
				instance.chessboardIds[i] = prev[i];
			}

			instance.updateChessboardFragment();
			return;
		}
		
		// update chessboard id and parent list
		long[] prev = instance.chessboardIds;
		instance.chessboardIds = new long[prev.length + 1];
		for(int i = 0; i < prev.length; i++) {
			instance.chessboardIds[i] = prev[i];
		}
		instance.chessboardIds[prev.length] = id;
		
		// refresh chessboard
		instance.updateChessboardFragment();
	}

	@Override
	protected void onResume() {
		super.onResume();

		long gridRet = GridConfigActivity.getCurrentGrid();
		if (gridRet != GridConfigActivity.NO_GRID_ID) {
			chessboardIds = new long[1];
			chessboardIds[0] = gridRet;
			
			updateChessboardFragment();
		}
		/*
		if (settingsOpened == true) {
			settingsOpened = false;
			
		}
		*/
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (ChessboardApplication.getFullscreenMode() == true) {
			ActivityUtils.setFullscreen(this);
		}

		if (ChessboardApplication.getDisableLockMode() == true) {
			ActivityUtils.disableWakeLock(this);
		}
		ActivityUtils.disableKeyguard(this);
		
		if(instance != null) {
			Log.d(getClass().getName(), "new ChessboardActivity()");
		}
		instance = this;
		
		setContentView(R.layout.activity_chessboard);
		
		this.container  = (FrameLayout) findViewById(R.id.container);
		
		// get chessboard number
		Intent intent = getIntent();
		chessboardIds = new long[1];
		chessboardIds[0] = intent.getLongExtra(ChessboardActivity.ID, 0);
		
		// read from database
		ChessboardDbUtility dbu = new ChessboardDbUtility(this);
		dbu.openReadable();
		Chessboard cb = readChessboard(dbu);
		Cell[] cells = readCells(dbu);
		dbu.close();
		
		// add chessboard
		TableLayout cbLayout = ChessboardLayout.newChessboardLayout(cb, cells, this);
		container.addView(cbLayout);
		
		setTitle(cb.getName());
//		if (savedInstanceState == null) {
//			ChessboardFragment chessboardFragment = ChessboardFragment
//					.newChessboardFragment(cb, cells);
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, chessboardFragment).commit();
//		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chessboard, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
//			settingsOpened = true;
			Intent intent = TestLockActivity.getStartIntent(this,
					new TestLockActivity.OnTestListener() {
						@Override
						public void onTest(boolean passed) {
							if (passed == true) {
								Intent intent = GridConfigActivity.getStartIntent(ChessboardActivity.this, chessboardIds[chessboardIds.length - 1]);
								startActivity(intent);
							}
						}
					});
			startActivity(intent);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		 doBack();
	}
	
	private void doBack() {
		if(chessboardIds.length > 1) {
			long[] prev = chessboardIds;
			chessboardIds = new long[prev.length - 1];
			for(int i = 0; i < prev.length - 1; i++) {
				chessboardIds[i] = prev[i];
			}
			updateChessboardFragment();
		} else {
			Intent intent = TestLockActivity.getStartIntent(this,
					new TestLockActivity.OnTestListener() {
						@Override
						public void onTest(boolean passed) {
							if (passed == true) {
								ChessboardActivity.this.finish();
							}
						}
					});
			startActivity(intent);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		ImageLoader.getInstance().cleanPictures();

		TtsSoundPool.release(); 
		
		if (ChessboardApplication.getDisableLockMode() == true || true) {
			ActivityUtils.clearWakeLock();
		}
		ActivityUtils.clearKeyguard();
	}
	
	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if(view instanceof AacButton) {
			AacButton ab = (AacButton) view;
			ab.setOnClickListener(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}
	
	private Chessboard readChessboard(ChessboardDbUtility dbu) {
		Chessboard cb = null;
		ChessboardCursor cursorCb = dbu.getCursorOnChessboard(chessboardIds[chessboardIds.length - 1]);
		if (cursorCb != null) {
			while (cursorCb.moveToNext()) {
				cb = cursorCb.getChessboard();
			}
			cursorCb.close();
		}
		return cb;
	}

	private Cell[] readCells(ChessboardDbUtility dbu) {
		LinkedList<Cell> cells = new LinkedList<Cell>();
		CellCursor cursor = dbu.getCursorOnCell(chessboardIds[chessboardIds.length - 1]);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				cells.add(cursor.getCell());
			}
			cursor.close();
		}
		return cells.toArray(new Cell[cells.size()]);
	}

	private void updateChessboardFragment() {
		// read again from database
		ChessboardDbUtility dbu = new ChessboardDbUtility(this);
		dbu.openWritable();
		Chessboard cb = readChessboard(dbu);
		// if deleted use root chessboard (or when not found!)
		if(cb == null)  { 
			chessboardIds = new long[1];
			chessboardIds[0] = dbu.getRootChessboardId();
			cb = readChessboard(dbu);
		}
		Cell[] cells = readCells(dbu);
		dbu.close();

		
		// replace chessboard
		unbindDrawables(container);
		container.removeAllViews();
		ImageLoader.getInstance().cleanPictures();
		TableLayout cbLayout = ChessboardLayout.newChessboardLayout(cb, cells, this);
		container.addView(cbLayout);
		
		// change title
		ActivityUtils.changeActionBarTitle(this, cb.getName());
		
		if(ChessboardApplication.DEBUG_HEAP_LOG) {
			ActivityUtils.logHeap();
		}
//		ChessboardFragment cbFragment = ChessboardFragment
//				.newChessboardFragment(cb, cells);
//		getSupportFragmentManager().beginTransaction()
//				.replace(R.id.container, cbFragment).commit();
	}
}
