package com.whatdoyouwanttodo;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.utils.TtsSoundPool;

/**
 * Questa classe rappresenta un bottone personalizzato per l'uso in un tabella
 * AAC.
 * Del bottone si pu� cambiare immagine, testo, colore di sfondo, spessore
 * bordo, colore bordo, dimensione testo e colore del testo; � possibile
 * inserire dei bottoni secondari in alto a sinistra e a destra o mostrare
 * un segnaposto.
 */
public class AacButton extends View {
	/**
	 * Distanza fra il bordo e il contenuto (testo e immagine)
	 */
	private final int PADDING;
	/**
	 * Dimensione massima dei bottoni secondari
	 */
	private final int SECONDARY_MAX_DIMENSION;
	/**
	 * Dimensione minima del bottone
	 */
	private final int PREFERRED;
	/**
	 * Colore del tratteggio nel caso di bottone segnaposto
	 */
	private final static int PLACEHOLDER_COLOR = Color.DKGRAY;
	
	// variable params
	/**
	 * indica se questo bottone � solo un segnaposto
	 */
	private boolean onlyPlaceholder = false;
	/**
	 * indica se questo bottone ha il bottone secondario in alto a destra
	 */
	private boolean configButton = false;
	/**
	 * indica se questo bottone ha l'indicatore secondario in alto a sinistra
	 */
	private boolean configShowTarget = false;
	
	// placeholder params
	private Paint placeholderPaint;
	
	// configuration button params
	private Paint configDeletePaint;
	private Paint configBorderPaint;
	private Paint configPaint;
	private Rect configRect;
	private int lowerX;
	private int upperY;
	private OnClickListener configListener;
	private boolean isSecondaryClick;
	
	// configuration show target params
	private Rect configShowTargetRect;
	private Options bmOptions;
	private Resources resources;
	private int configShowTargetDrawable;
	
	// image params
	private Bitmap image;
	private int imageResource;
	private String label;
	private int borderWidth;
	// private int backgroundColor;
	private int backgroundColorSelected;
	private int textSize;
	private int audioType;
	private int audioId;

	// for optimize onDraw method
	private Paint borderPaint;
	private Paint textPaint;
	private Rect imageSrc;
	private Rect imageDest;

	/**
	 * Crea un bottone AAC con i valori di default.
	 * 
	 * @param context il contesto in cui il bottone sar� usato
	 */
	public AacButton(Context context) {
		super(context);

		PADDING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		SECONDARY_MAX_DIMENSION = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
		PREFERRED = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());

