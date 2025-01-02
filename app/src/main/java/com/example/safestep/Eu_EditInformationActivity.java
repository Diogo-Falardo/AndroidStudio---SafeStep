package com.example.safestep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Eu_EditInformationActivity extends AppCompatActivity {

    private EditText nomeEditText, idadeEditText, moradaEditText, aboutEditText;
    private Button saveButton, voltarButton;

    // Nome do arquivo de SharedPreferences
    private static final String PREFERENCES_NAME = "user_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eu_edit_information);  // Certifique-se de que este é o layout correto

        // IDS
        nomeEditText = findViewById(R.id.eu_edit_nome);
        idadeEditText = findViewById(R.id.eu_edit_idade);
        moradaEditText = findViewById(R.id.eu_edit_morada);
        aboutEditText = findViewById(R.id.eu_edit_about);
        saveButton = findViewById(R.id.eu_save_button);
        voltarButton = findViewById(R.id.eu_voltar);

        // Configurar o botão "Voltar ao Início"
        voltarButton.setOnClickListener(view -> {
            Intent intent = new Intent(Eu_EditInformationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // botão Salvar
        saveButton.setOnClickListener(view -> {
            String nome = nomeEditText.getText().toString();
            String idade = idadeEditText.getText().toString();
            String morada = moradaEditText.getText().toString();
            String about = aboutEditText.getText().toString();

            //  Verificar se todos os campos estão preenchidos
            if (nome.isEmpty() || idade.isEmpty() || morada.isEmpty() || about.isEmpty()) {
                Toast.makeText(Eu_EditInformationActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {

                // Salvar os dados no SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nome", nome);
                editor.putString("idade", idade);
                editor.putString("morada", morada);
                editor.putString("about", about);
                editor.apply(); // Salva os dados


                Toast.makeText(Eu_EditInformationActivity.this, "Informações salvas!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Eu_EditInformationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
