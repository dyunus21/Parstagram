package com.example.parstagram.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String TAG = "Post";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LIKED_BY = "likedby";

    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getLikeCount() {
        return getLikedBy().size() + " likes";
    }

    public List<ParseUser> getLikedBy() {
        List<ParseUser> likedBy = getList(KEY_LIKED_BY);
        if (likedBy == null)
            return new ArrayList<>();
        return likedBy;
    }

    public void setLikedBy(List<ParseUser> likedBy) {
        put(KEY_LIKED_BY, likedBy);
    }

    public boolean isLikedbyCurrentUser(ParseUser currentUser) {
        for (ParseUser user : getLikedBy()) {
            if (currentUser.hasSameId(user)) {
                Log.i("Post", "Post is already liked by " + currentUser.getUsername());
                return true;
            }
        }
        Log.i("Post", "Post has not been liked by " + currentUser.getUsername());
        return false;
    }

    public void likePost(ParseUser currentUser) {
        List<ParseUser> likedBy = getLikedBy();
        for (int i = 0; i < likedBy.size(); i++) {
            if (likedBy.get(i).hasSameId(currentUser)) {
                likedBy.remove(i);
                Log.i("Post", "Size: " + likedBy.size());
                setLikedBy(likedBy);
                return;
            }
        }
        likedBy.add(currentUser);
        setLikedBy(likedBy);
        return;
    }

}
