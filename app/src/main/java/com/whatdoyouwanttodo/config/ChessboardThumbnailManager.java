package com.whatdoyouwanttodo.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.db.CellCursor;
import com.whatdoyouwanttodo.db.ChessboardCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.utils.FileUtils;

/**
 * Classe che gestisce la creazione e il riuso di miniature per le tabelle
 */
public class ChessboardThumbnailManager {
	public static final int THUMBNAIL_HEIGHT = 200;
	public static final int THUMBNAIL_APPROXIMATE_WIDTH = 300;
	private static ChessboardThumbnailManager instance = null;

	public static ChessboardThumbnailManager getInstance(Context context) {
		if(instance  == null)
			instance = new ChessboardThumbnailManager(context);
		return instance;
	}
	
	private TreeMap<Long, FileWithChecksum> fileMap = null;
	private Context context = null;
	
	private static final class FileWithChecksum {
		private String filePath = null;
		private int checksum = 0;
		
		public FileWithChecksum(String filePath, int checksum) {
			this.filePath = filePath;
			this.checksum = checksum;
		}
		
		public String getImagePath() {
			return filePath;
		}

		public int getChecksum() {
			return checksum;
		}
	}
	
	private ChessboardThumbnailManager(Context context) {
		this.context = context;
		this.fileMap  = new TreeMap<Long, FileWithChecksum>();
		File outputDir = getOutputFile();       
		File file[] = outputDir.listFiles();
		for (int i=0; i < file.length; i++)
		{
			String name = file[i].getName();
			String[] names = name.split("___");
			long id = Long.parseLong(names[1]);
			int checksum = Integer.parseInt(names[2]);
			fileMap.put(id, new FileWithChecksum(file[i].getPath(), checksum));
		}
	}

	public String getThumbnailPathOf(Context context, Chessboard cb, Cell[] cells) {
		this.context = context;

		FileWithChecksum imageFC = fileMap.get(cb.getId());
		boolean exist = false;
		if (imageFC != null) {
			File imageFile = new File(imageFC.getImagePath());
			exist = imageFile.exists();
		}
		boolean checksumCheck = false;
		int currentChecksum = getChecksum(cb, cells);
		if(imageFC != null) {
			if(imageFC.getChecksum() == currentChecksum) {
				checksumCheck = true;
			}
		}
		if (imageFC == null || exist == false || checksumCheck == false) {
			Bitmap bitmap = drawChessboardToBitmap(cb, cells);
			imageFC = new FileWithChecksum(saveToCache(bitmap, cb.getId(), currentChecksum), currentChecksum);
			fileMap.put(cb.getId(), imageFC);
		}

		return imageFC.getImagePath();
	}

	public String getThumbnailPathOf(Context context, ChessboardDbUtility dbu, long chessboardId) {
		this.context = context;
		
		Chessboard cb = readChessboard(dbu, chessboardId);
		Cell[] cells = readCells(dbu, chessboardId);
		
		FileWithChecksum imageFC = fileMap.get(chessboardId);
		boolean exist = false;
		if (imageFC != null) {
			File imageFile = new File(imageFC.getImagePath());
			exist = imageFile.exists();
		}
		boolean checksumCheck = false;
		int currentChecksum = getChecksum(cb, cells);
		if(imageFC != null) {
			if(imageFC.getChecksum() == currentChecksum) {
				checksumCheck = true;
			}
		}
		if (imageFC == null || exist == false || checksumCheck == true) {
			Bitmap bitmap = drawChessboardToBitmap(cb, cells);
			imageFC = new FileWithChecksum(saveToCache(bitmap, chessboardId, currentChecksum), currentChecksum);
			fileMap.put(chessboardId, imageFC);
		}
		
		return imageFC.getImagePath();
	}
	


