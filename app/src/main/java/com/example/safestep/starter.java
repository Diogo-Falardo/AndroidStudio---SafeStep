package com.example.safestep;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class starter extends AppCompatActivity {

    private static final int SPLASH_DELAY = 5000; // 5 segundo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starter);

        // Atrasar execução por 5 segundos e redirecionar
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(starter.this, login.class);
            startActivity(intent);
            finish(); // Finaliza a Splash Activity
        }, SPLASH_DELAY);
    }
}
