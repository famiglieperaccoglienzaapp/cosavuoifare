package com.whatdoyouwanttodo.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Builder;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.application.VideoPlaylist;
import com.whatdoyouwanttodo.config.YoutubeSearchActivity.YoutubeVideo;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.db.VideoPlaylistCursor;
import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.settings.Constants;
import com.whatdoyouwanttodo.ui.ChooseOptionDialog;
import com.whatdoyouwanttodo.ui.ImageListHelper;
import com.whatdoyouwanttodo.ui.ImageListHelper.Item;
import com.whatdoyouwanttodo.ui.MessageDialog;
import com.whatdoyouwanttodo.utils.ImageLoader;
import com.whatdoyouwanttodo.utils.IntentUtils;

/**
 * Attivita' che mostra la configurazione di una playlist di video
 */
public class VideoPlaylistConfigActivity extends ActionBarActivity {
	public static final long NO_ID = -1;

	public static Intent getStartIntent(Activity caller, long id) {
		Intent intent = new Intent(caller, VideoPlaylistConfigActivity.class);
		VideoPlaylistConfigActivity.ret = new VideoPlaylistReturn(caller);
		if (id == NO_ID) {
			ret = new VideoPlaylistReturn(caller);
		} else {
			VideoPlaylist videoPlaylist = null;

			ChessboardDbUtility dbu = new ChessboardDbUtility(caller);
			dbu.openReadable();
			VideoPlaylistCursor cursor = dbu.getCursorOnVideoPlaylist(id);
			while (cursor.moveToNext()) {
				videoPlaylist = cursor.getVideoPlaylist();
			}
			cursor.close();
			dbu.close();

			ret = new VideoPlaylistReturn(videoPlaylist);
		}
		return intent;
	}

	private static VideoPlaylistReturn ret;

	public static boolean haveReturnParams() {
		if (ret == null)
			return false;
		return true;
	}

	public static VideoPlaylistReturn getReturnParams() {
		VideoPlaylistReturn clone = ret.clone();
		ret = null;
		return clone;
	}

	public static class VideoPlaylistReturn implements Cloneable {
		private long id;
		private String name;
		private String[] videoUrl;

		public VideoPlaylistReturn(Context context) {
			id = -1;
			name = Constants.getInstance(context).NEW_VIDEO_PLAYLIST.getName();
			videoUrl = Constants.getInstance(context).NEW_VIDEO_PLAYLIST.getVideoUrl();
		}

		public VideoPlaylistReturn(VideoPlaylist vp) {
			this.id = vp.getId();
			this.name = vp.getName();
			this.videoUrl = vp.getVideoUrl();
		}

		private VideoPlaylistReturn() {
			// do nothing
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String[] getVideoUrl() {
			return videoUrl;
		}

		public void setVideoUrl(String[] videoUrl) {
			this.videoUrl = videoUrl;
		}

		@Override
		public VideoPlaylistReturn clone() {
			VideoPlaylistReturn ret = new VideoPlaylistReturn();
			ret.id = id;
			ret.name = name;
			ret.videoUrl = videoUrl.clone();
			return ret;
		}

		@Override
		public String toString() {
			return "VideoViewConfigReturn [id=" + id + ", name=" + name
					+ ", videoUrl=" + Arrays.toString(videoUrl) + "]";
		}
	}

	private ImageListHelper videoLayoutHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_playlist_config);

