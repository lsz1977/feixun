package com.cchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cchat.common.base.ADWaitDialogListener;
import com.cchat.common.base.ActionProtocol;
import com.cchat.common.base.BaseActivity;
import com.cchat.common.base.DAction;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.common.base.data.RegisterData;
import com.cchat.common.base.service.base.android.ShareDataPack;
import com.cchat.service.TextMessage;
import com.cchat.utils.CommonUtils;


public class ResetPasswdActivity extends BaseActivity {

    private static final String Tag = "RegisterActivity";
    private EditText mETAccount;
    private EditText mETPasswd;
    private EditText mETConfirmPasswd;
    private Button mRegisterBtn;
    private String mAccount;
    private String mPassword;
    private String mConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        setListener();
    }

    @Override
    protected void recieveMessage(ChatMessage textMessage) {

    }

    private void initViews(){
        mETAccount = (EditText)findViewById(R.id.et_account);
        mETPasswd = (EditText)findViewById(R.id.et_passwd);
        mETConfirmPasswd = (EditText)findViewById(R.id.et_confirm_passwd);
        mRegisterBtn = (Button)findViewById(R.id.btn_register);
    }

    private void setListener(){
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountRigister();
            }
        });
    }

    /**
     * 账户注册
     */
    public void accountRigister() {

        mAccount = mETAccount.getText().toString();
        if (TextUtils.isEmpty(mAccount)){
            CommonUtils.showToast(this, "用户名不能为空");
            return;
        }

        mPassword = mETPasswd.getText().toString();
        if (TextUtils.isEmpty(mPassword)){
            CommonUtils.showToast(this, "密码不能为空");
            return;
        }

        mConfirmPassword = mETConfirmPasswd.getText().toString();
        if (TextUtils.isEmpty(mConfirmPassword)){
            CommonUtils.showToast(this, "确认密码不能为空");
            return;
        }

        if (!mPassword.equals(mConfirmPassword)) {
            CommonUtils.showToast(this, "两次密码输入不一致");
            return;
        }

//        doAction
        doAction(new DAction(ActionProtocol.CHANGE_PASSWORD, mAccount, mPassword), new ADWaitDialogListener(this) {
            @Override
            protected void onSucceed(Object[] values) {
//                getSharedPreferences("account", Context.MODE_PRIVATE).edit().putString("card", card).putString("password", String.format("%02d", password.toLowerCase().length()) + new CArgot().getReport(password.toLowerCase(), 2)).apply();
//                ((DApplication) getApplication()).setLoginData(new CLoginData(card, password.toLowerCase()));
//                finishAll();
//                int code = (int)values[1];
                CommonUtils.showToast(ResetPasswdActivity.this, (String)values[0]);

                setResult(0, new Intent().putExtra("register", new ShareDataPack(mAccount, mPassword)));
                finish();
            }

            @Override
            protected void onFailed(/*HttpLogicResult result*/) {
               /* String status = "登陆失败";
                if (result != null) {
                    if (result.getErrorValue() == HttpLogicResult.CARD_WRONG) {
                        status = "该帐号不存在";
                    } else if (result.getErrorValue() == HttpLogicResult.PASSWORD_WRONG) {
                        status = "密码错误!请重新输入";
                    }
                }*/
//                showToast(status);
            }
        });
        /*try {
            mConnectStubImp.login(mAccount, mPassword);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

    }

}
