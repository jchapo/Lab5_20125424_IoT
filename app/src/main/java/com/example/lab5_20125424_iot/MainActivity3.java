package com.example.lab5_20125424_iot;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.lab5_20125424_iot.entity.Tarea;
import com.example.lab5_20125424_iot.entity.TareaDto;
import com.example.lab5_20125424_iot.items.ListElementTarea;
import com.example.lab5_20125424_iot.viewModels.NavigationActivityViewModel;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    String canal1 = "important";
    NavigationActivityViewModel viewModel;
    String nombreUsuario;
    String fileName;
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
        crearCanalesNotificacion();
        isEditing = getIntent().getBooleanExtra("isEditing", false);

        selectImportancia = findViewById(R.id.selectImportancia);
        importanciaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, importanciaOptions);
        selectImportancia.setAdapter(importanciaAdapter);

        editTitulo = findViewById(R.id.editTitulo);
        editDescripcion = findViewById(R.id.editDescripcion);
        editFecha = findViewById(R.id.editFecha);
        editHora = findViewById(R.id.editHora);
        // Inicializar el ViewModel
        viewModel = new ViewModelProvider(this).get(NavigationActivityViewModel.class);

        viewModel.getNombreUsuario().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String nombreUsuario) {
                if (nombreUsuario != null) {
                    fileName = nombreUsuario + "_listaTareasJson";
                }
            }
        });


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
                    LocalDateTime fechaActual = LocalDateTime.now(); // Cambia LocalDate a LocalDateTime
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // Formateador de fecha y hora
                    String fechaCreacion = fechaActual.format(formatter);

                    ListElementTarea listElement = new ListElementTarea(titulo, descripcion, fecha, hora, importancia, status, fechaCreacion);
                    actualizarArchivoTextoJson(this, listElement,isEditing);
                    String toastMessage = "Tarea creada";
                    Toast.makeText(MainActivity3.this, toastMessage, Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(MainActivity3.this, MainActivity2.class);
                    notificarImportanceDefault(titulo, importancia);
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
                    actualizarArchivoTextoJson(this, element,isEditing);
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

    public void actualizarArchivoTextoJson(Context context, ListElementTarea listElement, boolean isEditing) {

        List<ListElementTarea> listaTareas = new ArrayList<>();

        try (FileInputStream fileInputStream = context.openFileInput(fileName);
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
            if (listaTareasArray != null) {
                listaTareas.addAll(Arrays.asList(listaTareasArray));
            }

            if (isEditing) {
                for (int i = 0; i < listaTareas.size(); i++) {
                    ListElementTarea existingTask = listaTareas.get(i);
                    if (existingTask.getFechaCreacion().equals(listElement.getFechaCreacion())) {
                        // Si la fecha de creación coincide, actualiza la tarea existente con la nueva información.
                        listaTareas.set(i, listElement);
                        break; // Termina el bucle una vez que se actualiza la tarea.
                    }
                }
            } else {
                // Si no se está editando, simplemente agrega la nueva tarea a la lista.
                listaTareas.add(listElement);
            }

            // Ahora, guarda la lista actualizada de tareas en el archivo JSON.
            try (FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                 BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
                String nuevaJsonData = gson.toJson(listaTareas.toArray(new ListElementTarea[0]));
                bufferedWriter.write(nuevaJsonData);
                Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo guardado correctamente");
            } catch (IOException e) {
                Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo no guardado correctamente");
                e.printStackTrace();
            }

        } catch (IOException e) {
            Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo nuevo");
            e.printStackTrace();
            // Aquí maneja el caso cuando el archivo no existe o no se puede leer.
            // Puedes seguir con la lógica que ya tienes para guardar una nueva tarea en este caso.
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
    public void crearCanalesNotificacion() {

        NotificationChannel channel = new NotificationChannel(canal1,
                "Canal Users Creation",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Canal para notificaciones de creación de perfiles de usuario con prioridad default");
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        pedirPermisos();
    }

    public void pedirPermisos() {
        // TIRAMISU = 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 101);
        }
    }
    public void notificarImportanceDefault(String titulo, String importancia){

        //Crear notificación
        //Agregar información a la notificación que luego sea enviada a la actividad que se abre
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra("pid",4616);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canal1)
                .setSmallIcon(R.drawable.ic_notification_outline_black)
                .setContentTitle("Nueva tarea")
                .setContentText("Título: " + titulo + "\nPrioridad: " + importancia)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (importancia.equals("Alta")) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        } else if (importancia.equals("Media")) {
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } else if (importancia.equals("Baja")) {
            builder.setPriority(NotificationCompat.PRIORITY_LOW);
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, notification);
        }

    }

}
