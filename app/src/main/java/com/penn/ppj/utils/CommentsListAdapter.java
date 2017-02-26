package com.penn.ppj.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.penn.ppj.R;
import com.penn.ppj.models.Comment;
import com.penn.ppj.models.MomentOverview;

import java.util.ArrayList;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.CommentHolder> {
    private ArrayList<Comment> comments;

    public CommentsListAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater lf = LayoutInflater.from(context);

        View view = lf.inflate(R.layout.comment_cell, parent, false);
        CommentHolder ch = new CommentHolder(view);

        return ch;
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.ivCommentAvatar.setImageResource(R.drawable.add);
        holder.tvNickname.setText(comment.getAuthorNickname());
        holder.tvContent.setText(comment.getContent());
        holder.tvCreatedTime.setText(comment.getCreatedTimeDes());
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }

        return comments.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder {
        private ImageView ivCommentAvatar;
        private TextView tvNickname;
        private TextView tvContent;
        private TextView tvCreatedTime;

        public CommentHolder(View itemView) {
            super(itemView);

            ivCommentAvatar = (ImageView) itemView.findViewById(R.id.iv_comment_avatar);

            tvNickname = (TextView) itemView.findViewById(R.id.tv_comment_nickname);

            tvContent = (TextView) itemView.findViewById(R.id.tv_comment_content);

            tvCreatedTime = (TextView) itemView.findViewById(R.id.tv_comment_created_time);
        }
    }
}
