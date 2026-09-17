package com.farmacia.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // CAMBIO: Se eliminó "/" de la lista para no pelear con el LoginController
    @GetMapping({"/index", "/Index_Farmacia.jsp"})
    public String index() {
        return "index";
    }
}