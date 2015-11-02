package com.whatdoyouwanttodo.ui;

import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ChooseOptionDialog {
	private LinkedList<String> texts;
	private LinkedList<ChooseOptionListener> listeners;
	
    public interface ChooseOptionListener {
        public void onChoose();
    }
    
    public ChooseOptionDialog() {
    	this.texts = new LinkedList<String>();
    	this.listeners = new LinkedList<ChooseOptionListener>();
    }

	public void addOption(String text, ChooseOptionListener listener) {
		texts.add(text);
		listeners.add(listener);
	}

	private OnClickListener listener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			listeners.get(which).onChoose();
		}
	};
	
	public void show(Activity caller) {
		CharSequence[] items = texts.toArray(new CharSequence[texts.size()]);
		
		Builder builder = new Builder(caller);
		builder.setItems(items, listener);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
