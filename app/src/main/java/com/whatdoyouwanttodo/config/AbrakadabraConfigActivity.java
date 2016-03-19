package com.whatdoyouwanttodo.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.Abrakadabra;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.db.AbrakadabraCursor;
import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.ui.MessageDialog;

/**
 * Attivita' di configurazione per un'attivita' di tipo abrakadabra
 */
public class AbrakadabraConfigActivity extends ActionBarActivity {	
	public static final long NO_ID = -1;
	
	public static AbrakadabraReturn ret = null;
	
	private AbrakadabraConfigFragment configFragment;

	public static Intent getStartIntent(Activity caller, long id) {
		Intent intent = new Intent(caller, AbrakadabraConfigActivity.class);
		if(id == NO_ID) {
			ret = new AbrakadabraReturn(caller);
		} else {
			Abrakadabra abrakadabra = null;
			
			ChessboardDbUtility dbu = new ChessboardDbUtility(caller);
			dbu.openReadable();
			AbrakadabraCursor cursor = dbu.getCursorOnAbrakadabra(id);
			while(cursor.moveToNext()) {
				abrakadabra = cursor.getAbrakadabra();
			}
			cursor.close();
			dbu.close();
			
			ret = new AbrakadabraReturn(abrakadabra);
		}
		
		return intent;
	}

	public static boolean haveReturnParams() {
		if(ret == null)
			return false;
		return true;
	}

	public static AbrakadabraReturn getReturnParams() {
		AbrakadabraReturn clone = ret.clone();
		ret = null;
		return clone;
	}

	public static class AbrakadabraReturn implements Cloneable {
		private long id;
		private String name;
		String[] imagePaths;
		String soundPath;
		String musicPath;
		int musicDurationTime;
		int imageEffect;

		public AbrakadabraReturn(Context context) {
			id = -1;
			name = Constants.getInstance(context).NEW_MUSIC_SLIDES.getName();
			imagePaths = Constants.getInstance(context).NEW_MUSIC_SLIDES.getImagePaths();
			soundPath = Constants.getInstance(context).NEW_MUSIC_SLIDES.getSoundPath();
			musicPath = Constants.getInstance(context).NEW_MUSIC_SLIDES.getMusicPath();
			musicDurationTime = Constants.getInstance(context).NEW_MUSIC_SLIDES.getMusicDurationTime();
			imageEffect = Constants.getInstance(context).NEW_MUSIC_SLIDES.getImageEffect();
		}

		public AbrakadabraReturn(Abrakadabra abrakadabra) {
			id = abrakadabra.getId();
			name = abrakadabra.getName();
			imagePaths = abrakadabra.getImagePaths();
			soundPath = abrakadabra.getSoundPath();
			musicPath = abrakadabra.getMusicPath();
			musicDurationTime = abrakadabra.getMusicDurationTime();
			imageEffect = abrakadabra.getImageEffect();
		}

		private AbrakadabraReturn() {
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

		public String[] getImagePaths() {
			return imagePaths;
		}

		public void setImagePaths(String[] imagePaths) {
			this.imagePaths = imagePaths;
		}

		public String getSoundPath() {
			return soundPath;
		}

		public void setSoundPath(String soundPath) {
			this.soundPath = soundPath;
		}

		public String getMusicPath() {
			return musicPath;
		}

		public void setMusicPath(String musicPath) {
			this.musicPath = musicPath;
		}

		public int getMusicDurationTime() {
			return musicDurationTime;
		}

		public void setMusicDurationTime(int musicDurationTime) {
			this.musicDurationTime = musicDurationTime;
		}

		public int getImageEffect() {
			return imageEffect;
		}

		public void setImageEffect(int imageEffect) {
			this.imageEffect = imageEffect;
		}

		@Override
		public AbrakadabraReturn clone() {
			AbrakadabraReturn ret = new AbrakadabraReturn();
			ret.id = id;
			ret.name = name;
			ret.imagePaths = new String[imagePaths.length];
			for(int i = 0; i < imagePaths.length; i++) {
				ret.imagePaths[i] = imagePaths[i];
			}
			ret.soundPath = soundPath;
			ret.musicPath = musicPath;
			ret.musicDurationTime = musicDurationTime;
			ret.imageEffect = imageEffect;
			return ret;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_abrakadabra_config);
		
		configFragment = new AbrakadabraConfigFragment();
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, configFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.abrakadabra_config, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_back) {
			// back action
			ret = null;
			finish();
			return true;
		} else if (id == R.id.action_save) {
			// save action
			if (ret.imagePaths.length > 0 && ret.name.length() > 0) {
				saveConfig();
				finish();
			} else {
				Resources res = getResources();
			    String title = res.getString(R.string.activity_abrakadabra_config_warning);
			    String message = res.getString(R.string.activity_abrakadabra_config_warning_message);
			    String confirm = res.getString(R.string.activity_abrakadabra_config_warning_confirm);
				MessageDialog.showMessage(this, title, message, confirm);
			}
			return true;
		}
		/* else if (id == R.id.action_preview_content) {
			// show preview
			if (ret.imagePaths.length > 0 && ret.name.length() > 0) {
				Intent intent = AbrakadabraActivity.getStartIntentWithParams(
						this, ret.getImagePaths(), ret.getMusicPath());
				startActivity(intent);
			} else {
				Resources res = getResources();
			    String title = res.getString(R.string.activity_music_slides_config_warning);
			    String message = res.getString(R.string.activity_music_slides_config_warning_message);
			    String confirm = res.getString(R.string.activity_music_slides_config_warning_confirm);
				MessageDialog.showMessage(this, title, message, confirm);
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

		// save music slides
		long id = dbu.addAbrakadabra(ret.name, ret.imagePaths, ret.soundPath, ret.musicPath, ret.musicDurationTime, ret.imageEffect);
		ret.id = id;

		dbu.close();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		configFragment.onActivityResult(requestCode, resultCode, data);
	}
}
