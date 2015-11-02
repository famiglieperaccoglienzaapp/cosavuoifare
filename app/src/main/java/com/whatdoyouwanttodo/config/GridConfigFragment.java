package com.whatdoyouwanttodo.config;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.ui.ChooseButtonSetHelper;
import com.whatdoyouwanttodo.ui.ChooseButtonSetHelper.OnChooseClickListener;
import com.whatdoyouwanttodo.ui.ChooseButtonTableHelper;

/**
 * Pannello di configurazione per una tabella di celle, usato da GridConfigActivity
 */
public class GridConfigFragment extends Fragment {
	private static final String CHESSBOARD = "com.whatdoyouwanttodo.config.GridConfigFragment.CHESSBOARD";
	
	private Chessboard chessboard;
	private OnChessboardChangeListener callback;
	
	public static GridConfigFragment newGridConfigFragment(
			Chessboard chessboard) {
		GridConfigFragment fragment = new GridConfigFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelable(CHESSBOARD, chessboard);
		fragment.setArguments(arguments);

		return fragment;
	}

	public interface OnChessboardChangeListener {
		public void onChessboardChange(Chessboard chessboard);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			callback = (OnChessboardChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement " + OnChessboardChangeListener.class.getName());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_grid_config,
				container, false);

		Bundle arguments = getArguments();
		this.chessboard = arguments.getParcelable(CHESSBOARD);
		
		/*
		// tutorial
		if (ChessboardApplication.getGridTuturial() == false) {
			View name = rootView.findViewById(R.id.tutorial_name);
			View text = rootView.findViewById(R.id.tutorial_text);
			View confirm = rootView.findViewById(R.id.tutorial_ok);
			name.setVisibility(View.GONE);
			text.setVisibility(View.GONE);
			confirm.setVisibility(View.GONE);
		} else {
			View confirm = rootView.findViewById(R.id.tutorial_ok);
			confirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					View name = getActivity().findViewById(R.id.tutorial_name);
					View text = getActivity().findViewById(R.id.tutorial_text);
					View confirm = getActivity().findViewById(R.id.tutorial_ok);
					name.setVisibility(View.GONE);
					text.setVisibility(View.GONE);
					confirm.setVisibility(View.GONE);
					ChessboardApplication.setGridTutorial(false);
					ChessboardDbUtility dbu = new ChessboardDbUtility(getActivity());
					dbu.openWritable();
					dbu.setGridTutorial(false);
					dbu.close();
				}
			});
		}
		*/

