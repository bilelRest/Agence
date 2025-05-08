package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.repo.douaneRepo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
public class DouaneController {
    @Autowired
    private douaneRepo douaneRepo;

    @GetMapping("etatdouane")
    public String etatdouane(Model model) {


         LocalDate   date1 = LocalDate.now();
          LocalDate  date2 = LocalDate.now();
          model.addAttribute("date1",date1);

        List<Douane> colislise = new ArrayList<>();
        if (date1 != null && date2 != null) {
            colislise = douaneRepo.findBetweenDates(date1, date2);
        }
        model.addAttribute("date1", date1);
        model.addAttribute("date2", date2);
        model.addAttribute("colislise", colislise);

        return "etatdouane";
    }

    @GetMapping("/dounecalc")
    public String frais(Model model,
                        @RequestParam(value = "colis", required = false) String colis,
                        @RequestParam(value = "droit", required = false) String droit,
                        @RequestParam(value = "sequence", required = false) String sequence,
                        @RequestParam(value = "echec",required = false)String notValidated) {
        System.out.println("Sequence recu = "+sequence);
        Douane douane = new Douane();
        boolean exist = false;
        boolean empty = false;
        boolean validated = false;
        long daysBetween = 0;
        model.addAttribute("notValidated", false);
        if (StringUtils.hasText(notValidated)) {
            model.addAttribute("notValidated", Boolean.parseBoolean(notValidated));
        }

        if (StringUtils.hasText(sequence)){
            if (sequence.equals("0")){

                System.out.println("Sequence = 0");

                return "redirect:/dounecalc?colis="+colis+"&echec=true";
            }
            Douane douane1=douaneRepo.findByNumColis(colis);
            if (douane1!=null){
                if (douane1.getSequence()==Long.parseLong(sequence));
                {


                    return "redirect:/dounecalc?colis="+colis+"&echec=true";
                }
            }
        }
        if (StringUtils.hasText(colis) && StringUtils.hasText(droit) && StringUtils.hasText(sequence)) {

            validated = true;
        }


        if (colis != null) {

            douane = douaneRepo.findByNumColis(colis);
            if (douane == null) {
                empty = true;
                model.addAttribute("empty", empty);
                return "fraisdouane";
            }
            if (douane != null) {
                if (douane.isDelivered()) {
                    model.addAttribute("douane", douane);
                    exist = true;
                    model.addAttribute("exist", exist);
                    return "fraisdouane";
                }
            }
            double droit1 = 0;
            System.out.println("droit = " + droit);
            System.out.println("colis = " + colis);
            if (douane != null) {
                if (droit != "" && droit != null) {
                    droit1 = Double.parseDouble(droit);
                    System.out.println("droit1 = " + droit1);
                    douane.setDroitDouane(droit1);
                    douane.setSequence(Long.parseLong(sequence));


                }
                double magasinage = 0;
                daysBetween = ChronoUnit.DAYS.between(douane.getDateArrivee(), LocalDate.now());
                System.out.println(daysBetween);
                if (daysBetween < 7) {
                    douane.setTotPayer(douane.getNbColis() * 6 + douane.getDroitDouane());

                } else {
                    douane.setTotPayer((daysBetween - 6) * douane.getNbColis() + douane.getNbColis() * 6 + douane.getDroitDouane());
                    magasinage = (daysBetween - 6) * douane.getNbColis();


                }
                douane.setFraisDedouane(douane.getNbColis() * 4);
                douane.setFraisReemballage(douane.getNbColis() * 2);
                douane.setFraisMagasin(magasinage);


                douaneRepo.save(douane);


                model.addAttribute("validated", validated);
                model.addAttribute("exist",exist);

                model.addAttribute("daysBetween", daysBetween);
                model.addAttribute("douane", douane);
            }
        }


        return "fraisdouane";
    }


