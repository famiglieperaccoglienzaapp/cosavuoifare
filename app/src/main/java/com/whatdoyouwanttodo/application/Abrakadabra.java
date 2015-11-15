package com.whatdoyouwanttodo.application;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modella una playlist di immagini
 */
public class Abrakadabra implements Parcelable, Cloneable {
	public static final int EFFECT_NO_EFFECT = 0;
	public static final int EFFECT_KENBURNS = 1;
	
	private long id;
	private String name;
	private String[] imagePaths;
	private String soundPath;
	private String musicPath;
	private int imageEffect;

	public Abrakadabra(long id, String name, String[] imagePaths, String soundPath, String musicPath, int imageEffect) {
		this.id = id;
		this.name = name;
		this.imagePaths = imagePaths;
		this.soundPath = soundPath;
		this.musicPath = musicPath;
		this.imageEffect = imageEffect;
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
	
	public String getSoundPath() {
		return soundPath;
	}

	public void setSoundPath(String soundPath) {
		this.soundPath = soundPath;
	}

	public String getMusicPath() {
		return musicPath;
	}

	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}

	public int getImageEffect() {
		return imageEffect;
	}

	public void setImageEffect(int imageEffect) {
		this.imageEffect = imageEffect;
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
		dest.writeString(soundPath);
		dest.writeString(musicPath);
		dest.writeInt(imageEffect);
	}

	public final static Creator<Abrakadabra> CREATOR = new Creator<Abrakadabra>() {
		@Override
		public Abrakadabra createFromParcel(Parcel source) {
			long id = source.readLong();
			String name = source.readString();
			String[] imagePaths = null;
			source.readStringArray(imagePaths);
			String soundPath = source.readString();
			String musicPath = source.readString();
			int imageEffect = source.readInt();
			return new Abrakadabra(id, name, imagePaths, soundPath, musicPath, imageEffect);
		}

		@Override
		public Abrakadabra[] newArray(int size) {
			return new Abrakadabra[size];
		}
	};
	
	public void set(Abrakadabra adapted) {
		this.id = adapted.id;
		this.name = adapted.name;
		this.imagePaths = adapted.imagePaths.clone();
		this.soundPath = adapted.soundPath;
		this.musicPath = adapted.musicPath;
		this.imageEffect = adapted.imageEffect;
	}

	@Override
	public Abrakadabra clone() {
		return new Abrakadabra(id, name, imagePaths, soundPath, musicPath, imageEffect);
	}

	@Override
	public String toString() {
		return "Abrakadabra [id=" + id + ", name=" + name + ", imagePaths="
				+ Arrays.toString(imagePaths) + ", soundPath=" + soundPath
				+ ", musicPath=" + musicPath + ", imageEffect=" + imageEffect
				+ "]";
	}
}
