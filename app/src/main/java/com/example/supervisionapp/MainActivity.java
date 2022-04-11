package com.example.supervisionapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.ui.login.LoggedInUserView;
import com.example.supervisionapp.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.supervisionapp.ui.main.SectionsPagerAdapter;
import com.example.supervisionapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoggedInUserView loggedInUser = (LoggedInUserView) getIntent().getExtras().getSerializable("user");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), loggedInUser);
        ViewPager viewPager = binding.activityMainViewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        View logout = findViewById(R.id.activity_main_appBar_logout);
        logout.setClickable(true);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout(view);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppDatabase.getDatabase(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppDatabase.shutDown();
    }

    public void onClickLogout(View view) {
        LoginRepository loginRepository = LoginRepository.getInstance(null);
        loginRepository.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        // prevent users from going back in history after they logged out
        finish();
    }
}