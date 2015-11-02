package com.whatdoyouwanttodo.config;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.whatdoyouwanttodo.LongOperationActivity;
import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.LongOperationActivity.LongOperationModel;
import com.whatdoyouwanttodo.LongOperationActivity.LongOperationStep;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.application.DbFirstLoader;
import com.whatdoyouwanttodo.application.SimpleChessboardSelectorModel;
import com.whatdoyouwanttodo.application.SimpleChessboardSelectorModel.TrimReturn;
import com.whatdoyouwanttodo.application.SimpleShareSummary;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.ui.QuestionDialog;
import com.whatdoyouwanttodo.ui.QuestionDialog.QuestionDialogListener;
import com.whatdoyouwanttodo.utils.ImageLoader;
import com.whatdoyouwanttodo.utils.IntentUtils;
import com.whatdoyouwanttodo.utils.TtsSoundPool;

/**
 * Attivit� che mostra le impostazioni globali
 */
public class GeneralSettingsActivity extends ActionBarActivity {
	public static Intent getStartIntent(Activity caller) {
		Intent intent = new Intent(caller, GeneralSettingsActivity.class);
		return intent;
	}

	private ImageView currentChessboardImage;
	private Button changeChessboardButton;
	private CheckBox useYoutubeOnWifiCheck;
	private Button shareGridButton;
	private Button shareGridSetButton;
	private Button importGridsButton;
	private Button hardResetButton;
	private CheckBox fullscreenModeCheck;
	private CheckBox disableLockModeCheck;
	private Spinner textToSpeechSpinner;
	private ImageButton fullscreenModeButton;
	private ImageButton disableLockModeButton;
	private ImageButton textToSpeechLanguageButton;
	
	private String imagePath = null;
	private ArrayAdapter<LocaleAdapter> textToSpeechSpinnerAdapter;
	private String currentLocale = null;
	
	private static class LocaleAdapter {
		private final Locale lang;

		public LocaleAdapter(Locale lang) {
			this.lang = lang;
		}

		public Locale getLocale() {
			return lang;
		}
		
		@Override
		public String toString() {
			return lang.getDisplayName();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_general_settings);
		
		this.currentChessboardImage = (ImageView) findViewById(R.id.currentChessboardImage);
		this.changeChessboardButton = (Button) findViewById(R.id.changeChessboardButton);
		this.shareGridButton = (Button) findViewById(R.id.shareGridsButton);
		this.shareGridSetButton = (Button) findViewById(R.id.shareGridSetButton);
		this.importGridsButton = (Button) findViewById(R.id.importGrids);
		this.hardResetButton = (Button) findViewById(R.id.hardReset);
		this.useYoutubeOnWifiCheck = (CheckBox) findViewById(R.id.useYoutubeOnWifiCheck);
		this.fullscreenModeCheck = (CheckBox) findViewById(R.id.fullscreenMode);
		this.disableLockModeCheck = (CheckBox) findViewById(R.id.disableLockMode);
		this.textToSpeechSpinner = (Spinner) findViewById(R.id.textToSpeechLanguage);
		this.fullscreenModeButton = (ImageButton) findViewById(R.id.fullscreenModeInfo);
		this.disableLockModeButton = (ImageButton) findViewById(R.id.disableLockModeInfo);
		this.textToSpeechLanguageButton = (ImageButton) findViewById(R.id.textToSpeechLanguageInfo);
		
		initializeToCurrentSettings(savedInstanceState);
		
		this.changeChessboardButton.setOnClickListener(this.changeChessboardButtonClickListener);
		this.shareGridButton.setOnClickListener(this.shareGridButtonClickListener);
		this.shareGridSetButton.setOnClickListener(this.shareGridSetButtonClickListener);
		this.importGridsButton.setOnClickListener(this.importGridsListener);
		this.hardResetButton.setOnClickListener(this.hardReset);
		this.useYoutubeOnWifiCheck.setOnCheckedChangeListener(this.useYoutubeOnWifiCheckCheckedChangeListener);
		this.fullscreenModeCheck.setOnCheckedChangeListener(this.fullscreenModeChanged);
		this.disableLockModeCheck.setOnCheckedChangeListener(this.disableLockModeChanged);
		this.textToSpeechSpinner.setOnItemSelectedListener(this.textToSpeechLanguageChanged);
		this.fullscreenModeButton.setOnClickListener(this.fullscreenModeInfo);
		this.disableLockModeButton.setOnClickListener(this.disableLockModeInfo);
		this.textToSpeechLanguageButton.setOnClickListener(this.textToSpeechLanguageInfo);
	}
	
