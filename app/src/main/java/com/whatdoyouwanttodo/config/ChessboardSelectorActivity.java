package com.whatdoyouwanttodo.config;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.utils.ActivityUtils;

/**
 * Attivita' che permette di selezionare una tabella in un insieme di tabelle
 */
public class ChessboardSelectorActivity extends ActionBarActivity {
	private static final String WITH_CONTINUE = "com.whatdoyouwanttodo.config.ChessboardSelectorActivity.WITH_CONTINUE";
	private static final String SINGLE_SELECTION = "com.whatdoyouwanttodo.config.ChessboardSelectorActivity.SINGLE_SELECTION";

	private static ChessboardSelectorModel dataParameter = null;
	
	public static Intent getStartIntent(Activity caller, ChessboardSelectorModel data, boolean withContinue, boolean singleSelection) {
		Intent intent = new Intent(caller, ChessboardSelectorActivity.class);
		intent.putExtra(WITH_CONTINUE , withContinue);
		intent.putExtra(SINGLE_SELECTION, singleSelection);
		dataParameter = null;
		dataParameter = data;
		return intent;
	}
	
	public static interface ChessboardSelectorModel {
		String getHelpText();
		int getItemCount();
		String getItemName(int i);
		String getItemImage(int i);
		void resetSelections();
		void setSelectedItem(int i, boolean sel);
		boolean confirmSelected();
	}

	private ChessboardSelectorModel data;
	private boolean withContinue;
	private boolean singleSelection;
	
	private FrameLayout container;
	private TextView helpText;
	private TableLayout gridsContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chessboard_selector);
		
		this.data = dataParameter;
		dataParameter = null;
		Intent intent = getIntent();
		this.withContinue = intent.getBooleanExtra(WITH_CONTINUE, false);
		this.singleSelection = intent.getBooleanExtra(SINGLE_SELECTION, false);
		
		this.container = (FrameLayout) findViewById(R.id.container);
		this.helpText = (TextView) findViewById(R.id.help_text);
		this.gridsContainer = (TableLayout) findViewById(R.id.grids_container);
		
		if (singleSelection == false) {
			ActivityUtils.changeActionBarTitle(this, getResources().getString(R.string.activity_chessboard_selector_title_plural));
		}
		
		String text = data.getHelpText();
		if(text == null) {
			helpText.setVisibility(View.GONE);
			container.setPadding(container.getPaddingLeft(), 
					getResources().getDimensionPixelOffset(R.dimen.activity_config_space_large),
					container.getPaddingRight(),
					container.getPaddingBottom());
		} else {
			helpText.setText(text);
		}
		
		ArrayList<RadioButton> rbGroup = null;
		if(singleSelection == true) {
			rbGroup = new ArrayList<RadioButton>();
		}
		
		for(int i = 0; i < data.getItemCount(); i+=2) {
			View row = null;
			if(singleSelection == false) {
				row = getLayoutInflater().inflate(R.layout.activity_chessboard_selector_table_row, gridsContainer, false);
			} else {
				row = getLayoutInflater().inflate(R.layout.activity_chessboard_selector_table_row_single, gridsContainer, false);
			}
			
			TextView title1 = (TextView) row.findViewById(R.id.grid_text1);
			ImageButton gridImage1 = (ImageButton) row.findViewById(R.id.grid_image1);
			View gridCheck1 = row.findViewById(R.id.grid_check1);
			LinearLayout gridContainer2 = (LinearLayout) row.findViewById(R.id.grid_container2);
			TextView title2 = (TextView) row.findViewById(R.id.grid_text2);
			ImageButton gridImage2 = (ImageButton) row.findViewById(R.id.grid_image2);
			View gridCheck2 = row.findViewById(R.id.grid_check2);
			
			title1.setText(data.getItemName(i));
			gridImage1.setImageBitmap(BitmapFactory.decodeFile(data.getItemImage(i)));
			if (singleSelection == false) {
				CheckBox check = (CheckBox) gridCheck1;
				gridImage1.setOnClickListener(new ToggleConnector(check));
				check.setOnCheckedChangeListener(new CheckListener(i, data));
			} else {
				RadioButton check = (RadioButton) gridCheck1;
				rbGroup.add(check);
				gridImage1.setOnClickListener(new ToggleConnector(check, rbGroup, data, i));
				check.setOnClickListener(new CheckListener(i, data, rbGroup));
			}
			
			if(i  + 1 < data.getItemCount()) {
				title2.setText(data.getItemName(i + 1));
				gridImage2.setImageBitmap(BitmapFactory.decodeFile(data.getItemImage(i + 1)));
				if (singleSelection == false) {
					CheckBox check = (CheckBox) gridCheck2;
					gridImage2.setOnClickListener(new ToggleConnector(check));
					check.setOnCheckedChangeListener(new CheckListener(i + 1, data));
				} else {
					RadioButton check = (RadioButton) gridCheck2;
					rbGroup.add(check);
					gridImage2.setOnClickListener(new ToggleConnector(check, rbGroup, data, i + 1));
					check.setOnClickListener(new CheckListener(i + 1, data, rbGroup));
				}
			} else {
				gridContainer2.setVisibility(View.INVISIBLE);
			}
			
			gridsContainer.addView(row, gridsContainer.getChildCount());
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// TODO dare una mano al garbage collector
	}
	
	private static final class ToggleConnector implements OnClickListener {
		private CheckBox check = null;
		private RadioButton check1 = null;
		private ArrayList<RadioButton> group;
		private ChessboardSelectorModel model;
		private int i;

		public ToggleConnector(CheckBox check) {
			this.check = check;
		}
		
		public ToggleConnector(RadioButton check, ArrayList<RadioButton> group, ChessboardSelectorModel model, int i) {
			this.check1  = check;
			this.group = group;
			this.model = model;
			this.i = i;
		}

		@Override
		public void onClick(View v) {
			if(check != null) {
				check.toggle();
			} else {
				for(int j = 0; j < group.size(); j++) {
						group.get(j).setChecked(false);
				}
				model.resetSelections();
				model.setSelectedItem(i, true);
				check1.setChecked(true);
			}
		}
	}
	
	private static final class CheckListener implements OnCheckedChangeListener, OnClickListener {
		private int i = -1;
		private ChessboardSelectorModel model;
		private ArrayList<RadioButton> group = null;
		
		public CheckListener(int i, ChessboardSelectorModel model) {
			this.i = i;
			this.model = model;
		}

		public CheckListener(int i, ChessboardSelectorModel model, ArrayList<RadioButton> group) {
			this.i = i;
			this.model = model;
			this.group = group;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			model.setSelectedItem(i, isChecked);
		}

		@Override
		public void onClick(View view) {
			for(int j = 0; j < group.size(); j++) {
				if(i != j) {
					group.get(j).setChecked(false);
				}
			}
			model.resetSelections();
			model.setSelectedItem(i, true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (withContinue == false) {
			getMenuInflater().inflate(R.menu.chessboard_selector, menu);
		} else {
			getMenuInflater().inflate(R.menu.chessboard_selector_continue, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_save) {
			boolean notEmpty = data.confirmSelected();
			if(notEmpty == false) {
				Resources res = getResources();
				String message = res.getString(R.string.activity_chessboard_selector_empty_message);
				String confirm = res.getString(R.string.activity_chessboard_selector_empty_confirm);
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setMessage(message);
				dialogBuilder.setPositiveButton(confirm, new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int witch) {
						// do nothing
					}
				});
				AlertDialog dialog = dialogBuilder.create();
				dialog.show();
			} else {
				finish(); 
			}
			return true;
		} else if(id == R.id.action_back) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
