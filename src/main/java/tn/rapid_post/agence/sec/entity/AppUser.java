package tn.rapid_post.agence.sec.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<AppRole> roles = new ArrayList<>();

    // Constructeurs
    public AppUser() {}
    public AppUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public AppUser(String username, String password, List<AppRole> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Ajoute les rôles (format ROLE_NOM)
        authorities.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList()));

        // Ajoute les permissions (format PERM_NOM)
        authorities.addAll(roles.stream()
                .flatMap(role -> role.getPermissionList().stream())
                .map(perm -> new SimpleGrantedAuthority("PERM_" + perm.getNom()))
                .collect(Collectors.toList()));

        return authorities;
    }

    // Getters/Setters
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    public Long getId() { return id; }
    public List<AppRole> getRoles() { return roles; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRoles(List<AppRole> roles) { this.roles = roles; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}