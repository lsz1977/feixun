package com.cchat.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cchat.ChatActivity;
import com.cchat.MainActivity;
import com.cchat.R;
import com.cchat.common.base.AlbumsAdapterWithCommonViewHolder;
import com.cchat.service.Person;
import com.cchat.utils.XmppTool;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友列表的Fragment，用于显示好友界面内容，以及处理好友界面的点击事件。
 * 
 * @author guolin
 */
public class ConversationsFragment extends Fragment implements OnItemClickListener, OnClickListener {

	/**
	 * 菜单界面中只包含了一个ListView。
	 */
	private ListView menuList;

	public ChatFragment fragment = null;

	/**
	 * ListView的适配器。
	 */
	private AlbumsAdapterWithCommonViewHolder adapter;

	/**
	 * 用于填充ListView的数据，这里就简单只用了两条数据。
	 */
	public List<Person> mContents = new ArrayList<Person>();

	/**
	 * 是否是双页模式。如果一个Activity中包含了两个Fragment，就是双页模式。
	 */
	private boolean isTwoPane;

	private Relationship relationship;
	/**
	 * 当Activity和Fragment建立关联时，初始化适配器中的数据。
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		relationship = (Relationship)activity;

		adapter = new AlbumsAdapterWithCommonViewHolder(activity, R.layout.contact_item_view, mContents);
	}

	/**
	 * 加载menu_fragment布局文件，为ListView绑定了适配器，并设置了监听事件。
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.conversation_fragment, container, false);
		menuList = (ListView) view.findViewById(R.id.lv_conversation);
		menuList.setAdapter(adapter);
		menuList.setOnItemClickListener(this);

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
		String to = mContents.get(index).getName();

		showDialog(to);
		/*if (isTwoPane) {

			fragment = new ChatFragment();
			Bundle bundle = new Bundle();
			bundle.putString("to", to);
			fragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.details_layout, fragment).commit();
		} else {
			Intent intent = null;

			intent = new Intent(getActivity(), ChatActivity.class);
			intent.putExtra("to", to);

			startActivity(intent);
		}*/
	}

	@Override
	public void onClick(View view) {
		
	}

	public void refresh(){
//		CommonUtils.showToast(getActivity(), "menuItems->" + mContents.get(0).getName());

//		mContents.size()
		adapter.notifyDataSetChanged();
	}

	public interface Relationship{
		public void agree(String jid);
		public void reqAdd(String jid);
	}

	public void setRelationship(Relationship relationship){
		this.relationship = relationship;
	}


	private void showDialog(final String jid) {

		new AlertDialog.Builder(getActivity()).setTitle("系统提示")//设置对话框标题

				.setMessage("接受好友请求？")//设置显示的内容

				.setPositiveButton("接受",new DialogInterface.OnClickListener() {//添加确定按钮

					@Override

					public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

						if (relationship!=null){
							relationship.agree(jid);
						}
					}

				}).setNegativeButton("拒绝",new DialogInterface.OnClickListener() {//添加返回按钮

			@Override

			public void onClick(DialogInterface dialog, int which) {//响应事件

			}

		}).show();//在按键响应事件中显示此对话框

	}

}
