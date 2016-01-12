package com.cchat.common.base;

/**
 * User: Villain
 * Date: 2015/3/12
 * Time: 1:17
 */
public class DAction {
    private int mProtocol;
    private Object[] mValues;

    public DAction(int protocol, Object... values) {
        mProtocol = protocol;
        mValues = values;
    }

    public int getProtocol() {
        return mProtocol;
    }

    public Object[] getValues() {
        return mValues;
    }

    public Object get(int index) {
        return mValues[index];
    }

    public String getString(int index) {
        return (String) get(index);
    }
}
