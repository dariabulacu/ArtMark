package com.artmark.service;

import com.artmark.model.*;
import com.artmark.model.user.Client;
import com.artmark.model.user.Utilizator;
import com.artmark.model.user.Vanzator;
import com.artmark.repository.LicitatieRepository;
import com.artmark.repository.LotRepository;
import com.artmark.repository.NotificareRepository;
import com.artmark.repository.OfertaRepository;
import com.artmark.repository.ProdusRepository;
import com.artmark.repository.TranzactieRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// Cum LICITATIE nu-si incarca loturile si LOT nu-si incarca ofertele automat,
// serviciul asambleaza ce e nevoie la fiecare apel (ataseaza ofertele la lot).
public class AuctionService {
    private final LicitatieRepository licitatieRepo;
    private final LotRepository lotRepo;
    private final OfertaRepository ofertaRepo;
    private final TranzactieRepository tranzactieRepo;
    private final NotificareRepository notificareRepo;
    private final ProdusRepository produsRepo;

    public AuctionService(LicitatieRepository licitatieRepo, LotRepository lotRepo,
                          OfertaRepository ofertaRepo, TranzactieRepository tranzactieRepo,
                          NotificareRepository notificareRepo, ProdusRepository produsRepo) {
        this.licitatieRepo  = licitatieRepo;
        this.lotRepo = lotRepo;
        this.ofertaRepo = ofertaRepo;
        this.tranzactieRepo = tranzactieRepo;
        this.notificareRepo = notificareRepo;
        this.produsRepo = produsRepo;
    }

    public Licitatie creeazaLicitatie(String titlu, String descriere, LocalDateTime start, LocalDateTime end) {
        Licitatie licitatie = new Licitatie(titlu, descriere, start, end);
        Persistenta.executa(() -> licitatieRepo.save(licitatie));
        return licitatie;
    }

    public Lot adaugaLotInLicitatie(String licitatieId, Produs produs, double pretInceput, double estimMin, double estimMax) {
        Licitatie licitatie = getLicitatieSauEroare(licitatieId);

        if (!produs.isDisponibil()) {
            throw new IllegalStateException(
                    "Produsul " + produs.getTitlu() + " este deja intr-un lot activ.");
        }

        int numarLot = Persistenta.interogheaza(() -> lotRepo.findByLicitatieId(licitatieId)).size() + 1;
        Lot lot = new Lot(licitatieId, numarLot, produs, pretInceput, estimMin, estimMax);
        licitatie.adaugaLot(lot);
        Persistenta.executa(() -> lotRepo.save(lot));
        produs.setDisponibil(false);
        Persistenta.executa(() -> produsRepo.update(produs));
        return lot;
    }

    public void activeazaLicitatie(String licitatieId) {
        Licitatie licitatie = getLicitatieSauEroare(licitatieId);
        licitatie.activeaza();
        Persistenta.executa(() -> licitatieRepo.update(licitatie));
        for (Lot lot : Persistenta.interogheaza(() -> lotRepo.findByLicitatieId(licitatieId))) {
            lot.setStatus(Lot.StatusLot.ACTIV);
            Persistenta.executa(() -> lotRepo.update(lot));
        }
    }

    public Oferta plaseazaOferta(String lotId, Client client, double suma) {
        Lot lot = getLotSauEroare(lotId);   // incarca lotul + ofertele lui

        if (lot.getStatus() != Lot.StatusLot.ACTIV) {
            throw new IllegalStateException("Lotul nu este activ.");
        }
        if (lot.getVanzator().getId().equals(client.getId())) {
            throw new IllegalStateException("Nu poti licita pe propriul produs.");
        }
        if (suma <= lot.getOfertaCurenta()) {
            throw new IllegalArgumentException(
                    "Suma " + suma + " RON nu depaseste oferta curenta de "
                    + lot.getOfertaCurenta() + " RON.");
        }

        Oferta oferta = new Oferta(lotId, client, suma);
        Persistenta.executa(() -> ofertaRepo.save(oferta));

        // notifica toti biderii anteriori ca au fost depasiti si fara notificari duplicate
        Set<String> notificati = new HashSet<>();
        lot.getOferte().stream()
                .map(Oferta::getClient)
                .filter(c -> !c.getId().equals(client.getId()))
                .filter(c -> notificati.add(c.getId()))
                .forEach(c -> trimiteNotificare(c,
                        "Oferta ta pentru lotul " + lot.getNrLot()
                        + " a fost depasita. Oferta curenta: " + suma + " RON."));

        return oferta;
    }

    public Optional<Tranzactie> adjudecaLot(String lotId) {
        Lot lot = getLotSauEroare(lotId);

        if (lot.getStatus() != Lot.StatusLot.ACTIV) {
            throw new IllegalStateException("Lotul nu este activ.");
        }

        Optional<Oferta> castigatoare = lot.getOfertaCastigatoare();

        // nicio oferta — lotul e retras, produsele redevin disponibile
        if (castigatoare.isEmpty()) {
            lot.setStatus(Lot.StatusLot.RETRAS);
            Persistenta.executa(() -> lotRepo.update(lot));
            for (Produs p : lot.getProduse()) {
                p.setDisponibil(true);
                Persistenta.executa(() -> produsRepo.update(p));
            }
            return Optional.empty();
        }

        lot.setStatus(Lot.StatusLot.ADJUDECAT);
        Persistenta.executa(() -> lotRepo.update(lot));

        Oferta oferta = castigatoare.get();
        Vanzator vanzator  = lot.getVanzator();
        Tranzactie tranzactie = new Tranzactie(lot, oferta, vanzator, LocalDateTime.now());
        Persistenta.executa(() -> tranzactieRepo.save(tranzactie));

        trimiteNotificare(oferta.getClient(),
                "Felicitari! Ai castigat lotul " + lot.getNrLot()
                + " cu suma de " + oferta.getSuma() + " RON.");
        trimiteNotificare(vanzator,
                "Lotul " + lot.getNrLot() + " a fost adjudecat cu "
                + oferta.getSuma() + " RON de catre " + oferta.getClient().getNume() + ".");

        return Optional.of(tranzactie);
    }

