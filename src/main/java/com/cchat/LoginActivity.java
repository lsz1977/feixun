package com.cchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cchat.common.base.ADWaitDialogListener;
import com.cchat.common.base.ActionProtocol;
import com.cchat.common.base.BaseActivity;
import com.cchat.common.base.DAction;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.utils.CommonUtils;

public class LoginActivity extends BaseActivity {

    private static final String Tag = "LoginActivity";
    private EditText mETAccount;
    private EditText mETPasswd;
    private Button mLoginBtn;
    private String mUsername;
    private String mPassword;
    private Button mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        initViews();

        SharedPreferences mySharedPreferences = getSharedPreferences("config", Activity.MODE_PRIVATE); //TODO:加密后存储 解密后使用
        String account = mySharedPreferences.getString("account", null);
        String password = mySharedPreferences.getString("password", null);
        mETAccount.setText(account);
        mETPasswd.setText(password);

        setListener();

    }

    @Override
    protected void recieveMessage(ChatMessage textMessage) {

    }

    private void initViews(){
        mETAccount = (EditText)findViewById(R.id.et_account);
        mETPasswd = (EditText)findViewById(R.id.et_passwd);
        mLoginBtn = (Button)findViewById(R.id.btn_login);
        mRegisterBtn = (Button)findViewById(R.id.btn_register);

        mETAccount.addTextChangedListener(new TextChange());
        mETPasswd.addTextChangedListener(new TextChange());

    }

    private void setListener(){
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountLogin();
            }
        });
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class),0);
            }
        });
    }

    /**
     * 账户登录
     */
    public void accountLogin() {

        mUsername = mETAccount.getText().toString();
        if (TextUtils.isEmpty(mUsername)){
            CommonUtils.showToast(this, "用户名不能为空");
            return;
        }

        mPassword = mETPasswd.getText().toString();
        if (TextUtils.isEmpty(mPassword)){
            CommonUtils.showToast(this, "密码不能为空");
            return;
        }

//        doAction
        doAction(new DAction(ActionProtocol.LOGIN, mUsername, mPassword, true), new ADWaitDialogListener(this) {
            @Override
            protected void onSucceed(Object[] values) {
//                getSharedPreferences("account", Context.MODE_PRIVATE).edit().putString("card", card).putString("password", String.format("%02d", password.toLowerCase().length()) + new CArgot().getReport(password.toLowerCase(), 2)).apply();
//                ((DApplication) getApplication()).setLoginData(new CLoginData(card, password.toLowerCase()));
                CommonUtils.showToast(LoginActivity.this, (String)values[0]);
                if ((int)values[1] < 0)
                    return;

                //实例化SharedPreferences对象（第一步）
                SharedPreferences mySharedPreferences = getSharedPreferences("config",
                        Activity.MODE_PRIVATE); //TODO:加密后存储 解密后使用
                //实例化SharedPreferences.Editor对象（第二步）
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                //用putString的方法保存数据
                editor.putString("account", mUsername);
                editor.putBoolean("longinState", true);
                editor.putString("password", mPassword);
                //提交当前数据
                editor.commit();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                finishAll();
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
            mConnectStubImp.login(mUsername, mPassword);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

    }
    // EditText监听器
    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {
            boolean Sign2 = mETAccount.getText().length() > 0;
            boolean Sign3 = mETPasswd.getText().length() > 0;

            if (Sign2 & Sign3) {
                ((Button)findViewById(R.id.btn_login)).setTextColor(0xFFFFFFFF);
                ((Button)findViewById(R.id.btn_login)).setEnabled(true);
            }
            // 在layout文件中，对Button的text属性应预先设置默认值，否则刚打开程序的时候Button是无显示的
            else {
                ((Button)findViewById(R.id.btn_login)).setTextColor(0xFFD0EFC6);
                ((Button)findViewById(R.id.btn_login)).setEnabled(false);
            }
        }

    }

}
