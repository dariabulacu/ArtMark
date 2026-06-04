package com.artmark.model;

import java.util.UUID;

public class Categorie {
    private final String id;
    private String nume;
    private String descriere;

    public Categorie(String nume, String descriere) {
        this.id = UUID.randomUUID().toString();
        this.nume = nume;
        this.descriere = descriere;
    }
    public Categorie(String id, String nume, String descriere) {
        this.id = id;
        this.nume = nume;
        this.descriere = descriere;
    }
    public String getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }
}
