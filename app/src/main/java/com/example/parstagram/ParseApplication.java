package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("tsbTun3egsM7QmBHueFFi5zx7pGjUE8xaIXpaNSo")
                .clientKey("oAdLuKSSpkWWOj7xVXSXNOV8lwmSEs8CYUJjJuc5")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
