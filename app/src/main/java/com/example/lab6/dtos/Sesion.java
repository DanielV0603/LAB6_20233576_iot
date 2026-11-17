package com.example.lab6.dtos;

import com.google.firebase.firestore.DocumentId;

public class Sesion {

    @DocumentId
    private String id;
    private String uid;
    private String curso;
    private String fecha;   // formato dd/MM/yyyy
    private double horas;
    private String tipo;    // Teoría, Práctica o Laboratorio

    public Sesion() {
    }

    public Sesion(String uid, String curso, String fecha, double horas, String tipo) {
        this.uid = uid;
        this.curso = curso;
        this.fecha = fecha;
        this.horas = horas;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getHoras() {
        return horas;
    }

    public void setHoras(double horas) {
        this.horas = horas;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
