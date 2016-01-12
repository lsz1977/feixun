/**
 * Project: Callga
 * Create At 2015-1-30.
 * @author hhool
 */
package com.cchat.common.base.data.XmlData;

import android.os.Parcel;
import android.os.Parcelable;

import com.cchat.common.base.data.DataTalk;

public class DataChatMessage implements Parcelable, IToXml {
    public static final Creator<DataChatMessage> CREATOR = new Creator<DataChatMessage>() {
        @Override
        public DataChatMessage createFromParcel(Parcel in) {
            return new DataChatMessage(in);
        }

        @Override
        public DataChatMessage[] newArray(int size) {
            return new DataChatMessage[size];
        }
    };
    private String mType;
    private String mFrom;
    private String mTo;
    private DataTalk mTalk;

    public DataChatMessage(Parcel in) {
        mType = in.readString();
        mFrom = in.readString();
        mTo = in.readString();
        mTalk = in.readParcelable(SAndroidUtil.getClassLoader());
    }

    public DataChatMessage() {

    }

    /**
     * @return the type
     */
    public String getType() {
        return mType;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        mType = type;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return mFrom;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(String from) {
        mFrom = from;
    }

    /**
     * @return the to
     */
    public String getTo() {
        return mTo;
    }

    /**
     * @param to the to to set
     */
    public void setTo(String to) {
        mTo = to;
    }

    /**
     * @return the talk
     */
    public DataTalk getTalk() {
        return mTalk;
    }

    /**
     * @param talk the talk to set
     */
    public void setTalk(DataTalk talk) {
        mTalk = talk;
    }

    @Override
    public String toXml(boolean isUseHtmlEncode, boolean isUseUriEncode) {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append("message").append(" ").append("xmls").append("=\"").append("jabber:client").append("\"")
                .append(" ").append("type").append("=\"").append(mType).append("\"")
                .append(" ").append("from").append("=\"").append(mFrom).append("\"")
                .append(" ").append("to").append("=\"").append(mTo).append("\"").append(">")
                .append("<").append("body").append(">")
                .append(mTalk.toXml(isUseHtmlEncode, isUseUriEncode))
                .append("<").append("/body").append(">")
                .append("<").append("/message").append(">");
        return buf.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeString(mFrom);
        dest.writeString(mTo);
        dest.writeParcelable(mTalk, flags);
    }
}
