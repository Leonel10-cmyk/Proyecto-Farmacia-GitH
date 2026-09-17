package com.farmacia.app.model;

import java.sql.Date;

public class Producto {
    private String codigoProductos;
    private String descripcionProductos;
    private double preciounitaria;
    private String laboratorio;
    private String lote;
    private Date fechaVencimiento;
    private int stock;

    public String getCodigoProductos() {
        return codigoProductos;
    }

    public void setCodigoProductos(String codigoProductos) {
        this.codigoProductos = codigoProductos;
    }

    public String getDescripcionProductos() {
        return descripcionProductos;
    }

    public void setDescripcionProductos(String descripcionProductos) {
        this.descripcionProductos = descripcionProductos;
    }

    public double getPreciounitaria() {
        return preciounitaria;
    }

    public void setPreciounitaria(double preciounitaria) {
        this.preciounitaria = preciounitaria;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
