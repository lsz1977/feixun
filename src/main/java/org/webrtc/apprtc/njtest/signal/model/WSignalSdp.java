package org.webrtc.apprtc.njtest.signal.model;

/**
 * User: villain
 * Date: 14-5-20
 * Time: 下午7:37
 */
public class WSignalSdp {
    private String mType;
    private String mSdp;

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }

    public void setSdp(String sdp) {
        mSdp = sdp;
    }

    public String getSdp() {
        return mSdp;
    }
}
