package com.example.parstagram.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parstagram.R;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "CommentActivity";
    private Post post;
    private EditText etBody;
    private Button btnComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        post = getIntent().getParcelableExtra(Post.class.getSimpleName());
        Toast.makeText(this, post.getDescription(), Toast.LENGTH_SHORT).show();

        etBody = findViewById(R.id.etBody);
        btnComment = (Button) findViewById(R.id.btnComment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
    }

    private void postComment() {
        String body = etBody.getText().toString();

        Comment comment = new Comment();
        comment.setAuthor(post.getUser());
        comment.setBody(body);
        comment.setPost(post);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG,"Error with saving comment! " + e.getMessage());
                }
                finish();
            }
        });

    }
}