		init(Constants.getInstance(context).NEW_CELL.getText(),
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(),
				Constants.getInstance(context).NEW_CELL.getTextWidth(),
				Constants.getInstance(context).NEW_CELL.getTextColor());
	}

	/**
	 * Crea un segnaposto per un bottone AAC.
	 * 
	 * @param context il contesto in cui il bottone sar� usato
	 * @param placeholder sempre true, serve a distinguare questo costruttore da quello di default
	 */
	public AacButton(Context context, boolean placeholder) {
		super(context);
		
		PADDING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		SECONDARY_MAX_DIMENSION = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
		PREFERRED = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
		
		this.onlyPlaceholder = placeholder;
		init(Constants.getInstance(context).NEW_CELL.getText(),
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(),
				Constants.getInstance(context).NEW_CELL.getTextWidth(),
				Constants.getInstance(context).NEW_CELL.getTextColor());
	}

	/**
	 * Crea un bottone AAC con i valori specificati.
	 * 
	 * @param context il contesto in cui il bottone sar� usato
	 * @param label la scritta che comparir� sul bottone, pu� essere ""
	 * @param backgroundColor il colore di sfondo del bottone
	 * @param borderWidth lo spessore del bordo del bottone in pixel
	 * @param borderColor il colore del bordo del bottone
	 * @param textWidth la dimensione della scritta sul bottone in pixel
	 * @param textColor il colore della scritta sul bottone
	 */
	public AacButton(Context context, String label,
			int backgroundColor, int borderWidth, int borderColor,
			int textWidth, int textColor) {
		super(context);
		
		PADDING = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		SECONDARY_MAX_DIMENSION = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
		PREFERRED = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
		
		init(label, backgroundColor, borderWidth, borderColor, textWidth,
				textColor);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void init(String label, int backgroundColor, int borderWidth,
			int borderColor, int textWidth, int textColor) {
		this.label = label.toUpperCase(Locale.getDefault());
		this.image = null;
		this.textSize = calculatePixel(textWidth);

		// border paint
		this.borderPaint = new Paint();
		this.borderWidth = calculatePixel(borderWidth);
		borderPaint.setColor(borderColor);
		borderPaint.setStrokeWidth(this.borderWidth);

		// text paint
		this.textPaint = new Paint();
		textPaint.setColor(textColor);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);

		imageSrc = new Rect(0, 0, 0, 0);
		imageDest = new Rect(0, 0, 0, 0);

		setFocusable(true);
		this.backgroundColorSelected = getSoftComplementaryColor(backgroundColor);
		if (onlyPlaceholder) {
			setBackgroundColor(Color.TRANSPARENT);
			placeholderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			placeholderPaint.setColor(PLACEHOLDER_COLOR);
			placeholderPaint.setStyle(Paint.Style.STROKE);
			placeholderPaint.setStrokeWidth(PADDING * 2);
			placeholderPaint.setPathEffect(new DashPathEffect(new float[] { 10, 10 }, 0));
		} else {
			StateListDrawable states = new StateListDrawable();
			states.addState(new int[] {android.R.attr.state_pressed},
			    new ColorDrawable(backgroundColorSelected));
			states.addState(new int[] {android.R.attr.state_focused},
					new ColorDrawable(backgroundColorSelected));
			states.addState(new int[] { },
					new ColorDrawable(backgroundColor));
			try {
				setBackgroundDrawable(states);
			} catch(NoSuchMethodError err) {
				setBackground(states);
			}
		}
		audioType = -1;
		audioId = -1;
		
		if(configButton || configShowTarget) {
			configDeletePaint = new Paint();
			configDeletePaint.setStrokeWidth(8);
			configBorderPaint = new Paint();
			configBorderPaint.setColor(Color.BLACK);
			configBorderPaint.setStrokeWidth(8);
			configPaint = new Paint();
			configPaint.setColor(Color.WHITE);
			configPaint.setStyle(Paint.Style.FILL);
			configRect = new Rect();
			configShowTargetRect = new Rect();
			bmOptions = new Options();
			resources = getResources();
		}

		setClickable(true);
	}

	private int getSoftComplementaryColor(int backgroundColor) {
		int complementary = Color.rgb(255 - Color.red(backgroundColor),
				255 - Color.green(backgroundColor),
				255 - Color.blue(backgroundColor));
		float[] hsbVals = new float[3];
		Color.RGBToHSV(Color.red(complementary),
				Color.green(complementary),
				Color.blue(complementary), hsbVals);
		hsbVals[2] = 0.5f * (1f + hsbVals[2]);
		backgroundColor = Color.HSVToColor(hsbVals);
		return backgroundColor;
	}
	
	/**
	 * Seleziona la risorsa da mostrare nell'indicatore in alto a destra.
	 * 
	 * @param configButton true per attivare il disegno dell'indicatore
	 * @param configShowTargetDrawable id della risorsa da caricare durante il disegno dell'indicatore
	 */
	public void setConfigButton(boolean configButton, int configShowTargetDrawable) {
		this.configButton = configButton;
		if (configShowTargetDrawable >= 0) {
			this.configShowTarget = true;
			this.configShowTargetDrawable = configShowTargetDrawable;
		} else {
			this.configShowTarget = false;
		}
		if(configButton || configShowTarget) {
			configDeletePaint = new Paint();
			configDeletePaint.setStrokeWidth(8);
			configBorderPaint = new Paint();
			configBorderPaint.setColor(Color.BLACK);
			configBorderPaint.setStrokeWidth(8);
			configPaint = new Paint();
			configPaint.setColor(Color.WHITE);
			configPaint.setStyle(Paint.Style.FILL);
			configRect = new Rect();
			configShowTargetRect = new Rect();
			bmOptions = new Options();
			resources = getResources();
		}
	}

	/**
	 * Imposta il listener per la pressione sul bottone secondario
	 * 
	 * @param listener viene invocato alla pressione del bottone secondario
	 */
	public void setSecondaryOnClickListener(OnClickListener listener) {
		this.configListener = listener;
	}

	/**
	 * Imposta l'audio da riprodurre alla pressione del bottone.
	 * 
	 * @param audioType o TtsSoundPool.TYPE_FILE o TtsSoundPool.TYPE_TTS
	 * @param audioId l'id dell'audio nella TtsSoundPool
	 */
	public void setAudioTypeAndId(int audioType, int audioId) {
		this.audioType = audioType;
		this.audioId = audioId;
	}

	/**
	 * Imposta l'immagine da visualizzare
	 * 
	 * @param image l'immagine da visualizzare
	 */
	public void setImageBitmap(Bitmap image) {
		this.image = image;
	}

	/**
	 * Imposta la risorsa da visualizzare
	 * 
	 * @param res id della risorsa da visualizzare
	 */
	public void setImageResource(int res) {
		this.imageResource = res;
	}

	private int calculatePixel(int dpSize) {
		Resources r = getResources();
		float pxSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpSize, r.getDisplayMetrics());
		int pxSizeCorrected = (int) pxSize;
		pxSizeCorrected -= pxSizeCorrected % 2;
		return pxSizeCorrected;
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#performClick()
	 */
	@Override
	public boolean performClick() {
		if(isSecondaryClick == true) {
			configListener.onClick(this);
			return true;
		} else {
			boolean ret = super.performClick();
			if (audioId != -1) {
				TtsSoundPool.play(audioType, audioId);
			}
			return ret;
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
		if(!configButton) {
			isSecondaryClick = false;
			return super.dispatchTouchEvent(event);
		}
		
		float x = event.getX();
		float y = event.getY();
		if (x > lowerX && y < upperY) {
			isSecondaryClick = true;
		} else {
			isSecondaryClick = false;
		}
		return super.dispatchTouchEvent(event);
    }
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// background is already draw

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		if(width == 0 || height == 0)
			return;
		
		if (onlyPlaceholder) {
			float[] points = {
					0, 0, width, 0,
					0, height, width, height,
					0, 0, 0, height,
					width, 0, width, height
			};
			canvas.drawLines(points, placeholderPaint);
		} else {
			// check if image is loaded
			if(image == null) {
				// Get the dimensions of the bitmap
				Options bmOptions = new Options();
				bmOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(getResources(), imageResource, bmOptions);
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;
	
				// Determine how much to scale down the image
				int scaleFactor = Math.min(photoW / width, photoH / height);
	
				// Decode the image file into a Bitmap sized to fill the View
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scaleFactor;
				bmOptions.inPurgeable = true;
	
				image = BitmapFactory.decodeResource(getResources(), imageResource, bmOptions);
			}
	
			// draw border
			int halfStrokeWidth = borderWidth / 2;
			int doubleStrokeWidth = borderWidth * 2;
			if(borderWidth > 0) {
				float[] pts = { 0, halfStrokeWidth, width, halfStrokeWidth, 0,
						height - halfStrokeWidth, width, height - halfStrokeWidth,
						halfStrokeWidth, 0, halfStrokeWidth, height,
						width - halfStrokeWidth, 0, width - halfStrokeWidth, height };
				canvas.drawLines(pts, borderPaint);
			}
			
			int contentWidth = width - doubleStrokeWidth - 2 * PADDING;
			int contentHeight = height - doubleStrokeWidth - 2 * PADDING;
			int contentBorder = borderWidth + PADDING;
	
			// draw text
			float textWidth = textPaint.measureText(label);
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
			canvas.save();
			canvas.translate(contentBorder + textDiff, height - contentBorder);
			if (textWidth > (contentWidth * 0.99)) {
				canvas.scale(textScale, 1.0f);
			}
			canvas.drawText(label, 0, 0, textPaint);
			canvas.restore();
	
			// draw image (if any)
			if (image != null) {
				int widthSrc = image.getWidth();
				int heightSrc = image.getHeight();
				if (!(widthSrc == 0 || heightSrc == 0)) {
					imageSrc.set(0, 0, widthSrc, heightSrc);
					int xDest = contentBorder;
					int yDest = contentBorder;
					int widthDest = contentWidth;
					int heightDest = contentHeight - textSize;
					int widthDestAlt = heightDest * widthSrc / heightSrc;
					int heightDestAlt = widthDest * heightSrc / widthSrc;
					if (widthDestAlt < (widthDest * 0.7)) {
						int diff = widthDest - widthDestAlt;
						xDest += diff / 2;
						widthDest = widthDestAlt;
					} else if (heightDestAlt < (heightDest * 0.7)) {
						int diff = heightDest - heightDestAlt;
						yDest += diff / 2;
						heightDest = heightDestAlt;
					}
					imageDest.set(xDest, yDest, xDest + widthDest, yDest
							+ heightDest);
					canvas.drawBitmap(image, imageSrc, imageDest, null);
				}
			}
		
			// draw transparent gray color
			// utile per effetti sui bottoni
			// if (pressed) {
			// 	canvas.drawColor(backgroundColorSelected);
			// }
			if(configShowTarget) {
				int configWidth = width / 3;
				int configHeight = height / 3;
				if(configWidth > configHeight) {
					configHeight = configWidth;
				} else {
					configWidth = configHeight;
				}
				if(configWidth > SECONDARY_MAX_DIMENSION) {
					configWidth = SECONDARY_MAX_DIMENSION;
					configHeight = SECONDARY_MAX_DIMENSION;
				}
				configRect.set(0, 0, configWidth, configHeight);
				canvas.drawRect(configRect, configPaint);

				canvas.save();
				lowerX = width - configWidth;
				upperY = configHeight;
				canvas.clipRect(0, 0, configWidth, configHeight);
				canvas.translate(0, 0);
				float[] configBorderPoints = {
						0, 0, configWidth, 0,
						0, configWidth, configWidth, configWidth,
						0, 0, 0, configWidth,
						configWidth, 0, configWidth, configWidth,
				};
				canvas.drawLines(configBorderPoints, configBorderPaint);
				canvas.restore();
				
				canvas.save();
				canvas.translate(0, 0);
				int linesWidth = configWidth - 6;
				configShowTargetRect.set(6, 6, linesWidth, linesWidth);

				// Get the dimensions of the bitmap
				bmOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(resources, configShowTargetDrawable, bmOptions);
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;

				// Determine how much to scale down the image
				int scaleFactor = Math.min(photoW / linesWidth, photoH / linesWidth);

				// Decode the image file into a Bitmap sized to fill the View
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scaleFactor;
				bmOptions.inPurgeable = true;
				
				Bitmap bitmap = BitmapFactory.decodeResource(resources, configShowTargetDrawable, bmOptions);
				canvas.drawBitmap(bitmap, null, configShowTargetRect, configBorderPaint);
//				Drawable linkToDrawable = getResources().getDrawable(R.drawable.cell_link_to);
//				linkToDrawable.draw(canvas);
//				canvas.drawD(t, null, destRect, configBorderPaint);
				canvas.restore();
			}
		}
		
		if(configButton) {
			int configWidth = width / 3;
			int configHeight = height / 3;
			if(configWidth > configHeight) {
				configHeight = configWidth;
			} else {
				configWidth = configHeight;
			}
			if(configWidth > SECONDARY_MAX_DIMENSION) {
				configWidth = SECONDARY_MAX_DIMENSION;
				configHeight = SECONDARY_MAX_DIMENSION;
			}
			configRect.set(width - configWidth, 0, width, configHeight);
			canvas.drawRect(configRect, configPaint);

			canvas.save();
			lowerX = width - configWidth;
			upperY = configHeight;
			canvas.clipRect(width - configWidth, 0, width, configHeight);
			canvas.translate(width - configWidth, 0);
			float[] configBorderPoints = {
					0, 0, configWidth, 0,
					0, configWidth, configWidth, configWidth,
					0, 0, 0, configWidth,
					configWidth, 0, configWidth, configWidth,
			};
			canvas.drawLines(configBorderPoints, configBorderPaint);
			canvas.restore();
			
			canvas.save();
			int strokeWidth = (int) configBorderPaint.getStrokeWidth();
			canvas.translate(width - configWidth + strokeWidth, 0 + strokeWidth);
			int linesWidth = configWidth - strokeWidth - strokeWidth;
			if(onlyPlaceholder) {
				int linesHalf = linesWidth / 2;
				configDeletePaint.setColor(Color.GREEN);
				float[] configDeletePoints = {
						linesHalf, 0, linesHalf, linesWidth,
						0, linesHalf, linesWidth, linesHalf,
				};
				canvas.drawLines(configDeletePoints, configDeletePaint);
			} else {
				configDeletePaint.setColor(Color.RED);
				float[] configDeletePoints = {
						0, 0, linesWidth, linesWidth,
						linesWidth, 0, 0, linesWidth,
				};
				canvas.drawLines(configDeletePoints, configDeletePaint);
			}
			canvas.restore();
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		return getMeasurement(measureSpec, PREFERRED);
	}

	private int measureHeight(int measureSpec) {
		return getMeasurement(measureSpec, PREFERRED);
	}

	private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		int measurement = 0;

		switch (MeasureSpec.getMode(measureSpec)) {
		case MeasureSpec.EXACTLY:
			measurement = specSize;
			break;
		case MeasureSpec.AT_MOST:
			measurement = Math.max(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}

		return measurement;
	}
}