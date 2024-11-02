package com.example.shoeshopee_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.shoeshopee_admin.Model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ImageView imgUser;
    private TextView txtUserName, txtEmail;
    private DatabaseReference userDatabase;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        String item_nav_index = "0";
        String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
        if (fragmentToLoad == null) {
            item_nav_index = "home";
        }
        else item_nav_index = fragmentToLoad;

        switch (item_nav_index) {
            case "home":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case "product":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductFragment()).commit();
                break;
        }

        auth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        View headerView = navigationView.getHeaderView(0);
        imgUser = headerView.findViewById(R.id.imgUser);
        txtUserName = headerView.findViewById(R.id.txtUserName);
        txtEmail = headerView.findViewById(R.id.txtEmail);

        imgUser.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChangeProfileActivity.class)));

        loadUserData();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void loadUserData() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        userDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    txtUserName.setText(user.getName());
                    txtEmail.setText(user.getEmail());
                    if (user.getAvatarUrl() != null) {
                        Glide.with(MainActivity.this)
                                .load(user.getAvatarUrl())
                                .into(imgUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (id == R.id.nav_brand) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BrandFragment()).commit();
        } else if (id == R.id.nav_product) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductFragment()).commit();
        } else if (id == R.id.nav_order) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrderFragment()).commit();
        } else if (id == R.id.nav_Statistic) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StatisticFragment()).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
