package com.artmark.model;

import com.artmark.model.user.Vanzator;

import java.time.LocalDateTime;
import java.util.UUID;

public class Produs {
    private final String id;
    private String titlu;
    private String descriere;
    private Categorie categorie;
    private final Vanzator vanzator;
    private final LocalDateTime dataAdaugarii;
    private boolean disponibil;

    public Produs(String titlu, String descriere, Categorie categorie, Vanzator vanzator, LocalDateTime dataAdaugarii) {
        this.id = UUID.randomUUID().toString();
        this.titlu = titlu;
        this.descriere = descriere;
        this.categorie = categorie;
        this.vanzator = vanzator;
        this.dataAdaugarii = dataAdaugarii;
        this.disponibil = true;
    }
    public Produs(String id, String titlu, String descriere, Categorie categorie, Vanzator vanzator, LocalDateTime dataAdaugarii, boolean disponibil) {
        this.id = id;
        this.titlu = titlu;
        this.descriere = descriere;
        this.categorie = categorie;
        this.vanzator = vanzator;
        this.dataAdaugarii = dataAdaugarii;
        this.disponibil = disponibil;
    }

    public String getId() {
        return id;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Vanzator getVanzator() {
        return vanzator;
    }

    public LocalDateTime getDataAdaugarii() {
        return dataAdaugarii;
    }

    public boolean isDisponibil() {
        return disponibil;
    }

    public void setDisponibil(boolean disponibil) {
        this.disponibil = disponibil;
    }
}
