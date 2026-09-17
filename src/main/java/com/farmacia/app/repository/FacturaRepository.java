package com.farmacia.app.repository;

import com.farmacia.app.model.Factura;
import com.farmacia.app.model.FacturaDetalle;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FacturaRepository {
    private final JdbcTemplate jdbcTemplate;

    public FacturaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbc() { return jdbcTemplate; }

    // -----------------------------------------------------------------
    // Genera el siguiente código correlativo: F2025010 → F2026011, etc.
    // -----------------------------------------------------------------
    public String generarNuevoCodigo() {
        String anio = String.valueOf(java.time.Year.now().getValue());
        String prefijo = "F" + anio;
        String sql = "SELECT MAX(NroFactura) FROM Factura WHERE NroFactura LIKE ?";
        String ultimo = jdbcTemplate.queryForObject(sql, String.class, prefijo + "%");
        int siguiente = 1;
        if (ultimo != null && ultimo.length() > prefijo.length()) {
            try {
                siguiente = Integer.parseInt(ultimo.substring(prefijo.length())) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("%s%03d", prefijo, siguiente);
    }

    // Lista todas las facturas con JOIN a Cliente y Vendedor
    public List<FacturaDetalle> listarConCliente() {
        String sql = "SELECT F.NroFactura, F.Moneda, CAST(F.FechaEmision AS DATE) AS FechaEmision, "
                + "F.TipoPago, F.ID_Cliente, C.ClienteNombre, C.NumeroDoc, "
                + "F.ID_Vendedor, V.VendedorNombre, F.Total "
                + "FROM Factura F "
                + "JOIN Cliente C  ON F.ID_Cliente  = C.ClienteID "
                + "JOIN Vendedor V ON F.ID_Vendedor = V.VendedorID "
                + "ORDER BY F.FechaEmision DESC, F.NroFactura DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            FacturaDetalle d = new FacturaDetalle();
            d.setNroFactura(rs.getString("NroFactura"));
            d.setMoneda(rs.getString("Moneda"));
            d.setFechaEmision(rs.getDate("FechaEmision"));
            d.setTipoPago(rs.getString("TipoPago"));
            d.setIdCliente(rs.getInt("ID_Cliente"));
            d.setNombreCliente(rs.getString("ClienteNombre"));
            d.setNroDocCliente(rs.getString("NumeroDoc"));
            d.setIdVendedor(rs.getInt("ID_Vendedor"));
            d.setNombreVendedor(rs.getString("VendedorNombre"));
            d.setTotal(rs.getDouble("Total"));
            return d;
        });
    }

    // Obtiene una factura completa (encabezado) para el ticket
    public FacturaDetalle buscarConCliente(String nroFactura) {
        String sql = "SELECT F.NroFactura, F.Moneda, CAST(F.FechaEmision AS DATE) AS FechaEmision, "
                + "F.TipoPago, F.ID_Cliente, C.ClienteNombre, C.NumeroDoc, "
                + "F.ID_Vendedor, V.VendedorNombre, F.Total "
                + "FROM Factura F "
                + "JOIN Cliente C  ON F.ID_Cliente  = C.ClienteID "
                + "JOIN Vendedor V ON F.ID_Vendedor = V.VendedorID "
                + "WHERE F.NroFactura = ?";
        List<FacturaDetalle> lista = jdbcTemplate.query(sql, (rs, rowNum) -> {
            FacturaDetalle d = new FacturaDetalle();
            d.setNroFactura(rs.getString("NroFactura"));
            d.setMoneda(rs.getString("Moneda"));
            d.setFechaEmision(rs.getDate("FechaEmision"));
            d.setTipoPago(rs.getString("TipoPago"));
            d.setIdCliente(rs.getInt("ID_Cliente"));
            d.setNombreCliente(rs.getString("ClienteNombre"));
            d.setNroDocCliente(rs.getString("NumeroDoc"));
            d.setIdVendedor(rs.getInt("ID_Vendedor"));
            d.setNombreVendedor(rs.getString("VendedorNombre"));
            d.setTotal(rs.getDouble("Total"));
            return d;
        }, nroFactura);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // Lista simple (compatibilidad)
    public List<Factura> listar() {
        String sql = "SELECT NroFactura, ID_Cliente, ID_Vendedor, Moneda, FechaEmision, TipoPago, Total "
                + "FROM Factura ORDER BY FechaEmision DESC, NroFactura DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Factura f = new Factura();
            f.setNroFactura(rs.getString("NroFactura"));
            f.setIdClientes(rs.getInt("ID_Cliente"));
            f.setIdVendedor(rs.getInt("ID_Vendedor"));
            f.setMoneda(rs.getString("Moneda"));
            f.setFechaEmision(rs.getDate("FechaEmision"));
            f.setTipoPago(rs.getString("TipoPago"));
            f.setTotal(rs.getDouble("Total"));
            return f;
        });
    }

    // Registra el encabezado de factura
    public void registrar(Factura factura) {
        String sql = "INSERT INTO Factura (NroFactura, Moneda, FechaEmision, TipoPago, ID_Cliente, ID_Vendedor, Total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, factura.getNroFactura(), factura.getMoneda(), factura.getFechaEmision(),
                factura.getTipoPago(), factura.getIdClientes(), factura.getIdVendedor(), factura.getTotal());
    }

    // Registra un ítem en DetalleFactura y descuenta stock
    public void registrarDetalle(String nroFactura, String codigoProducto, int cantidad) {
        jdbcTemplate.update(
            "INSERT INTO DetalleFactura (NroFactura, CodigoProducto, Cantidad) VALUES (?, ?, ?)",
            nroFactura, codigoProducto, cantidad);
        jdbcTemplate.update(
            "UPDATE Producto SET Stock = Stock - ? WHERE CodigoProducto = ?",
            cantidad, codigoProducto);
    }

    // Recalcula y actualiza el Total de la factura
    public void recalcularTotal(String nroFactura) {
        String sql = "UPDATE Factura SET Total = ("
                + "SELECT SUM(DF.Cantidad * P.PrecioUnitario) "
                + "FROM DetalleFactura DF JOIN Producto P ON DF.CodigoProducto = P.CodigoProducto "
                + "WHERE DF.NroFactura = ?) "
                + "WHERE NroFactura = ?";
        jdbcTemplate.update(sql, nroFactura, nroFactura);
    }
}
