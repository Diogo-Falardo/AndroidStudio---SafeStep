package com.example.safestep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Eu_PersonalAbout extends AppCompatActivity {

    private TextView nomeTextView, idadeTextView, moradaTextView, aboutTextView;
    private static final String PREFERENCES_NAME = "user_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eu_personal_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // botoes
        Button voltarButton = findViewById(R.id.eu_voltar);
        Button editarButton = findViewById(R.id.eu_editarinf);

        // Configurar o botão "Voltar ao início"
        voltarButton.setOnClickListener(view -> {

            Intent intent = new Intent(Eu_PersonalAbout.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Configurar o botão "Editar Minhas Informações"
        editarButton.setOnClickListener(view -> {
            Intent intent = new Intent(Eu_PersonalAbout.this, Eu_EditInformationActivity.class);
            startActivity(intent);
        });

        // Referências para os campos de exibição de informações
        nomeTextView = findViewById(R.id.eu_nomeEU);
        idadeTextView = findViewById(R.id.eu_idadeEU);
        moradaTextView = findViewById(R.id.eu_moradaEU);
        aboutTextView = findViewById(R.id.eu_aboutme);

        // Recuperar os dados do SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String nome = sharedPreferences.getString("nome", "Não definido");
        String idade = sharedPreferences.getString("idade", "Não definido");
        String morada = sharedPreferences.getString("morada", "Não definido");
        String about = sharedPreferences.getString("about", "Não definido");

        // Exibir os dados recuperados
        nomeTextView.setText("Nome: " + nome);
        idadeTextView.setText("Idade: " + idade);
        moradaTextView.setText("Morada: " + morada);
        aboutTextView.setText(about);

    }
}