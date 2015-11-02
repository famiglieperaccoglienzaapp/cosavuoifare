package com.whatdoyouwanttodo.actions;

import com.whatdoyouwanttodo.ChessboardActivity;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * Implementazione di OnClickListener che ritorna alla tabella precedente
 */
public class GoBackAction implements OnClickListener {
	@Override
	public void onClick(View view) {
		ChessboardActivity.changeChessboard(-1, -1);
	}
}