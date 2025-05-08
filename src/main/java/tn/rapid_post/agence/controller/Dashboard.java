package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.repo.douaneRepo;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Dashboard {
    @Autowired
    private douaneRepo douaneRepo;
    @GetMapping(value = "/dashdouane")
    public String dashboard(Model model, @RequestParam(value = "etat",required = false)String etat1){
        System.out.println(etat1);
        List<Douane> douaneList=new ArrayList<>();
        boolean not=true;

        if (StringUtils.hasText(etat1)){

            if(etat1.equals("true")){
                not=false;
                model.addAttribute("etat",true);
                douaneList=douaneRepo.findByDeliveredTrue();
            }else if(etat1.equals("false")) {
                model.addAttribute("etat",false);
                not=false;

                douaneList = douaneRepo.findByDeliveredFalse();
            }
        }else {
            douaneList=douaneRepo.findAll();
        }
        model.addAttribute("not",not);


        model.addAttribute("list",douaneList);
        return "dashdouane";
    }
}
