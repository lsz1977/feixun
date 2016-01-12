package com.cchat.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cchat.ChatActivity;
import com.cchat.ChatMsgEntity;
import com.cchat.ChatMsgViewAdapter;
import com.cchat.Constant;
import com.cchat.DisplayActivity;
import com.cchat.R;
import com.cchat.AddFrientActivity;
import com.cchat.common.base.ADActionListener;
import com.cchat.common.base.AlbumsAdapterWithCommonViewHolder;
import com.cchat.common.base.BaseActivity;
import com.cchat.common.base.DAction;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.common.base.data.DChatMessageProtocol;
import com.cchat.common.base.data.DataTalk;
import com.cchat.common.base.data.FileResult;
import com.cchat.common.base.data.VTimeUtil;
import com.cchat.service.Person;
import com.cchat.utils.AudioRecorder;
import com.cchat.utils.CommonUtils;
import com.cchat.utils.FileSizeUtil;
import com.cchat.utils.FileUtils;
import com.cchat.utils.MyIntent;
import com.cchat.utils.XmppTool;

import org.webrtc.apprtc.njtest.TransferManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 菜单的Fragment，用于显示菜单界面内容，以及处理菜单界面的点击事件。
 * 
 * @author holand
 */
public class ChatFragment extends Fragment implements OnItemClickListener, OnClickListener {

	/**
	 * 菜单界面中只包含了一个ListView。
	 */
	private ListView mListView;

	private String mCurrentChatTo;

	/**
	 * ListView的适配器。
	 */
	private AlbumsAdapterWithCommonViewHolder adapter;

	/**
	 * 用于填充ListView的数据，这里就简单只用了两条数据。
	 */
	public String[] menuItems = { "Sound", "Display" };
	public List<Person> mContents;

	private Button mBtnSend;
	private EditText mEditTextContent;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();

	/**
	 * 是否是双页模式。如果一个Activity中包含了两个Fragment，就是双页模式。
	 */
	private boolean isTwoPane;
	private Button btnMore;

	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;
	private ListView listView;
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonPressToSpeak;
	private View more;
	private LinearLayout btnContainer;
	private RelativeLayout edittext_layout;
	private InputMethodManager manager;

	//voice data
	private Dialog dialog;

	private static int MAX_TIME = 15;    //最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 1;     //最短录制时间，单位秒，0为无时间限制，建议设为1

	private static int RECORD_NO = 0;  //不在录音
	private static int RECORD_ING = 1;   //正在录音
	private static int RECODE_ED = 2;   //完成录音

	private static int RECODE_STATE = 0;      //录音的状态

	private static float recodeTime=0.0f;    //录音的时间
	private static double voiceValue=0.0;    //麦克风获取的音量值

	private ImageView dialog_img;
	protected AudioRecorder mr;
	private ImageButton btn_voice_bar;
	private static boolean playState = false;  //播放状态

	private String timeStr;
	private String mDefaultSavePath = "/mnt/sdcard/cchat/";
	private TextView button_pushtotalk;
	private TransferManager transferMenager;
	private ImageView btn_picture;

