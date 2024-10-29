package com.example.shoeshopee_customer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IntroActivity extends AppCompatActivity {

    Button intro_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);

        intro_btn = findViewById(R.id.intro_btn);
        intro_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signIn();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void signIn() {
        TextView tvSignInSignUp = findViewById(R.id.tvSignInSignUp);

        String text = getString(R.string.intro_signin);

        SpannableString spannableString = new SpannableString(text);

        int signInStart = text.indexOf("Đăng nhập");
        int signInEnd = signInStart + "Đăng nhập".length();

        int signUpStart = text.indexOf("đăng ký");
        int signUpEnd = signUpStart + "đăng ký".length();

        ClickableSpan signInClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
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
                Intent intent = new Intent(IntroActivity.this, SignupActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#1670E9"));
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(signInClick, signInStart, signInEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(signUpClick, signUpStart, signUpEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSignInSignUp.setText(spannableString);
        tvSignInSignUp.setMovementMethod(LinkMovementMethod.getInstance());
    }
}