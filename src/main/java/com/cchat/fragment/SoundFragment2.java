package com.cchat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cchat.R;

/**
 * 声音界面的Fragment，加载了sound_fragment布局。
 * 
 * @author guolin
 */
public class SoundFragment2 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sound_fragment, container, false);
		return view;
	}

}
