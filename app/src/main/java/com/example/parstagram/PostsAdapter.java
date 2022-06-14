package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.databinding.ItemPostBinding;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;
    public ItemPostBinding binding;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        View view = binding.getRoot();
        return new ViewHolder(view);
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


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Post currentPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
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
            binding.tvLikes.setText(post.getLikeCount());

            binding.ibHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.ibHeart.setBackgroundResource(R.drawable.ufi_heart_active);
                    post.likeCount += 1;
                    binding.tvLikes.setText(post.getLikeCount());
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