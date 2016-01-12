package org.webrtc.apprtc.njtest;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.widget.Toast;

import com.cchat.ChatActivity;
import com.cchat.MainActivity;
import com.cchat.TransferStateListener;
import com.cchat.common.base.data.FileResult;
import com.cchat.utils.CommonUtils;
import com.cchat.utils.TLog;

import org.webrtc.Logging;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoRenderer.I420Frame;
import org.webrtc.apprtc.media.IWMediaManagerListener;
import org.webrtc.apprtc.media.IWVRenderListener;
import org.webrtc.apprtc.media.WLocalVideo;
import org.webrtc.apprtc.media.WMediaManager;
import org.webrtc.apprtc.media.WRemoteVideo;
import org.webrtc.apprtc.njtest.signal.IWSignalManagerListener;
import org.webrtc.apprtc.njtest.signal.WSignalManager;
import org.webrtc.apprtc.njtest.signal.model.WSignalAbility;
import org.webrtc.apprtc.njtest.signal.model.WSignalCandidate;
import org.webrtc.apprtc.njtest.signal.model.WSignalSdp;
import org.webrtc.apprtc.peer.IWPeerListener;
import org.webrtc.apprtc.peer.WNewPeer;
import org.webrtc.apprtc.peer.WPeerManager;
import org.webrtc.apprtc.queue.IWQueueManager;
import org.webrtc.apprtc.queue.WLoopQueue;
import org.webrtc.apprtc.queue.WQueueManager;
import org.webrtc.apprtc.queue.WUIQueue;
import org.webrtc.apprtc.util.WUUIDUtil;
import org.webrtc.voiceengine.AudioManagerAndroid;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * User: holand
 * Date: 2015/12/5
 * Time: 13:30
 */
public class TransferManager implements IWSignalManagerListener, IWMediaManagerListener, IWPeerListener,IWVRenderListener {
    private static final String SIGNAL_SERVER = "http://211.154.129.169:3001";
    private ArrayList<PeerConnection.IceServer> ICE_SERVERS = new ArrayList<PeerConnection.IceServer>() {
        {
            add(new PeerConnection.IceServer("turn:211.154.129.169:3478?transport=tcp", "1", "1"));
        }
    };

    private String mRoomId;

    private PeerConnectionFactory mFactory;
    private WPeerManager mPeerManager;
    private WMediaManager mMediaManager;
    private WSignalManager mSignalManager;

    private boolean mSignalOpened;
    private boolean mClosed;

    private IWQueueManager mQueueManager;

    private FileResult mFileResult;

    private Context mContext;
    private DataOutputStream fileOut;

    private boolean mTwoPanel;

    private TransferStateListener mTransferStateListener;

    public TransferManager(Activity activity, String roomId, FileResult fileResult, boolean panel, TransferStateListener transferStateListener) {
        this.mContext = activity;
        this.mRoomId = roomId;
        this.mFileResult = fileResult;
        this.mTwoPanel = panel;
        this.mTransferStateListener = transferStateListener;
        if (!mFileResult.isSender()){
            createFile(mFileResult);
        }
        AudioManager audioManager = ((AudioManager) activity.getSystemService(activity.AUDIO_SERVICE));
        @SuppressWarnings("deprecation")
        boolean isWiredHeadsetOn = audioManager.isWiredHeadsetOn();
        audioManager.setMode(isWiredHeadsetOn ? AudioManager.MODE_IN_CALL : AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(!isWiredHeadsetOn);

        mQueueManager = new WQueueManager(new WUIQueue(), new WLoopQueue(), new WLoopQueue());


        mQueueManager.getRTCQueue().addAction(new Runnable() {
            @Override
            public void run() {
                AudioManagerAndroid.setOpenslesInputNum10MsToBuffer(20);  //20
                AudioManagerAndroid.setOpenslesOutputNum10MsToBuffer(6); //10
                PeerConnectionFactory.initializeAndroidGlobals(mContext, true, true);

                mFactory = new PeerConnectionFactory();
                //hhool this for core logging output
                //Uncomment to get ALL WebRTC tracing and SENSITIVE libjingle logging.
                //NOTE: this _must_ happen while |factory| is alive!
                Logging.enableTracing("logcat:",EnumSet.of(Logging.TraceLevel.TRACE_ALL),Logging.Severity.LS_WARNING);
                ////////////////
                mPeerManager = new WPeerManager(mQueueManager, mFactory);
                mMediaManager = new WMediaManager(mQueueManager, TransferManager.this, mFactory);
                mSignalManager = new WSignalManager(SIGNAL_SERVER, WUUIDUtil.getIdentifyCode("/sdcard/identify"), ("10149520141023161020757"),TransferManager.this);
                mSignalManager.connect(mFileResult.getRoomId(), "b10146e592f34269bbd0011d4279859"); //自动加入房间
                mMediaManager.createLocalAudio("audio", WMediaManager.createAudioSourceConstraints());

            }
        });
    }

    private float mRemoteVolume = 1.0f;

    protected void ScaleRemoteVolume(float scale) {
        int pos = 0;
        int peerCount = mPeerManager.getPeerCount();
        for (; pos < peerCount; pos++) {
            String peerId = mPeerManager.getPeerWithIndex(pos).getId();
            mMediaManager.setRemoteAudioSourceVolumeWithId(peerId, scale);
        }
    }

    @Override
    public void signalManagerJoinSucceed(final HashMap<String, WSignalAbility> clientList, int errorCodeFinal) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                mSignalOpened = true;
//                mBtnJoin.setEnabled(true);

                for (Map.Entry<String, WSignalAbility> entry : clientList.entrySet()) {
                    WNewPeer peer = mPeerManager.addPeer(entry.getKey(), TransferManager.this);
                    peer.setReceiveAudio(false);
                    peer.setReceiveVideo(false);
                    peer.setSendAudio(false);
                    peer.setSendVideo(false);
                    peer.setDataChannel(true);
                    peer.setDataChannelType(WNewPeer.DATA_CHANNEL_TYPE_SCTP);
                }
                //主动发起 主动连接 在otherjoin 、被动连接 here 连接

//        CommonUtils.showToast(mContext,"1->joined");
        /*if (mFileResult.isSender()){

        } else*/ {
            //测试被动
            if (mPeerManager != null && mPeerManager.getPeerCount() > 0) {
                WNewPeer peer = mPeerManager.getPeerWithIndex(0);
//                CommonUtils.showToast(mContext,"1->ed->joined->"+peer.getStatus());
                if (peer.getStatus() == 0) {
                    mPeerManager.createInitiator(peer.getId(), ICE_SERVERS, mMediaManager);
                } /*else {
                    mPeerManager.disconnectPeer(peer.getId());
                    mSignalManager.sendKickOff(peer.getId());
                }*/
            }
        }

//                ((BaseAdapter) mListUser.getAdapter()).notifyDataSetInvalidated();
//            }
//        });
    }

