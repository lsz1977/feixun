package com.cchat;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.KeyEvent;

import com.cchat.common.base.ADWaitDialogListener;
import com.cchat.common.base.ActionProtocol;
import com.cchat.common.base.BaseActivity;
import com.cchat.common.base.DAction;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.common.base.data.DataTalk;
import com.cchat.common.base.data.FileResult;
import com.cchat.fragment.ChatFragment;
import com.cchat.fragment.ContactFragment;
import com.cchat.fragment.ConversationsFragment;
import com.cchat.fragment.ConversationsFragment.Relationship;
import com.cchat.fragment.MenuFragment;
import com.cchat.service.IServiceBindedListener;
import com.cchat.service.Person;
import com.cchat.utils.CommonUtils;
import com.cchat.utils.TLog;

import org.webrtc.apprtc.njtest.TransferManager;

import java.util.List;

/**
 * 程序的主界面。
 * 
 * @author guolin
 */
public class MainActivity extends BaseActivity implements Relationship {

	private boolean isTwoPane;
	private MenuFragment menuFragment = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (findViewById(R.id.details_layout) != null) {
			isTwoPane = true;
		} else {
			isTwoPane = false;
		}

		mConnectStubImp.setServiceBindedListener(new IServiceBindedListener() {
			@Override
			public void bindedSuccess() {
				//        doAction
				doAction(new DAction(ActionProtocol.GET_FRIENDS_FROM_NET), new ADWaitDialogListener(MainActivity.this) {
					@Override
					protected void onSucceed(Object[] values) {
//						mContents.clear();
//						mContents.addAll((List<Person>) values[0]);
						List<Person> persons = (List<Person>) values[0];
						if (persons.size()>0){
							CommonUtils.showToast(MainActivity.this, persons.get(0).getName());

							//TODO: 入库操作

							if (isTwoPane) {
								menuFragment=(MenuFragment)getFragmentManager().findFragmentById(R.id.left_fragment);
							} else {
								menuFragment=(MenuFragment)getFragmentManager().findFragmentById(R.id.menu_fragment);
							}
							ContactFragment contactFragment = ((ContactFragment)menuFragment.fragments[0]);

							ConversationsFragment conversationsFragment = ((ConversationsFragment)menuFragment.fragments[1]);
							if (persons!=null && persons.size()>0){
								contactFragment.mContents.clear();
								if (conversationsFragment!=null && conversationsFragment.mContents!=null){
									conversationsFragment.mContents.clear();
								}

								for (Person person: persons){

									if ("both".equals(person.getType())){
//										contactFragment.mContents.clear();
										contactFragment.mContents.add(person);
										contactFragment.refresh();
									} else {

										if (conversationsFragment!=null && conversationsFragment.mContents!=null){
											conversationsFragment.mContents.add(person);
											conversationsFragment.refresh();
										}
									}
								}

							}


						}

//						//TODO:[holand] ->这里通过 itemType.getType 区分 好友列表、好友请求列表 比较 请求、添加状态；在线 发起的请求 通过 friendListener 监听好友状态改变
//						adapter.notifyDataSetChanged();
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
			}
		});
		
	}

	@Override
	protected void recieveMessage(ChatMessage chatMessage) {

		ContactFragment contactFragment = ((ContactFragment)menuFragment.fragments[0]);
		final ChatFragment fragment = contactFragment.fragment;

		if (DataTalk.ContentType.file.equals(chatMessage.getDataTalk().getContentType())){
			CommonUtils.showToast(this, "Prepare to recieve a file:" + chatMessage.getDataTalk().getContent());
			FileResult fileResult = (FileResult) CommonUtils.parseCommonResult(chatMessage.getDataTalk().getContent(), FileResult.class);

			if (fileResult.getType().equals("end")){
				fragment.release();
				return;
			} else if (fileResult.getType().equals("start")){
				CommonUtils.showToast(this, fileResult.getFilePath());
				String roomId = fileResult.getRoomId();
				TLog.analytics("size=1="+fileResult.getSize());
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
			//文件类型处理
			return;
		}

		if (fragment != null)
			fragment.refresh(chatMessage.getDataTalk().getContent());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		ContactFragment contactFragment = ((ContactFragment)menuFragment.fragments[0]);
		ChatFragment fragment = contactFragment.fragment;

		if(KeyEvent.KEYCODE_BACK == keyCode) {
			if (fragment != null)
				fragment.onKeyDown(keyCode,event);
			return true;
		} else if(KeyEvent.KEYCODE_ENTER == keyCode){
			if (fragment != null)
				fragment.onKeyDown(keyCode,event);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void agree(String jid) {
		try {
			mConnectStubImp.agreeTo(jid);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reqAdd(String jid) {
		try {
			mConnectStubImp.reqAddTo(jid);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
