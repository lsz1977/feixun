package org.webrtc.apprtc.njtest.signal;

import org.webrtc.apprtc.njtest.signal.model.WSignalAbility;
import org.webrtc.apprtc.njtest.signal.model.WSignalCandidate;
import org.webrtc.apprtc.njtest.signal.model.WSignalSdp;

import java.util.HashMap;

/**
 * User: villain
 * Date: 14-5-20
 * Time: 下午6:40
 */
public interface IWSignalManagerListener {
    public void signalManagerJoinSucceed(HashMap<String, WSignalAbility> mClientList, int errorCodeFinal);

    public void signalManagerJoinFailed(Exception ex);

    public void signalManagerClose();

    public void signalManagerError(Exception ex);

    public void signalManagerReceiveCandidate(String peerId, WSignalCandidate candidate);

    public void signalManagerReceiveOffer(String peerId, WSignalSdp sdp);

    public void signalManagerReceiveAnswer(String peerId, WSignalSdp sdp);

    public void signalManagerReceiveReject(String peerId);

    public void signalManagerReceiveKickOff(String peerId);

    public void signalManagerReceiveOtherJoin(String peerId, WSignalAbility ability);

    public void signalManagerReceiveBye(String peerId);
}
