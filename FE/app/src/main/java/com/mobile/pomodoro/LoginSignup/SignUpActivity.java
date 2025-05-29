package com.mobile.pomodoro.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobile.pomodoro.R;
import com.mobile.pomodoro.enums.MessageState;
import com.mobile.pomodoro.request_dto.RegisterRequestDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.utils.LogObj;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText txtUsername, txtMail, txtPassword, txtConfirmPassword;
    private Button btnRegister;
    private TextView textViewLogin;
    private LogObj log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        log = new LogObj();
        log.setName(getClass().getSimpleName());
        log.info("Initializing...");

        txtUsername = findViewById(R.id.txtUsername);
        txtMail = findViewById(R.id.txtMail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        textViewLogin = findViewById(R.id.txtViewLogin);

        // Nút đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        // Chuyển về màn hình đăng nhập
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Đóng SignUpActivity
            }
        });
    }

    private void register() {
        log.info("Register action commence...");

        String username = txtUsername.getText().toString().trim();
        String email = txtMail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String confirmPassword = txtConfirmPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(username)) {
            log.warn("username is empty");
            txtUsername.setError("Vui lòng nhập tên đăng nhập");
            txtUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            log.warn("email is empty");
            txtMail.setError("Vui lòng nhập email");
            txtMail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            log.warn("invalid email format");
            txtMail.setError("Email không hợp lệ");
            txtMail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            log.warn("password is empty");
            txtPassword.setError("Vui lòng nhập mật khẩu");
            txtPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            log.warn("password too short");
            txtPassword.setError("Mật khẩu phải có ít nhất 8 ký tự");
            txtPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            log.warn("confirm password is empty");
            txtConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            txtConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            log.warn("passwords don't match");
            txtConfirmPassword.setError("Mật khẩu không khớp");
            txtConfirmPassword.requestFocus();
            return;
        }

        // API Call - Register
        PomodoroService.getClient().register(RegisterRequestDTO.builder().username(username).email(email).password(password).build()).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    if (message.equals(MessageState.USER_REGISTERED_SUCCESSFULLY.toString())) {
                        log.info("Registration successful");
                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Chuyển về LoginActivity
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        log.warn("Registration failed: " + message);
                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable throwable) {
                log.error("Registration failed: " + throwable.getMessage());
                Toast.makeText(SignUpActivity.this, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}