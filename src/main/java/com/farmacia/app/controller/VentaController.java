package com.farmacia.app.controller;

import com.farmacia.app.model.*;
import com.farmacia.app.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Controller
public class VentaController {

    private final ProductoRepository productoRepo;
    private final ClienteRepository  clienteRepo;
    private final BoletaRepository   boletaRepo;
    private final FacturaRepository  facturaRepo;

    public VentaController(ProductoRepository productoRepo,
                           ClienteRepository clienteRepo,
                           BoletaRepository boletaRepo,
                           FacturaRepository facturaRepo) {
        this.productoRepo = productoRepo;
        this.clienteRepo  = clienteRepo;
        this.boletaRepo   = boletaRepo;
        this.facturaRepo  = facturaRepo;
    }

    // -----------------------------------------------------------------------
    // GET /ventas  — Pantalla principal del módulo de ventas
    // -----------------------------------------------------------------------
    @GetMapping("/ventas")
    public String ventas(HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/";
        model.addAttribute("productos",     productoRepo.listar());
        model.addAttribute("clientes",      clienteRepo.listar());
        model.addAttribute("sigBoleta",     boletaRepo.generarNuevoCodigo());
        model.addAttribute("sigFactura",    facturaRepo.generarNuevoCodigo());
        return "ventas";
    }

