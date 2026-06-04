package com.artmark.service;

import java.sql.SQLException;

//permite aruncarea de exceptii pt cod mai curat in servicii pt a nu declara de fiecare data throws SQLException
//fiind exceptie checked exista obligativitate de try/catch
//clasa utilitara n am nevoie de vreo instanta
public final class Persistenta {

    // pentru scrieri save update delete care nu intorc nimic
    @FunctionalInterface
    public interface Operatie {
        void executa() throws SQLException;
    }

    // pentru interogari care intorc un rezultat
    @FunctionalInterface
    public interface Interogare<T> {
        T executa() throws SQLException;
    }

    private Persistenta() {}

    public static void executa(Operatie operatie) {
        try {
            operatie.executa();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la accesul in baza de date: " + e.getMessage(), e);
        }
    }

    public static <T> T interogheaza(Interogare<T> interogare) {
        try {
            return interogare.executa();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la accesul in baza de date: " + e.getMessage(), e);
        }
    }
}
