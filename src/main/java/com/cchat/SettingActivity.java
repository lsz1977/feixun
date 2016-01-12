package com.cchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cchat.common.base.ADWaitDialogListener;
import com.cchat.common.base.ActionProtocol;
import com.cchat.common.base.BaseActivity;
import com.cchat.common.base.DAction;
import com.cchat.common.base.data.ChatMessage;
import com.cchat.service.TextMessage;
import com.cchat.utils.CommonUtils;


public class SettingActivity extends BaseActivity {

    private static final String Tag = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void recieveMessage(ChatMessage textMessage) {

    }

}
