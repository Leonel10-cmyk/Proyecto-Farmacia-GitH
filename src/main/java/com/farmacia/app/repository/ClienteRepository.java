package com.farmacia.app.repository;

import com.farmacia.app.model.Cliente;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteRepository {
    private final JdbcTemplate jdbcTemplate;

    public ClienteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Cliente> listar() {
        String sql = "SELECT ClienteID, NumeroDoc, TipoDocum, ClienteNombre, ClienteDireccion "
                + "FROM Cliente ORDER BY ClienteID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Cliente c = new Cliente();
            c.setClientesID(rs.getInt("ClienteID"));
            c.setNumeroDoc(rs.getInt("NumeroDoc"));
            c.setTipoDocumento(rs.getString("TipoDocum"));
            c.setNombreClientes(rs.getString("ClienteNombre"));
            c.setClientesDireccion(rs.getString("ClienteDireccion"));
            return c;
        });
    }

    public Cliente buscarPorNumeroDoc(int numeroDoc) {
        String sql = "SELECT ClienteID, NumeroDoc, TipoDocum, ClienteNombre, ClienteDireccion "
                + "FROM Cliente WHERE NumeroDoc = ?";
        List<Cliente> clientes = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Cliente c = new Cliente();
            c.setClientesID(rs.getInt("ClienteID"));
            c.setNumeroDoc(rs.getInt("NumeroDoc"));
            c.setTipoDocumento(rs.getString("TipoDocum"));
            c.setNombreClientes(rs.getString("ClienteNombre"));
            c.setClientesDireccion(rs.getString("ClienteDireccion"));
            return c;
        }, numeroDoc);
        return clientes.isEmpty() ? null : clientes.get(0);
    }

    public void registrar(Cliente cliente) {
        String sql = "INSERT INTO Cliente (NumeroDoc, TipoDocum, ClienteNombre, ClienteDireccion) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, cliente.getNumeroDoc(), cliente.getTipoDocumento(),
                cliente.getNombreClientes(), cliente.getClientesDireccion());
    }

    public void actualizar(Cliente cliente) {
        String sql = "UPDATE Cliente SET NumeroDoc = ?, TipoDocum = ?, ClienteNombre = ?, ClienteDireccion = ? "
                + "WHERE ClienteID = ?";
        jdbcTemplate.update(sql, cliente.getNumeroDoc(), cliente.getTipoDocumento(),
                cliente.getNombreClientes(), cliente.getClientesDireccion(), cliente.getClientesID());
    }

    public void eliminar(int clienteID) {
        jdbcTemplate.update("DELETE FROM Cliente WHERE ClienteID = ?", clienteID);
    }
}
