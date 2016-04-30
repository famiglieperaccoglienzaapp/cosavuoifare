package com.whatdoyouwanttodo.application;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.whatdoyouwanttodo.LongOperationActivity.LongOperationStep;
import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.utils.FileUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;

/**
 * Carica l'insieme di tabelle predefinito nel database
 */
public class DbFirstLoader {
	private Context context = null;
	private Resources res = null;
	private List<String> fileToCopyList = null;
	private IncrementCounter incrementer;
	private ImageLoader imageLoader = null;

	public boolean isEmpty(Context context) {
		this.context = context;
		this.res = context.getResources();

		ChessboardDbUtility dbu = new ChessboardDbUtility(context);
		dbu.openWritable();

		boolean refreshDatabase = false;
		if (ChessboardApplication.DEBUG_REFRESH_DB) {
			refreshDatabase = true;
		}

		boolean ret = false;
		if (dbu.isEmpty() || refreshDatabase) {
			ret = true;
		}
		dbu.close();

		this.res = null;
		this.context = null;
		
		return ret;
	}
	
	private static final class IncrementCounter {
		private long startTime = System.currentTimeMillis();
		private int n = 0;
		private float start;
		private float range;
		private float count;

		public void setRange(int start, int stop, int count) {
			this.start = start;
			this.range = stop - start;
			this.count = count;
			n = 0;
			Log.w("increment", "time " + (startTime - System.currentTimeMillis()));
		}

		public int inc() {
			float pos = start + ((range * n) / count);
			n++;
			return (int) pos;
		}
	}

	public void resetAll(Context context, LongOperationStep callback) {
		this.context = context;
		this.res = context.getResources();
		this.incrementer = new IncrementCounter();
		this.imageLoader  = ImageLoader.getInstance();

		callback.onStep(0, res.getString(R.string.fs_start), "");
		ChessboardDbUtility dbu = new ChessboardDbUtility(context);
		dbu.openWritable();
		resetAll(dbu, callback);
		callback.onStep(100, res.getString(R.string.fs_stop), "");
		dbu.close();

		this.incrementer = null;
		this.imageLoader = null;
		this.res = null;
		this.context = null;
	}

	private void resetAll(ChessboardDbUtility dbu, LongOperationStep callback) {
		dbu.clear();
		
		this.fileToCopyList = new ArrayList<String>();

		// putOriginalData(dbu);
		
		putRefactoredData(dbu, callback);
		
		File dir = new File(FileUtils.getStorageDirectory(), Constants.getInstance(context).FILE_DIR);
		dir.mkdir();
		copyAssetToExternalMemory(callback);
		
		this.fileToCopyList = null;
	}
	
