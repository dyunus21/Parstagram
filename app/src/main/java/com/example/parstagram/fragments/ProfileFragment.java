package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parstagram.models.Post;
import com.example.parstagram.R;
import com.example.parstagram.adapters.ProfileAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProfileFragment extends Fragment {

    private RecyclerView rvPosts;
    protected ProfileAdapter adapter;
    protected List<Post> allPosts;
    private TextView tvUsername;
    private TextView tvName;
    private ImageView ivProfileImage;
    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        rvPosts.setLayoutManager(gridLayoutManager);
        Log.i(TAG, gridLayoutManager.toString());
        tvUsername = view.findViewById(R.id.tvUsername);
        tvName = view.findViewById(R.id.tvName);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        Log.i(TAG, "Name: " +ParseUser.getCurrentUser().getString("name"));
        tvName.setText(ParseUser.getCurrentUser().getString("name"));
        queryPosts(null);

    }

    protected void queryPosts(Date time) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
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
                rvPosts.scrollToPosition(0);
            }
        });
    }
}