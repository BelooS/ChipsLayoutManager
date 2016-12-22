package com.beloo.chipslayoutmanager.sample.ui;

import android.support.v7.widget.RecyclerView;

import com.beloo.chipslayoutmanager.sample.entity.ChipsEntity;

import java.util.List;

public class FewChipsFacade implements IItemsFacade<ChipsEntity> {

    private ChipsFactory chipsFactory = new ChipsFactory();

    @Override
    public List<ChipsEntity> getItems() {
        return chipsFactory.getFewItems();
    }

    @Override
    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> createAdapter(List<ChipsEntity> chipsEntities, OnRemoveListener onRemoveListener) {
        return chipsFactory.createAdapter(chipsEntities, onRemoveListener);
    }

    @Override
    public ChipsEntity createOneItemForPosition(int position) {
        return chipsFactory.createOneItemForPosition(position);
    }
}
