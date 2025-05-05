package tn.rapid_post.agence.sec.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nom;
    private String prenom;
    private long matricule;
    private boolean actif;
    private String login;
    private String password;
    @OneToMany(fetch = FetchType.LAZY)
    List<AppRole> appRoleList=new ArrayList<>();
}
