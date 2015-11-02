package com.whatdoyouwanttodo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.application.MusicSlides;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.db.MusicSlidesCursor;
import com.whatdoyouwanttodo.utils.ActivityUtils;
import com.whatdoyouwanttodo.utils.FileUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;

/**
 * Rappresenta una attività che riproduce immagini con musica
 */
public class MusicSlidesActivity extends FragmentActivity {
	private static final String ID = "com.whatdoyouwanttodo.MusicSlidesActivity.ID";
	private static final String IMAGE_PATHS = "com.whatdoyouwanttodo.MusicSlidesActivity.IMAGE_PATHS";
	private static final String MUSIC_PATH = "com.whatdoyouwanttodo.MusicSlidesActivity.MUSIC_PATH";

	private MediaPlayer player;
	private String[] imagePaths;
	private String musicPath;
	
	private Button backButton;
	private Handler backButtonWaitHandler = null;

	public static Intent getStartIntentWithParams(Activity caller,
			String[] imagePaths, String musicPath) {
		Intent intent = new Intent(caller, MusicSlidesActivity.class);
		intent.putExtra(IMAGE_PATHS, imagePaths);
		intent.putExtra(MUSIC_PATH, musicPath);
		return intent;
	}

	public static Intent getStartIntentWithId(Activity caller, long id) {
		Intent intent = new Intent(caller, MusicSlidesActivity.class);
		intent.putExtra(ID, id);
		return intent;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (ChessboardApplication.getFullscreenMode() == true) {
			ActivityUtils.setFullscreen(this);
		}

		if (ChessboardApplication.getDisableLockMode() == true) {
			ActivityUtils.disableWakeLock(this);
		}
		ActivityUtils.disableKeyguard(this);

		setContentView(R.layout.activity_music_slides);

		// get image paths and music path
		Intent intent = getIntent();
		long id = intent.getLongExtra(ID, -1);
		if (id != -1) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(this);
			dbu.openReadable();

			MusicSlides musicSlides = null;
			MusicSlidesCursor cursor = dbu.getCursorOnMusicSlides(id);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					musicSlides = cursor.getMusicSlides();
				}
				cursor.close();
			}
			imagePaths = musicSlides.getImagePaths();
			musicPath = musicSlides.getMusicPath();

			dbu.close();
		} else {
			imagePaths = intent.getStringArrayExtra(IMAGE_PATHS);
			musicPath = intent.getStringExtra(MUSIC_PATH);
		}
		if (musicPath.equals(""))
			musicPath = null;
		
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(backButtonClick);

		// create fragment
		if (savedInstanceState == null) {
			MusicSlidesFragment musicSlidesFragment = MusicSlidesFragment
					.newMusicSlidesFragment(imagePaths, musicPath);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, musicSlidesFragment).commit();
		}
	}
	
	private OnClickListener backButtonClick = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (backButtonWaitHandler == null) {
				backButtonWaitHandler = new Handler();
				backButton.setBackgroundResource(R.drawable.back_bg_red);
				backButton.setText(getResources().getString(R.string.activity_active_listening_press_again));
				backButtonWaitHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						backButton.setBackgroundResource(R.drawable.back_bg);
						backButton.setText("");
						backButtonWaitHandler = null;
					}
				}, 2000);
			} else {
				finish();
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		if (musicPath != null) {
			Uri musicUri = FileUtils.getResourceUri(musicPath);
			player = MediaPlayer.create(this, musicUri);
			player.setLooping(true);
			player.setVolume(100, 100);
			player.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (musicPath != null) {
			player.stop();
			player.release();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(imagePaths != null) {
			for(int i = 0; i < imagePaths.length; i++) {
				imagePaths[i] = null;
			}
			imagePaths = null;
		}
		musicPath = null;
		
		if(backButton == null) {
			backButton.setOnClickListener(null);
			backButton.setBackgroundResource(0);
			backButton = null;
		}
		backButtonWaitHandler = null;
		
		if (ChessboardApplication.DEBUG_HEAP_LOG) {
			ActivityUtils.logHeap();
		}
		
		if (ChessboardApplication.getDisableLockMode() == true) {
			ActivityUtils.clearWakeLock();
		}
		ActivityUtils.clearKeyguard();
	}

	public static class MusicSlidesFragment extends Fragment {
		private ImageView image;
		private String[] imagePaths;
		private int imagePos;

		public MusicSlidesFragment() {
		}

		public static MusicSlidesFragment newMusicSlidesFragment(
				String[] imagePaths, String musicPath) {
			MusicSlidesFragment fragment = new MusicSlidesFragment();

			Bundle arguments = new Bundle();
			arguments.putStringArray(IMAGE_PATHS, imagePaths);
			arguments.putString(MUSIC_PATH, musicPath);
			fragment.setArguments(arguments);

			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_music_slides,
					container, false);

			Bundle arguments = getArguments();
			imagePaths = arguments.getStringArray(IMAGE_PATHS);
			// String musicPath = arguments.getString(MUSIC_PATH);
			imagePos = 0;

			image = (ImageView) rootView.findViewById(R.id.front_image);
			ImageLoader.getInstance().loadImageLazyNoClean(image, imagePaths[imagePos]);
			image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					imagePos++;
					if (imagePos < imagePaths.length) {
						ImageLoader.getInstance().loadImage(image, imagePaths[imagePos]);
					} else {
						getActivity().finish();
					}
				}
			});

			return rootView;
		}
		
		@Override
		public void onDestroyView() {
			super.onDestroyView();
			
			if(image != null) {
				image.setImageBitmap(null);
				image.setBackgroundResource(0);
				image = null;
			}
			if(imagePaths != null) {
				for(int i = 0; i < imagePaths.length; i++) {
					imagePaths[i] = null;
				}
				imagePaths = null;
			}
			imagePos = 0;
		}
	}
}
