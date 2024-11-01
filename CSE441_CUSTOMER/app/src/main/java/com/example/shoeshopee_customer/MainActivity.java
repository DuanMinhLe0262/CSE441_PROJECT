package com.example.shoeshopee_customer;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayout);

        String item_nav_index = "0";
        String userId = getIntent().getStringExtra("userId");

        String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
        if (fragmentToLoad == null) {
            fragmentToLoad = "0";
        }
        else item_nav_index = fragmentToLoad;

        switch (item_nav_index) {
            case "0":
                loadFragment(HomeFragment.newInstance(userId), true);
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
                break;
            case "1":
                loadFragment(SearchFragment.newInstance(userId), true);
                bottomNavigationView.setSelectedItemId(R.id.nav_search);
                break;
            case "2":
                loadFragment(CartFragment.newInstance(userId), true);
                bottomNavigationView.setSelectedItemId(R.id.nav_cart);
                break;

            case "3":
                loadFragment(ProfileFragment.newInstance(userId), true);
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                break;
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    loadFragment(HomeFragment.newInstance(userId), false);
                } else if (itemId == R.id.nav_search) {
                    loadFragment(SearchFragment.newInstance(userId), false);
                } else {
                    loadFragment(ProfileFragment.newInstance(userId), false);
                }
                return true;
            }
        });


    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(isAppInitialized){
            fragmentTransaction.add(R.id.frameLayout, fragment);
        }else{
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();
    }
}