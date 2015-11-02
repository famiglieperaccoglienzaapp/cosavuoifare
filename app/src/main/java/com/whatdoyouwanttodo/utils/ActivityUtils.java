package com.whatdoyouwanttodo.utils;

import java.text.DecimalFormat;

import com.whatdoyouwanttodo.ChessboardActivity;
import com.whatdoyouwanttodo.application.ChessboardApplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressWarnings("deprecation")
public class ActivityUtils {
	private static WakeLock wakeLock = null;
	private static KeyguardLock keyguardLock = null;

	/* for future reuse
	public static void lockScreenOrientation(Activity activity) {
		WindowManager windowManager = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);
		Configuration configuration = activity.getResources()
				.getConfiguration();
		int rotation = windowManager.getDefaultDisplay().getRotation();

		// Search for the natural position of the device
		if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
				&& (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
				|| configuration.orientation == Configuration.ORIENTATION_PORTRAIT
				&& (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) {
			// Natural position is Landscape
			switch (rotation) {
			case Surface.ROTATION_0:
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case Surface.ROTATION_90:
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				break;
			case Surface.ROTATION_180:
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				break;
			case Surface.ROTATION_270:
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			}
		} else {
			// Natural position is Portrait
			switch (rotation) {
			case Surface.ROTATION_0:
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			case Surface.ROTATION_90:
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case Surface.ROTATION_180:
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				break;
			case Surface.ROTATION_270:
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				break;
			}
		}
	}
	*/

	public static void disableKeyguard(Activity activity) {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		
		try {
			KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Activity.KEYGUARD_SERVICE);
			keyguardLock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
			keyguardLock.disableKeyguard();
		} catch (Exception ex) {
			Log.e("ActivityUtils", "fail to disable keyguard", ex);
		}
	}
	
	public static void disableWakeLock(Activity activity) {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		try {
			PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
			wakeLock.acquire();
		} catch (Exception ex) {
			Log.e("ActivityUtils", "fail to disable warelock", ex);
		}
	}
	
	public static void clearWakeLock() {
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}
	}
	
	public static void clearKeyguard() {
		if (keyguardLock != null) {
			keyguardLock.reenableKeyguard();
			keyguardLock = null;
		}
	}

	public static void setFullscreen(Activity activity) {
		boolean methodSuccess = false;
		
		try {
			activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			methodSuccess = true;
		} catch (Exception ex) {
			// do nothing
		}
	    
		if(methodSuccess == true)
			return;
		
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				setFullscreenImmersive(activity);
				methodSuccess = true;
			}
		} catch (Exception ex) {
			// do nothing
		}
		
		if(methodSuccess == true)
			return;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static void setFullscreenImmersive(Activity activity) {
		activity.getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
				| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
				| View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	public static void changeActionBarTitle(ActionBarActivity activity, String newName) {
		if (ChessboardApplication.getFullscreenMode() == false) {
			try {
				activity.getSupportActionBar().setTitle(newName);
			} catch (Exception ex) {
				Log.e(activity.getClass().getName(),
						"error on change action bar title");
			}
		}
	}
	
	public static void logHeap() {
        Double allocated = Double.valueOf(Debug.getNativeHeapAllocatedSize())/Double.valueOf((1048576));
        Double available = Double.valueOf(Debug.getNativeHeapSize())/1048576.0;
        Double free = Double.valueOf(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.d(ChessboardActivity.class.getName(), "debug. =================================");
        Log.d(ChessboardActivity.class.getName(), "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        Log.d(ChessboardActivity.class.getName(), "debug.memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(Double.valueOf(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(Double.valueOf(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
    }
}
