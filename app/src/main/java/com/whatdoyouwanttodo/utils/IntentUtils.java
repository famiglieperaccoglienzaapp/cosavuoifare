package com.whatdoyouwanttodo.utils;

import java.io.File;
import java.io.IOException;

import com.whatdoyouwanttodo.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public final class IntentUtils {
	public static final int SELECT_AUDIO_INTENT = 1;
	public static final int SELECT_IMAGE_INTENT = 2;
	public static final int SELECT_VIDEO_INTENT = 3;
	public static final int SELECT_FILE_INTENT = 4;
	
	private static String intentId = null;
	private static int intentParam = 0;
	private static String cameraImagePath = null;
	
	public static void startSelectAudioIntent(Activity activity, String id, int param) {
		IntentUtils.intentId = id;
		IntentUtils.intentParam = param;
		startSelectAudioIntent(activity);
	}
	
	public static void startSelectAudioIntent(Activity activity, String id) {
		IntentUtils.intentId = id;
		startSelectAudioIntent(activity);
	}

	public static void startSelectImageIntent(Activity caller, String id, int param) {
		IntentUtils.intentId = id;
		IntentUtils.intentParam = param;
		startSelectImageIntent(caller);
	}

	public static void startSelectImageIntent(Activity caller, String id) {
		IntentUtils.intentId = id;
		startSelectImageIntent(caller);
	}
	
	public static void startSelectVideoIntent(Activity caller, String id, int param) {
		IntentUtils.intentId = id;
		IntentUtils.intentParam = param;
		startSelectVideoIntent(caller);
	}
	
	public static void startSelectVideoIntent(Activity caller, String id) {
		IntentUtils.intentId = id;
		startSelectVideoIntent(caller);
	}

	public static void startPlayVideoIntent(Activity caller, String videoPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(videoPath), "video/*");
		caller.startActivity(intent);
	}

	public static void startPlayAudioIntent(Activity caller, String musicPath) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(FileUtils.getResourceUri(musicPath), "audio/*");
		caller.startActivity(intent);
	}
	
	public static void startSelectZipFileIntent(Activity caller) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("application/zip");
		String title = caller.getResources().getString(R.string.class_intent_utils_select_zip);
		Intent chooser = Intent.createChooser(intent, title);
		caller.startActivityForResult(chooser, SELECT_FILE_INTENT);
	}

	public static String getAudioPath(Activity activity, Intent data) {
		return getFilePath(activity, data);
	}

	public static String getImagePath(Activity activity, Intent data) {
		try {
			return getFilePath(activity, data);
		} catch (Exception ex) {
			return cameraImagePath;
		}
	}

	public static String getVideoPath(Activity activity, Intent data) {
		return getFilePath(activity, data);
	}

	public static String getIntentId() {
		return intentId;
	}

	public static int getIntentParam() {
		return intentParam;
	}

	public static String getFilePath(Activity activity, Intent data) {
		Uri uri = data.getData();
		String imagePath = com.ipaulpro.afilechooser.utils.FileUtils.getPath(activity, uri);
		return imagePath;
	}

	private static void startSelectAudioIntent(Activity caller) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		String title = caller.getResources().getString(R.string.class_intent_utils_select_audio);
		caller.startActivityForResult(
				Intent.createChooser(intent, title),
				SELECT_AUDIO_INTENT);
	}

	private static void startSelectImageIntent(Activity caller) {
		Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
		galleryIntent.setType("image/*");
		
		File photoFile = null;
		cameraImagePath  = null;
		try {
			File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			photoFile = FileUtils.createFile(caller, dir, ".jpg");
		    cameraImagePath = photoFile.getAbsolutePath();
		} catch (IOException ex) {
		    cameraImagePath = null;
		}
		
		String title = caller.getResources().getString(R.string.class_intent_utils_select_image);
		Intent chooserIntent = Intent.createChooser(galleryIntent, title);
		if (cameraImagePath != null) {
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { cameraIntent });
		}
		caller.startActivityForResult(chooserIntent, SELECT_IMAGE_INTENT);
	}

	private static void startSelectVideoIntent(Activity caller) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("video/*");
		String title = caller.getResources().getString(R.string.class_intent_utils_select_video);
		caller.startActivityForResult(
				Intent.createChooser(intent, title),
				SELECT_VIDEO_INTENT);
	}
}
