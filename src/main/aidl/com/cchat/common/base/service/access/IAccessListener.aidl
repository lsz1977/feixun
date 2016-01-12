// IAccessListener.aidl
package com.cchat.common.base.service.access;

import com.cchat.common.base.service.base.android.ShareDataPack;

oneway interface IAccessListener {
    void onFinish(boolean bSuccess, in ShareDataPack pack);
}