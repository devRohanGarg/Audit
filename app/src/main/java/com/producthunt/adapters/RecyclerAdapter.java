package com.producthunt.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.producthunt.R;
import com.producthunt.R2;
import com.producthunt.models.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Product> products;
    private Context mContext;

    public RecyclerAdapter(Context context, ArrayList<Product> products) {
        this.mContext = context;
        this.products = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(products.get(position).getName());
        holder.comments_count.setText(String.valueOf(products.get(position).getComments_count()));
        holder.up_votes.setText(String.valueOf(products.get(position).getVotes_count()));
        Picasso.with(mContext).load(products.get(position).getThumbnail_url()).placeholder(R.drawable.ui_shadow_dark).error(R.drawable.ui_shadow_dark).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.comments_count)
        TextView comments_count;
        @BindView(R2.id.up_votes)
        TextView up_votes;
        @BindView(R2.id.thumbnail)
        ImageView thumbnail;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
