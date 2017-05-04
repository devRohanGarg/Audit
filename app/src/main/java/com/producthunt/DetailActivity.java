package com.producthunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.producthunt.adapters.CommentAdapter;
import com.producthunt.models.Comment;
import com.producthunt.models.Product;
import com.producthunt.models.User;
import com.producthunt.utils.AppController;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends BaseActivity {

    private String TAG = "DetailActivity";

    private ArrayList<Comment> comments;
    private CommentAdapter productAdapter;
    private long ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Product product = intent.getParcelableExtra("product");
        ID = product.getId();

        ImageView image = ButterKnife.findById(this, R.id.screenshot);
        Picasso.with(this).load(product.getScreenshot_url()).placeholder(R.drawable.ui_shadow_dark).error(R.drawable.ui_shadow_dark).into(image);

        TextView tag = ButterKnife.findById(this, R.id.tag);
        tag.setText(product.getTag_line());

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(product.getName());

        comments = new ArrayList<>();
        setupRecyclerView();
        fetch();
    }

    private void fetch() {
        String finalURL = getResources().getString(R.string.producthunt_base_url) + "comments?" + "&search[post_id]=" + ID + "&access_token=" + getResources().getString(R.string.producthunt_access_token);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(finalURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("comments");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Comment comment = new Comment();
                                comment.setId(jsonObject.getLong("id"));
                                comment.setChild_comments_count(jsonObject.getLong("child_comments_count"));
                                comment.setVotes(jsonObject.getLong("votes"));
                                comment.setBody(jsonObject.getString("body"));

                                User user = new User();
                                user.setName(jsonObject.getJSONObject("user").getString("name"));
                                user.setUser_name(jsonObject.getJSONObject("user").getString("username"));
                                user.setProfile_url(jsonObject.getJSONObject("user").getString("profile_url"));
                                user.setImage_url(jsonObject.getJSONObject("user").getJSONObject("image_url").getString("original"));
                                user.setId(jsonObject.getJSONObject("user").getLong("id"));

                                comment.setUser(user);
                                comments.add(comment);
                            }
                            productAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d(TAG, "Error: " + error.getMessage());
                        Snackbar.make(ButterKnife.findById(DetailActivity.this, R.id.coordinator_layout), "Aw, Snap! Something went wrong", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "JSON_OBJECT_REQUEST");
    }

    @OnClick(R2.id.fab)
    public void onClick(View view) {
        Snackbar.make(view, "Up-voted. Thanks for your love!", Snackbar.LENGTH_LONG).setAction("", null).show();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = ButterKnife.findById(this, R.id.comment_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        productAdapter = new CommentAdapter(this, comments);
        recyclerView.setAdapter(productAdapter);
    }
}
