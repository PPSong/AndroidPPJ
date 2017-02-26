package com.penn.ppj.utils;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.penn.ppj.R;
import com.penn.ppj.models.MomentOverview;
import com.squareup.picasso.Picasso;

import static com.penn.ppj.R.id.imageView;

public class MomentsListAdapter extends RecyclerView.Adapter<MomentsListAdapter.MomentOverviewHolder> {
    private Context context;
    private RecyclerViewClickListener rvClickListener;

    public MomentsListAdapter(RecyclerViewClickListener rvClickListener) {
        this.rvClickListener = rvClickListener;
    }

    @Override
    public MomentOverviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater lf = LayoutInflater.from(context);

        View view = lf.inflate(R.layout.moment_overview_cell, parent, false);
        MomentOverviewHolder moh = new MomentOverviewHolder(view);

        return moh;
    }

    @Override
    public void onBindViewHolder(MomentOverviewHolder holder, int position) {
        Log.d("P", position + "st");
        MomentOverview mo = PPData.getInstance().getTotalMoments(position);

        Picasso.with(context)
                .load(mo.getOverviewImage())
                .placeholder(R.drawable.logo)
                .error(R.drawable.error)
                .into(holder.ivImage);

        holder.tvContent.setText(mo.getContent());
        holder.tvCommentsCount.setText(mo.getCommentsCountStr());
        holder.tvReadCount.setText(mo.getReadCountStr());
        holder.tvPlaceName.setText(mo.getPlaceName());
        holder.tvCreatedTime.setText(mo.getCreatedTimeDes());
        holder.tvTag.setText(mo.getTag());
    }

    @Override
    public int getItemCount() {
        if (PPData.getInstance() == null) {
            return 0;
        }

        return PPData.getInstance().getTotalMomentsCount();
    }
    class MomentOverviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivImage;
        private TextView tvContent;
        private TextView tvCommentsCount;
        private TextView tvReadCount;
        private TextView tvPlaceName;
        private TextView tvCreatedTime;
        private TextView tvTag;

        public MomentOverviewHolder(View itemView) {
            super(itemView);

            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);

            tvContent = (TextView) itemView.findViewById(R.id.tv_content);

            tvCommentsCount = (TextView) itemView.findViewById(R.id.tv_comments_count);

            tvReadCount = (TextView) itemView.findViewById(R.id.tv_read_count);

            tvPlaceName = (TextView) itemView.findViewById(R.id.tv_place_name);

            tvCreatedTime = (TextView) itemView.findViewById(R.id.tv_created_time);

            tvTag = (TextView) itemView.findViewById(R.id.tv_tag);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.v("P", "abc");
            rvClickListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }
}
