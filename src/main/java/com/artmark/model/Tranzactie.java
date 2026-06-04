package com.artmark.model;

import com.artmark.model.user.Client;
import com.artmark.model.user.Vanzator;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tranzactie {
    private final String id;
    private final Lot lot;
    private final Oferta ofertaCastigatoare;
    private final Vanzator vanzator;
    private LocalDateTime dataFinalizarii;
    private StatusTranzactie status;

    public Tranzactie(Lot lot, Oferta ofertaCastigatoare, Vanzator vanzator, LocalDateTime dataFinalizarii) {
        this.id = UUID.randomUUID().toString();
        this.lot = lot;
        this.ofertaCastigatoare = ofertaCastigatoare;
        this.vanzator = vanzator;
        this.dataFinalizarii = dataFinalizarii;
        this.status = StatusTranzactie.IN_ASTEPTARE;
    }
    public Tranzactie(String id, Lot lot, Oferta ofertaCastigatoare, Vanzator vanzator, LocalDateTime dataFinalizarii, StatusTranzactie status) {
        this.id = id;
        this.lot = lot;
        this.ofertaCastigatoare = ofertaCastigatoare;
        this.vanzator = vanzator;
        this.dataFinalizarii = dataFinalizarii;
        this.status = status;
    }
    public String getId() {
        return id;
    }

    public Lot getLot() {
        return lot;
    }

    public Oferta getOfertaCastigatoare() {
        return ofertaCastigatoare;
    }

    public Client getClient() {
        return ofertaCastigatoare.getClient();
    }

    public Vanzator getVanzator() {
        return vanzator;
    }

    public double getSumaPlatita() {
        return ofertaCastigatoare.getSuma();
    }

    public LocalDateTime getDataFinalizarii() {
        return dataFinalizarii;
    }

    public void setDataFinalizarii(LocalDateTime dataFinalizarii) {
        this.dataFinalizarii = dataFinalizarii;
    }

    public StatusTranzactie getStatus() {
        return status;
    }

    public void setStatus(StatusTranzactie status) {
        this.status = status;
    }

    public enum StatusTranzactie { FINALIZATA, ANULATA, IN_ASTEPTARE }
}
