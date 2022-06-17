package com.example.parstagram.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.example.parstagram.adapters.ProfileAdapter;
import com.example.parstagram.models.BitmapScaler;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final String TAG = "ProfileFragment";
    private final ParseUser user;
    public String photoFileName = "photo.jpg";
    protected ProfileAdapter adapter;
    protected List<Post> allPosts;
    File photoFile;
    private RecyclerView rvPosts;
    private TextView tvUsername;
    private TextView tvName;
    private ImageView ivProfileImage;
    private Button btnEditProfileImage;
    private TextView tvPostCount;

    public ProfileFragment() {
        user = ParseUser.getCurrentUser();
    }

    public ProfileFragment(ParseUser user) {
        this.user = user;
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        rvPosts.setLayoutManager(gridLayoutManager);
        Log.i(TAG, gridLayoutManager.toString());
        tvUsername = view.findViewById(R.id.tvUsername);
        tvName = view.findViewById(R.id.tvName);
        tvUsername.setText(user.getUsername());
        Log.i(TAG, "Name: " + user.getString("name"));
        tvName.setText(user.getString("name"));
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        if (user.getParseFile("profileImage") != null)
            Glide.with(getContext()).load(user.getParseFile("profileImage").getUrl()).circleCrop().into(ivProfileImage);
        btnEditProfileImage = (Button) view.findViewById(R.id.btnEditProfileImage);
        if (Objects.equals(user.getObjectId(), ParseUser.getCurrentUser().getObjectId())) {
            btnEditProfileImage.setVisibility(View.VISIBLE);
        } else {
            Log.i(TAG, "User: " + user.getObjectId() + " currUser: " + ParseUser.getCurrentUser().getObjectId());
            btnEditProfileImage.setVisibility(View.GONE);
        }
        btnEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        tvPostCount = view.findViewById(R.id.tvPostCount);
        queryPosts(null);

    }

    protected void queryPosts(Date time) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo(Post.KEY_USER, user);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_LIKED_BY);
        query.setLimit(20);
        if (time != null) {
            Log.i(TAG, "Endless Scroll! on");
            query.whereLessThan(Post.KEY_CREATED_AT, time);
        }
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + " username: " + post.getUser().getUsername());
                }
                tvPostCount.setText(String.valueOf(posts.size()));
                adapter.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                rvPosts.scrollToPosition(0);
            }
        });
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 800);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                File resizedFile = getPhotoFileUri(photoFileName);
                try {
                    resizedFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(resizedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                photoFile = resizedFile;
                user.put("profileImage", new ParseFile(photoFile));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error in saving profile image!");
                        }
                        ivProfileImage.setImageBitmap(resizedBitmap);
                        Log.i(TAG, "Successfully changed profile image!");
                    }
                });

            } else {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}