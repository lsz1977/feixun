package com.cchat.common.base;

/**
 * User: Villain
 * Date: 2015/3/12
 * Time: 1:07
 */
public abstract class ADWaitDialogListener extends ADActionListener {
    public ADWaitDialogListener(BaseActivity activity) {
        super(activity);
    }

    @Override
    public void onStart() {
        getActivity().startLoading();
    }

    @Override
    public void onFinished(boolean succeed, Object[] values) {
        getActivity().finishLoading();
        super.onFinished(succeed, values);
    }

    public void onCancel() {
        getActivity().finishLoading();
    }

    public void onTimeOut() {
        getActivity().finishLoading();
    }
}
