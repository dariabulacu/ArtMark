package com.artmark.model;

import com.artmark.model.user.Vanzator;

import java.util.*;

public class Lot {
    private final String id;
    private final String licitatieId;
    private final int nrLot;
    private final List<Produs> produse;
    private double pretInceput;
    private double estimareMin;
    private double estimareMax;
    private StatusLot status;
    private final TreeSet<Oferta> oferte;

    public Lot(String licitatieId, int nrLot, Produs primulProdus, double pretInceput, double estimareMin, double estimareMax) {
        this.id = UUID.randomUUID().toString();
        this.licitatieId = licitatieId;
        this.nrLot = nrLot;
        this.produse = new ArrayList<>();
        this.produse.add(primulProdus);
        this.pretInceput = pretInceput;
        this.estimareMin = estimareMin;
        this.estimareMax = estimareMax;
        this.status = StatusLot.NESCOS;
        this.oferte = new TreeSet<>();
    }
    public Lot(String id, String licitatieId, int nrLot, double pretInceput, double estimareMin, double estimareMax, StatusLot status) {
        this.id = id;
        this.licitatieId = licitatieId;
        this.nrLot = nrLot;
        this.produse = new ArrayList<>();
        this.pretInceput = pretInceput;
        this.estimareMin = estimareMin;
        this.estimareMax = estimareMax;
        this.status = status;
        this.oferte = new TreeSet<>();
    }

    public void addProdus(Produs produs) {
        produse.add(produs);
    }

    public Vanzator getVanzator() {
        return produse.getFirst().getVanzator();
    }

    public double getOfertaCurenta() {
        if (oferte.isEmpty())
            return pretInceput;
        return oferte.first().getSuma();
    }

    public Optional<Oferta> getOfertaCastigatoare() {
        if(oferte.isEmpty())
            return Optional.empty();
        return Optional.of(oferte.first());
    }

    public String getId() {
        return id;
    }

    public String getLicitatieId() {
        return licitatieId;
    }

    public int getNrLot() {
        return nrLot;
    }

    public List<Produs> getProduse() {
        return produse;
    }

    public double getPretInceput() {
        return pretInceput;
    }

    public void setPretInceput(double pretInceput) {
        this.pretInceput = pretInceput;
    }

    public double getEstimareMin() {
        return estimareMin;
    }

    public void setEstimareMin(double estimareMin) {
        this.estimareMin = estimareMin;
    }

    public double getEstimareMax() {
        return estimareMax;
    }

    public void setEstimareMax(double estimareMax) {
        this.estimareMax = estimareMax;
    }

    public StatusLot getStatus() {
        return status;
    }

    public void setStatus(StatusLot status) {
        this.status = status;
    }

    public TreeSet<Oferta> getOferte() {
        return oferte;
    }

    public enum StatusLot { NESCOS, ACTIV, ADJUDECAT, RETRAS }
}
