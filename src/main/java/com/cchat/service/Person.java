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
public class Person implements Parcelable{
	private int id;
	private String name;
	private String type;

	public Person() {
	}

	public Person(int id, String name, String type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(type);
	}
	public static final Creator<Person> CREATOR = new Creator<Person>() {

		@Override
		public Person createFromParcel(Parcel source) {
			return new Person(source.readInt(), source.readString(), source.readString());
		}

		@Override
		public Person[] newArray(int size) {
			return new Person[size];
		}
	};

}
