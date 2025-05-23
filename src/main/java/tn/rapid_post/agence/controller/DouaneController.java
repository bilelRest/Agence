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
import tn.rapid_post.agence.entity.HistoryDouane;
import tn.rapid_post.agence.repo.douaneRepo;
import tn.rapid_post.agence.repo.historyDouanerepo;
import tn.rapid_post.agence.sec.entity.AppRole;
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
    @Autowired
    private historyDouanerepo historyDouaneRepo;
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

        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("logged",findLogged().getUsername().toUpperCase());



        List<Douane> colislise = new ArrayList<>();
            colislise = douaneRepo.findBetweenDates(LocalDate.now(), LocalDate.now())
                    .stream()
                    .map(d -> {
                        String numStr = d.getSequence().replaceAll("\\D+", "");
                        int sequenceNum = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
                        return new AbstractMap.SimpleEntry<>(d, sequenceNum);
                    })
                    .sorted(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue))
                    .map(AbstractMap.SimpleEntry::getKey)
                    .collect(Collectors.toList());
        model.addAttribute("date1", LocalDate.now());
        model.addAttribute("date2", LocalDate.now());
        model.addAttribute("colislise", colislise);

        return "etatdouane";
    }
    @GetMapping("etatdouaneagent")
    public String etatDouaneAgent(Model model,@RequestParam(value = "print",required = false)String print) {

        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());



        List<Douane> colislise = new ArrayList<>();


      List<Douane>  toutColis = douaneRepo.findBetweenDatesUser(LocalDate.now(), LocalDate.now(),findLogged().getId());
       boolean validated=true;
        if (StringUtils.hasText(print)){
            for (Douane douane:toutColis){
                douane.setSituation(true);
                douane.setValidateSituation(true);
douaneRepo.save(douane);
            }
        }
        for (Douane douane:toutColis){
            if (!douane.isValidateSituation()){
                colislise.add(douane);

            }

        }


               colislise .stream()
                .map(d -> {
                    String numStr = d.getSequence().replaceAll("\\D+", "");
                    int sequenceNum = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
                    return new AbstractMap.SimpleEntry<>(d, sequenceNum);
                })
                .sorted(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue))
                .map(AbstractMap.SimpleEntry::getKey)
                .collect(Collectors.toList());
        model.addAttribute("date1", LocalDate.now());
        model.addAttribute("date2", LocalDate.now());
        model.addAttribute("colislise", colislise);

        return "etatdouaneagent";
    }
    @PostMapping("situationAgent")
    public String situationAgent(){
        List<Douane> colislise = new ArrayList<>();
        colislise = douaneRepo.findBetweenDatesUser(LocalDate.now(), LocalDate.now(),findLogged().getId());
        for (Douane douane:colislise){
            douane.setSituation(true);
            douane.setValidateSituation(true);
            douaneRepo.save(douane);
        }
        return "redirect:/etatdouaneagent?print="+true;
    }

    @GetMapping("/dounecalc")
    public String frais(Model model,
                        @RequestParam(value = "colis", required = false) String colis,
                        @RequestParam(value = "droit", required = false) String droit,
                        @RequestParam(value = "sequence", required = false) String sequence,
                        @RequestParam(value = "echec", required = false) String notValidated) {
//verification des droit d'utilisteur
        boolean isAdmin = false;
        boolean notPrinted=false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getUsername().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);

//verification si pas de num colis
        if (!StringUtils.hasText(colis)) {
            return "fraisdouane";
        }

        Douane douane = new Douane();

        // Récupération du colis
        douane = douaneRepo.findByNumColis(colis);
        if (douane == null) {
            model.addAttribute("empty", true);
            return "fraisdouane";
        }
        if (!douane.isPrinted()){
            notPrinted=true;
            model.addAttribute("notPrinted",notPrinted);

        }
        model.addAttribute("notPrinted",notPrinted);



        System.out.println("Sequence reçue = " + sequence);

        model.addAttribute("notValidated", false);

        if (StringUtils.hasText(notValidated)) {
            model.addAttribute("notValidated", Boolean.parseBoolean(notValidated));
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


                // Vérifie si la séquence est déjà utilisée par un autre colis
                List<Douane> existingWithSequence = douaneRepo.findBySequence(sequence);

                boolean sequenceValide = true;

                for (Douane d : existingWithSequence) {
                    if (Objects.equals(d.getNumColis(), douane.getNumColis())) {
                        // Même colis : modification → séquence autorisée
                        break;
                    }

                    if (d.getDateSortie().getYear() == LocalDate.now().getYear()) {
                        // Même séquence déjà utilisée cette année pour un autre colis → invalide
                        sequenceValide = false;
                    }
                }

                if (!sequenceValide) {
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
                douane.setAppUser(findLogged());
historyDouaneRepo.save(new HistoryDouane(douane.getDateArrivee(),douane.getDateSortie(),douane.getNumColis(),String.valueOf(douane.getTotPayer()),douane.getSequence(), findLogged().getUsername(),douane.getBloc(), "Calucl frais"));
                douaneRepo.save(douane);


                model.addAttribute("validated", true);
                model.addAttribute("daysBetween", daysBetween);

            } catch (NumberFormatException e) {
                return "redirect:/dounecalc?colis=" + colis + "&echec=true";
            }

        }

        return "fraisdouane";
    }

