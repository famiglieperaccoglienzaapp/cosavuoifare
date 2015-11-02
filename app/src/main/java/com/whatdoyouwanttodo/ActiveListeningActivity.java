package com.whatdoyouwanttodo;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;

import com.whatdoyouwanttodo.application.ActiveListening;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.db.ActiveListeningCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.utils.ActivityUtils;
import com.whatdoyouwanttodo.utils.FileUtils;

/**
 * Rappresenta un Ascolto Attivo, cioe' una musica che viene messa in pausa
 * interrotta ogni n secondi e viene ripresa se l'utente preme un bottone
 */
public class ActiveListeningActivity extends FragmentActivity {
	private static final String ID = "com.whatdoyouwanttodo.ActiveListeningActivity.ID";
	private static final String BACKGROUND = "com.whatdoyouwanttodo.ActiveListeningActivity.BACKGROUND";
	private static final String MUSIC_PATH = "com.whatdoyouwanttodo.ActiveListeningActivity.MUSIC_PATH";
	private static final String INTERVAL = "com.whatdoyouwanttodo.ActiveListeningActivity.INTERVAL";
	private static final String REGISTRATION = "com.whatdoyouwanttodo.ActiveListeningActivity.REGISTRATION";
	private static final String PAUSE = "com.whatdoyouwanttodo.ActiveListeningActivity.PAUSE";
	private static final String PAUSE_INTERVAL = "com.whatdoyouwanttodo.ActiveListeningActivity.PAUSE_INTERVAL";
	private String[] musicPaths;
	private int interval;
	private String registrationPath;
	private int pause;
	private int pauseInterval;

	private static final int STATE_STARTING = 0;
	private static final int STATE_PLAYING = 1;
	private static final int STATE_PAUSE = 2;
	private static final int STATE_PAUSE_NO_REGISTRATION = 3;
	private int state;
	private MediaPlayer player;
	private Handler handler;
	private MediaPlayer innerPlayer;
	private int musicIndex;
	private Bitmap backgroundBitmap;
	private ImageButton goButton;
	
	private Button backButton;
	private Handler backButtonWaitHandler = null;

	/**
	 * Crea un attivita' Ascolto Attivo con i parametri indicati
	 * 
	 * @param caller attivita' chiamante
	 * @param background percorso dell'immagine di sfondo
	 * @param musicPath percorsi della musiche da riprodurre
	 * @param interval intervallo di interruzione della musica
	 * @param registrationPath percorso del messaggio audio da riprodurre
	 * @param pause tempo di attesa del primo messaggio di rinforzo
	 * @param pauseInterval tempo di attesa fra le ripetizioni del messagio di rinforzo
	 * @return l'intento che puo' essere usato per creare l'attivita'
	 */
	public static Intent getStartIntentWithParams(Activity caller,
			String background, String[] musicPath, int interval,
			String registrationPath, int pause, int pauseInterval) {
		Intent intent = new Intent(caller, ActiveListeningActivity.class);
		intent.putExtra(BACKGROUND, background);
		intent.putExtra(MUSIC_PATH, musicPath);
		intent.putExtra(INTERVAL, interval);
		intent.putExtra(REGISTRATION, registrationPath);
		intent.putExtra(PAUSE, pause);
		intent.putExtra(PAUSE_INTERVAL, pauseInterval);
		return intent;
	}

	/**
	 * Crea un attivita' Ascolto Attivo caricando i dati dal database
	 * 
	 * @param caller attivita' chiamante
	 * @param id l'identificatore dell'ascolto attivo nel database
	 * @return l'intento che puo' essere usato per creare l'attivita'
	 */
	public static Intent getStartIntentWithId(Activity caller, long id) {
		Intent intent = new Intent(caller, ActiveListeningActivity.class);
		intent.putExtra(ID, id);
		return intent;
	}

	private static boolean TRUE = true;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (ChessboardApplication.getFullscreenMode() == true) {
			ActivityUtils.setFullscreen(this);
		}

		if (ChessboardApplication.getDisableLockMode() == true || TRUE) {
			ActivityUtils.disableWakeLock(this);
		}
		ActivityUtils.disableKeyguard(this);

		// get intent parameters
		Intent intent = getIntent();
		long id = intent.getLongExtra(ID, -1);
		if (id != -1) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(this);
			dbu.openReadable();

