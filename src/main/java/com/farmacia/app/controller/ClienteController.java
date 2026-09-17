package com.farmacia.app.controller;

import com.farmacia.app.model.Cliente;
import com.farmacia.app.repository.ClienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClienteController {
    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/gestionclientes")
    public String gestionar(@RequestParam(defaultValue = "listar") String accion,
            @RequestParam(required = false) Integer ClienteID,
            @RequestParam(required = false) Integer NumeroDoc,
            @RequestParam(required = false) String TipoDocumento,
            @RequestParam(required = false) String NombreCliente,
            @RequestParam(required = false) String Direccion,
            Model model) {
        try {
            if ("buscar".equalsIgnoreCase(accion)) {
                Cliente cliente = NumeroDoc == null ? null : clienteRepository.buscarPorNumeroDoc(NumeroDoc);
                model.addAttribute("busquedaCliente", cliente);
                if (cliente == null) {
                    model.addAttribute("mensajes", "No se encontro el cliente.");
                }
            } else if ("registrar".equalsIgnoreCase(accion)) {
                clienteRepository.registrar(crearCliente(ClienteID, NumeroDoc, TipoDocumento, NombreCliente, Direccion));
                model.addAttribute("mensajes", "Cliente registrado correctamente.");
            } else if ("actualizar".equalsIgnoreCase(accion)) {
                clienteRepository.actualizar(crearCliente(ClienteID, NumeroDoc, TipoDocumento, NombreCliente, Direccion));
                model.addAttribute("mensajes", "Cliente actualizado correctamente.");
            } else if ("eliminar".equalsIgnoreCase(accion)) {
                clienteRepository.eliminar(ClienteID);
                model.addAttribute("mensajes", "Cliente eliminado correctamente.");
            }
        } catch (Exception ex) {
            model.addAttribute("mensajes", "Ocurrio un error: " + ex.getMessage());
        }
        model.addAttribute("listarClientes", clienteRepository.listar());
        return "clientes";
    }

    private Cliente crearCliente(Integer clienteID, Integer numeroDoc, String tipoDocumento,
            String nombreCliente, String direccion) {
        Cliente cliente = new Cliente();
        cliente.setClientesID(clienteID == null ? 0 : clienteID);
        cliente.setNumeroDoc(numeroDoc == null ? 0 : numeroDoc);
        cliente.setTipoDocumento(tipoDocumento);
        cliente.setNombreClientes(nombreCliente);
        cliente.setClientesDireccion(direccion);
        return cliente;
    }
}
