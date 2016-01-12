package com.cchat.common.base.service.base.android;

import android.os.Parcel;
import android.os.Parcelable;

import com.cchat.utils.SAndroidUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: SEAMAN
 * Date: 2015/3/9
 * Time: 10:44
 */
public class ShareDataPack implements Parcelable {
    private static final int TYPE_SERIALIZABLE = 0;
    private static final int TYPE_PARCELABLE = 1;
    private static final int TYPE_ARRAY_LIST = 2;

    private Object[] mValues;
    public static final Creator<ShareDataPack> CREATOR = new Creator<ShareDataPack>() {
        public ShareDataPack createFromParcel(Parcel source) {
            int len = source.readInt();
            Object[] values = new Object[len];
            for (int i = 0; i < len; ++i) {
                int type = source.readInt();
                if (type == TYPE_SERIALIZABLE) {
                    values[i] = source.readSerializable();
                } else if (type == TYPE_PARCELABLE) {
                    values[i] = source.readParcelable(SAndroidUtil.getClassLoader());
                } else if (type == TYPE_ARRAY_LIST) {
                    List list = new ArrayList();
                    source.readList(list, SAndroidUtil.getClassLoader());
                    values[i] = list;
                }
            }
            return new ShareDataPack(values);
        }

        public ShareDataPack[] newArray(int size) {
            return new ShareDataPack[size];
        }
    };

    public ShareDataPack(Object... values) {
        this.mValues = values;
    }

    public Object getValue() {
        return this.mValues[0];
    }

    public Object getValue(int index) {
        return mValues[index];
    }

    public Object[] getValues() {
        return mValues;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mValues.length);
        for (Object o : mValues) {
            if (o instanceof ArrayList) {
                dest.writeInt(TYPE_ARRAY_LIST);
                dest.writeList((List) o);
            } else if (o instanceof Serializable) {
                dest.writeInt(TYPE_SERIALIZABLE);
                dest.writeSerializable((Serializable) o);
            } else if (o instanceof Parcelable) {
                dest.writeInt(TYPE_PARCELABLE);
                dest.writeParcelable((Parcelable) o, flags);
            }
        }
    }
}