    @GetMapping("/avisedit")
    public String avisedit(Model model, @RequestParam(value = "exist", required = false) boolean exist,
                           @RequestParam(value = "success", required = false) boolean success,
                           @RequestParam(value = "id", required = false) String id) {
        Douane douane = new Douane();
        boolean edit = false;
        if (id != null && id != "") {
            Optional douane1 = douaneRepo.findById(Long.parseLong(id));
            if (douane1.isPresent()) {
                douane = (Douane) douane1.get();

                edit = true;
            } else {
                douane = new Douane();
            }
        }
        model.addAttribute("datear", LocalDate.now());
        model.addAttribute("douane", douane);
        model.addAttribute("edit", edit);
        List<Douane> colisList = new ArrayList<>();
        colisList = douaneRepo.findByPrintedFalse();
        model.addAttribute("success", success);
        model.addAttribute("exist", exist);
        model.addAttribute("colisList", colisList);
        return "avisedit";
    }

    @PostMapping("/addColis")
    public String addColis(@RequestParam(value = "numColis") String numColis,
                           @RequestParam(value = "nbColis") int nbColis,
                           @RequestParam(value = "poidColis") double poidColis,
                           @RequestParam(value = "nomDest") String nomDest,
                           @RequestParam(value = "observation", defaultValue = "false") boolean observation,
                           @RequestParam(value = "bloc") String bloc,
                           @RequestParam(value = "origin") String origin,
                           @RequestParam(value = "id", required = false) String id,
                           @RequestParam(value = "datear") LocalDate datear) {
        Douane colis = new Douane();
        if (id != null && !id.isEmpty()) {
            Optional<Douane> douane = douaneRepo.findById(Long.parseLong(id));
            if (douane.isPresent()) {
                colis = (Douane) douane.get();
            }
        } else {
            if (douaneRepo.findByNumColis(numColis) != null || douaneRepo.findByBloc(bloc) != null) {
                return "redirect:/avisedit?exist=" + true;

            }
        }
        System.out.println("Bloc recu : " + bloc);
        System.out.println("Date arrivee" + datear);
        colis.setDateArrivee(datear);

        colis.setNbColis(nbColis);
        colis.setBloc(bloc);
        colis.setOrigin(origin);
        colis.setNumColis(numColis);
        colis.setPoid(poidColis);
        colis.setNom(nomDest);
        colis.setObservation(observation ? "Sans facture" : "");
        colis.setDroitDouane(0);
        colis.setFraisMagasin(0);
        colis.setFraisDedouane(4);
        colis.setFraisReemballage(2);
        colis.setTotPayer(0);
        colis.setPrinted(false);
        colis.setDelivered(false);

        colis.setDateSortie(LocalDate.now());
        colis.setValidated(true);
        douaneRepo.save(colis);

        return "redirect:/avisedit?success=" + true;
    }

    @GetMapping("quinzaine")
    public String quinzaine(Model model,
                            @RequestParam(value = "date1", required = false) LocalDate date1,
                            @RequestParam(value = "date2", required = false) LocalDate date2) {
        List<Douane> results = new ArrayList<>();
        long nbTot = 0;
        long douaneTot = 0;
        if (date1 != null && date2 != null) {
            results = douaneRepo.findBetweenDates(date1, date2);
            for (Douane douane : results) {
                nbTot += douane.getNbColis();
                douaneTot += (long) douane.getDroitDouane();
            }
        }
        model.addAttribute("nbTot", nbTot);
        model.addAttribute("douaneTot", douaneTot);
        model.addAttribute("results", results);
        return "quinzaine";
    }

    @GetMapping("etatperiode")
    public String etatperiode(Model model, @RequestParam(value = "date1", required = false) LocalDate date1,
                              @RequestParam(value = "date2", required = false) LocalDate date2) {

        if (date1 == null && date2 == null) {
            date1 = LocalDate.now();
            date2 = LocalDate.now();
        }
        List<Douane> colislise = new ArrayList<>();
        if (date1 != null && date2 != null) {
            colislise = douaneRepo.findBetweenDates(date1, date2);
        }
        model.addAttribute("date1", date1);
        model.addAttribute("date2", date2);
        model.addAttribute("colislise", colislise);
        return "etatdouaneadmin";

    }

}