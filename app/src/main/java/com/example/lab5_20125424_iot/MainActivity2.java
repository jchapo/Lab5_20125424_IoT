package com.example.lab5_20125424_iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab5_20125424_iot.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FloatingActionButton agregarUsuarioButton = findViewById(R.id.agregarTarea);
        agregarUsuarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia "MainActivity" por la clase de la actividad a la que deseas cambiar
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                startActivity(intent);
            }
        });


    }
}
