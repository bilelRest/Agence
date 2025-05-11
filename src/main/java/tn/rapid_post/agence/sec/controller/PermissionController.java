package tn.rapid_post.agence.sec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.entity.Permission;
import tn.rapid_post.agence.sec.repo.PermissionRepository;
import tn.rapid_post.agence.sec.repo.RoleRepository;

import java.util.*;

@Controller
public class PermissionController {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("permission")
    public String permission(Model model,
                             @RequestParam(value = "nom", required = false) String nom,
                             @RequestParam(value = "path", required = false) String path) {
        boolean success = false;
        boolean exist = false;

        if (StringUtils.hasText(nom) && StringUtils.hasText(path)) {
            Permission permission = permissionRepository.findByPath(path);
            if (permission != null) {
                exist = true;
            } else {
                permissionRepository.save(new Permission(nom, path));
                success = true;
            }
        }

        model.addAttribute("success", success);
        model.addAttribute("exist", exist);
        return "permission";
    }
    @GetMapping("/roles")
    public String chooseRole(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return "role-select";
    }

    @GetMapping("/role")
    public String showRoleManagement(@RequestParam(value = "roleName", required = false) String roleName,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(roleName)) {
            return "redirect:/roles"; // redirection vers la page de sélection
        }

        Optional<AppRole> optionalRole = roleRepository.findByName(roleName);
        if (optionalRole.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rôle introuvable : " + roleName);
            return "redirect:/roles";
        }

        AppRole role = optionalRole.get();
        model.addAttribute("role", role);
        model.addAttribute("permissions", permissionRepository.findAll());
        return "role";
    }


    @PostMapping("/roleadd")
    public String updateRolePermissions(
            @RequestParam("roleName") String roleName,
            @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds,
            RedirectAttributes redirectAttributes) {

        try {
            AppRole role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé"));

            Set<Permission> permissions = (permissionIds == null || permissionIds.isEmpty())
                    ? new HashSet<>()
                    : new HashSet<>(permissionRepository.findAllById(permissionIds));

            role.setPermissionList(new ArrayList<>(permissions));
            roleRepository.save(role);

            redirectAttributes.addFlashAttribute("success", "Permissions mises à jour avec succès");
            return "redirect:/role?roleName=" + roleName;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/role?roleName=" + roleName;
        }
    }
}
