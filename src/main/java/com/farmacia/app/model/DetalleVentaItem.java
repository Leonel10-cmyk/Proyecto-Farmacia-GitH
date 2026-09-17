package com.farmacia.app.model;

/**
 * DTO que representa un ítem del carrito de ventas.
 * Combina datos del Producto con la cantidad seleccionada.
 */
public class DetalleVentaItem {
    private String codigoProducto;
    private String descripcion;
    private double precioUnitario;
    private int stock;
    private int cantidad;

    public DetalleVentaItem() {}

    public DetalleVentaItem(String codigoProducto, String descripcion, double precioUnitario, int stock, int cantidad) {
        this.codigoProducto = codigoProducto;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.stock = stock;
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
