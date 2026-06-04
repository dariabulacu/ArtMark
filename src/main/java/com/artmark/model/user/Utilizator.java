package com.artmark.model.user;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Utilizator {
    private final String id;
    private String nume;
    private String prenume;
    private String email;
    private String parolaHash;
    private LocalDateTime dataInregistrare;

    public Utilizator(String nume, String prenume, String email, String parolaHash, LocalDateTime dataInregistrare) {
        this.id = UUID.randomUUID().toString();
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.parolaHash = parolaHash;
        this.dataInregistrare = dataInregistrare;
    }
    protected Utilizator(String id , String nume, String prenume, String email, String parolaHash, LocalDateTime dataInregistrare){
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.parolaHash = parolaHash;
        this.dataInregistrare = dataInregistrare;
    }
    public String getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDataInregistrare() {
        return dataInregistrare;
    }

    public void setDataInregistrare(LocalDateTime dataInregistrare) {
        this.dataInregistrare = dataInregistrare;
    }

    public String getParolaHash() {
        return parolaHash;
    }

    public void schimbaParola(String parolaHashNoua) {
        this.parolaHash = parolaHashNoua;
    }

    public abstract String getRol();
}
