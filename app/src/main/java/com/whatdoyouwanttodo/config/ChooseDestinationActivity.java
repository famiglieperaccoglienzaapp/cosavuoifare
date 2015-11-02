package com.whatdoyouwanttodo.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.ActiveListening;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.application.MusicSlides;
import com.whatdoyouwanttodo.application.VideoPlaylist;
import com.whatdoyouwanttodo.db.ActiveListeningCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.db.MusicSlidesCursor;
import com.whatdoyouwanttodo.db.VideoPlaylistCursor;
import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.utils.FileUtils;

/**
 * Attivita' che conclude un'importazione o un'esportazione
 */
public class ChooseDestinationActivity extends ActionBarActivity {
	private static Chessboard[] cbParam = null;
	private static Cell[][] cbCellsParam = null;
	private static String importParam = null;

	public static Intent getStartIntent(Activity caller, Chessboard[] cb, Cell[][] cbCells) {
		Intent intent = new Intent(caller, ChooseDestinationActivity.class);
		cbParam = cb;
		cbCellsParam  = cbCells;
		importParam = null;
		return intent;
	}

	public static Intent getStartIntent(Activity caller, String path) {
		Intent intent = new Intent(caller, ChooseDestinationActivity.class);
		cbParam = null;
		cbCellsParam  = null;
		importParam  = path;
		return intent;
	}

	private Chessboard[] cbs;
	private Cell[][] cbCells;
	private String importPath;
	private TreeMap<String, String> filenameset;
	private MusicSlides[] musicSlides;
	private ActiveListening[] activeListening;
	private VideoPlaylist[] videoPlaylist;
	
	private TextView helpText;
	private TextView filePathText;
	private Button openFile;
	private Button close;
	
	private File outFile;
	private boolean overwrite = false;
	private String dateStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.cbs = cbParam;
		this.cbCells = cbCellsParam;
		this.importPath = importParam;
		cbParam = null;
		cbCellsParam = null;
		importParam = null;
		
		if(importPath == null && cbs != null && cbCells != null) {
			setContentView(R.layout.activity_choose_destination);

			this.helpText = (TextView) findViewById(R.id.help_text);
			this.filePathText = (TextView) findViewById(R.id.file_path_text);
			this.openFile = (Button) findViewById(R.id.open_file);
			this.close = (Button) findViewById(R.id.close);
			
			close.setOnClickListener(this.onClose);
		
			SaveOnFile task = new SaveOnFile();
			task.execute();
		} else if (importPath != null && cbs == null && cbCells == null) {
			setContentView(R.layout.activity_choose_destination_import);

			this.helpText = (TextView) findViewById(R.id.help_text);
			this.filePathText = (TextView) findViewById(R.id.file_path_text);
			this.openFile = (Button) findViewById(R.id.open_file);
			this.close = (Button) findViewById(R.id.close);
			
			SimpleDateFormat df = new SimpleDateFormat(Configurations.EXPORT_DATA_FORMAT, Locale.getDefault());
			this.dateStr = df.format(new Date());
			
			openFile.setOnClickListener(this.onOverwriteImport);
			close.setOnClickListener(this.onAppendImport);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		// clear pointer
		this.cbs = null;
		this.cbCells = null;
		this.importPath = null;
		cbParam = null;
		cbCellsParam = null;
		importParam = null;
		openFile.setOnClickListener(null);
		close.setOnClickListener(null);
		this.helpText = null;
		this.filePathText = null;
		this.openFile = null;
		this.close = null;
	}
	
