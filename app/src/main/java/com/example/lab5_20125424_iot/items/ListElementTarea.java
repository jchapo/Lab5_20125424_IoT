package com.example.lab5_20125424_iot.items;

import java.io.Serializable;

public class ListElementTarea implements Serializable {
    public String tarea;
    public String descripcion;
    public String recordatorio;
    public String fecha;
    public String hora;
    public String importancia;

    public ListElementTarea(String tarea, String descripcion, String recordatorio, String fecha, String hora, String importancia) {
        this.tarea = tarea;
        this.descripcion = descripcion;
        this.recordatorio = recordatorio;
        this.fecha = fecha;
        this.hora = hora;
        this.importancia = importancia;
    }

    public String getImportancia() {
        return importancia;
    }

    public void setImportancia(String importancia) {
        this.importancia = importancia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(String recordatorio) {
        this.recordatorio = recordatorio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
