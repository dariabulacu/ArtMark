package com.artmark.cli;

import com.artmark.model.user.Client;
import com.artmark.model.user.Utilizator;
import com.artmark.model.user.Vanzator;

//tine minte cine este logat pentru meniu specific si act permise
public class Session {
    private Utilizator utilizatorCurent;

    public boolean esteLogat() {
        return utilizatorCurent != null;
    }
    public Utilizator getUtilizator() {
        return utilizatorCurent;
    }

    public void login(Utilizator u) {
        this.utilizatorCurent = u;
    }
    public void logout(){
        this.utilizatorCurent = null;
    }
    public boolean esteClient() {
        return utilizatorCurent instanceof Client;
    }
    public boolean esteVanzator() {
        return utilizatorCurent instanceof Vanzator;
    }

    // cast-uri sigure: apelate doar din meniurile rolului corespunzator
    public Client caClient() { return (Client) utilizatorCurent; }
    public Vanzator caVanzator() { return (Vanzator) utilizatorCurent; }
}
