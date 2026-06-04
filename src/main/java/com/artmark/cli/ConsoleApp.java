package com.artmark.cli;

import com.artmark.repository.CategorieRepository;
import com.artmark.repository.LicitatieRepository;
import com.artmark.repository.LotRepository;
import com.artmark.repository.NotificareRepository;
import com.artmark.repository.OfertaRepository;
import com.artmark.repository.ProdusRepository;
import com.artmark.repository.TranzactieRepository;
import com.artmark.repository.UtilizatorRepository;
import com.artmark.service.AuctionService;
import com.artmark.service.ProductService;
import com.artmark.service.UserService;

// Punctul de intrare al CLI-ului. Asambleaza straturile (repository + servicii + controllere),
// ruleaza bucla de meniu si delega fiecare optiune catre controllerul potrivit.
// Meniul afisat depinde de starea sesiunii (nelogat / client / vanzator).
public class ConsoleApp {
    private final ConsoleReader reader = new ConsoleReader();
    private final Session session = new Session();

    // repository-urile pentru persistenta in DB
    private final CategorieRepository  categorieRepo  = new CategorieRepository();
    private final UtilizatorRepository utilizatorRepo = new UtilizatorRepository();
    private final ProdusRepository produsRepo = new ProdusRepository();
    private final LicitatieRepository  licitatieRepo = new LicitatieRepository();
    private final LotRepository lotRepo  = new LotRepository();
    private final OfertaRepository ofertaRepo = new OfertaRepository();
    private final TranzactieRepository tranzactieRepo = new TranzactieRepository();
    private final NotificareRepository notificareRepo = new NotificareRepository();

    // serviciile primesc repo-urile => fiecare actiune  in DB
    private final UserService userService = new UserService(utilizatorRepo);
    private final ProductService productService = new ProductService(categorieRepo, produsRepo);
    private final AuctionService auctionService = new AuctionService(licitatieRepo, lotRepo, ofertaRepo, tranzactieRepo, notificareRepo, produsRepo);

    // controllerele primesc serviciile prin constructor
    private final AuthController auth = new AuthController(userService, reader, session);
    private final ProductController produse = new ProductController(productService, reader, session);
    private final AuctionController licitatii = new AuctionController(auctionService, productService, reader, session);

    public static void main(String[] args) {
        new ConsoleApp().run();
    }

    public void run() {
        System.out.println("=== ArtMark - sistem de licitatii ===");
        boolean ruleaza = true;
        while (ruleaza) {
            try {
                ruleaza = session.esteLogat() ? meniuAutentificat() : meniuPublic();
            } catch (Exception e) {
                System.out.println("EROARE neasteptata: " + e.getMessage());
            }
        }
        reader.close();
        System.out.println("La revedere!");
    }

   //meniu pentru logare

    private boolean meniuPublic() {
        System.out.println("\n=== Meniu ===");
        System.out.println("1. Inregistrare");
        System.out.println("2. Autentificare");
        System.out.println("0. Iesire");
        switch (reader.readIntInRange("Optiune: ", 0, 2)) {
            case 1 -> auth.inregistrare();
            case 2 -> auth.login();
            case 0 -> { return false; }
        }
        return true;
    }

    private boolean meniuAutentificat() {
        return session.esteVanzator() ? meniuVanzator() : meniuClient();
    }

    private boolean meniuVanzator() {
        System.out.println("\n=== Meniu vanzator (" + session.getUtilizator().getNume() + " " + session.getUtilizator().getPrenume() + ") ===");
        System.out.println("1. Adauga categorie");
        System.out.println("2. Adauga produs");
        System.out.println("3. Produsele mele");
        System.out.println("4. Creeaza licitatie");
        System.out.println("5. Adauga lot in licitatie");
        System.out.println("6. Activeaza licitatie");
        System.out.println("7. Licitatiile mele");
        System.out.println("8. Finalizeaza licitatie");
        System.out.println("9. Notificarile mele");
        System.out.println("10. Tranzactiile mele");
        System.out.println("11. Schimba parola");
        System.out.println("12. Logout");
        System.out.println("0. Iesire");
        switch (reader.readIntInRange("Optiune: ", 0, 12)) {
            case 1 -> produse.adaugaCategorie();
            case 2 -> produse.adaugaProdus();
            case 3 -> produse.listeazaProduseleMele();
            case 4 -> licitatii.creeazaLicitatie();
            case 5 -> licitatii.adaugaLot();
            case 6 -> licitatii.activeazaLicitatie();
            case 7 -> licitatii.licitatiileMele();
            case 8 -> licitatii.finalizeazaLicitatie();
            case 9 -> licitatii.notificarileMele();
            case 10 -> licitatii.tranzactiileMele();
            case 11 -> auth.schimbaParola();
            case 12 -> auth.logout();
            case 0 -> { return false; }
        }
        return true;
    }

    private boolean meniuClient() {
        System.out.println("\n=== Meniu client (" + session.getUtilizator().getNume() + session.getUtilizator().getPrenume() + ") ===");
        System.out.println("1. Vezi licitatii active");
        System.out.println("2. Plaseaza oferta");
        System.out.println("3. Ofertele mele");
        System.out.println("4. Notificarile mele");
        System.out.println("5. Tranzactiile mele");
        System.out.println("6. Schimba parola");
        System.out.println("7. Logout");
        System.out.println("0. Iesire");
        switch (reader.readIntInRange("Optiune: ", 0, 7)) {
            case 1 -> licitatii.licitatiiActive();
            case 2 -> licitatii.plaseazaOferta();
            case 3 -> licitatii.ofertelMele();
            case 4 -> licitatii.notificarileMele();
            case 5 -> licitatii.tranzactiileMele();
            case 6 -> auth.schimbaParola();
            case 7 -> auth.logout();
            case 0 -> { return false; }
        }
        return true;
    }
}
