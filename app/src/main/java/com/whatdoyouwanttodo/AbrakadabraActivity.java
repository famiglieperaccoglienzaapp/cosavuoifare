package com.whatdoyouwanttodo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.whatdoyouwanttodo.application.Abrakadabra;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.db.AbrakadabraCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.utils.ActivityUtils;
import com.whatdoyouwanttodo.utils.FileUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;

/**
 * Rappresenta una attivita' che riproduce una sequenza di immagini che avanza al
 * tap emettendo un suono.
 * L'ultima immagine e' accompagnata da una musica di sottofondo
 * e da un'animazione.
 */
public class AbrakadabraActivity extends FragmentActivity {
	private static final String ID = "com.whatdoyouwanttodo.AbrakadabraActivity.ID";
	private static final String IMAGE_PATHS = "com.whatdoyouwanttodo.AbrakadabraActivity.IMAGE_PATHS";
	private static final String SOUND_PATH = "com.whatdoyouwanttodo.AbrakadabraActivity.SOUND_PATH";
	private static final String MUSIC_PATH = "com.whatdoyouwanttodo.AbrakadabraActivity.MUSIC_PATH";
	private static final String IMAGE_EFFECT = "com.whatdoyouwanttodo.AbrakadabraActivity.SOUND_PATH";
	
	private String[] imagePaths;
	private int imageEffect;
	private ImageView image = null;
	private KenBurnsView animatedImage = null;
	private int imagePos;
	private String soundPath;
	private MediaPlayer soundPlayer = null;
	private String musicPath;
	private MediaPlayer musicPlayer = null;
	
	// back button
	private Button backButton;
	private Handler backButtonWaitHandler = null;

	public static Intent getStartIntentWithParams(Activity caller, String[] imagePaths, String soundPath, String musicPath, int imageEffect) {
		Intent intent = new Intent(caller, AbrakadabraActivity.class);
		intent.putExtra(IMAGE_PATHS, imagePaths);
		intent.putExtra(SOUND_PATH, soundPath);
		intent.putExtra(MUSIC_PATH, musicPath);
		intent.putExtra(IMAGE_EFFECT, imageEffect);
		return intent;
	}

	public static Intent getStartIntentWithId(Activity caller, long id) {
		Intent intent = new Intent(caller, AbrakadabraActivity.class);
		intent.putExtra(ID, id);
		return intent;
	}

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

		setContentView(R.layout.activity_abrakadabra);

		// get image paths and music path
		Intent intent = getIntent();
		long id = intent.getLongExtra(ID, -1);
		if (id != -1) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(this);
			dbu.openReadable();

			Abrakadabra abrakadabra = null;
			AbrakadabraCursor cursor = dbu.getCursorOnAbrakadabra(id);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					abrakadabra = cursor.getAbrakadabra();
				}
				cursor.close();
			}
			imagePaths = abrakadabra.getImagePaths();
			soundPath = abrakadabra.getSoundPath();
			musicPath = abrakadabra.getMusicPath();
			imageEffect = abrakadabra.getImageEffect();
			
			dbu.close();
		} else {
			imagePaths = intent.getStringArrayExtra(IMAGE_PATHS);
			soundPath = intent.getStringExtra(SOUND_PATH);
			musicPath = intent.getStringExtra(MUSIC_PATH);
			imageEffect = intent.getIntExtra(IMAGE_EFFECT, Abrakadabra.EFFECT_NO_EFFECT);
		}
		
		imagePos = 0;
		image = (ImageView) findViewById(R.id.front_image);
		animatedImage = (KenBurnsView) findViewById(R.id.animated_image);
		image.setOnClickListener(imageClick);
		animatedImage.setOnClickListener(imageClick);
		if (imageEffect == Abrakadabra.EFFECT_KENBURNS
				&& imagePos == (imagePaths.length - 1)) {
			image.setVisibility(View.GONE);
			animatedImage.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().loadImageLazyNoClean(animatedImage, imagePaths[imagePos]);
		} else {
			ImageLoader.getInstance().loadImageLazyNoClean(image, imagePaths[imagePos]);
		}

		if (soundPath != null) {
			if (soundPath.equals("") == false) {
				Uri soundUri = FileUtils.getResourceUri(soundPath);
				soundPlayer = MediaPlayer.create(this, soundUri);
				soundPlayer.setLooping(false);
				soundPlayer.setVolume(100, 100);
			}
		}
		if (musicPath != null) {
			if (musicPath.equals("") == false) {
				Uri musicUri = FileUtils.getResourceUri(musicPath);
				musicPlayer = MediaPlayer.create(this, musicUri);
				musicPlayer.setLooping(false);
				musicPlayer.setVolume(100, 100);
			}
		}
		imagePos++;

		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(backButtonClick);
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
	
	private OnClickListener imageClick = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (imagePos < imagePaths.length) {
				if (soundPlayer != null) {
					if (soundPlayer.isPlaying() == true) {
						try {
							soundPlayer.stop();
							soundPlayer.reset();
							Uri soundUri = FileUtils.getResourceUri(soundPath);
							soundPlayer.setDataSource(soundUri.getPath());
							soundPlayer.prepare();
						} catch (IllegalStateException e) {
							Log.e("AbrakadabraActivity", e.getMessage(), e);
						} catch (Exception e) {
							Log.e("AbrakadabraActivity", e.getMessage(), e);
						}
					}
					soundPlayer.start();
				}
				if (musicPlayer != null) {
					if (imagePos == (imagePaths.length - 1)) {
						musicPlayer.start();
					}
				}
				if (imageEffect == Abrakadabra.EFFECT_KENBURNS
						&& imagePos == (imagePaths.length - 1)) {
					image.setVisibility(View.GONE);
					animatedImage.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().loadImage(animatedImage, imagePaths[imagePos]);
					ImageLoader.getInstance().loadImageLazyNoClean(animatedImage, imagePaths[imagePos]);
				} else {
					ImageLoader.getInstance().loadImage(image, imagePaths[imagePos]);
				}
			} else {
				finish();
			}
			imagePos++;
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		imagePaths = null;
		soundPath = null;
		musicPath = null;
		
		if(image != null) {
			image.setImageBitmap(null);
			image.setBackgroundResource(0);
			image = null;
		}
		if(animatedImage != null) {
			animatedImage.pause();
//			animatedImage.setImageBitmap(null);
//			animatedImage.setBackgroundResource(0);
			animatedImage = null;
		}
		if (soundPlayer != null) {
			soundPlayer.stop();
			soundPlayer.release();
			soundPlayer = null;
		}
		if(musicPlayer != null) {
			musicPlayer.stop();
			musicPlayer.release();
			musicPlayer = null;
		}
		
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
}
