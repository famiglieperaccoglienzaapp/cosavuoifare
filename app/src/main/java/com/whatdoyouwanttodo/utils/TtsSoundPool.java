package com.whatdoyouwanttodo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.SparseArray;

public class TtsSoundPool {	
	public static final int TYPE_TTS = 0;
	public static final int TYPE_FILE = 1;

	// text-to-speech
	private static TextToSpeech tts = null;
	private static boolean ttsReady = false;
	private static int ttsCount = 0;
	private static SparseArray<String> ttsTexts = null;

	private static String ttsLanguage = null;
	private static onGetTtsLanguages ttsLanguageCallback = null;
	public interface onGetTtsLanguages {
		void getTtsLanguages(Locale[] languages);
	}
	
	// sound-pool
	private static SoundPool soundPool = null;
	private static boolean soundPoolReady = false;
	private static TreeMap<String, Integer> soundLoaded = null;

	private static void initSoundPool() {
		soundLoaded = new TreeMap<String, Integer>();
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				Log.d(getClass().getName(), "sp: " + soundPool + " id: " + sampleId + " s: " + status);
				soundPoolReady = true;
			}
		});
	}
	
	private static void initTtsEngine(Context context) {
		ttsTexts = new SparseArray<String>();
		tts = new TextToSpeech(context, new OnInitListener() {
			@Override
			public void onInit(int status) {
				Locale[] locales = new Locale[4];
				try {
					locales[0] = null;
					if (ttsLanguage != null) {
						locales[0] = new Locale(ttsLanguage);
					}
				} catch (Exception ex) {
					Log.e(getClass().getName(), "invalid language " + ttsLanguage, ex);
				}
				locales[1] = Locale.ITALIAN;
				locales[2] = Locale.ITALY;
				locales[3] = Locale.ENGLISH;
				
				boolean languageAccepted = false;
				int languageIndex = 0;
				while (languageAccepted == false && languageIndex < locales.length) {
					int retStatus = TextToSpeech.LANG_NOT_SUPPORTED;
					if(locales[languageIndex] != null) {
						retStatus = tts.setLanguage(locales[languageIndex]);
					}
					languageIndex++;
					if (retStatus == TextToSpeech.LANG_AVAILABLE
							|| retStatus == TextToSpeech.LANG_COUNTRY_AVAILABLE
							|| retStatus == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE) {
						languageAccepted = true;
					}
				}
				Log.d(getClass().getName(), "status: " + status);
				ttsReady = true;
			}
		});
	}
	
	public static int load(String audioPath) {
		if(soundPool == null) {
			initSoundPool();
		}
		Integer idI = soundLoaded.get(audioPath);
		if (idI == null) {
			int id = soundPool.load(audioPath, 1);
			soundLoaded.put(audioPath, id);
			return id;
		}
		return idI;
	}

	public static int loadTss(String text, Context context) {
		if(tts == null) {
			initTtsEngine(context);
		}
		int id = ttsCount;
		ttsCount++;
		ttsTexts.put(id, text);
		return id;
	}

	public static void play(int type, int id) {
		if (type == TYPE_FILE) {
			if (soundPoolReady == true) {
				int ret = soundPool.play(id, 1.0f, 1.0f, 1, 0, 1.0f);
				if (ret == 0) {
					Log.e(TtsSoundPool.class.getName(), "failed to play: " + id);
				}
			}
		} else {
			String text = ttsTexts.get(id);
			if (ttsReady == true) {
				int ret = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
				if (ret == TextToSpeech.ERROR) {
					Log.e(TtsSoundPool.class.getName(), "error on speak: "
							+ text);
				}
			}
		}
	}
	
	public static void setTtsLanguage(String ttsLanguage) {
		TtsSoundPool.ttsLanguage = ttsLanguage;
	}

	public static void getTtsLanguages(Context context, onGetTtsLanguages onGetTtsLanguages) {
		ttsLanguageCallback = onGetTtsLanguages;
		if(tts == null) {
			tts = new TextToSpeech(context, new OnInitListener() {
				@Override
				public void onInit(int status) {
					Locale[] locales = Locale.getAvailableLocales();
					
			        ArrayList<Locale> languages = new ArrayList<Locale>();
			        for (Locale locale : locales) {
			            if(locale.getCountry().equals("")) {
			            	languages.add(locale);
			            }
			        }
			        if(languages.size() > 0) {
			            locales = languages.toArray(new Locale[languages.size()]);
			            languages.clear();
			            languages = null;
			        }

					List<Locale> localeList = new ArrayList<Locale>();
					for (Locale locale : locales) {
					    int res = tts.isLanguageAvailable(locale);
					    if (res == TextToSpeech.LANG_AVAILABLE || res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
					        localeList.add(locale);
					    }
					}
					Locale[] retLocales = localeList.toArray(new Locale[localeList.size()]);
					localeList.clear();
					localeList = null;
					
					ttsLanguageCallback.getTtsLanguages(retLocales);
					ttsLanguageCallback = null;
				}
			});
		}
	}
	
	public static void release() {
		if (soundPool != null) {
			if (soundPoolReady == true) {
				soundPool.release();
				soundPool = null;
			}
		}
		soundPoolReady = false;
		if (soundLoaded != null) {
			soundLoaded.clear();
			soundLoaded = null;
		}
		
		if (tts != null) {
			tts.stop();
			tts.shutdown();
			tts = null;
			Log.d(TtsSoundPool.class.getName(), "TTS Destroyed");
		}
		ttsReady = false;
		ttsCount = 0;
		if (ttsTexts != null) {
			ttsTexts.clear();
			ttsTexts = null;
		}
	}
}
