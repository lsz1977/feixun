package org.webrtc.apprtc.njtest;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;

import com.cchat.R;

import org.webrtc.Logging;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoRenderer.I420Frame;
import org.webrtc.apprtc.media.*;
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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
/**
 * User: SEAMAN
 * Date: 2015/4/6
 * Time: 13:30
 */
public class MyActivity extends Activity implements IWSignalManagerListener, IWMediaManagerListener, IWPeerListener,IWVRenderListener {
    //private static final String SIGNAL_SERVER = "http://211.154.129.169:5001";
    private static final String SIGNAL_SERVER = "http://211.154.129.169:3001";
    //private static final String SIGNAL_SERVER = "http://192.168.20.114:4001";
    private ArrayList<PeerConnection.IceServer> ICE_SERVERS = new ArrayList<PeerConnection.IceServer>() {
        {
            add(new PeerConnection.IceServer("turn:211.154.129.169:3478?transport=tcp", "1", "1"));
        }
    };

    private ArrayList<WVideoDevice> mDeviceList;
    private WVideoDevice mCurCaptureDevice;
//    private ListView mListDevice;
//    private ListView mListUser;
//    private EditText mEditRoom;
    private String mRoomId = "12345";
    private Button mBtnJoin;
//    private Button mBtnEnableAudio;
//    private Button mBtnEnableVideo;
//    private WVideoLayout mVideoLayout;

    private PeerConnectionFactory mFactory;
    private WPeerManager mPeerManager;
    private WMediaManager mMediaManager;
    private WSignalManager mSignalManager;

    private boolean mSignalOpened;
    private boolean mClosed;

    private IWQueueManager mQueueManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /////////////
        //hhool must add
        AudioManager audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
        // TODO(fischman): figure out how to do this Right(tm) and remove the suppression.
        @SuppressWarnings("deprecation")
        boolean isWiredHeadsetOn = audioManager.isWiredHeadsetOn();
        audioManager.setMode(isWiredHeadsetOn ? AudioManager.MODE_IN_CALL : AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(!isWiredHeadsetOn);
        /////////////////

        mQueueManager = new WQueueManager(new WUIQueue(), new WLoopQueue(), new WLoopQueue());

//        mListDevice = (ListView) findViewById(R.id.list_device);
//        mListUser = (ListView) findViewById(R.id.list_user);
//        mEditRoom = (EditText) findViewById(R.id.edit_room);
        mBtnJoin = (Button) findViewById(R.id.btn_join);
//        mBtnEnableAudio = (Button) findViewById(R.id.btn_enable_audio);
//        mBtnEnableVideo = (Button) findViewById(R.id.btn_enable_video);
//        mVideoLayout = (WVideoLayout) findViewById(R.id.lay_videos);

//        mListDevice.setAdapter(new WDeviceListAdapter());
//        mListUser.setAdapter(new WUserListAdapter());

        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPeerManager!=null && mPeerManager.getPeerCount()>0) {
                    WNewPeer peer = mPeerManager.getPeerWithIndex(0);
                    if (peer.getStatus() == 0) {
                        mPeerManager.createInitiator(peer.getId(), ICE_SERVERS, mMediaManager);
                    } else {
                        mPeerManager.disconnectPeer(peer.getId());
                        mSignalManager.sendKickOff(peer.getId());
                    }
                }

            }
        });
        /*mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSignalOpened) {
                    mBtnJoin.setEnabled(false);
                    mBtnJoin.setText("退出");
                    if (!mMediaManager.hasLocalVideo()) {
                        WVideoDevice device = null;
                        for (WVideoDevice d : mDeviceList) {
                            if (d.getFacing().equals(WVideoDevice.DEVICE_FACING_FRONT)) {
                                device = d;
                                break;
                            }
                        }
                        if (device == null && mDeviceList.size() > 0) {
                            device = mDeviceList.get(0);
                        }
                        if (device != null) {
                            mCurCaptureDevice = device;
                            mMediaManager.createLocalVideo(device, "video", WMediaManager.createVideoSourceConstraints(240, 180, 30));
                        }
                    }
                    mSignalManager.connect(mRoomId, "b10146e592f34269bbd0011d4279859");
                } else {
                    mBtnJoin.setEnabled(false);
                    mBtnJoin.setText("加入");
                    mSignalManager.disconnect();
                }
            }
        });*/

       /* mBtnEnableAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaManager.setLocalAudioEnabled(!mMediaManager.isLocalAudioEnabled());
                mBtnEnableAudio.setText(mMediaManager.isLocalAudioEnabled() ? "禁用音频" : "开启音频");
            }
        });
        mBtnEnableVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaManager.setLocalVideoEnabled(!mMediaManager.isLocalVideoEnabled());
                mBtnEnableVideo.setText(mMediaManager.isLocalVideoEnabled() ? "禁用视频" : "开启视频");
            }
        });*/

        mQueueManager.getRTCQueue().addAction(new Runnable() {
            @Override
            public void run() {
                AudioManagerAndroid.setOpenslesInputNum10MsToBuffer(20);  //20
                AudioManagerAndroid.setOpenslesOutputNum10MsToBuffer(6); //10
                PeerConnectionFactory.initializeAndroidGlobals(MyActivity.this, true, true);

                mFactory = new PeerConnectionFactory();
                //hhool this for core logging output
                //Uncomment to get ALL WebRTC tracing and SENSITIVE libjingle logging.
                //NOTE: this _must_ happen while |factory| is alive!
                Logging.enableTracing("logcat:",EnumSet.of(Logging.TraceLevel.TRACE_ALL),Logging.Severity.LS_WARNING);
                ////////////////
                mPeerManager = new WPeerManager(mQueueManager, mFactory);
                mMediaManager = new WMediaManager(mQueueManager, MyActivity.this, mFactory);
                mSignalManager = new WSignalManager(SIGNAL_SERVER, WUUIDUtil.getIdentifyCode("/sdcard/identify"), ("10149520141023161020757"),MyActivity.this);
                mSignalManager.connect(mRoomId, "b10146e592f34269bbd0011d4279859"); //自动加入房间
                mMediaManager.createLocalAudio("audio", WMediaManager.createAudioSourceConstraints());

                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceList = WVideoDevice.getDeviceList();
                        ((BaseAdapter) mListDevice.getAdapter()).notifyDataSetInvalidated();
                    }
                });*/
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

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mRemoteVolume >= 2.0f)
                    mRemoteVolume -= 1.0f;
                else if (mRemoteVolume >= 1.0f)
                    mRemoteVolume -= 0.1f;
                else if (mRemoteVolume < 1.0f)
                    mRemoteVolume -= 0.1f;

                if (mRemoteVolume < 0.0f)
                    mRemoteVolume = 0.0f;

                ScaleRemoteVolume(mRemoteVolume);
            case KeyEvent.KEYCODE_VOLUME_UP:

                if (mRemoteVolume >= 0.0f && mRemoteVolume < 1.0f)
                    mRemoteVolume += 0.1f;
                else if (mRemoteVolume >= 1.0f)
                    mRemoteVolume += 1.0f;

                if (mRemoteVolume >= 10.0f)
                    mRemoteVolume = 10.0f;

                ScaleRemoteVolume(mRemoteVolume);
        }

        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public void signalManagerJoinSucceed(final HashMap<String, WSignalAbility> clientList, int errorCodeFinal) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSignalOpened = true;
