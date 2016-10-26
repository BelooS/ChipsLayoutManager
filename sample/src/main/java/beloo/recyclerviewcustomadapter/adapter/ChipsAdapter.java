package beloo.recyclerviewcustomadapter.adapter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import beloo.recyclerviewcustomadapter.OnRemoveListener;
import beloo.recyclerviewcustomadapter.R;
import beloo.recyclerviewcustomadapter.entity.ChipsEntity;
import beloo.recyclerviewcustomadapter.util.CircleTransform;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class ChipsAdapter extends  RecyclerView.Adapter<ChipsAdapter.ViewHolder> {

    private List<ChipsEntity> chipsEntities;
    private OnRemoveListener onRemoveListener;

    public ChipsAdapter(List<ChipsEntity> chipsEntities, OnRemoveListener onRemoveListener) {
        this.chipsEntities = chipsEntities;
        this.onRemoveListener = onRemoveListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chip, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(chipsEntities.get(position));
    }

    @Override
    public int getItemCount() {
        return chipsEntities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDescription;
        private ImageView ivPhoto;
        private TextView tvName;
        private ImageButton ibClose;

        ViewHolder(View itemView) {
            super(itemView);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ibClose = (ImageButton) itemView.findViewById(R.id.ibClose);
        }

        void bindItem(ChipsEntity entity) {
            if (TextUtils.isEmpty(entity.getDescription())) {
                tvDescription.setVisibility(View.GONE);
            } else {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(entity.getDescription());
            }

            Glide.with(ivPhoto.getContext()).load(entity.getDrawableResId())
                    .transform(new CircleTransform(ivPhoto.getContext())).into(ivPhoto);

            tvName.setText(entity.getName());

            ibClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != -1) {
                        onRemoveListener.onItemRemoved(getAdapterPosition());
                    }
                }
            });
        }
    }

}