		// set up text name
		EditText gridName = (EditText) rootView.findViewById(R.id.et_grid_name);
		gridName.setText(chessboard.getName());
		gridName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (isEditorAction(actionId)) {
	            	chessboard.setName(v.getText().toString());
					callback.onChessboardChange(chessboard);
	                
	                return false;
	            }
	            return false;
			}
	    });
		
		// set up color buttons
		ChooseButtonSetHelper colorHelper = new ChooseButtonSetHelper();
		colorHelper.addButton(rootView.findViewById(R.id.btn_color_black),
				Color.BLACK, R.drawable.btn_color_black,
				R.drawable.btn_color_black_selected);
		colorHelper.addButton(rootView.findViewById(R.id.btn_color_gray),
				Color.GRAY, R.drawable.btn_color_gray,
				R.drawable.btn_color_gray_selected);
		colorHelper.addButton(rootView.findViewById(R.id.btn_color_white),
				Color.WHITE, R.drawable.btn_color_white,
				R.drawable.btn_color_white_selected);
		colorHelper.addButton(rootView.findViewById(R.id.btn_color_red),
				Color.RED, R.drawable.btn_color_red,
				R.drawable.btn_color_red_selected);
		colorHelper.addButton(rootView.findViewById(R.id.btn_color_green),
				Color.GREEN, R.drawable.btn_color_green,
				R.drawable.btn_color_green_selected);
		colorHelper.addButton(rootView.findViewById(R.id.btn_color_blue),
				Color.BLUE, R.drawable.btn_color_blue,
				R.drawable.btn_color_blue_selected);
		colorHelper.addButton(rootView.findViewById(R.id.btn_color_yellow),
				Color.YELLOW, R.drawable.btn_color_yellow,
				R.drawable.btn_color_yellow_selected);
		colorHelper.addButton(rootView.findViewById(R.id.btn_color_purple),
				Color.MAGENTA, R.drawable.btn_color_purple,
				R.drawable.btn_color_purple_selected);
		colorHelper.setDefaultButtonWithValue(chessboard.getBackgroundColor());
		colorHelper.setListener(new OnChooseClickListener() {
			@Override
			public void onClick(View view, int color) {
				chessboard.setBackgroundColor(color);
				callback.onChessboardChange(chessboard);
			}
		});
		colorHelper.initAll();

		// set up dimension grid
		ChooseButtonTableHelper tableHelper = new ChooseButtonTableHelper(3, 4);
		tableHelper.setDrawables(R.drawable.btn_content_cell,
				R.drawable.btn_content_empty);
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension11));
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension12));
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension13));
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension14), true);
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension21));
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension22));
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension23));
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension24), true);
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension31));
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension32));
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension33));
		tableHelper.addButton(rootView.findViewById(R.id.btn_dimension34));
		tableHelper.setDefaultButtonWithValues(chessboard.getRowCount(),
				chessboard.getColumnCount());
		tableHelper.setListener(new ChooseButtonTableHelper.OnChooseClickListenerDouble() {
			@Override
			public void onClick(View view, int row, int column) {
				chessboard.setRowCount(row);
				chessboard.setColumnCount(column);
				callback.onChessboardChange(chessboard);
			}
		});
		tableHelper.initAll();

		// set up border buttons
		ChooseButtonSetHelper borderHelper = new ChooseButtonSetHelper();
		borderHelper.addButton(
				rootView.findViewById(R.id.btn_border_no_border),
				Chessboard.BORDER_NO_BORDER, R.drawable.btn_border_no_border,
				R.drawable.btn_border_no_border_selected);
		borderHelper.addButton(rootView.findViewById(R.id.btn_border_small),
				Chessboard.BORDER_SMALL, R.drawable.btn_border_small,
				R.drawable.btn_border_small_selected);
		borderHelper.addButton(rootView.findViewById(R.id.btn_border_medium),
				Chessboard.BORDER_MEDIUM, R.drawable.btn_border_medium,
				R.drawable.btn_border_medium_selected);
		borderHelper.addButton(rootView.findViewById(R.id.btn_border_large),
				Chessboard.BORDER_LARGE, R.drawable.btn_border_large,
				R.drawable.btn_border_large_selected);
		borderHelper.setDefaultButtonWithValue(chessboard.getBorderWidth());
		borderHelper.setListener(new OnChooseClickListener() {
			@Override
			public void onClick(View view, int width) {
				chessboard.setBorderWidth(width);
				callback.onChessboardChange(chessboard);
			}
		});
		borderHelper.initAll();
		
		/*
		ToggleButton swapMode = (ToggleButton) rootView.findViewById(R.id.btn_mode_swap);
		swapMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				GridConfigFragment.this.swapMode = isChecked;
				callback.onChessboardChange(chessboard, isChecked);
			}

		});
		*/

		return rootView;
	}

	public static boolean isEditorAction(int actionId) {
		if (actionId == EditorInfo.IME_ACTION_DONE
				|| actionId == EditorInfo.IME_ACTION_NEXT
				|| actionId == EditorInfo.IME_ACTION_GO
				|| actionId == EditorInfo.IME_ACTION_SEND
				|| actionId == EditorInfo.IME_ACTION_PREVIOUS
				|| actionId == EditorInfo.IME_ACTION_SEARCH
				|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
			return true;
		} else {
			return false;
		}
	}
}