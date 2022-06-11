package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{
    Context context;
    List<Tweet> tweets;

    // Pass in context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    /**
     * For each row, inflates the layout
     * @param parent parent View to inflate
     * @param viewType
     * @return updated View
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Bind elements to View based on position of element
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfileImage;
        TextView tvTime;
        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        ImageView ivTweetImage;
        ImageButton ibFavorite;
        TextView tvFavoriteCount;
        final int ROUNDING_RADIUS = 90;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivTweetImage = itemView.findViewById(R.id.ivTweetImage);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            tvTime.setText(tweet.relativeTimeAgo);
            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name);
            tvScreenName.setText(tweet.user.screenName);
            tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCorners(ROUNDING_RADIUS)).into(ivProfileImage);
            if (!tweet.imageUrl.equals("")){
                Glide.with(context).load(tweet.imageUrl).transform(new RoundedCorners(ROUNDING_RADIUS)).into(ivTweetImage);
                ivTweetImage.setVisibility(View.VISIBLE);
            }
            else{
                ivTweetImage.setVisibility(View.GONE);
            }

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TwitterClient client = TwitterApp.getRestClient(context);
                    // If not already favorited
                    if(!tweet.isFavorited()){
                        // Tell Twitter I want to favorite this
                        client.favoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                // Change drawable to filled in heart
                                Drawable newHeart = context.getDrawable(R.drawable.ic_vector_heart);
                                ibFavorite.setImageDrawable(newHeart);
                                tweet.isFavorited = true;
                                // increment the text inside tvFavoriteCount
                                tweet.favoriteCount++;
=                                tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                                Log.i("FavoriteTweet", "favorited onSuccess");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("FavoriteTweet", "favorited onFailure", throwable);
                            }
                        });
                    }else{ // else, if already favorited, unfavorite
                        client.unfavoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Drawable newHeart = context.getDrawable(R.drawable.ic_vector_heart_stroke);
                                ibFavorite.setImageDrawable(newHeart);
                                tweet.isFavorited = false;
                                tweet.favoriteCount--;
                                tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                                Log.i("UnfavoriteTweet", "unfavorited onSuccess");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("UnfavoriteTweet", "favorited onFailure", throwable);
                            }
                        });
                    }
                    Log.i("FavoriteTweet", "done");
                }
            });

        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                TimelineActivity.openTweetDetail(tweet);
            }
        }
    }

   /* Methods for pull-down-to-refresh */

    /**
     * Clean all elements of the recycler
     */
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    /**
     * Add a list of items
     */
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}
