package org.webrtc.apprtc.njtest.signal.model;

/**
 * User: villain
 * Date: 14-5-20
 * Time: 下午7:34
 */
public class WSignalCandidate {
    private String mCandidate;
    private int mSdpMLineIndex;
    private String mSdpMid;

    public void setCandidate(String candidate) {
        mCandidate = candidate;
    }

    public String getCandidate() {
        return mCandidate;
    }

    public void setSdpMLineIndex(int sdpMLineIndex) {
        mSdpMLineIndex = sdpMLineIndex;
    }

    public int getSdpMLineIndex() {
        return mSdpMLineIndex;
    }

    public void setSdpMid(String sdpMid) {
        mSdpMid = sdpMid;
    }

    public String getSdpMid() {
        return mSdpMid;
    }
}