	private class SaveOnFile extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... voids) {
			return writeOnFile();
		}
		
		@Override
		protected void onPostExecute(Boolean ret) {
			if (helpText != null && filePathText != null && openFile != null && close != null) {
				Resources res = getResources();
				if (ret == true) {
				    String exportTitle = res.getString(R.string.activity_choose_destination_export_title);
				    String exportMessage = res.getString(R.string.activity_choose_destination_export_message);
					helpText.setText(exportTitle);
					filePathText.setText(exportMessage);
					filePathText.setVisibility(View.VISIBLE);
					openFile.setVisibility(View.GONE);
					close.setVisibility(View.VISIBLE);
				} else {
					String exportTitle = res.getString(R.string.activity_choose_destination_export_failed_title);
					String exportMessage = res.getString(R.string.activity_choose_destination_export_failed_message);
					helpText.setText(exportTitle);
					filePathText.setText(exportMessage);
					filePathText.setVisibility(View.VISIBLE);
					openFile.setVisibility(View.GONE);
					close.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	private class LoadFromFile extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... voids) {
			return loadFromFile();
		}

		@Override
		protected void onPostExecute(Boolean ret) {
			if (helpText != null && filePathText != null && openFile != null && close != null) {
				Resources res = getResources();
				if (ret == true) {
					String importTitle = res.getString(R.string.activity_choose_destination_import_title);
				    helpText.setText(importTitle);
					filePathText.setVisibility(View.GONE);
					openFile.setVisibility(View.GONE);
					close.setOnClickListener(onClose);
					close.setText(res.getString(R.string.activity_choose_destination_close));
					close.setVisibility(View.VISIBLE);
				} else {
					String importTitle = res.getString(R.string.activity_choose_destination_import_failed_title);
				    String importMessage = res.getString(R.string.activity_choose_destination_import_failed_message);
					helpText.setText(importTitle);
					filePathText.setText(importMessage);
					filePathText.setVisibility(View.VISIBLE);
					openFile.setVisibility(View.GONE);
					close.setOnClickListener(onClose);
					close.setText(res.getString(R.string.activity_choose_destination_close));
					close.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	private boolean writeOnFile() {
		// get a file name
		outFile = FileUtils.getWritableDocumentFile(this, Constants.getInstance(this).EXPORT_DIR, Constants.getInstance(this).FILE_DIR, Configurations.EXPORT_PREFIX);

		ZipOutputStream zipOutStream = null;
		boolean success = true;
		try {
			OutputStream outStream = new FileOutputStream(outFile);
			zipOutStream = new ZipOutputStream(new BufferedOutputStream(outStream));
			zipOutStream.setLevel(Deflater.BEST_COMPRESSION);

			filenameset = new TreeMap<String, String>();
			putXmlIndex(zipOutStream);
			
			Iterator<String> filenameIt = filenameset.keySet().iterator();
			while (filenameIt.hasNext()) {
				String filename = filenameIt.next();
				File file = FileUtils.getResourceFile(filenameset.get(filename));

				ZipEntry zipEntry = new ZipEntry(filename);
				zipOutStream.putNextEntry(zipEntry);
				
				BufferedInputStream bufInStream = null;
				try {
					int bufferSize = 8192;
					FileInputStream fileInStream = new FileInputStream(file);
					bufInStream = new BufferedInputStream(fileInStream, bufferSize);
					byte data[] = new byte[bufferSize];
					int count;
					while ((count = bufInStream.read(data, 0, bufferSize)) != -1) {
						zipOutStream.write(data, 0, count);
					}
				} catch (IOException ex) {
					Log.e(getClass().getName(), "error on export", ex);
					success = false;
				} finally {
					try {
						bufInStream.close();
					} catch (IOException ex) {
						Log.e(getClass().getName(), "error on export", ex);
						success = false;
					}
				}
				
				zipOutStream.closeEntry();
			}
		} catch (IOException ex) {
			Log.e(getClass().getName(), "error on export", ex);
			success = false;
		} finally {
			try {
				zipOutStream.close();
			} catch (IOException ex) {
				Log.e(getClass().getName(), "error on export", ex);
				success = false;
			}
		}

		if(filenameset != null) {
			filenameset.clear();
			filenameset = null;
		}
		
		return success;
	}
	
	private void putXmlIndex(ZipOutputStream zos) throws IOException {
		ZipEntry entry = new ZipEntry(Configurations.EXPORT_CONTENT_NAME);
        zos.putNextEntry(entry);
         
        // get temporary file
        File cacheDir = getCacheDir();
        File tempFile = File.createTempFile(Configurations.EXPORT_CONTENT_PREFIX, Configurations.EXPORT_CONTENT_POSTFIX, cacheDir);
        FileOutputStream fow = new FileOutputStream(tempFile);
        
        XmlSerializer xmlSerializer = Xml.newSerializer();
	    xmlSerializer.setOutput(fow, Configurations.EXPORT_ENCODING);

	    // write temporary file
	    xmlSerializer.startDocument(Configurations.EXPORT_ENCODING, true);
	    writeXmlIndex(xmlSerializer);
	    xmlSerializer.endDocument();
	    fow.close();
	    
	    // zip temp file
		int bufferSize = 8192;
		byte data[] = new byte[bufferSize];
		FileInputStream fi = new FileInputStream(tempFile);
		BufferedInputStream origin = new BufferedInputStream(fi, bufferSize);
		int count;
		while ((count = origin.read(data, 0, bufferSize)) != -1) {
			zos.write(data, 0, count);
		}
		origin.close();

		zos.closeEntry();
	}

	private void writeXmlIndex(XmlSerializer xmlSerializer) throws IllegalArgumentException, IllegalStateException, IOException {
		xmlSerializer.startTag("", "tables");

		ArrayList<Long> abrakadabra = new ArrayList<Long>();
		ArrayList<Long> activelistening = new ArrayList<Long>();
		ArrayList<Long> playvideo = new ArrayList<Long>();
		for (int i = 0; i < cbs.length; i++) {
			Chessboard cb = cbs[i];
			Cell[] cells = cbCells[i];

			// write chessboard
			xmlSerializer.startTag("", "table");
			long id = cb.getId();
			long parentId = cb.getParentId();
			String name = cb.getName();
			int rowCount = cb.getRowCount();
			int columnCount = cb.getColumnCount();
			int bgColor = cb.getBackgroundColor();
			int borderWidth = cb.getBorderWidth();
			xmlSerializer.attribute("", "id", Long.toString(id));
			xmlSerializer.attribute("", "parent", Long.toString(parentId));
			xmlSerializer.attribute("", "name", name);
			xmlSerializer.attribute("", "rows", Integer.toString(rowCount));
			xmlSerializer.attribute("", "columns", Integer.toString(columnCount));
			xmlSerializer.attribute("", "color", Integer.toString(bgColor));
			xmlSerializer.attribute("", "border", Integer.toString(borderWidth));
			
			// write cells
			for(int j = 0; j < cells.length; j++) {
				Cell cell = cells[j];
				
				xmlSerializer.startTag("", "symbol");

//				long chessboard = cell.getChessboard();
				long cellId = cell.getId();
				int row = cell.getRow();
				int column = cell.getColumn();
				String cellName = cell.getName();
				String imagePath = cell.getImagePath();
				String audioPath = cell.getAudioPath();
				String text = cell.getText();
				long activityType = cell.getActivityType();
				long activityParam = cell.getActivityParam();
				int cellBgColor = cell.getBackgroundColor();
				int borderColor = cell.getBorderColor();
				int cellBorderWidth = cell.getBorderWidth();
				int textColor = cell.getTextColor();
				int textWidth = cell.getTextWidth();
				
				imagePath = putExternalFile(imagePath);
				audioPath = putExternalFile(audioPath);
				
				xmlSerializer.attribute("", "id", Long.toString(cellId));
				xmlSerializer.attribute("", "row", Integer.toString(row));
				xmlSerializer.attribute("", "column", Integer.toString(column));
				xmlSerializer.attribute("", "name", cellName);
				xmlSerializer.attribute("", "image", imagePath);
				xmlSerializer.attribute("", "audio", audioPath);
				xmlSerializer.attribute("", "text", text);
				xmlSerializer.attribute("", "type", Long.toString(activityType));
				xmlSerializer.attribute("", "linkto", Long.toString(activityParam));
				xmlSerializer.attribute("", "color", Integer.toString(cellBgColor));
				xmlSerializer.attribute("", "bordercolor", Integer.toString(borderColor));
				xmlSerializer.attribute("", "borderwidth", Integer.toString(cellBorderWidth));
				xmlSerializer.attribute("", "textcolor", Integer.toString(textColor));
				xmlSerializer.attribute("", "textwidth", Integer.toString(textWidth));
				
				if (activityType == Cell.ACTIVITY_TYPE_ABRAKADABRA) {
					abrakadabra.add(activityParam);
				} else if (activityType == Cell.ACTIVITY_TYPE_ACTIVE_LISTENING) {
					activelistening.add(activityParam);
				} else if (activityType == Cell.ACTIVITY_TYPE_PLAY_VIDEO) {
					playvideo.add(activityParam);
				}
				
				xmlSerializer.endTag("", "symbol");
			}
			
			xmlSerializer.endTag("", "table");
		}
			
		// write activities (if exist)
		ChessboardDbUtility dbu = null;
		if (abrakadabra.size() > 0 || activelistening.size() > 0 || playvideo.size() > 0) {
			dbu = new ChessboardDbUtility(this);
			dbu.openReadable();
			xmlSerializer.startTag("", "interactions");
		}

		if (abrakadabra.size() > 0) {
			for (int j = 0; j < abrakadabra.size(); j++) {
				MusicSlidesCursor cursor = dbu
						.getCursorOnMusicSlides(abrakadabra.get(j));
				while (cursor.moveToNext()) {
					MusicSlides ms = cursor.getMusicSlides();

					xmlSerializer.startTag("", "musicslides");
					long mId = ms.getId();
					String mName = ms.getName();
					String[] mImages = ms.getImagePaths();
					String mMusic = ms.getMusicPath();
					
					mMusic = putExternalFile(mMusic);

					xmlSerializer.attribute("", "id", Long.toString(mId));
					xmlSerializer.attribute("", "name", mName);
					StringBuilder sb = new StringBuilder();
					for (int z = 0; z < mImages.length; z++) {
						mImages[z] = putExternalFile(mImages[z]);
						if (z > 0) sb.append(";");
						sb.append(mImages[z]);
					}
					xmlSerializer.attribute("", "images", sb.toString());
					xmlSerializer.attribute("", "audio", mMusic);

					xmlSerializer.endTag("", "musicslides");
				}
				cursor.close();
			}
		}
		abrakadabra.clear();
		abrakadabra = null;

		if (activelistening.size() > 0) {
			for (int j = 0; j < activelistening.size(); j++) {
				ActiveListeningCursor cursor = dbu
						.getCursorOnActiveListening(activelistening.get(j));
				while (cursor.moveToNext()) {
					ActiveListening al = cursor.getActiveListening();

					xmlSerializer.startTag("", "activelistening");
					long mId = al.getId();
					String mName = al.getName();
					String mImage = al.getBackground();
					String[] mAudio = al.getMusicPath();
					int mInterval = al.getInterval();
					int mPause = al.getPause();
					String mRegistration = al.getRegistrationPath();
					int mmInterval = al.getPauseInterval();
					
					mImage = putExternalFile(mImage);
					mRegistration = putExternalFile(mRegistration);

					xmlSerializer.attribute("", "id", Long.toString(mId));
					xmlSerializer.attribute("", "name", mName);
					xmlSerializer.attribute("", "image", mImage);
					StringBuilder sb = new StringBuilder();
					for (int z = 0; z < mAudio.length; z++) {
						mAudio[z] = putExternalFile(mAudio[z]);
						if (z > 0) sb.append(";");
						sb.append(mAudio[z]);
					}
					xmlSerializer.attribute("", "audio", sb.toString());
					xmlSerializer.attribute("", "playinterval", Integer.toString(mInterval));
					xmlSerializer.attribute("", "playpause", Integer.toString(mPause));
					xmlSerializer.attribute("", "message", mRegistration);
					xmlSerializer.attribute("", "messagepause", Long.toString(mmInterval));

					xmlSerializer.endTag("", "activelistening");
				}
				cursor.close();
			}
		}
		activelistening.clear();
		activelistening = null;

		if (playvideo.size() > 0) {
			for (int j = 0; j < playvideo.size(); j++) {
				VideoPlaylistCursor cursor = dbu
						.getCursorOnVideoPlaylist(playvideo.get(j));
				while (cursor.moveToNext()) {
					VideoPlaylist ms = cursor.getVideoPlaylist();

					xmlSerializer.startTag("", "playvideo");
					long mId = ms.getId();
					String mName = ms.getName();
					String[] mVideo = ms.getVideoUrl();

					xmlSerializer.attribute("", "id", Long.toString(mId));
					xmlSerializer.attribute("", "name", mName);
					StringBuilder sb = new StringBuilder();
					for (int z = 0; z < mVideo.length; z++) {
						mVideo[z] = putExternalFile(mVideo[z]);
						if (z > 0)
							sb.append(";");
						sb.append(mVideo[z]);
					}
					xmlSerializer.attribute("", "video", sb.toString());
					xmlSerializer.endTag("", "playvideo");
				}
				cursor.close();
			}
		}
		playvideo.clear();
		playvideo = null;

		if (dbu != null) {
			dbu.close();
			xmlSerializer.endTag("", "interactions");
		}
		dbu = null;

		xmlSerializer.endTag("", "tables");
	}

	private String putExternalFile(String filePath) {
		if(filePath == null)
			return "";
		
		if (filePath.equals(Configurations.IMAGE_BACK_NAME)) {
			return filePath;
		} else if (filePath.equals(Configurations.IMAGE_NEW_NAME)) {
			return filePath;
		} else if(filePath.startsWith(Configurations.YOUTUBE_PREFIX)) {
			return filePath;
		} else if(filePath.startsWith(Configurations.TTS_PREFIX)) {
			return filePath;
		}
		
		File file = FileUtils.getResourceFile(filePath);
		if(file == null) {
			return filePath;
		}
		
		String name = "file_" + file.getName();
		int count = 1;
		while(this.filenameset.containsKey(name) == true) {
			name = "file__" + count + "_" + file.getName();
			count++;
		}
		this.filenameset.put(name, filePath);
		return name;
	}
	

	
	private Boolean loadFromFile() {
		// get file path
		File inFile = new File(importPath);

		ZipInputStream zipInStream = null;
		boolean success = true;
		try {
			InputStream os = new FileInputStream(inFile);
			zipInStream = new ZipInputStream(new BufferedInputStream(os));

			getXmlIndex(zipInStream);
			
			ZipEntry entry = zipInStream.getNextEntry();
			while(entry != null) {
				String entryName = entry.getName();
				String destinationPath = filenameset.get(entryName);
				
				File destinationFile = FileUtils.getResourceFileForWrite(destinationPath);
				FileOutputStream destOutStream = new FileOutputStream(destinationFile);
				if(ChessboardApplication.DEBUG_IMPORT_EXPORT) {
					Log.w(getClass().getName(), "start import " + importPath + " -> " + destinationFile.getPath());
				}
				int bufferSize = 8192;
				byte data[] = new byte[bufferSize];
				int count;
				while ((count = zipInStream.read(data, 0, bufferSize)) != -1) {
					destOutStream.write(data, 0, count);
				}
				destOutStream.close();
				if(ChessboardApplication.DEBUG_IMPORT_EXPORT) {
					Log.w(getClass().getName(), "stop import " + importPath + " -> " + destinationFile.getPath());
				}
				
				entry = zipInStream.getNextEntry();
			}
		} catch (IOException e) {
			Log.d(getClass().getName(), e.getMessage());
			success = false;
		} catch (XmlPullParserException e) {
			Log.d(getClass().getName(), e.getMessage());
			success = false;
		} finally {
			try {
				zipInStream.close();
			} catch (IOException e) {
				Log.d(getClass().getName(), e.getMessage());
				success = false;
			}
		}

		if (filenameset != null) {
			filenameset.clear();
			filenameset = null;
		}
		
		ChessboardDbUtility dbu = new ChessboardDbUtility(this);
		dbu.openWritable();
		
		if (overwrite == true) {
			dbu.clear();
		}
		appendToDatabase(dbu);

		dbu.close();

		return success;
	}
	
	private void getXmlIndex(ZipInputStream zis) throws IOException, XmlPullParserException {
		ZipEntry entry = zis.getNextEntry();
		if(ChessboardApplication.DEBUG_IMPORT_EXPORT) {
			if(entry.getName().equals("content.xml") == false) {
				Log.e(getClass().getName(), "expected content.xml");
			}
			Log.w(getClass().getName(), "start import xml file");
		}
		
        // get temporary file
        File cacheDir = getCacheDir();
        File tempFile = File.createTempFile(Configurations.EXPORT_CONTENT_PREFIX, Configurations.EXPORT_CONTENT_POSTFIX, cacheDir);
        FileOutputStream fow = new FileOutputStream(tempFile);
		byte[] buffer = new byte[8192];
		int count;
		while ((count = zis.read(buffer)) != -1) {
			fow.write(buffer, 0, count);
		}
		fow.close();
		
		FileInputStream inputStream = new FileInputStream(tempFile);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xmlParser = factory.newPullParser();
        xmlParser.setInput(inputStream, null);
        parseXmlIndex(xmlParser);
        xmlParser = null;
        inputStream.close();
        
		if(ChessboardApplication.DEBUG_IMPORT_EXPORT) {
			Log.w(getClass().getName(), "end import xml file");
		}
	}
	
	private void parseXmlIndex(XmlPullParser xmlParser) throws IllegalArgumentException, IllegalStateException, IOException, XmlPullParserException {
		int cTag = xmlParser.nextTag(); // skip tables
		
		filenameset = new TreeMap<String, String>();

		LinkedList<Chessboard> cbs = new LinkedList<Chessboard>();
		LinkedList<Cell[]> cbCells = new LinkedList<Cell[]>();
		cTag = xmlParser.nextTag();
		while(cTag == XmlPullParser.START_TAG && xmlParser.getName().equals("table")) {
			Chessboard cb = null;
			LinkedList<Cell> cells = new LinkedList<Cell>();
			
			long id = Long.parseLong(xmlParser.getAttributeValue(null, "id"));
			int parentId = Integer.parseInt(xmlParser.getAttributeValue(null, "parent"));
			String name = xmlParser.getAttributeValue(null, "name");
			int rowCount = Integer.parseInt(xmlParser.getAttributeValue(null, "rows"));
			int columnCount = Integer.parseInt(xmlParser.getAttributeValue(null, "columns"));
			int bgColor = Integer.parseInt(xmlParser.getAttributeValue(null, "color"));
			int borderWidth = Integer.parseInt(xmlParser.getAttributeValue(null, "border"));
			cb = new Chessboard(id, parentId, name, rowCount, columnCount, bgColor, borderWidth);
			
			cTag = xmlParser.nextTag();
			while(cTag == XmlPullParser.START_TAG && xmlParser.getName().equals("symbol")) {
				long chessboard = cb.getId();
				long cellId = Long.parseLong(xmlParser.getAttributeValue(null, "id"));
				int row = Integer.parseInt(xmlParser.getAttributeValue(null, "row"));
				int column = Integer.parseInt(xmlParser.getAttributeValue(null, "column"));
				String cellName = xmlParser.getAttributeValue(null, "name");
				String imagePath = xmlParser.getAttributeValue(null, "image");
				String audioPath = xmlParser.getAttributeValue(null, "audio");
				String text = xmlParser.getAttributeValue(null, "text");
				int activityType = Integer.parseInt(xmlParser.getAttributeValue(null, "type"));
				long activityParam = Long.parseLong(xmlParser.getAttributeValue(null, "linkto"));
				int cellBgColor = Integer.parseInt(xmlParser.getAttributeValue(null, "color"));
				int borderColor = Integer.parseInt(xmlParser.getAttributeValue(null, "bordercolor"));
				int cellBorderWidth = Integer.parseInt(xmlParser.getAttributeValue(null, "borderwidth"));
				int textColor = Integer.parseInt(xmlParser.getAttributeValue(null, "textcolor"));
				int textWidth = Integer.parseInt(xmlParser.getAttributeValue(null, "textwidth"));
				
				imagePath = getExternalFile(imagePath);
				audioPath = getExternalFile(audioPath);
				
				Cell cell = new Cell(cellId, chessboard, cellName, row, column, cellBgColor, cellBorderWidth, borderColor, text, textWidth, textColor, imagePath, audioPath, activityType, activityParam);
				cells.add(cell);
				
				cTag = xmlParser.nextTag(); // </symbol>
				cTag = xmlParser.nextTag();
				if(cTag == XmlPullParser.END_TAG) {
					cTag = xmlParser.nextTag();
				}
			}
			
			cbs.add(cb);
			cbCells.add(cells.toArray(new Cell[cells.size()]));
			cells.clear();
			cells = null;
		}
		
		this.cbs = cbs.toArray(new Chessboard[cbs.size()]);
		this.cbCells = cbCells.toArray(new Cell[cbCells.size()][]);
		cbs.clear();
		cbCells.clear();
		cbs = null;
		cbCells = null;
		
		if(cTag == XmlPullParser.START_TAG && xmlParser.getName().equals("interactions")) {
			ArrayList<MusicSlides> abrakadabra = new ArrayList<MusicSlides>();
			ArrayList<ActiveListening> activelistening = new ArrayList<ActiveListening>();
			ArrayList<VideoPlaylist> playvideo = new ArrayList<VideoPlaylist>();
			cTag = xmlParser.nextTag();
			while(cTag == XmlPullParser.START_TAG && xmlParser.getName().equals("musicslides")) {
				long mId = Long.parseLong(xmlParser.getAttributeValue(null, "id"));
				String mName = xmlParser.getAttributeValue(null, "id");
				String[] mImages = xmlParser.getAttributeValue(null, "images").split(";");
				String mMusic = xmlParser.getAttributeValue(null, "audio");
				
				mImages = getExternalFile(mImages);
				mMusic = getExternalFile(mMusic);
				
				MusicSlides ms = new MusicSlides(mId, mName, mImages, mMusic);
				abrakadabra.add(ms);
				
				cTag = xmlParser.nextTag();
				cTag = xmlParser.nextTag();
				if(cTag == XmlPullParser.END_TAG) {
					cTag = xmlParser.nextTag();
				}
			}

	
			while(cTag == XmlPullParser.START_TAG && xmlParser.getName().equals("activelistening")) {
				long mId = Long.parseLong(xmlParser.getAttributeValue(null, "id"));
				String mName = xmlParser.getAttributeValue(null, "name");
				String mImage = xmlParser.getAttributeValue(null, "image");
				String[] mAudio = xmlParser.getAttributeValue(null, "audio").split(";");
				int mInterval = Integer.parseInt(xmlParser.getAttributeValue(null, "playinterval"));
				int mPause = Integer.parseInt(xmlParser.getAttributeValue(null, "playpause"));
				String mRegistration = xmlParser.getAttributeValue(null, "message");
				int mmInterval = Integer.parseInt(xmlParser.getAttributeValue(null, "messagepause"));

				mAudio = getExternalFile(mAudio);
				mImage = getExternalFile(mImage);
				mRegistration = getExternalFile(mRegistration);
				
				ActiveListening al = new ActiveListening(mId, mName, mImage, mAudio, mInterval, mRegistration, mPause, mmInterval);
				activelistening.add(al);
	
				cTag = xmlParser.nextTag();
				cTag = xmlParser.nextTag();
				if(cTag == XmlPullParser.END_TAG) {
					cTag = xmlParser.nextTag();
				}
			}
	
			while (cTag == XmlPullParser.START_TAG && xmlParser.getName().equals("playvideo")) {
				long mId = Long.parseLong(xmlParser.getAttributeValue(null, "id"));
				String mName = xmlParser.getAttributeValue(null, "name");
				String[] mVideo = xmlParser.getAttributeValue(null, "video").split(";");

				mVideo = getExternalFile(mVideo);
				
				VideoPlaylist vp = new VideoPlaylist(mId, mName, mVideo);
				playvideo.add(vp);

				cTag = xmlParser.nextTag();
				cTag = xmlParser.nextTag();
				if(cTag == XmlPullParser.END_TAG) {
					cTag = xmlParser.nextTag();
				}
			}
			
			this.musicSlides = abrakadabra.toArray(new MusicSlides[abrakadabra.size()]);
			this.activeListening = activelistening.toArray(new ActiveListening[activelistening.size()]);
			this.videoPlaylist = playvideo.toArray(new VideoPlaylist[playvideo.size()]);
		}
		
		Log.d(getClass().getName(), xmlParser.getPositionDescription());
	}

	private String[] getExternalFile(String[] files) {
		for(int i = 0; i < files.length; i++) {
			files[i] = getExternalFile(files[i]);
		}
		return files;
	}

	private String getExternalFile(String filePath) {
		if (filePath.equals(Configurations.IMAGE_BACK_NAME)) {
			return filePath;
		} else if (filePath.equals(Configurations.IMAGE_NEW_NAME)) {
			return filePath;
		} else if(filePath.startsWith(Configurations.YOUTUBE_PREFIX)) {
			return filePath;
		} else if(filePath.startsWith(Configurations.TTS_PREFIX)) {
			return filePath;
		}
		
		File destDirPrefix = new File(Constants.getInstance(this).FILE_DIR, Constants.getInstance(this).IMPORT_DIR + "__" + dateStr);
		File destFilePath = new File(destDirPrefix, filePath.substring("file_".length()));
		String destinationPath = destFilePath.getPath();
		this.filenameset.put(filePath, destinationPath);
		return destinationPath;
	}

	private void appendToDatabase(ChessboardDbUtility dbu) {		
		// track ids changes
		Map<Long, Long> musicSlidesIds = new TreeMap<Long, Long>();
		Map<Long, Long> activeListeningIds = new TreeMap<Long, Long>();
		Map<Long, Long> videoPlaylistIds = new TreeMap<Long, Long>();
		Map<Long, Long> chessboardIds = new TreeMap<Long, Long>();
		
		// add music slides data
		if(musicSlides == null) {
			musicSlides = new MusicSlides[0];
		}
		for(int i = 0; i < musicSlides.length; i++) {
			MusicSlides ms = musicSlides[i];
			
			long newId = dbu.addMusicSlides(ms.getName(),
					ms.getMusicPath(),
					ms.getImagePaths());
		
			musicSlidesIds.put(ms.getId(), newId);
		}
		
		// add active listening data
		if(activeListening == null) {
			activeListening = new ActiveListening[0];
		}
		for(int i = 0; i < activeListening.length; i++) {
			ActiveListening al = activeListening[i];
			
			long newId = dbu.addActiveListening(al.getName(),
					al.getBackground(),
					al.getMusicPath(),
					al.getInterval(),
					al.getRegistrationPath(),
					al.getPause(),
					al.getPauseInterval());
			
			activeListeningIds.put(al.getId(), newId);
		}
		
		// add video playlist data
		if(videoPlaylist == null) {
			videoPlaylist = new VideoPlaylist[0];
		}
		for(int i = 0; i < videoPlaylist.length; i++) {
			VideoPlaylist vp = videoPlaylist[i];
			
			long newId = dbu.addVideoPlaylist(vp.getName(),
					vp.getVideoUrl());
			
			videoPlaylistIds.put(vp.getId(), newId);
		}
		
		// add chessboards
		if(cbs == null) {
			cbs = new Chessboard[0];
		}
		for(int i = 0; i < cbs.length; i++) {
			Chessboard cb = cbs[i];
			
			long newId = dbu.addChessboard(cb.getParentId(),
					cb.getName(),
					cb.getRowCount(),
					cb.getColumnCount(),
					cb.getBackgroundColor(),
					cb.getBorderWidth());
			
			chessboardIds.put(cb.getId(), newId);
		}
		
		// add cells
		for(int i = 0; i < cbCells.length; i++) {
			Cell[] cells = cbCells[i];
			for(int j = 0; j < cells.length; j++) {
				Cell cell = cells[j];
				
				// update ids
				long newChessboard = chessboardIds.get(cell.getChessboard());
				int newActivityType = cell.getActivityType();
				long newActivityParam = cell.getActivityParam();
				if(newActivityType == Cell.ACTIVITY_TYPE_ABRAKADABRA) {
					if (musicSlidesIds.containsKey(newActivityParam)) {
						newActivityParam = musicSlidesIds.get(newActivityParam);
					} else {
						newActivityType = Cell.ACTIVITY_TYPE_NONE;
						newActivityParam = 0;
					}
				} else if(newActivityType == Cell.ACTIVITY_TYPE_ACTIVE_LISTENING) {
					if (activeListeningIds.containsKey(newActivityParam)) {
						newActivityParam = activeListeningIds.get(newActivityParam);
					} else {
						newActivityType = Cell.ACTIVITY_TYPE_NONE;
						newActivityParam = 0;
					}
				} else if(newActivityType == Cell.ACTIVITY_TYPE_PLAY_VIDEO) {
					if (videoPlaylistIds.containsKey(newActivityParam)) {
						newActivityParam = videoPlaylistIds.get(newActivityParam);
					} else {
						newActivityType = Cell.ACTIVITY_TYPE_NONE;
						newActivityParam = 0;
					}
				} else if(newActivityType == Cell.ACTIVITY_TYPE_CLOSE_CHESSBOARD) {
					newActivityParam = 0;
				} else if(newActivityType == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD) {
					if(chessboardIds.containsKey(newActivityParam)) {
						newActivityParam = chessboardIds.get(newActivityParam);
					} else {
						newActivityType = Cell.ACTIVITY_TYPE_NONE;
						newActivityParam = 0;
					}
				} else if(newActivityType == Cell.ACTIVITY_TYPE_NONE) {
					newActivityParam = 0;
				}
				
				/* long newId = */ dbu.addCell(newChessboard,
						cell.getName(),
						cell.getRow(),
						cell.getColumn(),
						cell.getBackgroundColor(),
						cell.getBorderWidth(),
						cell.getBorderColor(),
						cell.getText(),
						cell.getTextWidth(),
						cell.getTextColor(),
						cell.getImagePath(),
						cell.getAudioPath(),
						newActivityType,
						newActivityParam);
			}
		}
		
		// clear id maps (help gc)
		musicSlidesIds.clear();
		activeListeningIds.clear();
		videoPlaylistIds.clear();
		chessboardIds.clear();
		musicSlidesIds = null;
		activeListeningIds = null;
		videoPlaylistIds = null;
		chessboardIds = null;
	}
	
	private OnClickListener onOverwriteImport = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Resources res = getResources();
			String title = res.getString(R.string.activity_choose_destination_override_warning_title);
			String message = res.getString(R.string.activity_choose_destination_override_warning_message);
			String confirm = res.getString(R.string.activity_choose_destination_override_warning_confirm);
			String abort = res.getString(R.string.activity_choose_destination_override_warning_abort);
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ChooseDestinationActivity.this);
			dialogBuilder.setTitle(title);
			dialogBuilder.setMessage(message);
			dialogBuilder.setPositiveButton(confirm, new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int witch) {
					Resources res = getResources();
					helpText.setText(res.getString(R.string.activity_choose_destination_wait));
					filePathText.setText("");
					filePathText.setVisibility(View.INVISIBLE);
					openFile.setVisibility(View.INVISIBLE);
					close.setVisibility(View.INVISIBLE);
					
					overwrite = true;
					LoadFromFile task = new LoadFromFile();
					task.execute();
				}
			});
			dialogBuilder.setNegativeButton(abort, new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do nothing
				}
			});
			AlertDialog dialog = dialogBuilder.create();
			dialog.show();
		}
	};
	
	private OnClickListener onAppendImport = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Resources res = getResources();
			helpText.setText(res.getString(R.string.activity_choose_destination_wait));
			filePathText.setText("");
			filePathText.setVisibility(View.INVISIBLE);
			openFile.setVisibility(View.INVISIBLE);
			close.setVisibility(View.INVISIBLE);
			
			overwrite = false;
			LoadFromFile task = new LoadFromFile();
			task.execute();
		}
	};

	private OnClickListener onClose = new OnClickListener() {
		@Override
		public void onClick(View view) {
			finish();
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_destination, menu);
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