	private void putRefactoredData(ChessboardDbUtility dbu, LongOperationStep callback) {
		incrementer.setRange(1, 3, 11);
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_train_slides));
		// add two abrakadabra		
		long ak1 = addAbrakadabra(
				dbu,
				res.getString(R.string.fc_train_slides),
				new String[] {
						f(Constants.getInstance(context).FILE_DIR, "train1.png"),
						f(Constants.getInstance(context).FILE_DIR, "train2.png"),
						f(Constants.getInstance(context).FILE_DIR, "train3.png"),
						f(Constants.getInstance(context).FILE_DIR, "train4.png"),
						f(Constants.getInstance(context).FILE_DIR, "train5.png"),
						f(Constants.getInstance(context).FILE_DIR, "train6.png") },
				f(Constants.getInstance(context).FILE_DIR, "drum.ogg"),
				f(Constants.getInstance(context).FILE_DIR, "colorful.ogg"),
				15,
				Abrakadabra.EFFECT_KENBURNS);
	
		// add two active listening
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_hachiko));
		long al1 = addActiveListening(dbu, res.getString(R.string.fc_hachiko), new String[] { f(Constants.getInstance(context).FILE_DIR, "hachiko.ogg") },
				10, f(Constants.getInstance(context).FILE_DIR, "press_button.ogg"), 10, 5, f(Constants.getInstance(context).FILE_DIR, "music.png"));
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_as_colorful_as_ever));
		long al2 = addActiveListening(dbu, res.getString(R.string.fc_as_colorful_as_ever), new String[] { f(Constants.getInstance(context).FILE_DIR, "colorful.ogg") },
				5, f(Constants.getInstance(context).FILE_DIR, "press_button.ogg"), 5, 5, f(Constants.getInstance(context).FILE_DIR, "music_equalizer.jpg"));	
		
		// add video
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + "What is AAC?");
		long vp1 = addYoutubeVideo(dbu, res.getString(R.string.fc_germany_vs_brasil), new String[] { Configurations.YOUTUBE_PREFIX + "r3m8_YmTDDM" });
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_lo_hobbit));
		long vp2 = addYoutubeVideo(dbu, res.getString(R.string.fc_lo_hobbit), new String[] { Configurations.YOUTUBE_PREFIX + "7mYAyN4ryMQ" });
		
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_main_table));
		long cb1 = addChessboard(dbu, 0, 2, 2, res.getString(R.string.fc_main_table));
		
		// Comunicazione
		long cb2 = addChessboard(dbu, cb1, 3, 4, res.getString(R.string.fc_comm_subject));
		long cb3 = addChessboard(dbu, cb1, 3, 4, res.getString(R.string.fc_comm_verb));
		long cb4 = addChessboard(dbu, cb1, 3, 4, res.getString(R.string.fc_comm_object));
		
		// Tabella a tema
		long cb5 = addChessboard(dbu, cb1, 2, 2, res.getString(R.string.fc_eat));
		long cb6 = addChessboard(dbu, cb5, 2, 3, res.getString(R.string.fc_food));
	
		// Attivit� aggiuntive
		long cb7 = addChessboard(dbu, cb1, 2, 3, res.getString(R.string.fc_entertainment));
		
		// Tabella di introduzione al tutorial
		long cb8 = addChessboard(dbu, cb1, 1, 2, res.getString(R.string.fc_personalization));
		
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_communication));
		// Tabella di accoglienza
		addLinkCell(dbu, cb1, 0, 0, res.getString(R.string.fc_communication), f(Constants.getInstance(context).FILE_DIR, "happy man.svg"), cb2);
		addLinkCell(dbu, cb1, 0, 1, res.getString(R.string.fc_theme_table), f(Constants.getInstance(context).FILE_DIR, "communication board.svg"), cb5);
		addLinkCell(dbu, cb1, 1, 0, res.getString(R.string.fc_entertainment), f(Constants.getInstance(context).FILE_DIR, "play area.svg"), cb7);
		addLinkCell(dbu, cb1, 1, 1, res.getString(R.string.fc_personalization), f(Constants.getInstance(context).FILE_DIR, "personal passport.svg"), cb8);
		
		//  Comunicazione
		// mettere tre tabelle per la composizione di frasi consecutiva
		// soggetto - verbo - oggetto
		// sfondo bianco - sola enfasi sui simboli
		// mettere solo immagini dal set personalizzato
		// soggetto
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_communication));
		addLinkCell(dbu, cb2, 0, 0, res.getString(R.string.fc_i_am), f(Constants.getInstance(context).FILE_DIR, "face neutral 3.svg"), cb3);
		addLinkCell(dbu, cb2, 0, 1, res.getString(R.string.fc_you), f(Constants.getInstance(context).FILE_DIR, "point , to.svg"), cb3);
		addLinkCell(dbu, cb2, 0, 2, res.getString(R.string.fc_in_general), f(Constants.getInstance(context).FILE_DIR, "every.svg"), cb3);
		addLinkCell(dbu, cb2, 0, 3, res.getString(R.string.fc_friends), f(Constants.getInstance(context).FILE_DIR, "friends.svg"), cb3);
		
		addLinkCell(dbu, cb2, 1, 0, res.getString(R.string.fc_friend), f(Constants.getInstance(context).FILE_DIR, "happy man.svg"), cb3);
		addLinkCell(dbu, cb2, 1, 1, res.getString(R.string.fc_relative), f(Constants.getInstance(context).FILE_DIR, "parents.svg"), cb3);
		addLinkCell(dbu, cb2, 1, 2, res.getString(R.string.fc_nurse), f(Constants.getInstance(context).FILE_DIR, "nurse 1a.svg"), cb3);
		addLinkCell(dbu, cb2, 1, 3, res.getString(R.string.fc_acquaintance), f(Constants.getInstance(context).FILE_DIR, "face neutral 3.svg"), cb3);
				
		addLinkCell(dbu, cb2, 2, 0, res.getString(R.string.fc_the_time), f(Constants.getInstance(context).FILE_DIR, "sun.svg"), cb3);
		addLinkCell(dbu, cb2, 2, 1, res.getString(R.string.fc_the_temperature), f(Constants.getInstance(context).FILE_DIR, "thermometer 2.svg"), cb3);
		addLinkCell(dbu, cb2, 2, 2, res.getString(R.string.fc_environment), f(Constants.getInstance(context).FILE_DIR, "room.svg"), cb3);
		addLinkCell(dbu, cb2, 2, 3, res.getString(R.string.fc_menu), f(Constants.getInstance(context).FILE_DIR, "communication board.svg"), cb1);
		
		// verbo
		addLinkCell(dbu, cb3, 0, 0, res.getString(R.string.fc_to_do), f(Constants.getInstance(context).FILE_DIR, "do.svg"), cb4);
		addLinkCell(dbu, cb3, 0, 1, res.getString(R.string.fc_to_be), f(Constants.getInstance(context).FILE_DIR, "same.svg"), cb4);
		addLinkCell(dbu, cb3, 0, 2, res.getString(R.string.fc_want), f(Constants.getInstance(context).FILE_DIR, "good.svg"), cb4);
		addLinkCell(dbu, cb3, 0, 3, res.getString(R.string.fc_read), f(Constants.getInstance(context).FILE_DIR, "work book.svg"), cb4);
		
		addLinkCell(dbu, cb3, 1, 0, res.getString(R.string.fc_help), f(Constants.getInstance(context).FILE_DIR, "help.svg"), cb4);
		addLinkCell(dbu, cb3, 1, 1, res.getString(R.string.fc_go_to), f(Constants.getInstance(context).FILE_DIR, "run , to.svg"), cb4);
		addLinkCell(dbu, cb3, 1, 2, res.getString(R.string.fc_put), f(Constants.getInstance(context).FILE_DIR, "put.svg"), cb4);
		addLinkCell(dbu, cb3, 1, 3, res.getString(R.string.fc_get), f(Constants.getInstance(context).FILE_DIR, "get.svg"), cb4);
		
		addLinkCell(dbu, cb3, 2, 0, res.getString(R.string.fc_wait), f(Constants.getInstance(context).FILE_DIR, "clock.svg"), cb4);
		addLinkCell(dbu, cb3, 2, 1, res.getString(R.string.fc_open), f(Constants.getInstance(context).FILE_DIR, "open.svg"), cb4);
		addLinkCell(dbu, cb3, 2, 2, res.getString(R.string.fc_draw), f(Constants.getInstance(context).FILE_DIR, "pen and paper 2.svg"), cb4);
		addLinkCell(dbu, cb3, 2, 3, res.getString(R.string.fc_stop), f(Constants.getInstance(context).FILE_DIR, "mistake no wrong.svg"), cb2);
		
		// oggetto
		addLinkCell(dbu, cb4, 0, 0, res.getString(R.string.fc_bad), f(Constants.getInstance(context).FILE_DIR, "bad.svg"), cb2);
		addLinkCell(dbu, cb4, 0, 1, res.getString(R.string.fc_good), f(Constants.getInstance(context).FILE_DIR, "good.svg"), cb2);
		addLinkCell(dbu, cb4, 0, 2, res.getString(R.string.fc_surprised), f(Constants.getInstance(context).FILE_DIR, "surprised man.svg"), cb2);
		addLinkCell(dbu, cb4, 0, 3, res.getString(R.string.fc_afraid), f(Constants.getInstance(context).FILE_DIR, "afraid man.svg"), cb2);
		
		addLinkCell(dbu, cb4, 1, 0, res.getString(R.string.fc_sleep), f(Constants.getInstance(context).FILE_DIR, "bed time.svg"), cb2);
		addLinkCell(dbu, cb4, 1, 1, res.getString(R.string.fc_more), f(Constants.getInstance(context).FILE_DIR, "more.svg"), cb2);
		addLinkCell(dbu, cb4, 1, 2, res.getString(R.string.fc_nothing), f(Constants.getInstance(context).FILE_DIR, "nothing.svg"), cb2);
		addLinkCell(dbu, cb4, 1, 3, res.getString(R.string.fc_stop1), f(Constants.getInstance(context).FILE_DIR, "no more.svg"), cb2);
		
		addLinkCell(dbu, cb4, 2, 0, res.getString(R.string.fc_medicine), f(Constants.getInstance(context).FILE_DIR, "medicine.svg"), cb2);
		addLinkCell(dbu, cb4, 2, 1, res.getString(R.string.fc_clothes), f(Constants.getInstance(context).FILE_DIR, "dress.svg"), cb2);
		addLinkCell(dbu, cb4, 2, 2, res.getString(R.string.fc_door), f(Constants.getInstance(context).FILE_DIR, "door.svg"), cb2);
		addLinkCell(dbu, cb4, 2, 3, res.getString(R.string.fc_stop), f(Constants.getInstance(context).FILE_DIR, "mistake no wrong.svg"), cb2);
		
		//  Tabelle a tema
		// Tre tabelle per il cibo
		// solo immagini dal set
		// Ricordarsi di dare un modo per interagire
		// voglio -> tavella cibo ritorno
		// non mi piace - mi piace
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_theme_table));
		addCell(dbu, cb5, 0, 0, res.getString(R.string.fc_good_food), f(Constants.getInstance(context).FILE_DIR, "good to eat 2.svg"));
		addCell(dbu, cb5, 0, 1, res.getString(R.string.fc_bad_food), f(Constants.getInstance(context).FILE_DIR, "bad to eat 2.svg"));
		addLinkCell(dbu, cb5, 1, 0, res.getString(R.string.fc_food), f(Constants.getInstance(context).FILE_DIR, "food.svg"), res.getString(R.string.fc_food_message), cb6);
		addLinkCell(dbu, cb5, 1, 1, res.getString(R.string.fc_menu), f(Constants.getInstance(context).FILE_DIR, "communication board.svg"), cb1);
		
		addCell(dbu, cb6, 0, 0, res.getString(R.string.fc_bananas), f(Constants.getInstance(context).FILE_DIR, "banana bunch.svg"));
		addCell(dbu, cb6, 0, 1, res.getString(R.string.fc_water), f(Constants.getInstance(context).FILE_DIR, "drink.svg"));
		addCell(dbu, cb6, 0, 2, res.getString(R.string.fc_omelette), f(Constants.getInstance(context).FILE_DIR, "egg yolk.svg"));
		addCell(dbu, cb6, 1, 0, res.getString(R.string.fc_soup), f(Constants.getInstance(context).FILE_DIR, "soup.svg"));
		addCell(dbu, cb6, 1, 1, res.getString(R.string.fc_cake), f(Constants.getInstance(context).FILE_DIR, "cake.svg"));
		addLinkCell(dbu, cb6, 1, 2, res.getString(R.string.fc_back), f(Constants.getInstance(context).FILE_DIR, "backwards.svg"), cb5);

		//  Attivita' aggiuntive
		// Mostrare delle attivita' aggiuntive che possono essere fatte
		// Due ascolti attivi
		// Due video youtube
		// Due abrakadabra
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_entertainment));
		addActiveListeningCell(dbu, cb7, 0, 0, res.getString(R.string.fc_hachiko), f(Constants.getInstance(context).FILE_DIR, "music.svg"), al1);
		addActiveListeningCell(dbu, cb7, 1, 0, res.getString(R.string.fc_as_colorful_as_ever), f(Constants.getInstance(context).FILE_DIR, "music.svg"), al2);
		addVideoCell(dbu, cb7, 0, 1, res.getString(R.string.fc_video1), f(Constants.getInstance(context).FILE_DIR, "video camera.svg"), vp1);
		addVideoCell(dbu, cb7, 1, 1, res.getString(R.string.fc_video2), f(Constants.getInstance(context).FILE_DIR, "video camera.svg"), vp2);
		addAbrakadabraCell(dbu, cb7, 0, 2, res.getString(R.string.fc_train), f(Constants.getInstance(context).FILE_DIR, "train.png"), ak1);
		addLinkCell(dbu, cb7, 1, 2, res.getString(R.string.fc_menu), f(Constants.getInstance(context).FILE_DIR, "communication board.svg"), cb1);

		//  Configurazione
		// Mettere un tutorial per il normodotato!
		// Mono casella con abrakadabra fatto apposta
		callback.onStep(incrementer.inc(), res.getString(R.string.fs_create_main_table),
				res.getString(R.string.fs_create_table) + res.getString(R.string.fc_personalization));
		String goToTutorialPath = "go_to_tutorial.png";
		if(Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage())) {
			goToTutorialPath = "go_to_tutorial_en.png";
		}
		addLinkCell(dbu, cb8, 0, 0, res.getString(R.string.fc_menu), f(Constants.getInstance(context).FILE_DIR, "communication board.svg"), cb1);
		addCell(dbu, cb8, 0, 1, res.getString(R.string.fc_personalization), f(Constants.getInstance(context).FILE_DIR, goToTutorialPath));
	}
	
	private String f(String dir, String path) {
		File pathFile = new File(dir, path);
		String pathname = pathFile.getName();
		AssetManager assetManager = context.getAssets();
		try {
			InputStream in = assetManager.open(pathname);
			fileToCopyList.add(pathname);
			in.close();
		} catch (IOException e) {
			Log.e(getClass().getName(), "path " + path + " not packaged", e);
		}
		return dir + File.separator + path;
	}
	
	private void copyAssetToExternalMemory(LongOperationStep callback) {
		AssetManager assetManager = context.getAssets();
		File destExtDir = new File(FileUtils.getStorageDirectory(), Constants.getInstance(context).FILE_DIR);
		String[] files = fileToCopyList.toArray(new String[fileToCopyList.size()]);
		incrementer.setRange(3, 52, files.length);
		for (String filename : files) {
			callback.onStep(incrementer.inc(),
					res.getString(R.string.fs_create_table_files),
					res.getString(R.string.fs_create_file) + filename);
			InputStream in = null;
			OutputStream out = null;
			File outFile = new File(destExtDir, filename);
			try {
				in = assetManager.open(filename);
				try {
					out = new BufferedOutputStream(new FileOutputStream(outFile), 8192);
					copyFile(in, out);
					out.flush();
					out.close();
					out = null;
				} finally {
					in.close();
					in = null;
				}
			} catch (IOException e) {
				Log.e(getClass().getName(), "Failed to copy asset file: " + filename, e);
			}
			imageLoader.rasterizeIfVectorial(outFile);
		}
		
		incrementer.setRange(52, 100, 3118);
		try {
			InputStream inputStream = assetManager.open("MulberrySymbolSet.zip");
			try {
				ZipInputStream zipInputStream = new ZipInputStream(inputStream);
				ZipEntry zipEntry = null;
				while ((zipEntry = zipInputStream.getNextEntry()) != null) {
					if (zipEntry.isDirectory()) {
						File dirFile = new File(destExtDir, zipEntry.getName());

						if (!dirFile.isDirectory()) {
							dirFile.mkdirs();
						}
					} else {
						String zipName = zipEntry.getName();
						File outFile = new File(destExtDir, zipName);
						callback.onStep(incrementer.inc(),
								res.getString(R.string.fs_create_symbol_set),
								res.getString(R.string.fs_copy_symbol) + zipName);
						
						try {
							FileOutputStream outStream = new FileOutputStream(outFile);
							try {
								byte[] buffer = new byte[8192];
								int read;
								while ((read = zipInputStream.read(buffer)) != -1) {
									outStream.write(buffer, 0, read);
								}
							} catch (Exception ex) {
								Log.e(getClass().getName(), "error while copy " + outFile.getPath(), ex);
							} finally {
								try {
									outStream.close();
								} catch (Exception ex) {
									Log.e(getClass().getName(), "error on close outStream", ex);
								}
							}
						} catch (Exception ex) {
							Log.e(getClass().getName(), "error on copy " + outFile.getPath(), ex);
						} finally {
							try {
								zipInputStream.closeEntry();
							} catch (IOException ex) {
								Log.e(getClass().getName(), "error on close zip entry ", ex);
							}
						}
					}

				}
				zipInputStream.close();
			} catch (IOException ex) {
				Log.e(getClass().getName(), "error on get asset file", ex);
			} finally {
				inputStream.close();
				inputStream = null;
			}
		} catch (IOException e) {
			Log.e(getClass().getName(), "Failed to copy Mulberry Symbol Set", e);
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[8192];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	private long addCell(ChessboardDbUtility dbu, long chessboard, int row,
			int column, String name, String imagePath) {
		return dbu.addCell(chessboard, name, row, column,
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(), name, Cell.TEXT_NORMAL,
				Constants.getInstance(context).NEW_CELL.getTextColor(), imagePath,
				Configurations.TTS_PREFIX + name, 0, 0);
	}

	private long addLinkCell(ChessboardDbUtility dbu, long chessboard, int row,
			int column, String name, String imagePath, String audio, long destCb) {
		return dbu.addCell(chessboard, name, row, column,
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(), name, Cell.TEXT_NORMAL,
				Constants.getInstance(context).NEW_CELL.getTextColor(), imagePath,
				Configurations.TTS_PREFIX + audio,
				Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD, destCb);
	}

	private long addLinkCell(ChessboardDbUtility dbu, long chessboard, int row,
			int column, String name, String imagePath, long destCb) {
		return dbu.addCell(chessboard, name, row, column,
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(), name, Cell.TEXT_NORMAL,
				Constants.getInstance(context).NEW_CELL.getTextColor(), imagePath,
				Configurations.TTS_PREFIX + name,
				Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD, destCb);
	}

	/*
	private long addBackCell(ChessboardDbUtility dbu, long chessboard, int row,
			int column, String name, String imagePath, String audio) {
		return dbu.addCell(chessboard, name, row, column,
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(), name, Cell.TEXT_NORMAL,
				Constants.getInstance(context).NEW_CELL.getTextColor(), imagePath,
				Configurations.TTS_PREFIX + audio,
				Cell.ACTIVITY_TYPE_CLOSE_CHESSBOARD, 0);
	}

	private long addBackCell(ChessboardDbUtility dbu, long chessboard, int row,
			int column, String name, String imagePath) {
		return dbu.addCell(chessboard, name, row, column,
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(), name, Cell.TEXT_NORMAL,
				Constants.getInstance(context).NEW_CELL.getTextColor(), imagePath,
				Configurations.TTS_PREFIX + name,
				Cell.ACTIVITY_TYPE_CLOSE_CHESSBOARD, 0);
	}
	*/

	private long addAbrakadabraCell(ChessboardDbUtility dbu, long chessboard,
			int row, int column, String name, String imagePath,
			long idAbrakadabra) {
		return dbu.addCell(chessboard, name, row, column,
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(), name, Cell.TEXT_NORMAL,
				Constants.getInstance(context).NEW_CELL.getTextColor(), imagePath,
				Configurations.TTS_PREFIX + name,
				Cell.ACTIVITY_TYPE_ABRAKADABRA, idAbrakadabra);
	}

	private long addVideoCell(ChessboardDbUtility dbu, long chessboard,
			int row, int column, String name, String imagePath, long idVideo) {
		return dbu.addCell(chessboard, name, row, column,
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(), name, Cell.TEXT_NORMAL,
				Constants.getInstance(context).NEW_CELL.getTextColor(), imagePath,
				Configurations.TTS_PREFIX + name,
				Cell.ACTIVITY_TYPE_PLAY_VIDEO, idVideo);
	}

	private long addActiveListeningCell(ChessboardDbUtility dbu,
			long chessboard, int row, int column, String name,
			String imagePath, long idActiveListeining) {
		return dbu.addCell(chessboard, name, row, column,
				Constants.getInstance(context).NEW_CELL.getBackgroundColor(),
				Constants.getInstance(context).NEW_CELL.getBorderWidth(),
				Constants.getInstance(context).NEW_CELL.getBorderColor(), name, Cell.TEXT_NORMAL,
				Constants.getInstance(context).NEW_CELL.getTextColor(), imagePath,
				Configurations.TTS_PREFIX + name,
				Cell.ACTIVITY_TYPE_ACTIVE_LISTENING, idActiveListeining);
	}

	private long addChessboard(ChessboardDbUtility dbu, long parentId,
			int nRows, int nColumns, String name) {
		return dbu.addChessboard(parentId, name, nRows, nColumns,
				Constants.getInstance(context).NEW_CHESSBOARD.getBackgroundColor(),
				Constants.getInstance(context).NEW_CHESSBOARD.getBorderWidth());
	}

	private long addAbrakadabra(ChessboardDbUtility dbu, String name,
			String[] imagePaths, String soundPath, String musicPath,
			int musicDurationTime, int imageEffect) {
		return dbu.addAbrakadabra(name, imagePaths, soundPath, musicPath,
				musicDurationTime, imageEffect);
	}

	private long addActiveListening(ChessboardDbUtility dbu, String name,
			String[] musicPath, int interval, String registrationPath,
			int pause, int pauseInterval, String image) {
		return dbu.addActiveListening(name, image, musicPath, interval,
				registrationPath, pause, pauseInterval);
	}

	private long addYoutubeVideo(ChessboardDbUtility dbu, String name,
			String[] videoUrl) {
		return dbu.addVideoPlaylist(name, videoUrl);
	}
}
