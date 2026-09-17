package com.farmacia.app.repository;

import com.farmacia.app.model.Producto;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductoRepository {
    private final JdbcTemplate jdbcTemplate;

    public ProductoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Producto> listar() {
        String sql = "SELECT CodigoProducto, DescripcionProducto, PrecioUnitario, Laboratorio, Lote, FechaVencimiento, Stock "
                + "FROM Producto ORDER BY CodigoProducto";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Producto p = new Producto();
            p.setCodigoProductos(rs.getString("CodigoProducto"));
            p.setDescripcionProductos(rs.getString("DescripcionProducto"));
            p.setPreciounitaria(rs.getDouble("PrecioUnitario"));
            p.setLaboratorio(rs.getString("Laboratorio"));
            p.setLote(rs.getString("Lote"));
            p.setFechaVencimiento(rs.getDate("FechaVencimiento"));
            p.setStock(rs.getInt("Stock"));
            return p;
        });
    }

    public Producto buscar(String codigo) {
        String sql = "SELECT CodigoProducto, DescripcionProducto, PrecioUnitario, Laboratorio, Lote, FechaVencimiento, Stock "
                + "FROM Producto WHERE CodigoProducto = ?";
        List<Producto> productos = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Producto p = new Producto();
            p.setCodigoProductos(rs.getString("CodigoProducto"));
            p.setDescripcionProductos(rs.getString("DescripcionProducto"));
            p.setPreciounitaria(rs.getDouble("PrecioUnitario"));
            p.setLaboratorio(rs.getString("Laboratorio"));
            p.setLote(rs.getString("Lote"));
            p.setFechaVencimiento(rs.getDate("FechaVencimiento"));
            p.setStock(rs.getInt("Stock"));
            return p;
        }, codigo);
        return productos.isEmpty() ? null : productos.get(0);
    }

    public void registrar(Producto producto) {
        String sql = "INSERT INTO Producto (CodigoProducto, DescripcionProducto, PrecioUnitario, Laboratorio, Lote, FechaVencimiento, Stock) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, producto.getCodigoProductos(), producto.getDescripcionProductos(),
                producto.getPreciounitaria(), producto.getLaboratorio(), producto.getLote(),
                producto.getFechaVencimiento(), producto.getStock());
    }

    public void actualizar(Producto producto) {
        String sql = "UPDATE Producto SET DescripcionProducto = ?, PrecioUnitario = ?, Laboratorio = ?, Lote = ?, "
                + "FechaVencimiento = ?, Stock = ? WHERE CodigoProducto = ?";
        jdbcTemplate.update(sql, producto.getDescripcionProductos(), producto.getPreciounitaria(),
                producto.getLaboratorio(), producto.getLote(), producto.getFechaVencimiento(),
                producto.getStock(), producto.getCodigoProductos());
    }

    public void eliminar(String codigo) {
        jdbcTemplate.update("DELETE FROM Producto WHERE CodigoProducto = ?", codigo);
    }

    // Busca productos por código o descripción (para el buscador live de ventas)
    public List<Producto> buscarPorTexto(String texto) {
        String like = "%" + texto + "%";
        String sql = "SELECT CodigoProducto, DescripcionProducto, PrecioUnitario, Laboratorio, Lote, FechaVencimiento, Stock "
                + "FROM Producto WHERE CodigoProducto LIKE ? OR DescripcionProducto LIKE ? ORDER BY CodigoProducto";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Producto p = new Producto();
            p.setCodigoProductos(rs.getString("CodigoProducto"));
            p.setDescripcionProductos(rs.getString("DescripcionProducto"));
            p.setPreciounitaria(rs.getDouble("PrecioUnitario"));
            p.setLaboratorio(rs.getString("Laboratorio"));
            p.setLote(rs.getString("Lote"));
            p.setFechaVencimiento(rs.getDate("FechaVencimiento"));
            p.setStock(rs.getInt("Stock"));
            return p;
        }, like, like);
    }
}