    @Override
    public void signalManagerJoinFailed(Exception ex) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                Toast.makeText(mContext, "login failed", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public void signalManagerClose() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                mSignalOpened = false;
//                mBtnJoin.setEnabled(true);
                mPeerManager.disconnectPeers();
                mPeerManager.removeAllPeers();
                if (mClosed) {
                    mQueueManager.getRTCQueue().addAction(new Runnable() {
                        @Override
                        public void run() {
                            mMediaManager.dispose();
                            mQueueManager.getDisposeQueue().addAction(new Runnable() {
                                @Override
                                public void run() {
                                    mFactory.dispose();
                                    mQueueManager.exit(3000);
                                }
                            });
                        }
                    });
                }
//            }
//        });
    }

    @Override
    public void signalManagerError(Exception ex) {
    }

    @Override
    public void signalManagerReceiveCandidate(String peerId, WSignalCandidate candidate) {
        mPeerManager.addPeerCandidate(peerId, candidate.getCandidate(), candidate.getSdpMLineIndex(), candidate.getSdpMid());
    }

    @Override
    public void signalManagerReceiveOffer(String peerId, WSignalSdp sdp) {
        mPeerManager.createResponder(peerId, ICE_SERVERS, mMediaManager, sdp.getSdp());
    }

    @Override
    public void signalManagerReceiveAnswer(String peerId, WSignalSdp sdp) {
        mPeerManager.setPeerAnswerSdp(peerId, sdp.getSdp());
    }

    @Override
    public void signalManagerReceiveReject(String peerId) {
    }

    @Override
    public void signalManagerReceiveKickOff(String peerId) {
        mPeerManager.disconnectPeer(peerId);
    }

    @Override
    public void signalManagerReceiveOtherJoin(final String peerId, final WSignalAbility ability) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                WNewPeer peer = mPeerManager.addPeer(peerId, TransferManager.this);
                peer.setReceiveAudio(false);
                peer.setReceiveVideo(false);
                peer.setSendAudio(false);
                peer.setSendVideo(false);
                peer.setDataChannel(true);
                peer.setDataChannelType(WNewPeer.DATA_CHANNEL_TYPE_SCTP);

                //主动被动
        if(mTwoPanel)
            CommonUtils.showToast((MainActivity)mContext,"1->otherjoined->"+peer.getStatus());
        else
            CommonUtils.showToast((ChatActivity)mContext,"1->otherjoined->"+peer.getStatus());

//        if (mFileResult.isSender())
        {
            //测试主动
            if (mPeerManager != null && mPeerManager.getPeerCount() > 0) {
//                    WNewPeer peer = mPeerManager.getPeerWithIndex(0);
                if (peer.getStatus() == 0) {
                    mPeerManager.createInitiator(peer.getId(), ICE_SERVERS, mMediaManager);
                } /*else {
                    mPeerManager.disconnectPeer(peer.getId());
                    mSignalManager.sendKickOff(peer.getId());
                }*/
            }
        }
//                ((BaseAdapter) mListUser.getAdapter()).notifyDataSetInvalidated();
//            }
//        });
    }

    @Override
    public void signalManagerReceiveBye(final String peerId) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                mPeerManager.disconnectPeer(peerId);
                mPeerManager.removePeer(peerId);
