package com.turnon.turnonapi.model;

import java.sql.Timestamp;

public class Turno {

    private int idTurno;
    private String codigoTurno;
    private Timestamp fecha;
    private int idCliente;
    private int idServicio;
    private int idEstadoTurno;
    private String ventanilla;

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public String getCodigoTurno() {
        return codigoTurno;
    }

    public void setCodigoTurno(String codigoTurno) {
        this.codigoTurno = codigoTurno;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public int getIdEstadoTurno() {
        return idEstadoTurno;
    }

    public void setIdEstadoTurno(int idEstadoTurno) {
        this.idEstadoTurno = idEstadoTurno;
    }

    public String getVentanilla() {
        return ventanilla;
    }

    public void setVentanilla(String ventanilla) {
        this.ventanilla = ventanilla;
    }
}