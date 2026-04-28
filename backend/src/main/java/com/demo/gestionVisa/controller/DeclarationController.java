package com.demo.gestionVisa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Declaration page (Loss declarations - Perte de carte/passeport)
 */
@Controller
@RequestMapping("/declaration")
@RequiredArgsConstructor
public class DeclarationController {

    /**
     * Display the "Perte de Carte Résident" (Loss of Resident Card) form
     */
    @GetMapping("/perte-carte")
    public String perteCarteResident() {
        return "declaration/perte-carte";
    }

    /**
     * Display the "Perte de Passeport" (Loss of Passport) form
     */
    @GetMapping("/perte-passport")
    public String pertePasseport() {
        return "declaration/perte-passport";
    }
}
