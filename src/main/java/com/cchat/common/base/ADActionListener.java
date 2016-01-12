package com.cchat.common.base;

import com.cchat.utils.CommonUtils;

/**
 * User: Villain
 * Date: 2015/3/12
 * Time: 13:36
 */
public abstract class ADActionListener implements IDActionListener {
    private BaseActivity mActivity;

    public ADActionListener(BaseActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFinished(boolean succeed, Object[] values) {
        if (succeed) {
            onSucceed(values);
        } else {
            onFailed(/*(HttpLogicResult) values[0]*/);
        }
    }

    public BaseActivity getActivity() {
        return mActivity;
    }


    protected abstract void onSucceed(Object[] values);

    protected void onFailed(/*HttpLogicResult result*/) {
       /* String status = "出错了，请稍后再尝试";
        if (result != null) {
            status = getError(result.getErrorValue());
        }
        if (!mActivity.isLogin())
            return;*/
//        mActivity.showToast(status);
        CommonUtils.showToast(mActivity, "error");
    }

}