	/**
	 * 当Activity和Fragment建立关联时，初始化适配器中的数据。
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
//		Intent intent = getIntent();
//		mToUser = intent.getStringExtra("touser");
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) getActivity().getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");


		mContents = new ArrayList<Person>();
		adapter = new AlbumsAdapterWithCommonViewHolder(activity, R.layout.contact_item_view, mContents);
	}

	/**
	 * 加载menu_fragment布局文件，为ListView绑定了适配器，并设置了监听事件。
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_chat_test, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);

		initView(view);

		initData();

		return view;
	}

	/**
	 * 当Activity创建完毕后，尝试获取一下布局文件中是否有details_layout这个元素，如果有说明当前
	 * 是双页模式，如果没有说明当前是单页模式。
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getActivity().findViewById(R.id.details_layout) != null) {
			isTwoPane = true;
		} else {
			isTwoPane = false;
		}
	}

	/**
	 * 处理ListView的点击事件，会根据当前是否是双页模式进行判断。如果是双页模式，则会动态添加Fragment。
	 * 如果不是双页模式，则会打开新的Activity。
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
		if (isTwoPane) {
			Fragment fragment = null;
			if (index == 0) {
				fragment = new ChatFragment();
			} else if (index == 1) {
				fragment = new ConversationsFragment();
			}
			getFragmentManager().beginTransaction().replace(R.id.details_layout, fragment).commit();
		} else {
			Intent intent = null;
			if (index == 0) {
				intent = new Intent(getActivity(), AddFrientActivity.class);
			} else if (index == 1) {
				intent = new Intent(getActivity(), DisplayActivity.class);
			}
			startActivity(intent);
		}
	}

	public void refresh(String test){
//		CommonUtils.showToast(getActivity(), test);
		recieveMsg(test);
	}

	public void updateFileTransferState(FileResult fileResult){
//		if ("start".equals(fileResult.getType())){
			recieveFileMsg(fileResult);
//		} else
//			recieveFileMsg(fileResult);
	}

	public void release(){
		if (transferMenager!=null){
			transferMenager.endToTransfer();
		}
	}
	public void changeTransferState(){
		SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("config", Activity.MODE_PRIVATE);
		String account = mySharedPreferences.getString("account", null);
		sendMessageToChangeState(account + XmppTool.g_Domain, mCurrentChatTo, null);
	}

	public void initView(View view) {
//		mListView = (ListView) view.findViewById(R.id.listview);
		mBtnSend = (Button) view.findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		btnMore = (Button) view.findViewById(R.id.btn_more);
		btnMore.setOnClickListener(this);
		edittext_layout = (RelativeLayout) view.findViewById(R.id.edittext_layout);
		btnContainer = (LinearLayout) view.findViewById(R.id.ll_btn_container);

		mEditTextContent = (EditText) view.findViewById(R.id.et_sendmessage);

		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);

				btnContainer.setVisibility(View.GONE);
			}
		});
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					mBtnSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					mBtnSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mEditTextContent.setText("test");
		more = view.findViewById(R.id.more);

		buttonSetModeVoice = view.findViewById(R.id.btn_set_mode_voice);

		buttonSetModeVoice.setOnClickListener(this);

		buttonSetModeKeyboard = view.findViewById(R.id.btn_set_mode_keyboard);
		buttonSetModeKeyboard.setOnClickListener(this);

		buttonPressToSpeak = view.findViewById(R.id.btn_press_to_speak);
		button_pushtotalk = (TextView)view.findViewById(R.id.button_pushtotalk);
		buttonPressToSpeak.setOnClickListener(this);

		btn_picture = (ImageView)view.findViewById(R.id.btn_picture);
		btn_picture.setOnClickListener(this);
		//录音
		/*buttonPressToSpeak.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {

					case MotionEvent.ACTION_DOWN:
						if (RECODE_STATE != RECORD_ING) {
							scanOldFile();

							timeStr = timeStr() + getRandomChar(5);
							mr = new AudioRecorder(mDefaultSavePath, timeStr);
							RECODE_STATE=RECORD_ING;
							showVoiceDialog();
							try {
								mr.start();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mythread();
						}

						break;
					case MotionEvent.ACTION_UP:
						if (RECODE_STATE == RECORD_ING) {
							RECODE_STATE=RECODE_ED;
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
							try {
								mr.stop();
								voiceValue = 0.0;
							} catch (IOException e) {
								e.printStackTrace();
							}

							if (recodeTime < MIX_TIME) {
								showWarnToast();
								button_pushtotalk.setText("松开结束");
								RECODE_STATE=RECORD_NO;

							} else {
								//play test
								//准备发送音频文件  over！！！！

								String url1 = mDefaultSavePath+timeStr+".amrv";
								String[] a = new String[1];
								a[0] = url1;
								sendVoice("voice",a);
							}
						}

						break;
				}
				return false;
			}
		});*/
	}

	/**
	 * 显示语音图标按钮
	 *
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		edittext_layout.setVisibility(View.GONE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		mBtnSend.setVisibility(View.GONE);
		btnMore.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		btnContainer.setVisibility(View.VISIBLE);

	}

	/**
	 * 显示键盘图标
	 *
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		// mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if(hasFocus){
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// }
		// }
		// });
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			mBtnSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			mBtnSend.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 显示或隐藏图标按钮页
	 *
	 */
	public void more() {
		if (more.getVisibility() == View.GONE) {
			System.out.println("more gone");
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
		} else {
			more.setVisibility(View.GONE);


		}

	}
	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private Thread recordThread;

	//voice
	//删除老文件
	void scanOldFile(){
		File file = new File(Environment
				.getExternalStorageDirectory(), "my/voice.amr");
		if(file.exists()){
			file.delete();
		}
	}

	//录音时显示Dialog
	void showVoiceDialog(){
		dialog = new Dialog(getActivity(), R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog_img=(ImageView)dialog.findViewById(R.id.dialog_img);
		dialog.show();
	}

	//录音时间太短时Toast显示
	void showWarnToast(){
		Toast toast = new Toast(getActivity());
		LinearLayout linearLayout = new LinearLayout(getActivity());
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		// 定义一个ImageView
		ImageView imageView = new ImageView(getActivity());
		imageView.setImageResource(R.drawable.voice_to_short); // 图标

		TextView mTv = new TextView(getActivity());
		mTv.setText("时间太短   录音失败");
		mTv.setTextSize(14);
		mTv.setTextColor(Color.WHITE);//字体颜色
		//mTv.setPadding(0, 10, 0, 0);

		// 将ImageView和ToastView合并到Layout中
		linearLayout.addView(imageView);
		linearLayout.addView(mTv);
		linearLayout.setGravity(Gravity.CENTER);//内容居中
		linearLayout.setBackgroundResource(R.drawable.record_bg);//设置自定义toast的背景

		toast.setView(linearLayout);
		toast.setGravity(Gravity.CENTER, 0,0);//起点位置为中间     100为向下移100dp
		toast.show();
	}

	//获取文件手机路径
	private String getAmrPath(){
		File file = new File(Environment
				.getExternalStorageDirectory(), "my/voice.amr");
		return file.getAbsolutePath();
	}

	//录音计时线程
	void mythread(){
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	//录音Dialog图片随声音大小切换
	void setDialogImage(){
		if (voiceValue < 200.0) {
			dialog_img.setImageResource(R.drawable.record_animate_01);
		}else if (voiceValue > 200.0 && voiceValue < 400) {
			dialog_img.setImageResource(R.drawable.record_animate_02);
		}else if (voiceValue > 400.0 && voiceValue < 800) {
			dialog_img.setImageResource(R.drawable.record_animate_03);
		}else if (voiceValue > 800.0 && voiceValue < 1600) {
			dialog_img.setImageResource(R.drawable.record_animate_04);
		}else if (voiceValue > 1600.0 && voiceValue < 3200) {
			dialog_img.setImageResource(R.drawable.record_animate_05);
		}else if (voiceValue > 3200.0 && voiceValue < 5000) {
			dialog_img.setImageResource(R.drawable.record_animate_06);
		}else if (voiceValue > 5000.0 && voiceValue < 7000) {
			dialog_img.setImageResource(R.drawable.record_animate_07);
		}else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_08);
		}else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_09);
		}else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_10);
		}else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_11);
		}else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_12);
		}else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_13);
		}else if (voiceValue > 28000.0) {
			dialog_img.setImageResource(R.drawable.record_animate_14);
		}
	}

	//录音线程
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE==RECORD_ING) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0);
				}else{
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						if (RECODE_STATE == RECORD_ING) {
							voiceValue = mr.getAmplitude();
							imgHandle.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
					case 0:
						//录音超过15秒自动停止
						if (RECODE_STATE == RECORD_ING) {
							RECODE_STATE=RECODE_ED;
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
							try {
								mr.stop();
								voiceValue = 0.0;
							} catch (IOException e) {
								e.printStackTrace();
							}

							if (recodeTime < 1.0) {
								showWarnToast();
								button_pushtotalk.setText("按住说话");
								RECODE_STATE=RECORD_NO;
							} else{
								button_pushtotalk.setText("录音完成!点击重新录音");
							}
						}
						break;
					case 1:
						setDialogImage();
						break;
					default:
						break;
				}

			}
		};
	};

	public void initData() {

		wakeLock = ((PowerManager) getActivity().getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cchat");
		mAdapter = new ChatMsgViewAdapter(getActivity(), mDataArrays);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ChatMsgEntity chatMsgEntity = mDataArrays.get(position);
				if(chatMsgEntity.getMessageType() == 1){
					int fileType = MyIntent.getFileTyte(chatMsgEntity.getFilePath());
					switch (fileType){
						case MyIntent.PictureType:
							MyIntent.getPictureIntent(chatMsgEntity.getFilePath());
							break;
						case MyIntent.AudioType:
							MyIntent.getAudioIntent(chatMsgEntity.getFilePath());
							break;
						case MyIntent.TextType:
							MyIntent.getTextIntent(chatMsgEntity.getFilePath());
							break;
						case MyIntent.VideoType:
							MyIntent.getVideoIntent(chatMsgEntity.getFilePath());
							break;
						case MyIntent.UnknowType:
							CommonUtils.showToast(getActivity(), "位置文件类型");
							break;
					}
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btn_send:

				if (isTwoPane) {
					mCurrentChatTo = getArguments().getString("to");

				} else
					mCurrentChatTo = (String) ((ChatActivity) getActivity()).getIntent().getStringExtra("to");

				String contString = mEditTextContent.getText().toString();
				if (contString.length() > 0) {
					ChatMsgEntity entity = new ChatMsgEntity();
					entity.setDate(getDate());
					entity.setMsgType(false);
					entity.setText(contString);

					mDataArrays.add(entity);
					mAdapter.notifyDataSetChanged();

					mEditTextContent.setText("");

					SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("config", Activity.MODE_PRIVATE);
					String account = mySharedPreferences.getString("account", null);

					sendTextMessage(account + XmppTool.g_Domain, mCurrentChatTo, contString, null);
					mListView.setSelection(mListView.getCount() - 1);
				}

				/*sendImageMessage("2@hd", "8@hd", "http://www.baidu.com", new ADActionListener(((BaseActivity)getActivity())) {
					@Override
					protected void onSucceed(Object[] values) {
						System.out.println();

					}
				});*/

				break;
			case R.id.btn_set_mode_voice:
				setModeVoice(v);
				break;
			case R.id.btn_set_mode_keyboard:
				setModeKeyboard(v);
				break;
			case R.id.btn_press_to_speak:
				if (RECODE_STATE != RECORD_ING) {
					scanOldFile();

					timeStr = CommonUtils.timeStr() + CommonUtils.getRandomChar(5);
					mr = new AudioRecorder(mDefaultSavePath, timeStr);
					RECODE_STATE=RECORD_ING;
					showVoiceDialog();
					try {
						mr.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mythread();
				}
				//OK键 发送  其他键 取消
				CommonUtils.showToast(getActivity(), "按OK键发送，按其他键任意键取消!");
				break;
			case R.id.btn_more:
				more();
				break;

			case R.id.btn_picture:
				showFileChooser();
				break;
			default:
				break;
		}
	}


	public boolean onKeyDown( int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			CommonUtils.showToast(getActivity(), "BACK");
			return true;
		} else if(KeyEvent.KEYCODE_ENTER == keyCode) {
			CommonUtils.showToast(getActivity(), "ENTER");
			return true;
		}

		return false;

	}

	private void send(String to) {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			//entity.setName("me");
			entity.setMsgType(false);
			entity.setText(contString);

			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();

			mEditTextContent.setText("");

			((BaseActivity)getActivity()).sendMessage(to, contString);

			/*try {
				mConnectStubImp.sendMessage(mToUser, contString);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			mListView.setSelection(mListView.getCount() - 1);
		}
	}

	private void recieveMsg(String contString){
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setDate(getDate());
		//entity.setName("me");
		entity.setMsgType(true);
		entity.setText(contString);

		mDataArrays.add(entity);
		mAdapter.notifyDataSetChanged();

		mListView.setSelection(mListView.getCount() - 1);
	}

	private void recieveFileMsg(FileResult fileResult){
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setDate(getDate());
		//entity.setName("me");
		entity.setMsgType(true);
		entity.setText(fileResult.getFileName());


		entity.setFilePath(fileResult.getFilePath());
		mDataArrays.add(entity);
		mAdapter.notifyDataSetChanged();

		mListView.setSelection(mListView.getCount() - 1);
	}

	private String getDate() {
		Calendar c = Calendar.getInstance();

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);


		return sbBuffer.toString();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
//				this.finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void head_xiaohei(View v) {
		//Intent intent = new Intent (ChatActivity.this,InfoXiaohei.class);
		//startActivity(intent);
	}

	public  void sendImageMessage(String from, String to, String image, final ADActionListener listener) {
		final ChatMessage message = new ChatMessage();
		String localTime = VTimeUtil.getTimeText(VTimeUtil.FORMAT_FULL);
		message.setTo(to);
		message.setFrom(from);
		message.setDisplayTime(localTime);
		final DataTalk talk = new DataTalk();
		talk.setContentType(DataTalk.ContentType.image);
		talk.setAttachLocal(image);
		talk.setTimeId(localTime);
		talk.setFun(DataTalk.Func.OnMsg);
		/*((BaseActivity)getActivity()).doAction(new DAction(DSystemTimeProtocol.GET_CUR_STANDARD_TIME, 0), new ADActionListener(((BaseActivity)getActivity())) {
			@Override
			protected void onSucceed(Object[] values) {
				String standerTime = TimeUtils.getTimeText(TimeUtils.FORMAT_FULL, (Long) values[0]);
				talk.setTimeId(standerTime);
				message.setDataTalk(talk);
				message.setDisplayTime(standerTime);
				((BaseActivity) getActivity()).doAction(new DAction(DChatMessageProtocol.SEND_MESSAGE, message), null);
				TransferStateListener.onFinished(true, new Object[]{message});
			}
		});*/
		talk.setTimeId("1111-1111-1111");
		message.setDataTalk(talk);
		message.setDisplayTime("2015-0505-05");

		((BaseActivity) getActivity()).doPreAction(new DAction(DChatMessageProtocol.SEND_MESSAGE, message), message);
	}

	public void sendTextMessage( String from, String to, String content, final ADActionListener listener) {
		final ChatMessage message = new ChatMessage();
		String localTime = VTimeUtil.getTimeText(VTimeUtil.FORMAT_FULL);
		message.setTo(to);
		message.setFrom(from);
		message.setDisplayTime(localTime);
		final DataTalk talk = new DataTalk();
		talk.setContentType(DataTalk.ContentType.text);
		talk.setContent(content);
		talk.setTimeId(localTime);
		talk.setFun(DataTalk.Func.OnMsg);
		/*activity.doAction(new DAction(DSystemTimeProtocol.GET_CUR_STANDARD_TIME, 0), new ADActionListener(activity) {
			@Override
			protected void onSucceed(Object[] values) {*/
//				String standerTime = TimeUtils.getTimeText(TimeUtils.FORMAT_FULL, (Long) values[0]);
				/*talk.setTimeId(standerTime);
				message.setDataTalk(talk);
				message.setDisplayTime(standerTime);*/
		talk.setTimeId("1111-1111-1111");
		message.setDataTalk(talk);
		message.setDisplayTime("2015-0505-05");
		((BaseActivity) getActivity()).doPreAction(new DAction(DChatMessageProtocol.SEND_MESSAGE, message), message);
//				TransferStateListener.onFinished(true, new Object[]{message});
			/*}
		});*/
	}

	public void sendTextMessageFileTest( String from, String to, FileResult fileResult, final ADActionListener listener) {
		final ChatMessage message = new ChatMessage();
		String localTime = VTimeUtil.getTimeText(VTimeUtil.FORMAT_FULL);
		message.setTo(to);
		message.setFrom(from);
		message.setDisplayTime(localTime);

		String roomId = "1234567";
		fileResult.setRoomId(roomId);
		fileResult.setId(1);
		fileResult.setIsSender(true);

		if (getView().findViewById(R.id.details_layout) != null) {
			isTwoPane = true;
		} else {
			isTwoPane = false;
		}

		if (fileResult.getFileName().length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			entity.setMsgType(false);
			entity.setMessageType(2);//1 text  2 file
			entity.setFilePath(fileResult.getFilePath());
			entity.setText(fileResult.getFileName());

			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();

//			mEditTextContent.setText("");

			SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("config", Activity.MODE_PRIVATE);
			String account = mySharedPreferences.getString("account", null);

			mListView.setSelection(mListView.getCount() - 1);
		}


		transferMenager = new TransferManager(getActivity(), roomId, fileResult, isTwoPane, null);

		final DataTalk talk = new DataTalk();
		talk.setContentType(DataTalk.ContentType.file);
		talk.setContent(/*content*/"{id:345,roomId:\""+fileResult.getRoomId()+"\",fileName:\""+fileResult.getFileName()+"\",filePath:\""+fileResult.getFilePath()+"\",size:"+fileResult.getSize()+",serverIp:\"192.168.1.102\",port:\"3001\",senderPerrId:\"767676767\",recieverPeerId:\"6768686857657\",type:\"start\"} ");
		talk.setTimeId(localTime);
		talk.setFun(DataTalk.Func.OnMsg);
		/*activity.doAction(new DAction(DSystemTimeProtocol.GET_CUR_STANDARD_TIME, 0), new ADActionListener(activity) {
			@Override
			protected void onSucceed(Object[] values) {*/
//				String standerTime = TimeUtils.getTimeText(TimeUtils.FORMAT_FULL, (Long) values[0]);
				/*talk.setTimeId(standerTime);
				message.setDataTalk(talk);
				message.setDisplayTime(standerTime);*/
		talk.setTimeId("1111-1111-1111");
		message.setDataTalk(talk);
		message.setDisplayTime("2015-0505-05");


		((BaseActivity) getActivity()).doPreAction(new DAction(DChatMessageProtocol.SEND_MESSAGE, message), message);
//				TransferStateListener.onFinished(true, new Object[]{message});
			/*}
		});*/
	}

	public void sendMessageToChangeState( String from, String to, final ADActionListener listener) {
		CommonUtils.showToast(getActivity(), "I'm a sender! "+ from);
		final ChatMessage message = new ChatMessage();
		String localTime = VTimeUtil.getTimeText(VTimeUtil.FORMAT_FULL);
		message.setTo(to);
		message.setFrom(from);
		message.setDisplayTime(localTime);

		String roomId = "1234567";
		FileResult fileResult = new FileResult();
		fileResult.setRoomId(roomId);
		fileResult.setId(1);
		fileResult.setFileName("2.txt");
		fileResult.setFilePath("/mnt/sdcard/2.txt");
		fileResult.setIsSender(true);

		if (getView().findViewById(R.id.details_layout) != null) {
			isTwoPane = true;
		} else {
			isTwoPane = false;
		}

//		transferMenager = new TransferManager(getActivity(), roomId, fileResult, isTwoPane, null);

		final DataTalk talk = new DataTalk();
		talk.setContentType(DataTalk.ContentType.file);
		talk.setContent(/*content*/"{id:345,roomId:\""+roomId+"\",fileName:\"2.txt\",filePath:\"/mnt/sdcard/2.txt\",targetPath:\""+fileResult.getTargetPath()+"\",serverIp:\"192.168.1.102\",port:\"3001\",senderPerrId:\"767676767\",recieverPeerId:\"6768686857657\",type:\"end\"} ");
		talk.setTimeId(localTime);
		talk.setFun(DataTalk.Func.OnMsg);
		/*activity.doAction(new DAction(DSystemTimeProtocol.GET_CUR_STANDARD_TIME, 0), new ADActionListener(activity) {
			@Override
			protected void onSucceed(Object[] values) {*/
//				String standerTime = TimeUtils.getTimeText(TimeUtils.FORMAT_FULL, (Long) values[0]);
				/*talk.setTimeId(standerTime);
				message.setDataTalk(talk);
				message.setDisplayTime(standerTime);*/
		talk.setTimeId("1111-1111-1111");
		message.setDataTalk(talk);
		message.setDisplayTime("2015-0505-05");


		((BaseActivity) getActivity()).doPreAction(new DAction(DChatMessageProtocol.SEND_MESSAGE, message), message);
//				TransferStateListener.onFinished(true, new Object[]{message});
			/*}
		});*/
	}
	private PowerManager.WakeLock wakeLock;

	/**
	 * 点击文字输入框
	 *
	 */
	public void editClick() {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
		}

	}

	/** 调用文件选择软件来选择文件 **/
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
					Constant.FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(getActivity(), "请安装文件管理器", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/** 根据返回选择的文件，来进行上传操作 **/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			String url;
			url = FileUtils.getPath(getActivity(), uri);

			SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("config", Activity.MODE_PRIVATE);
			String account = mySharedPreferences.getString("account", null);

			if (isTwoPane) {
				mCurrentChatTo = getArguments().getString("to");

			} else
				mCurrentChatTo = (String) ((ChatActivity) getActivity()).getIntent().getStringExtra("to");

			String fileName = url.substring(url.lastIndexOf("/") + 1);

			FileResult fileResult = new FileResult();
			fileResult.setFileName(fileName);
			fileResult.setFilePath(url);
			String targetPath = "/"+CommonUtils.getRandomNumChar(8)+ fileResult.getFileName();
			fileResult.setTargetPath(targetPath);
			fileResult.setSize(FileSizeUtil.getFileOrFilesSize(url, FileSizeUtil.SIZETYPE_B));
			sendTextMessageFileTest(account + XmppTool.g_Domain, mCurrentChatTo, fileResult, null);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
