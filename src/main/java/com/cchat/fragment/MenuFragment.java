package com.cchat.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cchat.DisplayActivity;
import com.cchat.R;
import com.cchat.AddFrientActivity;


/**
 * 菜单的Fragment，用于显示菜单界面内容，以及处理菜单界面的点击事件。
 * 
 * @author guolin
 */
public class MenuFragment extends Fragment implements OnItemClickListener, OnClickListener {

	/**
	 * 菜单界面中只包含了一个ListView。
	 */
	private ListView menuList;

	/**
	 * ListView的适配器。
	 */
	private ArrayAdapter<String> adapter;

	/**
	 * 用于填充ListView的数据，这里就简单只用了两条数据。
	 */
	private String[] menuItems = { "Sound", "Display" };

	/**
	 * 是否是双页模式。如果一个Activity中包含了两个Fragment，就是双页模式。
	 */
	private boolean isTwoPane;

	public Fragment[] fragments;

	private int currentTabIndex;

	/**
	 * 当Activity和Fragment建立关联时，初始化适配器中的数据。
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ContactFragment ContactFragment = new ContactFragment();
		ConversationsFragment conversationFragment = new ConversationsFragment();
		SettingFragment settingFragment = new SettingFragment();
		fragments = new Fragment[] { ContactFragment, conversationFragment, settingFragment };

		android.app.FragmentTransaction trx = getFragmentManager().beginTransaction();
		for (int i=0;i<fragments.length;i++){
			if (!fragments[i].isAdded()) {
				trx.add(R.id.ll_content, fragments[i]);
			}
		}
		trx.commit();
		adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, menuItems);
	}

	/**
	 * 加载menu_fragment布局文件，为ListView绑定了适配器，并设置了监听事件。
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_fragment, container, false);
		menuList = (ListView) view.findViewById(R.id.menu_list);
		menuList.setAdapter(adapter);
		menuList.setOnItemClickListener(this);
		Button btn_test_a = (Button)view.findViewById(R.id.btn_test_a);
		btn_test_a.setOnClickListener(this);
		Button btn_test_b = (Button)view.findViewById(R.id.btn_test_b);
		btn_test_b.setOnClickListener(this);
		Button btn_test_c = (Button)view.findViewById(R.id.btn_test_c);
		btn_test_c.setOnClickListener(this);


		android.app.FragmentTransaction trx = getFragmentManager().beginTransaction();
		trx.hide(fragments[currentTabIndex]);
//		if (!fragments[0].isAdded()) {
//			trx.add(R.id.ll_content, fragments[0]);
//		}
		trx.show(fragments[0]).commit();

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
				fragment = new ContactFragment();
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

	@Override
	public void onClick(View view) {
		int index = 0;
		switch (view.getId()) {
		case R.id.btn_test_a:
            index = 0;
            break;
		case R.id.btn_test_b:
            index = 1;
           
            break;
		case R.id.btn_test_c:
            index = 2;
            
            break;
		default:
			break;
		}
		if (currentTabIndex != index) {
			android.app.FragmentTransaction trx = getFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.ll_content, fragments[index]);
			}
			trx.show(fragments[index]).commit();
//			mainTitle.setText(titles[index]);
		}
//		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
//		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}
	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
	    int index = 0;
		switch (view.getId()) {
		case R.id.btn_test_a:
            index = 0;
            break;
		case R.id.btn_test_b:
            index = 1;
           
            break;
		case R.id.btn_test_c:
            index = 2;
            
            break;
		default:
			break;
		}
		if (currentTabIndex != index) {
			android.app.FragmentTransaction trx = getFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.ll_content, fragments[index]);
			}
			trx.show(fragments[index]).commit();
//			mainTitle.setText(titles[index]);
		}
//		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
//		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

}
