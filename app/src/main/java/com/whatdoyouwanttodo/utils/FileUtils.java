package com.whatdoyouwanttodo.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.settings.Constants;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public final class FileUtils {
	public static File getStorageDirectory() {
		File extDir = Environment.getExternalStorageDirectory();
		String extDirState = Environment.getExternalStorageState();
		if (!(extDirState.equals(Environment.MEDIA_MOUNTED)
				|| extDirState.equals(Environment.MEDIA_SHARED))) {
			return null;
		}
		return extDir;
	}
	
	public static File getResourceFile(String path) {
		// check storage directory
		File extDir = getStorageDirectory();
		if (extDir == null)
			return null;
		
		String directoryPath = extDir.getPath();
		
		// is relative
		File resFile = new File(directoryPath, path);
		if(resFile.exists())
			return resFile;
		
		// is absolute
		resFile = new File(path);
		if(resFile.exists())
			return resFile;
		
		// not found
		return null;
	}

	public static File getResourceFileForWrite(String path) {
		// check storage directory
		File extDir = getStorageDirectory();
		if (extDir == null)
			return null;

		// is relative
		File resFile = new File(extDir, path);
		File resFileFolder = resFile.getParentFile();
		if(resFileFolder.exists() == false) {
			resFileFolder.mkdirs();
		}
		return resFile;
	}

	public static Uri getResourceUri(String path) {
		File resFile = getResourceFile(path);
		if (resFile == null)
			return null;
		return Uri.fromFile(resFile);
	}
	
	public static File createFile(Context context, File storageDir, String extension)
			throws IOException {
		// make timestamp string
		Locale loc = Locale.getDefault();
		SimpleDateFormat dateFor = new SimpleDateFormat(Configurations.IMAGE_TIMESTAMP, loc);
		Date nowDate = new Date();
		String timeStamp = dateFor.format(nowDate);

		// Create a file 
		String imageFileName = Constants.getInstance(context).IMAGE_PREFIX + timeStamp + extension;
		File image = new File(storageDir, imageFileName);
		return image;
	}

	public static File getWritableDocumentFile(Activity activity, String folder, String prefix, String ext) {
		File baseDir = new File(getStorageDirectory(), Constants.getInstance(activity).FILE_DIR);
		File folderDir = new File(baseDir, folder);
		if(folderDir.exists() == false) {
			folderDir.mkdir();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append("__");
		SimpleDateFormat sdf = new SimpleDateFormat(Configurations.EXPORT_DATA_FORMAT, Locale.getDefault());
		sb.append(sdf.format(new Date()));
		sb.append(ext);
		File outFile = new File(folderDir, sb.toString());
		
		return outFile;
	}
}
