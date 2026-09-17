package com.farmacia.app.controller;

import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @GetMapping("/")
    public String home() { return "home"; }

    @GetMapping("/login")
    public String login(@RequestParam String rol, HttpSession session) {
        session.setAttribute("rol", rol);
        return "login"; 
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String usuario, 
                             @RequestParam String password, 
                             HttpSession session, Model model) {
        
        String rol = (String) session.getAttribute("rol");

        // Acceso para Administrador
        if ("ADMINISTRADOR".equals(rol) && "gerencia".equals(usuario) && "admin2026".equals(password)) {
            session.setAttribute("usuario", usuario);
            return "redirect:/menu";
        }

        // Acceso para Vendedor
        if ("VENDEDOR".equals(rol) && "cajero".equals(usuario) && "farmacia123".equals(password)) {
            session.setAttribute("usuario", usuario);
            return "redirect:/menu";
        }

        model.addAttribute("error", "Credenciales incorrectas.");
        return "login";
    }

    @GetMapping("/menu")
    public String menu(HttpSession session) {
        // Esto permite que cualquiera que esté logueado entre al menú
        if (session.getAttribute("usuario") == null) return "redirect:/";
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}