package com.codepath.apps.restclienttemplate;

import static com.facebook.stetho.inspector.network.PrettyPrinterDisplayType.JSON;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
// DetailFragment.OnItemSelectedListener
public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 27;
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private ActionBar tActionBar;
    private ActionBar bActionBar;
    DetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);


        // TODO: clean up code here
        // Find the toolbar view and set as ActionBar
        Toolbar topToolbar = (Toolbar) findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);
        tActionBar = getSupportActionBar();
        tActionBar.setDisplayShowTitleEnabled(false);

        Toolbar bottomToolbar = (Toolbar) findViewById(R.id.bottomToolbar);
        setSupportActionBar(bottomToolbar);
        bActionBar = getSupportActionBar();
        bActionBar.setDisplayShowTitleEnabled(false);

        // Find recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // Initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        // Recycler view setup: layout manager and adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                // TODO: fix page variable
                fetchTimelineAsync();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.twitter_blue, R.color.medium_red);
        populateHomeTimeline();

        fragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detailFragment);

    }

    // TODO: way to modularize this and combine with populateHomeTimeline?

    /**
     * Sends the network request to fetch the updated data
     * endpoint here: getHomeTimeline()
     */
    public void fetchTimelineAsync() {
        adapter.clear();
        populateHomeTimeline();
        Log.i(TAG, "refreshing timeline");
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }

//    /**
//     * Inflates the menu
//     * Adds items to the action bar if it is present.
//     *
//     * @param menu
//     * @return Menu with all menu options
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


//    /***
//     * Handle presses on the action bar items
//     * @param item - menu item
//     */
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            // Compose icon has been selected
            Toast.makeText(this, "Composing message", Toast.LENGTH_SHORT).show();
            // Navigate to the compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            // Launches child activity (compose) & sends data back to parent
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
            return super.onOptionsItemSelected(item);
        }
     */

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                // get data from the intent (tweet)
                Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
                // Update the RV with this tweet
                // Modify data source of tweets
                tweets.add(0, tweet);
                // update adapter
                adapter.notifyItemInserted(0);
                rvTweets.smoothScrollToPosition(0);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        private void populateHomeTimeline () {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "onSuccess!" + json.toString());
                    JSONArray jsonArray = json.jsonArray;
                    try {
                        tweets.addAll(Tweet.fromJsonArray(jsonArray));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "json exception", e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure!" + response, throwable);
                }
            });
        }

        /***
         * Composes tweet and refreshes timeline to show new tweet
         * @param v View passed in by onClick call in xml file
         */
        public void composeTweet (View v){
            // Compose icon has been selected
            FragmentManager fm = getSupportFragmentManager();
            ComposeFragment composeFragment = ComposeFragment.newInstance("new tweet");
            composeFragment.show(fm, "fragment_compose");
            fetchTimelineAsync();
        }

        public void logOut(View v){
//        Intent intent = new Intent(this, LoginActivity.class);
            this.finish();
            client.clearAccessToken();
//        this.startActivity(intent);
        }

    /**
     * Replaces current main fragment
     */
    public void openTweetDetail(View v){
            // Begin the transaction
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
            ft.replace(R.id.scrollingTimeline, new DetailFragment());
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
            ft.commit();
        }


        // Now we can define the action to take in the activity when the fragment event fires
        // This is implementing the `OnItemSelectedListener` interface methods
//    @Override
//    public void onRssItemSelected(String link) {
//        if (fragment != null && fragment.isInLayout()) {
//            fragment.setText(link);
//            setContentView(R.layout.fragment_detail);
//        }
//    }
}