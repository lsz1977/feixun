package org.webrtc.apprtc.njtest.signal;

import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.apprtc.njtest.signal.model.WSignalCandidate;
import org.webrtc.apprtc.njtest.signal.model.WSignalMessage;
import org.webrtc.apprtc.njtest.signal.model.WSignalSdp;

import java.util.HashMap;

/**
 * User: villain
 * Date: 14-5-20
 * Time: 下午6:55
 */
public class WSignalUtil {
    private static class ArgMap extends HashMap<Object, Object> {
        public ArgMap(Object... keyValues) {
            if (keyValues.length % 2 != 0) {
                throw new RuntimeException("Key and value do not match");
            }
            for (int i = 0; i < keyValues.length; i += 2) {
                put(keyValues[i], keyValues[i + 1]);
            }
        }

        public JSONObject toArg() {
            return new JSONObject(this);
        }

        public JSONArray toArgs() {
            return new JSONArray().put(new JSONObject(this));
        }
    }

    public static JSONArray makeJoinArgs(String room, String deviceId, String serverKey, JSONObject argMap) {
        return new ArgMap("room", room, "key", serverKey, "id", deviceId, "ability", argMap).toArgs();
    }
    
    public static JSONArray makeJoinArgs(String room, String deviceId, String serverKey,String Jid, JSONObject argMap) {
        return new ArgMap("room", room, "key", serverKey, "id", deviceId, "jid",Jid,"ability", argMap).toArgs();
    }

    public static JSONObject makeAbilityArgs(boolean isAudio, boolean isVideo, boolean isData) {
        return new ArgMap("audio", isAudio, "video", isVideo, "data", isData).toArg();
    }

    public static JSONArray makeSignalMessageArgs(WSignalMessage message) {
        JSONObject arg = null;
        if (message.getPayload() instanceof WSignalCandidate) {
            WSignalCandidate candidate = (WSignalCandidate) message.getPayload();
            arg = new ArgMap("candidate", candidate.getCandidate(), "sdpMid", candidate.getSdpMid(), "sdpMLineIndex", candidate.getSdpMLineIndex()).toArg();
        } else if (message.getPayload() instanceof WSignalSdp) {
            WSignalSdp sdp = (WSignalSdp) message.getPayload();
            arg = new ArgMap("type", sdp.getType(), "sdp", sdp.getSdp()).toArg();
        }
        return new ArgMap("to", message.getTo(), "roomType", message.getRoomType(), "prefix", message.getPrefix(), "type", message.getType(), "payload", arg).toArgs();
    }
}
