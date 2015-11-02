package com.whatdoyouwanttodo.config;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whatdoyouwanttodo.ChessboardFragment;
import com.whatdoyouwanttodo.ChessboardLayout;
import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.db.CellCursor;
import com.whatdoyouwanttodo.db.ChessboardCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.ui.QuestionDialog;
import com.whatdoyouwanttodo.utils.ActivityUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;

/**
 * Attivita' che permette di configurare una tabella di celle
 */
public class GridConfigActivity extends ActionBarActivity implements
		GridConfigFragment.OnChessboardChangeListener,
		ChessboardLayout.OnSecondaryCellEventListener,
		ChessboardLayout.OnCellEventListener {
	public static final String CHESSBOARD_ID = "com.whatdoyouwanttodo.config.GridConfigActivity.CHESSBOARD_ID";
	
	// change in swap/normal mode
	private TextView previewText;
	private LinearLayout previewContainer;

	// for swap mode
	private boolean swapMode;
	private boolean previousCell;
	private int previousRow;
	private int previousColumn;
	private Button swapModeOff;
	
	// cells and chessboard to modify
	private FlexibleCellGrid flexGrid; // handle cells
	private Chessboard chessboard;
	
	// for packing feature
	private int lastWidth;
	
	// true after cell config activity has changed a cell
	private boolean cellChanged = false;
	
	private boolean gridDirtyFlag = false;

	public static Intent getStartIntent(Activity caller, long chessboardId) {
		Intent intent = new Intent(caller, GridConfigActivity.class);
		intent.putExtra(GridConfigActivity.CHESSBOARD_ID, chessboardId);
		return intent;
	}
	
	public static long NO_GRID_ID = -1;
	private static long currentGridId = NO_GRID_ID;

	public static long getCurrentGrid() {
		long gridId = currentGridId;
		currentGridId = NO_GRID_ID;
		return gridId;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get chessboard number
		Intent intent = getIntent();
		long id = intent.getLongExtra(CHESSBOARD_ID, 0);
		
		if(ChessboardApplication.DEBUG_MODE_IN_OUT_ACTIVITY) {
			Log.d(getClass().getName(), "input: " + id);
		}

		// read from database
		readChessboardWithCells(id);

		setContentView(R.layout.activity_grid_config);

		if (savedInstanceState == null) {
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			GridConfigFragment gridConfigFragment = GridConfigFragment
					.newGridConfigFragment(chessboard);
			transaction.add(R.id.config_container, gridConfigFragment);
			Cell[] configCells = flexGrid.getConfigCells();
			ChessboardFragment chessboardFragment = ChessboardFragment
					.newChessboardFragmentWithConfigButtons(chessboard, configCells, true, true);
			transaction.add(R.id.chessboard_container, chessboardFragment);
			transaction.commit();
		}

		previewText = (TextView) findViewById(R.id.grid_config_preview_text);
		previewContainer = (LinearLayout) findViewById(R.id.grid_config_preview_container);
		swapMode = false;
		previousCell = false;
		
		swapModeOff = (Button) findViewById(R.id.btn_mode_swap_off);
		swapModeOff.setVisibility(View.INVISIBLE);
		swapModeOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				changeChessboard(chessboard, false);
			}
		});
		
		currentGridId = chessboard.getId();
		gridDirtyFlag = false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(cellChanged == true) {
			// if resume after CellGridConfig is called refresh chessboard
			cellChanged = false;
			changeChessboard(chessboard, swapMode);
		}
		
		if(AllGridViewActivity.haveReturnParams()) {
			long id = AllGridViewActivity.getReturnParams().getId();
			
			if(id != chessboard.getId()) {
				// change chessboard
				readChessboardWithCells(id);
				changeChessboard(chessboard, false);
				
				// replace chessboard config
				GridConfigFragment gridConfigFragment = GridConfigFragment
						.newGridConfigFragment(chessboard);
				FragmentManager manager = getSupportFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.replace(R.id.config_container, gridConfigFragment);
				transaction.commit();
				
				currentGridId = chessboard.getId();
				gridDirtyFlag = false;
			}
		}
	}

	@Override
	public void onCellEvent(Chessboard chessboard, int row, int column, long param) {
		if (swapMode == true) {
			if (previousCell == false) {
				previousRow = row;
				previousColumn = column;
				previousCell = true;
				
				Resources res = getResources();
				previewText.setText(res.getString(R.string.activity_grid_config_swap1));
			} else {
				// change cells
				if (previousRow == row && previousColumn == column) {
					// do nothing
				} else {
					// swap cells
					flexGrid.swapCells(previousRow, previousColumn, row, column);
				}
				previousCell = false;
				
				previewText.setText(getResources().getString(
						R.string.activity_grid_config_swap_mode));
				changeChessboard(chessboard, swapMode);
			}
		} else {
			// if cell not exist do nothing
			if(flexGrid.existCell(row, column) == true) {
				// open single cell configuration
				Intent intent = CellGridConfigActivity.getStartIntent(this, row, column, flexGrid);
				this.cellChanged = true;
				startActivity(intent);
				
				// normally hidden behind the new activity
				// changeChessboard(chessboard, swapMode);
			}
		}
	}

	@Override
	public void onSecondaryCellEvent(boolean longTouch, Chessboard chessboard, int row,
			int column, long param) {
		if(longTouch == false) {
			if (flexGrid.existCell(row, column) == false) {
				// create cell
				flexGrid.createCellIfNotExist(this, row, column);
			} else {
				// delete cell
				flexGrid.deleteCell(row, column);
			}
			changeChessboard(chessboard, swapMode);
		} else {
			changeChessboard(chessboard, true);
		}
	}

	@Override
	public void onChessboardChange(Chessboard chessboard) {
		changeChessboard(chessboard, swapMode);
	}
	
	private void readChessboardWithCells(long id) {
		ChessboardDbUtility dbu = new ChessboardDbUtility(this);
		dbu.openReadable();

		chessboard = null;
		ChessboardCursor cursorCb = dbu.getCursorOnChessboard(id);
		if (cursorCb != null) {
			while (cursorCb.moveToNext()) {
				chessboard = cursorCb.getChessboard();
			}
			cursorCb.close();
		}
		lastWidth = chessboard.getColumnCount();

		LinkedList<Cell> cells = new LinkedList<Cell>();
		CellCursor cursor = dbu.getCursorOnCell(id);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				cells.add(cursor.getCell());
			}
			cursor.close();
		}
		flexGrid = new FlexibleCellGrid(cells, chessboard.getId());

		dbu.close();
	}

	private void changeChessboard(Chessboard chessboard, boolean swapMode) {
		gridDirtyFlag = true;
		
		// set swap mode
		if (swapMode == true) {
			if (this.swapMode == false) {
				swapModeOff.setVisibility(View.VISIBLE);
				previewText.setText(getResources().getString(
						R.string.activity_grid_config_swap_mode));
				previewText
						.setTextColor(getResources().getColor(R.color.black));
				previewContainer.setBackgroundColor(getResources().getColor(
						R.color.grid_preview_color_swap_mode));
				this.swapMode = true;
			}
		} else {
			if (this.swapMode == true) {
				swapModeOff.setVisibility(View.INVISIBLE);
				previewText.setText(getResources().getString(
						R.string.activity_grid_config_preview));
				previewText
						.setTextColor(getResources().getColor(R.color.white));
				previewContainer.setBackgroundColor(getResources().getColor(
						R.color.grid_preview_color_normal));
				previousCell = false;
				this.swapMode = false;
			}
		}
		
		// pack cell when size changed
		if (lastWidth != chessboard.getColumnCount()) {
			lastWidth = chessboard.getColumnCount();
			flexGrid.packCells(chessboard.getColumnCount());
		}

		// update chessboard
		ImageLoader.getInstance().cleanPictures();
		Cell[] configCells = flexGrid.getConfigCells();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		ChessboardFragment chessboardFragment = ChessboardFragment
				.newChessboardFragmentWithConfigButtons(chessboard, configCells, true, true);
		transaction.replace(R.id.chessboard_container, chessboardFragment);
		transaction.commit();

		if(ChessboardApplication.DEBUG_HEAP_LOG) {
			ActivityUtils.logHeap();
		}
		
		this.chessboard = chessboard;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid_config, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_grid_save) {
			saveConfig();
			finish();
			return true;
		} else if(id == R.id.action_overview) {
			if(gridDirtyFlag == true) {
				Resources res = getResources();
				QuestionDialog dialog = new QuestionDialog(
						res.getString(R.string.activity_grid_config_exit_dialog_message),
						res.getString(R.string.activity_grid_config_exit_dialog_yes),
						res.getString(R.string.activity_grid_config_exit_dialog_no),
						new QuestionDialog.QuestionDialogListener() {
	
							@Override
							public void onPositiveClick() {
								saveConfig();
								Intent intent = AllGridViewActivity.getStartIntentForOverview(
										GridConfigActivity.this, chessboard.getId());
								startActivity(intent);
							}
	
							@Override
							public void onNegativeClick() {
								Intent intent = AllGridViewActivity.getStartIntentForOverview(
										GridConfigActivity.this, chessboard.getId());
								startActivity(intent);
							}
						});
				dialog.show(this);
			} else {
				Intent intent = AllGridViewActivity.getStartIntentForOverview(
						GridConfigActivity.this, chessboard.getId());
				startActivity(intent);
			}
			
			return true;
		} else if(id == R.id.action_dismiss_chessboard) {
			finish();
			return true;
			/* TODO: for unlikely future use 
			// first grid is not removable
			if(chessboard.getId() == 1) {
				Resources res = getResources();
			    String title = res.getString(R.string.activity_grid_config_delete_root_grid);
			    String message = res.getString(R.string.activity_grid_config_delete_root_grid_message);
			    String confirm = res.getString(R.string.activity_grid_config_delete_root_grid_confirm);
				MessageDialog.showMessage(this, title, message, confirm);
				return true;
			}
			
			// grid with cells is not removable
			if (flexGrid.isEmpty() == false) {
				Resources res = getResources();
				String title = res.getString(R.string.activity_grid_config_delete_grid);
				String message = res.getString(R.string.activity_grid_config_delete_grid_message);
				String confirm = res.getString(R.string.activity_grid_config_delete_grid_confirm);
				MessageDialog.showMessage(this, title, message, confirm);
			} else {
				// remove grid after confirm
				Resources res = getResources();
				String title = res.getString(R.string.activity_grid_config_delete_grid_action);
				String message = res.getString(R.string.activity_grid_config_delete_grid_message_action);
				String confirm = res.getString(R.string.activity_grid_config_delete_grid_confirm_action);
				QuestionDialog dialog = new QuestionDialog(
						title, message, confirm,
						new QuestionDialog.QuestionDialogListener() {
							@Override
							public void onPositiveClick() {
								ChessboardDbUtility dbu = new ChessboardDbUtility(
										GridConfigActivity.this);
								dbu.openWritable();

								// delete chessboard
								dbu.deleteChessboard(chessboard.getId());

								// remove all cells
								flexGrid.writeOnDatabase(dbu);

								List<Cell> toUnlink = new LinkedList<Cell>();
								CellCursor cc = dbu.getCursorOnEveryCell();
								while (cc.moveToNext()) {
									Cell cell = cc.getCell();
									if (cell.getActivityType() == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD
											&& cell.getActivityParam() == chessboard
													.getId()) {
										toUnlink.add(cell);
									}
								}
								cc.close();
								Iterator<Cell> cIt = toUnlink.iterator();
								while (cIt.hasNext()) {
									Cell cell = cIt.next();

									dbu.updateCell(cell.getId(), cell.getChessboard(),
											cell.getName(), cell.getRow(),
											cell.getColumn(), cell.getBackgroundColor(),
											cell.getBorderWidth(), cell.getBorderColor(),
											cell.getText(), cell.getTextWidth(),
											cell.getTextColor(), cell.getImagePath(),
											cell.getAudioPath(), Cell.ACTIVITY_TYPE_NONE, 0);
								}
								dbu.close();

								finish();
							}

							@Override
							public void onNegativeClick() {
								// do nothing
							}
						});
				dialog.show(this);
			}
			
			return true;
			*/
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Ask the user if they want to save configuration
			Resources res = getResources();
			QuestionDialog dialog = new QuestionDialog(res.getString(
					R.string.activity_grid_config_exit_dialog_message),
					res.getString(R.string.activity_grid_config_exit_dialog_yes),
					res.getString(R.string.activity_grid_config_exit_dialog_no),
					new QuestionDialog.QuestionDialogListener() {	
							@Override
							public void onPositiveClick() {
								saveConfig();
								finish();
							}
							
							@Override
							public void onNegativeClick() {
								finish();
							}
						});
			dialog.show(this);

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}	
	
	private void saveConfig() {
		// debug code
		// dbu.close();
		// dbu.printAllDatabaseForDebug();
		// dbu.openWritable();
		
		ChessboardDbUtility dbu = new ChessboardDbUtility(this);
		dbu.openWritable();
		
		dbu.updateChessboard(chessboard.getId(), chessboard.getParentId(),
				chessboard.getName(), chessboard.getRowCount(),
				chessboard.getColumnCount(), chessboard.getBackgroundColor(),
				chessboard.getBorderWidth());
		
		// update cells
		flexGrid.writeOnDatabase(dbu);

		dbu.close();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(ChessboardApplication.DEBUG_MODE_IN_OUT_ACTIVITY) {
			Log.d(getClass().getName(), "output: " + flexGrid.toString());
		}
	}
}
