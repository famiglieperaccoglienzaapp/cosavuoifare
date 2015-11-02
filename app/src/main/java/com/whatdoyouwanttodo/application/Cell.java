package com.whatdoyouwanttodo.application;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modella una cella di una tabella AAC
 */
public class Cell implements Parcelable, Cloneable {
	public static final int BORDER_NO_BORDER = 0;
	public static final int BORDER_SMALL = 8;
	public static final int BORDER_MEDIUM = 16;
	public static final int BORDER_LARGE = 32;

	public static final int TEXT_SMALL = 16;
	public static final int TEXT_NORMAL = 36;
	public static final int TEXT_MEDIUM = 46;
	public static final int TEXT_LARGE = 58;

	public static final int ACTIVITY_TYPE_NONE = 0;
	public static final int ACTIVITY_TYPE_OPEN_CHESSBOARD = 1;
	public static final int ACTIVITY_TYPE_CLOSE_CHESSBOARD = 2;
	public static final int ACTIVITY_TYPE_ABRAKADABRA = 3;
	public static final int ACTIVITY_TYPE_ACTIVE_LISTENING = 4;
	public static final int ACTIVITY_TYPE_PLAY_VIDEO = 5;

	private long id;
	private long chessboard;
	private String name;
	private int row;
	private int column;
	private int backgroundColor;
	private int borderWidth;
	private int borderColor;
	private String text;
	private int textWidth;
	private int textColor;
	private String imagePath;
	private String audioPath;
	private int activityType;
	private long activityParam;

	public Cell(long id, long chessboard, String name, int row, int column,
			int backgroundColor, int borderWidth, int borderColor, String text,
			int textWidth, int textColor, String imagePath, String audioPath,
			int activityType, long activityParam) {
		this.id = id;
		this.chessboard = chessboard;
		this.name = name;
		this.row = row;
		this.column = column;
		this.backgroundColor = backgroundColor;
		this.borderWidth = borderWidth;
		this.borderColor = borderColor;
		this.text = text;
		this.textWidth = textWidth;
		this.textColor = textColor;
		this.imagePath = imagePath;
		this.audioPath = audioPath;
		this.activityType = activityType;
		this.activityParam = activityParam;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getChessboard() {
		return chessboard;
	}

	public void setChessboard(long chessboard) {
		this.chessboard = chessboard;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getTextWidth() {
		return textWidth;
	}

	public void setTextWidth(int textWidth) {
		this.textWidth = textWidth;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}

	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public long getActivityParam() {
		return activityParam;
	}

	public void setActivityParam(long param) {
		this.activityParam = param;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(chessboard);
		dest.writeString(name);
		dest.writeInt(row);
		dest.writeInt(column);
		dest.writeInt(backgroundColor);
		dest.writeInt(borderWidth);
		dest.writeInt(borderColor);
		dest.writeString(text);
		dest.writeInt(textWidth);
		dest.writeInt(textColor);
		dest.writeString(imagePath);
		dest.writeString(audioPath);
		dest.writeInt(activityType);
		dest.writeLong(activityParam);
	}

	public final static Creator<Cell> CREATOR = new Creator<Cell>() {
		@Override
		public Cell createFromParcel(Parcel source) {
			long id = source.readLong();
			long chessboard = source.readLong();
			String name = source.readString();
			int row = source.readInt();
			int column = source.readInt();
			int backgroundColor = source.readInt();
			int borderWidth = source.readInt();
			int borderColor = source.readInt();
			String text = source.readString();
			int textWidth = source.readInt();
			int textColor = source.readInt();
			String imagePath = source.readString();
			String audioPath = source.readString();
			int activityType = source.readInt();
			long activityParam = source.readLong();
			return new Cell(id, chessboard, name, row, column, backgroundColor,
					borderWidth, borderColor, text, textWidth, textColor,
					imagePath, audioPath, activityType, activityParam);
		}

		@Override
		public Cell[] newArray(int size) {
			return new Cell[size];
		}
	};
	
	public void set(Cell adapted) {
		this.id = adapted.id;
		this.chessboard = adapted.chessboard;
		this.name = adapted.name;
		this.row = adapted.row;
		this.column = adapted.column;
		this.backgroundColor = adapted.backgroundColor;
		this.borderWidth = adapted.borderWidth;
		this.borderColor = adapted.borderColor;
		this.text = adapted.text;
		this.textWidth = adapted.textWidth;
		this.textColor = adapted.textColor;
		this.imagePath = adapted.imagePath;
		this.audioPath = adapted.audioPath;
		this.activityType = adapted.activityType;
		this.activityParam = adapted.activityParam;
	}

	@Override
	public Cell clone() {
		return new Cell(id, chessboard, name, row, column, backgroundColor,
				borderWidth, borderColor, text, textWidth, textColor,
				imagePath, audioPath, activityType, activityParam);
	}

	@Override
	public String toString() {
		return "Cell [id=" + id + ", chessboard=" + chessboard + ", name="
				+ name + ", row=" + row + ", column=" + column
				+ ", backgroundColor=" + backgroundColor + ", borderWidth="
				+ borderWidth + ", borderColor=" + borderColor + ", text="
				+ text + ", textWidth=" + textWidth + ", textColor="
				+ textColor + ", imagePath=" + imagePath + ", audioPath="
				+ audioPath + ", activityType=" + activityType
				+ ", activityParam=" + activityParam + "]";
	}

}
