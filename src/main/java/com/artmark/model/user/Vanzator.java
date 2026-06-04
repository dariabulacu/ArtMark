package com.artmark.model.user;

import java.time.LocalDateTime;

public class Vanzator extends Utilizator {
    private String companie;

    public Vanzator(String nume, String prenume, String email, String parolaHash, LocalDateTime dataInregistrare, String companie) {
        super(nume, prenume, email, parolaHash, dataInregistrare);
        this.companie = companie;
    }
    public Vanzator(String id, String nume, String prenume, String email, String parolaHash, LocalDateTime dataInregistrare, String companie) {
        super(id, nume, prenume, email, parolaHash, dataInregistrare);
        this.companie = companie;
    }
    public String getCompanie() {
        return companie;
    }

    public void setCompanie(String companie) {
        this.companie = companie;
    }

    @Override
    public String getRol() {
        return "VANZATOR";
    }
}