		// initialize name field
		EditText name = (EditText) findViewById(R.id.video_view_name);
		String nameText = ret.getName();
		if (nameText.equals("") == false) {
			name.setText(nameText);
		}
		name.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (GridConfigFragment.isEditorAction(actionId)) {
					ret.setName(v.getText().toString());
					return false;
				}
				return false;
			}
		});

		// initialize video list
		LinearLayout videoLayout = (LinearLayout) findViewById(R.id.layout_video_container);
		videoLayoutHelper = new ImageListHelper(videoLayout,
				this, new ImageListHelper.OnListChange() {
					@Override
					public void onListChange(Item[] items) {
						String[] paths = new String[items.length];
						for(int i = 0; i < items.length; i++)
							paths[i] = items[i].getData();
						ret.setVideoUrl(paths);
					}
				}, new ImageListHelper.OnAction() {
					@Override
					public LinearLayout onMakeLayout(LayoutInflater inflater, ViewGroup parent) {
						return (LinearLayout) inflater.inflate(
								R.layout.activity_video_playlist_config_video_layout, parent, false);					}

					@Override
					public void onUpdate(TextView text, ImageView image,
							Item item) {
						String name = item.getName();
						String imagePath = item.getImagePath();
						
						text.setText(name);
						if(imagePath != null)
							ImageLoader.getInstance().loadImageLazy(image, imagePath);

//						Uri imageUri = Uri.fromFile(file);
//						ImageLoader.getInstance().loadImageLazy(image,
//								imageUri.getPath());
					}
					@Override
					public void onChange(int id) {
						ChooseOptionDialog dialog = new ChooseOptionDialog();
						Resources res = getResources();
						String local = res.getString(R.string.activity_video_view_config_select_local_file);
						String youtube = res.getString(R.string.activity_video_view_config_search_on_youtube);
						dialog.addOption(local, new SelectVideoFromLocale(id));
						dialog.addOption(youtube, new SelectVideoFromYoutube(id));
						dialog.show(VideoPlaylistConfigActivity.this);
					}

					@Override
					public void onNew(int id) {
						ChooseOptionDialog dialog = new ChooseOptionDialog();
						Resources res = getResources();
						String local = res.getString(R.string.activity_video_view_config_select_local_file);
						String youtube = res.getString(R.string.activity_video_view_config_search_on_youtube);
						dialog.addOption(local, new SelectVideoFromLocale(id));
						dialog.addOption(youtube, new SelectVideoFromYoutube(id));
						dialog.show(VideoPlaylistConfigActivity.this);
					}
				});
		String[] videoUrl = ret.getVideoUrl();
		Item[] itemList = new Item[videoUrl.length];
		for (int i = 0; i < videoUrl.length; i++) {
			String videoId = videoUrl[i].substring(Configurations.YOUTUBE_PREFIX.length());
			if (videoUrl[i].startsWith(Configurations.YOUTUBE_PREFIX)) {
				String picturePath = null;
				try {
					String urlString = "https://i.ytimg.com/vi/" + videoId + "/0.jpg";
					Bitmap bitmap = BitmapFactory.decodeStream(new URL(urlString).openConnection().getInputStream());
					File outputDir = getCacheDir();
					File outputFile = File.createTempFile("youtube_thumbnail", ".png", outputDir);
					bitmap.compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(outputFile));
					bitmap = null;
					picturePath = outputFile.getPath();
				} catch (Exception ex) {
					// do nothing
				}

				String title;
				try {
					title = new GetTitleTask().execute(videoId).get(); // getTitle(videoId);
				} catch (InterruptedException ex) {
					title = "";
				} catch (ExecutionException ex) {
					title = "";
				}
				
				itemList[i] = new Item(title, picturePath, videoUrl[i]);
			} else {
				File file = new File(videoUrl[i]);
				itemList[i] = new Item(file.getName(), null, videoUrl[i]);
			}
		}
		videoLayoutHelper.initAll(itemList);
	}

	class GetTitleTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... videoIdList) {
			String videoId = videoIdList[0];

			HttpTransport netHttpTransport = new NetHttpTransport();
			JsonFactory gsonFactory = new GsonFactory();
			HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {
				}
			};
			String appName = getResources().getString(R.string.app_name);
			Builder youtubeBuilder = new Builder(netHttpTransport, gsonFactory, httpRequestInitializer);
			youtubeBuilder.setApplicationName(appName);
			YouTube youtube = youtubeBuilder.build();
			Videos youtubeVideos = youtube.videos();

			try {
				YouTube.Videos.List videoDetails = youtubeVideos.list("snippet");
				videoDetails.setKey(Configurations.BROWSER_KEY);
				videoDetails.setId(videoId);
				videoDetails.setMaxResults(1L);

				// do search
				VideoListResponse videoResponse = videoDetails.execute();

				List<Video> searchResultList = videoResponse.getItems();
				if (searchResultList.size() == 0) {
					return "Titolo non trovato";
				} else {
					Iterator<Video> it = searchResultList.iterator();
					while (it.hasNext()) {
						Video result = it.next();
						return result.getSnippet().getTitle();
					}
				}
			} catch (IOException e) {
				return "Errore nel recupero";
			}

			return "";
		}
	}

	private class SelectVideoFromLocale implements
			ChooseOptionDialog.ChooseOptionListener {
		private int position;

		public SelectVideoFromLocale(int position) {
			this.position = position;
		}

		@Override
		public void onChoose() {
			IntentUtils.startSelectVideoIntent(VideoPlaylistConfigActivity.this, "add_youtube_video", position);
		}
	}

	private class SelectVideoFromYoutube implements
			ChooseOptionDialog.ChooseOptionListener {
		private int position;

		public SelectVideoFromYoutube(int position) {
			this.position = position;
		}

		@Override
		public void onChoose() {
			Intent intent = YoutubeSearchActivity.getStartIntent(
					VideoPlaylistConfigActivity.this,
					new YoutubeSearchActivity.OnVideoSelection() {
						@Override
						public void onSelect(YoutubeVideo item) {
							videoLayoutHelper.add(position,
									new Item(
											item.title,
											item.picturePath,
											Configurations.YOUTUBE_PREFIX + item.id));
						}
					});
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_playlist_config, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_back) {
			// back action
			ret = null;
			finish();
			return true;
		} else if (id == R.id.action_save) {
			// save action
			if (ret.videoUrl.length > 0 && ret.name.length() > 0) {
				saveConfig();
				finish();
			} else {
				Resources res = getResources();
			    String title = res.getString(R.string.activity_video_view_config_warning);
			    String message = res.getString(R.string.activity_video_view_config_warning_message);
			    String confirm = res.getString(R.string.activity_video_view_config_warning_confirm);
				MessageDialog.showMessage(this, title, message, confirm);
			}
			return true;
		}
		/* else if (id == R.id.action_preview_content) {
			// show preview
			if (ret.videoUrl.length > 0 && ret.name.length() > 0) {
				Intent intent = VideoPlaylistActivity.getStartIntentWithParams(
						this, ret.getVideoUrl());
				startActivity(intent);
			} else {
				Resources res = getResources();
			    String title = res.getString(R.string.activity_video_view_config_warning);
			    String message = res.getString(R.string.activity_video_view_config_warning_message);
			    String confirm = res.getString(R.string.activity_video_view_config_warning_confirm);
				MessageDialog.showMessage(this, title, message, confirm);
			}
		} */

		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onBackPressed() {
		ret = null;
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == IntentUtils.SELECT_VIDEO_INTENT && resultCode == Activity.RESULT_OK) {
			String imagePath = IntentUtils.getVideoPath(this, data);

			// update image
			int id = IntentUtils.getIntentParam();
			File file = new File(imagePath);
			Item imagePathItem = new Item(file.getName(), null, imagePath);
			videoLayoutHelper.add(id, imagePathItem);
		}
	}

	private void saveConfig() {
		ChessboardDbUtility dbu = new ChessboardDbUtility(this);
		dbu.openWritable();

		// save video view
		long id = dbu.addVideoPlaylist(ret.name, ret.videoUrl);
		ret.id = id;

		dbu.close();
	}
}
