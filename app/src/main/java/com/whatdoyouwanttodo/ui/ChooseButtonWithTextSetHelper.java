package com.whatdoyouwanttodo.ui;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatdoyouwanttodo.application.ChessboardApplication;

public class ChooseButtonWithTextSetHelper {
	private int[] drawables;
	private int[] selectedDrawables;
	private ViewGroup[] views;
	private ImageView[] imageViews;
	private TextView[] textViews;
	private int[] defaultText;
	private int[] values;
	private int position;
	
	private int lastSelectedPosition;
	
	private OnChooseClickListener listener;
	
	public static interface OnChooseClickListener {
		void onClick(View view, int value);
	}

	public ChooseButtonWithTextSetHelper(int size) {
		views = new ViewGroup[size];
		imageViews = new ImageView[size];
		textViews = new TextView[size];
		defaultText = new int[size];
		drawables = new int[size];
		selectedDrawables = new int[size];
		values = new int[size];
		position = 0;
	}

	public void addButton(ViewGroup view, View imageView, View textView, int text, int value, int drawable, int selectedDrawable) {
		views[position] = view;
		imageViews[position] = (ImageView) imageView;
		textViews[position] = (TextView) textView;
		defaultText[position] = text;
		values[position] = value;
		drawables[position] = drawable;
		selectedDrawables[position] = selectedDrawable;
		position++;
	}

	public void setDefaultButtonWithValue(int value, String name) {
		for(int i = 0; i < values.length; i++) {
			if(values[i] == value) {
				imageViews[i].setImageResource(selectedDrawables[i]);
				textViews[i].setText(name);
				views[i].setBackgroundColor(Color.LTGRAY);
				return;
			}
		}
		
		// check default value found
		if (ChessboardApplication.DEBUG_MODE) {
			Log.e("not assigned cell", "value: " + value);
		}
	}

	public void selectWithValue(int type, String param) {
		// deselect previous buttons
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i].setImageResource(drawables[i]);
			textViews[i].setText("");
			views[i].setBackgroundColor(Color.WHITE);
		}
	
		if(type >= 0) {
			// select button
			imageViews[lastSelectedPosition].setImageResource(selectedDrawables[lastSelectedPosition]);
			if (param != null) {
				textViews[lastSelectedPosition].setText(param);
				views[lastSelectedPosition].setBackgroundColor(Color.LTGRAY);
				textViews[lastSelectedPosition].setText(param);
			}
		}
	}

	public void setListener(OnChooseClickListener listener) {
		this.listener = listener;
		if(views != null) {
			for (int i = 0; i < views.length; i++) {
				views[i].setClickable(true);
				views[i].setOnClickListener(new OnChooseButtonSelection(i, values[i], listener));
			}
		}
	}

	public void initAll() {
		for (int i = 0; i < views.length; i++) {
			views[i].setClickable(true);
			views[i].setOnClickListener(new OnChooseButtonSelection(i,
					values[i], listener));
		}
	}

	private class OnChooseButtonSelection implements OnClickListener {
		private int selectedPos;
		private int selectedValue;
		private OnChooseClickListener listener;

		public OnChooseButtonSelection(int selectedPos, int selectedValue,
				OnChooseClickListener listener) {
			this.selectedPos = selectedPos;
			this.selectedValue = selectedValue;
			this.listener = listener;
		}

		@Override
		public void onClick(View view) {
			lastSelectedPosition = selectedPos;
			listener.onClick(view, selectedValue);
		}
	}
}
