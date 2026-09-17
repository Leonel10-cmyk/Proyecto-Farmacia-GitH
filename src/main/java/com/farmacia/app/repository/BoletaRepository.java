package com.farmacia.app.repository;

import com.farmacia.app.model.Boleta;
import com.farmacia.app.model.BoletaDetalle;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BoletaRepository {
    private final JdbcTemplate jdbcTemplate;

    public BoletaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbc() { return jdbcTemplate; }

    // Genera el siguiente código correlativo: B2025001 → B2025011, etc.
    // Formato: B + año actual + correlativo 3 dígitos (ej. B2026011)
    
    public String generarNuevoCodigo() {
        String anio = String.valueOf(java.time.Year.now().getValue());
        String prefijo = "B" + anio;
        String sql = "SELECT MAX(NroBoleta) FROM BoletaVenta WHERE NroBoleta LIKE ?";
        String ultimo = jdbcTemplate.queryForObject(sql, String.class, prefijo + "%");
        int siguiente = 1;
        if (ultimo != null && ultimo.length() > prefijo.length()) {
            try {
                siguiente = Integer.parseInt(ultimo.substring(prefijo.length())) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("%s%03d", prefijo, siguiente);
    }

    // Lista todas las boletas con JOIN a Cliente y Vendedor (nombre + DNI)
    public List<BoletaDetalle> listarConCliente() {
        String sql = "SELECT B.NroBoleta, B.Moneda, CAST(B.FechaEmision AS DATE) AS FechaEmision, "
                + "B.TipoPago, B.ID_Cliente, C.ClienteNombre, C.NumeroDoc, "
                + "B.ID_Vendedor, V.VendedorNombre, B.Total "
                + "FROM BoletaVenta B "
                + "JOIN Cliente C  ON B.ID_Cliente  = C.ClienteID "
                + "JOIN Vendedor V ON B.ID_Vendedor = V.VendedorID "
                + "ORDER BY B.FechaEmision DESC, B.NroBoleta DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BoletaDetalle d = new BoletaDetalle();
            d.setNroBoleta(rs.getString("NroBoleta"));
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

    // Obtiene una boleta completa (encabezado + detalle) para el ticket
    public BoletaDetalle buscarConCliente(String nroBoleta) {
        String sql = "SELECT B.NroBoleta, B.Moneda, CAST(B.FechaEmision AS DATE) AS FechaEmision, "
                + "B.TipoPago, B.ID_Cliente, C.ClienteNombre, C.NumeroDoc, "
                + "B.ID_Vendedor, V.VendedorNombre, B.Total "
                + "FROM BoletaVenta B "
                + "JOIN Cliente C  ON B.ID_Cliente  = C.ClienteID "
                + "JOIN Vendedor V ON B.ID_Vendedor = V.VendedorID "
                + "WHERE B.NroBoleta = ?";
        List<BoletaDetalle> lista = jdbcTemplate.query(sql, (rs, rowNum) -> {
            BoletaDetalle d = new BoletaDetalle();
            d.setNroBoleta(rs.getString("NroBoleta"));
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
        }, nroBoleta);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // Lista simple (mantener compatibilidad con BoletaController)
    public List<Boleta> listar() {
        String sql = "SELECT NroBoleta, Moneda, FechaEmision, TipoPago, ID_Cliente, ID_Vendedor, Total "
                + "FROM BoletaVenta ORDER BY FechaEmision DESC, NroBoleta DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Boleta b = new Boleta();
            b.setNroboletas(rs.getString("NroBoleta"));
            b.setMoneda(rs.getString("Moneda"));
            b.setFechaemision(rs.getDate("FechaEmision"));
            b.setTipopago(rs.getString("TipoPago"));
            b.setIdClientes(rs.getInt("ID_Cliente"));
            b.setIdVendedor(rs.getInt("ID_Vendedor"));
            b.setTotal(rs.getDouble("Total"));
            return b;
        });
    }

    public Boleta buscar(String nroBoleta) {
        String sql = "SELECT NroBoleta, Moneda, FechaEmision, TipoPago, ID_Cliente, ID_Vendedor, Total "
                + "FROM BoletaVenta WHERE NroBoleta = ?";
        List<Boleta> boletas = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Boleta b = new Boleta();
            b.setNroboletas(rs.getString("NroBoleta"));
            b.setMoneda(rs.getString("Moneda"));
            b.setFechaemision(rs.getDate("FechaEmision"));
            b.setTipopago(rs.getString("TipoPago"));
            b.setIdClientes(rs.getInt("ID_Cliente"));
            b.setIdVendedor(rs.getInt("ID_Vendedor"));
            b.setTotal(rs.getDouble("Total"));
            return b;
        }, nroBoleta);
        return boletas.isEmpty() ? null : boletas.get(0);
    }

    // Registra el encabezado de boleta
    public void registrar(Boleta boleta) {
        String sql = "INSERT INTO BoletaVenta (NroBoleta, Moneda, FechaEmision, TipoPago, ID_Cliente, ID_Vendedor, Total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, boleta.getNroboletas(), boleta.getMoneda(), boleta.getFechaemision(),
                boleta.getTipopago(), boleta.getIdClientes(), boleta.getIdVendedor(), boleta.getTotal());
    }

    // Registra un ítem en DetalleBoleta y descuenta stock
    public void registrarDetalle(String nroBoleta, String codigoProducto, int cantidad) {
        jdbcTemplate.update(
            "INSERT INTO DetalleBoleta (NroBoleta, CodigoProducto, Cantidad) VALUES (?, ?, ?)",
            nroBoleta, codigoProducto, cantidad);
        jdbcTemplate.update(
            "UPDATE Producto SET Stock = Stock - ? WHERE CodigoProducto = ?",
            cantidad, codigoProducto);
    }

    // Recalcula y actualiza el Total de la boleta
    public void recalcularTotal(String nroBoleta) {
        String sql = "UPDATE BoletaVenta SET Total = ("
                + "SELECT SUM(DB.Cantidad * P.PrecioUnitario) "
                + "FROM DetalleBoleta DB JOIN Producto P ON DB.CodigoProducto = P.CodigoProducto "
                + "WHERE DB.NroBoleta = ?) "
                + "WHERE NroBoleta = ?";
        jdbcTemplate.update(sql, nroBoleta, nroBoleta);
    }

    public void actualizar(Boleta boleta) {
        String sql = "UPDATE BoletaVenta SET Moneda = ?, FechaEmision = ?, TipoPago = ?, ID_Cliente = ?, "
                + "ID_Vendedor = ?, Total = ? WHERE NroBoleta = ?";
        jdbcTemplate.update(sql, boleta.getMoneda(), boleta.getFechaemision(), boleta.getTipopago(),
                boleta.getIdClientes(), boleta.getIdVendedor(), boleta.getTotal(), boleta.getNroboletas());
    }

    public void eliminar(String nroBoleta) {
        jdbcTemplate.update("DELETE FROM BoletaVenta WHERE NroBoleta = ?", nroBoleta);
    }
}
