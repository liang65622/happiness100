package com.happiness100.app.ui.fragment;/**
 * Created by Administrator on 2016/8/15.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.happiness100.app.App;

/**
 * 作者：justin on 2016/8/15 17:26
 */
public class BaseFragment extends Fragment {
    App mApplication;
    Context mContext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (App) getActivity().getApplication();
        mContext = getContext();
    }



}
