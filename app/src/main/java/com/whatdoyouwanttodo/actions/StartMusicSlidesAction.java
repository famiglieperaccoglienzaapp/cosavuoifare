package com.whatdoyouwanttodo.actions;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.whatdoyouwanttodo.ChessboardActivity;
import com.whatdoyouwanttodo.MusicSlidesActivity;

/**
 * Implementazione di OnClickListener che apre una playlist di immagini
 */
public class StartMusicSlidesAction implements OnClickListener {
	private long id;

	public StartMusicSlidesAction(long id) {
		this.id = id;
	}
	
	@Override
	public void onClick(View view) {
		Activity activity = ChessboardActivity.getActivity();
		Intent intent = MusicSlidesActivity.getStartIntentWithId(activity, id);
		activity.startActivity(intent);
	}
}