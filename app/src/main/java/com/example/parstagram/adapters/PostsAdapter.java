package com.example.parstagram.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemPostBinding;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.models.Post;
import com.example.parstagram.activities.PostDetailsActivity;
import com.example.parstagram.R;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private static final String TAG = "PostsAdapter" ;
    private static Context context;
    private List<Post> posts;
    private ItemPostBinding item_binding;


    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Post currentPost;
        public ItemPostBinding binding;

        public ViewHolder(@NonNull ItemPostBinding itemView) {
            super(itemView.getRoot());
            itemView.getRoot().setOnClickListener(this);
            this.binding = itemView;
        }

        public void bind(Post post) {
            currentPost = post;
            binding.tvUsername.setText(post.getUser().getUsername());
            String sourceString = "<b>" + post.getUser().getUsername() + "</b> " + post.getDescription();
            binding.tvDescription.setText(Html.fromHtml(sourceString));
            ParseFile image = post.getImage();
            if(image != null) {
                Glide.with(context).load(image.getUrl()).into(binding.ivImage);
            }
            binding.tvTimestamp.setText(Post.calculateTimeAgo(post.getCreatedAt()));
            if(post.getUser().getParseFile("profileImage") != null)
                Glide.with(context).load(post.getUser().getParseFile("profileImage").getUrl()).circleCrop().into(binding.ivProfileImage);
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
        public void onClick(View v) {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra(Post.class.getSimpleName(), currentPost);
            context.startActivity(intent);
        }
    }


}