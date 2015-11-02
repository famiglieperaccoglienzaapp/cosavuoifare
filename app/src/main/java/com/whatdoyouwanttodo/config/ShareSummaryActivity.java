package com.whatdoyouwanttodo.config;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.whatdoyouwanttodo.R;

/**
 * Attivita' che mostra un riepilogo delle tabelle da condividere
 */
public class ShareSummaryActivity extends ActionBarActivity {

private static ShareSummaryModel dataParameter = null;
	
	public static Intent getStartIntent(Activity caller, ShareSummaryModel data) {
		Intent intent = new Intent(caller, ShareSummaryActivity.class);
		dataParameter = null;
		dataParameter = data;
		return intent;
	}
	
	public static interface ShareSummaryModel {
		String getHelpText();
		int getItemCount();
		String getItemName(int i);
		String getItemImage(int i);
		String getItemDescription1(int i);
		String getItemDescription2(int i);
		String getItemsDescription1();
		String getItemsDescription2();
		void confirmShare();
	}

	private ShareSummaryModel data;
	
	private FrameLayout container;
	private TextView helpText;
	private TableLayout gridsContainer;
	private TextView allGridTextA;
	private TextView allGridTextB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_summary);
		
		this.data = dataParameter;
		dataParameter = null;
		
		this.container = (FrameLayout) findViewById(R.id.container);
		this.helpText = (TextView) findViewById(R.id.help_text);
		this.gridsContainer = (TableLayout) findViewById(R.id.grids_container);
		this.allGridTextA = (TextView) findViewById(R.id.all_grid_texta);
		this.allGridTextB = (TextView) findViewById(R.id.all_grid_textb);
		
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
		
		for(int i = 0; i < data.getItemCount(); i++) {
			View row = getLayoutInflater().inflate(R.layout.activity_share_summary_table_row, gridsContainer, false);
			
			TextView title1 = (TextView) row.findViewById(R.id.grid_text1);
			ImageButton gridImage1 = (ImageButton) row.findViewById(R.id.grid_image1);
			TextView text2a = (TextView) row.findViewById(R.id.grid_text2a);
			TextView text2b = (TextView) row.findViewById(R.id.grid_text2b);
			
			title1.setText(data.getItemName(i));
			gridImage1.setImageBitmap(BitmapFactory.decodeFile(data.getItemImage(i)));
			text2a.setText(data.getItemDescription1(i));
			text2b.setText(data.getItemDescription2(i));
			
			gridsContainer.addView(row, gridsContainer.getChildCount());
		}
		
		allGridTextA.setText(data.getItemsDescription1());
		allGridTextB.setText(data.getItemsDescription2());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// TODO dare una mano al garbage collector
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share_summary, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_back) {
			finish();
			return true;
		} else if(id == R.id.action_continue) {
			data.confirmShare();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
