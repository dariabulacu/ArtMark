package com.artmark.cli;

import com.artmark.model.user.Client;
import com.artmark.model.user.Utilizator;
import com.artmark.model.user.Vanzator;
import com.artmark.service.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;

// Stratul de prezentare pentru autentificare. Nu contine logica:
// citeste input, cheama UserService si traduce rezultatul/exceptia pe ecran.
public class AuthController {
    private final UserService userService;
    private final ConsoleReader reader;
    private final Session session;

    public AuthController(UserService userService, ConsoleReader reader, Session session) {
        this.userService = userService;
        this.reader = reader;
        this.session = session;
    }

    public void inregistrare() {
        System.out.println("\n--- Inregistrare ---");
        System.out.println("1. Client");
        System.out.println("2. Vanzator");
        int tip = reader.readIntInRange("Tip cont: ", 1, 2);

        String nume    = reader.readNonEmpty("Nume: ");
        String prenume = reader.readNonEmpty("Prenume: ");
        String email   = reader.readNonEmpty("Email: ");
        String parola  = reader.readNonEmpty("Parola: ");

        try {
            if (tip == 1) {
                double buget = reader.readDouble("Buget maxim (RON): ");
                Client client = userService.inregistreazaClient(nume, prenume, email, parola, buget);
                System.out.println("OK: cont client creat. Bine ai venit, " + client.getNume() + "!");
            } else {
                String companie = reader.readNonEmpty("Companie: ");
                Vanzator vanzator = userService.inregistreazaVanzator(nume, prenume, email, parola, companie);
                System.out.println("OK: cont vanzator creat. Bine ai venit, " + vanzator.getNume() + "!");
            }
        } catch (IllegalArgumentException e) {
            // ex. "Email-ul este deja inregistrat: ..."
            System.out.println("EROARE: " + e.getMessage());
        }
    }

    public void login() {
        System.out.println("\n--- Autentificare ---");
        String email  = reader.readNonEmpty("Email: ");
        String parola = reader.readNonEmpty("Parola: ");

        // autentificare intoarce Optional (nu exceptie): gol = email sau parola gresita
        Optional<Utilizator> rezultat = userService.autentificare(email, parola);
        if (rezultat.isPresent()) {
            Utilizator u = rezultat.get();
            session.login(u);
            System.out.println("OK: autentificat ca " + u.getNume() + " " + u.getPrenume() + " (" + u.getRol() + ").");
        } else {
            System.out.println("EROARE: email sau parola gresita.");
        }
    }

    public void schimbaParola() {
        System.out.println("\n--- Schimbare parola ---");
        String veche = reader.readNonEmpty("Parola veche: ");
        String noua  = reader.readNonEmpty("Parola noua: ");
        try {
            userService.schimbaParola(session.getUtilizator().getId(), veche, noua);
            System.out.println("OK: parola a fost schimbata.");
        } catch (IllegalArgumentException | NoSuchElementException e) {
            // ex. "Parola veche este incorecta."
            System.out.println("EROARE: " + e.getMessage());
        }
    }

    public void logout() {
        session.logout();
        System.out.println("Ai fost delogat.");
    }
}
