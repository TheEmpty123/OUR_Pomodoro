package com.mobile.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editTxtUsername;
    private EditText editTxtPassword;
    private Button btnLogin;
    private TextView textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTxtUsername = findViewById(R.id.editTextUsername);
        editTxtPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        textRegister = findViewById(R.id.textViewRegister);

        // nút đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // đăng ký tài khoản
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void login() {
        String username = editTxtUsername.getText().toString().trim();
        String password = editTxtPassword.getText().toString().trim();

        // ktra nếu username trống
        if (TextUtils.isEmpty(username)) {
            editTxtUsername.setError("Vui lòng nhập tên đăng nhập");
            editTxtUsername.requestFocus();
            return;
        }

        // ktra nếu pass trống
        if (TextUtils.isEmpty(password)) {
            editTxtPassword.setError("Vui lòng nhập mật khẩu");
            editTxtPassword.requestFocus();
            return;
        }

        // chuyển đến giao diện Welcome sau khi đăng nhập thành công
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}