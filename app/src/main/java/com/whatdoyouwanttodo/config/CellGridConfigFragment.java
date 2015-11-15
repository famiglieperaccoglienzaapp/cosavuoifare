package com.whatdoyouwanttodo.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.ActiveListening;
import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;
import com.whatdoyouwanttodo.application.Abrakadabra;
import com.whatdoyouwanttodo.application.VideoPlaylist;
import com.whatdoyouwanttodo.db.ActiveListeningCursor;
import com.whatdoyouwanttodo.db.ChessboardCursor;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.db.AbrakadabraCursor;
import com.whatdoyouwanttodo.db.VideoPlaylistCursor;
import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.ui.ChooseButtonSetHelper;
import com.whatdoyouwanttodo.ui.ChooseButtonSetHelper.OnChooseClickListener;
import com.whatdoyouwanttodo.ui.ChooseButtonWithTextSetHelper;
import com.whatdoyouwanttodo.ui.QuestionDialog;
import com.whatdoyouwanttodo.utils.IntentUtils;

/**
 * Pannello di configurazione di una singola cella, usato da CellGrdiConfigActivity
 */
public class CellGridConfigFragment extends Fragment {
	private static final String CELL = "com.whatdoyouwanttodo.config.CellGridConfigFragment.CELL";

	private static final String INTENT_CHANGE_CELL_IMAGE = "change_cell_image";
	private static final String INTENT_CHANGE_CELL_AUDIO = "change_cell_audio";
	
	private OnCellChangeListener callback;
	private Cell cell;

	// for actions
	private ChooseButtonWithTextSetHelper actionHelper;

	public CellGridConfigFragment() {
	}
	
	public static CellGridConfigFragment newCellGridConfigFragment(
			Cell cell) {
		CellGridConfigFragment fragment = new CellGridConfigFragment();

		Bundle arguments = new Bundle();
		arguments.putParcelable(CELL, cell);
		fragment.setArguments(arguments);

		return fragment;
	}

