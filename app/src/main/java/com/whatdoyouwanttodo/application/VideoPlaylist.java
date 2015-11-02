package com.whatdoyouwanttodo.application;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modella una playlist di video
 */
public class VideoPlaylist implements Parcelable, Cloneable {
	private long id;
	private String name;
	private String[] videoUrl;

	public VideoPlaylist(long id, String name, String[] videoUrl) {
		this.id = id;
		this.name = name;
		this.videoUrl = videoUrl;
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
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeStringArray(videoUrl);
	}

	public final static Creator<VideoPlaylist> CREATOR = new Creator<VideoPlaylist>() {
		@Override
		public VideoPlaylist createFromParcel(Parcel source) {
			long id = source.readLong();
			String name = source.readString();
			String[] videoUrl = null;
			source.readStringArray(videoUrl);
			return new VideoPlaylist(id, name, videoUrl);
		}

		@Override
		public VideoPlaylist[] newArray(int size) {
			return new VideoPlaylist[size];
		}
	};

	public void set(VideoPlaylist adapted) {
		this.id = adapted.id;
		this.name = adapted.name;
		this.videoUrl = adapted.videoUrl;
	}

	@Override
	public VideoPlaylist clone() {
		return new VideoPlaylist(id, name, videoUrl);
	}

	@Override
	public String toString() {
		return "VideoPlaylist [id=" + id + ", name=" + name + ", videoUrl="
				+ Arrays.toString(videoUrl) + "]";
	}
}
