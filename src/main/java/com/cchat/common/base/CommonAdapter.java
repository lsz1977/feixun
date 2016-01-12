package com.cchat.common.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by holand on 2015/11/20.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected int mResourceId;
    public CommonAdapter(Context context, int resourceId, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
        this.mInflater = LayoutInflater.from(context);
        this.mResourceId = resourceId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.get(mContext, convertView, parent,
                mResourceId, position);
        convert(viewHolder, getItem(position));
        return viewHolder.getmConvertView();
    }

    public abstract void convert(ViewHolder viewHolder, T t);
}
