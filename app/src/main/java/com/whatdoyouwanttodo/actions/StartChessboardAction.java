package com.whatdoyouwanttodo.actions;

import com.whatdoyouwanttodo.ChessboardActivity;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * Implementazione di OnClickListener che apre una nuova tabella
 */
public class StartChessboardAction implements OnClickListener {
	private long id;
	private long parentId;

	public StartChessboardAction(long id, long parentId) {
		this.id = id;
		this.parentId = parentId;
	}

	@Override
	public void onClick(View view) {
		ChessboardActivity.changeChessboard(id, parentId);
	}
}