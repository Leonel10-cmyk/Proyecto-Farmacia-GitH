package com.farmacia.app.model;

import java.sql.Date;

/**
 * DTO de vista para mostrar Factura con datos completos del cliente (nombre y DNI).
 */
public class FacturaDetalle {
    private String nroFactura;
    private String moneda;
    private Date fechaEmision;
    private String tipoPago;
    private int idCliente;
    private String nombreCliente;
    private String nroDocCliente;
    private int idVendedor;
    private String nombreVendedor;
    private double total;

    public String getNroFactura() { return nroFactura; }
    public void setNroFactura(String nroFactura) { this.nroFactura = nroFactura; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public Date getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Date fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNroDocCliente() { return nroDocCliente; }
    public void setNroDocCliente(String nroDocCliente) { this.nroDocCliente = nroDocCliente; }

    public int getIdVendedor() { return idVendedor; }
    public void setIdVendedor(int idVendedor) { this.idVendedor = idVendedor; }

    public String getNombreVendedor() { return nombreVendedor; }
    public void setNombreVendedor(String nombreVendedor) { this.nombreVendedor = nombreVendedor; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
