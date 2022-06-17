package com.example.parstagram.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parstagram.databinding.ActivityCommentBinding;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private ActivityCommentBinding binding;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        post = getIntent().getParcelableExtra(Post.class.getSimpleName());
        Toast.makeText(this, post.getDescription(), Toast.LENGTH_SHORT).show();
        binding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
    }

    private void postComment() {
        String body = binding.etBody.getText().toString();
        Comment comment = new Comment();
        comment.setAuthor(post.getUser());
        comment.setBody(body);
        comment.setPost(post);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with saving comment! " + e.getMessage());
                    return;
                }
                finish();
            }
        });
    }
}