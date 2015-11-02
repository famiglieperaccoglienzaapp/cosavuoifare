package com.whatdoyouwanttodo.config;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.ui.ImageListHelper;
import com.whatdoyouwanttodo.ui.ImageListHelper.Item;
import com.whatdoyouwanttodo.utils.FileUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;
import com.whatdoyouwanttodo.utils.IntentUtils;

/**
 * Pannello di configurazione per una playlist di immagini, usato da MusicSlidesConfigActivity
 */
public class MusicSlidesConfigFragment extends Fragment {
	private static String INTENT_CHANGE_MUSIC = "change_music";
	private static String INTENT_CHANGE_IMAGE = "change_image";
	private static String INTENT_ADD_IMAGE = "add_image";
	
	private ImageListHelper images;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.fragment_music_slides_config, container, false);
		
		// initialize name field
		EditText name = (EditText) rootView.findViewById(R.id.music_slides_name);
		String nameText = MusicSlidesConfigActivity.ret.getName();
		if (nameText != null) {
			name.setText(nameText);
		}
		name.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (GridConfigFragment.isEditorAction(actionId)) {
					MusicSlidesConfigActivity.ret.setName(v.getText().toString());
					return false;
				}
				return false;
			}
		});
		
		// initialize music change button
		Button music = (Button) rootView.findViewById(R.id.btn_music);
		String musicPath = MusicSlidesConfigActivity.ret.getMusicPath();
		if(musicPath.equals("") == false) {
			File musicFile = FileUtils.getResourceFile(musicPath);
			music.setText(musicFile.getName());
		}
		music.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// select an music
				IntentUtils.startSelectAudioIntent(getActivity(), INTENT_CHANGE_MUSIC);
			}
		});
		
		// initialize play music button
		ImageView playMusic = (ImageView) rootView.findViewById(R.id.preview_play_music);
		playMusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String musicPath = MusicSlidesConfigActivity.ret.getMusicPath();
				if (musicPath != null) {
					IntentUtils.startPlayAudioIntent(getActivity(), musicPath);
				}
			}
		});

		// initialize image list layouts
		LinearLayout imagesLayout = (LinearLayout) rootView.findViewById(R.id.imagesLayout);
		images = new ImageListHelper(imagesLayout,
				getActivity(),
				new ImageListHelper.OnListChange() {
			@Override
			public void onListChange(Item[] items) {
				String[] paths = new String[items.length];
				for(int i = 0; i < items.length; i++)
					paths[i] = items[i].getData();
				MusicSlidesConfigActivity.ret.setImagePaths(paths);
			}
		}, new ImageListHelper.OnAction() {
			@Override
			public LinearLayout onMakeLayout(LayoutInflater inflater, ViewGroup parent) {
				return (LinearLayout) inflater.inflate(
						R.layout.fragment_music_slides_config_image_layout, parent, false);
			}

			@Override
			public void onUpdate(TextView text, ImageView image, Item item) {
				String imagePath = item.getData();
				text.setText(item.getName());
				if (image.isShown() == true) {
					ImageLoader.getInstance().loadImage(image, imagePath);
				} else {
					ImageLoader.getInstance().loadImageLazy(image, imagePath);
				}
			}

			@Override
			public void onChange(int id) {
				IntentUtils.startSelectImageIntent(getActivity(), INTENT_CHANGE_IMAGE, id);
			}

			@Override
			public void onNew(int id) {
				IntentUtils.startSelectImageIntent(getActivity(), INTENT_ADD_IMAGE, id);
			}
		});
		String[] imagePaths = MusicSlidesConfigActivity.ret.getImagePaths();
		Item[] itemList = new Item[imagePaths.length];
		for(int i = 0; i < imagePaths.length; i++) {
			File file = new File(imagePaths[i]);
			itemList[i] = new Item(file.getName(), imagePaths[i], imagePaths[i]);
		}
		images.initAll(itemList);
		
		return rootView;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == IntentUtils.SELECT_IMAGE_INTENT && resultCode == Activity.RESULT_OK) {
			// get file path
			String imagePath = IntentUtils.getImagePath(getActivity(), data);

			// update image
			int id = IntentUtils.getIntentParam();
			File file = new File(imagePath);
			Item imagePathItem = new Item(file.getName(), null, imagePath);
			if(IntentUtils.getIntentId().equals(INTENT_CHANGE_IMAGE)) {
				images.change(id, imagePathItem);
			} else {
				images.add(id, imagePathItem);
			}
		} else if (requestCode == IntentUtils.SELECT_AUDIO_INTENT && resultCode == Activity.RESULT_OK) {
			// get file path			
			String musicPath = IntentUtils.getAudioPath(getActivity(), data);

			// update image
			File musicFile = FileUtils.getResourceFile(musicPath);
			Button musicButton = (Button) getActivity().findViewById(R.id.btn_music);
			musicButton.setText(musicFile.getName());
			
			MusicSlidesConfigActivity.ret.setMusicPath(musicPath);
		}
	}
}