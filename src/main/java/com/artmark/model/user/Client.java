package com.artmark.model.user;

import com.artmark.model.Produs;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Client extends Utilizator {
    private double bugetMaxim;
    private final Set<Produs> articoleFavorite;

    public Client(String nume, String prenume, String email, String parolaHash, LocalDateTime dataInregistrare, double bugetMaxim) {
        super(nume, prenume, email, parolaHash, dataInregistrare);
        this.bugetMaxim = bugetMaxim;
        this.articoleFavorite = new HashSet<>();
    }
    public Client(String id, String nume, String prenume, String email, String parolaHash, LocalDateTime dataInregistrare, double bugetMaxim) {
        super(id, nume, prenume, email, parolaHash, dataInregistrare);
        this.bugetMaxim = bugetMaxim;
        this.articoleFavorite = new HashSet<>();
    }
    public double getBugetMaxim() {
        return bugetMaxim;
    }

    public void setBugetMaxim(double bugetMaxim) {
        this.bugetMaxim = bugetMaxim;
    }

    public Set<Produs> getArticoleFavorite() {
        return articoleFavorite;
    }

    public void addArticolFavorit(Produs produs) {
        articoleFavorite.add(produs);
    }

    public void removeArticolFavorit(Produs produs) {
        articoleFavorite.remove(produs);
    }

    @Override
    public String getRol() {
        return "CLIENT";
    }
}
