package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ActivityPostDetailsBinding;


public class PostDetailsActivity extends AppCompatActivity {
    public static final String TAG = "PostDetailsActivity";
    private ActivityPostDetailsBinding binding;
    private Post post;

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
        binding.tvLikes.setText(post.getLikeCount() + " likes");

        binding.ibHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart_active);
                post.setLikecount(1);
                binding.tvLikes.setText(post.getLikeCount() + " likes");
            }
        });
    }
}