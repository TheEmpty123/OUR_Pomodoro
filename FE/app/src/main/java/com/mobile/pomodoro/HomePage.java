package com.mobile.pomodoro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomePage extends AppCompatActivity {
    private TextView timerText;
    private TextView currentTaskText;
    private FloatingActionButton btnPlayPause;
    private ImageButton btnReset;
    private ImageButton btnSkip;
    private Button btnFocus;
    private Button btnShortBreak;
    private Button btnLongBreak;
    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        timerText = findViewById(R.id.timerText);
        currentTaskText = findViewById(R.id.currentTaskText);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnReset = findViewById(R.id.btnReset);
        btnSkip = findViewById(R.id.btnSkip);
        btnFocus = findViewById(R.id.btnFocus);
        btnShortBreak = findViewById(R.id.btnShortBreak);
        btnLongBreak = findViewById(R.id.btnLongBreak);
        bottomNavView = findViewById(R.id.bottomNavigation);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavView.setOnItemSelectedListener(item -> {
            // Xử lý chuyển đổi giữa các tab
            switch (item.getItemId()) {
//                case R.id.page_home:
//                    return true;
//                case R.id.page_todo:
//                    return true;
//                case R.id.page_task:
//                    return true;
//                case R.id.page_calendar:
//                    return true;
//                case R.id.page_setting:
//                    return true;
            }
            return false;
        });
    }
}