package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

//public class DetailFragment extends Fragment {
//    // ...
//    // Define the listener of the interface type
//    // listener will the activity instance containing fragment
//    private OnItemSelectedListener listener;
//
//    public void setText(String link) {
//    }
//
//    // Define the events that the fragment will use to communicate
//    public interface OnItemSelectedListener {
//        // This can be any number of events to be sent to the activity
//        public void onRssItemSelected(String link);
//    }
//
//    // Store the listener (activity) that will have events fired once the fragment is attached
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnItemSelectedListener) {
//            listener = (OnItemSelectedListener) context;
//        } else {
//            throw new ClassCastException(context.toString()
//                    + " must implement MyListFragment.OnItemSelectedListener");
//        }
//    }
//
//    // Now we can fire the event when the user selects something in the fragment
//    public void onSomeClick(View v) {
//        listener.onRssItemSelected("some link");
//    }
//}


import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

public class DetailFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.


    public static DetailFragment newInstance(Tweet tweet ) {
        DetailFragment fragmentDemo = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("strName", tweet.getUser().getName());
        args.putString("strScreenName", tweet.getUser().getScreenName());
        args.putString("strDetailProfileUrl", tweet.getUser().getProfileImageUrl());
        args.putString("strDetailImageUrl", tweet.getImageUrl());
        args.putString("strBody", tweet.getBody());
        args.putString("strCreatedAt", tweet.getCreatedAt());
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_detail, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        super.onViewCreated(view, savedInstanceState);

        TextView tvDetailTime = view.findViewById(R.id.tvDetailTime);
        ImageView ivDetailProfileImage = view.findViewById(R.id.ivDetailProfileImage);
        TextView tvDetailName = view.findViewById(R.id.tvDetailName);
        TextView tvDetailScreenName = view.findViewById(R.id.tvDetailScreenName);
        TextView tvDetailBody = view.findViewById(R.id.tvDetailBody);
        ImageView tvDetailTweetImage = view.findViewById(R.id.ivDetailTweetImage);

        String strName = getArguments().getString("strName", "First Last");
        String strScreenName = getArguments().getString("strScreenName", "@user");
        String strDetailProfileImage = getArguments().getString("strDetailProfileUrl", "");
        String strBody = getArguments().getString("strBody", "");
        String strDetailImageUrl = getArguments().getString("strDetailImageUrl", "");
        String strCreatedAt = getArguments().getString("strCreatedAt", "Jan 1");

        tvDetailName.setText(strName);
        tvDetailScreenName.setText(strScreenName);
        Glide.with(view).load(strDetailProfileImage).into(ivDetailProfileImage);
        tvDetailBody.setText(strBody);
        Glide.with(view).load(strDetailImageUrl).into(tvDetailTweetImage);
        tvDetailTime.setText(strCreatedAt);
    }
}