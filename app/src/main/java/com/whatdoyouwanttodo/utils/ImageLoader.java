package com.whatdoyouwanttodo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.whatdoyouwanttodo.AacButton;
import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.settings.Configurations;

public final class ImageLoader {
	private static ImageLoader instance = null;

	private ImageLoader() { }

	public static ImageLoader getInstance() {
		if (instance == null)
			instance = new ImageLoader();
		return instance;
	}

	private LinkedList<ImageAdapter> images = new LinkedList<ImageAdapter>();

	public boolean existImage(String imagePath) {
		File dirFile = FileUtils.getStorageDirectory();
		if(dirFile == null)
			return false;
		
		String directoryPath = dirFile.toString();
		
		// is relative
		File imgFile = new File(directoryPath, imagePath);
		if(imgFile.exists())
			return true;
		
		// is absolute
		imgFile = new File(imagePath);
		if(imgFile.exists())
			return true;
		
		// is resources
		if (imagePath.equals(Configurations.IMAGE_BACK_NAME)) {
			return true;
		} else if(imagePath.equals(Configurations.IMAGE_NEW_NAME)) {
			return true;
		}
		
		// not found
		return false;
	}
	
	public void loadImageLazyNoClean(ImageView image, String imagePath) {
		loadImage(new ImageAdapter(image), imagePath, true, false);
	}

	public void loadImageLazy(ImageView image, String imagePath) {
		loadImage(new ImageAdapter(image), imagePath, true, true);
	}

	public void loadImageLazy(AacButton aacButton, String imagePath) {
		loadImage(new ImageAdapter(aacButton), imagePath, true, true);
		
	}

	public void loadImage(ImageView image, String imagePath) {
		loadImage(new ImageAdapter(image), imagePath, false, true);
	}

	public void cleanPictures() {
		ImageAdapter i = images.poll();
		while(i != null) {
			i.setImageBitmap(null);
			i = images.poll();
		}
	}

	private static class ImageAdapter {
		private AacButton aacButton;
		private ImageView imageView;

		public ImageAdapter(AacButton aacButton) {
			this.aacButton = aacButton;
			this.imageView = null;
		}
		
		public ImageAdapter(ImageView imageView) {
			this.aacButton = null;
			this.imageView = imageView;
		}

		public ViewTreeObserver getViewTreeObserver() {
			if (aacButton != null) {
				return aacButton.getViewTreeObserver();
			} else {
				return imageView.getViewTreeObserver();
			}
		}

		public void setImageResource(int res) {
			if (aacButton != null) {
				aacButton.setImageResource(res);
			} else {
				imageView.setImageResource(res);
			}
		}

		public int getWidth() {
			if (aacButton != null) {
				return aacButton.getWidth();
			} else {
				return imageView.getWidth();
			}
		}

		public int getHeight() {
			if (aacButton != null) {
				return aacButton.getHeight();
			} else {
				return imageView.getHeight();
			}
		}

		public void setImageBitmap(Bitmap bitmap) {
			if (aacButton != null) {
				aacButton.setImageBitmap(bitmap);
			} else {
				imageView.setImageBitmap(bitmap);
			}
		}
	}
	
	private static class ImageLoaderListener implements OnGlobalLayoutListener {
		private ImageAdapter imageAdapter;
		private String imagePath;
		private boolean withClean;
	
		public ImageLoaderListener(ImageAdapter imageAdapter, String imagePath, boolean withClean) {
			this.imageAdapter = imageAdapter;
			this.imagePath = imagePath;
			this.withClean = withClean;
		}
	
		@Override
		public void onGlobalLayout() {
			if (imagePath != null) {
				ImageLoader.getInstance().loadImage(imageAdapter, imagePath, false, withClean);
				imageAdapter = null;
				imagePath = null;
			}
		}
	}