	public interface OnCellChangeListener {
		public void onCellChange(Cell cell);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			callback = (OnCellChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement " + OnCellChangeListener.class.getName());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_cell_grid_config, container, false);
		
		// get cell to edit
		Bundle arguments = getArguments();
		this.cell = arguments.getParcelable(CELL);

		// tutorial
		/*
		if (ChessboardApplication.getCellTuturial() == false) {
			View name = rootView.findViewById(R.id.tutorial_name);
			View text = rootView.findViewById(R.id.tutorial_text);
			View confirm = rootView.findViewById(R.id.tutorial_ok);
			name.setVisibility(View.GONE);
			text.setVisibility(View.GONE);
			confirm.setVisibility(View.GONE);
		} else {
			View confirm = rootView.findViewById(R.id.tutorial_ok);
			confirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					View name = getActivity().findViewById(R.id.tutorial_name);
					View text = getActivity().findViewById(R.id.tutorial_text);
					View confirm = getActivity().findViewById(R.id.tutorial_ok);
					name.setVisibility(View.GONE);
					text.setVisibility(View.GONE);
					confirm.setVisibility(View.GONE);
					ChessboardApplication.setCellTutorial(false);
					ChessboardDbUtility dbu = new ChessboardDbUtility(getActivity());
					dbu.openWritable();
					dbu.setCellTutorial(false);
					dbu.close();
				}
			});
		}
		*/
		
		// set up name
		EditText nameText = (EditText) rootView.findViewById(R.id.etx_cell_name);
		nameText.setText(cell.getName());
		nameText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (GridConfigFragment.isEditorAction(actionId)) {
					String str = v.getText().toString();
					cell.setName(str);
					cell.setText(str);
					cell.setAudioPath(Configurations.TTS_PREFIX + str);
					callback.onCellChange(cell);
					return false;
				}
				return false;
			}
		});
		
		// set up background color buttons
		ChooseButtonSetHelper backgroundColorHelper = new ChooseButtonSetHelper();
		backgroundColorHelper.addButton(rootView.findViewById(R.id.btn_color_black),
				Color.BLACK, R.drawable.btn_color_black,
				R.drawable.btn_color_black_selected);
		backgroundColorHelper.addButton(rootView.findViewById(R.id.btn_color_gray),
				Color.GRAY, R.drawable.btn_color_gray,
				R.drawable.btn_color_gray_selected);
		backgroundColorHelper.addButton(rootView.findViewById(R.id.btn_color_white),
				Color.WHITE, R.drawable.btn_color_white,
				R.drawable.btn_color_white_selected);
		backgroundColorHelper.addButton(rootView.findViewById(R.id.btn_color_red),
				Color.RED, R.drawable.btn_color_red,
				R.drawable.btn_color_red_selected);
		backgroundColorHelper.addButton(rootView.findViewById(R.id.btn_color_green),
				Color.GREEN, R.drawable.btn_color_green,
				R.drawable.btn_color_green_selected);
		backgroundColorHelper.addButton(rootView.findViewById(R.id.btn_color_blue),
				Color.BLUE, R.drawable.btn_color_blue,
				R.drawable.btn_color_blue_selected);
		backgroundColorHelper.addButton(rootView.findViewById(R.id.btn_color_yellow),
				Color.YELLOW, R.drawable.btn_color_yellow,
				R.drawable.btn_color_yellow_selected);
		backgroundColorHelper.addButton(rootView.findViewById(R.id.btn_color_purple),
				Color.MAGENTA, R.drawable.btn_color_purple,
				R.drawable.btn_color_purple_selected);
		backgroundColorHelper.setDefaultButtonWithValue(cell.getBackgroundColor());
		backgroundColorHelper.setListener(new OnChooseClickListener() {
			@Override
			public void onClick(View view, int color) {
				cell.setBackgroundColor(color);
				callback.onCellChange(cell);
			}
		});
		backgroundColorHelper.initAll();
		
		// set up border width buttons
		ChooseButtonSetHelper borderWidthHelper = new ChooseButtonSetHelper();
		borderWidthHelper.addButton(
				rootView.findViewById(R.id.btn_border_no_border),
				Chessboard.BORDER_NO_BORDER, R.drawable.btn_border_no_border,
				R.drawable.btn_border_no_border_selected);
		borderWidthHelper.addButton(rootView.findViewById(R.id.btn_border_small),
				Chessboard.BORDER_SMALL, R.drawable.btn_border_small,
				R.drawable.btn_border_small_selected);
		borderWidthHelper.addButton(rootView.findViewById(R.id.btn_border_medium),
				Chessboard.BORDER_MEDIUM, R.drawable.btn_border_medium,
				R.drawable.btn_border_medium_selected);
		borderWidthHelper.addButton(rootView.findViewById(R.id.btn_border_large),
				Chessboard.BORDER_LARGE, R.drawable.btn_border_large,
				R.drawable.btn_border_large_selected);
		borderWidthHelper.setDefaultButtonWithValue(cell.getBorderWidth());
		borderWidthHelper.setListener(new OnChooseClickListener() {
			@Override
			public void onClick(View view, int width) {
				cell.setBorderWidth(width);
				callback.onCellChange(cell);
			}
		});
		borderWidthHelper.initAll();
		
		// set up border color buttons
		ChooseButtonSetHelper borderColorHelper = new ChooseButtonSetHelper();
		borderColorHelper.addButton(rootView.findViewById(R.id.btn_border_color_black),
				Color.BLACK, R.drawable.btn_border_color_black,
				R.drawable.btn_border_color_black_selected);
		borderColorHelper.addButton(rootView.findViewById(R.id.btn_border_color_gray),
				Color.GRAY, R.drawable.btn_border_color_gray,
				R.drawable.btn_border_color_gray_selected);
		borderColorHelper.addButton(rootView.findViewById(R.id.btn_border_color_white),
				Color.WHITE, R.drawable.btn_border_color_white,
				R.drawable.btn_border_color_white_selected);
		borderColorHelper.addButton(rootView.findViewById(R.id.btn_border_color_red),
				Color.RED, R.drawable.btn_border_color_red,
				R.drawable.btn_border_color_red_selected);
		borderColorHelper.addButton(rootView.findViewById(R.id.btn_border_color_green),
				Color.GREEN, R.drawable.btn_border_color_green,
				R.drawable.btn_border_color_green_selected);
		borderColorHelper.addButton(rootView.findViewById(R.id.btn_border_color_blue),
				Color.BLUE, R.drawable.btn_border_color_blue,
				R.drawable.btn_border_color_blue_selected);
		borderColorHelper.addButton(rootView.findViewById(R.id.btn_border_color_yellow),
				Color.YELLOW, R.drawable.btn_border_color_yellow,
				R.drawable.btn_border_color_yellow_selected);
		borderColorHelper.addButton(rootView.findViewById(R.id.btn_border_color_purple),
				Color.MAGENTA, R.drawable.btn_border_color_purple,
				R.drawable.btn_border_color_purple_selected);
		borderColorHelper.setDefaultButtonWithValue(cell.getBorderColor());
		borderColorHelper.setListener(new OnChooseClickListener() {
			@Override
			public void onClick(View view, int color) {
				cell.setBorderColor(color);
				callback.onCellChange(cell);
			}
		});
		borderColorHelper.initAll();
		
		// set up text width buttons
		ChooseButtonSetHelper textWidthHelper = new ChooseButtonSetHelper();
		textWidthHelper.addButton(
				rootView.findViewById(R.id.btn_text_very_small),
				Cell.TEXT_SMALL, R.drawable.btn_text_very_small,
				R.drawable.btn_text_very_small_selected);
		textWidthHelper.addButton(rootView.findViewById(R.id.btn_text_small),
				Cell.TEXT_NORMAL, R.drawable.btn_text_small,
				R.drawable.btn_text_small_selected);
		textWidthHelper.addButton(rootView.findViewById(R.id.btn_text_medium),
				Cell.TEXT_MEDIUM, R.drawable.btn_text_medium,
				R.drawable.btn_text_medium_selected);
		textWidthHelper.addButton(rootView.findViewById(R.id.btn_text_large),
				Cell.TEXT_LARGE, R.drawable.btn_text_large,
				R.drawable.btn_text_large_selected);
		textWidthHelper.setDefaultButtonWithValue(cell.getTextWidth());
		textWidthHelper.setListener(new OnChooseClickListener() {
			@Override
			public void onClick(View view, int width) {
				cell.setTextWidth(width);
				callback.onCellChange(cell);
			}
		});
		textWidthHelper.initAll();
		
		// set up text color buttons
		ChooseButtonSetHelper textColorHelper = new ChooseButtonSetHelper();
		textColorHelper.addButton(rootView.findViewById(R.id.btn_text_color_black),
				Color.BLACK, R.drawable.btn_text_color_black,
				R.drawable.btn_text_color_black_selected);
		textColorHelper.addButton(rootView.findViewById(R.id.btn_text_color_gray),
				Color.GRAY, R.drawable.btn_text_color_gray,
				R.drawable.btn_text_color_gray_selected);
		textColorHelper.addButton(rootView.findViewById(R.id.btn_text_color_white),
				Color.WHITE, R.drawable.btn_text_color_white,
				R.drawable.btn_text_color_white_selected);
		textColorHelper.addButton(rootView.findViewById(R.id.btn_text_color_red),
				Color.RED, R.drawable.btn_text_color_red,
				R.drawable.btn_text_color_red_selected);
		textColorHelper.addButton(rootView.findViewById(R.id.btn_text_color_green),
				Color.GREEN, R.drawable.btn_text_color_green,
				R.drawable.btn_text_color_green_selected);
		textColorHelper.addButton(rootView.findViewById(R.id.btn_text_color_blue),
				Color.BLUE, R.drawable.btn_text_color_blue,
				R.drawable.btn_text_color_blue_selected);
		textColorHelper.addButton(rootView.findViewById(R.id.btn_text_color_yellow),
				Color.YELLOW, R.drawable.btn_text_color_yellow,
				R.drawable.btn_text_color_yellow_selected);
		textColorHelper.addButton(rootView.findViewById(R.id.btn_text_color_purple),
				Color.MAGENTA, R.drawable.btn_text_color_purple,
				R.drawable.btn_text_color_purple_selected);
		textColorHelper.setDefaultButtonWithValue(cell.getTextColor());
		textColorHelper.setListener(new OnChooseClickListener() {
			@Override
			public void onClick(View view, int color) {
				cell.setTextColor(color);
				callback.onCellChange(cell);
			}
		});
		textColorHelper.initAll();
		
		// set up image selector
		ImageView buttonImage = (ImageView) rootView.findViewById(R.id.iv_button_image);
		// ImageUtils.getInstance().loadImageLazy(buttonImage, cell.getImagePath());
		buttonImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				IntentUtils.startSelectImageIntent(getActivity(), INTENT_CHANGE_CELL_IMAGE);
			}
		});
		
		// set up registration from button
		ImageView recordAudio = (ImageView) rootView.findViewById(R.id.iv_button_select_music);
		recordAudio.setOnClickListener(new OnClickListener() {
			@SuppressLint("InflateParams")
			@Override
			public void onClick(View view) {
				String defaultText = cell.getAudioPath();
				if(defaultText.startsWith(Configurations.TTS_PREFIX)) {
					defaultText = defaultText.substring(Configurations.TTS_PREFIX.length());
				} else {
					defaultText = "";
				}
				Resources res = getResources();
				String back = res.getString(R.string.activity_cell_grid_config_tts_message_back);
				String confirm = res.getString(R.string.activity_cell_grid_config_tts_message_confirm);
			    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_tts_or_audio, null);
			    Button selectFile = (Button) dialogView.findViewById(R.id.select_file);
			    final EditText ttsText = (EditText) dialogView.findViewById(R.id.tts_text);
			    ttsText.setText(defaultText);
			    builder.setView(dialogView);
			    builder.setNegativeButton(back, new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
			    });
			    builder.setPositiveButton(confirm, new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						cell.setAudioPath(Configurations.TTS_PREFIX + ttsText.getText().toString());
						callback.onCellChange(cell);
					}
			    });
				final AlertDialog dialog = builder.create();
				selectFile.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
						IntentUtils.startSelectAudioIntent(getActivity(), INTENT_CHANGE_CELL_AUDIO);
					}
			    });	
				dialog.show();
			}
		});
		
		// set up link to
		actionHelper = new ChooseButtonWithTextSetHelper(4);
		actionHelper.addButton((ViewGroup) rootView.findViewById(R.id.ll_link_to),
				rootView.findViewById(R.id.iv_link_to_image),
				rootView.findViewById(R.id.tv_link_to_text),
				R.string.activity_cell_grid_config_action_link_to,
				Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD, R.drawable.cell_link_to,
				R.drawable.cell_link_to);
		actionHelper.addButton((ViewGroup) rootView.findViewById(R.id.ll_music_slides),
				rootView.findViewById(R.id.iv_music_slides_image),
				rootView.findViewById(R.id.tv_music_slides_text),
				R.string.activity_cell_grid_config_action_music_slides,
				Cell.ACTIVITY_TYPE_ABRAKADABRA, R.drawable.cell_music_slides,
				R.drawable.cell_music_slides);
		actionHelper.addButton((ViewGroup) rootView.findViewById(R.id.ll_active_listening),
				rootView.findViewById(R.id.iv_active_listening_image),
				rootView.findViewById(R.id.tv_active_listening_text),
				R.string.activity_cell_grid_config_action_active_listening,
				Cell.ACTIVITY_TYPE_ACTIVE_LISTENING, R.drawable.cell_active_listening,
				R.drawable.cell_active_listening);
		actionHelper.addButton((ViewGroup) rootView.findViewById(R.id.ll_youttube),
				rootView.findViewById(R.id.iv_youtube_image),
				rootView.findViewById(R.id.tv_youtube_text),
				R.string.activity_cell_grid_config_action_play_video,
				Cell.ACTIVITY_TYPE_PLAY_VIDEO,
				R.drawable.cell_play_video,
				R.drawable.cell_play_video);
		int cellActivityType = cell.getActivityType();
		if (cellActivityType == Cell.ACTIVITY_TYPE_CLOSE_CHESSBOARD) {
			cellActivityType = Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD;
		}
		String actionName = getNameFromDb(cell.getActivityType(), cell.getActivityParam());
		actionHelper.setDefaultButtonWithValue(cellActivityType, actionName);
		actionHelper.setListener(new OnCellActivityClickListener(cellActivityType, cell.getActivityParam(), actionName));
		actionHelper.initAll();
		
		return rootView;
	}
	
	private String getNameFromDb(int type, long id) {
		if(type == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(getActivity());
			dbu.openReadable();

			Chessboard cb = null;
			ChessboardCursor cursorCb = dbu.getCursorOnChessboard(id);
			if (cursorCb != null) {
				while (cursorCb.moveToNext()) {
					cb = cursorCb.getChessboard();
				}
				cursorCb.close();
			}
			dbu.close();

			return cb.getName();
		} else if(type == Cell.ACTIVITY_TYPE_CLOSE_CHESSBOARD) {
		    Resources res = getResources();
		    String destStr = res.getString(R.string.activity_cell_grid_config_link_to_back);
			return destStr;
		} else if(type == Cell.ACTIVITY_TYPE_ABRAKADABRA) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(getActivity());
			dbu.openReadable();

			Abrakadabra ms = null;
			AbrakadabraCursor cursorCb = dbu.getCursorOnAbrakadabra(id);
			if (cursorCb != null) {
				while (cursorCb.moveToNext()) {
					ms = cursorCb.getAbrakadabra();
				}
				cursorCb.close();
			}
			dbu.close();
			
			return ms.getName();
		} else if(type == Cell.ACTIVITY_TYPE_ACTIVE_LISTENING) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(getActivity());
			dbu.openReadable();

			ActiveListening al = null;
			ActiveListeningCursor cursorCb = dbu.getCursorOnActiveListening(id);
			if (cursorCb != null) {
				while (cursorCb.moveToNext()) {
					al = cursorCb.getActiveListening();
				}
				cursorCb.close();
			}
			dbu.close();

			return al.getName();
		} else if(type == Cell.ACTIVITY_TYPE_PLAY_VIDEO) {
			ChessboardDbUtility dbu = new ChessboardDbUtility(getActivity());
			dbu.openReadable();

			VideoPlaylist pl = null;
			VideoPlaylistCursor cursorCb = dbu.getCursorOnVideoPlaylist(id);
			if (cursorCb != null) {
				while (cursorCb.moveToNext()) {
					pl = cursorCb.getVideoPlaylist();
				}
				cursorCb.close();
			}
			dbu.close();
			
			return pl.getName();
		}
		return "";
	}

	private class OnCellActivityClickListener implements ChooseButtonWithTextSetHelper.OnChooseClickListener {
		private int activityType;
		private long activityParam;
		private String activityName;

		public OnCellActivityClickListener(int activityType, long activityParam, String activityName) {
			this.activityType = activityType;
			this.activityParam = activityParam;
			this.activityName = activityName;
		}

		@Override
		public void onClick(View view, int param) {
			boolean showWarning = true;
			if(activityType == Cell.ACTIVITY_TYPE_NONE) {
				showWarning = false;
			}
			if(param == activityType) {
				showWarning = false;
			}
			
			if (param == Cell.ACTIVITY_TYPE_PLAY_VIDEO) {
				if(showWarning == false) {
					if (activityParam == 0) {
						activityParam = VideoPlaylistConfigActivity.NO_ID;
					}
					Intent intent = VideoPlaylistConfigActivity.getStartIntent(getActivity(), activityParam);
					startActivity(intent);
				} else {
					showOverrideWarning(activityType, activityName, new Action() {
						@Override
						public void doAction() {
							Intent intent = VideoPlaylistConfigActivity.getStartIntent(getActivity(), VideoPlaylistConfigActivity.NO_ID);
							startActivity(intent);
						}
					});
				}
			} else if (param == Cell.ACTIVITY_TYPE_ABRAKADABRA) {
				if(showWarning == false) {
					if (activityParam == 0) {
						activityParam = AbrakadabraConfigActivity.NO_ID;
					}
					Intent intent = AbrakadabraConfigActivity.getStartIntent(getActivity(), activityParam);
					startActivity(intent);
				} else {
					showOverrideWarning(activityType, activityName, new Action() {
						@Override
						public void doAction() {
							Intent intent = AbrakadabraConfigActivity.getStartIntent(getActivity(), AbrakadabraConfigActivity.NO_ID);
							startActivity(intent);
						}
					});
				}
			} else if (param == Cell.ACTIVITY_TYPE_ACTIVE_LISTENING) {
				if(showWarning == false) {
					if (activityParam == 0) {
						activityParam = ActiveListeningConfigActivity.NO_ID;
					}
					Intent intent = ActiveListeningConfigActivity.getStartIntent(getActivity(), activityParam);
					startActivity(intent);
				} else {					
					showOverrideWarning(activityType, activityName, new Action() {
						@Override
						public void doAction() {
							Intent intent = ActiveListeningConfigActivity.getStartIntent(getActivity(), ActiveListeningConfigActivity.NO_ID);
							startActivity(intent);
						}
					});
				}
			} else if (param == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD) {
				if(showWarning == false) {
					// set up link to grid button
					Intent intent = AllGridViewActivity.getStartIntentForSelection(getActivity());
					startActivity(intent);
				} else {
					showOverrideWarning(activityType, activityName, new Action() {
						@Override
						public void doAction() {
							Intent intent = AllGridViewActivity.getStartIntentForSelection(getActivity());
							startActivity(intent);
						}
					});
				}
			}
		}
	}
	
	private static interface Action {
		void doAction();
	}

	private void showOverrideWarning(int activityType, String name, final Action action) {
	    Resources res = getResources();
	    String message = null;
		if (activityType == Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD) {
			message = res.getString(R.string.activity_cell_grid_config_override_message1);
		} else {
			message = res.getString(R.string.activity_cell_grid_config_override_message2);
		}
		message = message.replace("###", "\'" + name + "\'");
	    String confirm = res.getString(R.string.activity_cell_grid_config_override_message_confirm);
	    String abort = res.getString(R.string.activity_cell_grid_config_override_message_abort);
		QuestionDialog dialog = new QuestionDialog(
				message,
				confirm, abort,
				new QuestionDialog.QuestionDialogListener() {
					@Override
					public void onPositiveClick() {
						action.doAction();
					}

					@Override
					public void onNegativeClick() {
						// do nothing
					}
				});
		dialog.show(getActivity());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == IntentUtils.SELECT_IMAGE_INTENT && resultCode == Activity.RESULT_OK) {
			String imagePath = IntentUtils.getImagePath(getActivity(), data);

			// change cell
			cell.setImagePath(imagePath);
			callback.onCellChange(cell);
		} else if(requestCode == IntentUtils.SELECT_AUDIO_INTENT && resultCode == Activity.RESULT_OK) {
			String audioPath = IntentUtils.getAudioPath(getActivity(), data);

			// change cell
			cell.setAudioPath(audioPath);
			callback.onCellChange(cell);
		}
	}
	
//	private void galleryAddPic() {
//		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//		File f = FileUtils.getResourceFile(cameraImagePath);
//		Uri contentUri = Uri.fromFile(f);
//		mediaScanIntent.setData(contentUri);
//		getActivity().sendBroadcast(mediaScanIntent);
//	}

	public void selectActionWithValue(int type, long param, String name) {
		cell.setActivityType(type);
		cell.setActivityParam(param);
		actionHelper.selectWithValue(type, name);
		actionHelper.setListener(new OnCellActivityClickListener(type, param, name));
	}

	public void setLinkToText(long id, String text) {
		selectActionWithValue(Cell.ACTIVITY_TYPE_OPEN_CHESSBOARD, id, text);
	}
}