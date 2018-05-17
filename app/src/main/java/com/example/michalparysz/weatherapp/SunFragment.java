package com.example.michalparysz.weatherapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by michalparysz on 27.04.2018.
 */

public class SunFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sun_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.moonButton})
    public void onClickMoonButton(View view) {
        ((MainActivity) getActivity()).setViewPager(0);

    }
    @OnClick({R.id.sunButton})
    public void onClickSunButton(View view) {
        ((MainActivity) getActivity()).setViewPager(1);
    }
}
