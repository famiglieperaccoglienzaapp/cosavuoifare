package com.whatdoyouwanttodo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public final class MessageDialog {
	public static void showMessage(Activity caller,
			String title, String message, String confirm) {
		Builder builder = new Builder(caller);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(confirm, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
