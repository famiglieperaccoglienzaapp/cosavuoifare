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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.Abrakadabra;
import com.whatdoyouwanttodo.ui.ImageListHelper;
import com.whatdoyouwanttodo.ui.ImageListHelper.Item;
import com.whatdoyouwanttodo.utils.FileUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;
import com.whatdoyouwanttodo.utils.IntentUtils;

/**
 * Pannello di configurazione per una playlist di immagini, usato da AbrakadabraConfigActivity
 */
public class AbrakadabraConfigFragment extends Fragment {
	private static String INTENT_CHANGE_IMAGE = "change_image";
	private static String INTENT_ADD_IMAGE = "add_image";
	private static String INTENT_CHANGE_SOUND = "change_sound";
	private static String INTENT_CHANGE_MUSIC = "change_music";
	
	private ImageListHelper images;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.fragment_abrakadabra_config, container, false);
		
		// initialize name field
		EditText name = (EditText) rootView.findViewById(R.id.music_slides_name);
		String nameText = AbrakadabraConfigActivity.ret.getName();
		if (nameText != null) {
			name.setText(nameText);
		}
		name.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (GridConfigFragment.isEditorAction(actionId)) {
					AbrakadabraConfigActivity.ret.setName(v.getText().toString());
					return false;
				}
				return false;
			}
		});
		
		// initialize sound change button
		Button sound = (Button) rootView.findViewById(R.id.btn_sound);
		String soundPath = AbrakadabraConfigActivity.ret.getSoundPath();
		if(soundPath.equals("") == false) {
			File soundFile = FileUtils.getResourceFile(soundPath);
			sound.setText(soundFile.getName());
		}
		sound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// select an music
				IntentUtils.startSelectAudioIntent(getActivity(), INTENT_CHANGE_SOUND);
			}
		});
		
		// initialize play sound button
		ImageView playSound = (ImageView) rootView.findViewById(R.id.preview_play_sound);
		playSound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String soundPath = AbrakadabraConfigActivity.ret.getSoundPath();
				if (soundPath != null) {
					IntentUtils.startPlayAudioIntent(getActivity(), soundPath);
				}
			}
		});
		
		// initialize music change button
		Button music = (Button) rootView.findViewById(R.id.btn_music);
		String musicPath = AbrakadabraConfigActivity.ret.getMusicPath();
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

		// initialize music duration spinner
		SeekBar musicDurationTime = (SeekBar) rootView.findViewById(R.id.music_duration_time);
		int musicDurationTimeVal = AbrakadabraConfigActivity.ret.getMusicDurationTime();
		musicDurationTime.setProgress(getMusicDurationSliderPos(musicDurationTimeVal));
		musicDurationTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) { }

			public void onStopTrackingTouch(SeekBar seekBar) {
				AbrakadabraConfigActivity.ret.setMusicDurationTime(getMusicDurationSliderValue(progressChanged));
			}
		});
		
		// initialize play music button
		ImageView playMusic = (ImageView) rootView.findViewById(R.id.preview_play_music);
		playMusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String musicPath = AbrakadabraConfigActivity.ret.getMusicPath();
				if (musicPath != null) {
					IntentUtils.startPlayAudioIntent(getActivity(), musicPath);
				}
			}
		});
		
		// initialize image effect spinner
		Spinner imageEffectSpinner = (Spinner) rootView.findViewById(R.id.spn_image_effect);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.activity_abrakadabra_config_image_effect, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		imageEffectSpinner.setAdapter(adapter);
		imageEffectSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						if (pos == 0) {
							AbrakadabraConfigActivity.ret.setImageEffect(Abrakadabra.EFFECT_NO_EFFECT);
						} else {
							AbrakadabraConfigActivity.ret.setImageEffect(Abrakadabra.EFFECT_KENBURNS);
						}
					}

					public void onNothingSelected(AdapterView<?> parent) { }
				});
		if (AbrakadabraConfigActivity.ret.getImageEffect() == Abrakadabra.EFFECT_KENBURNS) {
			imageEffectSpinner.setSelection(1);
		}

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
				AbrakadabraConfigActivity.ret.setImagePaths(paths);
			}
		}, new ImageListHelper.OnAction() {
			@Override
			public LinearLayout onMakeLayout(LayoutInflater inflater, ViewGroup parent) {
				return (LinearLayout) inflater.inflate(
						R.layout.fragment_abrakadabra_config_image_layout, parent, false);
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
		String[] imagePaths = AbrakadabraConfigActivity.ret.getImagePaths();
		Item[] itemList = new Item[imagePaths.length];
		for(int i = 0; i < imagePaths.length; i++) {
			File file = new File(imagePaths[i]);
			itemList[i] = new Item(file.getName(), imagePaths[i], imagePaths[i]);
		}
		images.initAll(itemList);
		
		return rootView;
	}


	private int getMusicDurationSliderValue(int value) {
		if (value == 0) {
			return 5;
		} else if (value == 1) {
			return 10;
		} else if (value == 2) {
			return 15;
		} else if (value == 3) {
			return 30;
		} else if (value == 4) {
			return 60;
		}
		return -1;
	}

	private int getMusicDurationSliderPos(int value) {
		if (value == 5) {
			return 0;
		} else if (value <= 10) {
			return 1;
		} else if (value <= 15) {
			return 2;
		} else if (value <= 30) {
			return 3;
		} else if (value <= 60) {
			return 4;
		}
		return -1;
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
			
			
			Button musicButton;
			
			if (IntentUtils.getIntentId().equals(INTENT_CHANGE_MUSIC)) {
				musicButton = (Button) getActivity().findViewById(R.id.btn_music);
				AbrakadabraConfigActivity.ret.setMusicPath(musicPath);
			} else {
				musicButton = (Button) getActivity().findViewById(R.id.btn_sound);
				AbrakadabraConfigActivity.ret.setSoundPath(musicPath);
			}
			
			musicButton.setText(musicFile.getName());
		}
	}
}