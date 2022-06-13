package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("tsbTun3egsM7QmBHueFFi5zx7pGjUE8xaIXpaNSo")
                .clientKey("oAdLuKSSpkWWOj7xVXSXNOV8lwmSEs8CYUJjJuc5")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
