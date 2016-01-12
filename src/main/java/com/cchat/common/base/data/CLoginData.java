package com.cchat.common.base.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SEAMAN on 2015/8/21.
 */
public class CLoginData implements Parcelable {
    public static final String TAG = "CLoginData";
    private String mPassword;
    private String mCard;

    public CLoginData(String card, String pass) {
        mCard = card;
        mPassword = pass;
    }

    public CLoginData() {

    }

    public void setCard(String card) {
        mCard = card;
    }

    public String getCard() {
        return mCard;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    @Override
    public String toString() {
        return "CLoginData{" +
                "mCard=" + mCard +
                ", mPassword='" + mPassword + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCard);
        dest.writeString(mPassword);
    }

    public static final Creator<CLoginData> CREATOR = new Creator<CLoginData>() {
        @Override
        public CLoginData createFromParcel(Parcel source) {
            CLoginData data = new CLoginData();
            data.setCard(source.readString());
            data.setPassword(source.readString());
            return data;
        }

        @Override
        public CLoginData[] newArray(int size) {
            return new CLoginData[size];
        }
    };
}
