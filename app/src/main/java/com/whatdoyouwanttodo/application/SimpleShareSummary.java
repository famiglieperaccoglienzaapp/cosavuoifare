package com.whatdoyouwanttodo.application;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.config.ChessboardThumbnailManager;
import com.whatdoyouwanttodo.config.ShareSummaryActivity;
import com.whatdoyouwanttodo.db.ActiveListeningCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.db.MusicSlidesCursor;
import com.whatdoyouwanttodo.db.VideoPlaylistCursor;
import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.utils.FileUtils;

/**
 * Modella un insieme di tabelle da visualizzare
 */
public class SimpleShareSummary implements
		ShareSummaryActivity.ShareSummaryModel {
	private Chessboard[] cb;
	private Cell[][] cbCells;
	private Activity activity;
	private String title;
	private long globalSize = 0;
	private int globalCellLength = 0;
	private int globalCellActs = 0;
	private ShareListener callback = null;
	
	public static interface ShareListener {
		void share(Chessboard[] cb, Cell[][] cbCells);
	}

	public SimpleShareSummary(Activity activity, String title, Chessboard[] cb, Cell[][] cells) {
		this.activity = activity;
		this.title = title;
		this.cb = cb;
		this.cbCells = cells;
	}

	public void setCallback(ShareListener callback) {
		this.callback = callback;
	}

	@Override
	public String getItemName(int i) {
		return cb[i].getName();
	}

	@Override
	public String getItemImage(int i) {
		String path = ChessboardThumbnailManager.getInstance(activity)
				.getThumbnailPathOf(activity, cb[i], cbCells[i]);
		return path;
	}

	@Override
	public String getItemDescription1(int i) {		
		Cell[] cells = cbCells[i];
	
		int nAct = 0;
		for(int j = 0; j < cells.length; j++) {
			int aType = cells[j].getActivityType();
			if(aType == Cell.ACTIVITY_TYPE_ABRAKADABRA ||
					aType == Cell.ACTIVITY_TYPE_ACTIVE_LISTENING ||
					aType == Cell.ACTIVITY_TYPE_PLAY_VIDEO) {
				nAct++;
			}
		}
		
		globalCellLength += cells.length;
		globalCellActs += nAct;
		
		Resources res = activity.getResources();
		String d1 = res.getString(R.string.class_simple_share_summary_description1);
		String d2 = res.getString(R.string.class_simple_share_summary_description2);
		return cells.length + d1 + nAct + d2;
	}

	@Override
	public String getItemDescription2(int i) {
		Cell[] cells = cbCells[i];
		
		long size = 124; // chessboard data size
		
		ArrayList<Long> abrakadabra = new ArrayList<Long>(cells.length);
		ArrayList<Long> activelistening = new ArrayList<Long>(cells.length);
		ArrayList<Long> playvideo = new ArrayList<Long>(cells.length);
		for(int j = 0; j < cells.length; j++) {
			size += 128; // cell data size without image and activity-1;
			
			String image = cells[j].getImagePath();
			size += getSize(image);
			
			int aType = cells[j].getActivityType();
			if(aType == Cell.ACTIVITY_TYPE_ABRAKADABRA) {
				abrakadabra.add(cells[j].getActivityParam());
			} else if(aType == Cell.ACTIVITY_TYPE_ACTIVE_LISTENING) {
				activelistening.add(cells[j].getActivityParam());
			} else if(aType == Cell.ACTIVITY_TYPE_PLAY_VIDEO) {
				playvideo.add(cells[j].getActivityParam());
			}
		}
		
		ChessboardDbUtility dbu = null;
		
		if(abrakadabra.size() > 0) {
			if(dbu == null) {
				dbu = new ChessboardDbUtility(activity);
				dbu.openReadable();
			}
			
			for(int j = 0; j < abrakadabra.size(); j++) {
				MusicSlidesCursor cursor = dbu.getCursorOnMusicSlides(abrakadabra.get(j));
				while(cursor.moveToNext()) {
					MusicSlides ms = cursor.getMusicSlides();
					
					size += getSize(ms.getImagePaths());
					size += getSize(ms.getMusicPath());
				}
			}
		}
		abrakadabra.clear();
		abrakadabra = null;
		
		if(activelistening.size() > 0) {
			if(dbu == null) {
				dbu = new ChessboardDbUtility(activity);
				dbu.openReadable();
			}
			
			for(int j = 0; j < activelistening.size(); j++) {
				ActiveListeningCursor cursor = dbu.getCursorOnActiveListening(activelistening.get(j));
				while(cursor.moveToNext()) {
					ActiveListening al = cursor.getActiveListening();
					
					size += getSize(al.getBackground());
					size += getSize(al.getMusicPath());
					size += getSize(al.getRegistrationPath());
				}
			}
		}
		activelistening.clear();
		activelistening = null;
		
		if(playvideo.size() > 0) {
			if(dbu == null) {
				dbu = new ChessboardDbUtility(activity);
				dbu.openReadable();
			}
			
			for(int j = 0; j < playvideo.size(); j++) {
				VideoPlaylistCursor cursor = dbu.getCursorOnVideoPlaylist(playvideo.get(j));
				while(cursor.moveToNext()) {
					VideoPlaylist ms = cursor.getVideoPlaylist();
					
					String[] videourl = ms.getVideoUrl();
					for(int z = 0; z < videourl.length; z++) {
						if(videourl[z] != null) {
							if(videourl[z].startsWith(Configurations.YOUTUBE_PREFIX) == false) {
								size += getSize(videourl[z]);
							}
						}
					}
				}
			}
		}
		playvideo.clear();
		playvideo = null;
		
		if(dbu != null) {
			dbu.close();
		}
		dbu = null;
		
		globalSize  += size;
		
		return formatSize(size);
	}

	@Override
	public int getItemCount() {
		return cb.length;
	}

	@Override
	public String getHelpText() {
		return title;
	}

	private String formatSize(long size) {
		long sizeKb = size / 1024;
		long sizeMb = sizeKb / 1024;
			
		Resources res = activity.getResources();
		String mbStr = res.getString(R.string.class_simple_share_summary_mb);
		String kbStr = res.getString(R.string.class_simple_share_summary_kb);
		String bStr = res.getString(R.string.class_simple_share_summary_b);
		
		if (sizeMb > 0) {
			NumberFormat nf = NumberFormat.getInstance();
			return nf.format(sizeMb) + mbStr;
		} else if (sizeKb > 0) {
			NumberFormat nf = NumberFormat.getInstance();
			return nf.format(sizeKb) + kbStr;
		} else {
			NumberFormat nf = NumberFormat.getInstance();
			return nf.format(size) + bStr;
		}
	}

	private long getSize(String[] images) {
		int size = 0;
		for (int i = 0; i < images.length; i++) {
			String image = images[i];
			if (image != null) {
				if (image.equals("") == false) {
					File imageFile = FileUtils.getResourceFile(image);
					if (imageFile != null) {
						if (imageFile.exists() == true) {
							long len = imageFile.length();
							size += len;
						}
					}
				}
			}
		}
		return size;
	}

	private long getSize(String image) {
		int size = 0;
		if (image != null) {
			if (image.equals("") == false) {
				File imageFile = FileUtils.getResourceFile(image);
				if (imageFile != null) {
					if (imageFile.exists() == true) {
						long len = imageFile.length();
						size += len;
					}
				}
			}
		}
		return size;
	}

	@Override
	public String getItemsDescription1() {
		Resources res = activity.getResources();
		String d1 = res.getString(R.string.class_simple_share_summary_description1);
		String d2 = res.getString(R.string.class_simple_share_summary_description2);
		String d3 = res.getString(R.string.class_simple_share_summary_description3);
		
		return cb.length + d3 + globalCellLength + d1 + globalCellActs + d2;
	}

	@Override
	public String getItemsDescription2() {
		return formatSize(globalSize);
	}

	@Override
	public void confirmShare() {
		if (callback != null) {
			callback.share(cb, cbCells);
		}
	}
}