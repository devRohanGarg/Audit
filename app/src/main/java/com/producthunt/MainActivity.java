package com.producthunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.producthunt.adapters.RecyclerAdapter;
import com.producthunt.models.Product;
import com.producthunt.utils.AppController;
import com.producthunt.utils.GridAutoFitLayoutManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AppCompatButton mLoginButton;
    TextView mNameTextView;
    TextView mEmailTextView;
    private String TAG = "MainActivity";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TwitterAuthClient mTwitterAuthClient;

    private ArrayList<Product> products;
    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));

        // Inflate layout (must be done after Twitter is configured)
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = ButterKnife.findById(this, R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = ButterKnife.findById(this, R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        mLoginButton = ButterKnife.findById(navigationView.getHeaderView(0), R.id.login_button);
        mNameTextView = ButterKnife.findById(navigationView.getHeaderView(0), R.id.name);
        mEmailTextView = ButterKnife.findById(navigationView.getHeaderView(0), R.id.email);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        products = new ArrayList<>();
        setupRecyclerView();
        fetch();
    }

    private void fetch() {
        String finalURL = getResources().getString(R.string.producthunt_base_url) + "posts?" + "&access_token=" + getResources().getString(R.string.producthunt_access_token);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(finalURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("posts");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Product product = new Product();
                                product.setName(jsonObject.getString("name"));
                                product.setTag_line(jsonObject.getString("tagline"));
                                product.setDay(jsonObject.getString("day"));
                                product.setDiscussion_url(jsonObject.getString("discussion_url"));
                                product.setRedirect_url(jsonObject.getString("redirect_url"));
                                product.setScreenshot_url(jsonObject.getJSONObject("screenshot_url").getString("300px"));
                                product.setThumbnail_url(jsonObject.getJSONObject("thumbnail").getString("image_url"));
                                product.setId(jsonObject.getLong("id"));
                                product.setComments_count(jsonObject.getLong("comments_count"));
                                product.setVotes_count(jsonObject.getLong("votes_count"));
                                products.add(product);
                            }
                            recyclerAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d(TAG, "Error: " + error.getMessage());
                        Snackbar.make(ButterKnife.findById(MainActivity.this, R.id.drawer_layout), "Aw, Snap! Something went wrong", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "JSON_OBJECT_REQUEST");
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = ButterKnife.findById(this, R.id.product_list);
        GridAutoFitLayoutManager layoutManager = new GridAutoFitLayoutManager(this, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 152, getApplicationContext().getResources().getDisplayMetrics()));
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(this, products);
        recyclerView.setAdapter(recyclerAdapter);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }// [END on_start_check_user]

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the Twitter login button.
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]

    // [START auth_with_twitter]
    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_twitter]

    void login() {
        // [START initialize_twitter_login]
        mTwitterAuthClient = new TwitterAuthClient();
        mTwitterAuthClient.authorize(MainActivity.this, new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(final TwitterException e) {
                Log.w(TAG, "twitterLogin:failure", e);
                updateUI(null);
            }
        });
        // [END initialize_twitter_login]
    }

    private void signOut() {
        mAuth.signOut();
        Twitter.logOut();

        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        NavigationView navigationView = ButterKnife.findById(this, R.id.nav_view);
        Menu menu = navigationView.getMenu();
        if (user != null) {
            Log.d(TAG, user.getDisplayName());
            Log.d(TAG, user.getEmail());
            Log.d(TAG, String.valueOf(user.getPhotoUrl()));

            mLoginButton.setVisibility(View.GONE);

            menu.findItem(R.id.nav_logout).setVisible(true);

            mNameTextView.setText(user.getDisplayName());
            mEmailTextView.setText(user.getEmail());

            mNameTextView.setVisibility(View.VISIBLE);
            mEmailTextView.setVisibility(View.VISIBLE);
        } else {
            mNameTextView.setVisibility(View.GONE);
            mEmailTextView.setVisibility(View.GONE);

            mLoginButton.setVisibility(View.VISIBLE);
            menu.findItem(R.id.nav_logout).setVisible(false);
        }
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        Snackbar.make(view, "Made with Love", Snackbar.LENGTH_LONG).setAction("", null).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = ButterKnife.findById(this, R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = ButterKnife.findById(this, R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
