package com.whatdoyouwanttodo.application;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modella una playlist di immagini
 */
public class MusicSlides implements Parcelable, Cloneable {
	private long id;
	private String name;
	private String[] imagePaths;
	private String musicPath;

	public MusicSlides(long id, String name, String[] imagePaths, String musicPath) {
		this.id = id;
		this.name = name;
		this.imagePaths = imagePaths;
		this.musicPath = musicPath;
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

	public String[] getImagePaths() {
		return imagePaths;
	}

	public void setImagePaths(String[] imagePaths) {
		this.imagePaths = imagePaths;
	}

	public String getMusicPath() {
		return musicPath;
	}

	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeStringArray(imagePaths);
		dest.writeString(musicPath);
	}

	public final static Creator<MusicSlides> CREATOR = new Creator<MusicSlides>() {
		@Override
		public MusicSlides createFromParcel(Parcel source) {
			long id = source.readLong();
			String name = source.readString();
			String[] imagePaths = null;
			source.readStringArray(imagePaths);
			String musicPath = source.readString();
			return new MusicSlides(id, name, imagePaths, musicPath);
		}

		@Override
		public MusicSlides[] newArray(int size) {
			return new MusicSlides[size];
		}
	};
	
	public void set(MusicSlides adapted) {
		this.id = adapted.id;
		this.name = adapted.name;
		this.imagePaths = adapted.imagePaths.clone();
		this.musicPath = adapted.musicPath;
	}

	@Override
	public MusicSlides clone() {
		return new MusicSlides(id, name, imagePaths, musicPath);
	}

	@Override
	public String toString() {
		return "MusicSlides [id=" + id + ", name=" + name + ", imagePaths="
				+ Arrays.toString(imagePaths) + ", musicPath=" + musicPath
				+ "]";
	}
}
