package com.whatdoyouwanttodo.ui;

import java.util.LinkedList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.utils.ImageLoader;

public class ImageListHelper {	
	public static final int TYPE_MUSIC = 0;
	public static final int TYPE_IMAGE = 1;
	public static final int TYPE_VIDEO = 2;
	
	private LinearLayout rootLayout;
	private LayoutInflater inflater;
	private OnListChange callback;
	
	public static class Item {
		private String name = null;
		private String imagePath = null;
		private String data = null;
		
		public Item(String name, String imagePath, String data) {
			this.name = name;
			this.imagePath = imagePath;
			this.data = data;
		}

		public String getName() {
			return name;
		}

		public String getImagePath() {
			return imagePath;
		}

		public String getData() {
			return data;
		}
	}
	 
	LinkedList<Item> items = new LinkedList<Item>();
	private OnAction onActions;
	
	public static interface OnSelectVideo {
		public void selectVideo(int position);
	}

	public static interface OnAction {
		LinearLayout onMakeLayout(LayoutInflater inflater, ViewGroup parent);
		void onUpdate(TextView text, ImageView image, Item item);
		void onChange(int id);
		void onNew(int id);
	}
	
	public ImageListHelper(LinearLayout root, Activity activity, OnListChange callback, OnAction onActions) {
		this.rootLayout = root;
		this.inflater = activity.getLayoutInflater();
		this.callback = callback;
		this.onActions = onActions;
	}
	
	public static interface OnListChange {
		void onListChange(Item[] items);
	}

	public void change(int id, Item item) {
		// get layout to change
		LinearLayout imageLayout = (LinearLayout) rootLayout.getChildAt(id);

		// change image and text
		ImageView previewImage = (ImageView) imageLayout
				.findViewById(R.id.preview_image);
		TextView previewText = (TextView) imageLayout
				.findViewById(R.id.preview_text);
		previewText.setText(item.getName());
		if(item.imagePath != null)
			ImageLoader.getInstance().loadImage(previewImage, item.imagePath);

		// change path
		items.set(id, item);
		
		callback.onListChange(items.toArray(new Item[items.size()]));
	}
	
	public void add(int id, Item item) {
		// -1 == add cell at last
		if(id == -1) {
			// create layout
			LinearLayout imageLayout = makeLayout(items.size(), true, item, rootLayout);
			
			// add layout
			rootLayout.addView(imageLayout, items.size());
			
			// add cell at last
			items.add(item);
		} else {
			// create layout
			LinearLayout imageLayout = makeLayout(items.size(), true, item, rootLayout);
			
			// add layout
			rootLayout.addView(imageLayout, id);

			// change image and text
			ImageView previewImage = (ImageView) imageLayout
					.findViewById(R.id.preview_image);
			TextView previewText = (TextView) imageLayout
					.findViewById(R.id.preview_text);
			previewText.setText(item.getName());
			if(item.imagePath != null)
				ImageLoader.getInstance().loadImageLazy(previewImage, item.imagePath);
			
			// change path
			items.add(id, item);
		}
		
		syncronizeIds();
		
		callback.onListChange(items.toArray(new Item[items.size()]));
	}

	public void initAll(Item[] items) {
		if(items != null) {
			for(int id = 0; id < items.length; id++) {
				LinearLayout imageLayout = makeLayout(id, true, items[id], rootLayout);
				rootLayout.addView(imageLayout);
				this.items.add(items[id]);
			}
		}
		
		// make last layout
		LinearLayout imageLayout = makeLayout(-1, false, null, rootLayout);
		
		// set invisible button and first change listener
		ImageView previewImage = (ImageView) imageLayout.findViewById(R.id.preview_image);
		LinearLayout previewImageContainer = (LinearLayout) imageLayout.findViewById(R.id.preview_image_container);
		TextView previewText = (TextView) imageLayout.findViewById(R.id.preview_text);
		Button previewNew = (Button) imageLayout.findViewById(R.id.preview_new);
		Button previewDelete = (Button) imageLayout.findViewById(R.id.preview_delete);
		previewImage.setVisibility(View.INVISIBLE);
		previewImageContainer.setVisibility(View.INVISIBLE);
		previewText.setVisibility(View.INVISIBLE);
		previewNew.setOnClickListener(new EventListener(EventListener.NEW, -1));
		previewDelete.setVisibility(View.INVISIBLE);

		// add layout
		rootLayout.addView(imageLayout);
	}

	private LinearLayout makeLayout(int id, boolean withListener, Item item, ViewGroup parent) {
		// inflate layout
		LinearLayout imageLayout = null;
		imageLayout = onActions.onMakeLayout(inflater, parent);
		
		// get component
		ImageView previewImage = (ImageView) imageLayout
				.findViewById(R.id.preview_image);
		TextView previewText = (TextView) imageLayout
				.findViewById(R.id.preview_text);
		Button previewNew = (Button) imageLayout
				.findViewById(R.id.preview_new);
		Button previewDelete = (Button) imageLayout
				.findViewById(R.id.preview_delete);
			
		if (withListener == true) {
			// assign listener
			previewImage.setOnClickListener(new EventListener(
					EventListener.CHANGE, id));
			previewNew.setOnClickListener(new EventListener(
					EventListener.NEW, id));
			previewDelete.setOnClickListener(new EventListener(
					EventListener.DELETE, id));
		}
		
		if(item != null) {
			onActions.onUpdate(previewText, previewImage, item);
		}
		return imageLayout;
	}
	
	private class EventListener implements OnClickListener {
		public static final int DELETE = 0;
		public static final int NEW = 1;
		public static final int CHANGE = 2;

		private int type;
		private int id;

		private EventListener(int type, int id) {
			this.type = type;
			this.id = id;
		}

		@Override
		public void onClick(View view) {
			if(type == CHANGE) {
				onActions.onChange(id);
			} else if(type == NEW) {
				onActions.onNew(id);
			} else { 
				// clean layout (with some help to garbage collector)
				LinearLayout imageLayout =  (LinearLayout) rootLayout.getChildAt(id);
				ImageView previewImage = (ImageView) imageLayout
						.findViewById(R.id.preview_image);
				TextView previewText = (TextView) imageLayout
						.findViewById(R.id.preview_text);
				Button previewNew = (Button) imageLayout
						.findViewById(R.id.preview_new);
				Button previewDelete = (Button) imageLayout
						.findViewById(R.id.preview_delete);
				previewImage.setOnClickListener(null);
				previewText.setOnClickListener(null);
				previewNew.setOnClickListener(null);
				previewDelete.setOnClickListener(null);
				imageLayout.removeAllViews();
				imageLayout = null;
				
				rootLayout.removeViewAt(id);
				
				items.remove(id);
				
				syncronizeIds();
				
				callback.onListChange(items.toArray(new Item[items.size()]));
			}
		}
	}
	
	private void syncronizeIds() {
		int len = rootLayout.getChildCount() - 1;
		for (int id = 0; id < len; id++) {

			LinearLayout imageLayout = (LinearLayout) rootLayout.getChildAt(id);

			// get buttons
			ImageView previewImage = (ImageView) imageLayout
					.findViewById(R.id.preview_image);
			Button previewNew = (Button) imageLayout
					.findViewById(R.id.preview_new);
			Button previewDelete = (Button) imageLayout
					.findViewById(R.id.preview_delete);

			// set new listener
			previewImage.setOnClickListener(new EventListener(
					EventListener.CHANGE, id));
			previewNew.setOnClickListener(new EventListener(
					EventListener.NEW, id));
			previewDelete.setOnClickListener(new EventListener(
					EventListener.DELETE, id));
		}
	}	
}
