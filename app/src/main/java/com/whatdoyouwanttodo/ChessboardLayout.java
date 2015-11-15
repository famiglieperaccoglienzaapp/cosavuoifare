package com.whatdoyouwanttodo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.whatdoyouwanttodo.actions.CallCallbackAction;
import com.whatdoyouwanttodo.actions.CallSecondaryCallbackAction;
import com.whatdoyouwanttodo.actions.GoBackAction;
import com.whatdoyouwanttodo.actions.StartActiveListeningAction;
import com.whatdoyouwanttodo.actions.StartChessboardAction;
import com.whatdoyouwanttodo.actions.StartAbrakadabraAction;
import com.whatdoyouwanttodo.actions.StartVideoAction;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.utils.ArrayUtils;
import com.whatdoyouwanttodo.utils.FileUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;
import com.whatdoyouwanttodo.utils.TtsSoundPool;

/**
 * Rappresenta una tabella AAC, e' usato da ChessboardFragment per creare il layout
 */
public class ChessboardLayout {
	public final static String CHESSBOARD = "com.whatdoyouwanttodo.ChessboardFragment.CHESSBOARD";
	public final static String CELL_ARRAY = "com.whatdoyouwanttodo.ChessboardFragment.CELL_ARRAY";
	public static final String SECONDARY_BUTTONS = "com.whatdoyouwanttodo.ChessboardLayout.SECONDARY_BUTTONS";
	public static final String TARGET_DRAWABLE = "com.whatdoyouwanttodo.ChessboardLayout.TARGET_DRAWABLE";

	protected Chessboard chessboard;
	protected boolean configMode;
	protected OnCellEventListener callback;
	protected OnSecondaryCellEventListener secondaryCallback;
	protected Activity activity;
	protected Bundle arguments;

	public ChessboardLayout() {
		chessboard = null;
		configMode = false;
		callback = null;
		secondaryCallback = null;
		activity = null;
		arguments = null;
	}

	public static TableLayout newChessboardLayout(Chessboard chessboard,
			Cell[] cells, Activity activity) {
		ChessboardLayout layout = new ChessboardLayout();

		Bundle arguments = new Bundle();
		arguments.putParcelable(CHESSBOARD, chessboard);
		arguments.putParcelableArray(CELL_ARRAY, cells);
		layout.setArguments(arguments);
		layout.setActivity(activity);

		TableLayout tableLayout = (TableLayout) layout.createView();

		return tableLayout;
	}

	private void setArguments(Bundle arguments) {
		this.arguments = arguments;
	}

	private void setActivity(Activity activity) {
		this.activity = activity;
	}

	public interface OnCellEventListener {
		public void onCellEvent(Chessboard chessboard, int row, int column, long param);
	}
	
	public interface OnSecondaryCellEventListener {
		public void onSecondaryCellEvent(boolean longTouch, Chessboard chessboard, int row, int column, long param);
	}

