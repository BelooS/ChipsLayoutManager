package beloo.recyclerviewcustomadapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static String TAG = RecyclerViewAdapter.class.getSimpleName();
    private int viewHolderCount;

    private List<String> items;

    RecyclerViewAdapter(List<String> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, parent, false);
        viewHolderCount++;
        Log.w(TAG, "created holders = " + viewHolderCount);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(items.get(position));
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
                        items.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            });
        }

        void bindItem(String text) {
            tvText.setText(text);
        }
     }
}
