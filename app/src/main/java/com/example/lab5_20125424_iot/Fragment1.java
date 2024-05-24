package com.example.lab5_20125424_iot;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
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

import com.example.lab5_20125424_iot.items.ListAdapterTarea;
import com.example.lab5_20125424_iot.items.ListElementTarea;
import com.example.lab5_20125424_iot.viewModels.NavigationActivityViewModel;

import java.util.ArrayList;

public class Fragment1 extends Fragment {
    private ArrayList<ListElementTarea> listaTarea;
    private ListAdapterTarea listAdapterTarea;
    private RecyclerView recyclerView;
    NavigationActivityViewModel navigationActivityViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        navigationActivityViewModel = new ViewModelProvider(requireActivity()) .get(NavigationActivityViewModel. class);
        init(view);
        return view;
    }
    public void init(View view) {
        listaTarea = new ArrayList<>();
        if (navigationActivityViewModel != null) {
        navigationActivityViewModel.getListaTareas().observe(getViewLifecycleOwner(), listaTareas -> {
                for (ListElementTarea p : listaTareas) {
                    listaTarea.add(p);
                    Log.d("msg-test", "Name3: " + listaTareas.size());
                }
            });
        } else {
            // Manejar el caso en el que navigationActivityViewModel es nulo
        }

        listAdapterTarea = new ListAdapterTarea(listaTarea, getContext(), item -> moveToDescription(item));
        recyclerView = view.findViewById(R.id.listAdapterTarea);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listAdapterTarea);
    }

    public void moveToDescription(ListAdapterTarea item) {
        Intent intent = new Intent(getContext(), MainActivity_1_Users_UserDetais.class);
        intent.putExtra("ListElement", item);
        startActivity(intent);
    }
}