package com.artmark.model;

import com.artmark.model.user.Client;

import java.time.LocalDateTime;
import java.util.UUID;

public class Oferta implements Comparable<Oferta> {
    private final String id;
    private final String lotId;
    private final Client client;
    private final double suma;
    private final LocalDateTime dataOra;

    public Oferta(String lotId, Client client, double suma) {
        this.id = UUID.randomUUID().toString();
        this.lotId = lotId;
        this.client = client;
        this.suma = suma;
        this.dataOra = LocalDateTime.now();
    }
    public Oferta(String id, String lotId, Client client, double suma, LocalDateTime dataOra) {
        this.id = id;
        this.lotId = lotId;
        this.client = client;
        this.suma = suma;
        this.dataOra = dataOra;
    }

    public String getId() {
        return id;
    }

    public String getLotId() {
        return lotId;
    }

    public Client getClient() {
        return client;
    }

    public double getSuma() {
        return suma;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    @Override
    public int compareTo(Oferta other) {
        int cmp = Double.compare(other.suma, this.suma);
        if (cmp != 0) return cmp;
        return this.id.compareTo(other.id);
    }
}