//                mBtnJoin.setEnabled(true);

                for (Map.Entry<String, WSignalAbility> entry : clientList.entrySet()) {
                    WNewPeer peer = mPeerManager.addPeer(entry.getKey(), MyActivity.this);
                    peer.setReceiveAudio(false);
                    peer.setReceiveVideo(false);
                    peer.setSendAudio(false);
                    peer.setSendVideo(false);
                    peer.setDataChannel(true);
                    peer.setDataChannelType(WNewPeer.DATA_CHANNEL_TYPE_SCTP);
                }
                //主动发起 主动连接 在otherjoin 、被动连接 here 连接

                //测试被动
                if (mPeerManager!=null && mPeerManager.getPeerCount()>0) {
                    WNewPeer peer = mPeerManager.getPeerWithIndex(0);
                    if (peer.getStatus() == 0) {
                        mPeerManager.createInitiator(peer.getId(), ICE_SERVERS, mMediaManager);
                    } else {
                        mPeerManager.disconnectPeer(peer.getId());
                        mSignalManager.sendKickOff(peer.getId());
                    }
                }

//                ((BaseAdapter) mListUser.getAdapter()).notifyDataSetInvalidated();
            }
        });
    }

    @Override
    public void signalManagerJoinFailed(Exception ex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void signalManagerClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
            }
        });
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WNewPeer peer = mPeerManager.addPeer(peerId, MyActivity.this);
                peer.setReceiveAudio(false);
                peer.setReceiveVideo(false);
                peer.setSendAudio(false);
                peer.setSendVideo(false);
                peer.setDataChannel(true);
                peer.setDataChannelType(WNewPeer.DATA_CHANNEL_TYPE_SCTP);

                //主动被动
                //测试主动
                if (mPeerManager!=null && mPeerManager.getPeerCount()>0) {
//                    WNewPeer peer = mPeerManager.getPeerWithIndex(0);
                    if (peer.getStatus() == 0) {
                        mPeerManager.createInitiator(peer.getId(), ICE_SERVERS, mMediaManager);
                    } else {
                        mPeerManager.disconnectPeer(peer.getId());
                        mSignalManager.sendKickOff(peer.getId());
                    }
                }
