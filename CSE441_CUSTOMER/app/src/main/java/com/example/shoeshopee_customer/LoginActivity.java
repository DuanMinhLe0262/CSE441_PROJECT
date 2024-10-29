package com.example.shoeshopee_customer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private ImageView back_login;
    private Button btnLogin;
    private EditText edtEmail, edtPasswd;
    private FirebaseAuth mAuth;
    Drawable account_icon, lock_icon, gg_icon, fb_icon;
    Button btnLoginWithGg, btnLoginWithFb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        account_icon = getResources().getDrawable(R.drawable.account_icon);
        account_icon.setBounds(0, 0, 70, 70);
        lock_icon = getResources().getDrawable(R.drawable.lock_icon);
        lock_icon.setBounds(0, 0, 75, 75);
        gg_icon = getResources().getDrawable(R.drawable.gg_icon);
        gg_icon.setBounds(0, 0, 90, 90);
        fb_icon = getResources().getDrawable(R.drawable.fb_icon);
        fb_icon.setBounds(0, 0, 90, 90);

        back_login = findViewById(R.id.back_login);
        back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
                startActivity(intent);
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtEmail.setCompoundDrawables(account_icon, null, null, null);
        edtPasswd = findViewById(R.id.edtPasswd);
        edtPasswd.setCompoundDrawables(lock_icon, null, null, null);
        btnLoginWithGg = findViewById(R.id.btnLoginWithGg);
        btnLoginWithGg.setCompoundDrawables(gg_icon, null, null, null);
        btnLoginWithFb = findViewById(R.id.btnLoginWithFb);
        btnLoginWithFb.setCompoundDrawables(fb_icon, null, null, null);


        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPasswd.getText().toString();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if(!password.isEmpty()){
                        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

                            userRef.child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userType = snapshot.getValue(String.class);
                                    if (userType != null) {
                                        if (userType.equals("user")) {
                                            // Truyền userId đến MainActivity
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.putExtra("userId", userId);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Bạn không có quyền truy cập ứng dụng này", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Không thể xác thực loại người dùng", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Lỗi khi truy xuất dữ liệu", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }).addOnFailureListener(e -> {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });


                    } else {
                        Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();

                    }
                } else if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đúng định dạng Email", Toast.LENGTH_SHORT).show();
                }

            }
        });
        clickTxt();

    }

    public void clickTxt() {
        TextView textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        TextView bottom_login_title = findViewById(R.id.bottom_login_title);

// Tạo chuỗi từ TextView
        String textSignup = getString(R.string.login_bottom_title);
        String textReset = getString(R.string.login_to_reset);

        SpannableString spannableStringSignup = new SpannableString(textSignup);
        SpannableString spannableStringReset = new SpannableString(textReset);

        int resetStart = textReset.indexOf("Quên");
        int resetEnd = resetStart + "Quên".length();

        int signUpStart = textSignup.indexOf("Đăng ký ngay");
        int signUpEnd = signUpStart + "Đăng ký ngay".length();

        ClickableSpan resetClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#1670E9"));
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan signUpClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#1670E9"));
                ds.setUnderlineText(false);
            }
        };

        spannableStringReset.setSpan(resetClick, resetStart, resetEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringSignup.setSpan(signUpClick, signUpStart, signUpEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        textViewForgotPassword.setText(spannableStringReset);
        textViewForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
        bottom_login_title.setText(spannableStringSignup);
        bottom_login_title.setMovementMethod(LinkMovementMethod.getInstance());

    }
}