package com.artmark.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Licitatie {
    private final String id;
    private String titlu;
    private String descriere;
    private final LocalDateTime dataInceput;
    private final LocalDateTime dataSfarsit;
    private StatusLicitatie status;
    private final List<Lot> loturi;

    public Licitatie(String titlu, String descriere, LocalDateTime dataInceput, LocalDateTime dataSfarsit) {
        if (!dataSfarsit.isAfter(dataInceput)) {
            throw new IllegalArgumentException("Data de sfarsit trebuie sa fie dupa data de inceput.");
        }
        this.id = UUID.randomUUID().toString();
        this.titlu = titlu;
        this.descriere = descriere;
        this.dataInceput = dataInceput;
        this.dataSfarsit = dataSfarsit;
        this.status = StatusLicitatie.PROGRAMATA;
        this.loturi = new ArrayList<>();
    }
    public Licitatie(String id, String titlu, String descriere, LocalDateTime dataInceput, LocalDateTime dataSfarsit, StatusLicitatie status) {
        this.id = id;
        this.titlu = titlu;
        this.descriere = descriere;
        this.dataInceput = dataInceput;
        this.dataSfarsit = dataSfarsit;
        this.status = status;
        this.loturi = new ArrayList<>();
    }


    public void adaugaLot(Lot lot) {
        if (status == StatusLicitatie.FINALIZATA || status == StatusLicitatie.ANULATA) {
            throw new IllegalStateException("Nu se pot adauga loturi la o licitatie finalizata sau anulata.");
        }
        loturi.add(lot);
    }

    public void activeaza() {
        if (status != StatusLicitatie.PROGRAMATA) {
            throw new IllegalStateException("Doar o licitatie programata poate fi activata.");
        }
        // vanzatorul decide explicit cand deschide licitatia — nu legam activarea de ceas
        this.status = StatusLicitatie.ACTIVA;
    }

    public void finalizeaza() {
        if (status != StatusLicitatie.ACTIVA) {
            throw new IllegalStateException("Doar o licitatie activa poate fi finalizata.");
        }
        this.status = StatusLicitatie.FINALIZATA;
    }

    public void anuleaza() {
        if (status == StatusLicitatie.FINALIZATA) {
            throw new IllegalStateException("O licitatie finalizata nu poate fi anulata.");
        }
        this.status = StatusLicitatie.ANULATA;
    }

    public String getId() {
        return id;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public LocalDateTime getDataInceput() {
        return dataInceput;
    }

    public LocalDateTime getDataSfarsit() {
        return dataSfarsit;
    }

    public StatusLicitatie getStatus() {
        return status;
    }

    public List<Lot> getLoturi() {
        return loturi;
    }

    public enum StatusLicitatie { ACTIVA, FINALIZATA, PROGRAMATA, ANULATA }
}
