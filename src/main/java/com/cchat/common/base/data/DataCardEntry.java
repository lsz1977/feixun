/**
 * Project: Callga
 * Create At 2014-8-7.
 * @author hhool
 */
package com.cchat.common.base.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DataCardEntry implements Parcelable, Cloneable, IDisplayName {
    private String mCard;                  //用户在Callga中的系统帐号 例：100031
    private String mCardXmlUrl;           //用户详细信息的地址
    private String mStyleLoc;              //头像上传网络地址

    public DataCardEntry(Parcel in) {
        mCard = in.readString();
        mCardXmlUrl = in.readString();
        mStyleLoc = in.readString();
    }

    public DataCardEntry() {

    }

    public String getCard() {
        return mCard;
    }

    public DataCardEntry setCard(String card) {
        if (card == null || card.length() == 0)
            return DataCardEntry.this;
        this.mCard = card;
        return DataCardEntry.this;
    }

    public String getCardXmlUrl() {
        return mCardXmlUrl;
    }

    public DataCardEntry setCardXmlUrl(String cardXml) {
        if (cardXml == null || cardXml.length() == 0)
            return DataCardEntry.this;
        this.mCardXmlUrl = cardXml;
        return DataCardEntry.this;
    }

    public String getStyleLoc() {
        return mStyleLoc;
    }

    public DataCardEntry setStyleLoc(String styleLoc) {
        if (styleLoc == null || styleLoc.length() == 0)
            return DataCardEntry.this;
        this.mStyleLoc = styleLoc;
        return DataCardEntry.this;
    }

    public void mergeFrom(DataCardEntry info) {
        if (info == null)
            return;
        this.setCard(info.getCard());
        this.setCardXmlUrl(info.getCardXmlUrl());
        this.setStyleLoc(info.getStyleLoc());
    }

    @Override
    public DataCardEntry clone() {
        DataCardEntry info = new DataCardEntry();
        info.setCard(this.getCard());
        info.setCardXmlUrl(this.getCardXmlUrl());
        info.setStyleLoc(this.getStyleLoc());
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataCardEntry) {
            DataCardEntry cardInfo = (DataCardEntry) o;
            if (cardInfo.getCard() != null && mCard != null) {
                if (cardInfo.getCard().equals(mCard)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "mCard:" + mCard + " " +
                "mCardXml:" + mCardXmlUrl + " " +
                "mStyleLoc:" + mStyleLoc;
    }

    @Override
    public String getDisplayName() {
        return mCard;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCard);
        dest.writeString(mCardXmlUrl);
        dest.writeString(mStyleLoc);
    }

    public static final Creator<DataCardEntry> CREATOR = new Creator<DataCardEntry>() {
        @Override
        public DataCardEntry createFromParcel(Parcel in) {
            return new DataCardEntry(in);
        }

        @Override
        public DataCardEntry[] newArray(int size) {
            return new DataCardEntry[size];
        }
    };
}
