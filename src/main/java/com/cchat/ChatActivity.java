package com.cchat;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;

import com.cchat.common.base.ADActionListener;
import com.cchat.common.base.BaseActivity;
import com.cchat.common.base.DAction;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.common.base.data.DChatMessageProtocol;
import com.cchat.common.base.data.DSystemTimeProtocol;
import com.cchat.common.base.data.DataTalk;
import com.cchat.common.base.data.FileResult;
import com.cchat.common.base.data.TimeUtils;
import com.cchat.common.base.data.VTimeUtil;
import com.cchat.fragment.ChatFragment;
import com.cchat.service.TextMessage;
import com.cchat.utils.CommonUtils;
import com.cchat.utils.TLog;

import org.webrtc.apprtc.njtest.TransferManager;

/**
 * 会话界面的Activity，加入了chat_new_activity的布局。
 * 
 * @author guolin
 */
public class ChatActivity extends BaseActivity {

	private ChatFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_new_activity);

		fragment = (ChatFragment)getFragmentManager().findFragmentById(R.id.chat_fragment);

	}

	@Override
	protected void recieveMessage(ChatMessage chatMessage) {
		if (DataTalk.ContentType.file.equals(chatMessage.getDataTalk().getContentType())){
			CommonUtils.showToast(this, "Prepare to recieve a file:" + chatMessage.getDataTalk().getContent());

			FileResult fileResult = (FileResult) CommonUtils.parseCommonResult(chatMessage.getDataTalk().getContent(), FileResult.class);

			if (fileResult.getType().equals("end")){
				fragment.release();
				fragment.updateFileTransferState(fileResult);
				return;
			} else if (fileResult.getType().equals("start")){

				fragment.updateFileTransferState(fileResult);
				CommonUtils.showToast(this, fileResult.getFilePath());

				String roomId = fileResult.getRoomId();
				TLog.analytics("size=1=" + fileResult.getSize());
				fileResult.setId(1);
				fileResult.setFileName(fileResult.getFileName());
				fileResult.setFilePath(fileResult.getFilePath());
				fileResult.setIsSender(false);
				TransferManager transferMenager = new TransferManager(this, roomId, fileResult, true, new TransferStateListener(){
					@Override
					public void finish() {
						fragment.changeTransferState();
					}

					@Override
					public void cancel() {
						fragment.changeTransferState();
					}
				});
			}
			return;
		} else
			fragment.refresh(chatMessage.getDataTalk().getContent());
	}

	/**
	 * 点击文字输入框
	 *
	 * @param v
	 */
	public void editClick(View v) {
		fragment.editClick();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*if(KeyEvent.KEYCODE_BACK == keyCode) {
			fragment.onKeyDown(keyCode,event);
			return true;
		} else if(KeyEvent.KEYCODE_ENTER == keyCode){
			fragment.onKeyDown(keyCode,event);
			return true;
		}*/

		return super.onKeyDown(keyCode, event);
	}

}
