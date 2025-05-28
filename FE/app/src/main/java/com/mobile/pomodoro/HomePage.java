package com.mobile.pomodoro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomePage extends NavigateActivity {
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

        timerText = findViewById(R.id.timerText);
        currentTaskText = findViewById(R.id.currentTaskText);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnReset = findViewById(R.id.btnReset);
        btnSkip = findViewById(R.id.btnSkip);
        btnFocus = findViewById(R.id.btnFocus);
        btnShortBreak = findViewById(R.id.btnShortBreak);
        btnLongBreak = findViewById(R.id.btnLongBreak);
        bottomNavView = findViewById(R.id.bottomNavigation);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_homepage;
    }

    @Override
    protected int getCurrentMenuItemId() {
        return R.id.page_home;
    }
}