//                ((BaseAdapter) mListUser.getAdapter()).notifyDataSetInvalidated();
            }
        });
    }

    @Override
    public void signalManagerReceiveBye(final String peerId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPeerManager.disconnectPeer(peerId);
                mPeerManager.removePeer(peerId);
//                ((BaseAdapter) mListUser.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    @Override
    public void mediaManagerCreateLocalVideoFailed() {
    }

//    private void saveYUVFile(String identify, VideoRenderer.I420Frame frame) {
//    	if(frame.width == 180 && frame.height == 240) {
//			String path = null;
//			if (Environment.getExternalStorageState().equals(
//                    Environment.MEDIA_MOUNTED)) {
//				path = Environment.getExternalStorageDirectory()
//				 .getAbsolutePath()
//				 +"/" + "180x240_" + identify.substring(0,5) + ".yuv";
//
//				File file = new File(path);
//				if(!file.exists()) {
//	    			try {
//	    				file.createNewFile();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//	    		}
//
//				try {
//					FileChannel fops = new FileOutputStream(file,true).getChannel();
//					fops.write(frame.yuvPlanes[0]);
//					fops.write(frame.yuvPlanes[1]);
//					fops.write(frame.yuvPlanes[2]);
//					fops.close();
//				} catch (FileNotFoundException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			} else {
//
//			}
//    	}
//    }

    @Override
    public void renderFrame(String id, I420Frame frame) {
        //saveYUVFile(id , frame);
    }

    @Override
    public void setSize(final String id, final int width, final int height) {
       /* mQueueManager.getUIQueue().addAction(new Runnable() {
            @Override
            public void run() {
                if ("local".equals(id)) {
                    WVideoView videoView = mVideoLayout.getVideoView(id);
                    if (videoView != null) {
                        LayoutParams params = new LayoutParams(mVideoLayout.dip2px(mVideoLayout.getContext(), width / 2),
                                mVideoLayout.dip2px(mVideoLayout.getContext(), height / 2));
                        videoView.setLayoutParams(params);
                    }
                } else {
                    WVideoView videoView = mVideoLayout.getVideoView(id);
                    if (videoView != null) {
                        LayoutParams params = new LayoutParams(mVideoLayout.dip2px(mVideoLayout.getContext(), width / 2),
                                mVideoLayout.dip2px(mVideoLayout.getContext(), height / 2));
                        videoView.setLayoutParams(params);
                    }
                }
            }
        });*/
    }

    @Override
    public void mediaManagerCreateLocalVideoSucceed(WLocalVideo video) {
        /*boolean bMirror = true;
        if(mCurCaptureDevice.getFacing().equals(WVideoDevice.DEVICE_FACING_BACK)) {
            bMirror = false;
        }
        WVideoView videoView = mVideoLayout.addVideoView("local",bMirror);
        videoView.AddRenderCallback(this);
        video.beginRender(videoView);*/
    }

    @Override
    public void mediaManagerRenderRemoteVideo(String id, WRemoteVideo video) {
       /* WVideoView videoView = mVideoLayout.addVideoView(id,false);
        videoView.AddRenderCallback(this);
        video.beginRender(videoView);*/
    }

    @Override
    public void peerConnecting(final String peerId) {
//        ((BaseAdapter) mListUser.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void peerConnected(final String peerId) {
//        ((BaseAdapter) mListUser.getAdapter()).notifyDataSetChanged();
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
//                ((BaseAdapter) mListUser.getAdapter()).notifyDataSetChanged();
//                mVideoLayout.removeVideoView(peerId);
            }
        });
