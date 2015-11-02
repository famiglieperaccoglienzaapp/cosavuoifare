package com.whatdoyouwanttodo.actions;

import android.view.View;
import android.view.View.OnClickListener;

import com.whatdoyouwanttodo.ChessboardLayout.OnCellEventListener;
import com.whatdoyouwanttodo.application.Chessboard;

/**
 * Implementazione di OnClickListener che chiama OnCellEventListener
 */
public class CallCallbackAction implements OnClickListener {
	private Chessboard chessboard;
	private OnCellEventListener callback;
	private int row;
	private int column;
	private long param;

	public CallCallbackAction(OnCellEventListener callback,
			Chessboard chessboard, int row, int column, long param) {
		this.callback = callback;
		this.chessboard = chessboard;
		this.row = row;
		this.column = column;
		this.param = param;
	}

	@Override
	public void onClick(View view) {
		callback.onCellEvent(chessboard, row, column, param);
	}
}