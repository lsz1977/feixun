package com.cchat.common.base;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.cchat.R;
import com.cchat.service.Person;

import java.util.List;

public class AlbumsAdapterWithCommonViewHolder extends CommonAdapter<Person> {

    public AlbumsAdapterWithCommonViewHolder(Context context, int textViewResourceId, List<Person> albums) {
        super(context, textViewResourceId, albums);
    }

    public void convert(ViewHolder viewHolder, Person content) {
        ((TextView)(viewHolder.getView(R.id.textViewUser))).setText(content.getName());
//        ((TextView)(viewHolder.getView(R.id.tv_title))).setText(content.getTitle());

//        ImageView iv = viewHolder.getView(R.id.iv_icon);

        /*String realPath = content.getImg();
        if (!TextUtils.isEmpty(realPath)) {
            if (CommonUtils.isNetWorkConnected(mContext)) {

            }
            Picasso.with(mContext).loadIDActionListener(realPath).placeholder(R.mipmap.ic_launcher).into(iv);
        } else
            Picasso.with(mContext).load(R.mipmap.ic_launcher).into(iv);*/
    }

}