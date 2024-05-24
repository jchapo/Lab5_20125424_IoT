package com.example.lab5_20125424_iot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab5_20125424_iot.entity.Tarea;
import com.example.lab5_20125424_iot.entity.TareaDto;
import com.example.lab5_20125424_iot.items.ListElementTarea;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    ListElementTarea element;
    private EditText editTitulo, editDescripcion, editFecha, editHora;
    private MaterialAutoCompleteTextView selectImportancia;
    ArrayAdapter<String> importanciaAdapter;
    private boolean isEditing = false; // Indicador para editar o crear nuevo usuario

    String[] importanciaOptions = {"Alta", "Media", "Baja"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        isEditing = getIntent().getBooleanExtra("isEditing", false);

        selectImportancia = findViewById(R.id.selectImportancia);
        importanciaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, importanciaOptions);
        selectImportancia.setAdapter(importanciaAdapter);

        editTitulo = findViewById(R.id.editTitulo);
        editDescripcion = findViewById(R.id.editDescripcion);
        editFecha = findViewById(R.id.editFecha);
        editHora = findViewById(R.id.editHora);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBarAgregarTarea);
        if (isEditing) {
            topAppBar.inflateMenu(R.menu.top_app_bar_edit);
        } else {
            topAppBar.inflateMenu(R.menu.top_app_bar_new);
        }
        Intent intent = getIntent();
        if (intent.hasExtra("ListElement")) {
            ListElementTarea element = (ListElementTarea) intent.getSerializableExtra("ListElement");
            isEditing = true;
            fillFields(element);
            topAppBar.setTitle("Editar Tarea");
        } else {
            topAppBar.setTitle("Nueva Tarea");
        }

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.createNewTopAppBar) {
                if (areFieldsEmpty()) {
                    Toast.makeText(MainActivity3.this, "Debe completar todos los datos", Toast.LENGTH_SHORT).show();
                } else {
                    String titulo = editTitulo.getText().toString();
                    String descripcion = editDescripcion.getText().toString();
                    String fecha = editFecha.getText().toString();
                    String hora = editHora.getText().toString();
                    String importancia = selectImportancia.getText().toString();
                    String status = "Activo";
                    LocalDate fechaActual = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String fechaCreacion = fechaActual.format(formatter);

                    ListElementTarea listElement = new ListElementTarea(titulo, descripcion, fecha, hora, importancia, status, fechaCreacion);
                    actualizarArchivoTextoJson(this, listElement);
                    String toastMessage = "Tarea creada";
                    Toast.makeText(MainActivity3.this, toastMessage, Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(MainActivity3.this, MainActivity2.class);
                    startActivity(intent2);
                }
                return true;
            } else if (item.getItemId() == R.id.saveOldTopAppBar) {
                if (areFieldsEmpty()) {
                    Toast.makeText(MainActivity3.this, "Debe completar todos los datos", Toast.LENGTH_SHORT).show();
                } else {
                    String titulo = editTitulo.getText().toString();
                    String descripcion = editDescripcion.getText().toString();
                    String fecha = editFecha.getText().toString();
                    String hora = editHora.getText().toString();
                    String importancia = selectImportancia.getText().toString();
                    String status = "Activo";
                    LocalDate fechaActual = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String fechaCreacion = fechaActual.format(formatter);

                    ListElementTarea element = (ListElementTarea) getIntent().getSerializableExtra("ListElement");
                    element.setTitulo(titulo);
                    element.setDescripcion(descripcion);
                    element.setFecha(fecha);
                    element.setHora(hora);
                    element.setImportancia(importancia);
                    actualizarArchivoTextoJson(this, element);
                    String toastMessage = "Tarea actualizada";
                    Toast.makeText(MainActivity3.this, toastMessage, Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(MainActivity3.this, MainActivity2.class);
                    startActivity(intent3);
                }
                return true;
            } else {
                return false;
            }
        });

        topAppBar.setNavigationOnClickListener(v -> finish());

        editFecha.setOnClickListener(v -> {
            hideKeyboard(v);

            final Calendar calendario = Calendar.getInstance();
            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity3.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String fechaSeleccionada = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        editFecha.setText(fechaSeleccionada);
                        verificarFechaYHora();
                    }, año, mes, dia);
            datePickerDialog.show();
        });

        editHora.setOnClickListener(v -> {
            hideKeyboard(v);

            final Calendar calendario = Calendar.getInstance();
            int hora = calendario.get(Calendar.HOUR_OF_DAY);
            int minuto = calendario.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity3.this,
                    (view, hourOfDay, minute) -> {
                        String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
                        editHora.setText(horaSeleccionada);
                        verificarFechaYHora();
                    }, hora, minuto, true);
            timePickerDialog.show();
        });
    }

    private void verificarFechaYHora() {
        String fechaSeleccionada = editFecha.getText().toString();
        String horaSeleccionada = editHora.getText().toString();

        if (!fechaSeleccionada.isEmpty() && !horaSeleccionada.isEmpty()) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalDate fechaActual = LocalDate.now();
            Calendar ahora = Calendar.getInstance();
            int horaActual = ahora.get(Calendar.HOUR_OF_DAY);

            LocalDate fechaSeleccionadaDate = LocalDate.parse(fechaSeleccionada, dateFormatter);
            int horaSeleccionadaInt = Integer.parseInt(horaSeleccionada.split(":")[0]);

            if (fechaSeleccionadaDate.equals(fechaActual) && horaSeleccionadaInt <= (horaActual + 3)) {
                selectImportancia.setText("Alta");
                selectImportancia.setEnabled(false);
                Toast.makeText(this, "Tarea de pioridad ALTA.", Toast.LENGTH_SHORT).show();
            } else {
                selectImportancia.setEnabled(true);
            }
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean areFieldsEmpty() {
        return selectImportancia.getText().toString().isEmpty() ||
                editTitulo.getText().toString().isEmpty() ||
                editDescripcion.getText().toString().isEmpty() ||
                editFecha.getText().toString().isEmpty() ||
                editHora.getText().toString().isEmpty();
    }

    public void actualizarArchivoTextoJson(Context context, ListElementTarea listElement) {
        String fileName = "listaTareasJson";

        try (FileInputStream fileInputStream = context.openFileInput(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            StringBuilder jsonDataBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonDataBuilder.append(line);
            }
            Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo para actualizar");

            String jsonData = jsonDataBuilder.toString();

            Gson gson = new Gson();
            ListElementTarea[] listaTareas = gson.fromJson(jsonData, ListElementTarea[].class);

            List<ListElementTarea> nuevaListaTareas = new ArrayList<>(Arrays.asList(listaTareas));
            nuevaListaTareas.add(listElement);

            try (FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                 BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {

                String nuevaJsonData = gson.toJson(nuevaListaTareas.toArray(new ListElementTarea[0]));
                bufferedWriter.write(nuevaJsonData);

                Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo guardado correctamente");
            } catch (IOException e) {
                Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo no guardado correctamente");
                e.printStackTrace();
            }

        } catch (IOException e) {
            Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo nuevo");
            e.printStackTrace();
            List<ListElementTarea> nuevaListaTareas = new ArrayList<>();
            nuevaListaTareas.add(listElement);

            try (FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                 BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                Gson gson = new Gson();
                String nuevaJsonData = gson.toJson(nuevaListaTareas.toArray(new ListElementTarea[0]));
                bufferedWriter.write(nuevaJsonData);
                Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo guardado correctamente");
            } catch (IOException er) {
                Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo no guardado correctamente");
                e.printStackTrace();
            }
        }
    }

    private void fillFields(ListElementTarea element) {
        editTitulo.setText(element.getTitulo());
        editDescripcion.setText(element.getDescripcion());
        editFecha.setText(element.getFecha());
        editHora.setText(element.getHora());
        int index = Arrays.asList(importanciaOptions).indexOf(element.getImportancia());
        if (index != -1) {
            selectImportancia.setText(importanciaOptions[index], false);
        }
        verificarFechaYHora();
    }


}