    // -----------------------------------------------------------------------
    // GET /ventas/buscar?q=texto  — Búsqueda AJAX de productos en tiempo real
    // -----------------------------------------------------------------------
    @GetMapping("/ventas/buscar")
    @ResponseBody
    public List<Map<String, Object>> buscar(@RequestParam String q) {
        List<Producto> productos = q == null || q.isBlank()
                ? productoRepo.listar()
                : productoRepo.buscarPorTexto(q);
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Producto p : productos) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("codigo",      p.getCodigoProductos());
            item.put("descripcion", p.getDescripcionProductos());
            item.put("precio",      p.getPreciounitaria());
            item.put("stock",       p.getStock());
            resultado.add(item);
        }
        return resultado;
    }

    // -----------------------------------------------------------------------
    // POST /ventas/registrar  — Registra la venta (boleta o factura)
    //
    // Recibe:
    //   tipo       = "BOLETA" | "FACTURA"
    //   idCliente  = id del cliente
    //   tipoPago   = Efectivo / Tarjeta / Yape / Transferencia
    //   moneda     = PEN / USD
    //   codigos[]  = array de códigos de producto
    //   cantidades[]= array de cantidades
    // -----------------------------------------------------------------------
    @PostMapping("/ventas/registrar")
    public String registrar(
            @RequestParam String tipo,
            @RequestParam int idCliente,
            @RequestParam String tipoPago,
            @RequestParam(defaultValue = "PEN") String moneda,
            @RequestParam(value = "codigos",    required = false) List<String>  codigos,
            @RequestParam(value = "cantidades", required = false) List<Integer> cantidades,
            HttpSession session,
            Model model) {

        if (session.getAttribute("usuario") == null) return "redirect:/";
        if (codigos == null || codigos.isEmpty()) {
            model.addAttribute("error", "Debe agregar al menos un producto.");
            return redireccionVentas(session, model);
        }

        // ID del vendedor: si es VENDEDOR usamos ID fijo 1 (cajero).
        // Si quieres mapearlo dinámicamente, agrega una tabla de usuarios.
        String rol = (String) session.getAttribute("rol");
        int idVendedor = "ADMINISTRADOR".equals(rol) ? 1 : 2;

        Date hoy = Date.valueOf(LocalDate.now());
        String codigo;

        try {
            if ("FACTURA".equalsIgnoreCase(tipo)) {
                codigo = facturaRepo.generarNuevoCodigo();
                Factura f = new Factura();
                f.setNroFactura(codigo);
                f.setMoneda(moneda);
                f.setFechaEmision(hoy);
                f.setTipoPago(tipoPago);
                f.setIdClientes(idCliente);
                f.setIdVendedor(idVendedor);
                f.setTotal(0);
                facturaRepo.registrar(f);
                for (int i = 0; i < codigos.size(); i++) {
                    int cant = (cantidades != null && i < cantidades.size()) ? cantidades.get(i) : 1;
                    facturaRepo.registrarDetalle(codigo, codigos.get(i), cant);
                }
                facturaRepo.recalcularTotal(codigo);
                return "redirect:/ventas/ticket?tipo=F&codigo=" + codigo;

            } else {
                codigo = boletaRepo.generarNuevoCodigo();
                Boleta b = new Boleta();
                b.setNroboletas(codigo);
                b.setMoneda(moneda);
                b.setFechaemision(hoy);
                b.setTipopago(tipoPago);
                b.setIdClientes(idCliente);
                b.setIdVendedor(idVendedor);
                b.setTotal(0);
                boletaRepo.registrar(b);
                for (int i = 0; i < codigos.size(); i++) {
                    int cant = (cantidades != null && i < cantidades.size()) ? cantidades.get(i) : 1;
                    boletaRepo.registrarDetalle(codigo, codigos.get(i), cant);
                }
                boletaRepo.recalcularTotal(codigo);
                return "redirect:/ventas/ticket?tipo=B&codigo=" + codigo;
            }
        } catch (Exception ex) {
            model.addAttribute("error", "Error al registrar: " + ex.getMessage());
            return redireccionVentas(session, model);
        }
    }

    // -----------------------------------------------------------------------
    // GET /ventas/ticket?tipo=B&codigo=B2026001  — Muestra el ticket imprimible
    // -----------------------------------------------------------------------
    @GetMapping("/ventas/ticket")
    public String ticket(@RequestParam String tipo,
                         @RequestParam String codigo,
                         HttpSession session,
                         Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/";

        if ("F".equalsIgnoreCase(tipo)) {
            FacturaDetalle fd = facturaRepo.buscarConCliente(codigo);
            if (fd == null) return "redirect:/ventas";
            List<Map<String, Object>> items = obtenerDetalleFactura(codigo);
            model.addAttribute("documento", fd);
            model.addAttribute("items",     items);
            model.addAttribute("tipoDoc",   "FACTURA");
            model.addAttribute("nroDoc",    fd.getNroFactura());
        } else {
            BoletaDetalle bd = boletaRepo.buscarConCliente(codigo);
            if (bd == null) return "redirect:/ventas";
            List<Map<String, Object>> items = obtenerDetalleBoleta(codigo);
            model.addAttribute("documento", bd);
            model.addAttribute("items",     items);
            model.addAttribute("tipoDoc",   "BOLETA");
            model.addAttribute("nroDoc",    bd.getNroBoleta());
        }
        return "ticket";
    }

    // -----------------------------------------------------------------------
    // POST /ventas/cliente/nuevo — Registra un cliente nuevo desde AJAX
    // Devuelve JSON: { id, nombre, nroDoc } o { error }
    // -----------------------------------------------------------------------
    @PostMapping("/ventas/cliente/nuevo")
    @ResponseBody
    public Map<String, Object> nuevoCliente(
            @RequestParam String tipoDoc,
            @RequestParam String nroDoc,
            @RequestParam String nombre,
            @RequestParam(required = false, defaultValue = "") String direccion,
            HttpSession session) {

        Map<String, Object> resp = new LinkedHashMap<>();
        if (session.getAttribute("usuario") == null) {
            resp.put("error", "Sesión expirada."); return resp;
        }
        if (nroDoc.isBlank() || nombre.isBlank()) {
            resp.put("error", "Documento y nombre son obligatorios."); return resp;
        }
        try {
            Cliente c = new Cliente();
            c.setTipoDocumento(tipoDoc);
            c.setNumeroDoc(Integer.parseInt(nroDoc.trim()));
            c.setNombreClientes(nombre.trim());
            c.setClientesDireccion(direccion.isBlank() ? null : direccion.trim());
            clienteRepo.registrar(c);

            // Recuperar el ID recién insertado buscando por número de documento
            Cliente guardado = clienteRepo.buscarPorNumeroDoc(Integer.parseInt(nroDoc.trim()));
            if (guardado != null) {
                resp.put("id",     guardado.getClientesID());
                resp.put("nombre", guardado.getNombreClientes());
                resp.put("nroDoc", String.valueOf(guardado.getNumeroDoc()));
            } else {
                resp.put("error", "Cliente registrado pero no se pudo recuperar el ID.");
            }
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.contains("UNIQUE")) {
                resp.put("error", "Ya existe un cliente con ese número de documento.");
            } else {
                resp.put("error", "Error al registrar: " + msg);
            }
        }
        return resp;
    }


    private String redireccionVentas(HttpSession session, Model model) {
        model.addAttribute("productos",  productoRepo.listar());
        model.addAttribute("clientes",   clienteRepo.listar());
        model.addAttribute("sigBoleta",  boletaRepo.generarNuevoCodigo());
        model.addAttribute("sigFactura", facturaRepo.generarNuevoCodigo());
        return "ventas";
    }

    private List<Map<String, Object>> obtenerDetalleBoleta(String nroBoleta) {
        String sql = "SELECT P.CodigoProducto, P.DescripcionProducto, DB.Cantidad, "
                + "P.PrecioUnitario, CAST(DB.Cantidad * P.PrecioUnitario AS DECIMAL(12,2)) AS SubTotal "
                + "FROM DetalleBoleta DB "
                + "JOIN Producto P ON DB.CodigoProducto = P.CodigoProducto "
                + "WHERE DB.NroBoleta = ?";
        return boletaRepo.getJdbc().query(sql, (rs, i) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("codigo",      rs.getString("CodigoProducto"));
            m.put("descripcion", rs.getString("DescripcionProducto"));
            m.put("cantidad",    rs.getInt("Cantidad"));
            m.put("precio",      rs.getDouble("PrecioUnitario"));
            m.put("subtotal",    rs.getDouble("SubTotal"));
            return m;
        }, nroBoleta);
    }

    private List<Map<String, Object>> obtenerDetalleFactura(String nroFactura) {
        String sql = "SELECT P.CodigoProducto, P.DescripcionProducto, DF.Cantidad, "
                + "P.PrecioUnitario, CAST(DF.Cantidad * P.PrecioUnitario AS DECIMAL(12,2)) AS SubTotal "
                + "FROM DetalleFactura DF "
                + "JOIN Producto P ON DF.CodigoProducto = P.CodigoProducto "
                + "WHERE DF.NroFactura = ?";
        return facturaRepo.getJdbc().query(sql, (rs, i) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("codigo",      rs.getString("CodigoProducto"));
            m.put("descripcion", rs.getString("DescripcionProducto"));
            m.put("cantidad",    rs.getInt("Cantidad"));
            m.put("precio",      rs.getDouble("PrecioUnitario"));
            m.put("subtotal",    rs.getDouble("SubTotal"));
            return m;
        }, nroFactura);
    }
}
