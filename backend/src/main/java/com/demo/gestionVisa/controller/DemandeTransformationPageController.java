package com.demo.gestionVisa.controller;

import com.demo.gestionVisa.enums.TypePieceJustificative;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demandes-transformation")
public class DemandeTransformationPageController {

    @GetMapping("/nouvelle")
    public String formulaire(Model model) {
        model.addAttribute("typesPieces", TypePieceJustificative.values());
        return "demande-transformation/formulaire";
    }
}
