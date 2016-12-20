package com.beloo.chipslayoumanager.sample.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beloo.chipslayoumanager.sample.entity.ChipsEntity;
import com.beloo.chipslayoumanager.sample.ui.adapter.ChipsAdapter;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;

import java.util.List;

import com.beloo.chipslayoutmanager.sample.ui.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class BottomSheetFragment extends Fragment {

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public static BottomSheetFragment newInstance() {
        Bundle args = new Bundle();
        BottomSheetFragment fragment = new BottomSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.btnShowSheet)
    void onShowSheetClicked(View view) {
        BottomSheetDialogFragment fragment = BottomSheetDialogFragment.newInstance();
        fragment.show(getChildFragmentManager(), fragment.getTag());
    }


}
