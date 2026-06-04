package com.artmark.cli;

import com.artmark.model.Categorie;
import com.artmark.model.Produs;
import com.artmark.service.ProductService;

import java.util.List;

// Prezentare pentru categorii si produse. Actiunile sunt de vanzator
// (adaugarea de produs foloseste vanzatorul logat din sesiune).
public class ProductController {
    private final ProductService productService;
    private final ConsoleReader reader;
    private final Session session;

    public ProductController(ProductService productService, ConsoleReader reader, Session session) {
        this.productService = productService;
        this.reader = reader;
        this.session = session;
    }

    public void adaugaCategorie() {
        System.out.println("\n--- Adauga categorie ---");
        String nume = reader.readNonEmpty("Nume: ");
        String desc = reader.readNonEmpty("Descriere: ");
        try {
            Categorie c = productService.adaugaCategorie(nume, desc);
            System.out.println("OK: categorie creata - " + c.getNume());
        } catch (IllegalArgumentException e) {
            System.out.println("EROARE: " + e.getMessage());
        }
    }

    public void adaugaProdus() {
        System.out.println("\n--- Adauga produs ---");
        List<Categorie> categorii = productService.toateCategoriile();
        if (categorii.isEmpty()) {
            System.out.println("! Nu exista categorii. Adauga intai o categorie.");
            return;
        }
        Categorie cat = reader.alegeDinLista("Alege categoria: ", categorii, Categorie::getNume);

        String titlu = reader.readNonEmpty("Titlu: ");
        String desc  = reader.readNonEmpty("Descriere: ");

        Produs p = productService.adaugaProdus(titlu, desc, cat, session.caVanzator());
        System.out.println("OK: produs adaugat - " + p.getTitlu());
    }

    public void listeazaProduseleMele() {
        System.out.println("\n--- Produsele mele ---");
        List<Produs> produse = productService.produseVanzator(session.getUtilizator().getId());
        if (produse.isEmpty()) {
            System.out.println("(niciun produs)");
            return;
        }
        for (Produs p : produse) {
            System.out.println("  - " + p.getTitlu()
                    + " [" + p.getCategorie().getNume() + "] "
                    + (p.isDisponibil() ? "disponibil" : "in lot"));
        }
    }

    public void listeazaCategorii() {
        System.out.println("\n--- Categorii ---");
        List<Categorie> categorii = productService.toateCategoriile();
        if (categorii.isEmpty()) {
            System.out.println("(nicio categorie)");
            return;
        }
        for (Categorie c : categorii) {
            System.out.println("  - " + c.getNume() + ": " + c.getDescriere());
        }
    }
}
