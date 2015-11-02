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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.ui.ImageListHelper;
import com.whatdoyouwanttodo.ui.ImageListHelper.Item;
import com.whatdoyouwanttodo.utils.FileUtils;
import com.whatdoyouwanttodo.utils.ImageLoader;
import com.whatdoyouwanttodo.utils.IntentUtils;

/**
 * Pannello di configurazione di un Ascolto Attivo, usato da ActiveListeningConfigActivity
 */
public class ActiveListeningConfigFragment extends Fragment {
	private ImageListHelper musics;
	private ImageView backgroundPreview;
	private Button backgroundButton;
	
	private static String INTENT_CHANGE_BACKGROUND = "change_background";
	private static String INTENT_CHANGE_AUDIO = "change_audio";
	private static String INTENT_ADD_AUDIO = "add_audio";
	private static String INTENT_CHANGE_REGISTRATION = "change_registration";

	public ActiveListeningConfigFragment() {
		musics = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_active_listening_config, container, false);
		
		// initialize name field
		EditText name = (EditText) rootView.findViewById(R.id.active_listening_name);
		String nameText = ActiveListeningConfigActivity.ret.getName();
		if (nameText != null) {
			name.setText(nameText);
		}
		name.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (GridConfigFragment.isEditorAction(actionId)) {
					ActiveListeningConfigActivity.ret.setName(v.getText().toString());
					return false;
				}
				return false;
			}
		});
		
		// initialize background button
		backgroundButton = (Button) rootView.findViewById(R.id.background_button);
		backgroundButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				IntentUtils.startSelectImageIntent(getActivity(), INTENT_CHANGE_BACKGROUND);
			}
		});
		backgroundPreview = (ImageView) rootView.findViewById(R.id.background_preview);
		String backgroundPath = ActiveListeningConfigActivity.ret.getBackground();
		if (backgroundPath.equals("") == false) {
			ImageLoader.getInstance().loadImageLazy(backgroundPreview, backgroundPath);
			File file = FileUtils.getResourceFile(backgroundPath);
			backgroundButton.setText(file.getName());
		}

		// initialize image list layouts
		LinearLayout imagesLayout = (LinearLayout) rootView.findViewById(R.id.musicsLayout);
		musics = new ImageListHelper(imagesLayout, getActivity(), new ImageListHelper.OnListChange() {
			@Override
			public void onListChange(Item[] items) {
				String[] paths = new String[items.length];
				for(int i = 0; i < items.length; i++)
					paths[i] = items[i].getData();
				ActiveListeningConfigActivity.ret.setMusicPaths(paths);
			}
		},  new ImageListHelper.OnAction() {
			@Override
			public LinearLayout onMakeLayout(LayoutInflater inflater, ViewGroup parent) {
				return (LinearLayout) inflater.inflate(
						R.layout.fragment_active_listening_config_music_layout, parent, false);
			}

			@Override
			public void onUpdate(TextView text, ImageView image,
					Item item) {
				File file = new File(item.getName());
				text.setText(file.getName());

				// TODO: put play image
			}
			
			@Override
			public void onChange(int id) {
				IntentUtils.startSelectAudioIntent(getActivity(), INTENT_CHANGE_AUDIO, id);
			}

			@Override
			public void onNew(int id) {
				IntentUtils.startSelectAudioIntent(getActivity(), INTENT_ADD_AUDIO, id);
			}
		});
		String[] musicPaths = ActiveListeningConfigActivity.ret.getMusicPaths();
		Item[] itemList = new Item[musicPaths.length];
		for(int i = 0; i < musicPaths.length; i++) {
			File file = new File(musicPaths[i]);
			itemList[i] = new Item(file.getName(), null, musicPaths[i]);
		}
		musics.initAll(itemList);
		
		// initialize interval slider
		SeekBar intervalSlider = (SeekBar) rootView.findViewById(R.id.interval_time);
		int intervalStartValue = ActiveListeningConfigActivity.ret.getInterval();
		intervalSlider.setProgress(getSliderPos(intervalStartValue));
		intervalSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) { }

			public void onStopTrackingTouch(SeekBar seekBar) {
				ActiveListeningConfigActivity.ret.setInterval(getSliderValue(progressChanged));
			}
		});

		
		// initialize registration change button
		Button registration = (Button) rootView.findViewById(R.id.btn_registration);

		String registrationStartValue = ActiveListeningConfigActivity.ret.getRegistration();
		if(registrationStartValue.equals("") == false) {
			String musicName = FileUtils.getResourceFile(registrationStartValue).getName();
			registration.setText(musicName);
		}
		registration.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// select an music
				IntentUtils.startSelectAudioIntent(getActivity(), INTENT_CHANGE_REGISTRATION);
			}
		});

		// initialize play registration button
		ImageView playRegistration = (ImageView) rootView.findViewById(R.id.preview_play_registration);
		playRegistration.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String musicPath = ActiveListeningConfigActivity.ret.getRegistration();
				if (musicPath != null) {
					IntentUtils.startPlayAudioIntent(getActivity(), musicPath);
				}
			}
		});
		
		// initialize pause slider
		SeekBar pauseSlider = (SeekBar) rootView.findViewById(R.id.registration_time);
		int pauseStartValue = ActiveListeningConfigActivity.ret.getPause();
		pauseSlider.setProgress(getReducedSliderPos(pauseStartValue));
		pauseSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) { }

			public void onStopTrackingTouch(SeekBar seekBar) {
				ActiveListeningConfigActivity.ret.setPause(getReducedSliderValue(progressChanged));
			}
		});
		
		// initialize pause interval slider
		SeekBar pauseIntervalSlider = (SeekBar) rootView.findViewById(R.id.registration_pause_time);
		int pauseIntervalStartValue = ActiveListeningConfigActivity.ret.getPauseInterval();
		pauseIntervalSlider.setProgress(getReducedSliderPos(pauseIntervalStartValue));
		pauseIntervalSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				progressChanged = progress;
			}

			public void onStartTrackingTouch(SeekBar seekBar) { }

			public void onStopTrackingTouch(SeekBar seekBar) {
				ActiveListeningConfigActivity.ret.setPauseInterval(getReducedSliderValue(progressChanged));
			}
		});
		
		return rootView;
	}

	private int getReducedSliderValue(int value) {
		if (value == 0) {
			return 0;
		} else if (value == 1) {
			return 1;
		} else if (value == 2) {
			return 2;
		} else if (value == 3) {
			return 3;
		} else if (value == 4) {
			return 6;
		} else if (value == 5) {
			return 10;
		} else if (value == 6) {
			return 15;
		} else if (value == 7) {
			return 30;
		} else if (value == 8) {
			return 60;
		} else if (value == 9) {
			return 120;
		}
		return -1;
	}
	
	private int getReducedSliderPos(int value) {
		if (value == 0) {
			return 0;
		} else if (value <= 1) {
			return 1;
		} else if (value <= 2) {
			return 2;
		} else if (value <= 3) {
			return 3;
		} else if (value <= 6) {
			return 4;
		} else if (value <= 10) {
			return 5;
		} else if (value <= 15) {
			return 6;
		} else if (value <= 30) {
			return 7;
		} else if (value <= 60) {
			return 8;
		} else if (value <= 120) {
			return 9;
		}
		return -1;
	}
	
	private int getSliderValue(int value) {
		if (value == 0) {
			return 0;
		} else if (value == 1) {
			return 5;
		} else if (value == 2) {
			return 10;
		} else if (value == 3) {
			return 15;
		} else if (value == 4) {
			return 20;
		} else if (value == 5) {
			return 30;
		} else if (value == 6) {
			return 40;
		} else if (value == 7) {
			return 50;
		} else if (value == 8) {
			return 60;
		} else if (value == 9) {
			return 120;
		}
		return -1;
	}
	
	private int getSliderPos(int value) {
		if (value == 0) {
			return 0;
		} else if (value <= 5) {
			return 1;
		} else if (value <= 10) {
			return 2;
		} else if (value <= 15) {
			return 3;
		} else if (value <= 20) {
			return 4;
		} else if (value <= 30) {
			return 5;
		} else if (value <= 40) {
			return 6;
		} else if (value <= 50) {
			return 7;
		} else if (value <= 60) {
			return 8;
		} else if (value <= 120) {
			return 9;
		}
		return -1;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == IntentUtils.SELECT_AUDIO_INTENT && resultCode == Activity.RESULT_OK) {
			String musicPath = IntentUtils.getAudioPath(getActivity(), data);
			File musicFile = FileUtils.getResourceFile(musicPath);

			String iId = IntentUtils.getIntentId();
			if (iId.equals(INTENT_ADD_AUDIO)) {
				int id = IntentUtils.getIntentParam();
				musics.add(id, new Item(musicFile.getName(), null, musicPath));
			} else if (iId.equals(INTENT_CHANGE_AUDIO)) {
				int id = IntentUtils.getIntentParam();
				musics.change(id, new Item(musicFile.getName(), null, musicPath));
			} else if (iId.equals(INTENT_CHANGE_REGISTRATION)) {
				Button musicButton = (Button) getActivity().findViewById(R.id.btn_registration);
				musicButton.setText(musicFile.getName());
				ActiveListeningConfigActivity.ret.setRegistration(musicPath);
			}
		} else if(requestCode == IntentUtils.SELECT_IMAGE_INTENT && resultCode == Activity.RESULT_OK) {
			String imagePath = IntentUtils.getImagePath(getActivity(), data);
			
			ImageLoader.getInstance().loadImage(backgroundPreview, imagePath);
			File file = FileUtils.getResourceFile(imagePath);
			backgroundButton.setText(file.getName());
			
			ActiveListeningConfigActivity.ret.setBackground(imagePath);
		}
	}
}