//        mMediaManager.removeRemoteMediaStream(peerId);
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
                Toast.makeText(MyActivity.this, "receive message:" + text + " from:" + id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void peerReceiveBinaryByteArray(String id, final byte[] bytes) {
        mQueueManager.getUIQueue().addAction(new Runnable() {
            @Override
            public void run() {
                try {
                    String savePath = "/sdcard/2.txt";
                    int bufferSize = 16*1000;
                    DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));

                    fileOut.write(bytes, 0, bytes.length);

                    fileOut.close();
                } catch (Exception e) {
                    System.out.println("接收消息错误" + "\n");
                    return;
                }
            }
        });
    }

    @Override
    public void peerDataChannelConnected(String id) {
        System.out.println("peerDataChannelConnected");
        mPeerManager.sendDataToPeer(id, "hello");
        mPeerManager.sendDataToPeer(id,"/sdcard/1.txt", true, 16000);
    }

    @Override
    public void finish() {
        super.finish();
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaManager != null) {
            mMediaManager.stopLocalVideo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaManager != null) {
            mMediaManager.restartLocalVideo();
        }
    }

    /*private class WUserListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mPeerManager == null) {
                return 0;
            } else {
                return mPeerManager.getPeerCount();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final WNewPeer peer = mPeerManager.getPeerWithIndex(position);
            TextView textId;
            CheckBox checkReceiveAudio;
            CheckBox checkReceiveVideo;
            CheckBox checkSendAudio;
            CheckBox checkSendVideo;
            TextView textStatus;
            Button btnConnect;
            Button btnToggleLocalAudio;
            Button btnToggleLocalVideo;
            Button btnToggleRemoteAudio;
            Button btnToggleRemoteVideo;
            if (convertView == null) {
                convertView = View.inflate(MyActivity.this, R.layout.item_user, null);
            }
            textId = (TextView) convertView.findViewById(R.id.text_id);
            checkReceiveAudio = (CheckBox) convertView.findViewById(R.id.check_receive_audio);
            checkReceiveVideo = (CheckBox) convertView.findViewById(R.id.check_receive_video);
            checkSendAudio = (CheckBox) convertView.findViewById(R.id.check_send_audio);
            checkSendVideo = (CheckBox) convertView.findViewById(R.id.check_send_video);
            textStatus = (TextView) convertView.findViewById(R.id.text_status);
            btnConnect = (Button) convertView.findViewById(R.id.btn_connect);
            btnToggleLocalAudio = (Button) convertView.findViewById(R.id.btn_toggle_local_audio);
            btnToggleLocalVideo = (Button) convertView.findViewById(R.id.btn_toggle_local_video);
            btnToggleRemoteAudio = (Button) convertView.findViewById(R.id.btn_toggle_remote_audio);
            btnToggleRemoteVideo = (Button) convertView.findViewById(R.id.btn_toggle_remote_video);

            textId.setText(peer.getId());
            checkReceiveAudio.setChecked(peer.isReceiveAudio());
            checkReceiveVideo.setChecked(peer.isReceiveVideo());
            checkSendAudio.setChecked(peer.isSendAudio());
            checkSendVideo.setChecked(peer.isSendVideo());
            textStatus.setText(peer.getStatus() == 0 ? "未连接" : (peer.getStatus() == 1 ? "正在连接" : "已连接"));
            btnConnect.setText(peer.getStatus() == 0 ? "开始连接" : "结束连接");

            checkReceiveAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    peer.setReceiveAudio(isChecked);
                }
            });
            checkReceiveVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    peer.setReceiveVideo(isChecked);
                }
            });
            checkSendAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    peer.setSendAudio(isChecked);
                }
            });
            checkSendVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    peer.setSendVideo(isChecked);
                }
            });
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (peer.getStatus() == 0) {
                        mPeerManager.createInitiator(peer.getId(), ICE_SERVERS, mMediaManager);
                    } else {
                        mPeerManager.disconnectPeer(peer.getId());
                        mSignalManager.sendKickOff(peer.getId());
                    }
                }
            });
            btnToggleLocalAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mediaStreamId = peer.getMediaStreamId();
                    mMediaManager.setLocalAudioEnabledWithId(mediaStreamId, !mMediaManager.isLocalAudioEnabledWithId(mediaStreamId));
                }
            });
            btnToggleLocalVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mediaStreamId = peer.getMediaStreamId();
                    mMediaManager.setLocalVideoEnabledWithId(mediaStreamId, !mMediaManager.isLocalVideoEnabledWithId(mediaStreamId));
                }
            });
            btnToggleRemoteAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mediaStreamId = peer.getMediaStreamId();
                    mMediaManager.setRemoteAudioEnabled(mediaStreamId, !mMediaManager.isRemoteAudioEnabled(mediaStreamId));
                }
            });
            btnToggleRemoteVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mediaStreamId = peer.getMediaStreamId();
                    mMediaManager.setRemoteVideoEnabled(mediaStreamId, !mMediaManager.isRemoteVideoEnabled(mediaStreamId));
                }
            });
            return convertView;
        }
    }

    private class WDeviceListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mDeviceList == null) {
                return 0;
            } else {
                return mDeviceList.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView != null) {
                textView = (TextView) convertView;
            } else {
                textView = new TextView(MyActivity.this);
                textView.setPadding(20, 20, 20, 20);
            }
            textView.setText(mDeviceList.get(position).getName());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPeerManager.removeStream(false, true);
                    mMediaManager.destroyStream(false, true);
                    mCurCaptureDevice = mDeviceList.get(position);
                    mMediaManager.createLocalVideo(mCurCaptureDevice, "video", WMediaManager.createVideoSourceConstraints(240, 180, 30));
                    mPeerManager.addStream(false, true, mMediaManager);
                }
            });
            return textView;
        }
    }*/
}
