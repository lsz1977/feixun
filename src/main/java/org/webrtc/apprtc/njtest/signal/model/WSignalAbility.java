package org.webrtc.apprtc.njtest.signal.model;

/**
 *   
 *  @author HD 
 *  @version 2014年7月30日 下午5:28:56
 */
public class WSignalAbility {
    private boolean mAudio;
    private boolean mVideo;
    private boolean mData;

    public WSignalAbility(boolean audio, boolean video, boolean data) {
        mAudio = audio;
        mVideo = video;
        mData = data;
    }

    public boolean isAudio() {
        return mAudio;
    }

    public boolean isVideo() {
        return mVideo;
    }

    public boolean isData() {
        return mData;
    }
}
