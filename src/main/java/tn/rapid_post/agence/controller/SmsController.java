

package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tn.rapid_post.agence.entity.B3;
import tn.rapid_post.agence.entity.RetourB3;
import tn.rapid_post.agence.repo.retourB3Rep;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.service.ApiService;
import tn.rapid_post.agence.repo.b3Repo;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Controller


public class SmsController {

    @Autowired
    private ApiService smsService;
    @Autowired
    private b3Repo b3Rep;
    @Autowired
    private retourB3Rep rep;
    @GetMapping("/sms")
    public String sms(Model model,
                      @RequestParam(value = "exist", required = false, defaultValue = "false") boolean exist,
                      @RequestParam(value = "status", required = false) String status,
                      @RequestParam(value = "id", required = false) String id) {

        B3 b31=new B3();
        model.addAttribute("b3mod",b31);

        if (StringUtils.hasText(id)&&!StringUtils.hasText(status)) {
            b31 = b3Rep.findByIdB3(Long.parseLong(id));
            if (b31 != null) {
                System.out.println("B3 trouvé pour modif " + b31.getIdB3());
            } else {
                System.out.println("Else null");
                b31 = new B3(); // fallback si l'ID est incorrect
            }
        } else {
            System.out.println("Else id vide");
            b31 = new B3(); // cas d'ajout
        }
        System.out.println("B3 trouvé pour modif apres elses " + b31.getIdB3());

        model.addAttribute("b3mod", b31);

        model.addAttribute("b3List", b3Rep.findByNotifiedFalse().stream()
                .sorted(Comparator.comparing(B3::getIdB3).reversed())
                .collect(Collectors.toList()));

        model.addAttribute("exist", exist);
        model.addAttribute("status", status);
        model.addAttribute("id", id);

        return "sms";
    }

    @PostMapping("/sms")
    public String sms(@RequestParam String tel,
                      @RequestParam String ref,
                      @RequestParam String post,
                      @RequestParam(value = "nom",defaultValue = "")String nom,
                      @RequestParam(value = "resend",required = false)boolean resend
                    ,@RequestParam(value = "id",required = false)String id ) throws UnsupportedEncodingException {

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
if (StringUtils.hasText(id)){
B3 b3=b3Rep.findByIdB3(Long.parseLong(id));
if (b3!=null){
    b3.setNom(nom);
    b3.setDestination(post);
    b3.setNumTel(Integer.parseInt(tel));
    b3.setNumB3(ref);
    b3Rep.save(b3);
    return "redirect:/sms";
}
}

        if(b3Rep.existsByNumB3(ref)){
            System.out.println("Entré dans if b3 existant");
            return "redirect:/sms?exist="+true;
        }if (post.isEmpty()){
            post="Agence";
        }
        B3 b3=new B3();
        b3.setNom(nom);
        b3.setDestination(post);
        b3.setNumTel(Integer.parseInt(tel));
        b3.setNumB3(ref);
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

//    @PostMapping("/findb3")
//    public String rechercherB3(@RequestParam(value = "query") String query,
//                               RedirectAttributes redirectAttributes) {
//
//        // Validation de l'entrée
//        if (!StringUtils.hasText(query)) {
//            redirectAttributes.addAttribute("error", true);
//            return "redirect:/b3consul";
//        }
//
//        // Recherche par numéro B3
//        B3 b3 = b3Rep.findByNumB3(query.trim());
//
//        if (b3 != null) {
//            return "redirect:/b3consul?query=" + b3.getIdB3();
//        } else {
//            redirectAttributes.addAttribute("error", true);
//            return "redirect:/b3consul";
//        }
//    }
    @GetMapping("/")
    public String home(Model model){
        return "template";
    }
    @GetMapping("/findb3")
    public String retourb3(Model model,
                           @RequestParam(value = "numb3", required = false) String numb3,
                           @RequestParam(value = "tel", required = false) String tel) {

        List<B3> b3List = new ArrayList<>();

        if (StringUtils.hasText(numb3)) {
            Optional<RetourB3> optionalRetour = rep.findBynumB3(numb3);
            if (optionalRetour.isPresent()) {
                RetourB3 retour = optionalRetour.get();
                B3 b3 = new B3();
                b3.setRetourId(retour.getId());
                b3.setNom(retour.getNomPrenB3Ro());
                b3.setNumB3(retour.getNumB3());
                try {
                    b3.setNumTel(retour.getB3().getNumTel());
                } catch (Exception e) {
                    b3.setNumTel(0);
                }
                b3List.add(b3);
            } else {
                B3 b3 = b3Rep.findByNumB3(numb3);
                if (b3 != null) {
                    b3.setRetourId(String.valueOf(b3.getIdB3()));
                    b3List.add(b3);
                }
            }
        }

        if (StringUtils.hasText(tel)) {
            List<RetourB3> retourList = rep.findByNumTel(Integer.parseInt(tel));
            for (RetourB3 retour : retourList) {
                B3 b3 = new B3();
                b3.setRetourId(retour.getId());
                b3.setNom(retour.getNomPrenB3Ro());
                b3.setNumB3(retour.getNumB3());
                try {
                    b3.setNumTel(retour.getB3().getNumTel());
                } catch (Exception e) {
                    b3.setNumTel(0);
                }
                b3List.add(b3);
            }

            // Si aucun retour trouvé, chercher directement dans la table B3
            if (retourList.isEmpty()) {
                List<B3> foundB3List = b3Rep.findByNumTel(Integer.parseInt(tel));
                for (B3 b3 : foundB3List) {
                    b3.setRetourId(String.valueOf(b3.getIdB3()));
                    b3List.add(b3);
                }
            }
        }

        model.addAttribute("results", b3List);
        return "b3consul";
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
    class FindB3{
        String id;
        String nom;
        String num;
        String tel;
}
}


