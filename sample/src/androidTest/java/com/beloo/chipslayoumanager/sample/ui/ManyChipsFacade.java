package com.beloo.chipslayoumanager.sample.ui;

import android.support.v7.widget.RecyclerView;

import com.beloo.chipslayoumanager.sample.entity.ChipsEntity;

import java.util.List;

public class ManyChipsFacade implements IItemsFacade<ChipsEntity> {

    private ChipsFactory chipsFactory = new ChipsFactory();

    @Override
    public List<ChipsEntity> getItems() {
        return chipsFactory.getALotOfItems();
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
