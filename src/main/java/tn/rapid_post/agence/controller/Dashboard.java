package tn.rapid_post.agence.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
                            @RequestParam(value = "key", required = false) String key) {

        boolean isAdmin = false;
        for (AppRole appRole : findLogged().getRoles()) {
            if ("ADMIN".equals(appRole.getName())) {
                isAdmin = true;
                break;
            }
        }
        model.addAttribute("isAdmin", isAdmin);

        List<Douane> douaneList = new ArrayList<>();
        boolean not = true;
        Boolean etat = null;

        if (StringUtils.hasText(etat1)) {
            if (etat1.equals("true")) {
                etat = true;
                not = false;
            } else if (etat1.equals("false")) {
                etat = false;
                not = false;
            }
        }

        if (StringUtils.hasText(key)) {
            key = "%" + key.trim().toLowerCase() + "%";
            if (etat == null) {
                douaneList = douaneRepo.searchMultiFields(key);
            } else if (etat) {
                douaneList = douaneRepo.searchMultiFieldsByDelivered(key, true);
            } else {
                douaneList = douaneRepo.searchMultiFieldsByDelivered(key, false);
            }
        } else {
            if (etat == null) {
                douaneList = douaneRepo.findAll();
            } else if (etat) {
                douaneList = douaneRepo.findByDeliveredTrue();
            } else {
                douaneList = douaneRepo.findByDeliveredFalse();
            }
        }

        model.addAttribute("list", douaneList);
        model.addAttribute("etat", etat);
        model.addAttribute("key", key != null ? key.replace("%", "") : "");
        model.addAttribute("not", not);
        return "dashdouane";
    }

}
