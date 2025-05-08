package com.mobile.pomodoro;

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

import com.mobile.pomodoro.enums.MessageState;
import com.mobile.pomodoro.request_dto.LoginRequestDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;
import com.mobile.pomodoro.service.PomodoroService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
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

        // TEST
//        PomodoroService.getClient().testLogin(LoginRequestDTO.builder()
//                        .username(username)
//                        .password(password)
//                        .build())
//                .enqueue(new Callback<MessageResponseDTO>() {
//                    @Override
//                    public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
//                        if (response.isSuccessful() &&
//                                Objects.requireNonNull(response.body()).getMessage() != null){
//                            Toast.makeText(LoginActivity.this,
//                                            response.body().getMessage(),
//                                            Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                        else {
//                            Toast.makeText(LoginActivity.this,
//                                            "No response",
//                                            Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<MessageResponseDTO> call, Throwable throwable) {
//                        throw new RuntimeException(throwable);
//                    }
//                });

        System.out.println("Huh?");
        // Login handler
        PomodoroService.getClient().login(LoginRequestDTO.builder()
                    .username(username)
                    .password(password)
                    .build())
                .enqueue(new Callback<MessageResponseDTO>() {
                    @Override
                    public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                        if (response.isSuccessful() &&
                                response.body().getMessage().equals(MessageState.LOGIN_SUCCESSFUL.toString())){

                            Toast.makeText(LoginActivity.this,
                                            response.body().getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();

                            // tartActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        else {
                            if (!response.body().getMessage().equals(MessageState.LOGIN_SUCCESSFUL.toString())) {
                                Toast.makeText(LoginActivity.this,
                                                response.body().getMessage(),
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponseDTO> call, Throwable throwable) {
                        throw new RuntimeException(throwable);
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