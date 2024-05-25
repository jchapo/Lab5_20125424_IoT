package com.example.lab5_20125424_iot;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lab5_20125424_iot.items.ListAdapterTarea;
import com.example.lab5_20125424_iot.items.ListElementTarea;
import com.example.lab5_20125424_iot.viewModels.NavigationActivityViewModel;
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
import java.util.Objects;

public class Fragment1 extends Fragment implements ListAdapterTarea.OnItemClickListener{

    private ArrayList<ListElementTarea> listaTarea;
    private ListAdapterTarea listAdapterTarea;
    private RecyclerView recyclerView;
    NavigationActivityViewModel navigationActivityViewModel;
    String nombreUsuario;
    String fileName = "listaTareasJson";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        navigationActivityViewModel = new ViewModelProvider(requireActivity()) .get(NavigationActivityViewModel. class);

        init(view);

        ListAdapterTarea adapter = new ListAdapterTarea(listaTarea, getContext(), this);
        recyclerView.setAdapter(adapter);
        return view;
    }
    public void init(View view) {
        listaTarea = new ArrayList<>();
        recyclerView = view.findViewById(R.id.listElementsTareas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapterTarea = new ListAdapterTarea(listaTarea, getContext(), this);
        recyclerView.setAdapter(listAdapterTarea);

        if (navigationActivityViewModel != null) {
            navigationActivityViewModel.getListaTareas().observe(getViewLifecycleOwner(), listaTareas -> {
                listaTarea.clear(); // Clear the existing list to avoid duplications
                listaTarea.addAll(listaTareas); // Add all new items
                listAdapterTarea.notifyDataSetChanged();

                // Obtener la cantidad de elementos en listaTareas
                int cantidad = listaTareas.size();
                Log.d("msg-test-cantidadDeTareas", "Cantidad de tareas: " + cantidad);
                notificarImportanceDefault(cantidad); // Llama a tu método con la cantidad actualizada
            });
        } else {
            // Manejar el caso en el que navigationActivityViewModel es nulo
            Log.d("msg-test-cantidadDeTareas", "ViewModel es null");
        }
    }


    public void onItemClick(ListElementTarea item, int actionId) {
        if (actionId == R.id.btnAccion1) {
            // Acción específica para el botón 1 (editar)
            Intent intent = new Intent(getActivity(), MainActivity3.class);
            intent.putExtra("isEditing", true);
            intent.putExtra("ListElement", item);
            startActivity(intent);
        } else if (actionId == R.id.btnAccion2) {
            eliminarTarea(item);
        } else if (actionId == R.id.item_view) {
            // Acción cuando se hace clic en el itemView (editar)
            Intent intent2 = new Intent(getActivity(), MainActivity4.class);
            intent2.putExtra("ListElement", item);
            startActivity(intent2);
        } else {
            // Acción por defecto o manejo de otros casos
        }
    }

    private void eliminarTarea(ListElementTarea item) {
        // Elimina el elemento de la lista y notifica al adaptador
        listaTarea.remove(item);
        listAdapterTarea.notifyDataSetChanged();
        String fileName = "listaTareasJson";

        try (FileOutputStream fileOutputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            Gson gson = new Gson();
            String nuevaJsonData = gson.toJson(listaTarea.toArray(new ListElementTarea[0]));
            bufferedWriter.write(nuevaJsonData);
            Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo removido correctamente");
        } catch (IOException er) {
            Log.d("msg-test-guardarArchivoTextoComoJson", "Archivo no removido correctamente");
            er.printStackTrace();
        }
        String toastMessage = "Tarea completada";
        Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
        Intent intent3 = new Intent(getContext(), MainActivity2.class);
        startActivity(intent3);

    }
    public void notificarImportanceDefault(Integer candidad){

        String canal1 = "tareaspendientes";
        crearCanalesNotificacion(canal1);

        Intent intent = new Intent(getContext(), MainActivity2.class);
        intent.putExtra("pid",4616);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), canal1)
                .setSmallIcon(R.drawable.ic_notification_outline_black)
                .setContentTitle("Tareas Pendientes")
                .setContentText(candidad+" tareas en curso")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(2, notification);
        }

    }
    public void crearCanalesNotificacion(String canal1) {

        NotificationChannel channel = new NotificationChannel(canal1,
                "Canal Users Creation",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Canal para notificaciones de creación de perfiles de usuario con prioridad default");
        channel.enableVibration(true);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        pedirPermisos();
    }

    public void pedirPermisos() {
        // TIRAMISU = 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(getContext(), POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{POST_NOTIFICATIONS}, 101);
        }
    }
}