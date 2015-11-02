package com.whatdoyouwanttodo.actions;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.whatdoyouwanttodo.ChessboardLayout.OnSecondaryCellEventListener;
import com.whatdoyouwanttodo.application.Chessboard;

/**
 * Implementazione di OnClickListener e OnLongClickListener che chiama OnSecondaryCellEventCallback
 */
public class CallSecondaryCallbackAction implements OnClickListener, OnLongClickListener {
	private boolean isLongTouch;
	private Chessboard chessboard;
	private OnSecondaryCellEventListener callback;
	private int row;
	private int column;
	private long param;

	public CallSecondaryCallbackAction(boolean isLongTouch,
			OnSecondaryCellEventListener callback, Chessboard chessboard,
			int row, int column, long param) {
		this.isLongTouch = isLongTouch;
		this.callback = callback;
		this.chessboard = chessboard;
		this.row = row;
		this.column = column;
		this.param = param;
	}

	@Override
	public void onClick(View view) {
		callback.onSecondaryCellEvent(isLongTouch, chessboard, row, column, param);
	}

	@Override
	public boolean onLongClick(View view) {
		callback.onSecondaryCellEvent(isLongTouch, chessboard, row, column, param);
		return true;
	}
}