package com.whatdoyouwanttodo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class QuestionDialog {
	private String message;
	private String positiveButton;
	private String negativeButton;
    private QuestionDialogListener callback;
	
	public interface QuestionDialogListener {
		public void onPositiveClick();
		public void onNegativeClick();
	}

	public QuestionDialog(String message, String positiveButton,
			String negativeButton, QuestionDialogListener callback) {
		this.message = message;
		this.positiveButton = positiveButton;
		this.negativeButton = negativeButton;
		this.callback = callback;
	}

	private OnClickListener positiveListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			callback.onPositiveClick();
		}
	};
	
	private OnClickListener negativeListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			callback.onNegativeClick();
		}
	};
	
	@SuppressLint("InflateParams")
	public void show(Activity caller) {
		Builder builder = new Builder(caller);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(message);
		builder.setPositiveButton(positiveButton, positiveListener);
		builder.setNegativeButton(negativeButton, negativeListener);
		builder.show();
	}
}
