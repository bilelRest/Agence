

package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tn.rapid_post.agence.entity.B3;
import tn.rapid_post.agence.service.ApiService;
import tn.rapid_post.agence.repo.b3Repo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller


public class SmsController {

    @Autowired
    private ApiService smsService;
    @Autowired
    private b3Repo b3Rep;
    @GetMapping("/sms")
    public String sms(Model model,@RequestParam(value = "exist",required = false) boolean exist,
                      @RequestParam(value = "status",required = false)String status,@RequestParam(value = "id",required = false)String id){
        String id2;
        model.addAttribute("id",id2=id==null?"":id);
        if (status!=null&& !status.equals("")){
            model.addAttribute("status", status.equals("true"));
        }

        model.addAttribute("b3List", b3Rep.findByNotifiedFalse().stream()
                //.sorted(Comparator.comparingLong(B3::getIdB3)) // ordre croissant
                .sorted(Comparator.comparingLong(B3::getIdB3).reversed()) // ordre décroissant
                .collect(Collectors.toList()));
        model.addAttribute("exist",exist);
        String name="Bilel";
        model.addAttribute("name",name);
        return "sms";
    }
    @PostMapping("/sms")
    public String sms(@RequestParam String tel,
                      @RequestParam String ref,
                      @RequestParam String post,
                      @RequestParam(value = "resend",required = false)boolean resend
                     ) throws UnsupportedEncodingException {
if (resend){
    B3 b3=b3Rep.findByNumB3(ref);
    if(b3!=null){;

            if(smsService.getApiData(b3))
        {
            return "redirect:/sms?status="+true+"&id="+b3.getIdB3();
        }else {

                return "redirect:/sms?status="+false+"&id="+b3.getIdB3();

            }
    }
}

        if(b3Rep.existsByNumB3(ref)){
            System.out.println("Entré dans if b3 existant");
            return "redirect:/sms?exist="+true;
        }if (post.isEmpty()){
            post="Agence";
        }
        B3 b3=new B3(ref,post,false,Integer.parseInt(tel));
        b3Rep.save(b3);


            if(smsService.getApiData(b3)) {
                return "redirect:/sms?status="+true+"&id="+b3.getIdB3();
            }
         else {

            return "redirect:/sms?status="+false+"&id="+b3.getIdB3();
        }
    }
    @GetMapping("/b3consul")
    public String consulterB3(Model model,
                              @RequestParam(value = "query", required = false) String query,
                              @RequestParam(value = "error", required = false) Boolean error) {

        // Ajout de l'attribut error si présent
        if (error != null && error) {
            model.addAttribute("error", "Aucun B3 trouvé avec cette référence");
        }

        // Traitement de la recherche seulement si query n'est pas vide
        if (StringUtils.hasText(query)) {
            try {
                Long id = Long.parseLong(query);
                b3Rep.findById(id)
                        .ifPresent(b3 -> model.addAttribute("b3", b3));
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Format d'ID invalide");
            }
        }

        return "b3consul";
    }

    @PostMapping("/findb3")
    public String rechercherB3(@RequestParam(value = "query") String query,
                               RedirectAttributes redirectAttributes) {

        // Validation de l'entrée
        if (!StringUtils.hasText(query)) {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/b3consul";
        }

        // Recherche par numéro B3
        B3 b3 = b3Rep.findByNumB3(query.trim());

        if (b3 != null) {
            return "redirect:/b3consul?query=" + b3.getIdB3();
        } else {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/b3consul";
        }
    }
    @GetMapping("/")
    public String home(Model model){
        return "template";
    }

//    @GetMapping("/send_sms")
//    public String sendSms(@RequestParam String numero,@RequestParam String message,@RequestParam String fact) {
//
//        try {
//            smsService.getApiData(numero,message,fact);
//            return "SMS envoyé avec succès.";
//        } catch (Exception e) {
//            return "Erreur lors de l'envoi du SMS.";
//        }
 //   }
}


