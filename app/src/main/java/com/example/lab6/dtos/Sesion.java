package com.example.lab6.dtos;

public class Sesion {

    private String curso;
    private String fecha;
    private double horas;
    private String tipo;

    public Sesion(){

    }
    public Sesion(String curso, String fecha, double horas, String tipo){
        this.curso = curso;
        this.fecha = fecha;
        this.horas = horas;
        this.tipo = tipo;
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
