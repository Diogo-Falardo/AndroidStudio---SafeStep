package com.example.safestep;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class header extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header);

        // Recupera o dado passado
        String email = getIntent().getStringExtra("email");

        // Atualiza o TextView com o dado recebido
        TextView textView = findViewById(R.id.user_details);
        if (email != null) {
            textView.setText(email);
        } else {
            textView.setText("Erro");
        }


    }
}
