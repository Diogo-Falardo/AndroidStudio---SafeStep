package com.example.safestep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class addlocations_Saved extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> locationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlocations_saved);

        listView = findViewById(R.id.listViewLocations);
        Button clearAllButton = findViewById(R.id.btnClearAll);
        Button voltarButton = findViewById(R.id.al_voltar);

        // Carregar as localizações e configurar o ListView
        loadLocations();

        // Configurar botão para limpar todas as localizações
        clearAllButton.setOnClickListener(v -> clearAllLocations());

        // Configurar botão para voltar à MainActivity
        voltarButton.setOnClickListener(v -> {
            Intent intent = new Intent(addlocations_Saved.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Configurar duplo clique para apagar uma localização específica
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private long lastClickTime = 0;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 500) { // Verificar duplo clique (500ms)
                    String locationToRemove = locationsList.get(position);
                    removeLocation(locationToRemove);
                }
                lastClickTime = currentTime;
            }
        });
    }

    private void loadLocations() {
        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        String data = sharedPreferences.getString("data", "");

        // Verifica se o dado recuperado está vazio ou contém apenas espaços
        if (data.trim().isEmpty()) {
            // Configura a mensagem na TextView se não houver localizações
            TextView alertTextView = findViewById(R.id.alert_localizacaoguardas);
            alertTextView.setText("Nenhuma Localização salva");

            // Garante que a lista esteja vazia
            locationsList = new ArrayList<>();
        } else {
            // Separa as localizações pela quebra de linha
            locationsList = new ArrayList<>(Arrays.asList(data.split("\n")));

            // Esconde a TextView de alerta
            TextView alertTextView = findViewById(R.id.alert_localizacaoguardas);
            alertTextView.setText("");
        }

        // Configura o adaptador para exibir os dados na ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationsList);
        listView.setAdapter(adapter);
    }


    private void saveLocations() {
        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Salva as localizações concatenadas por quebra de linha
        editor.putString("data", String.join("\n", locationsList));
        editor.apply();
    }

    private void clearAllLocations() {
        // Limpa a lista
        locationsList.clear();

        // Atualiza a TextView com a mensagem
        TextView alertTextView = findViewById(R.id.alert_localizacaoguardas);
        alertTextView.setText("Nenhuma Localização salva");

        // Atualiza o adaptador e salva as mudanças
        adapter.notifyDataSetChanged();
        saveLocations();

        Toast.makeText(this, "Todas as localizações foram removidas!", Toast.LENGTH_SHORT).show();
    }


    private void removeLocation(String locationToRemove) {
        if (locationsList.contains(locationToRemove)) {
            // Remove a localização selecionada
            locationsList.remove(locationToRemove);

            // Atualiza o adaptador e salva as mudanças
            adapter.notifyDataSetChanged();
            saveLocations();

            Toast.makeText(this, "Localização removida!", Toast.LENGTH_SHORT).show();
        }
    }
}
