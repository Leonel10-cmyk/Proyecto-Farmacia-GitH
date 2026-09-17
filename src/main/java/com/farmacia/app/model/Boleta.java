package com.farmacia.app.model;

import java.sql.Date;

public class Boleta {
    private String nroboletas;
    private String moneda;
    private Date fechaemision;
    private String tipopago;
    private int idClientes;
    private int idVendedor;
    private double total;

    public String getNroboletas() {
        return nroboletas;
    }

    public void setNroboletas(String nroboletas) {
        this.nroboletas = nroboletas;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Date getFechaemision() {
        return fechaemision;
    }

    public void setFechaemision(Date fechaemision) {
        this.fechaemision = fechaemision;
    }

    public String getTipopago() {
        return tipopago;
    }

    public void setTipopago(String tipopago) {
        this.tipopago = tipopago;
    }

    public int getIdClientes() {
        return idClientes;
    }

    public void setIdClientes(int idClientes) {
        this.idClientes = idClientes;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