	public View createView() {
		chessboard = arguments.getParcelable(CHESSBOARD);
		int nRow = chessboard.getRowCount();
		int nColumn = chessboard.getColumnCount();
		Cell[] cells = (Cell[]) arguments.getParcelableArray(CELL_ARRAY);
		cells = ArrayUtils.sortInTableOrder(cells);
		// check and warning on duplicate cells
		if(ChessboardApplication.DEBUG_RAPID_DOUBLE_CELL_FIX) {
			int prevLen = cells.length;
			cells = ArrayUtils.fixSortInTableOrder(cells);
			if(prevLen != cells.length) {
				Log.w(getClass().getName(), "fixed " + cells.length + " " + prevLen);
			}
		}
		
		boolean secondaryButton = arguments.getBoolean(SECONDARY_BUTTONS, false);
		boolean showTargetDrawable = arguments.getBoolean(TARGET_DRAWABLE, false);

		TableLayout tableLayout = new TableLayout(activity);
		LayoutParams tableLayoutParam = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tableLayout.setLayoutParams(tableLayoutParam);
		tableLayout.setBackgroundColor(chessboard.getBackgroundColor());
		if (chessboard.getBorderWidth() > 0) {
			int padding = chessboard.getBorderWidth() / 2;
			tableLayout.setPadding(padding, padding, padding, padding);
		}
		tableLayout.setStretchAllColumns(true);
		int count = 0;
		for (int iRow = 0; iRow < nRow; iRow++) {
			TableRow tr = new TableRow(activity);
			TableLayout.LayoutParams rowLayoutParams = new TableLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 150, 0.3f);
			tr.setLayoutParams(rowLayoutParams);
			for (int iCol = 0; iCol < nColumn; iCol++) {
				if (count < cells.length) {
					if (iRow == cells[count].getRow()
							&& iCol == cells[count].getColumn()) {
						Cell cell = cells[count];
						
						int targetDrawable = -1;
						if (showTargetDrawable == true) {
							if (cell.getActivityType() == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD
									|| cell.getActivityType() == Cell.ACTIVITY_TYPE_CLOSE_CHESSBOARD) {
								targetDrawable = R.drawable.cell_link_to;
							} else if (cell.getActivityType() == Cell.ACTIVITY_TYPE_ABRAKADABRA) {
								targetDrawable = R.drawable.cell_music_slides;
							} else if (cell.getActivityType() == Cell.ACTIVITY_TYPE_ACTIVE_LISTENING) {
								targetDrawable = R.drawable.cell_active_listening;
							} else if (cell.getActivityType() == Cell.ACTIVITY_TYPE_PLAY_VIDEO) {
								targetDrawable = R.drawable.cell_play_video;
							}
						}
						AacButton button = createTableCell(activity,
								chessboard.getBorderWidth(), cell.getImagePath(), cell, false,
								secondaryButton, targetDrawable);

						if (configMode == false) {
							if (cell.getActivityType() == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD) {
								if (showTargetDrawable == false) {
									button.setOnClickListener(new StartChessboardAction(cell.getActivityParam(), chessboard.getId()));
								}
							} else if (cell.getActivityType() == Cell.ACTIVITY_TYPE_CLOSE_CHESSBOARD) {
								if (showTargetDrawable == false) {
									button.setOnClickListener(new GoBackAction());
								}
							} else if (cell.getActivityType() == Cell.ACTIVITY_TYPE_ABRAKADABRA) {
								button.setOnClickListener(new StartAbrakadabraAction(cell.getActivityParam()));
							} else if (cell.getActivityType() == Cell.ACTIVITY_TYPE_ACTIVE_LISTENING) {
								button.setOnClickListener(new StartActiveListeningAction(cell.getActivityParam()));
							} else if (cell.getActivityType() == Cell.ACTIVITY_TYPE_PLAY_VIDEO) {
								button.setOnClickListener(new StartVideoAction(cell.getActivityParam()));
							} else if (cell.getActivityType() == Cell.ACTIVITY_TYPE_NONE) {
								// do nothing
							}
						} else {
							button.setOnClickListener(new CallCallbackAction(
									this.callback, this.chessboard,
									cell.getRow(), cell.getColumn(),
									cell.getActivityParam()));
							if (secondaryButton) {
								button.setOnLongClickListener(new CallSecondaryCallbackAction(
										true, this.secondaryCallback, this.chessboard,
										iRow, iCol, 0));
								button.setSecondaryOnClickListener(new CallSecondaryCallbackAction(
										false, this.secondaryCallback, this.chessboard,
										cell.getRow(), cell.getColumn(),
										cell.getActivityParam()));
							}
						}
						tr.addView(button);

						count++;
					} else {
						AacButton defaultButton = createTableCell(activity,
								chessboard.getBorderWidth(), null, null, true,
								secondaryButton, -1);
						if (configMode == true)
							defaultButton
									.setOnClickListener(new CallCallbackAction(
											this.callback, this.chessboard, iRow, iCol, 0));
						if (secondaryButton) {
							defaultButton.setOnLongClickListener(new CallSecondaryCallbackAction(
									true, this.secondaryCallback, this.chessboard,
									iRow, iCol, 0));
							defaultButton.setSecondaryOnClickListener(new CallSecondaryCallbackAction(
									false, this.secondaryCallback, this.chessboard,
									iRow, iCol, 0));
						}
						tr.addView(defaultButton);
					}
				} else {
					AacButton defaultButton = createTableCell(activity,
							chessboard.getBorderWidth(), null, null, true,
							secondaryButton, -1);
					if (configMode == true)
						defaultButton
								.setOnClickListener(new CallCallbackAction(
										this.callback, this.chessboard, iRow, iCol, 0));
					if (secondaryButton) {
						defaultButton.setOnLongClickListener(new CallSecondaryCallbackAction(
								true, this.secondaryCallback, this.chessboard,
								iRow, iCol, 0));
						defaultButton.setSecondaryOnClickListener(new CallSecondaryCallbackAction(
								false, this.secondaryCallback, this.chessboard,
								iRow, iCol, 0));
					}
					tr.addView(defaultButton);
				}
			}
			tableLayout.addView(tr);
		}
		return tableLayout;
	}

	private AacButton createTableCell(Activity activity, int padding,
			String imagePath, Cell cell, boolean placeholder,
			boolean secondaryButtons, int showTargetDrawable) {
		AacButton aacButton = null;
		if(placeholder == true) {
			aacButton = new AacButton(activity, placeholder);
			aacButton.setImageResource(R.drawable.cell_no_image_high);
		} else {
			aacButton = new AacButton(activity, cell.getText(),
					cell.getBackgroundColor(), cell.getBorderWidth(),
					cell.getBorderColor(), cell.getTextWidth(),
					cell.getTextColor());
			String audioPath = cell.getAudioPath();
			if (audioPath != null) {
				if (audioPath.equals("") == false) {
					if(audioPath.startsWith(Configurations.TTS_PREFIX)) {
						String textString = audioPath.substring(Configurations.TTS_PREFIX.length());
						int audioId = TtsSoundPool.loadTss(textString, activity);
						aacButton.setAudioTypeAndId(TtsSoundPool.TYPE_TTS, audioId);
					} else {
						String absAudioPath = FileUtils.getResourceFile(audioPath).toString();
						if (absAudioPath != null) {
							int audioId = TtsSoundPool.load(absAudioPath);
							aacButton.setAudioTypeAndId(TtsSoundPool.TYPE_FILE, audioId);
						}
					}
				}
			}
			if (imagePath != null) {
				if (ImageLoader.getInstance().existImage(imagePath) == true) {
					ImageLoader.getInstance().loadImageLazy(aacButton,
							imagePath);
				}
			}
		}

		TableRow.LayoutParams layoutParams;
		layoutParams = new TableRow.LayoutParams(50, LayoutParams.MATCH_PARENT);
		if (padding > 0) {
			int hPadding = padding / 2;
			layoutParams.setMargins(hPadding, hPadding, hPadding, hPadding);
		}
		aacButton.setLayoutParams(layoutParams);
		
		if(secondaryButtons || showTargetDrawable >= 0) {
			// cell link_to
			// cell active listening
			// cell_play_video
			// cell_music_slides
			aacButton.setConfigButton(secondaryButtons, showTargetDrawable);
		}

		return aacButton;
	}
}