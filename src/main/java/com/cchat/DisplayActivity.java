package com.cchat;

import android.app.Activity;
import android.os.Bundle;
import com.cchat.R;

/**
 * 显示界面的Activity，加入了display_activity的布局。
 * 
 * @author guolin
 */
public class DisplayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_activity);
	}

}
