package com.cchat;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cchat.common.base.BaseActivity;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.utils.CommonUtils;
import com.cchat.utils.XmppTool;

/**
 * 声音界面的Activity，加入了sound_activity的布局。
 * 
 * @author guolin
 */
public class AddFrientActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend_activity);

		final EditText editText = (EditText)findViewById(R.id.et_add);
		Button buttonAdd = (Button)findViewById(R.id.btn_add);
		buttonAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(editText.getText().toString())){
					try {
						mConnectStubImp.reqAddTo(editText.getText().toString() + XmppTool.g_Domain);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} else
					CommonUtils.showToast(AddFrientActivity.this, "请输入账号！");
//				mConnectStubImp.reqAddTo();
			}
		});
	}

	@Override
	protected void recieveMessage(ChatMessage textMessage) {

	}

}
