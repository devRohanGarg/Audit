package com.producthunt.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.producthunt.R;
import com.producthunt.R2;
import com.producthunt.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<Comment> comments;
    private Context mContext;

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.mContext = context;
        this.comments = comments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.body.setText(comments.get(position).getBody());
        holder.name.setText(comments.get(position).getUser().getName());
        holder.user_name.setText("@" + comments.get(position).getUser().getUser_name());
        holder.child_comments_count.setText(String.valueOf(comments.get(position).getChild_comments_count()));
        holder.up_votes.setText(String.valueOf(comments.get(position).getVotes()));
        Picasso.with(mContext).load(comments.get(position).getUser().getImage_url()).placeholder(R.drawable.ui_shadow_dark).error(R.drawable.ui_shadow_dark).into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.body)
        TextView body;
        @BindView(R2.id.child_comments_count)
        TextView child_comments_count;
        @BindView(R2.id.up_votes)
        TextView up_votes;
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.user_name)
        TextView user_name;
        @BindView(R2.id.image)
        CircularImageView profile_image;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