			ActiveListening activeListening = null;
			ActiveListeningCursor cursor = dbu.getCursorOnActiveListening(id);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					activeListening = cursor.getActiveListening();
				}
				cursor.close();
			}
			String background = activeListening.getBackground();
			if (background.equals("") == false) {
				File file = FileUtils.getResourceFile(background);
				backgroundBitmap = BitmapFactory.decodeFile(file.getPath());
			}
			musicPaths = activeListening.getMusicPath();
			interval = activeListening.getInterval();
			registrationPath = activeListening.getRegistrationPath();
			pause = activeListening.getPause();
			pauseInterval = activeListening.getPauseInterval();

			dbu.close();
		} else {
			String background = intent.getStringExtra(BACKGROUND);
			if (background.equals("") == false) {
				File file = FileUtils.getResourceFile(background);
				backgroundBitmap = BitmapFactory.decodeFile(file.getPath());
			}
			musicPaths = intent.getStringArrayExtra(MUSIC_PATH);
			interval = intent.getIntExtra(INTERVAL, 10);
			registrationPath = intent.getStringExtra(REGISTRATION);
			pause = intent.getIntExtra(PAUSE, 10);
			pauseInterval = intent.getIntExtra(PAUSE_INTERVAL, 5);
		}
		if (registrationPath.equals(""))
			registrationPath = null;

		// Initialize internal fields
		state = STATE_STARTING;
		player = null;
		handler = new Handler();
		innerPlayer = null;

		// set view
		setContentView(R.layout.activity_active_listening);
		
		backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(backButtonClick);

		// set listener
		goButton = (ImageButton) findViewById(R.id.goButton);
		goButton.setOnClickListener(this.onGoButtonClick);
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
	protected void onResume() {
		super.onResume();
		if (state == STATE_STARTING) {
			state = STATE_PLAYING;
			musicIndex = 0;
			Uri musicUri = FileUtils.getResourceUri(musicPaths[musicIndex]);
			player = MediaPlayer.create(this, musicUri);
			player.setLooping(false);
			player.setVolume(100, 100);
			player.setOnCompletionListener(onMusicCompletion);
			player.start();
			if (interval > 0) {
				handler.postDelayed(waitRunnable, interval * 1000);
			}
			goButton.setImageBitmap(backgroundBitmap);
			goButton.setScaleType(ScaleType.FIT_CENTER);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		state = STATE_STARTING;

		// clean first player
		if (player.isPlaying() == true) {
			player.stop();
		}
		player.release();
		player = null;

		if (innerPlayer != null) {
			if (innerPlayer.isPlaying() == true) {
				innerPlayer.stop();
			}
			innerPlayer.release();
			innerPlayer = null;
		}

		goButton.setImageResource(R.drawable.config_red_button);
		goButton.setScaleType(ScaleType.FIT_CENTER);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		backgroundBitmap = null;
		goButton.setImageBitmap(null);

		if (ChessboardApplication.getDisableLockMode() == true || TRUE) {
			ActivityUtils.clearWakeLock();
		}
		ActivityUtils.clearKeyguard();
	}

	private OnClickListener onGoButtonClick = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (state == STATE_PAUSE || state == STATE_PAUSE_NO_REGISTRATION) {
				state = STATE_PLAYING;

				// remove other callbacks
				handler.removeCallbacks(waitRunnable);

				// stop registration if playing
				if (innerPlayer != null) {
					if (innerPlayer.isPlaying() == true) {
						innerPlayer.stop();
					}
					innerPlayer.release();
					innerPlayer = null;
				}

				// start music
				player.start();

				// schedule next interruption
				handler.postDelayed(waitRunnable, interval * 1000);

				goButton.setImageBitmap(backgroundBitmap);
				goButton.setScaleType(ScaleType.FIT_CENTER);
			}
		}
	};

	private Runnable waitRunnable = new Runnable() {
		@Override
		public void run() {
			if (state == STATE_PLAYING) {
				goButton.setImageResource(R.drawable.config_red_button);
				goButton.setScaleType(ScaleType.FIT_CENTER);
				state = STATE_PAUSE_NO_REGISTRATION;
				player.pause();
				if (pause > 0) {
					handler.postDelayed(this, pause * 1000);
				} else {
					state = STATE_PAUSE;
					Uri registrationUri = FileUtils
							.getResourceUri(registrationPath);
					innerPlayer = MediaPlayer.create(
							ActiveListeningActivity.this, registrationUri);
					innerPlayer.setVolume(100, 100);
					innerPlayer.start();
					innerPlayer
							.setOnCompletionListener(onRegistrationCompletion);
				}
			} else if (state == STATE_PAUSE_NO_REGISTRATION) {
				state = STATE_PAUSE;
				Uri registrationUri = FileUtils
						.getResourceUri(registrationPath);
				innerPlayer = MediaPlayer.create(ActiveListeningActivity.this,
						registrationUri);
				innerPlayer.setVolume(100, 100);
				innerPlayer.start();
				innerPlayer.setOnCompletionListener(onRegistrationCompletion);
			}
		}
	};

	private OnCompletionListener onRegistrationCompletion = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			innerPlayer.release();
			innerPlayer = null;

			state = STATE_PAUSE_NO_REGISTRATION;
			player.pause();
			if (pause > 0) {
				handler.postDelayed(waitRunnable, pauseInterval * 1000);
			} else {
				state = STATE_PAUSE;
				Uri registrationUri = FileUtils
						.getResourceUri(registrationPath);
				innerPlayer = MediaPlayer.create(ActiveListeningActivity.this,
						registrationUri);
				innerPlayer.setVolume(100, 100);
				innerPlayer.start();
				innerPlayer.setOnCompletionListener(onRegistrationCompletion);
			}
		}
	};

	private OnCompletionListener onMusicCompletion = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			musicIndex++;
			if (musicIndex < musicPaths.length) {
				player.release();
				player = null;

				state = STATE_PLAYING;
				Uri musicUri = FileUtils.getResourceUri(musicPaths[musicIndex]);
				player = MediaPlayer.create(ActiveListeningActivity.this,
						musicUri);
				player.setLooping(false);
				player.setVolume(100, 100);
				player.setOnCompletionListener(onMusicCompletion);
				player.start();
			} else {
				finish();
			}
		}
	};
}
