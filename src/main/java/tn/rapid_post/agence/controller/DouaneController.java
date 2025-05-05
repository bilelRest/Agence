package tn.rapid_post.agence.controller;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.repo.douaneRepo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Controller
public class DouaneController {
    @Autowired
    private douaneRepo douaneRepo;
@GetMapping("etatdouane")
public String etatdouane(Model model,@RequestParam(value = "date1",required = false)LocalDate date1,
                         @RequestParam(value = "date2",required = false)LocalDate date2){

    if(date1==null&&date2==null){
        date1=LocalDate.now();
        date2=LocalDate.now();
    }
    List<Douane> colislise=new ArrayList<>();
    if (date1!=null&&date2!=null){
        colislise=douaneRepo.findBetweenDates(date1,date2);
    }
    model.addAttribute("date1",date1);
    model.addAttribute("date2",date2);
    model.addAttribute("colislise",colislise);

    return "etatdouane";
}
    @GetMapping("/dounecalc")
    public String frais(Model model,
                        @RequestParam(value = "colis",required = false)String colis,
                        @RequestParam(value = "droit" ,required = false)String droit){
    Douane douane=new Douane();
        long daysBetween=0;
    if (colis!=null){

        douane=douaneRepo.findByNumColis(colis);
        double droit1=0;
        System.out.println("droit = "+droit);
        System.out.println("colis = "+colis);
        if(douane!=null) {
            if (droit!=""&&droit!=null){
                 droit1=Double.parseDouble(droit);
                 System.out.println("droit1 = "+droit1);
                 douane.setDroitDouane(droit1);



            }
            daysBetween= ChronoUnit.DAYS.between(douane.getDateArrivee(), LocalDate.now());
            System.out.println(daysBetween);
            if(daysBetween<7){
                douane.setTotPayer(douane.getNbColis()*6+douane.getDroitDouane());
            }else {
                douane.setTotPayer(daysBetween*douane.getNbColis()+douane.getNbColis()*6+douane.getDroitDouane());

            }

            model.addAttribute("daysBetween",daysBetween);
            model.addAttribute("douane",douane);
        }
    }


        return "fraisdouane";
    }


@GetMapping("/avisedit")
public String avisedit(Model model,@RequestParam(value = "exist",required = false)boolean exist,
                       @RequestParam(value = "success",required = false)boolean success){

    List<Douane> colisList=new ArrayList<>();
    colisList=douaneRepo.findByPrintedFalse();
    model.addAttribute("success",success);
    model.addAttribute("exist",exist);
model.addAttribute("colisList",colisList);
    return "avisedit";
}
@PostMapping("/addColis")
    public String addColis(@RequestParam (value = "numColis")String numColis,
                           @RequestParam(value = "nbColis") int nbColis,
                           @RequestParam(value = "poidColis")double poidColis,
                           @RequestParam(value = "nomDest")String nomDest,
                           @RequestParam(value = "observation",defaultValue = "false")boolean observation,
                           @RequestParam(value = "bloc")String bloc,
                           @RequestParam(value = "origin")String origin){
    if (douaneRepo.findByNumColis(numColis)!=null || douaneRepo.findByBloc(bloc)!=null){
        return "redirect:/avisedit?exist="+true;

    }
    System.out.println("Bloc recu : "+bloc);
    Douane colis=new Douane();
    colis.setNbColis(nbColis);
    colis.setBloc(bloc);
    colis.setOrigin(origin);
    colis.setNumColis(numColis);
    colis.setPoid(poidColis);
    colis.setNom(nomDest);
    colis.setObservation(observation?"Sans facture":"");
    colis.setDroitDouane(0);
    colis.setFraisMagasin(0);
    colis.setFraisDedouane(4);
    colis.setFraisReemballage(2);
    colis.setTotPayer(0);
    colis.setPrinted(false);
    colis.setDelivered(false);
    colis.setDateArrivee(LocalDate.now());
    colis.setDateSortie(LocalDate.now());
    douaneRepo.save(colis);

    return "redirect:/avisedit?success="+true;
}

}
