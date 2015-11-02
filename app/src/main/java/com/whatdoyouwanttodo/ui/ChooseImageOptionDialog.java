package com.whatdoyouwanttodo.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatdoyouwanttodo.R;

public class ChooseImageOptionDialog {
	private ArrayList<DialogItem> values;

	public ChooseImageOptionDialog() {
		this.values = new ArrayList<DialogItem>();
	}

	public interface ChooseImageOptionListener {
		public void onChoose(DialogItem item);
	}

	public void addOption(String id, String name, Bitmap bitmap,
			ChooseImageOptionListener listener) {
		values.add(new DialogItem(id, name, bitmap, listener));
	}

	public static class DialogItem {
		private String id;
		private String name;
		private Bitmap bitmap;
		private ChooseImageOptionListener listener;

		public DialogItem(String id, String name, Bitmap bitmap,
				ChooseImageOptionListener listener) {
			this.id = id;
			this.name = name;
			this.bitmap = bitmap;
			this.listener = listener;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public ChooseImageOptionListener getListener() {
			return listener;
		}
	}

	private static class DialogArrayAdapter extends ArrayAdapter<DialogItem> {
		private Activity activity;
		private ArrayList<DialogItem> values;

		public DialogArrayAdapter(Activity activity, int resource,
				ArrayList<DialogItem> values) {
			super(activity, resource, values);
			this.activity = activity;
			this.values = values;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			if (convertView == null)
				vi = activity.getLayoutInflater().inflate(
						R.layout.dialog_image_with_text, null);

			TextView text = (TextView) vi.findViewById(R.id.dialog_text);
			ImageView image = (ImageView) vi.findViewById(R.id.dialog_image);

			text.setText(values.get(position).getName());
			image.setImageDrawable(new BitmapDrawable(activity.getResources(),
					values.get(position).getBitmap()));

			return vi;
		}
	}

	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			values.get(which).getListener().onChoose(values.get(which));
			dialog.dismiss();
		}
	};

	public void show(Activity caller) {
		ArrayAdapter<DialogItem> parametersAdapter = new DialogArrayAdapter(
				caller, android.R.layout.simple_spinner_item, values);

		Builder builder = new Builder(caller);
		builder.setSingleChoiceItems(parametersAdapter, -1, this.onClick);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
