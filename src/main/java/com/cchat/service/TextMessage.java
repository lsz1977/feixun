/**
 * 
 */
package com.cchat.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author HD
 *
 */
public class TextMessage implements Parcelable{
	private int id;
	private String text;
	private String from;

	/**
	 * 
	 */
	public TextMessage() {
		// TODO Auto-generated constructor stub
	}

	
	public TextMessage(int id, String text, String from) {
		super();
		this.id = id;
		this.text = text;
		this.from = from;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeString(text);
		dest.writeString(from);
	}
	public static final Creator<TextMessage> CREATOR = new Creator<TextMessage>() {

		@Override
		public TextMessage createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new TextMessage(source.readInt(),source.readString(),source.readString());
		}

		@Override
		public TextMessage[] newArray(int size) {
			// TODO Auto-generated method stub
			return new TextMessage[size];
		}
	};

}
