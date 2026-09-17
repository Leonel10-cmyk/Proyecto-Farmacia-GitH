package com.farmacia.app.controller;

import com.farmacia.app.model.Producto;
import com.farmacia.app.repository.ProductoRepository;
import java.sql.Date;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductoController {
    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/gestionproductos")
    public String gestionar(@RequestParam(defaultValue = "listar") String accion,
            @RequestParam(required = false) String Codigo,
            @RequestParam(required = false) String Descripcion,
            @RequestParam(required = false) Double Precio,
            @RequestParam(required = false) String Laboratorio,
            @RequestParam(required = false) String Lote,
            @RequestParam(required = false) String FechaVencimiento,
            @RequestParam(required = false) Integer Stock,
            Model model) {
        try {
            if ("buscar".equalsIgnoreCase(accion)) {
                Producto producto = productoRepository.buscar(Codigo);
                model.addAttribute("busquedaProducto", producto);
                if (producto == null) {
                    model.addAttribute("mensajes", "No se encontro el producto.");
                }
            } else if ("registrar".equalsIgnoreCase(accion)) {
                productoRepository.registrar(crearProducto(Codigo, Descripcion, Precio, Laboratorio, Lote, FechaVencimiento, Stock));
                model.addAttribute("mensajes", "Producto registrado correctamente.");
            } else if ("actualizar".equalsIgnoreCase(accion)) {
                productoRepository.actualizar(crearProducto(Codigo, Descripcion, Precio, Laboratorio, Lote, FechaVencimiento, Stock));
                model.addAttribute("mensajes", "Producto actualizado correctamente.");
            } else if ("eliminar".equalsIgnoreCase(accion)) {
                productoRepository.eliminar(Codigo);
                model.addAttribute("mensajes", "Producto eliminado correctamente.");
            }
        } catch (Exception ex) {
            model.addAttribute("mensajes", "Ocurrio un error: " + ex.getMessage());
        }
        model.addAttribute("listarProductos", productoRepository.listar());
        return "productos";
    }

    private Producto crearProducto(String codigo, String descripcion, Double precio, String laboratorio,
            String lote, String fechaVencimiento, Integer stock) {
        Producto producto = new Producto();
        producto.setCodigoProductos(codigo);
        producto.setDescripcionProductos(descripcion);
        producto.setPreciounitaria(precio == null ? 0 : precio);
        producto.setLaboratorio(laboratorio);
        producto.setLote(lote);
        producto.setFechaVencimiento(fechaVencimiento == null || fechaVencimiento.isBlank()
                ? null : Date.valueOf(fechaVencimiento));
        producto.setStock(stock == null ? 0 : stock);
        return producto;
    }
}
