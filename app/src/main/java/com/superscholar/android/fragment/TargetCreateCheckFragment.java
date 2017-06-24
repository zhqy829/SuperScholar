package com.superscholar.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.superscholar.android.R;

/**
 * Created by zhqy on 2017/6/24.
 */

public class TargetCreateCheckFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.target_create_check,container,false);

        return view;
    }
}
