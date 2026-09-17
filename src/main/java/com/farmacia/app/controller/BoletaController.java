package com.farmacia.app.controller;

import com.farmacia.app.model.BoletaDetalle;
import com.farmacia.app.model.Boleta;
import com.farmacia.app.repository.BoletaRepository;
import com.farmacia.app.repository.FacturaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;

@Controller
public class BoletaController {
    private final BoletaRepository boletaRepository;
    private final FacturaRepository facturaRepository;

    public BoletaController(BoletaRepository boletaRepository, FacturaRepository facturaRepository) {
        this.boletaRepository = boletaRepository;
        this.facturaRepository = facturaRepository;
    }

    @GetMapping("/gestionboletas")
    public String gestionar(@RequestParam(defaultValue = "listar") String accion,
            @RequestParam(required = false) String Codigo,
            @RequestParam(required = false) String Moneda,
            @RequestParam(required = false) String FechaEmision,
            @RequestParam(required = false) String tipopagos,
            @RequestParam(required = false) Integer idClientes,
            @RequestParam(required = false) Integer idvendedor,
            @RequestParam(required = false) Double Total,
            Model model) {
        try {
            if ("Buscar".equalsIgnoreCase(accion)) {
                Boleta boleta = boletaRepository.buscar(Codigo);
                model.addAttribute("busquedaBoletas", boleta);
                if (boleta == null) {
                    model.addAttribute("mensajes", "No se encontró la boleta.");
                }
            } else if ("registrar".equalsIgnoreCase(accion)) {
                boletaRepository.registrar(crearBoleta(Codigo, Moneda, FechaEmision, tipopagos, idClientes, idvendedor, Total));
                model.addAttribute("mensajes", "Boleta registrada correctamente.");
            } else if ("actualizar".equalsIgnoreCase(accion)) {
                boletaRepository.actualizar(crearBoleta(Codigo, Moneda, FechaEmision, tipopagos, idClientes, idvendedor, Total));
                model.addAttribute("mensajes", "Boleta actualizada correctamente.");
            } else if ("eliminar".equalsIgnoreCase(accion)) {
                boletaRepository.eliminar(Codigo);
                model.addAttribute("mensajes", "Boleta eliminada correctamente.");
            }
        } catch (Exception ex) {
            model.addAttribute("mensajes", "Ocurrió un error: " + ex.getMessage());
        }

        // Usar DTOs con nombre y DNI de cliente
        model.addAttribute("listarBoletas",   boletaRepository.listarConCliente());
        model.addAttribute("listarFacturas",  facturaRepository.listarConCliente());
        return "boletas";
    }

    private Boleta crearBoleta(String codigo, String moneda, String fechaEmision, String tipoPago,
            Integer idClientes, Integer idVendedor, Double total) {
        Boleta boleta = new Boleta();
        boleta.setNroboletas(codigo);
        boleta.setMoneda(moneda);
        boleta.setFechaemision(fechaEmision == null || fechaEmision.isBlank() ? null : Date.valueOf(fechaEmision));
        boleta.setTipopago(tipoPago);
        boleta.setIdClientes(idClientes == null ? 0 : idClientes);
        boleta.setIdVendedor(idVendedor == null ? 0 : idVendedor);
        boleta.setTotal(total == null ? 0 : total);
        return boleta;
    }
}
