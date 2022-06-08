package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {

    public String body;
    public String createdAt;
    public User user;
    public String imageUrl;

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
        return tweet;
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


    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
