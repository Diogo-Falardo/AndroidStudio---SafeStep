package com.example.safestep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // NAVBAR

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    FirebaseAuth auth;
    Button logout;
    TextView textView;
    FirebaseUser user;


    // Relativo APP
    private MapView mapView;
    private static final int SPEECH_REQUEST_CODE = 100;
    private EditText textInput;
    private Marker currentMarker; // Para manter o marcador atual


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // NavigationBar

        // Configurar a Toolbar como a ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Necessário para usar getSupportActionBar()

        // Verifique se getSupportActionBar() não retorna null
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Exibe o botão de navegação
        }

        // Configurar o Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configurar o NavigationView
        NavigationView navigationView = findViewById(R.id.navbar);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Toast.makeText(this, "Home selecionado", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.nav_addlocation) {
                Intent intent = new Intent(MainActivity.this, addlocations.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, settings.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish();
            } else if (itemId == R.id.nav_eu) {
                Intent intent = new Intent(MainActivity.this, Eu_PersonalAbout.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_about) {
                Intent intent = new Intent(MainActivity.this, SafeStep.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return true;
        });


        // Autenticaçao

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        try {
            if (user == null) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            } else {
                String email = user.getEmail();
                if (email == null) {
                    Toast.makeText(this, "E-mail do usuário não está disponível.", Toast.LENGTH_SHORT).show();
                } else {
                    View headerView = navigationView.getHeaderView(0); // Pega o primeiro header do NavigationView
                    TextView emailTextView = headerView.findViewById(R.id.user_details); // Aqui você referencia o TextView do header

                    // Define o e-mail no TextView do header
                    emailTextView.setText(email);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


        //###############################################################################//
        // Configuração do OSMdroid
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this));

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMenu(View view) {
        // Criação do PopupMenu diretamente por código
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Recupera as localizações salvas
        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        String data = sharedPreferences.getString("data", "");

        if (data.trim().isEmpty()) {
            // Caso não haja localizações salvas, exiba uma mensagem
            popupMenu.getMenu().add("Nenhuma localização salva.");
        } else {
            // Adiciona cada localização como um item no menu
            String[] locations = data.split("\n"); // Cada localização é separada por uma nova linha
            for (String location : locations) {
                location = location.trim();
                String[] parts = location.split(" - "); // Divide o nome e as coordenadas

                if (parts.length > 0) {
                    String name = parts[0].trim(); // Pega apenas o nome, ignorando as coordenadas
                    popupMenu.getMenu().add(0, 0, 0, name); // Adiciona apenas o nome ao menu
                }
            }

            // Configura a ação ao clicar em uma localização
            popupMenu.setOnMenuItemClickListener(item -> {
                String name = item.getTitle().toString(); // Recupera o nome do item clicado
                String[] locationsArray = data.split("\n");

                boolean locationFound = false; // Flag para controlar se a localização foi encontrada
                for (String location : locationsArray) {
                    String[] parts = location.split(" - ");
                    if (parts.length == 2 && parts[0].trim().equals(name)) {
                        String coordenadas = parts[1].trim(); // Recupera as coordenadas

                        // Agora, vamos dividir as coordenadas em latitude e longitude
                        String[] coords = coordenadas.split(",");
                        if (coords.length == 2) {
                            try {
                                double latitude = Double.parseDouble(coords[0].trim());
                                double longitude = Double.parseDouble(coords[1].trim());
                                updateMapLocation(latitude, longitude); // Atualiza o mapa para a localização selecionada
                                locationFound = true;
                            } catch (NumberFormatException e) {
                                Toast.makeText(this, "Coordenadas inválidas.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Formato de coordenadas inválido.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }

                if (!locationFound) {
                    Toast.makeText(this, "Localização não encontrada.", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }

        // Exibe o menu popup
        popupMenu.show();
    }





    private void showLocations() {
        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        String data = sharedPreferences.getString("data", "Nenhuma localização salva.");

        // Exibe as localizações em um AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Localizações Salvas")
                .setMessage(data)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void clearLocations() {
        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}


