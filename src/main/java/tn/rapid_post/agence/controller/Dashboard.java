package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tn.rapid_post.agence.repo.douaneRepo;

@Controller
public class Dashboard {
    @Autowired
    private douaneRepo douaneRepo;
    @GetMapping(value = "/dashdouane")
    public String dashboard(Model model){
        model.addAttribute("list",douaneRepo.findAll());
        return "dashdouane";
    }
}
