package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    public String body;
    public String createdAt;
    public User user;

    public String imageUrl;
    public String relativeTimeAgo;
    public boolean isFavorited;
    public Integer favoriteCount;

    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.imageUrl = getImageUrl(jsonObject);
        tweet.relativeTimeAgo = getRelativeTimeAgo(tweet.createdAt);
        tweet.isFavorited = jsonObject.getBoolean("favorited");
        tweet.favoriteCount= jsonObject.getInt("favorite_count");
        return tweet;
    }


    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            Tweet newTweet = fromJson(jsonArray.getJSONObject(i));
            if (newTweet != null){ // skip retweets
                tweets.add(newTweet);
            }
        }
        return tweets;
    }

    /**
     * Queries and returns first url image for each Tweet from Twitter extended entities object
     * @param jsonObject JSONObject passed in from fromJson()
     * @return String url for image
     */
    public static String getImageUrl(JSONObject jsonObject){
        String url = "";
        try {
            JSONObject firstEntity = (JSONObject) jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0);
            url = (String) firstEntity.getString("media_url_https");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Parses a relative twitter date
     * @author nesquena
     * @param rawJsonDate
     * @return
     */
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString()
                    .replace( "hours" , "h" )
                    .replace( "In" , "") // TODO: better way to do this?
                    .replace( "minutes" , "m" );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getRelativeTimeAgo() {
        return relativeTimeAgo;
    }

    public boolean isFavorited() {return isFavorited;};

    public Integer getFavoriteCount(){return favoriteCount;};

}
