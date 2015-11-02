package com.whatdoyouwanttodo.actions;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.whatdoyouwanttodo.ActiveListeningActivity;
import com.whatdoyouwanttodo.ChessboardActivity;

/**
 * Implementazione di OnClickListener che apre un Ascolto Attivo
 */
public class StartActiveListeningAction implements OnClickListener {
	private long id;

	public StartActiveListeningAction(long id) {
		this.id = id;
	}
	
	@Override
	public void onClick(View view) {
		Activity activity = ChessboardActivity.getActivity();
		Intent intent = ActiveListeningActivity.getStartIntentWithId(activity, id);
		activity.startActivity(intent);
	}
}