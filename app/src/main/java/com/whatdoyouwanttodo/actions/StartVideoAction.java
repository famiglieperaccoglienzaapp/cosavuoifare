package com.whatdoyouwanttodo.actions;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.whatdoyouwanttodo.ChessboardActivity;
import com.whatdoyouwanttodo.VideoPlaylistActivity;

/**
 * Implementazione di on click listener che apre una playlist di video
 */
public class StartVideoAction implements OnClickListener {
	private long id;

	public StartVideoAction(long id) {
		this.id = id;
	}

	@Override
	public void onClick(View view) {
		Activity activity = ChessboardActivity.getActivity();
		Intent intent = VideoPlaylistActivity.getStartIntentWithId(activity, id);
		activity.startActivity(intent);
	}
}