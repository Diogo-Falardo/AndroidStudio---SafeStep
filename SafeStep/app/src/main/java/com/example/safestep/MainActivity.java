package com.example.safestep;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private static final int SPEECH_REQUEST_CODE = 100;
    private EditText textInput;
    private Marker currentMarker; // Para manter o marcador atual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuração do OSMdroid
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map);
        textInput = findViewById(R.id.text_input); // Referência ao EditText

        // Configurar o mapa
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Coordenadas específicas
        GeoPoint point = new GeoPoint(38.9954378, -9.1411938);
        mapView.getController().setZoom(15);
        mapView.getController().setCenter(point);

        // Adicionar um marcador inicial
        currentMarker = new Marker(mapView);
        currentMarker.setPosition(point);
        currentMarker.setTitle("Localização Simulada");
        mapView.getOverlays().add(currentMarker);
        currentMarker.showInfoWindow();

        // Inicializar botão de voz
        Button voiceButton = findViewById(R.id.voice_button);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });

        // Inicializar botão de enviar texto
        Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR"); // Defina o idioma para português do Brasil
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale algo...");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Reconhecimento de voz não disponível.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spokenText = results.get(0);
                Toast.makeText(this, "Você disse: " + spokenText, Toast.LENGTH_SHORT).show();
                // Aqui você pode adicionar lógica para lidar com os comandos de voz
            }
        }
    }

    private void sendText() {
        String text = textInput.getText().toString();
        if (!text.isEmpty()) {
            String[] coordinates = text.split(","); // Divide o texto em coordenadas
            if (coordinates.length == 2) {
                try {
                    double latitude = Double.parseDouble(coordinates[0].trim());
                    double longitude = Double.parseDouble(coordinates[1].trim());
                    updateMapLocation(latitude, longitude);
                    textInput.setText(""); // Limpar o campo de texto
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Formato de coordenadas inválido.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Por favor, insira coordenadas no formato: 'latitude, longitude'", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor, digite algo antes de enviar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMapLocation(double latitude, double longitude) {
        // Atualiza a posição do marcador atual
        GeoPoint newPoint = new GeoPoint(latitude, longitude);

        // Atualiza a câmera do mapa
        mapView.getController().setCenter(newPoint);

        // Atualiza ou cria um novo marcador
        if (currentMarker != null) {
            currentMarker.setPosition(newPoint);
            currentMarker.setTitle("Nova Localização");
        } else {
            currentMarker = new Marker(mapView);
            currentMarker.setPosition(newPoint);
            currentMarker.setTitle("Nova Localização");
            mapView.getOverlays().add(currentMarker);
        }
        currentMarker.showInfoWindow();
        mapView.invalidate(); // Atualiza o mapa para mostrar as alterações
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // para gerenciar o ciclo de vida do MapView
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach(); // para liberar recursos
    }
}
