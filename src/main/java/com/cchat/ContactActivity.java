package com.cchat;

import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.cchat.common.base.ADWaitDialogListener;
import com.cchat.common.base.ActionProtocol;
import com.cchat.common.base.AlbumsAdapterWithCommonViewHolder;
import com.cchat.common.base.BaseActivity;
import com.cchat.common.base.DAction;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.service.ConnectionService;
import com.cchat.service.IServiceBindedListener;
import com.cchat.service.Person;
import com.cchat.service.TextMessage;

public class ContactActivity extends BaseActivity {

    public static final String Tag = "ContactActivity";
    private ListView listView;
    private List<Person> mContents;
    private AlbumsAdapterWithCommonViewHolder adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        initView();

        listView = (ListView)findViewById(R.id.listViewContact);
        mContents = new ArrayList<Person>();
        adapter = new AlbumsAdapterWithCommonViewHolder(this, R.layout.contact_item_view, mContents);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = mContents.get(position).getName();
                Intent intent = new Intent(ContactActivity.this, ChatActivity.class);
                intent.putExtra("touser", userName);
                startActivity(intent);
            }
        });

        mConnectStubImp.setServiceBindedListener(new IServiceBindedListener() {
            @Override
            public void bindedSuccess() {
               /* try {
                    mConnectStubImp.getFriends();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/
                //        doAction
                doAction(new DAction(ActionProtocol.GET_FRIENDS_FROM_NET), new ADWaitDialogListener(ContactActivity.this) {
                    @Override
                    protected void onSucceed(Object[] values) {
//                getSharedPreferences("account", Context.MODE_PRIVATE).edit().putString("card", card).putString("password", String.format("%02d", password.toLowerCase().length()) + new CArgot().getReport(password.toLowerCase(), 2)).apply();
//                ((DApplication) getApplication()).setLoginData(new CLoginData(card, password.toLowerCase()));
                        mContents.clear();
                        mContents.addAll((List<Person>)values[0]);
                        //TODO:[holand] ->这里通过 itemType.getType 区分 好友列表、好友请求列表 比较 请求、添加状态；在线 发起的请求 通过 friendListener 监听好友状态改变
                        adapter.notifyDataSetChanged();
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void recieveMessage(ChatMessage textMessage) {

    }

    private MediaPlayer player;

    private MediaPlayer ring() throws Exception, IOException {
        // TODO Auto-generated method stub
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer player = new MediaPlayer();
        player.setDataSource(this, alert);
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            // player.setLooping(true);
            player.prepare();
            player.start();
        }
        return player;
    }

    private void initView() {

        Button btnAdd = (Button)findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 开启服务 监听服务器
        Intent intent = new Intent(this, ConnectionService.class);
        startService(intent);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*MyselfFragment interface*/

    /*public void exitAccount() {
        try {
            mConnectStubImp.logout();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finish();
    }*/

}
