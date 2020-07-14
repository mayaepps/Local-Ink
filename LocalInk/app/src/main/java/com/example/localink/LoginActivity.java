package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.localink.databinding.ActivityLoginBinding;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        // Log in as a reader button takes the user to MainActivity
        onClickGoToActivity(binding.btnReaderLogin, MainActivity.class, true);

        // Log in as a bookstore button takes the user to the BookstoreMainActivity
        onClickGoToActivity(binding.btnBookstoreLogin, BookstoreMainActivity.class, true);

        // Register as a reader button takes the user to the RegisterReaderActivity
        onClickGoToActivity(binding.btnReaderRegister, RegisterReaderActivity.class, false);

        // Register as a bookstore button takes the user to the RegisterBookstoreActivity
        onClickGoToActivity(binding.btnBookstoreRegister, RegisterBookstoreActivity.class, false);
    }

    protected void onClickGoToActivity(MaterialButton button, final Class activity, final boolean doFinish) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, activity);
                startActivity(i);
                if (doFinish) {
                    finish();
                }
            }
        });
    }
}