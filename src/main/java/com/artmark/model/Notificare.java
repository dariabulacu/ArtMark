package com.artmark.model;

import com.artmark.model.user.Utilizator;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notificare {
    private final String id;
    private final Utilizator destinatar;
    private String mesaj;
    private final LocalDateTime data;
    private boolean citita;

    public Notificare(Utilizator destinatar, String mesaj) {
        this.id = UUID.randomUUID().toString();
        this.destinatar = destinatar;
        this.mesaj = mesaj;
        this.data = LocalDateTime.now();
        this.citita = false;
    }
    public Notificare(String id, Utilizator destinatar, String mesaj, LocalDateTime data, boolean citita) {
        this.id = id;
        this.destinatar = destinatar;
        this.mesaj = mesaj;
        this.data = data;
        this.citita = citita;
    }
    public void marcheazaCitita() {
        this.citita = true;
    }

    public String getId() {
        return id;
    }

    public Utilizator getDestinatar() {
        return destinatar;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public LocalDateTime getData() {
        return data;
    }

    public boolean isCitita() {
        return citita;
    }
}
