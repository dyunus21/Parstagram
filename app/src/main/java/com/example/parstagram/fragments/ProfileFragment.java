package com.example.parstagram.fragments;

import android.util.Log;

import com.example.parstagram.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

public class ProfileFragment extends PostsFragment{
    private static final String TAG = "PostsFragment";

    @Override
    protected void queryPosts(Date time) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        if(time != null) {
            Log.i(TAG,"Endless Scroll! on");
            query.whereLessThan(Post.KEY_CREATED_AT, time);
        }
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + " username: " + post.getUser().getUsername());
                }
                adapter.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
