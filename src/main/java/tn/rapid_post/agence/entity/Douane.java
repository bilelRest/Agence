package tn.rapid_post.agence.entity;

import jakarta.persistence.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import tn.rapid_post.agence.sec.entity.AppUser;

import java.time.LocalDate;

@Entity
public class Douane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDouane;
    private String nom;
    private String numColis;
    private LocalDate dateSortie;
    private LocalDate dateArrivee;
    private int nbColis;
    private double droitDouane;
    private double fraisDedouane;
    private double fraisReemballage;
    private double fraisMagasin;
    private double totPayer;
    private double poid;
    private String observation;
    private boolean printed;
    private boolean delivered;
    private String origin;
    private String bloc;
    private String sequence;
    @OneToOne(fetch = FetchType.LAZY)
    private AppUser appUser;

    private boolean validated;

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Douane(String nom, String numColis, LocalDate dateSortie, LocalDate dateArrivee, int nbColis, double droitDouane, double fraisDedouane, double fraisReemballage, double fraisMagasin, double totPayer, double poid, String observation, boolean printed, boolean delivered, String origin, String bloc, AppUser appUser, boolean validated,String sequence) {
        this.nom = nom;
        this.numColis = numColis;
        this.dateSortie = dateSortie;
        this.dateArrivee = dateArrivee;
        this.nbColis = nbColis;
        this.droitDouane = droitDouane;
        this.fraisDedouane = fraisDedouane;
        this.fraisReemballage = fraisReemballage;
        this.fraisMagasin = fraisMagasin;
        this.totPayer = totPayer;
        this.poid = poid;
        this.observation = observation;
        this.printed = printed;
        this.delivered = delivered;
        this.origin = origin;
        this.bloc = bloc;
        this.appUser = appUser;
        this.validated = validated;
        this.sequence=sequence;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean vallidated) {
        this.validated = vallidated;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getBloc() {
        return bloc;
    }

    public void setBloc(String bloc) {
        this.bloc = bloc;
    }

    public Douane(boolean printed) {
        this.printed = printed;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public Douane(long idDouane, String nom, String numColis, LocalDate dateSortie, LocalDate dateArrivee, int nbColis, double droitDouane, double fraisDedouane, double fraisReemballage, double fraisMagasin, double totPayer, double poid, String observation, boolean printed, boolean delivered, String origin, String bloc,boolean validated) {
        this.idDouane = idDouane;
        this.nom = nom;
        this.numColis = numColis;
        this.dateSortie = dateSortie;
        this.dateArrivee = dateArrivee;
        this.nbColis = nbColis;
        this.droitDouane = droitDouane;
        this.fraisDedouane = fraisDedouane;
        this.fraisReemballage = fraisReemballage;
        this.fraisMagasin = fraisMagasin;
        this.totPayer = totPayer;
        this.poid = poid;
        this.observation = observation;
        this.printed = printed;
        this.delivered = delivered;
        this.origin = origin;
        this.bloc = bloc;
        this.validated=validated;
    }

    public long getIdDouane() {
        return idDouane;
    }

    public void setIdDouane(long idDouane) {
        this.idDouane = idDouane;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumColis() {
        return numColis;
    }

    public void setNumColis(String numColis) {
        this.numColis = numColis;
    }

    public LocalDate getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDate dateSortie) {
        this.dateSortie = dateSortie;
    }

    public LocalDate getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(LocalDate dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public int getNbColis() {
        return nbColis;
    }

    public void setNbColis(int nbColis) {
        this.nbColis = nbColis;
    }

    public double getDroitDouane() {
        return droitDouane;
    }

    public void setDroitDouane(double droitDouane) {
        this.droitDouane = droitDouane;
    }

    public double getFraisDedouane() {
        return fraisDedouane;
    }

    public void setFraisDedouane(double fraisDedouane) {
        this.fraisDedouane = fraisDedouane;
    }

    public double getFraisMagasin() {
        return fraisMagasin;
    }

    public void setFraisMagasin(double fraisMagasin) {
        this.fraisMagasin = fraisMagasin;
    }

    public double getFraisReemballage() {
        return fraisReemballage;
    }

    public void setFraisReemballage(double fraisReemballage) {
        this.fraisReemballage = fraisReemballage;
    }

    public double getTotPayer() {
        return totPayer;
    }

    public void setTotPayer(double totPayer) {
        this.totPayer = totPayer;
    }

    public double getPoid() {
        return poid;
    }

    public void setPoid(double poid) {
        this.poid = poid;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Douane() {
    }
}