@GetMapping("setprinted")
public String setprinted(@RequestParam(value = "id")String id){

    Douane douane=new Douane();

        if (StringUtils.hasText(id)){
           douane=douaneRepo.findByNumColis(id);
            if (douane!=null){
                douane.setPrinted(true);
                douane.setAppUser(findLogged());
                historyDouaneRepo.save(new HistoryDouane(douane.getDateArrivee(),
                        douane.getDateSortie(),douane.getNumColis(),
                        String.valueOf(douane.getTotPayer()),douane.getSequence(),
                        findLogged().getUsername(),douane.getBloc(), "Impression"));

                douaneRepo.save(douane);

            }
        }
    return "redirect:/dounecalc?colis="+douane.getNumColis();
}
    @GetMapping("/avisedit")
    public String avisedit(Model model,
                           @RequestParam(value = "exist", required = false) boolean exist,
                           @RequestParam(value = "success", required = false) boolean success,
                           @RequestParam(value = "id", required = false) String id,
                           @RequestParam(value = "error",required = false)String error) {
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getUsername().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
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
                colis.setAppUser(findLogged());

                historyDouaneRepo.save(new HistoryDouane(colis.getDateArrivee(),
                        colis.getDateSortie(),colis.getNumColis(),
                        String.valueOf(colis.getTotPayer()),colis.getSequence(),
                        findLogged().getUsername(),douane.get().getBloc(), "Edition avis"));
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
        colis.setAppUser(findLogged());

        douaneRepo.save(colis);
      Douane douane=  douaneRepo.findByNumColis(colis.getNumColis());
        if (douane!=null){
            douane.setAppUser(findLogged());

            historyDouaneRepo.save(new HistoryDouane(douane.getDateArrivee(),
                    douane.getDateSortie(),douane.getNumColis(),
                    String.valueOf(douane.getTotPayer()),douane.getSequence(),
                    findLogged().getUsername(),douane.getBloc(), "Edition avis"));
        }

        return "redirect:/avisedit";
    }

    @GetMapping("quinzaine")
    public String quinzaine(Model model,
                            @RequestParam(value = "date1", required = false) LocalDate date1,
                            @RequestParam(value = "date2", required = false) LocalDate date2) {
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getUsername().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
        List<Douane> results = new ArrayList<>();
        long nbTot = 0;
        double douaneTot = 0;
        if (date1 != null && date2 != null) {
            results = douaneRepo.findBetweenDates(date1, date2)
                    .stream()
                    .map(d -> {
                        String numStr = d.getSequence().replaceAll("\\D+", "");
                        int sequenceNum = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
                        return new AbstractMap.SimpleEntry<>(d, sequenceNum);
                    })
                    .sorted(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue))
                    .map(AbstractMap.SimpleEntry::getKey)
                    .collect(Collectors.toList());
            for (Douane douane : results) {
                nbTot += douane.getNbColis();
                douaneTot += douane.getDroitDouane();
                System.out.println(douaneTot);
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
                              @RequestParam(value = "admin",required = false)String admin,
                              @RequestParam(value = "agent",required = false)String agent) {
        List<AppUser> agents=appUserRepo.findAll();
        model.addAttribute("agents",agents);
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getUsername().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
if (StringUtils.hasText(admin)){
System.out.println("admin recu "+admin);
}
        if (date1 == null && date2 == null) {
            date1 = LocalDate.now();
            date2 = LocalDate.now();
        }
        List<Douane> colislise = new ArrayList<>();
        if (date1 != null && date2 != null) {
            List<Douane> list=douaneRepo.findBetweenDates(date1, date2);
            if (StringUtils.hasText(agent)){
                for (Douane douane:list) {
                    if (douane.getAppUser().getUsername().equals(agent)) {
                        colislise.add(douane);
                    }
                }
            }else {
                colislise=list;

            }
                  colislise  .stream()
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
    @GetMapping("etatjourneeadmin")
    public String etatjourneeadmin(Model model) {
        List<AppUser> agents=appUserRepo.findAll();
        model.addAttribute("agents",agents);
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getUsername().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);


        List<Douane> colislise=douaneRepo.findBetweenDates(LocalDate.now(), LocalDate.now());

            colislise  .stream()
                    .map(d -> {
                        String numStr = d.getSequence().replaceAll("\\D+", "");
                        int sequenceNum = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
                        return new AbstractMap.SimpleEntry<>(d, sequenceNum);
                    })
                    .sorted(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue))
                    .map(AbstractMap.SimpleEntry::getKey)
                    .collect(Collectors.toList());

       model.addAttribute("date1",LocalDate.now());
       model.addAttribute("date2",LocalDate.now());
        model.addAttribute("colislise", colislise);
        return "etatjourneeadmin";

    }

    @GetMapping("avisconsul")
    public String avisconsul(Model model,@RequestParam(value = "colis",required = false)String colis,
                             @RequestParam(value = "echec",required = false)String echec){
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
        Douane douane=new Douane();
        boolean reprintnotdelivered=false;
        boolean reprintdelivered=false;

        if(StringUtils.hasText(colis)){
            douane=douaneRepo.findByNumColis(colis);

            if (douane!=null){
            if (douane.isDelivered()){
                System.out.println(douane.isDelivered());
                reprintdelivered=true;

            }else {
                reprintnotdelivered=true;
              }



        }
        }else {
            douane=new Douane();
        }
        model.addAttribute("reprintdelivered",reprintdelivered);
        model.addAttribute("reprintnotdelivered",reprintnotdelivered);
model.addAttribute("douane",douane);

        return "avisconsul";
    }
    @PostMapping("delete")
    public String delete(@RequestParam(value = "id",required = true)long id,
                         @RequestParam(value = "dash",required = false)String dash){



        try {
            Optional<Douane> douane=douaneRepo.findById(id);
System.out.println(douane.get().getNumColis());


            if (douane.isPresent()){
                System.out.println("Entréé dans suppression ");
                try {

                    historyDouaneRepo.save(new HistoryDouane(douane.get().getDateArrivee(),
                            douane.get().getDateSortie(),douane.get().getNumColis(),
                            String.valueOf(douane.get().getTotPayer()),douane.get().getSequence(),
                            findLogged().getUsername(),douane.get().getBloc(), "Suppression"));


                   douane.get().setAppUser(null);

                    douaneRepo.delete(douane.get());

//                    douaneRepo.delete(douane.get());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
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
    @GetMapping("/welcome")
    public String welcome(Model model,@RequestParam(value = "status",required = false)boolean status){
        model.addAttribute("status",status);
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);

        return "welcome";
    }
    @GetMapping("printquinzaine")
    public String printquinzaine(Model model,
                                 @RequestParam(value = "date1",required = false)LocalDate date1,
                                 @RequestParam(value = "date2",required = false)LocalDate date2){
        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("logged",findLogged().getNomPrenom().toUpperCase());
        model.addAttribute("isAdmin", isAdmin);
        boolean autorized = false;
        if (findLogged().getRoles().contains("ADMIN") ){
            autorized =true;
        }
        model.addAttribute("autorized",autorized);
        model.addAttribute("date1",date1);
        model.addAttribute("date2",date2);
        List<Douane> results=douaneRepo.findBetweenDates(date1,date2);
        results = douaneRepo.findBetweenDates(date1, date2)
                .stream()
                .map(d -> {
                    String numStr = d.getSequence().replaceAll("\\D+", "");
                    int sequenceNum = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
                    return new AbstractMap.SimpleEntry<>(d, sequenceNum);
                })
                .sorted(Comparator.comparingInt(AbstractMap.SimpleEntry::getValue))
                .map(AbstractMap.SimpleEntry::getKey)
                .collect(Collectors.toList());

        model.addAttribute("list",results);
        return "printquinzaine";
    }

}