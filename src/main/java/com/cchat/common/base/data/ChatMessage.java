/**
 * Project: Callga
 * Create At 2015-1-28.
 *
 * @author hhool
 */
package com.cchat.common.base.data;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * <message xmls="jabber:client" type="chat" to="100000" from="101504">
 *   <body>
 *     <talk func="onMsg" timeId="2015-01-29 21:54:51:051">
 *        <nick>hhool</nick>
 *           <head>http://cardxml000001.dorpost.com/UploadServer/upload/heads/101504201501240226080.jpg</head>
 *           <content type="emoji">[猪头]</content>
 *           <attachments></attachments>
 *     </talk>
 *   </body>
 *</message>
 */
public class ChatMessage implements Parcelable, Cloneable, IToXml {
    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }

        @Override
        public ChatMessage createFromParcel(Parcel in) {
            ChatMessage info = new ChatMessage(in);
            return info;
        }
    };
    private String mFrom;
    private String mTo;
    private DataTalk mDataTalk = new DataTalk();
    private CMActionResult mResult = CMActionResult.none;
    private DataCardXmlInfo mFriendCardXmlInfo;
    private int mIsShowTime;
    private String mDisplayTime;

    public int getIsShowTime() {
        return mIsShowTime;
    }

    public void setIsShowTime(int isShowTime) {
        this.mIsShowTime = isShowTime;
    }

    public ChatMessage() {
    }

    public ChatMessage(String from, String to, DataTalk talk) {
        mFrom = from;
        mTo = to;
        mDataTalk = talk;
    }

    public ChatMessage(Parcel in) {
        mFrom = in.readString();
        mTo = in.readString();
        mResult = CMActionResult.valueOf(in.readString());
        mDataTalk = in.readParcelable(SAndroidUtil.getClassLoader());
        mFriendCardXmlInfo = in.readParcelable(SAndroidUtil.getClassLoader());
        mIsShowTime = in.readInt();
        mDisplayTime = in.readString();
    }

    /**
     * @return the mFrom
     */
    public String getFrom() {
        return mFrom;
    }

    /**
     * @param from the mFrom to set
     */
    public void setFrom(String from) {
        this.mFrom = from;
    }

    /**
     * @return the mTo
     */
    public String getTo() {
        return mTo;
    }

    /**
     * @param to the mTo to set
     */
    public void setTo(String to) {
        this.mTo = to;
    }

    /*
     * @param displayTime the mDisplayTime to set
     */
    public void setDisplayTime(String displayTime) {
        mDisplayTime = displayTime;
    }

    /*
     * @return the mDisplayTime;
     */
    public String getDisplayTime() {
        return mDisplayTime;
    }

    /**
     * @return the DataTalk
     */
    public DataTalk getDataTalk() {
        return mDataTalk;
    }

    /**
     * @param talk the DataTalk to set
     */
    public void setDataTalk(DataTalk talk) {
        mDataTalk = talk;
    }

    /**
     * @return the result
     */
    public CMActionResult getResult() {
        return mResult;
    }

    /**
     * @param result the result to set
     */
    public void setResult(CMActionResult result) {
        this.mResult = result;
    }

    public void setFriendCardXml(DataCardXmlInfo cardXmlInfo) {
        if (cardXmlInfo != null) {
            mFriendCardXmlInfo = cardXmlInfo;
        }
    }

    public DataCardXmlInfo getFriendCardXmlInfo() {
        return mFriendCardXmlInfo;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ChatMessage && ((ChatMessage) o).getDataTalk().equals(getDataTalk());
    }

    @Override
    public String toXml(boolean isUseHtmlEncode, boolean isUseUriEncode) {
        return mDataTalk.toXml(isUseHtmlEncode, isUseUriEncode);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("From:" + mFrom);
        builder.append("To:" + mTo);
        builder.append("talk:" + mDataTalk.toXml(true, false));
        return builder.toString();
    }

    public static List<ChatMessage> sort(List<ChatMessage> list) {
        Collections.sort(list, new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage lhs, ChatMessage rhs) {
                long lhsTime = TimeUtils.timeStringToMilli(lhs.getDataTalk().getTimeId());
                long rhsTime = TimeUtils.timeStringToMilli(rhs.getDataTalk().getTimeId());
                return ((lhsTime - rhsTime) > 0) ? 1 : -1;
            }
        });
        return list;
    }

    @Override
    public ChatMessage clone() {
        ChatMessage chatMsg = new ChatMessage();
        chatMsg.mFrom = this.mFrom;
        chatMsg.mTo = this.mTo;
        chatMsg.mDisplayTime = this.mDisplayTime;
        chatMsg.mDataTalk = this.mDataTalk.clone();

        if (mFriendCardXmlInfo != null) {
            chatMsg.mFriendCardXmlInfo = this.mFriendCardXmlInfo.clone();
        }
        if (this.mResult != null) {
            chatMsg.mResult = this.mResult;
        }
        return chatMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFrom);
        dest.writeString(mTo);
        dest.writeString(mResult.toString());
        dest.writeParcelable(mDataTalk, flags);
        dest.writeParcelable(mFriendCardXmlInfo, flags);
        dest.writeInt(mIsShowTime);
        dest.writeString(mDisplayTime);
    }

    public enum CMActionResult {
        none,
        sending,
        ok,
        failed
    }
}