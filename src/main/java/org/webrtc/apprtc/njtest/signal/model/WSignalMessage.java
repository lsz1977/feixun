package org.webrtc.apprtc.njtest.signal.model;

/**
 * User: villain
 * Date: 14-5-20
 * Time: 下午7:55
 */
public class WSignalMessage {
    private String mTo;
    private String mRoomType;
    private String mPrefix;
    private String mType;
    private Object mPayload;

    public void setTo(String to) {
        mTo = to;
    }

    public String getTo() {
        return mTo;
    }

    public void setRoomType(String roomType) {
        mRoomType = roomType;
    }

    public String getRoomType() {
        return mRoomType;
    }

    public void setPrefix(String prefix) {
        mPrefix = prefix;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }

    public Object getPayload() {
        return mPayload;
    }

    public void setPayload(Object payload) {
        mPayload = payload;
    }
}
