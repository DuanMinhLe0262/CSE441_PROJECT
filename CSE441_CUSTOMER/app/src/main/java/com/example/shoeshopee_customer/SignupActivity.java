package com.example.shoeshopee_customer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoeshopee_customer.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private ImageView back_signup;
    private EditText edtEmail, edtPasswd, edtRewritePasswd, edtName;
    private Button btnSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        back_signup = findViewById(R.id.back_signup);
        back_signup.setOnClickListener(v -> finish());
        edtEmail = findViewById(R.id.edtEmail);
        edtPasswd = findViewById(R.id.edtPasswd);
        edtRewritePasswd = findViewById(R.id.edtRewritePasswd);
        edtName = findViewById(R.id.edtName);
        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPasswd.getText().toString().trim();
            String confirmPassword = edtRewritePasswd.getText().toString().trim();
            String name = edtName.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(name)) {
                Toast.makeText(SignupActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo tài khoản Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                    // Tạo đối tượng User và lưu vào Firebase Realtime Database
                    User newUser = new User(userId, name, "", "", "", email, "", "customer");
                    userRef.setValue(newUser).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(SignupActivity.this, "Đăng ký thất bại: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SignupActivity.this, "Đăng ký thất bại: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        clickTxt();
    }

    public void clickTxt() {
        TextView signup_bottom_title_txt = findViewById(R.id.signup_bottom_title_txt);
        String textSignin = getString(R.string.signup_bottom_title);
        SpannableString spannableStringSignin = new SpannableString(textSignin);

        int signinStart = textSignin.indexOf("Đăng nhập ngay");
        int signinEnd = signinStart + "Đăng nhập ngay".length();

        ClickableSpan signinClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#1670E9"));
                ds.setUnderlineText(false);
            }
        };

        spannableStringSignin.setSpan(signinClick, signinStart, signinEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signup_bottom_title_txt.setText(spannableStringSignin);
        signup_bottom_title_txt.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
