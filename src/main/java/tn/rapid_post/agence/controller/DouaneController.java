package tn.rapid_post.agence.controller;

import net.sf.jasperreports.engine.fill.EvaluationBoundAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.repo.douaneRepo;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.repo.UserRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DouaneController {
    @Autowired
    private douaneRepo douaneRepo;
    @Autowired
    private UserRepository appUserRepo;
    public AppUser findLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {


            String utilisateur = ((UserDetails) authentication.getPrincipal()).getUsername();


            Optional<AppUser> appUser1 = appUserRepo.findByUsername(utilisateur);
            return appUser1.orElse(null);

        } else return null;
    }

    @GetMapping("etatdouane")
    public String etatdouane(Model model) {

        boolean autorized = false;
        if (findLogged().getRoles().contains("ADMIN") || findLogged().getRoles().contains("AGENTB")){
            autorized =true;
        }
        model.addAttribute("autorized",autorized);
         LocalDate   date1 = LocalDate.now();
          LocalDate  date2 = LocalDate.now();
          model.addAttribute("date1",date1);

        List<Douane> colislise = new ArrayList<>();
        if (date1 != null && date2 != null) {
            colislise = douaneRepo.findBetweenDates(date1, date2)
                    .stream()
                    .map(d -> {
                        String numStr = d.getSequence().replaceAll("\\D+", "");
                        int sequenceNum = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
                        return new AbstractMap.SimpleEntry<>(d, sequenceNum);
                    })
                    .sorted(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue))
                    .map(AbstractMap.SimpleEntry::getKey)
                    .collect(Collectors.toList());      }
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
                        @RequestParam(value = "echec", required = false) String notValidated) {




        System.out.println("Sequence reçue = " + sequence);
        Douane douane = new Douane();
        model.addAttribute("notValidated", false);

        if (StringUtils.hasText(notValidated)) {
            model.addAttribute("notValidated", Boolean.parseBoolean(notValidated));
        }

        if (!StringUtils.hasText(colis)) {
            return "fraisdouane";
        }

        // Récupération du colis
        douane = douaneRepo.findByNumColis(colis);
        if (douane == null) {
            model.addAttribute("empty", true);
            return "fraisdouane";
        }

        // Si le colis est déjà livré
        if (douane.isDelivered()) {
            model.addAttribute("douane", douane);
            model.addAttribute("exist", true);
            return "fraisdouane";
        }

        model.addAttribute("exist", false);
        model.addAttribute("douane", douane);

        // Validation de la saisie (calcul)
        if (StringUtils.hasText(sequence) && StringUtils.hasText(droit)) {
            try {
                double droitValue = Double.parseDouble(droit);
                douane.setDroitDouane(droitValue);
                douaneRepo.save(douane);

                // Vérifie si la séquence est déjà utilisée par un autre colis
                Optional<Douane> existingWithSequence = douaneRepo.findBySequence(sequence);
                if (existingWithSequence.isPresent() &&
                        !existingWithSequence.get().getNumColis().equals(douane.getNumColis())) {
                    return "redirect:/dounecalc?colis=" + colis + "&echec=true";
                }

                // Calcul des frais
                douane.setDroitDouane(droitValue);
                douane.setSequence(sequence);

                long daysBetween = ChronoUnit.DAYS.between(douane.getDateArrivee(), LocalDate.now());
                double magasinage = Math.max(0, (daysBetween - 6) * douane.getNbColis());
                double fraisFixes = douane.getNbColis() * 6;
                double total = fraisFixes + magasinage + droitValue;

                douane.setFraisMagasin(magasinage);
                douane.setFraisDedouane(douane.getNbColis() * 4);
                douane.setFraisReemballage(douane.getNbColis() * 2);
                douane.setTotPayer(total);

                douaneRepo.save(douane);


                model.addAttribute("validated", true);
                model.addAttribute("daysBetween", daysBetween);

            } catch (NumberFormatException e) {
                return "redirect:/dounecalc?colis=" + colis + "&echec=true";
            }

        }

        return "fraisdouane";
    }


    @GetMapping("/avisedit")
    public String avisedit(Model model,
                           @RequestParam(value = "exist", required = false) boolean exist,
                           @RequestParam(value = "success", required = false) boolean success,
                           @RequestParam(value = "id", required = false) String id,
                           @RequestParam(value = "error",required = false)String error) {

model.addAttribute("date1",LocalDate.now());
        boolean echec=false;
        if (StringUtils.hasText(error)){
            echec=true;
        }
        model.addAttribute("echec",echec);

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
        if (StringUtils.hasText(id)) {
            Optional<Douane> douane = douaneRepo.findById(Long.parseLong(id));
            if (douane.isPresent()) {
                colis = (Douane) douane.get();
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
                return "redirect:/avisedit";

            }
        }
        colis = new Douane();

            if (douaneRepo.findByNumColis(numColis) != null ) {
                return "redirect:/avisedit?exist=" + true+"&colis="+colis.getNumColis();

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

        return "redirect:/avisedit";
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
        model.addAttribute("date1",date1);
        model.addAttribute("date2",date2);
        model.addAttribute("nbTot", nbTot);
        model.addAttribute("douaneTot", douaneTot);
        model.addAttribute("results", results);
        return "quinzaine";
    }

    @GetMapping("etatperiode")
    public String etatperiode(Model model, @RequestParam(value = "date1", required = false) LocalDate date1,
                              @RequestParam(value = "date2", required = false) LocalDate date2,
                              @RequestParam(value = "admin",required = false)String admin) {
if (StringUtils.hasText(admin)){
System.out.println("admin recu "+admin);
}
        if (date1 == null && date2 == null) {
            date1 = LocalDate.now();
            date2 = LocalDate.now();
        }
        List<Douane> colislise = new ArrayList<>();
        if (date1 != null && date2 != null) {
            colislise = douaneRepo.findBetweenDates(date1, date2)
                    .stream()
                    .map(d -> {
                        String numStr = d.getSequence().replaceAll("\\D+", "");
                        int sequenceNum = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
                        return new AbstractMap.SimpleEntry<>(d, sequenceNum);
                    })
                    .sorted(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue))
                    .map(AbstractMap.SimpleEntry::getKey)
                    .collect(Collectors.toList());
        }
        model.addAttribute("date1", date1);
        model.addAttribute("date2", date2);
        model.addAttribute("colislise", colislise);
        return "etatdouaneadmin";

    }
    @GetMapping("avisconsul")
    public String avisconsul(Model model,@RequestParam(value = "colis",required = false)String colis){
        Douane douane=new Douane();
        if(StringUtils.hasText(colis)){
            douane=douaneRepo.findByNumColis(colis);
            if (douane!=null){
                model.addAttribute("douane",douane);
            }

        }else {
            model.addAttribute("douane",new Douane());
        }

        return "avisconsul";
    }
    @PostMapping("delete")
    public String delete(@RequestParam(value = "id",required = true)long id,
                         @RequestParam(value = "dash",required = false)String dash){
        try {
            douaneRepo.deleteById(id);
            if (StringUtils.hasText(dash)){
                return "redirect:/dashdouane";
            }
            System.out.println("id recu pour suppression : "+id);
            return "redirect:/avisedit";
        }catch (Exception e){
            if (StringUtils.hasText(dash)){
                return "redirect:/dashdouane";
            }
            return "redirect:/avisedit?error="+true;
        }

    }
    @GetMapping("printquinzaine")
    public String printquinzaine(Model model,
                                 @RequestParam(value = "date1",required = false)LocalDate date1,
                                 @RequestParam(value = "date2",required = false)LocalDate date2){
        model.addAttribute("date1",date1);
        model.addAttribute("date2",date2);
        List<Douane> douaneList=douaneRepo.findBetweenDates(date1,date2);
        model.addAttribute("list",douaneList);
        return "printquinzaine";
    }

}