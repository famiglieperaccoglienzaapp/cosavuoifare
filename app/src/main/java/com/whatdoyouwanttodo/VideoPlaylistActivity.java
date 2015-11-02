package com.whatdoyouwanttodo;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeIntents;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.application.VideoPlaylist;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.db.VideoPlaylistCursor;
import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.utils.ActivityUtils;
import com.whatdoyouwanttodo.utils.FileUtils;
import com.whatdoyouwanttodo.utils.IntentUtils;

/**
 * Rappresenta un'attivita' che riproduce video da YouTube o da file locale
 */
public class VideoPlaylistActivity extends Activity {
	public static final String ID = "com.whatdoyouwanttodo.VideoViewActivity.ID";
	public static final String VIDEO_URL = "com.whatdoyouwanttodo.VideoViewActivity.VIDEO_URL";

	private VideoView videoView;

	private String[] videoUrl;
	private int index;
	
	private boolean firstResume;

	public static Intent getStartIntentWithId(Activity caller, long id) {
		Intent intent = new Intent(caller, VideoPlaylistActivity.class);
		intent.putExtra(ID, id);
		return intent;
	}

	public static Intent getStartIntentWithParams(Activity caller, String[] videoUrl) {
		Intent intent = new Intent(caller, VideoPlaylistActivity.class);
		intent.putExtra(VIDEO_URL, videoUrl);
		return intent;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		
		if (ChessboardApplication.getWifyCheck() == true) {
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (mWifi.isConnected() == false) {
				finish();
				return;
			}
		}
		
		if (ChessboardApplication.getFullscreenMode() == true) {
			ActivityUtils.setFullscreen(this);
		}

		if (ChessboardApplication.getDisableLockMode() == true) {
			ActivityUtils.disableWakeLock(this);
		}
		ActivityUtils.disableKeyguard(this);
		
		// get intent parameters
		Intent intent = getIntent();
		long id = intent.getLongExtra(ID, -1);
		if (id != -1) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(this);
			dbu.openReadable();

			VideoPlaylist videoPlaylist = null;
			VideoPlaylistCursor cursor = dbu.getCursorOnVideoPlaylist(id);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					videoPlaylist = cursor.getVideoPlaylist();
				}
				cursor.close();
			}
			videoUrl = videoPlaylist.getVideoUrl();

			dbu.close();
		} else {
			videoUrl = intent.getStringArrayExtra(VIDEO_URL);
		}

		setContentView(R.layout.activity_video_playlist);
		videoView = (VideoView) findViewById(R.id.video_view);
		MediaController videoMediaController = new MediaController(this);
		videoMediaController.setMediaPlayer(videoView);
		videoView.setMediaController(videoMediaController);
		videoView.setOnCompletionListener(this.onCompletionListener);
		index = 0;
		
		tryPlayVideo();
		
		firstResume = true;
	}

	private void tryPlayVideo() {
		// if last terminate
		if(index >= videoUrl.length) {
			finish();
			return;
		}
		
		if (videoUrl[index].startsWith(Configurations.YOUTUBE_PREFIX)) {
			// get YouTube id
			String video = videoUrl[index].substring(Configurations.YOUTUBE_PREFIX.length());
			
			if (YouTubeIntents.canResolvePlayVideoIntent(this) == true) {
				// try to launch YouTube intent
				Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(
						this, video, true, true);
				startActivity(intent);
			} else {
				// launch generic view intent
				IntentUtils.startPlayVideoIntent(this, video);
			}
		} else {
			String video = videoUrl[index];
			File videoFile = FileUtils.getResourceFile(video);
			if(videoFile == null) {
				index++;
				tryPlayVideo();
				return;
			}
			video = videoFile.getPath();
			// local video
			// is file
			videoView.setVideoPath(video);
			videoView.start();
		}
		
		index++;
	}
	
	private OnCompletionListener onCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			tryPlayVideo();
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		if (firstResume == false) {
			tryPlayVideo();
		} else {
			firstResume = false;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (ChessboardApplication.getDisableLockMode() == true) {
			ActivityUtils.clearWakeLock();
		}
		ActivityUtils.clearKeyguard();
	}
}
