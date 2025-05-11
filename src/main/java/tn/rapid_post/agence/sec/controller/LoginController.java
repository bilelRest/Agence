package tn.rapid_post.agence.sec.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.session.InMemoryWebSessionStore;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.AppUser;
import tn.rapid_post.agence.sec.entity.Permission;
import tn.rapid_post.agence.sec.repo.RoleRepository;
import tn.rapid_post.agence.sec.repo.UserRepository;
import tn.rapid_post.agence.sec.service.CustomUserDetailsService;

import java.net.Authenticator;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @GetMapping("loginpath")
    public String afterLoginUser(HttpServletRequest request, HttpSession session) {
        String username = request.getUserPrincipal().getName();

        if (StringUtils.hasText(username)) {
            System.out.println("=== Authentification réussie ===");
            System.out.println("Nom d'utilisateur connecté : " + username);

            Optional<AppUser> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isPresent()) {
                AppUser appUser = optionalUser.get();
                List<AppRole> roles = appUser.getRoles();

                if (roles.isEmpty()) {
                    System.out.println("Aucun rôle associé à cet utilisateur.");
                } else {
                    System.out.println("Nombre de rôles : " + roles.size());
                    for (AppRole role : roles) {
                        System.out.println("Rôle : " + role.getName());
                        List<Permission> permissions = role.getPermissionList();
                        if (permissions.isEmpty()) {
                            System.out.println("  Aucune permission pour ce rôle.");
                        } else {
                            for (Permission permission : permissions) {
                                System.out.println("  Permission : " + permission.getPath());
                            }
                        }
                    }
                }
            } else {
                System.out.println("Utilisateur introuvable dans la base de données.");
            }
        } else {
            System.out.println("Nom d'utilisateur vide ou null !");
        }

        return "redirect:/"; // redirection après login
    }


    @GetMapping("login")
    public String loginUser( ){

        return "login";
    }

}
