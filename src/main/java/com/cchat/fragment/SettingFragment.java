package com.cchat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cchat.LoginActivity;
import com.cchat.R;
import com.cchat.SettingActivity;
import com.cchat.common.base.ADWaitDialogListener;
import com.cchat.common.base.ActionProtocol;
import com.cchat.common.base.BaseActivity;
import com.cchat.common.base.DAction;
import com.cchat.service.ConnectionService;
import com.cchat.utils.CommonUtils;

/**
 * 声音界面的Fragment，加载了sound_fragment布局。
 * 
 * @author guolin
 */
public class SettingFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setting_fragment, container, false);
		LinearLayout mLogutBtn = (LinearLayout) view.findViewById(R.id.ll_logout);
		mLogutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				accountLogut();
			}
		});

		return view;
	}

	/**
	 * 退出登录
	 */
	public void accountLogut() {

//        doAction
		((BaseActivity)getActivity()).doAction(new DAction(ActionProtocol.LOGOUT), new ADWaitDialogListener((BaseActivity)getActivity()) {
			@Override
			protected void onSucceed(Object[] values) {
//                getSharedPreferences("account", Context.MODE_PRIVATE).edit().putString("card", card).putString("password", String.format("%02d", password.toLowerCase().length()) + new CArgot().getReport(password.toLowerCase(), 2)).apply();
//                ((DApplication) getApplication()).setLoginData(new CLoginData(card, password.toLowerCase()));
				CommonUtils.showToast(getActivity(), (String) values[0]);
				if ((int) values[1] < 0)
					return;
				// 关闭服务
				Intent intent = new Intent(getActivity(), ConnectionService.class);
				getActivity().stopService(intent);
				startActivity(new Intent(getActivity(), LoginActivity.class));
//                finishAll();
				getActivity().finish();
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

}
