package com.cchat;

import android.content.Context;

import com.cchat.common.base.data.CLoginData;

import java.util.ArrayList;
import java.util.List;


public class CApplication extends SApplication {
    public static final String TAG = CApplication.class.getSimpleName();
    private static Context mContext;
//    private CDatabase mDB;
    private CLoginData mLoginData;
//    private CSelfData mSelfData;
//    private CContactsListData mContactsListData;
//    private CBlackListData mBlackListData;
//    private CMessageListData mMessageListData;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
//        SLogger.setLevel(SLogger.VERBOSE);
//        new CArgot().argot_init(this);
//        mDB = new CDatabase(this);
//        String card = CSelfCardAccessUtil.getAccount(this);
//        String pass = CSelfCardAccessUtil.getPlainPassword(this);
//        setLoginData(new CLoginData(card, pass));
    }

    public static Context getContext() {
        return mContext;
    }

    public CLoginData getLoginData() {
        return mLoginData;
    }

    //设定用户的登陆用户名和密码,同时对应此用户的数据库
    public synchronized void setLoginData(CLoginData loginData) {
        if (loginData != null && loginData.getCard() != null && loginData.getCard().length() > 0 && loginData.getPassword() != null && loginData.getPassword().length() > 0) {
//            HttpRequestManager.getInstance().setSelfCard(loginData.getCard());
//            HttpRequestManager.getInstance().setPassword(loginData.getPassword());
//            CIpData ipData = CRouteAccessUtil.getRouteIPCache(this, CRouteAccessUtil.ROUTE_TYPE_WEB);
//            if (ipData != null) {
//                HttpRequestManager.getInstance().setWebIP(ipData.getIp());
//            }
            boolean bSameAccount = true;
            if (mLoginData != null && mLoginData.getCard() != null && mLoginData.getPassword() != null) {
                if (!mLoginData.getCard().equals(loginData.getCard())) {
                    bSameAccount = false;
                }
            } else {
                if (mLoginData == null) {
                    bSameAccount = false;
                }
            }
//            if (!bSameAccount && mDB != null) {
//                if (mDB.isOpened()) {
//                    mDB.close();
//                }
//            }
            mLoginData = loginData;
//            mDB.open(mLoginData.getCard() + ".db", CDatabase.DATABASE_VERSION);
        } else {
//            mDB.close();
        }
    }

    public synchronized boolean isLogin() {
        if (mLoginData == null) {
            return false;
        }
        if (mLoginData.getCard() == null || "".equals(mLoginData.getCard())) {
            return false;
        }
        if (mLoginData.getPassword() == null || "".equals(mLoginData.getPassword())) {
            return false;
        }
        return true;
    }

   /* public synchronized CSelfData getSelfData() {
        if (mDB.isOpened()) {
            CDBSelfCardRecord dbRecord = new CDBSelfCardRecord();
            mSelfData = new CSelfData(dbRecord.getSelfDataCardXmlInfo());
        } else {
            SLogger.w(TAG, "mDB is null");
        }
        return mSelfData;
    }*/

    //登陆用户的详细信息
   /* public synchronized void setSelfData(CSelfData selfData) {
        if (mDB.isOpened() && selfData.getSelf() != null) {
            //TODO:hhool there is a bug
            try {
                CDBSelfCardRecord dbRecord = new CDBSelfCardRecord();
                dbRecord.updateCardXmlInfo(selfData.getSelf());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mSelfData = selfData;
    }*/

    //读取用户联系人列表
  /*  public synchronized CContactsListData getContactsListData() {
        mContactsListData = new CContactsListData();
        CDBFriendRecord friendRecord = new CDBFriendRecord();
        List<DataFriendInfo> friendInfoList = friendRecord.getFriendList();
        mContactsListData.setContactsList(new CContactsList());
        mContactsListData.getContactsList().addFriends(friendInfoList);
        return mContactsListData;
    }*/

    //读取黑名单列表
    /*public synchronized CBlackListData getBlackListData() {
        mBlackListData = new CBlackListData();
        CDBFriendRecord dbRecord = new CDBFriendRecord();
        List<DataBlackCardInfo> blackCardInfoList = dbRecord.getBlackList();
        mBlackListData.setBlackList(new CBlackList());
        mBlackListData.getBlackList().getBlacks().addAll(blackCardInfoList);
        return mBlackListData;
    }*/

    //读取用户的消息的Shortcut
    /*public synchronized CMessageListData getShortCutListData() {
        mMessageListData = new CMessageListData();
        ArrayList<ChatShortcutItem> list = (ArrayList<ChatShortcutItem>) ChatShortcutAccessUtil.getShortcut(0, Integer.MAX_VALUE, true);
        mMessageListData.setMessageList(new CMessageList(list));
        return mMessageListData;
    }
*/
    @Override
    public void onTerminate() {
        super.onTerminate();
        /*SLogger.i(TAG, "restore:CApplication:onTerminate:this:" + this.toString());
        if (mDB != null) {
            mDB.close();
            mDB = null;
        }*/
    }
}
