package com.whatdoyouwanttodo.ui;

import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.whatdoyouwanttodo.application.ChessboardApplication;

public class ChooseButtonSetHelper {
	private ArrayList<Item> items;
	private int defaultPos;
	private OnChooseClickListener listener;
	
	public static interface OnChooseClickListener {
		public void onClick(View view, int id);
	}
	
	private static class Item {
		private int drawable;
		private int selectedDrawable;
		private View view;
		private boolean selected;
		private int value;

		public Item(int drawable, int selectedDrawable, View view,
				boolean selected, int value) {
			this.drawable = drawable;
			this.selectedDrawable = selectedDrawable;
			this.view = view;
			this.selected = selected;
			this.value = value;
		}

		public int getDrawable() {
			return drawable;
		}

		public int getSelectedDrawable() {
			return selectedDrawable;
		}

		public View getView() {
			return view;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public boolean isSelected() {
			return selected;
		}

		public int getValue() {
			return value;
		}
	}

	public ChooseButtonSetHelper() {
		this.items = new ArrayList<Item>();
	}

	public void addButton(View view, int value, int drawable, int selectedDrawable) {
		items.add(new Item(drawable, selectedDrawable, view, false, value));
	}

	public void setDefaultButtonWithValue(int value) {
		for(int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			if(item.getValue() == value) {
				defaultPos = i;
				item.setSelected(true);
				return;
			}
		}
		
		// check default value found
		if (ChessboardApplication.DEBUG_MODE) {
			Log.e("not assigned cell", "value: " + value);
		}
	}

	public void setListener(OnChooseClickListener listener) {
		this.listener = listener;
	}

	public void initAll() {
		for (int i = 0; i < items.size(); i++) {
			View view = items.get(i).getView();
			view.setOnClickListener(new OnChooseButtonSelection(items, i, listener));
		}
		Item selItem= items.get(defaultPos);
		selItem.getView().setBackgroundResource(selItem.getSelectedDrawable());
	}

	private class OnChooseButtonSelection implements OnClickListener {
		private ArrayList<Item> items;
		private int selectedPos;
		private OnChooseClickListener listener;

		public OnChooseButtonSelection(ArrayList<Item> items, int selectedPos, OnChooseClickListener listener) {
			this.items = items;
			this.selectedPos = selectedPos;
			this.listener = listener;
		}

		@Override
		public void onClick(View view) {
			// deselect previous buttons
			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);
				if (item.isSelected()) {
					// check if same view
					if (item.getView() == view)
						return;

					item.getView().setBackgroundResource(item.getDrawable());
					item.setSelected(false);
				}
			}

			// select button
			Item selItem = items.get(selectedPos);
			view.setBackgroundResource(selItem.getSelectedDrawable());
			selItem.setSelected(true);
			listener.onClick(view, selItem.getValue());
		}
	}
}
