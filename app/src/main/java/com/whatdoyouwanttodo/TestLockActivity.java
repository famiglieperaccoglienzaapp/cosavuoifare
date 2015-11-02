package com.whatdoyouwanttodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.utils.ActivityUtils;

/**
 * Rappresenta un'attivita' che serve da blocco di sicurezza per evitare che l'utente disabile riconfiguri il programma
 */
public class TestLockActivity extends Activity {
	private static OnTestListener callbackParam = null;
	
	private Button[] checks = null;
	private View[] timeLeds = null;
	private int[] checkOrders = null;
	private int step = 0;
	
	private OnTestListener callback = null;

	private Handler countdownHandler = null;
	private int countdownTime = 3;
	private Runnable countdown = new Runnable() {
		@Override
		public void run() {
			if(countdownHandler != null) {
				timeLeds[3 - countdownTime].setBackgroundColor(getResources().getColor(R.color.dark_green));
				countdownTime--;
				if (countdownTime > 0) {
					countdownHandler.postDelayed(countdown, 1000);
				} else {
					countdown = null;
					callback.onTest(false);
					finish();
				}
			}
		}
	};
	
	public static interface OnTestListener {
		void onTest(boolean passed);
	};

	public static Intent getStartIntent(Activity caller, OnTestListener callback) {
		Intent intent = new Intent(caller, TestLockActivity.class);
		TestLockActivity.callbackParam = callback;
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
		
		setContentView(R.layout.activity_setting_lock);
		
		this.callback = callbackParam;
		callbackParam = null;

		RelativeLayout checkContainer = (RelativeLayout) findViewById(R.id.check_container);
		checkContainer.setOnClickListener(negativeCheck);
		
		checks = new Button[3];
		checks[0] = (Button) findViewById(R.id.check_button1);
		checks[1] = (Button) findViewById(R.id.check_button2);
		checks[2] = (Button) findViewById(R.id.check_button3);
		
		timeLeds = new View[3];
		timeLeds[0] = (View) findViewById(R.id.check_led1);
		timeLeds[1] = (View) findViewById(R.id.check_led2);
		timeLeds[2] = (View) findViewById(R.id.check_led3);
		
		checkOrders = new int[3];
		checkOrders[0] = 0;
		checkOrders[1] = 1;
		checkOrders[2] = 2;
//		shuffleArray(checkOrders);
		
		step = 1;
		nextStep();
		
		countdownHandler = new Handler();
		countdownHandler.postDelayed(countdown, 1000);
	}
	
	// Implementing Fisher Yates shuffle
	/*
	static void shuffleArray(int[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
	*/

	private OnClickListener positiveCheck = new OnClickListener() {
		@Override
		public void onClick(View view) {
			step++;
			nextStep();
		}
	};
	
	private OnClickListener negativeCheck = new OnClickListener() {
		@Override
		public void onClick(View view) {
			finish();
			callback.onTest(false);
		}
	};
	
	private void nextStep() {
		if(step > checks.length) {
			finish();
			callback.onTest(true);
			return;
		}
		
		int selected = checkOrders[step - 1];
		for(int i = 0; i < checks.length; i++) {
			checks[i].setOnClickListener(null);
			checks[i].setText("");
			if(i == selected) {
				checks[i].setOnClickListener(positiveCheck);
				checks[i].setText(Integer.toString(step));
			} else {
				checks[i].setOnClickListener(negativeCheck);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		callback = null;
		for(int i = 0; i < checks.length; i++) {
			checks[i] = null;
		}
		checks = null;
		checkOrders = null;
		
		if(countdown != null) {
			countdownHandler.removeCallbacks(countdown);
			countdownHandler = null;
			countdown = null;
		}
		
		if (ChessboardApplication.getDisableLockMode() == true) {
			ActivityUtils.clearWakeLock();
		}
		ActivityUtils.clearKeyguard();
	}
}