    // finalizeaza intreaga licitatie: adjudeca toate loturile inca active
    public List<Tranzactie> finalizeazaLicitatie(String licitatieId) {
        Licitatie licitatie = getLicitatieSauEroare(licitatieId);

        List<Tranzactie> tranzactiiNoi = Persistenta.interogheaza(() -> lotRepo.findByLicitatieId(licitatieId)).stream()
                .filter(lot -> lot.getStatus() == Lot.StatusLot.ACTIV)
                .map(lot -> adjudecaLot(lot.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        licitatie.finalizeaza();
        Persistenta.executa(() -> licitatieRepo.update(licitatie));
        return tranzactiiNoi;
    }

    public List<Licitatie> getLicitatiiActive() {
        return getToateLicitatiile().stream()
                .filter(l -> l.getStatus() == Licitatie.StatusLicitatie.ACTIVA)
                .sorted(Comparator.comparing(Licitatie::getDataSfarsit))
                .collect(Collectors.toList());
    }

    public List<Licitatie> getToateLicitatiile() {
        return Persistenta.interogheaza(licitatieRepo::findAll);
    }

    // loturile unei licitatii, cu ofertele atasate (asa getOfertaCurenta functioneaza)
    public List<Lot> getLoturiPerLicitatie(String licitatieId) {
        List<Lot> loturi = Persistenta.interogheaza(() -> lotRepo.findByLicitatieId(licitatieId));
        loturi.forEach(this::incarcaOferte);
        return loturi;
    }

    public Optional<Lot> getLot(String lotId) {
        Optional<Lot> lot = Persistenta.interogheaza(() -> lotRepo.findById(lotId));
        lot.ifPresent(this::incarcaOferte);
        return lot;
    }

    public List<Licitatie> getLicitatiiVanzator(String vanzatorId) {
        return getToateLicitatiile().stream()
                .filter(l -> getLoturiPerLicitatie(l.getId()).stream()
                        .anyMatch(lot -> lot.getVanzator().getId().equals(vanzatorId)))
                .collect(Collectors.toList());
    }

    //istoric
    //ofertele unui lot, sortate descrescator dupa suma
    public List<Oferta> getIstoricOfertePerLot(String lotId) {
        return Persistenta.interogheaza(() -> ofertaRepo.findByLotId(lotId));
    }

    // toate ofertele unui client, clientul vede pe ce loturi a licitat
    public List<Oferta> getOfertelePerClient(String clientId) {
        return Persistenta.interogheaza(() -> ofertaRepo.findByClientId(clientId));
    }

    public List<Tranzactie> getRaportTranzactiiClient(String clientId) {
        return Persistenta.interogheaza(tranzactieRepo::findAll).stream()
                .filter(t -> t.getClient().getId().equals(clientId))
                .sorted(Comparator.comparing(Tranzactie::getDataFinalizarii).reversed())
                .collect(Collectors.toList());
    }

    public List<Tranzactie> getRaportTranzactiiVanzator(String vanzatorId) {
        return Persistenta.interogheaza(tranzactieRepo::findAll).stream()
                .filter(t -> t.getVanzator().getId().equals(vanzatorId))
                .sorted(Comparator.comparing(Tranzactie::getDataFinalizarii).reversed())
                .collect(Collectors.toList());
    }


    public void trimiteNotificare(Utilizator destinatar, String mesaj) {
        Notificare notificare = new Notificare(destinatar, mesaj);
        Persistenta.executa(() -> notificareRepo.save(notificare));
    }

    public List<Notificare> getNotificariUtilizator(String utilizatorId) {
        return Persistenta.interogheaza(() -> notificareRepo.findByDestinatarId(utilizatorId));
    }

    public long getNrNotificariNecitite(String utilizatorId) {
        return getNotificariUtilizator(utilizatorId).stream()
                .filter(n -> !n.isCitita())
                .count();
    }


    private Licitatie getLicitatieSauEroare(String id) {
        return Persistenta.interogheaza(() -> licitatieRepo.findById(id))
                .orElseThrow(() -> new NoSuchElementException("Licitatie negasita: " + id));
    }

    private Lot getLotSauEroare(String id) {
        Lot lot = Persistenta.interogheaza(() -> lotRepo.findById(id))
                .orElseThrow(() -> new NoSuchElementException("Lot negasit: " + id));
        incarcaOferte(lot);
        return lot;
    }

    // ataseaza ofertele din DB la TreeSet-ul lotului (colectie sortata in memorie)
    private void incarcaOferte(Lot lot) {
        List<Oferta> oferte = Persistenta.interogheaza(() -> ofertaRepo.findByLotId(lot.getId()));
        oferte.forEach(o -> lot.getOferte().add(o));
    }
}
