package com.cchat.common.base;

/**
 * User: Villain
 * Date: 2015/3/12
 * Time: 1:21
 */
public interface IDActionListener {
    void onStart();

    void onFinished(boolean succeed, Object... values);
}
