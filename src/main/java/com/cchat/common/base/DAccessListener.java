package com.cchat.common.base;

import android.os.RemoteException;

import com.cchat.common.base.service.access.IAccessListener;
import com.cchat.common.base.service.base.android.ShareDataPack;

/**
 * User: Villain
 * Date: 2015/3/12
 * Time: 1:26
 */
public class DAccessListener extends IAccessListener.Stub {
    private BaseActivity mActivity;
    private IDActionListener mListener;

    public DAccessListener(BaseActivity activity, IDActionListener listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    public void onFinish(final boolean bSuccess, final ShareDataPack data) throws RemoteException {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFinished(bSuccess, data.getValues());
                }
            }
        });
    }
}
