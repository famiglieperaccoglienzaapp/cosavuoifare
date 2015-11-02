package com.whatdoyouwanttodo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whatdoyouwanttodo.ChessboardLayout.OnCellEventListener;
import com.whatdoyouwanttodo.ChessboardLayout.OnSecondaryCellEventListener;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.config.GridConfigActivity;

/**
 * Rappresenta una Tabella AAC, è usato in ChessboardActivity per mostrare la tabella e cambiarla senza cambiare attività
 */
public class ChessboardFragment extends Fragment {
	private ChessboardLayout layout;
	
	public ChessboardFragment() {
		layout = new ChessboardLayout();
	}

	public static ChessboardFragment newChessboardFragment(
			Chessboard chessboard, Cell[] cells) {
		ChessboardFragment fragment = new ChessboardFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelable(ChessboardLayout.CHESSBOARD, chessboard);
		arguments.putParcelableArray(ChessboardLayout.CELL_ARRAY, cells);
		fragment.setArguments(arguments);

		return fragment;
	}
	
	public static ChessboardFragment newChessboardFragmentWithConfigButtons (
			Chessboard chessboard, Cell[] cells,
			boolean addDeleteButton, boolean showTargetButton) {
		ChessboardFragment fragment = new ChessboardFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelable(ChessboardLayout.CHESSBOARD, chessboard);
		arguments.putParcelableArray(ChessboardLayout.CELL_ARRAY, cells);
		arguments.putBoolean(ChessboardLayout.SECONDARY_BUTTONS, addDeleteButton);
		arguments.putBoolean(ChessboardLayout.TARGET_DRAWABLE, showTargetButton);
		fragment.setArguments(arguments);

		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		if (activity.getClass() == GridConfigActivity.class) {
			try {
				layout.callback = (OnCellEventListener) activity;
				layout.configMode = true;
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString() + " must implement " + OnCellEventListener.class.getName());
			}
			
			if (getArguments().getBoolean(ChessboardLayout.SECONDARY_BUTTONS, false) == true) {
				try {
					layout.secondaryCallback = (OnSecondaryCellEventListener) activity;
				} catch (ClassCastException e) {
					throw new ClassCastException(activity.toString()
							+ " must implement " + OnSecondaryCellEventListener.class.getName());
				}
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(layout.activity == null)
			layout.activity = getActivity();
		layout.arguments = getArguments();
		
		return layout.createView();
	}
}