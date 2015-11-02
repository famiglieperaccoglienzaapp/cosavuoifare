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
import com.whatdoyouwanttodo.application.MusicSlides;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.db.MusicSlidesCursor;
import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.ui.MessageDialog;

/**
 * Attivita' di configurazione per una playlist di immagini
 */
public class MusicSlidesConfigActivity extends ActionBarActivity {	
	public static final long NO_ID = -1;
	
	public static MusicSlidesReturn ret = null;
	
	private MusicSlidesConfigFragment configFragment;

	public static Intent getStartIntent(Activity caller, long id) {
		Intent intent = new Intent(caller, MusicSlidesConfigActivity.class);
		if(id == NO_ID) {
			ret = new MusicSlidesReturn(caller);
		} else {
			MusicSlides musicSlides = null;
			
			ChessboardDbUtility dbu = new ChessboardDbUtility(caller);
			dbu.openReadable();
			MusicSlidesCursor cursor = dbu.getCursorOnMusicSlides(id);
			while(cursor.moveToNext()) {
				musicSlides = cursor.getMusicSlides();
			}
			cursor.close();
			dbu.close();
			
			ret = new MusicSlidesReturn(musicSlides);
		}
		
		return intent;
	}

	public static boolean haveReturnParams() {
		if(ret == null)
			return false;
		return true;
	}

	public static MusicSlidesReturn getReturnParams() {
		MusicSlidesReturn clone = ret.clone();
		ret = null;
		return clone;
	}

	public static class MusicSlidesReturn implements Cloneable {
		private long id;
		private String name;
		String[] imagePaths;
		String musicPath;

		public MusicSlidesReturn(Context context) {
			id = -1;
			name = Constants.getInstance(context).NEW_MUSIC_SLIDES.getName();
			imagePaths = Constants.getInstance(context).NEW_MUSIC_SLIDES.getImagePaths();
			musicPath = Constants.getInstance(context).NEW_MUSIC_SLIDES.getMusicPath();
		}

		public MusicSlidesReturn(MusicSlides musicSlides) {
			id = musicSlides.getId();
			name = musicSlides.getName();
			imagePaths = musicSlides.getImagePaths();
			musicPath = musicSlides.getMusicPath();
		}

		private MusicSlidesReturn() {
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

		public String getMusicPath() {
			return musicPath;
		}

		public void setMusicPath(String musicPath) {
			this.musicPath = musicPath;
		}

		@Override
		public MusicSlidesReturn clone() {
			MusicSlidesReturn ret = new MusicSlidesReturn();
			ret.id = id;
			ret.name = name;
			ret.imagePaths = new String[imagePaths.length];
			for(int i = 0; i < imagePaths.length; i++) {
				ret.imagePaths[i] = imagePaths[i];
			}
			ret.musicPath = musicPath;
			return ret;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_music_slides_config);
		
		configFragment = new MusicSlidesConfigFragment();
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, configFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.music_slides_config, menu);
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
			    String title = res.getString(R.string.activity_music_slides_config_warning);
			    String message = res.getString(R.string.activity_music_slides_config_warning_message);
			    String confirm = res.getString(R.string.activity_music_slides_config_warning_confirm);
				MessageDialog.showMessage(this, title, message, confirm);
			}
			return true;
		}
		/* else if (id == R.id.action_preview_content) {
			// show preview
			if (ret.imagePaths.length > 0 && ret.name.length() > 0) {
				Intent intent = MusicSlidesActivity.getStartIntentWithParams(
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
		long id = dbu.addMusicSlides(ret.name, ret.musicPath, ret.imagePaths);
		ret.id = id;
		
		dbu.close();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		configFragment.onActivityResult(requestCode, resultCode, data);
	}
}
