package com.whatdoyouwanttodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.whatdoyouwanttodo.utils.ActivityUtils;

public class LongOperationActivity extends ActionBarActivity {
	private static LongOperationModel modelParam = null;

	public static Intent getStartIntent(Activity caller, LongOperationModel model) {
		Intent intent = new Intent(caller, LongOperationActivity.class);
		LongOperationActivity.modelParam  = model;
		return intent;
	}
	
	public static interface LongOperationModel {
		String getTitle();
		String getSubtitle();
		void doOperation(LongOperationStep callback);
	}
	
	public static interface LongOperationStep {
		void onStep(int progress, String detail1, String detail2);
		void onEnd();
	}

	private LongOperationModel model = null;
	private ProgressBar progressBar;
	private TextView details1Text;
	private TextView details2Text;
	
	private Thread thread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_long_operation);
		
		model = modelParam;
		modelParam = null;
		
		ActivityUtils.disableKeyguard(this);
		ActivityUtils.disableWakeLock(this);
		
		ActivityUtils.changeActionBarTitle(this, model.getTitle());
		TextView subtitle = (TextView) findViewById(R.id.subtitle);
		subtitle.setText(model.getSubtitle());
		
		progressBar = (ProgressBar) findViewById(R.id.progress);
		details1Text = (TextView) findViewById(R.id.details1);
		details2Text = (TextView) findViewById(R.id.details2);
		
		thread = new Thread(runnable);		
		thread.start();
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			model.doOperation(longOperationStep);
		}
	};
	
	private LongOperationStep longOperationStep = new LongOperationStep() {
		@Override
		public void onStep(int progress, String details1, String details2) {
			LongOperationActivity.this.runOnUiThread(new UpdateRunnable(progress, details1, details2));
		}

		@Override
		public void onEnd() {
			LongOperationActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					LongOperationActivity.this.finish();
				}
			});
		}
	};
	
	private class UpdateRunnable implements Runnable {
		private int progress;
		private String details1;
		private String details2;

		public UpdateRunnable(int progress, String details1, String details2) {
			this.progress = progress;
			this.details1 = details1;
			this.details2 = details2;
		}

		@Override
		public void run() {
			if (LongOperationActivity.this != null) {
				if (progressBar != null) {
					progressBar.setProgress(progress);
					details1Text.setText(details1);
					details2Text.setText(details2);
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		model = null;
		progressBar = null;
		details1Text = null;
		details2Text = null;
		
		ActivityUtils.clearKeyguard();
		ActivityUtils.clearWakeLock();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.long_operation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
