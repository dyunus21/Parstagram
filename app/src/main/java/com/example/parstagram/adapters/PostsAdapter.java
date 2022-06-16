package com.example.parstagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.models.Post;
import com.example.parstagram.activities.PostDetailsActivity;
import com.example.parstagram.R;
import com.example.parstagram.databinding.ItemPostBinding;
import com.parse.ParseFile;
import com.parse.ParseUser;

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
            List<ParseUser> likedBy = post.getLikedBy();
            binding.tvLikes.setText(post.getLikeCount());
            if (isLikedbyCurrentUser()) {
                binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart_active);
            }
            else {
                binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart);
            }

            binding.ibHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isLikedbyCurrentUser()) {
                        likedBy.remove(ParseUser.getCurrentUser());
                        post.setLikedBy(likedBy);
                        binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart);
                    }
                    else {
                        likedBy.add(ParseUser.getCurrentUser());
                        post.setLikedBy(likedBy);
                        binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart_active);
                    }
                    post.setLikecount(likedBy.size());
                    post.saveInBackground();
                    binding.tvLikes.setText(post.getLikeCount());
                }
            });
            binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG,"Go to profile page");

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