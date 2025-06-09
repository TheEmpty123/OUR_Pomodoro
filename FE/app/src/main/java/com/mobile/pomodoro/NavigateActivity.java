package com.mobile.pomodoro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class NavigateActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            setupBottomNavigation();
        }
    }

    // các lớp con phải override 2 phương thức này, cung cấp id của activity đó
    protected abstract int getLayoutResourceId();

    protected abstract int getCurrentMenuItemId();

    private void setupBottomNavigation() {
        // item hiện tại
        bottomNavigationView.setSelectedItemId(getCurrentMenuItemId());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == getCurrentMenuItemId()) {
                return true;
            }

            // Chuyển đến các màn hình khác
            if (itemId == R.id.page_home) {
                startActivity(new Intent(this, HomePage.class));
                finish();
                return true;
            } else if (itemId == R.id.page_todo) {
                startActivity(new Intent(this, TodoActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.page_plan) {
                startActivity(new Intent(this, PlanActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.page_calendar) {
                startActivity(new Intent(this, DailyTaskActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.page_setting) {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