	private int getChecksum(Chessboard cb, Cell[] cells) {
		int checksum = 0;
		checksum += cb.getBorderWidth();
		checksum += cb.getBackgroundColor();
		checksum += cb.getRowCount();
		checksum += cb.getColumnCount();
		
		for(int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			checksum += cell.getBackgroundColor();
			checksum += cell.getBorderColor();
			checksum += cell.getBorderWidth();
			checksum += cell.getColumn();
			checksum += cell.getImagePath().hashCode();
			checksum += cell.getRow();
			checksum += cell.getText().hashCode();
			checksum += cell.getTextColor();
			checksum += cell.getTextWidth();
		}
		
		return checksum;
	}

	private Bitmap drawChessboardToBitmap(Chessboard cb, Cell[] cells) {
		int columnCount = cb.getColumnCount();
		int rowCount = cb.getRowCount();
		
		int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, THUMBNAIL_APPROXIMATE_WIDTH, context.getResources().getDisplayMetrics());
		int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, THUMBNAIL_HEIGHT, context.getResources().getDisplayMetrics());
		w = h * columnCount / rowCount;
		
		Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);

		// draw background
		int bgColor = cb.getBackgroundColor();
		c.drawColor(bgColor);
		
		// draw cells
		int border = cb.getBorderWidth();
		
		int cellWidth = w;
		int cellHeight = h;
		for(int i = 0; i <= columnCount; i++) {
			cellWidth -= border;
		}
		for(int i = 0; i <= rowCount; i++) {
			cellHeight -= border;
		}
		cellWidth /= columnCount;
		cellHeight /= rowCount;
		for(int i = 0; i < cells.length; i++) {
			Cell cell = cells[i];
			int cellRow = cell.getRow();
			int cellColumn = cell.getColumn();
			
	
			int left = border * (cellColumn + 1) + cellWidth * cellColumn;
			int right = left + cellWidth;
			int top = border * (cellRow + 1) + cellHeight * cellRow;
			int bottom = top + cellHeight;
			drawCell(c, left, top, right, bottom, cell);
		}
		
		return b;
	}

	private void drawCell(Canvas c, int left, int top, int right, int bottom, Cell cell) {
		// draw border
		Paint paint = new Paint();
		int borderColor = cell.getBorderColor();
		paint.setColor(borderColor);
		paint.setStyle(Paint.Style.FILL);
		c.drawRect(left, top, right, bottom, paint);
		
		// draw background
		int borderWidth = cell.getBorderWidth();
		int bgColor = cell.getBackgroundColor();
		left += borderWidth;
		right -= borderWidth;
		top += borderWidth;
		bottom -= borderWidth;
		paint.setColor(bgColor);
		c.drawRect(left, top, right, bottom, paint);
		
		// draw text
		left += 4;
		right -= 4;
		top += 4;
		bottom -= 4;
		int contentWidth = right - left;
		int contentHeight = bottom - top;
		int textColor = cell.getTextColor();
		int textSize = cell.getTextWidth();
		if(textSize > (contentHeight / 4)) {
			textSize = contentHeight / 4;
		}
		if(textSize > 6) {
			String label = cell.getText();
			paint.setColor(textColor);
			paint.setAntiAlias(true);
			paint.setTextSize(textSize);
			float textWidth = paint.measureText(label);
			float textScale = 1.0f;
			float textEffectiveWidth = 0.0f;
			float textDiff = 0.0f;
			if (textWidth > (contentWidth * 0.99)) {
				textScale = contentWidth / textWidth;
				textEffectiveWidth = textWidth * textScale;
				textDiff = 0;
			} else {
				textEffectiveWidth = textWidth;
				textDiff = (contentWidth - textEffectiveWidth) / 2;
			}
			c.save();
			c.translate(left + textDiff, bottom);
			if (textWidth > (contentWidth * 0.99)) {
				c.scale(textScale, 1.0f);
			}
			c.drawText(label, 0, 0, paint);
			c.restore();
		}

		// draw image
		bottom -= textSize + 4;
		String imagePath = cell.getImagePath();
		if(imagePath.equals(Configurations.IMAGE_BACK_NAME)) {
			Drawable d = context.getResources().getDrawable(R.drawable.cell_back_high);
			d.setBounds(left, top, right, bottom);
			d.draw(c);
		} else if(imagePath.equals(Configurations.IMAGE_NEW_NAME)) {
			Drawable d = context.getResources().getDrawable(R.drawable.cell_new_high);
			d.setBounds(left, top, right, bottom);
			d.draw(c);
		} else {
			File dirFile = FileUtils.getStorageDirectory();
			String path;
			File imgFile = new File(dirFile, imagePath);
			if(imgFile.exists()) {
				path = imgFile.getAbsolutePath();
			} else {
				imgFile = new File(imagePath);
				path = imgFile.getAbsolutePath();
			}
			if(imgFile.exists() == true) {
				if(path.endsWith(Configurations.SVG_EXT)) {
					String rastImage = path.replace(Configurations.SVG_EXT, Configurations.SVG_RASTERIZED_POSTFIX);
					File file = new File(rastImage);
					if(file.exists() == false) {
						try {
							FileInputStream svgInputStream = new FileInputStream(path);
							SVG svg = SVG.getFromInputStream(svgInputStream);
							if (svg.getDocumentWidth() != -1) {
								Bitmap bitmap = Bitmap.createBitmap(
										(int) svg.getDocumentWidth(),
										(int) svg.getDocumentHeight(),
										Bitmap.Config.ARGB_8888);
								Canvas bmcanvas = new Canvas(bitmap);
								svg.renderToCanvas(bmcanvas);
								FileOutputStream out = null;
								try {
									out = new FileOutputStream(rastImage);
									bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
								} catch (Exception e) {
									// do nothing
								} finally {
									try {
										out.close();
									} catch (Throwable ignore) {
									}
								}
							}
						} catch (FileNotFoundException e) {
							// skip this image
						} catch (SVGParseException e) {
							// skip this image (invalid or not supported)
						}
					}
					path = rastImage;
					
				}
					
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				
				if(bitmap != null) {
					Rect imageSrc = new Rect();
					imageSrc.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
					
					Rect imageDest = new Rect();
					imageDest.set(left, top, right, bottom);
					
					c.drawBitmap(bitmap, imageSrc, imageDest, null);
				}
			}
		}
	}

	public void clear() {
		// clean all used temporary thumbnail files and this object
		// do nothing
	}
	
	private Chessboard readChessboard(ChessboardDbUtility dbu, long chessboardId) {
		Chessboard cb = null;
		ChessboardCursor cursorCb = dbu.getCursorOnChessboard(chessboardId);
		if (cursorCb != null) {
			while (cursorCb.moveToNext()) {
				cb = cursorCb.getChessboard();
			}
			cursorCb.close();
		}
		return cb;
	}

	private Cell[] readCells(ChessboardDbUtility dbu, long chessboardId) {
		LinkedList<Cell> cells = new LinkedList<Cell>();
		CellCursor cursor = dbu.getCursorOnCell(chessboardId);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				cells.add(cursor.getCell());
			}
			cursor.close();
		}
		return cells.toArray(new Cell[cells.size()]);
	}
	
	private String saveToCache(Bitmap bitmapImage, long gridId, int checksum) {
		File outputDir = getOutputFile();
		File outputFile = null;
		// Create a file
		String imageFileName = "img" + "___" + gridId + "___" + checksum;
		outputFile = new File(outputDir, imageFileName);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outputFile);
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			return null;
		}
		return outputFile.getPath();
	}

	private File getOutputFile() {
		Constants ct = Constants.getInstance(context);
		File outFile = new File (new File(FileUtils.getStorageDirectory(), ct.FILE_DIR), ct.THUMBNAIL_DIR);
		outFile.mkdirs();
		return outFile;
	}
}
