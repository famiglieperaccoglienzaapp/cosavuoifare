package com.whatdoyouwanttodo.application;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modella una tabella AAC
 */
public class Chessboard implements Parcelable, Cloneable {
	public static final int BORDER_NO_BORDER = 0;
	public static final int BORDER_SMALL = 8;
	public static final int BORDER_MEDIUM = 16;
	public static final int BORDER_LARGE = 32;

	private long id;
	private int parentId;
	private String name;
	private int rowCount;
	private int columnCount;
	private int backgroundColor;
	private int borderWidth;

	public Chessboard(long id, int parentId, String name, int rowCount,
			int columnCount, int backgroundColor, int borderWidth) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.backgroundColor = backgroundColor;
		this.borderWidth = borderWidth;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeInt(parentId);
		dest.writeString(name);
		dest.writeInt(rowCount);
		dest.writeInt(columnCount);
		dest.writeInt(backgroundColor);
		dest.writeInt(borderWidth);
	}

	public final static Creator<Chessboard> CREATOR = new Creator<Chessboard>() {
		@Override
		public Chessboard createFromParcel(Parcel source) {
			long id = source.readLong();
			int parentId = source.readInt();
			String name = source.readString();
			int rowCount = source.readInt();
			int columnCount = source.readInt();
			int backgroundColor = source.readInt();
			int borderWidth = source.readInt();

			return new Chessboard(id, parentId, name, rowCount, columnCount,
					backgroundColor, borderWidth);
		}

		@Override
		public Chessboard[] newArray(int size) {
			return new Chessboard[size];
		}
	};

	@Override
	public Chessboard clone() {
		return new Chessboard(id, parentId, name, rowCount, columnCount,
				backgroundColor, borderWidth);
	}

	@Override
	public String toString() {
		return "Chessboard [id=" + id + ", parentId=" + parentId + ", name="
				+ name + ", rowCount=" + rowCount + ", columnCount="
				+ columnCount + ", backgroundColor=" + backgroundColor
				+ ", borderWidth=" + borderWidth + "]";
	}

}
