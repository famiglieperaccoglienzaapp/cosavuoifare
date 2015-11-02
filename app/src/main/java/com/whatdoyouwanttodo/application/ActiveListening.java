package com.whatdoyouwanttodo.application;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modella un Ascolto Attivo
 */
public class ActiveListening implements Parcelable, Cloneable {
	private long id;
	private String name;
	private String background;
	private String[] musicPaths;
	private int interval;
	private String registrationPath;
	private int pause;
	private int pauseInterval;

	public ActiveListening(long id, String name, String background, String[] musicPaths, int interval,
			String registrationPath, int pause, int pauseInterval) {
		this.id = id;
		this.name = name;
		this.background = background;
		this.musicPaths = musicPaths;
		this.interval = interval;
		this.registrationPath = registrationPath;
		this.pause = pause;
		this.pauseInterval = pauseInterval;
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
	
	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String[] getMusicPath() {
		return musicPaths;
	}

	public void setMusicPath(String[] musicPaths) {
		this.musicPaths = musicPaths;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getRegistrationPath() {
		return registrationPath;
	}

	public void setRegistrationPath(String registrationPath) {
		this.registrationPath = registrationPath;
	}

	public int getPause() {
		return pause;
	}

	public void setPause(int pause) {
		this.pause = pause;
	}
	
	public int getPauseInterval() {
		return pauseInterval;
	}

	public void setPauseInterval(int pauseInterval) {
		this.pauseInterval = pauseInterval;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(background);
		dest.writeStringArray(musicPaths);
		dest.writeLong(interval);
		dest.writeString(registrationPath);
		dest.writeInt(pause);
		dest.writeInt(pauseInterval);
	}

	public final static Creator<ActiveListening> CREATOR = new Creator<ActiveListening>() {
		@Override
		public ActiveListening createFromParcel(Parcel source) {
			long id = source.readLong();
			String name = source.readString();
			String background = source.readString();
			String[] musicPaths = null;
			source.readStringArray(musicPaths);
			int interval = source.readInt();
			String registrationPath = source.readString();
			int pause = source.readInt();
			int pauseInterval = source.readInt();
			return new ActiveListening(id, name, background, musicPaths, interval, registrationPath, pause, pauseInterval);
		}

		@Override
		public ActiveListening[] newArray(int size) {
			return new ActiveListening[size];
		}
	};
	
	public void set(ActiveListening adapted) {
		this.id = adapted.id;
		this.name = adapted.name;
		this.background = adapted.background;
		this.musicPaths = adapted.musicPaths;
		this.interval = adapted.interval;
		this.registrationPath = adapted.registrationPath;
		this.pause = adapted.pause;
		this.pauseInterval = adapted.pause;
	}

	@Override
	public ActiveListening clone() {
		return new ActiveListening(id, name, background, musicPaths, interval, registrationPath, pause, pauseInterval);
	}

	@Override
	public String toString() {
		return "ActiveListening [id=" + id + ", name=" + name + ", background="
				+ background + ", musicPaths=" + Arrays.toString(musicPaths)
				+ ", interval=" + interval + ", registrationPath="
				+ registrationPath + ", pause=" + pause + ", pauseInterval="
				+ pauseInterval + "]";
	}
}
