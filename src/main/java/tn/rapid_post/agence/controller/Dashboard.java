package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.repo.douaneRepo;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.repo.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class Dashboard {
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

    @GetMapping("/dashdouane")
    public String dashboard(Model model,
                            @RequestParam(value = "etat", required = false) String etat1,
                            @RequestParam(value = "key", required = false) String key,
                            @RequestParam(value = "date1", required = false) LocalDate date1,
                            @RequestParam(value = "date2", required = false) LocalDate date2,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {



        // Traitement des paramètres de filtre
        Boolean etat = null;
        boolean not = true;
        model.addAttribute("pageSizes", List.of(5, 10, 20, 50));

        if (StringUtils.hasText(etat1)) {
            if (etat1.equals("true")) {
                etat = true;
                not = false;
            } else if (etat1.equals("false")) {
                etat = false;
                not = false;
            }
        }

        Page<Douane> douanePage;
        Pageable pageable = PageRequest.of(page, size);

        if (date1 != null && date2 != null) {
            // Recherche par dates
            if (StringUtils.hasText(key)) {
                key = "%" + key.trim().toLowerCase() + "%";
                if (etat == null) {
                    douanePage = douaneRepo.searchBetweenDatesWithKey(date1, date2, key, pageable);
                } else {
                    douanePage = douaneRepo.searchBetweenDatesWithKeyAndEtat(date1, date2, key, etat, pageable);
                }
            } else {
                if (etat == null) {
                    douanePage = douaneRepo.findBetweenDates(date1, date2, pageable);
                } else {
                    douanePage = douaneRepo.findBetweenDatesByEtat(date1, date2, etat, pageable);
                }
            }
        } else {
            // Recherche sans dates
            if (StringUtils.hasText(key)) {
                key = "%" + key.trim().toLowerCase() + "%";
                if (etat == null) {
                    douanePage = douaneRepo.searchMultiFields(key, pageable);
                } else {
                    douanePage = douaneRepo.searchMultiFieldsByDelivered(key, etat, pageable);
                }
            } else {
                if (etat == null) {
                    douanePage = douaneRepo.findAll(pageable);
                } else {
                    douanePage = douaneRepo.findByDelivered(etat, pageable);
                }
            }
        }

        // Ajout des attributs au modèle
        model.addAttribute("date1", date1);
        model.addAttribute("date2", date2);
        model.addAttribute("list", douanePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", douanePage.getTotalPages());
        model.addAttribute("totalItems", douanePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("etat", etat);
        model.addAttribute("key", key != null ? key.replace("%", "") : "");
        model.addAttribute("not", not);

        return "dashdouane";
    }
}