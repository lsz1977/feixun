package com.cchat.common.base.data;

import java.io.Serializable;

/**
 * Created by holand on 15/11/24.
 */
public class RegisterData implements Serializable{
    private String mAccount;
    private String mPasswd;

    public RegisterData(String mAccount, String mPasswd) {
        this.mAccount = mAccount;
        this.mPasswd = mPasswd;
    }

    public String getmAccount() {
        return mAccount;
    }

    public void setmAccount(String mAccount) {
        this.mAccount = mAccount;
    }

    public String getmPasswd() {
        return mPasswd;
    }

    public void setmPasswd(String mPasswd) {
        this.mPasswd = mPasswd;
    }
}
