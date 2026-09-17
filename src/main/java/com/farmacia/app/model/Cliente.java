package com.farmacia.app.model;

public class Cliente {
    private int clientesID;
    private int numeroDoc;
    private String tipoDocumento;
    private String nombreClientes;
    private String clientesDireccion;

    public int getClientesID() {
        return clientesID;
    }

    public void setClientesID(int clientesID) {
        this.clientesID = clientesID;
    }

    public int getNumeroDoc() {
        return numeroDoc;
    }

    public void setNumeroDoc(int numeroDoc) {
        this.numeroDoc = numeroDoc;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNombreClientes() {
        return nombreClientes;
    }

    public void setNombreClientes(String nombreClientes) {
        this.nombreClientes = nombreClientes;
    }

    public String getClientesDireccion() {
        return clientesDireccion;
    }

    public void setClientesDireccion(String clientesDireccion) {
        this.clientesDireccion = clientesDireccion;
    }
}
