package com.whatdoyouwanttodo.config;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.utils.ArrayUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;

/**
 * Mostra un insieme di tabelle, usato da AllGridViewActivity
 */
public class AllGridViewFragment extends Fragment {
	public static final String CHESSBOARD_ARRAY = "com.whatdoyouwanttodo.config.AllGridViewFragment.CHESSBOARD_ARRAY";
	public static final String CELL_ARRAY = "com.whatdoyouwanttodo.config.AllGridViewFragment.CELL_ARRAY";
	public static final String WITH_LINKS = "com.whatdoyouwanttodo.config.AllGridViewFragment.WITH_LINKS";

	public static AllGridViewFragment newAllGridViewFragment(Chessboard[] chessboards, Cell[] cells, boolean withLinks) {
		AllGridViewFragment fragment = new AllGridViewFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelableArray(CHESSBOARD_ARRAY, chessboards);
		arguments.putParcelableArray(CELL_ARRAY, cells);
		arguments.putBoolean(WITH_LINKS, withLinks);
		fragment.setArguments(arguments);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// get params
		Bundle arguments = getArguments();
		Chessboard[] chessboards = (Chessboard[]) arguments.getParcelableArray(CHESSBOARD_ARRAY);
		Cell[] cells = (Cell[]) arguments.getParcelableArray(CELL_ARRAY);
		boolean withLinks = arguments.getBoolean(WITH_LINKS);
		ArrayUtils.sortInIdOrder(chessboards);
		ArrayUtils.sortInChessboardIdOrder(cells);
		if (withLinks == true) {
			// add new chessboard
			Chessboard[] newChessboards = new Chessboard[chessboards.length + 1];
			for (int i = 0; i < chessboards.length; i++) {
				newChessboards[i] = chessboards[i];
			}

			Constants constants = Constants.getInstance(getActivity());

			Chessboard newCb = constants.NEW_CHESSBOARD.clone();
			newCb.setId(AllGridViewActivity.SELECTED_NEW_CHESSBOARD);
			newChessboards[newChessboards.length - 1] = newCb;

			/*
			Chessboard backCb = constants.NEW_CHESSBOARD.clone();
			backCb.setName(constants.BACK_CHESSBOARD_NAME);
			backCb.setId(AllGridViewActivity.SELECTED_BACK_CHESSBOARD);
			newChessboards[1] = backCb;
			*/

			chessboards = newChessboards;
		}

		ImageView[] cbFragments = new ImageView[chessboards.length];
		int c = 0;
		for (int cb = 0; cb < chessboards.length; cb++) {
			Chessboard chessboard = chessboards[cb];
			long cbId = chessboard.getId();

			int ce = c;
			boolean cond = ce < cells.length;
			if (cond == true)
				cond = cond && cells[ce].getChessboard() == cbId;
			while (cond) {
				ce++;
				cond = ce < cells.length;
				if (cond == true)
					cond = cond && cells[ce].getChessboard() == cbId;
			}

			// create chessboard
			Cell[] cbCells = ArrayUtils.copyOfRange(cells, c, ce);
			for(int i = 0; i < cbCells.length; i++) {
				cbCells[i].setActivityType(Cell.ACTIVITY_TYPE_NONE);
			}
			if (chessboard.getId() == AllGridViewActivity.SELECTED_NEW_CHESSBOARD) {
				cbFragments[cb] = new ImageView(getActivity());
				cbFragments[cb].setImageResource(R.drawable.cell_chessboard_new_high);
			} else if (chessboard.getId() == AllGridViewActivity.SELECTED_BACK_CHESSBOARD) {
				cbFragments[cb] = new ImageView(getActivity());
				cbFragments[cb].setImageResource(R.drawable.cell_chessboard_back_high);
			} else {
				String tnPath = ChessboardThumbnailManager.getInstance(getActivity()).getThumbnailPathOf(getActivity(), chessboard, cbCells);
				cbFragments[cb] = new ImageView(getActivity());
				ImageLoader.getInstance().loadImageLazy(cbFragments[cb], tnPath);
			}
            cbFragments[cb].setBackgroundColor(getResources().getColor(R.color.soft_black));
            int layoutHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ChessboardThumbnailManager.THUMBNAIL_HEIGHT, getResources().getDisplayMetrics());
			cbFragments[cb].setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, layoutHeight));

			c = ce;
		}

		// construct table layout
		FragmentActivity fragmentActivity = getActivity();
		TableLayout tableLayout = new TableLayout(fragmentActivity);
		tableLayout.setStretchAllColumns(true);
		int rowCount = 0;
		TableRow tableRow = null;
		for (int cb = 0; cb < chessboards.length; cb++) {
			Chessboard chessboard = chessboards[cb];

			if (rowCount == 0) {
				tableRow = new TableRow(fragmentActivity);
				TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				if (cb > 0) {
					layoutParams.topMargin = getActivity().getResources().getDimensionPixelSize(R.dimen.activity_config_space_small);
				}
				tableRow.setLayoutParams(layoutParams);
			}

			LinearLayout linearLayout = new LinearLayout(fragmentActivity);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT);
			if (rowCount > 0) {
				layoutParams.leftMargin = getActivity().getResources().getDimensionPixelSize(R.dimen.activity_config_space_small);
			}
			linearLayout.setLayoutParams(layoutParams);
			linearLayout.setClickable(true);
			OpenChessboardListener openChessboardListener = new OpenChessboardListener(chessboard.getId());
			linearLayout.setOnClickListener(openChessboardListener);

			TextView textView = new TextView(fragmentActivity, null, android.R.attr.textAppearanceLarge);
			textView.setText(chessboard.getName());
			textView.setTextColor(Color.BLACK);
			textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			linearLayout.addView(textView);
			linearLayout.addView(cbFragments[cb]);

			linearLayout.setOnClickListener(openChessboardListener);
			tableRow.addView(linearLayout);
			rowCount++;

			if (rowCount >= 2) {
				tableLayout.addView(tableRow);
				rowCount = 0;
			}
		}
		if (rowCount > 0) {
			tableLayout.addView(tableRow);
		}

		return tableLayout;
	}

	private class OpenChessboardListener implements OnClickListener {
		private long id;

		public OpenChessboardListener(long id) {
			this.id = id;
		}

		@Override
		public void onClick(View view) {
			if(id == AllGridViewActivity.SELECTED_NEW_CHESSBOARD) {
				Activity activity = getActivity();
				Resources res = activity.getResources();
				String messageStr = res.getString(R.string.activity_all_grid_view_new_table_message);
				String confirmStr = res.getString(R.string.activity_all_grid_view_new_table_confirm);
				String backStr = res.getString(R.string.activity_all_grid_view_new_table_back);
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage(messageStr);
				final EditText inputText = new EditText(activity);
				builder.setView(inputText);
				builder.setPositiveButton(confirmStr, new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Activity fragmentActivity = getActivity();
						fragmentActivity.finish();
						AllGridViewActivity.ret = new AllGridViewActivity.AllGridViewReturn(id);
						AllGridViewActivity.ret.setName(inputText.getText().toString());
					}
				});
				builder.setNegativeButton(backStr, new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
 			} else {
				Activity fragmentActivity = getActivity();
				fragmentActivity.finish();
				AllGridViewActivity.ret = new AllGridViewActivity.AllGridViewReturn(id);
 			}
		}
	}
}