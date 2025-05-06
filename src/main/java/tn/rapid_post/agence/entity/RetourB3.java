package tn.rapid_post.agence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RetourB3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String numB3Ro;
    private String nomPrenB3Ro;

    public String getId() {
        return "RO-"+id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumB3Ro() {
        return numB3Ro;
    }

    public void setNumB3Ro(String numB3Ro) {
        this.numB3Ro = numB3Ro;
    }

    public String getNomPrenB3Ro() {
        return nomPrenB3Ro;
    }

    public void setNomPrenB3Ro(String nomPrenB3Ro) {
        this.nomPrenB3Ro = nomPrenB3Ro;
    }

    public RetourB3() {
    }

    @Override
    public String toString() {
        return "RetourB3{" +
                "id=" + getId() +
                ", numB3Ro='" + numB3Ro + '\'' +
                ", nomPrenB3Ro='" + nomPrenB3Ro + '\'' +
                '}';
    }

    public RetourB3(String numB3Ro, String nomPrenB3Ro) {

        this.numB3Ro = numB3Ro;
        this.nomPrenB3Ro = nomPrenB3Ro;
    }
}

