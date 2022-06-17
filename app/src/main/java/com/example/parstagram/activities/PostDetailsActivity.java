package com.example.parstagram.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.example.parstagram.adapters.CommentsAdapter;

import com.example.parstagram.databinding.ActivityPostDetailsBinding;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class PostDetailsActivity extends AppCompatActivity {
    public static final String TAG = "PostDetailsActivity";
    private ActivityPostDetailsBinding binding;
    private Post post;
    private CommentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        post = getIntent().getParcelableExtra(Post.class.getSimpleName());
        Log.i(TAG, "In post details activity for post: " + post.getDescription());

        binding.tvUsername.setText(post.getUser().getUsername());
        String sourceString = "<b>" + post.getUser().getUsername() + "</b> " + post.getDescription();
        binding.tvCaption.setText(Html.fromHtml(sourceString));
        Glide.with(this).load(post.getImage().getUrl()).into(binding.ivImage);
        binding.tvTimestamp.setText(Post.calculateTimeAgo(post.getCreatedAt()));
        if(post.getUser().getParseFile("profileImage") != null)
            Glide.with(this).load(post.getUser().getParseFile("profileImage").getUrl()).circleCrop().into(binding.ivProfileImage);
        List<ParseUser> likedBy = post.getLikedBy();
        binding.tvLikes.setText(post.getLikeCount());
        if (post.isLikedbyCurrentUser(ParseUser.getCurrentUser())) {
            binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart_active);
        }
        else {
            binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart);
        }

        binding.ibHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Current User: " + ParseUser.getCurrentUser().getObjectId());
                if (post.isLikedbyCurrentUser(ParseUser.getCurrentUser())) {
                    binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart);
                }
                else {
                    binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart_active);
                }
                post.likePost(ParseUser.getCurrentUser());

                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null)
                            Log.e(TAG,"Error in liking post" + e);
                    }
                });
                binding.tvLikes.setText(post.getLikeCount());
            }
        });
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(this);
        binding.rvComments.setAdapter(commentsAdapter);
        binding.ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailsActivity.this, CommentActivity.class);
                intent.putExtra(Post.class.getSimpleName(), post);
                startActivity(intent);
            }
        });
        refreshComments();
        binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ProfileFragment profileFragment = new ProfileFragment(post.getUser());
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.rlContainer,profileFragment).commit();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshComments();
    }

    private void refreshComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
        query.whereEqualTo(Comment.KEY_POST,post);
        query.orderByDescending(Comment.KEY_CREATED_AT);
        query.include(Comment.KEY_AUTHOR);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error in fetching comments");
                    return;
                }
                commentsAdapter.comments.addAll(objects);
                commentsAdapter.notifyDataSetChanged();
            }
        });
    }
}