package com.cchat.common.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.cchat.ChatMsgEntity;
import com.cchat.ContactActivity;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.common.base.data.DChatMessageProtocol;
import com.cchat.common.base.data.DSystemTimeProtocol;
import com.cchat.common.base.dialog.CWaitDialog;
import com.cchat.service.ConnectionService;
import com.cchat.service.Person;
import com.cchat.service.TextMessage;
import com.cchat.stub.ConnectStubImp;
import com.cchat.utils.CommonUtils;

import java.util.List;

public abstract class BaseActivity extends FragmentActivity {

    public static final String Tag = "BaseActivity";
    private CWaitDialog mWaitDialog;
    private boolean mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected ConnectStubImp mConnectStubImp = new ConnectStubImp(this) {

        @Override
        public void receive(final ChatMessage textMessage) throws RemoteException {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    CommonUtils.showToast(BaseActivity.this, Tag + ":" + textMessage.getText());
                    recieveMessage(textMessage);
                }
            });

        }

        @Override
        public void loginState(final boolean isState) throws RemoteException {
            BaseActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //Toast.makeText(LoginActivity.this, isState+"", Toast.LENGTH_SHORT).show();
                    if (isState) {
                        Intent mIntent = new Intent(BaseActivity.this, ConnectionService.class);
                        startService(mIntent);

//                        insHandler.sendEmptyMessage(0); // 将用户名保存
//                        DemoApplication.UserName = mUsername;
                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(BaseActivity.this, ContactActivity.class));
                        finish();
                    } else {
//                        insHandler.sendEmptyMessage(1);
                        Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                    }
//                    DialogMaker.dismissProgressDialog();
                }
            });
        }

        @Override
        public void rosterStateChange(final Person person, final int type) throws RemoteException {
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonUtils.showToast(BaseActivity.this, Tag + " rosterState:" + person.getName() + " type:"+type);

                }
            });
        }

        @Override
        public void passPersons(final List<Person> persons) throws RemoteException {
//            mContents.addAll(persons);
//            adapter.notifyDataSetChanged();
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuffer flist = new StringBuffer();
                    for (int i = 0; i < persons.size(); i++) {
                        flist.append(persons.get(i).getName());
                    }
                    CommonUtils.showToast(BaseActivity.this, Tag + ":flist->" + flist);

                }
            });

        }
    };

    protected DAccessListener makeAccessListener(IDActionListener listener) {
        return new DAccessListener(this, listener);
    }

    public void doAction(DAction action, final IDActionListener listener) {
        if (listener != null) {
            listener.onStart();
        }
        switch (action.getProtocol()){
            case ActionProtocol.LOGIN: //login action static final
//                mFacadeBinder.getCardAccess().login((String) action.get(0), (String) action.get(1), (Boolean) action.get(2), false, makeAccessListener(TransferStateListener));
                try {
                    mConnectStubImp.login((String) action.get(0), (String) action.get(1), (Boolean) action.get(2), makeAccessListener(listener));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case ActionProtocol.REGISTER: //regist action static final
                try {
                    mConnectStubImp.register((String) action.get(0), (String) action.get(1), makeAccessListener(listener));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case ActionProtocol.GET_FRIENDS_FROM_NET: //get friends from network server action static final
                try {
                    mConnectStubImp.getFriends(makeAccessListener(listener));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case ActionProtocol.CHANGE_PASSWORD:
                try {
                    mConnectStubImp.register((String) action.get(0), (String) action.get(1), makeAccessListener(listener));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case ActionProtocol.LOGOUT:
                try {
                    mConnectStubImp.logout(makeAccessListener(listener));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;


            default:

                break;
        }

    }

    public void doPreAction(DAction action, final ChatMessage chatMessage) {

        switch (action.getProtocol()) {
            case  DChatMessageProtocol.SEND_MESSAGE:
                try {
                    mConnectStubImp.sendMessageObj(chatMessage);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void startLoading() {
        if (!mLoading) {
            if (mWaitDialog == null) {
                mWaitDialog = new CWaitDialog();
                mWaitDialog.show(getSupportFragmentManager(), "wait");
            }
            mLoading = true;
        }
    }

    public void finishLoading() {
        if (mLoading) {
            if (mWaitDialog != null) {
                mWaitDialog.dismiss();
                mWaitDialog = null;
            }
            mLoading = false;
        }
    }

    @Override
    protected void onResume() {
        bindService();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unbindService();
        super.onPause();
    }

    private void bindService() {
        mConnectStubImp.bind();
    }

    private void unbindService() {
        mConnectStubImp.unbind();
    }

    protected abstract void recieveMessage(ChatMessage textMessage);

    public  void sendMessage(String to, String msg){

        try {
            mConnectStubImp.sendMessage(to, msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void postFacadeBinderAction(Runnable action) {
        if (mConnectStubImp != null) {
            action.run();
        } else {
//            mFacadeBinderQueue.add(action);
            startService(new Intent(this, ConnectionService.class));
//            bindService(new Intent(this, ConnectionService.class), mFacadeBinderConnection, BIND_AUTO_CREATE);
        }
    }
    /*protected void doSystemTimeAction(DAction action, final IDActionListener TransferStateListener) throws RemoteException {
        switch (action.getProtocol()) {
            case DSystemTimeProtocol.GET_CUR_STANDARD_TIME:
                mConnectStubImp.getSystemTimeAccess().getCurStandardTime((Integer) action.get(0), makeAccessListener(TransferStateListener));
                break;
        }
    }*/
}
