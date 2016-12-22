package com.beloo.chipslayoutmanager.sample.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import com.beloo.chipslayoutmanager.sample.ui.OnRemoveListener;
import com.beloo.chipslayoutmanager.sample.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static String TAG = RecyclerViewAdapter.class.getSimpleName();
    private int viewHolderCount;

    private final int ITEM_TYPE_DEFAULT = 0;
    private final int ITEM_TYPE_INCREASED = 1;

    private List<String> items;
    private OnRemoveListener onRemoveListener;

    public RecyclerViewAdapter(List<String> items, OnRemoveListener onRemoveListener) {
        this.items = items;
        this.onRemoveListener = onRemoveListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_TYPE_INCREASED:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_increased, parent, false);
                break;

            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, parent, false);
                break;
        }
        viewHolderCount++;
//        Timber.w(TAG, "created holders = " + viewHolderCount);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(items.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        String item = items.get(position);
        if (item.startsWith("!")) {
            return ITEM_TYPE_INCREASED;
        }

        return ITEM_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvText;
        private ImageButton ibClose;

        ViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            ibClose = (ImageButton) itemView.findViewById(R.id.ibClose);
            ibClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != -1) {
                        onRemoveListener.onItemRemoved(position);
                    }
                }
            });
        }

        void bindItem(String text) {
            tvText.setText(text);
        }
     }
}
