package org.webrtc.apprtc.njtest.signal;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.apprtc.njtest.signal.model.WSignalAbility;
import org.webrtc.apprtc.njtest.signal.model.WSignalCandidate;
import org.webrtc.apprtc.njtest.signal.model.WSignalMessage;
import org.webrtc.apprtc.njtest.signal.model.WSignalSdp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * User: villain
 * Date: 14-5-20
 * Time: 下午6:38
 */
public class WSignalManager implements DisconnectCallback, ErrorCallback {
    private static final int CONNECT_STATUS_DISCONNECTED = 0;
    private static final int CONNECT_STATUS_CONNECTING = 1;
    private static final int CONNECT_STATUS_CONNECTED = 2;
    private static final int CONNECT_STATUS_DISCONNECTING = 3;

    private String mServer;
    private String mDeviceId;
    private String mJid;
    private IWSignalManagerListener mListener;
    private String mRoom;
    private int mConnectStatus;
    private HashMap<String, WSignalAbility> mClientList;
    private SocketIOClient mSocketIOClient;
    private String mServerKey;

    public WSignalManager(String server, String deviceId,String jid, IWSignalManagerListener listener) {
        mServer = server;
        mDeviceId = deviceId;
        mJid = jid;
        mListener = listener;
    }

    public void connect(String room, String serverKey) {
        if (mConnectStatus == CONNECT_STATUS_DISCONNECTED) {
            mConnectStatus = CONNECT_STATUS_CONNECTING;
            mRoom = room;
            mServerKey = serverKey;
            //WebRTCLog.i("connect signal server with address " + mServer);
            SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), mServer, new ConnectCallback() {
                @Override
                public void onConnectCompleted(Exception ex, SocketIOClient client) {
                    if (mConnectStatus != CONNECT_STATUS_CONNECTING) {
                        return;
                    }
                    if (ex == null) {
                        onConnectSucceed(client);
                    } else {
                        onConnectFailed(ex);
                    }
                }
            });
        }
    }

    public void disconnect() {
        if (mConnectStatus == CONNECT_STATUS_CONNECTING || mConnectStatus == CONNECT_STATUS_CONNECTED) {
            mConnectStatus = CONNECT_STATUS_DISCONNECTING;
            if (mSocketIOClient == null) {
                onDisconnect(null);
            } else {
                mSocketIOClient.disconnect();
            }
        }
    }

    public void sendOffer(String peerId, String sdp) {
        WSignalSdp signalSdp = new WSignalSdp();
        signalSdp.setType("offer");
        signalSdp.setSdp(sdp);

        WSignalMessage message = new WSignalMessage();
        message.setTo(peerId);
        message.setPayload(signalSdp);

        sendMessage(message);
    }

    public void sendAnswer(String peerId, String sdp) {
        WSignalSdp signalSdp = new WSignalSdp();
        signalSdp.setType("answer");
        signalSdp.setSdp(sdp);

        WSignalMessage message = new WSignalMessage();
        message.setTo(peerId);
        message.setPayload(signalSdp);

        sendMessage(message);
    }

    public void sendCandidate(String peerId, String sdp, int sdpMLineIndex, String sdpMid) {
        WSignalCandidate signalCandidate = new WSignalCandidate();
        signalCandidate.setSdpMLineIndex(sdpMLineIndex);
        signalCandidate.setSdpMid(sdpMid);
        signalCandidate.setCandidate(sdp);

        WSignalMessage message = new WSignalMessage();
        message.setTo(peerId);
        message.setPayload(signalCandidate);

        sendMessage(message);
    }

    public void sendKickOff(String peerId) {
        WSignalMessage message = new WSignalMessage();
        message.setTo(peerId);
        message.setType("kickoff");

        sendMessage(message);
    }

    public void sendMessage(final WSignalMessage msg) {
        msg.setRoomType("video");
        msg.setPrefix("webkit");
        if (msg.getPayload() instanceof WSignalCandidate) {
            msg.setType("candidate");
        } else if (msg.getPayload() instanceof WSignalSdp) {
            msg.setType(((WSignalSdp) msg.getPayload()).getType());
        }
        mSocketIOClient.emit("message", WSignalUtil.makeSignalMessageArgs(msg));
    }

    @Override
    public void onDisconnect(Exception e) {
        mConnectStatus = CONNECT_STATUS_DISCONNECTED;
        mListener.signalManagerClose();
    }

    @Override
    public void onError(final String error) {
        onSignalError(new Exception(error));
    }

    protected void onConnectSucceed(SocketIOClient client) {
        mConnectStatus = CONNECT_STATUS_CONNECTED;
        mSocketIOClient = client;
        mSocketIOClient.setDisconnectCallback(this);
        mSocketIOClient.setErrorCallback(this);
        mSocketIOClient.addListener("message", new EventCallback() {
            @Override
            public void onEvent(final JSONArray argument, final Acknowledge acknowledge) {
                try {
                    onMessage(argument);
                } catch (final Exception e) {
                    onSignalError(e);
                }
            }
        });
        mSocketIOClient.addListener("remove", new EventCallback() {
            @Override
            public void onEvent(final JSONArray argument, final Acknowledge acknowledge) {
                try {
                    onRemove(argument);
                } catch (final Exception e) {
                    onSignalError(e);
                }
            }
        });//WebRTCLog.i("join room " + mRoom + " with id " + mDeviceId);

        boolean abilityConfig = false; //client config of ability
        mSocketIOClient.emit("join", WSignalUtil.makeJoinArgs(mRoom, mDeviceId, mServerKey,mJid, abilityConfig ? WSignalUtil.makeAbilityArgs(false, false, true) : null), new Acknowledge() {
            @Override
            public void acknowledge(JSONArray arguments) {
                onJoinSucceed(arguments);
            }
        });
    }

    protected void onConnectFailed(final Exception ex) {
        mConnectStatus = CONNECT_STATUS_DISCONNECTED;
        mListener.signalManagerJoinFailed(ex);
    }

    protected void onJoinSucceed(JSONArray args) {
        try {
            mClientList = new HashMap<String, WSignalAbility>();
            int result = 0;
            for (int i = 0; i < args.length(); i++) {
                JSONObject jsonObject = args.getJSONObject(i);
                Iterator it = jsonObject.keys();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    if (key.equals("clients")) {
                        JSONObject jsonUsersObject = jsonObject.getJSONObject("clients");
                        Iterator keyIterator = jsonUsersObject.keys();
                        while (keyIterator.hasNext()) {
                            String id = (String) keyIterator.next();
                            JSONObject jsonoObject = (JSONObject) jsonUsersObject.get(id);
                            WSignalAbility ability = new WSignalAbility(jsonoObject.getBoolean("audio"),
                                    jsonoObject.getBoolean("video"), jsonoObject.getBoolean("data"));
                            if (!id.equals(mDeviceId)) {
                                mClientList.put(id, ability);
                            }
                        }
                    } else if (key.equals("result")) {
                        result = jsonObject.getInt("result");
                    }
                }
            }
            final int errorCodeFinal = result;
            mListener.signalManagerJoinSucceed(mClientList, errorCodeFinal);
        } catch (final Exception ex) {
            mListener.signalManagerJoinFailed(ex);
        }
    }

    protected void onMessage(JSONArray args) throws Exception {
        JSONObject json = args.getJSONObject(0);
        String type = json.getString("type");
        final String from = json.getString("from");
        final JSONObject payload = json.optJSONObject("payload");

        if ("candidate".equals(type)) {
            final WSignalCandidate candidate = new WSignalCandidate();
            candidate.setCandidate(payload.getString("candidate"));
            candidate.setSdpMLineIndex(payload.getInt("sdpMLineIndex"));
            candidate.setSdpMid(payload.getString("sdpMid"));
            mListener.signalManagerReceiveCandidate(from, candidate);
        } else if ("offer".equals(type)) {
            final WSignalSdp sdp = new WSignalSdp();
            sdp.setType("offer");
            sdp.setSdp(payload.getString("sdp"));
            mListener.signalManagerReceiveOffer(from, sdp);
        } else if ("answer".equals(type)) {
            final WSignalSdp sdp = new WSignalSdp();
            sdp.setType("answer");
            sdp.setSdp(payload.getString("sdp"));
            mListener.signalManagerReceiveAnswer(from, sdp);
        } else if ("reject".equals(type)) {
            mListener.signalManagerReceiveReject(from);
        } else if ("kickoff".equals(type)) {
            mListener.signalManagerReceiveKickOff(from);
        } else if ("other_join".equals(type)) {
            Iterator keyIterator = payload.keys();
            final String peerId = (String) keyIterator.next();
            JSONObject jsonoObject = (JSONObject) payload.get(peerId);
            final WSignalAbility ability = new WSignalAbility(
                    jsonoObject.getBoolean("audio"),
                    jsonoObject.getBoolean("video"),
                    jsonoObject.getBoolean("data"));
            mClientList.put(peerId, ability);
            mListener.signalManagerReceiveOtherJoin(peerId, ability);
        }
    }


    protected void onRemove(JSONArray args) throws Exception {
        final List<String> byeClients = new LinkedList<String>();
        for (int i = 0, count = args.length(); i < count; ++i) {
            JSONObject client = args.getJSONObject(i);
            String id = client.getString("id");
            mClientList.remove(id);
            byeClients.add(id);
        }
        for (String byeClient : byeClients) {
            mListener.signalManagerReceiveBye(byeClient);
        }
    }

    protected void onSignalError(Exception e) {
        mListener.signalManagerError(e);
    }
}
