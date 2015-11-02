package com.whatdoyouwanttodo.config;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.ActiveListening;
import com.whatdoyouwanttodo.db.ActiveListeningCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.ui.MessageDialog;

/**
 * Attivita' di configurazione di un Ascolto Attivo.
 */
public class ActiveListeningConfigActivity extends ActionBarActivity {
	public static final long NO_ID = -1;

	public static ActiveListeningReturn ret = null;
	
	private ActiveListeningConfigFragment configFragment;

	public static Intent getStartIntent(Activity caller, long id) {
		Intent intent = new Intent(caller, ActiveListeningConfigActivity.class);
		if (id == NO_ID) {
			ret = new ActiveListeningReturn(caller);
		} else {
			ActiveListening activeListening = null;

			ChessboardDbUtility dbu = new ChessboardDbUtility(caller);
			dbu.openReadable();
			ActiveListeningCursor cursor = dbu.getCursorOnActiveListening(id);
			while (cursor.moveToNext()) {
				activeListening = cursor.getActiveListening();
			}
			cursor.close();
			dbu.close();

			ret = new ActiveListeningReturn(activeListening);
		}
		return intent;
	}
	
	public static boolean haveReturnParams() {
		if(ret == null)
			return false;
		return true;
	}

	public static ActiveListeningReturn getReturnParams() {
		ActiveListeningReturn clone = ret.clone();
		ret = null;
		return clone;
	}

	public static class ActiveListeningReturn implements Cloneable {
		private long id;
		private String name;
		private String background;
		private String[] musicPaths;
		private int interval;
		private String registration;
		private int pause;
		private int pauseInterval;
		
		public ActiveListeningReturn(Context context) {
			this.id = -1;
			this.name = Constants.getInstance(context).NEW_ACTIVE_LISTENING.getName();
			this.background = Constants.getInstance(context).NEW_ACTIVE_LISTENING.getBackground();
			this.musicPaths = Constants.getInstance(context).NEW_ACTIVE_LISTENING.getMusicPath();
			this.interval = Constants.getInstance(context).NEW_ACTIVE_LISTENING.getInterval();
			this.registration = Constants.getInstance(context).NEW_ACTIVE_LISTENING.getRegistrationPath();
			this.pause = Constants.getInstance(context).NEW_ACTIVE_LISTENING.getPause();
			this.pauseInterval = Constants.getInstance(context).NEW_ACTIVE_LISTENING.getPauseInterval();
		}

		public ActiveListeningReturn(ActiveListening activeListening) {
			this.id = activeListening.getId();
			this.name = activeListening.getName();
			this.background = activeListening.getBackground();
			this.musicPaths = activeListening.getMusicPath();
			this.interval = activeListening.getInterval();
			this.registration = activeListening.getRegistrationPath();
			this.pause = activeListening.getPause();
			this.pauseInterval = activeListening.getPauseInterval();
		}

		private ActiveListeningReturn() {
			// do nothing (for clone)
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getBackground() {
			return background;
		}

		public void setBackground(String background) {
			this.background = background;
		}

		public String[] getMusicPaths() {
			return musicPaths;
		}

		public void setMusicPaths(String[] musicPaths) {
			this.musicPaths = musicPaths;
		}

		public int getInterval() {
			return interval;
		}

		public void setInterval(int interval) {
			this.interval = interval;
		}

		public String getRegistration() {
			return registration;
		}

		public void setRegistration(String registration) {
			this.registration = registration;
		}

		public int getPause() {
			return pause;
		}

		public void setPause(int pause) {
			this.pause = pause;
		}
		
		public int getPauseInterval() {
			return pauseInterval;
		}

		public void setPauseInterval(int pauseInterval) {
			this.pauseInterval = pauseInterval;
		}

		@Override
		public ActiveListeningReturn clone() {
			ActiveListeningReturn ret = new ActiveListeningReturn();
			ret.id = id;
			ret.name = name;
			ret.background = background;
			ret.musicPaths = musicPaths;
			ret.interval = interval;
			ret.registration = registration;
			ret.pause = pause;
			ret.pauseInterval = pauseInterval;
			return ret;
		}

		@Override
		public String toString() {
			return "ActiveListeningReturn [id=" + id + ", name=" + name
					+ ", background=" + background + ", musicPaths="
					+ Arrays.toString(musicPaths) + ", interval=" + interval
					+ ", registration=" + registration + ", pause=" + pause
					+ ", pauseInterval=" + pauseInterval + "]";
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_active_listening_config);

		if (savedInstanceState == null) {
			configFragment = new ActiveListeningConfigFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, configFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.active_listening_config, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_back) {
			// back action
			ret = null;
			finish();
			return true;
		} else if (id == R.id.action_save) {
			// save action
			if (ret.musicPaths.length > 0 && ret.name.length() > 0) {
				saveConfig();
				finish();
			} else {
				Resources res = getResources();
				String warning = res.getString(R.string.activity_active_listening_config_warning);
				String description = res.getString(R.string.activity_active_listening_config_warning_description);
				String confirm = res.getString(R.string.activity_active_listening_config_warning_confirm);
				MessageDialog.showMessage(this, warning, description, confirm);
			}
			return true;
		}
		/* else if (id == R.id.action_preview_content) {
			// show preview
			if (ret.musicPaths.length > 0 && ret.name.length() > 0) {
				Intent intent = ActiveListeningActivity
						.getStartIntentWithParams(this, ret.getBackground(), ret.getMusicPaths(),
								ret.getInterval(), ret.getRegistration(),
								ret.getPause(), ret.getPauseInterval());
				startActivity(intent);
			} else {
				Resources res = getResources();
				String warning = res.getString(R.string.activity_active_listening_config_warning);
				String description = res.getString(R.string.activity_active_listening_config_warning_description);
				String confirm = res.getString(R.string.activity_active_listening_config_warning_confirm);
				MessageDialog.showMessage(this, warning, description, confirm);
			}
		} */
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		ret = null;
		finish();
	}

	private void saveConfig() {
		ChessboardDbUtility dbu = new ChessboardDbUtility(this);
		dbu.openWritable();
		
		// save active listening
		long id = dbu.addActiveListening(ret.name, ret.background, ret.musicPaths, ret.interval, ret.registration, ret.pause, ret.pauseInterval);
		ret.id = id;
		
		dbu.close();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		configFragment.onActivityResult(requestCode, resultCode, data);
	}
}
