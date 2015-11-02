package com.whatdoyouwanttodo.ui;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.whatdoyouwanttodo.application.ChessboardApplication;

public class ChooseButtonTableHelper {
	private int selectedDrawable;
	private int unselectedDrawable;
	private View[][] views;
	private boolean[][] selectedViews;
	private int rowPosition;
	private int columnPosition;
	private int defaultRow;
	private int defaultColumn;
	private OnChooseClickListenerDouble listener;
	
	public static interface OnChooseClickListenerDouble {
		void onClick(View view, int row, int column);	
	}

	public ChooseButtonTableHelper(int rows, int columns) {
		views = new View[rows][];
		selectedViews = new boolean[rows][];
		for(int i = 0; i < views.length; i++) {
			views[i] = new View[columns];
			selectedViews[i] = new boolean[columns];
		}
		rowPosition = 0;
		columnPosition = 0;
	}

	public void setDrawables(int selected, int unselected) {
		this.selectedDrawable = selected;
		this.unselectedDrawable = unselected;
	}

	public void addButton(View view) {
		views[rowPosition][columnPosition] = view;
		selectedViews[rowPosition][columnPosition] = false;
		columnPosition++;
	}

	public void addButton(View view, boolean newLine) {
		if (newLine == false) {
			addButton(view);
		} else {
			views[rowPosition][columnPosition] = view;
			selectedViews[rowPosition][columnPosition] = false;
			rowPosition++;
			columnPosition = 0;
		}
	}

	public void setDefaultButtonWithValues(int row, int column) {
		this.defaultRow = row;
		this.defaultColumn = column;
		
		// check default value found
		if (ChessboardApplication.DEBUG_MODE) {
			if (!(row >= 0 && row < views.length && column >= 0
					&& column < views[0].length)) {
				Log.e("not assigned cell", "values: " + row + " " + column);
			}
		}
	}

	public void setListener(OnChooseClickListenerDouble listener) {
		this.listener = listener;
	}

	public void initAll() {
		// set listener
		for (int i = 0; i < views.length; i++) {
			for (int j = 0; j < views[i].length; j++) {
				views[i][j].setOnClickListener(new OnChooseTableButtonSelection(
						unselectedDrawable, selectedDrawable, views, selectedViews,
						listener));
				if (i < defaultRow && j < defaultColumn) {
					views[i][j].setBackgroundResource(selectedDrawable);
					selectedViews[i][j] = true;
				} else {
					views[i][j].setBackgroundResource(unselectedDrawable);
				}
			}
		}
	}

	class OnChooseTableButtonSelection implements OnClickListener {
		private int emptyDrawable;
		private int cellDrawable;
		private View[][] views;
		private boolean[][] selectedViews;
		private OnChooseClickListenerDouble listener;

		public OnChooseTableButtonSelection(int emptyDrawable,
				int cellDrawable, View[][] views, boolean[][] selectedViews,
				OnChooseClickListenerDouble listener) {
			this.emptyDrawable = emptyDrawable;
			this.cellDrawable = cellDrawable;
			this.views = views;
			this.selectedViews = selectedViews;
			this.listener = listener;
		}

		@Override
		public void onClick(View view) {
			// localize button row and column
			int row = 0;
			int column = 0;
			for (int i = 0; i < views.length; i++) {
				for (int j = 0; j < views[i].length; j++) {
					if (view == views[i][j]) {
						row = i + 1;
						column = j + 1;
					}
				}
			}

			for (int i = 0; i < views.length; i++) {
				for (int j = 0; j < views[i].length; j++) {
					if (i < row && j < column) {
						if (selectedViews[i][j] == false) {
							views[i][j].setBackgroundResource(cellDrawable);
							selectedViews[i][j] = true;
						}
					} else {
						if (selectedViews[i][j] == true) {
							views[i][j].setBackgroundResource(emptyDrawable);
							selectedViews[i][j] = false;
						}
					}
				}
			}

			listener.onClick(view, row, column);
		}
	}
}
