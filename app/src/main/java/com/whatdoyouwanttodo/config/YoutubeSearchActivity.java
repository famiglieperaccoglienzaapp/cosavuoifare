package com.whatdoyouwanttodo.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Builder;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.whatdoyouwanttodo.R;
import com.whatdoyouwanttodo.settings.Configurations;
import com.whatdoyouwanttodo.utils.ImageLoader;

/**
 * Attivita' che permette la ricrca di un video su YouTube
 */
public class YoutubeSearchActivity extends Activity {
	private static YoutubeSearchReturn ret = null;
	private static OnVideoSelection callback = null;

	private ImageButton searchButton;
	private EditText searchEditText;
	private ListView resultListView;
	private YouTube youtube;
	private Search youtubeSearch;

	public static Intent getStartIntent(Activity caller, OnVideoSelection onVideoSelection) {
		Intent intent = new Intent(caller, YoutubeSearchActivity.class);
		ret = null;
		callback = onVideoSelection;
		return intent;
	}
	
	public static boolean haveReturnParams() {
		if(ret == null)
			return false;
		return true;
	}

	public static YoutubeSearchReturn getResultParams() {
		YoutubeSearchReturn clone = ret.clone();
		ret = null;
		return clone;
	}
	
	public static class YoutubeSearchReturn implements Cloneable {
		protected String id;

		@Override
		public YoutubeSearchReturn clone() {
			YoutubeSearchReturn clone = new YoutubeSearchReturn();
			clone.id = id;
			return clone;
		}

		@Override
		public String toString() {
			return "YoutubeSearchReturn [id=" + id + "]";
		}
	}
	
	public static class YoutubeVideo {
		protected String id;
		protected String title;
		protected String picturePath;
		
		@Override
		public String toString() {
			return "YoutubeVideo [id=" + id + ", title=" + title
					+ ", picturePath=" + picturePath + "]";
		}
	}
	
	private static class DialogArrayAdapter extends ArrayAdapter<YoutubeVideo> {
		private Activity activity;
		private List<YoutubeVideo> values;

		public DialogArrayAdapter(Activity activity, int resource, List<YoutubeVideo> videos) {
			super(activity, resource, videos);
			this.activity = activity;
			this.values = videos;
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

			text.setText(values.get(position).title);
			ImageLoader.getInstance().loadImageLazy(image, values.get(position).picturePath);

			return vi;
		}
	}
	
	protected interface OnVideoSelection {
		void onSelect(YoutubeVideo item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_youtube_search);
		
		searchButton = (ImageButton) findViewById(R.id.search_button);
		searchButton.setOnClickListener(this.onSearch);
		
		searchEditText = (EditText) findViewById(R.id.search_text);
		
		resultListView = (ListView) findViewById(R.id.search_result);
		resultListView.setOnItemClickListener(this.onVideoSelection);
		
		HttpTransport netHttpTransport = new NetHttpTransport();
		JsonFactory gsonFactory = new GsonFactory();
		HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {
			}
		};
		String appName = getResources().getString(R.string.app_name);
		Builder youtubeBuilder = new Builder(netHttpTransport, gsonFactory, httpRequestInitializer);
		youtubeBuilder.setApplicationName(appName);
		youtube = youtubeBuilder.build();
		youtubeSearch = youtube.search();
	}
	
	private OnClickListener onSearch = new OnClickListener() {
		@Override
		public void onClick(View view) {
			try {
				searchButton.setEnabled(false);
				
				// get search text
				String searchText = searchEditText.getText().toString();
				
				// prepare youtube search
				YouTube.Search.List search = youtubeSearch.list("id,snippet");
				search.setKey(Configurations.BROWSER_KEY);
				search.setQ(searchText);
				search.setType("video");
				search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
				search.setMaxResults(8L);
				
				// do search
				try {
					SearchListResponse searchResponse = search.execute();
					
					List<SearchResult> searchResultList = searchResponse.getItems();
					if (searchResultList.size() == 0) {
						String noVideoStr = getResources().getString(R.string.activity_youtube_search_no_video);
						Toast toast = Toast.makeText(YoutubeSearchActivity.this, noVideoStr, Toast.LENGTH_LONG);
						toast.show();
					} else {
						List<YoutubeVideo> videos = new LinkedList<YoutubeVideo>();
						Iterator<SearchResult> it = searchResultList.iterator();
						while (it.hasNext()) {
							SearchResult result = it.next();
							ResourceId rId = result.getId();
							if (rId.getKind().equals("youtube#video")) {
								YoutubeVideo item = new YoutubeVideo();
								
								item.id = rId.getVideoId();
								
								item.title = result.getSnippet().getTitle();

								String urlString = "https://i.ytimg.com/vi/" + rId.getVideoId() + "/0.jpg";
								URL url = null;
								try {
									url = new URL(urlString);
								} catch (MalformedURLException e) {
									// not used
								}
								try {
									item.picturePath = null;
									Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
									File outputDir = getCacheDir();
									File outputFile = File.createTempFile("youtube_thumbnail", ".png", outputDir);
									bitmap.compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(outputFile));
									bitmap = null;
									item.picturePath = outputFile.getPath();
								} catch (IOException e) {
									// not used
								}
								
								videos.add(item);
							}
						}
						showResults(videos);
					}
				} catch (IOException e) {
					String failedStr = getResources().getString(R.string.activity_youtube_search_failed);
					Toast toast = Toast.makeText(YoutubeSearchActivity.this, failedStr, Toast.LENGTH_LONG);
					toast.show();
					searchButton.setEnabled(true);
				}				
			} catch (IOException e) {
				String failedStr = getResources().getString(R.string.activity_youtube_search_no_request);
				Toast toast = Toast.makeText(YoutubeSearchActivity.this, failedStr, Toast.LENGTH_LONG);
				toast.show();
				searchButton.setEnabled(true);
			}
		}
	};

	private void showResults(List<YoutubeVideo> videos) {
		ArrayAdapter<YoutubeVideo> videoAdapter = new DialogArrayAdapter(
				this, android.R.layout.simple_spinner_item, videos);
		resultListView.setAdapter(videoAdapter);
		searchButton.setEnabled(true);
	}
	
	private OnItemClickListener onVideoSelection = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			YoutubeVideo item = (YoutubeVideo) parent.getItemAtPosition(position);
			ret = new YoutubeSearchReturn();
			ret.id = item.id;
			callback.onSelect(item);
			finish();
		}
	};
}
