package com.beloo.chipslayoumanager.sample.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;

import java.util.List;

import com.beloo.chipslayoutmanager.sample.ui.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomSheetDialogFragment extends android.support.design.widget.BottomSheetDialogFragment {

    @BindView(R.id.rvBottomSheet)
    RecyclerView rvBottomSheet;

    private IItemsFactory itemsFactory = new ChipsFactory();
    private RecyclerView.Adapter adapter;

    public static BottomSheetDialogFragment newInstance() {
        Bundle args = new Bundle();
        BottomSheetDialogFragment fragment = new BottomSheetDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressWarnings("unchecked")
    private RecyclerView.Adapter createAdapter() {

//        List<String> items = itemsFactory.getDoubleItems();
        List<String> items = itemsFactory.getItems();

        adapter = itemsFactory.createAdapter(items, null);
        return adapter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ChipsLayoutManager layoutManager = ChipsLayoutManager.newBuilder(getContext()).build();
        rvBottomSheet.setLayoutManager(layoutManager);
        rvBottomSheet.setAdapter(createAdapter());
        rvBottomSheet.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelOffset(R.dimen.item_space),
                getResources().getDimensionPixelOffset(R.dimen.item_space)));
    }
}
