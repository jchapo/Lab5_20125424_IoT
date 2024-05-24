package com.example.lab5_20125424_iot.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab5_20125424_iot.R;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterTarea extends RecyclerView.Adapter<ListAdapterTarea.ViewHolder> {
    private List<ListElementTarea> nData;
    private List<ListElementTarea> nDataFull; // Lista original sin filtrar
    private LayoutInflater nInflater;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ListElementTarea item, int actionId);
    }

    public ListAdapterTarea(List<ListElementTarea> itemList, Context context, OnItemClickListener listener) {
        this.nInflater = LayoutInflater.from(context);
        this.context = context;
        this.nData = itemList;
        this.nDataFull = new ArrayList<>(itemList); // Inicializar la lista completa
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = nInflater.inflate(R.layout.tarea, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(nData.get(position));
    }

    @Override
    public int getItemCount() {
        return nData.size();
    }

    public void setItems(List<ListElementTarea> items) {
        nData = items;
        nDataFull = new ArrayList<>(items); // Actualizar la lista completa al cambiar los datos
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, fecha, hora;
        Button btnAccion1, btnAccion2;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textviewTitulo);
            descripcion = itemView.findViewById(R.id.textviewDescripcion);
            fecha = itemView.findViewById(R.id.textviewFecha);
            hora = itemView.findViewById(R.id.textviewHora);
            btnAccion1 = itemView.findViewById(R.id.btnAccion1);
            btnAccion2 = itemView.findViewById(R.id.btnAccion2); // Asumiendo que este es el ID del segundo botón
        }

        void bindData(final ListElementTarea item) {
            titulo.setText(item.getTitulo());
            descripcion.setText(item.getDescripcion());
            fecha.setText(item.getFecha());
            hora.setText(item.getHora());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item, R.id.item_view);
                }
            });
            btnAccion1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item, R.id.btnAccion1);
                }
            });
            btnAccion2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item, R.id.btnAccion2);
                }
            });
        }
    }
}
