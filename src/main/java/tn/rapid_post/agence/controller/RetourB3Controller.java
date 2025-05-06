package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.rapid_post.agence.entity.RetourB3;
import tn.rapid_post.agence.repo.retourB3Rep;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class RetourB3Controller {
    @Autowired
    private retourB3Rep b3Rep;
    @GetMapping("/retourb3rcp")
    public String retourb3rcp(Model model,
                              @RequestParam(value = "numb3", required = false) String numb3,
                              @RequestParam(value = "name", required = false) String name) {
        List<RetourB3> results=new ArrayList<>();

        // Validation des paramètres
        if (StringUtils.hasText(numb3) && StringUtils.hasText(name)) {
            Optional<RetourB3> existingB3 = b3Rep.findBynumB3Ro(numb3);

            if (existingB3.isPresent()) {
                model.addAttribute("exist", true);
                model.addAttribute("message", "Le B3 existe déjà dans le système");
            } else {
                RetourB3 newB3 = new RetourB3(numb3, name);
                 b3Rep.save(newB3);
                Optional retourB3=b3Rep.findBynumB3Ro(numb3);
                if (retourB3.isPresent())
                results.add((RetourB3) retourB3.get());
                String id=retourB3.isPresent()? ((RetourB3) retourB3.get()).getId() :"";

                model.addAttribute("success", true);
                model.addAttribute("message", "B3 ajouté avec succès");
                model.addAttribute("id",id);
            }
        }

        // Ajout des résultats pour l'affichage
        model.addAttribute("results", results);

        return "recepb3";
    }
    @GetMapping("/retourb3")
    public String retourb3(Model model,
                           @RequestParam(value = "numb3", required = false) String numb3,
                           @RequestParam(value = "name", required = false) String name) {

        List<RetourB3> results = new ArrayList<>();

        try {
            if (StringUtils.hasText(numb3)) {
                // Recherche par numéro B3

              Optional<RetourB3> retourB3 = b3Rep.findBynumB3Ro(numb3);
              if (retourB3.isPresent()) {
                  results.add((RetourB3) retourB3.get());
              }
            }
        if (StringUtils.hasText(name)) {
                // Recherche par nom (insensible à la casse)
                results = b3Rep.findByNomPrenB3RoContainingIgnoreCase(name.trim());
            }

        } catch (NumberFormatException e) {
            model.addAttribute("error", "Format de numéro B3 invalide");
        }

        model.addAttribute("results", results);
        return "retourb3";
    }
}
