package com.cchat.stub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.cchat.common.base.data.ChatMessage;
import com.cchat.common.base.service.access.IAccessListener;
import com.cchat.service.IConnectService;
import com.cchat.service.IConnectServiceCallback;
import com.cchat.service.IServiceBindedListener;

public abstract class ConnectStubImp extends IConnectServiceCallback.Stub {
	private IConnectService mService;
	private Activity mActivity;
	
	public ConnectStubImp(Activity activity) {
		super();
		mActivity = activity;
	}

	/*此处 添加实现的空方法 子类中便 不会强制实现*/
	/*@Override
	public double getWeight() throws RemoteException {
		return 0;
	}

	@Override
	public void getCount(final String count) throws RemoteException {
	}
	*/
	
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IConnectService.Stub.asInterface(service);
			try {
				mService.register(ConnectStubImp.this);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			if (mServiceBindedListener != null) {
				mServiceBindedListener.bindedSuccess();
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
	};
	
	public void bind() {
		Intent intent = new Intent(IConnectService.class.getName());
		mActivity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
		
	}
	
	public void unbind() {
		if (mService != null) {
			try {
				mService.unRegister(ConnectStubImp.this);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		mActivity.unbindService(conn);
	}
	
	public void login(String user, String password, boolean autoLogin, final IAccessListener listener) throws RemoteException {
		if (mService != null) {
			mService.login(user, password, autoLogin, listener);
		}
		
	}

	public void register(String user, String password, final IAccessListener listener) throws RemoteException {
		if (mService != null) {
			mService.userRegister(user, password, listener);
		}
	}

	public void resetPasswd(String user, String password, final IAccessListener listener) throws RemoteException {
		if (mService != null) {
			mService.resetPasswd(password, listener);
		}

	}

	public void logout(final IAccessListener listener) throws RemoteException {
		if (mService != null) {
			mService.logout(listener);
		}
		
	}

	public void sendMessageObj(final ChatMessage chatMessage) throws RemoteException {
		if (mService != null) {
			mService.sendMessageObj(chatMessage);
		}

	}
	
	public void getFriends(final IAccessListener listener) throws RemoteException {
		if (mService != null) {
			mService.getFriensList(listener);
		}
		
	}
	
	public void sendMessage(String to, String textMessage) throws RemoteException {
		if (mService != null) {
			mService.sendMessage(to, textMessage);
		}
		
	}

	public void agreeTo(String jid) throws RemoteException {
		if (mService != null) {
			mService.agreeAdd(jid);
		}

	}

	public void reqAddTo(String jid) throws RemoteException {
		if (mService != null) {
			mService.reqAdd(jid);
		}

	}

	private IServiceBindedListener mServiceBindedListener;

    public void setServiceBindedListener(IServiceBindedListener serviceBindedListener){
        this.mServiceBindedListener = serviceBindedListener;
    }
}