	private void loadImage(ImageAdapter imageAdapter, String imagePath, boolean lazy, boolean withClean) {
		String directoryPath = FileUtils.getStorageDirectory().toString();

		// is relative
		File imgFile = new File(directoryPath, imagePath);
		if (imgFile.exists()) {
			if (lazy) {
				ViewTreeObserver observer = imageAdapter.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(
						new ImageLoaderListener(imageAdapter, imgFile.getAbsolutePath(), withClean));
			} else {
				setScaledPicture(imageAdapter, imgFile.getAbsolutePath(), withClean);
			}
			return;
		}

		// is absolute
		imgFile = new File(imagePath);
		if (imgFile.exists()) {
			if (lazy) {
				ViewTreeObserver observer = imageAdapter.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(
						new ImageLoaderListener(imageAdapter, imgFile.getAbsolutePath(), withClean));
			} else {
				setScaledPicture(imageAdapter, imgFile.getAbsolutePath(), withClean);
			}
			return;
		}

		// is resources
		if (imagePath.equals(Configurations.IMAGE_BACK_NAME)) {
			imageAdapter.setImageResource(R.drawable.cell_back_high);
		} else if (imagePath.equals(Configurations.IMAGE_NEW_NAME)) {
			imageAdapter.setImageResource(R.drawable.cell_new_high);
		}
	}
	
	private void setScaledPicture(ImageAdapter imageAdapter, String imagePath, boolean withClean) {		
		// Get the dimensions of the View
		int targetW = imageAdapter.getWidth();
		int targetH = imageAdapter.getHeight();
		if(targetW == 0 || targetH == 0)
			return;

		boolean renderSvg = imagePath.endsWith(Configurations.SVG_EXT);
		if(renderSvg == true) {
			String rastImage = imagePath.replace(Configurations.SVG_EXT, Configurations.SVG_RASTERIZED_POSTFIX);
			File file = new File(rastImage);
			if(file.exists() == true) {
				imagePath = rastImage;
				renderSvg = false;
			}
		}
		
		if(renderSvg == true) {
			try {
				FileInputStream svgInputStream = new FileInputStream(imagePath);
				SVG svg = SVG.getFromInputStream(svgInputStream);
				if (svg.getDocumentWidth() != -1) {
					Bitmap bitmap = Bitmap.createBitmap(
							(int) svg.getDocumentWidth(),
							(int) svg.getDocumentHeight(),
							Bitmap.Config.ARGB_8888);
					Canvas bmcanvas = new Canvas(bitmap);
					svg.renderToCanvas(bmcanvas);
					imageAdapter.setImageBitmap(bitmap);
					rasterizeSvgImage(imagePath, bitmap);
					if(withClean == true) {
						images.add(imageAdapter);
					}
				}
			} catch (FileNotFoundException e) {
				// skip this image
			} catch (SVGParseException e) {
				// skip this image (invalid or not supported)
			}
		} else {
			// Get the dimensions of the bitmap
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imagePath, bmOptions);
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;
	
			// Determine how much to scale down the image
			int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
	
			// Decode the image file into a Bitmap sized to fill the View
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;
	
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
			imageAdapter.setImageBitmap(bitmap);
			if(withClean == true) {
				images.add(imageAdapter);
			}
		}
	}

	private void rasterizeSvgImage(String path, Bitmap bitmap) {
		String outPath = path.replace(Configurations.SVG_EXT, Configurations.SVG_RASTERIZED_POSTFIX);
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outPath);
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

	public void rasterizeIfVectorial(File outFile) {
		boolean renderSvg = outFile.getName().endsWith(Configurations.SVG_EXT);
		if (renderSvg == true) {
			String rastImage = outFile.getName().replace(Configurations.SVG_EXT, Configurations.SVG_RASTERIZED_POSTFIX);
			File file = new File(outFile.getParentFile(), rastImage);
			try {
				FileInputStream svgInputStream = new FileInputStream(outFile);
				SVG svg = SVG.getFromInputStream(svgInputStream);
				if (svg.getDocumentWidth() != -1) {
					Bitmap bitmap = Bitmap.createBitmap(
							(int) svg.getDocumentWidth(),
							(int) svg.getDocumentHeight(),
							Bitmap.Config.ARGB_8888);
					Canvas bmcanvas = new Canvas(bitmap);
					svg.renderToCanvas(bmcanvas);
					rasterizeSvgImage(file.getPath(), bitmap);
				}
			} catch (FileNotFoundException e) {
				// skip this image
			} catch (SVGParseException e) {
				// skip this image (invalid or not supported)
			}
		}
	}
}
