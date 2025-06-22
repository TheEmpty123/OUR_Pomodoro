package com.mobile.pomodoro.LoginSignup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobile.pomodoro.R;
import com.mobile.pomodoro.WelcomeActivity;
import com.mobile.pomodoro.enums.*;
import com.mobile.pomodoro.request_dto.LoginRequestDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.utils.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editTxtUsername;
    private EditText editTxtPassword;
    private Button btnLogin;
    private TextView textRegister;
    private TextView textGuest;
    private LogObj log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new LogObj();
        log.setName(getClass().getSimpleName());
        log.info("Initializing...");

        setContentView(R.layout.activity_login);

        editTxtUsername = findViewById(R.id.editTextUsername);
        editTxtPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        textRegister = findViewById(R.id.textViewRegister);
        textGuest = findViewById(R.id.textViewGuest);

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
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        // đăng nhập tài khoản khách
        textGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.info("Login as guest");
                Toast.makeText(LoginActivity.this, "Welcome back, Master", Toast.LENGTH_SHORT).show();

                MyUtils.applicationMode = ApplicationMode.OFFLINE; // Assign application mode to offline
                Intent intent = new Intent(LoginActivity.this ,WelcomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        log.info("Login action commerce...");

        String username = editTxtUsername.getText().toString().trim();
        String password = editTxtPassword.getText().toString().trim();

        // ktra nếu username trống
        if (TextUtils.isEmpty(username)) {
            log.warn("username is empty");
            editTxtUsername.setError("Vui lòng nhập tên đăng nhập");
            editTxtUsername.requestFocus();
            return;
        }

        // ktra nếu pass trống
        if (TextUtils.isEmpty(password)) {
            log.warn("password is empty");
            editTxtPassword.setError("Vui lòng nhập mật khẩu");
            editTxtPassword.requestFocus();
            return;
        }

        // TEST
//        PomodoroService.getClient().testLogin(LoginRequestDTO.builder()
//                        .username(username)
//                        .password(password)
//                        .build())
//                .enqueue(new Callback<MessageResponseDTO>() {
//                    @Override
//                    public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
//                        if (response.isSuccessful() &&
//                                Objects.requireNonNull(response.body()).getMessage() != null) {
//                            Toast.makeText(LoginActivity.this,
//                                            response.body().getMessage(),
//                                            Toast.LENGTH_SHORT)
//                                    .show();
//                        } else {
//                            Toast.makeText(LoginActivity.this,
//                                            "No response",
//                                            Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<MessageResponseDTO> call, Throwable throwable) {
//                        log.error("Network request failed", throwable);
//                        Toast.makeText(LoginActivity.this,
//                                        "Failed to connect to the server. Please try again later.",
//                                        Toast.LENGTH_SHORT)
//                                .show();
//                    }
//                });

        // Login handler
        PomodoroService.getClient().login(LoginRequestDTO.builder()
                        .username(username)
                        .password(password)
                        .build())
                .enqueue(new Callback<MessageResponseDTO>() {
                    @Override
                    public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                        if (response.isSuccessful() &&
                                response.body().getMessage().equals(MessageState.LOGIN_SUCCESSFUL.toString())) {
                            log.info("Login successful, move to homepage");
                            Toast.makeText(LoginActivity.this,
                                            response.body().getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();

                            MyUtils.save(LoginActivity.this, "username", username);
                            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                        } else {
                            if (!response.body().getMessage().equals(MessageState.LOGIN_SUCCESSFUL.toString())) {
                                log.warn(response.body().getMessage());
                                Toast.makeText(LoginActivity.this,
                                                response.body().getMessage(),
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponseDTO> call, Throwable throwable) {
                        log.error(throwable.getMessage());
                    }
                });

        // finish();
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}