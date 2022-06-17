package com.example.parstagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.activities.PostDetailsActivity;
import com.example.parstagram.databinding.ItemCardBinding;
import com.example.parstagram.models.Post;
import com.parse.ParseFile;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> posts;
    private ItemCardBinding item_binding;

    public ProfileAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemCardBinding.inflate(LayoutInflater.from(context), parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemCardBinding binding;
        private Post currentPost;

        public ViewHolder(@NonNull ItemCardBinding itemView) {
            super(itemView.getRoot());
            itemView.getRoot().setOnClickListener(this);
            this.binding = itemView;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra(Post.class.getSimpleName(), currentPost);
            context.startActivity(intent);
        }

        public void bind(Post post) {
            currentPost = post;
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(binding.ivPostImage);
            }
        }
    }
}
