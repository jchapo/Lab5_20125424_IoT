package com.example.lab5_20125424_iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab5_20125424_iot.items.ListElementTarea;

public class MainActivity4 extends AppCompatActivity {
    TextView tituloTextView, descripcionTextView, fechaTextView, horaTextView;
    String estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        ListElementTarea element = (ListElementTarea) getIntent().getSerializableExtra("ListElement");
        tituloTextView = findViewById(R.id.tituloTextView);
        descripcionTextView = findViewById(R.id.descripcionTextView);
        fechaTextView = findViewById(R.id.fechaTextView);
        horaTextView = findViewById(R.id.horaTextView);


        tituloTextView.setText(element.getTitulo());
        descripcionTextView.setText(element.getDescripcion());
        fechaTextView.setText(element.getFecha());
        horaTextView.setText(element.getHora());
        estado = element.getEstado();

        Toolbar toolbar = findViewById(R.id.topAppBarAgregarTarea);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.editarFab).setOnClickListener(new View.OnClickListener() {
            // Código para abrir MainActivity_new_user_admin desde la actividad del perfil de usuario
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity4.this, MainActivity3.class);
                intent.putExtra("isEditing", true);
                intent.putExtra("ListElement", element);
                startActivity(intent);
            }
        });

    }
}