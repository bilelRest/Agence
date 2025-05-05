package tn.rapid_post.agence.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Dashboard {
    @GetMapping(value = "/dashboard")
    public String dashboard(Model model){
        model.addAttribute("test","Hello Dashborad from thymelaf controller");
        return "dashboard";
    }
}
