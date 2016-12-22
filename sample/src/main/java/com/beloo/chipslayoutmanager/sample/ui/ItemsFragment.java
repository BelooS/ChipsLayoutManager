package com.beloo.chipslayoutmanager.sample.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.beloo.chipslayoutmanager.sample.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class ItemsFragment extends Fragment {

    private static final String EXTRA = "data";

    @BindView(R.id.rvTest)
    RecyclerView rvTest;
    @BindView(R.id.spinnerPosition)
    Spinner spinnerPosition;
    @BindView(R.id.spinnerMoveTo)
    Spinner spinnerMoveTo;

    private RecyclerView.Adapter adapter;
    private List<String> positions;
    private List items;

    /** replace here different data sets */
    private IItemsFactory itemsFactory = new ShortChipsFactory();


    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    public ItemsFragment() {
        // Required empty public constructor
    }

    public static ItemsFragment newInstance() {
        return new ItemsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items, container, false);
    }


    @SuppressWarnings("unchecked")
    private RecyclerView.Adapter createAdapter(Bundle savedInstanceState) {

        List<String> items;
        if (savedInstanceState == null) {
//            items = itemsFactory.getFewItems();
//            items = itemsFactory.getALotOfItems();
            items = itemsFactory.getItems();
        } else {
            items = savedInstanceState.getStringArrayList(EXTRA);
        }

        adapter = itemsFactory.createAdapter(items, onRemoveListener);
        this.items = items;

        return adapter;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        adapter = createAdapter(savedInstanceState);

        ChipsLayoutManager spanLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();

        rvTest.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelOffset(R.dimen.item_space),
                getResources().getDimensionPixelOffset(R.dimen.item_space)));

        positions = new LinkedList<>();
        for (int i = 0; i< items.size(); i++) {
            positions.add(String.valueOf(i));
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        ArrayAdapter<String> spinnerAdapterMoveTo = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        spinnerPosition.setAdapter(spinnerAdapter);
        spinnerMoveTo.setAdapter(spinnerAdapterMoveTo);

        rvTest.setLayoutManager(spanLayoutManager);
        rvTest.getRecycledViewPool().setMaxRecycledViews(0, 10);
        rvTest.getRecycledViewPool().setMaxRecycledViews(1, 10);
        rvTest.setAdapter(adapter);

    }

    private OnRemoveListener onRemoveListener = new OnRemoveListener() {
        @Override
        public void onItemRemoved(int position) {
            items.remove(position);
            Log.i("activity", "delete at " + position);
            adapter.notifyItemRemoved(position);
            updateSpinners();
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA, new ArrayList<>(items));
    }

    private void updateSpinners() {
        positions = new LinkedList<>();
        for (int i = 0; i< items.size(); i++) {
            positions.add(String.valueOf(i));
        }

        int selectedPosition = Math.min(spinnerPosition.getSelectedItemPosition(), positions.size() - 1);
        int selectedMoveToPosition = Math.min(spinnerMoveTo.getSelectedItemPosition(), positions.size() -1);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        spinnerPosition.setAdapter(spinnerAdapter);
        selectedPosition = Math.min(spinnerAdapter.getCount() -1 , selectedPosition);
        spinnerPosition.setSelection(selectedPosition);

        ArrayAdapter<String> spinnerAdapterMoveTo = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        spinnerMoveTo.setAdapter(spinnerAdapterMoveTo);
        spinnerMoveTo.setSelection(selectedMoveToPosition);
    }

    @OnClick(R.id.btnRevert)
    public void onRevertClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            return;

        int positionMoveTo = spinnerMoveTo.getSelectedItemPosition();
        if (positionMoveTo == Spinner.INVALID_POSITION)
            return;

        if (position == positionMoveTo) return;

        spinnerPosition.setSelection(positionMoveTo);
        spinnerMoveTo.setSelection(position);
    }

    @OnClick(R.id.btnDelete)
    public void onDeleteClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            return;
        items.remove(position);
        Log.i("activity", "delete at " + position);
        adapter.notifyItemRemoved(position);
        updateSpinners();
    }

    @OnClick(R.id.btnMove)
    public void onMoveClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            return;

        int positionMoveTo = spinnerMoveTo.getSelectedItemPosition();
        if (positionMoveTo == Spinner.INVALID_POSITION)
            return;

        if (position == positionMoveTo) return;

        Object item = items.remove(position);
        items.add(positionMoveTo, item);

        adapter.notifyItemMoved(position, positionMoveTo);
    }

    @OnClick(R.id.btnScroll)
    public void onScrollClicked(View view) {
//        rvTest.scrollBy(0, 500);
        rvTest.scrollToPosition(spinnerPosition.getSelectedItemPosition());
    }

    @OnClick(R.id.btnInsert)
    public void onInsertClicked(View view) {
        int position = spinnerPosition.getSelectedItemPosition();
        if (position == Spinner.INVALID_POSITION)
            position = 0;
        items.add(position, itemsFactory.createOneItemForPosition(position));
        Log.i("activity", "insert at " + position);
        adapter.notifyItemInserted(position);
        updateSpinners();
    }

}
