package com.artmark.cli;

import com.artmark.model.Licitatie;
import com.artmark.model.Lot;
import com.artmark.model.Notificare;
import com.artmark.model.Oferta;
import com.artmark.model.Produs;
import com.artmark.model.Tranzactie;
import com.artmark.service.AuctionService;
import com.artmark.service.ProductService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// Prezentare pentru licitatii, loturi si oferte.
// Actiuni de vanzator: creare licitatie, adaugare lot, activare, finalizare.
// Actiuni de client: vizualizare licitatii active, plasare oferta, ofertele mele.
public class AuctionController {
    private final AuctionService auctionService;
    private final ProductService productService;
    private final ConsoleReader reader;
    private final Session session;

    public AuctionController(AuctionService auctionService, ProductService productService,
                             ConsoleReader reader, Session session) {
        this.auctionService = auctionService;
        this.productService = productService;
        this.reader = reader;
        this.session = session;
    }

    public void creeazaLicitatie() {
        System.out.println("\n--- Creeaza licitatie ---");
        String titlu = reader.readNonEmpty("Titlu: ");
        String desc  = reader.readNonEmpty("Descriere: ");
        int durataZile = reader.readIntInRange("Durata in zile: ", 1, 365);
        LocalDateTime start = LocalDateTime.now();        // incepe ACUM -> se poate activa imediat
        LocalDateTime end   = start.plusDays(durataZile);
        try {
            Licitatie l = auctionService.creeazaLicitatie(titlu, desc, start, end);
            System.out.println("OK: licitatie creata - " + l.getTitlu() + " (status " + l.getStatus()
                    + "). O poti activa imediat (optiunea 6).");
        } catch (IllegalArgumentException e) {
            System.out.println("EROARE: " + e.getMessage());
        }
    }

    public void adaugaLot() {
        System.out.println("\n--- Adauga lot in licitatie ---");
        List<Licitatie> licitatii = auctionService.getToateLicitatiile();
        if (licitatii.isEmpty()) {
            System.out.println("! Nu exista licitatii. Creeaza una intai.");
            return;
        }
        Licitatie licitatie = reader.alegeDinLista("Alege licitatia: ", licitatii,
                l -> l.getTitlu() + " (" + l.getStatus() + ")");

        // doar produsele mele inca disponibile
        List<Produs> disponibile = productService.produseVanzator(session.getUtilizator().getId())
                .stream().filter(Produs::isDisponibil).collect(Collectors.toList());
        if (disponibile.isEmpty()) {
            System.out.println("! Nu ai produse disponibile. Adauga un produs intai.");
            return;
        }
        Produs produs = reader.alegeDinLista("Alege produsul: ", disponibile, Produs::getTitlu);

        double pret = reader.readDouble("Pret de inceput (RON): ");
        double eMin = reader.readDouble("Estimare minima (RON): ");
        double eMax = reader.readDouble("Estimare maxima (RON): ");
        try {
            Lot lot = auctionService.adaugaLotInLicitatie(licitatie.getId(), produs, pret, eMin, eMax);
            System.out.println("OK: lot " + lot.getNrLot() + " adaugat in \"" + licitatie.getTitlu() + "\".");
        } catch (IllegalStateException | IllegalArgumentException | NoSuchElementException e) {
            System.out.println("EROARE: " + e.getMessage());
        }
    }

    public void activeazaLicitatie() {
        System.out.println("\n--- Activeaza licitatie ---");
        List<Licitatie> programate = auctionService.getToateLicitatiile().stream()
                .filter(l -> l.getStatus() == Licitatie.StatusLicitatie.PROGRAMATA)
                .collect(Collectors.toList());
        if (programate.isEmpty()) {
            System.out.println("! Nicio licitatie programata.");
            return;
        }
        Licitatie l = reader.alegeDinLista("Alege licitatia: ", programate, Licitatie::getTitlu);
        try {
            auctionService.activeazaLicitatie(l.getId());
            System.out.println("OK: licitatie activata.");
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println("EROARE: " + e.getMessage());
        }
    }

    public void finalizeazaLicitatie() {
        System.out.println("\n--- Finalizeaza licitatie ---");
        List<Licitatie> active = auctionService.getLicitatiiActive();
        if (active.isEmpty()) {
            System.out.println("! Nicio licitatie activa.");
            return;
        }
        Licitatie l = reader.alegeDinLista("Alege licitatia: ", active, Licitatie::getTitlu);
        try {
            List<Tranzactie> tranzactii = auctionService.finalizeazaLicitatie(l.getId());
            System.out.println("OK: licitatie finalizata. Tranzactii generate: " + tranzactii.size());
            for (Tranzactie t : tranzactii) {
                System.out.println("  - Lot " + t.getLot().getNrLot() + " adjudecat cu "
                        + t.getSumaPlatita() + " RON de " + t.getClient().getNume());
            }
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println("EROARE: " + e.getMessage());
        }
    }

