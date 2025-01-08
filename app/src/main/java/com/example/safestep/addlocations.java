package com.example.safestep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class addlocations extends AppCompatActivity {

    private EditText etCoordenadas, etNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlocations);

        etCoordenadas = findViewById(R.id.al_coordenadas);
        etNome = findViewById(R.id.al_nome);

        Button guardarButton = findViewById(R.id.al_guardar);
        Button locsSalvas = findViewById(R.id.al_locsalavas);
        Button voltarButton = findViewById(R.id.al_voltar);

        // Botão para guardar informações
        guardarButton.setOnClickListener(v -> {
            String coordenadas = etCoordenadas.getText().toString();
            String nome = etNome.getText().toString();

            if (!coordenadas.isEmpty() && !nome.isEmpty()) {
                // Verifica se as coordenadas estão no formato correto
                if (isValidCoordinates(coordenadas)) {
                    saveLocation(nome, coordenadas);
                    Toast.makeText(this, "Localização Guardada!", Toast.LENGTH_SHORT).show();
                    etCoordenadas.setText("");
                    etNome.setText("");
                } else {
                    Toast.makeText(this, "Formato de coordenadas inválido. Use: 'latitude, longitude'", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            }
        });

        // Botão para voltar ao MainActivity
        voltarButton.setOnClickListener(v -> {
            Intent intent = new Intent(addlocations.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Botão para ver locs Guardadas
        locsSalvas.setOnClickListener(v -> {
            Intent intent = new Intent(addlocations.this, addlocations_Saved.class);
            startActivity(intent);
            finish();
        });
    }

    private void saveLocation(String nome, String coordenadas) {
        try {
            // Acessar o SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Obter os dados existentes
            String currentData = sharedPreferences.getString("data", "");

            // Criar a nova localização no formato "Nome - Coordenadas"
            String newLocation = nome + " - " + coordenadas;

            // Concatenar a nova localização às existentes, com separador por quebra de linha
            String updatedData = currentData.isEmpty() ? newLocation : currentData + "\n" + newLocation;

            // Salvar os dados atualizados no SharedPreferences
            editor.putString("data", updatedData);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao salvar localização.", Toast.LENGTH_SHORT).show();
        }
    }


    // Função para validar as coordenadas no formato 'latitude, longitude'
    private boolean isValidCoordinates(String coordenadas) {
        String[] parts = coordenadas.split(",");
        if (parts.length == 2) {
            try {
                double latitude = Double.parseDouble(parts[0].trim());
                double longitude = Double.parseDouble(parts[1].trim());
                return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
}
