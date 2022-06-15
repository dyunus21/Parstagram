package com.example.parstagram;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.PostsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Log.i(TAG, "toHomePage");
                        fragment = new PostsFragment();
                        break;
                    case R.id.action_compose:
                        Log.i(TAG, "toComposePage");
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_profile:
                        Log.i(TAG, "toProfilePage");
                        fragment = new ProfileFragment();
                        break;
                    default:
                        return true;
                }
                fragmentManager.beginTransaction().replace(R.id.rlContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);

//        queryPost();
    }


}