    // licitatiile in care vanzatorul logat are cel putin un lot
    public void licitatiileMele() {
        System.out.println("\n--- Licitatiile mele ---");
        List<Licitatie> ale = auctionService.getLicitatiiVanzator(session.getUtilizator().getId());
        if (ale.isEmpty()) {
            System.out.println("(nu ai loturi in nicio licitatie)");
            return;
        }
        for (Licitatie l : ale) {
            System.out.println("  * " + l.getTitlu() + " [" + l.getStatus()
                    + "] - se termina " + l.getDataSfarsit());
            for (Lot lot : auctionService.getLoturiPerLicitatie(l.getId())) {
                System.out.println("      Lot " + lot.getNrLot() + " [" + lot.getStatus()
                        + "] " + descriereLot(lot) + " | oferta curenta: " + lot.getOfertaCurenta() + " RON");
            }
        }
    }

    //pentru client
    public void licitatiiActive() {
        System.out.println("\n--- Licitatii active ---");
        List<Licitatie> active = auctionService.getLicitatiiActive();
        if (active.isEmpty()) {
            System.out.println("(niciuna)");
            return;
        }
        for (Licitatie l : active) {
            System.out.println("  * " + l.getTitlu() + " - se termina " + l.getDataSfarsit());
            for (Lot lot : auctionService.getLoturiPerLicitatie(l.getId())) {
                System.out.println("      Lot " + lot.getNrLot() + " [" + lot.getStatus()
                        + "] " + descriereLot(lot) + " | oferta curenta: " + lot.getOfertaCurenta() + " RON");
            }
        }
    }

    public void plaseazaOferta() {
        System.out.println("\n--- Plaseaza oferta ---");
        List<Licitatie> active = auctionService.getLicitatiiActive();
        if (active.isEmpty()) {
            System.out.println("! Nicio licitatie activa.");
            return;
        }
        Licitatie licitatie = reader.alegeDinLista("Alege licitatia: ", active, Licitatie::getTitlu);

        List<Lot> loturi = auctionService.getLoturiPerLicitatie(licitatie.getId());
        if (loturi.isEmpty()) {
            System.out.println("! Licitatia nu are loturi.");
            return;
        }
        Lot lot = reader.alegeDinLista("Alege lotul: ", loturi,
                l -> "Lot " + l.getNrLot() + " - " + descriereLot(l)
                        + " | oferta curenta: " + l.getOfertaCurenta() + " RON");

        double suma = reader.readDouble("Suma oferta (RON): ");
        try {
            Oferta o = auctionService.plaseazaOferta(lot.getId(), session.caClient(), suma);
            System.out.println("OK: oferta plasata - " + o.getSuma() + " RON pe lotul " + lot.getNrLot() + ".");
        } catch (IllegalStateException | IllegalArgumentException | NoSuchElementException e) {
            // ex. "Nu poti licita pe propriul produs.", "Suma ... nu depaseste oferta curenta ..."
            System.out.println("EROARE: " + e.getMessage());
        }
    }

    public void ofertelMele() {
        System.out.println("\n--- Ofertele mele ---");
        List<Oferta> oferte = auctionService.getOfertelePerClient(session.getUtilizator().getId());
        if (oferte.isEmpty()) {
            System.out.println("(nicio oferta plasata)");
            return;
        }
        for (Oferta o : oferte) {
            // oferta isi poarta lotId-ul; il traducem in nr de lot pentru afisare
            String nrLot = auctionService.getLot(o.getLotId())
                    .map(l -> "Lot " + l.getNrLot())
                    .orElse("Lot " + o.getLotId());
            System.out.println("  - " + nrLot + ": " + o.getSuma() + " RON la " + o.getDataOra());
        }
    }

    // notificarile utilizatorului logat (merge si pentru client, si pentru vanzator)
    public void notificarileMele() {
        System.out.println("\n--- Notificarile mele ---");
        List<Notificare> notificari = auctionService.getNotificariUtilizator(session.getUtilizator().getId());
        if (notificari.isEmpty()) {
            System.out.println("(nicio notificare)");
            return;
        }
        for (Notificare n : notificari) {
            System.out.println("  " + (n.isCitita() ? "[citit]" : "[NOU]  ") + " " + n.getMesaj()
                    + "  (" + n.getData() + ")");
        }
    }

    // tranzactiile utilizatorului logat — raportul difera dupa rol
    public void tranzactiileMele() {
        System.out.println("\n--- Tranzactiile mele ---");
        String id = session.getUtilizator().getId();
        List<Tranzactie> tranzactii = session.esteVanzator()
                ? auctionService.getRaportTranzactiiVanzator(id)
                : auctionService.getRaportTranzactiiClient(id);
        if (tranzactii.isEmpty()) {
            System.out.println("(nicio tranzactie)");
            return;
        }
        for (Tranzactie t : tranzactii) {
            System.out.println("  - Lot " + t.getLot().getNrLot() + " | " + t.getSumaPlatita() + " RON"
                    + " | cumparator: " + t.getClient().getNume() + " " + t.getClient().getPrenume()
                    + " | vanzator: " + t.getVanzator().getNume() + " " + t.getVanzator().getPrenume()
                    + " | " + t.getDataFinalizarii());
        }
    }

    private String descriereLot(Lot lot) {
        return lot.getProduse().isEmpty() ? "(gol)" : lot.getProduse().get(0).getTitlu();
    }
}
