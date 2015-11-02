package com.whatdoyouwanttodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.whatdoyouwanttodo.LongOperationActivity.LongOperationModel;
import com.whatdoyouwanttodo.LongOperationActivity.LongOperationStep;
import com.whatdoyouwanttodo.application.ChessboardApplication;
import com.whatdoyouwanttodo.application.DbFirstLoader;
import com.whatdoyouwanttodo.config.GeneralSettingsActivity;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.utils.TtsSoundPool;

/**
 * Rappresenta un'attivita' che serve da benvenuto per l'utente, chiedendo se vuole visualizzare la tabella principale o cambiare le impostazioni globali
 */
public class WelcomeActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		DbFirstLoader dbl = new DbFirstLoader();
		if(dbl.isEmpty(this) == true) {
			Intent intent = LongOperationActivity.getStartIntent(this, new LongOperationModel() {
				@Override
				public String getTitle() {
					return "Che Cosa Vuoi Fare? (Primo Avvio)";
				}

				@Override
				public String getSubtitle() {
					return "E' in corso il completamento dell'installazione, attendi qualche minuto";
				}

				@Override
				public void doOperation(LongOperationStep callback) {
					DbFirstLoader dbl = new DbFirstLoader();
					dbl.resetAll(WelcomeActivity.this, callback);
					callback.onEnd();
				}
			});
			startActivity(intent);
		}

	}

	public void showChessboard(View view) {
		ChessboardDbUtility dbu = new ChessboardDbUtility(this);
		dbu.openReadable();
		long rootId = dbu.getRootChessboardId();
		boolean fullscreenMode = dbu.getFullscreenMode();
		String ttsLanguage = dbu.getTtsLanguage();
		boolean wikiCheck = dbu.getWifyCheck();
		boolean disableLockMode = dbu.getDisableLockMode();
		dbu.close();

		TtsSoundPool.setTtsLanguage(ttsLanguage);
		ChessboardApplication.setFullscreenMode(fullscreenMode);
		ChessboardApplication.setWifyCheck(wikiCheck);
		ChessboardApplication.setDisableLockMode(disableLockMode);

		Intent intent = ChessboardActivity.getStartIntent(this, rootId);
		startActivity(intent);
	}
	
	/*
	public void showInfo(View view) {
		Resources res = getResources();
		String title = res.getString(R.string.activity_welcome_info_title);
		String message = res.getString(R.string.activity_welcome_info_message);
		String confirm = res.getString(R.string.activity_welcome_info_confirm);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		dialogBuilder.setPositiveButton(confirm, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
			}
		});

		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}
	*/

	public void openGeneralSettings(View view) {
		Intent intent = GeneralSettingsActivity.getStartIntent(this);
		startActivity(intent);
	}
}
