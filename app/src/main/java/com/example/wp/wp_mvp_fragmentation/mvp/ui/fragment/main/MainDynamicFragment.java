package com.example.wp.wp_mvp_fragmentation.mvp.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wp.wp_mvp_fragmentation.R;
import com.example.wp.wp_mvp_fragmentation.app.base.MySupportFragment;
import com.jess.arms.di.component.AppComponent;

/**
 * 动态模块
 */
public class MainDynamicFragment extends MySupportFragment{
    public static MainDynamicFragment newInstance() {

        Bundle args = new Bundle();

        MainDynamicFragment fragment = new MainDynamicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dynamic,null);
        return v;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void setData(@Nullable Object data) {

    }
}
