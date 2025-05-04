package tn.rapid_post.agence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class B3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idB3;
    private String numB3;
    private String destination;
    private boolean notified;
    private int numTel;
    private final LocalDateTime dateNotif= LocalDateTime.now();

    public B3() {
    }

    public B3(String numB3, String destination, boolean notified, int numTel) {
        this.numB3 = numB3;
        this.destination = destination;
        this.notified = notified;
        this.idB3 = idB3;
        this.numTel = numTel;
    }

    public int getNumTel() {
        return numTel;
    }

    public void setNumTel(int numTel) {
        this.numTel = numTel;
    }

    public String getNumB3() {
        return numB3;
    }

    public void setNumB3(String numB3) {
        this.numB3 = numB3;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public LocalDateTime getDateNotif() {
        return dateNotif;
    }

    public long getIdB3() {
        return idB3;
    }

    public void setIdB3(long idB3) {
        this.idB3 = idB3;
    }
}
