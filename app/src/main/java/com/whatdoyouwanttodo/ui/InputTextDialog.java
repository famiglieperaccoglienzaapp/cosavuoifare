package com.whatdoyouwanttodo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.EditText;

import com.whatdoyouwanttodo.R;

public class InputTextDialog {
	private String message;
	private String positiveButton;
	private String negativeButton;
	private String alternativeButton;
	private String defaultText;
    private InputTextDialogListener callback;
    
	private EditText text;
	
    public interface InputTextDialogListener {
        public void onPositiveClick(String text);
        public void onNegativeClick();
		public void onAlternativeClick();
    }

	public InputTextDialog (String message,
			String positiveButton, String negativeButton, String alternativeButton,
			String defaultText, InputTextDialogListener callback) {
		this.message = message;
		this.positiveButton = positiveButton;
		this.negativeButton = negativeButton;
		this.alternativeButton = alternativeButton;
		this.defaultText = defaultText;
		this.callback = callback;
	}

	private OnClickListener positiveListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			String s = text.getText().toString();
			callback.onPositiveClick(s);
		}
	};
	private OnClickListener alternativeListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			callback.onAlternativeClick();
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

		View rootView = caller.getLayoutInflater().inflate(R.layout.dialog_input_text, null);
		text = (EditText) rootView.findViewById(R.id.text);
		text.setText(defaultText);

		builder.setView(rootView);
		builder.setMessage(message);
		builder.setPositiveButton(positiveButton, this.positiveListener);
		if(alternativeButton != null)
			builder.setNeutralButton(alternativeButton, this.alternativeListener);
		builder.setNegativeButton(negativeButton, this.negativeListener);

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