//                ((BaseAdapter) mListUser.getAdapter()).notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public void mediaManagerCreateLocalVideoFailed() {
    }

    @Override
    public void renderFrame(String id, I420Frame frame) {
        //saveYUVFile(id , frame);
    }

    @Override
    public void setSize(final String id, final int width, final int height) {

    }

    @Override
    public void mediaManagerCreateLocalVideoSucceed(WLocalVideo video) {

    }

    @Override
    public void mediaManagerRenderRemoteVideo(String id, WRemoteVideo video) {

    }

    @Override
    public void peerConnecting(final String peerId) {
    }

    @Override
    public void peerConnected(final String peerId) {
    }

    @Override
    public void peerDisconnected(String peerId) {
        //提示用户当前网络不好,此时peer的auido和video都停止了,
        //此时要做几件事情：
        //1.如果peer的含有video,对渲染区域做一些处理使界面更友好.
        //2.同时开始此状态的起始时间,记录此状态的时长,当时长大于某一默认值30秒或其他数值.主动退出通话。
    }

    @Override
    public void peerSendAnswer(String peerId, String sdp) {
        mSignalManager.sendAnswer(peerId, sdp);
    }

    @Override
    public void peerSendOffer(String peerId, String sdp) {
        mSignalManager.sendOffer(peerId, sdp);
    }

    @Override
    public void peerSendCandidate(String peerId, String sdp, int sdpMLineIndex, String sdpMid) {
        mSignalManager.sendCandidate(peerId, sdp, sdpMLineIndex, sdpMid);
    }

    @Override
    public void peerAddStream(String peerId, MediaStream mediaStream) {
        mMediaManager.addRemoteMediaStream(peerId, mediaStream);
    }

    @Override
    public void peerRemoveStream(String peerId) {
        mMediaManager.removeRemoteMediaStream(peerId);
    }

    @Override
    public void peerDisposing(final String peerId) {
        mQueueManager.getUIQueue().addAction(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void peerDisposed(String peerId, String mediaStreamId) {
        mMediaManager.removeLocalMediaStream(mediaStreamId);
    }

    @Override
    public void peerReceiveMessage(final String id, final String text) {
        mQueueManager.getUIQueue().addAction(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "receive message:" + text + " from:" + id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createFile(FileResult fileResult){
        String savePath = /*CommonUtils.getSDPath() + */fileResult.getFilePath();//CommonUtils.getSDPath()+"/"+CommonUtils.getRandomNumChar(8)+ fileResult.getFileName();//mFileResult.getFilePath();//"/sdcard/2.txt";
        try {
            fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void endToTransfer(){

        if (mPeerManager!=null && mPeerManager.getPeerCount()>0) {
                    WNewPeer peer = mPeerManager.getPeerWithIndex(0);
            if (peer.getStatus() != 0) {
                mPeerManager.disconnectPeer(peer.getId());
                mSignalManager.sendKickOff(peer.getId());
            }
        }

//        finish();
    }
    @Override
    public void peerReceiveBinaryByteArray(String id, final byte[] bytes) {
        mQueueManager.getUIQueue().addAction(new Runnable() {
            @Override
            public void run() {
                try {
//                    String savePath = mFileResult.getFilePath();//"/sdcard/2.txt";
//                    int bufferSize = 16*1000;
//                    DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));

                    fileOut.write(bytes, 0, bytes.length);

//                    fileOut.close();

                } catch (Exception e) {
                    System.out.println("接收消息错误" + "\n");
                    return;
                }
                TLog.analytics(fileOut.size() +"=1="+ mFileResult.getSize());
                if (fileOut.size() == mFileResult.getSize()) {
                    TLog.analytics(fileOut.size() +"=1= recieved finish!"+ mFileResult.getSize());
                    try {
                        if (fileOut!=null)
                            fileOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    endToTransfer();
                    if (mTransferStateListener!=null){
                        mTransferStateListener.finish();
                    }
                }
            }
        });
    }

    @Override
    public void peerDataChannelConnected(String id) {
        System.out.println("peerDataChannelConnected");
        mPeerManager.sendDataToPeer(id, "hello");

        if (mFileResult.isSender()) {
            mPeerManager.sendDataToPeer(id, /*CommonUtils.getSDPath()+"/"+mFileResult.getFileName()*/mFileResult.getFilePath(), true, 16000);
        }
//        else
//            CommonUtils.showToast(mContext, "开始收数据");
    }

    public void finish() {
        mClosed = true;
        if (mSignalOpened) {
            mSignalManager.disconnect();
        } else {
            mQueueManager.getRTCQueue().addAction(new Runnable() {
                @Override
                public void run() {
                    mMediaManager.dispose();
                    mQueueManager.getDisposeQueue().addAction(new Runnable() {
                        @Override
                        public void run() {
                            mFactory.dispose();
                            mQueueManager.exit(3000);
                        }
                    });
                }
            });
        }
    }

}
