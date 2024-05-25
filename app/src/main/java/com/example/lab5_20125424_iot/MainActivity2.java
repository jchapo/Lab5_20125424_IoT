package com.example.lab5_20125424_iot;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.lab5_20125424_iot.R;
import com.example.lab5_20125424_iot.entity.Tarea;
import com.example.lab5_20125424_iot.items.ListElementTarea;
import com.example.lab5_20125424_iot.viewModels.NavigationActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    ArrayList<Tarea> listaTareas;
    String nombreUsuario;
    String fileName;
    NavigationActivityViewModel navigationActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        navigationActivityViewModel = new ViewModelProvider(this).get(NavigationActivityViewModel.class);
        navigationActivityViewModel.getNombreUsuario().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String nombreUsuario) {
                if (nombreUsuario != null) {
                    fileName = nombreUsuario + "_listaTareasJson";
                }
            }
        });

        FloatingActionButton agregarUsuarioButton = findViewById(R.id.agregarTarea);
        agregarUsuarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia "MainActivity" por la clase de la actividad a la que deseas cambiar
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                startActivity(intent);
            }
        });
        List<ListElementTarea> listaTareas = new ArrayList<>();

        try (FileInputStream fileInputStream = this.openFileInput(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            StringBuilder jsonDataBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonDataBuilder.append(line);
            }
            String jsonData = jsonDataBuilder.toString();

            Gson gson = new Gson();
            ListElementTarea[] listaTareasArray = gson.fromJson(jsonData, ListElementTarea[].class);

            // Convierte el array en una lista y la asigna a la variable local
            listaTareas.addAll(Arrays.asList(listaTareasArray));

        } catch (IOException e) {
            // Si no se encuentra el archivo, crea uno nuevo con formato vacío
            Log.d("msg-test-abrirArchivoTextoComoJson", "No se pudo leer el archivo, se creará uno nuevo");
            e.printStackTrace();

            // Aquí se crea una nueva lista vacía de tareas
            StringBuilder jsonDataBuilder = new StringBuilder();
            String jsonData = jsonDataBuilder.toString();

            // Se convierte la lista vacía a JSON
            Gson gson = new Gson();
            ListElementTarea[] listaTareasArray = gson.fromJson(jsonData, ListElementTarea[].class);

            // Convierte el array en una lista y la asigna a la variable local
            listaTareas.addAll(Arrays.asList(listaTareasArray));
        }

        navigationActivityViewModel.getListaTareas().setValue(new ArrayList<>(listaTareas));
        replaceFragment(new Fragment1());

    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameListaTareas, fragment);
        fragmentTransaction.commit();
    }
    protected void onResume() {
        super.onResume();
        replaceFragment(new Fragment1());
    }

}
