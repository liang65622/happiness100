package com.happiness100.app.ui.fragment;/**
 * Created by Administrator on 2016/8/23.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happiness100.app.R;

import butterknife.ButterKnife;

/**
 * 作者：justin on 2016/8/23 09:25
 */
public class FamilyInfoFragment extends  BaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_family_info, container, false);
        ButterKnife.bind(this, v);
        return v;
    }
}
