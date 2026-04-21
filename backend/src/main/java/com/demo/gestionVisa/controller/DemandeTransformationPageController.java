package com.demo.gestionVisa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demandes-transformation")
public class DemandeTransformationPageController {

    @GetMapping("/nouvelle")
    public String formulaire() {
        return "demande-transformation/formulaire";
    }
}