	private void initializeToCurrentSettings(Bundle savedInstanceState) {
		this.imagePath = null;
		boolean onlyWify = true;
		boolean fullscreenCheck = false;
		boolean lockMode = true;
		
		if (savedInstanceState != null) {
			imagePath = savedInstanceState.getString("thumbnail");
			onlyWify = savedInstanceState.getBoolean("wifycheck");
			fullscreenCheck = savedInstanceState.getBoolean("fullscreen");
			lockMode = savedInstanceState.getBoolean("lockmode");
		}
		
		if(imagePath == null) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(this);
			dbu.openReadable();
			
			long rootChessboardId = dbu.getRootChessboardId();
			imagePath = ChessboardThumbnailManager.getInstance(this).getThumbnailPathOf(this, dbu, rootChessboardId);
			
			onlyWify = dbu.getWifyCheck();
			fullscreenCheck = dbu.getFullscreenMode();
			lockMode = dbu.getDisableLockMode();
			dbu.close();
		}
		
		ImageLoader.getInstance().loadImageLazy(currentChessboardImage, imagePath);
		useYoutubeOnWifiCheck.setChecked(onlyWify);
		fullscreenModeCheck.setChecked(fullscreenCheck);
		disableLockModeCheck.setChecked(lockMode);
		
		ChessboardDbUtility dbu = new ChessboardDbUtility(GeneralSettingsActivity.this);
		dbu.openWritable();
		this.currentLocale = dbu.getTtsLanguage();
		dbu.close();
		TtsSoundPool.getTtsLanguages(this, new TtsSoundPool.onGetTtsLanguages() {
			@Override
			public void getTtsLanguages(Locale[] languages) {
				textToSpeechSpinnerAdapter = new ArrayAdapter<LocaleAdapter>(GeneralSettingsActivity.this, android.R.layout.simple_spinner_item, android.R.id.text1);
				textToSpeechSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				textToSpeechSpinner.setAdapter(textToSpeechSpinnerAdapter);
				int count = 0;
				int position = 0;
				String defaultLanguage = Locale.getDefault().getLanguage();
				for (Locale lang : languages) {
					if (currentLocale != null) {
						if (lang.getLanguage().equals(currentLocale)) {
							position = count;
						}
					} else if (currentLocale == null) {
						if (lang.getLanguage().equals(defaultLanguage)) {
							position = count;
						}
					}
					textToSpeechSpinnerAdapter.add(new LocaleAdapter(lang));
					count++;
				}
				textToSpeechSpinnerAdapter.notifyDataSetChanged();
				textToSpeechSpinner.setSelection(position);
			}
		});
	}
	
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("thumbnail", imagePath);
		savedInstanceState.putBoolean("wifycheck", useYoutubeOnWifiCheck.isChecked());
		savedInstanceState.putBoolean("fullscreen", fullscreenModeCheck.isChecked());
		savedInstanceState.putBoolean("lockmode", disableLockModeCheck.isChecked());
    }


	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		this.changeChessboardButton.setOnClickListener(null);
		this.shareGridButton.setOnClickListener(null);
		this.shareGridSetButton.setOnClickListener(null);
		this.importGridsButton.setOnClickListener(null);
		this.hardResetButton.setOnClickListener(null);
		this.useYoutubeOnWifiCheck.setOnCheckedChangeListener(null);
		this.fullscreenModeCheck.setOnCheckedChangeListener(null);
		this.disableLockModeCheck.setOnCheckedChangeListener(null);
		this.textToSpeechSpinner.setOnItemSelectedListener(null);
		this.fullscreenModeButton.setOnClickListener(null);
		this.disableLockModeButton.setOnClickListener(null);
		this.textToSpeechLanguageButton.setOnClickListener(null);
		
		this.currentChessboardImage = null;
		this.changeChessboardButton = null;
		this.shareGridButton = null;
		this.shareGridSetButton = null;
		this.importGridsButton = null;
		this.hardResetButton = null;
		this.useYoutubeOnWifiCheck = null;
		this.fullscreenModeCheck = null;
		this.disableLockModeCheck = null;
		this.textToSpeechSpinner = null;
		this.fullscreenModeButton = null;
		this.disableLockModeButton = null;
		this.textToSpeechLanguageButton = null;
		
		ChessboardThumbnailManager.getInstance(this).clear();
		
		TtsSoundPool.release();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.general_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_back) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private OnClickListener changeChessboardButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			String helpMessage = getResources().getString(R.string.activity_general_settings_select_main_grid);
			SimpleChessboardSelectorModel model = new SimpleChessboardSelectorModel(GeneralSettingsActivity.this, helpMessage);
			model.setCallback(selectRootChessboard);	
			Intent intent = ChessboardSelectorActivity.getStartIntent(GeneralSettingsActivity.this, model, false, true);
			startActivity(intent);
		}
	};
	
	private SimpleChessboardSelectorModel.SelectionListener selectRootChessboard = new SimpleChessboardSelectorModel.SelectionListener() {
		@Override
		public void selected(Chessboard[] cb, Cell[][] cells, boolean[] selected) {
			TrimReturn ret = SimpleChessboardSelectorModel.trimToSelected(cb, cells, selected);
			if (ret.cbs.length > 0) {
				String imagePath = ChessboardThumbnailManager.getInstance(GeneralSettingsActivity.this).getThumbnailPathOf(GeneralSettingsActivity.this, ret.cbs[0], ret.cbCells[0]);
				ImageLoader.getInstance().loadImage(currentChessboardImage, imagePath);
				ChessboardDbUtility dbu = new ChessboardDbUtility(GeneralSettingsActivity.this);
				dbu.openWritable();
				dbu.setRootChessboardId(ret.cbs[0].getId());
				dbu.close();
			}
		}
	};
	
	private OnClickListener shareGridButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			String helpMessage = getResources().getString(R.string.activity_general_settings_export_grids);
			SimpleChessboardSelectorModel model = new SimpleChessboardSelectorModel(GeneralSettingsActivity.this, helpMessage);
			model.setCallback(selectGridToShare);	
			Intent intent = ChessboardSelectorActivity.getStartIntent(GeneralSettingsActivity.this, model, true, false);
			startActivity(intent);
		}
	};
	
	private SimpleChessboardSelectorModel.SelectionListener selectGridToShare = new SimpleChessboardSelectorModel.SelectionListener() {
		@Override
		public void selected(Chessboard[] cb, Cell[][] cells, boolean[] selected) {
			TrimReturn ret = SimpleChessboardSelectorModel.trimToSelected(cb, cells, selected);
			if (ret.cbs.length > 0) {
				String helpMessage = getResources().getString(R.string.activity_general_settings_confirm_share);
				SimpleShareSummary model = new SimpleShareSummary(GeneralSettingsActivity.this, helpMessage, ret.cbs, ret.cbCells);
				model.setCallback(shareGrid);
				Intent intent = ShareSummaryActivity.getStartIntent(GeneralSettingsActivity.this, model);
				startActivity(intent);
			}
		}
	};
	
	private OnClickListener shareGridSetButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			String helpMessage = getResources().getString(R.string.activity_general_settings_export_grid_set);
			SimpleChessboardSelectorModel model = new SimpleChessboardSelectorModel(GeneralSettingsActivity.this, helpMessage);
			model.setCallback(selectGridToShareRecursive);	
			Intent intent = ChessboardSelectorActivity.getStartIntent(GeneralSettingsActivity.this, model, true, true);
			startActivity(intent);
		}
	};
	
	private SimpleChessboardSelectorModel.SelectionListener selectGridToShareRecursive = new SimpleChessboardSelectorModel.SelectionListener() {
		@Override
		public void selected(Chessboard[] cb, Cell[][] cells, boolean[] selected) {
			TrimReturn ret = SimpleChessboardSelectorModel.recurseFromSelected(cb, cells, selected);
			if (cb.length > 0) {
				String helpMessage = getResources().getString(R.string.activity_general_settings_confirm_share);
				SimpleShareSummary model = new SimpleShareSummary(GeneralSettingsActivity.this, helpMessage, ret.cbs, ret.cbCells);
				model.setCallback(shareGrid);
				Intent intent = ShareSummaryActivity.getStartIntent(GeneralSettingsActivity.this, model);
				startActivity(intent);
			}
		}
	};
	
	private SimpleShareSummary.ShareListener shareGrid = new SimpleShareSummary.ShareListener() {
		@Override
		public void share(Chessboard[] cb, Cell[][] cbCells) {
			Intent intent = ChooseDestinationActivity.getStartIntent(GeneralSettingsActivity.this, cb, cbCells);
			startActivity(intent);
		}
	};
	
	private OnClickListener importGridsListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			IntentUtils.startSelectZipFileIntent(GeneralSettingsActivity.this);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == IntentUtils.SELECT_FILE_INTENT && resultCode == Activity.RESULT_OK) {
			String path = IntentUtils.getFilePath(this, data);
			
			Intent intent = ChooseDestinationActivity.getStartIntent(this, path);
			startActivity(intent);
		}
	}
	

	private OnCheckedChangeListener useYoutubeOnWifiCheckCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(GeneralSettingsActivity.this);
			dbu.openWritable();
			dbu.setWifyCheck(isChecked);
			dbu.close();
		}
	};
	
	private OnCheckedChangeListener fullscreenModeChanged = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(GeneralSettingsActivity.this);
			dbu.openWritable();
			dbu.setFullscreenMode(isChecked);
			dbu.close();
		}
	};
	
	private OnCheckedChangeListener disableLockModeChanged = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(GeneralSettingsActivity.this);
			dbu.openWritable();
			dbu.setDisableLockMode(isChecked);
			dbu.close();
		}
	};
	
	private OnItemSelectedListener textToSpeechLanguageChanged = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(GeneralSettingsActivity.this);
			dbu.openWritable();
			LocaleAdapter locAdapt = textToSpeechSpinnerAdapter.getItem(position);
			Locale loc = locAdapt.getLocale();
			dbu.setTtsLanguage(loc.getLanguage());
			dbu.close();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(GeneralSettingsActivity.this);
			dbu.openWritable();
			dbu.setTtsLanguage("");
			dbu.close();
		}
	};
	
	private OnClickListener fullscreenModeInfo = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Resources res = getResources();
			String title = res.getString(R.string.activity_general_settings_fullscreen_title);
			String info = res.getString(R.string.activity_general_settings_fullscreen_info);
			showAlert(title, info);
		}
	};
	
	private OnClickListener disableLockModeInfo = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Resources res = getResources();
			String title = res.getString(R.string.activity_general_settings_lockmode_title);
			String info = res.getString(R.string.activity_general_settings_lockmode_info);
			showAlert(title, info);
		}
	};
	
	private OnClickListener textToSpeechLanguageInfo = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Resources res = getResources();
			String title = res.getString(R.string.activity_general_settings_texttospeech_title);
			String info = res.getString(R.string.activity_general_settings_texttospeech_info);
			showAlert(title, info);
		}
	};
	
	private OnClickListener hardReset = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Resources res = getResources();
			QuestionDialog dialog = new QuestionDialog(
					res.getString(R.string.activity_general_settings_reset_message),
					res.getString(R.string.activity_general_settings_reset_ok),
					res.getString(R.string.activity_general_settings_reset_abort),
					new QuestionDialogListener() {
						@Override
						public void onPositiveClick() {
							Intent intent = LongOperationActivity.getStartIntent(GeneralSettingsActivity.this, new LongOperationModel() {
								@Override
								public String getTitle() {
									return "Che Cosa Vuoi Fare? (Ripristino)";
								}

								@Override
								public String getSubtitle() {
									return "E' in corso il ripristino della configurazione iniziale, attendi qualche minuto";
								}

								@Override
								public void doOperation(LongOperationStep callback) {
									DbFirstLoader dbl = new DbFirstLoader();
									dbl.resetAll(GeneralSettingsActivity.this, callback);
									callback.onEnd();
								}
							});
							startActivity(intent);
						}

						@Override
						public void onNegativeClick() {
							// do nothing
						}
					});
			dialog.show(GeneralSettingsActivity.this);
		}
	};
	
	private void showAlert(String title, String message) {
		Builder dialogBuilder = new Builder(this);
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		DialogInterface.OnClickListener emptyListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		};
		dialogBuilder.setPositiveButton(android.R.string.ok, emptyListener);
		dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}
}
