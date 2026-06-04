package com.artmark.service;

import com.artmark.model.user.Client;
import com.artmark.model.user.Utilizator;
import com.artmark.model.user.Vanzator;
import com.artmark.repository.UtilizatorRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

//totul se scrie prin utilizatorRepository si se salveaza direct in baza de date
public class UserService {

    // PBKDF2WithHmacSHA256 parametri conform recomandarii OWASP 2024
    private static final int ITERATIONS = 310_000;
    private static final int KEY_LENGTH = 256; // biti
    private static final String ALGORITHM  = "PBKDF2WithHmacSHA256";

    private final UtilizatorRepository utilizatorRepo;

    public UserService(UtilizatorRepository utilizatorRepo) {
        this.utilizatorRepo = utilizatorRepo;
    }

    public Client inregistreazaClient(String nume, String prenume, String email, String parola, double bugetMaxim) {
        valideazaEmailUnic(email);
        Client client = new Client(nume, prenume, email,
                hashParola(parola), LocalDateTime.now(), bugetMaxim);
        Persistenta.executa(() -> utilizatorRepo.save(client));
        return client;
    }

    public Vanzator inregistreazaVanzator(String nume, String prenume, String email, String parola, String companie) {
        valideazaEmailUnic(email);
        Vanzator vanzator = new Vanzator(nume, prenume, email,
                hashParola(parola), LocalDateTime.now(), companie);
        Persistenta.executa(() -> utilizatorRepo.save(vanzator));
        return vanzator;
    }

    public Optional<Utilizator> autentificare(String email, String parola) {
        Optional<Utilizator> utilizator = Persistenta.interogheaza(() -> utilizatorRepo.findByEmail(email));
        if (utilizator.isPresent() && verificaParola(parola, utilizator.get().getParolaHash())) {
            return utilizator;
        }
        return Optional.empty();
    }

    public void schimbaParola(String utilizatorId, String parolaVeche, String parolaNoua) {
        Utilizator u = gasesteDupaId(utilizatorId)
                .orElseThrow(() -> new NoSuchElementException("Utilizator negasit: " + utilizatorId));
        if (!verificaParola(parolaVeche, u.getParolaHash())) {
            throw new IllegalArgumentException("Parola veche este incorecta.");
        }
        u.schimbaParola(hashParola(parolaNoua));
        Persistenta.executa(() -> utilizatorRepo.update(u));
    }

    public Optional<Utilizator> gasesteDupaId(String id) {
        return Persistenta.interogheaza(() -> utilizatorRepo.findById(id));
    }

    public List<Utilizator> totiUtilizatorii() {
        return Persistenta.interogheaza(utilizatorRepo::findAll);
    }

    public List<Client> totiClientii() {
        return totiUtilizatorii().stream()
                .filter(u -> u instanceof Client)
                .map(u -> (Client) u)
                .collect(Collectors.toList());
    }

    public List<Vanzator> totiVanzatorii() {
        return totiUtilizatorii().stream()
                .filter(u -> u instanceof Vanzator)
                .map(u -> (Vanzator) u)
                .collect(Collectors.toList());
    }

   private String hashParola(String parola) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            byte[] hash = pbkdf2(parola.toCharArray(), salt);

            Base64.Encoder enc = Base64.getEncoder();
            return enc.encodeToString(salt) + ":" + enc.encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la hash-uire parola", e);
        }
    }

    private boolean verificaParola(String parolaIntrodusa, String parolaHashStocata) {
        try {
            String[] parts = parolaHashStocata.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hashReal = Base64.getDecoder().decode(parts[1]);
            byte[] hashTest = pbkdf2(parolaIntrodusa.toCharArray(), salt);

            return MessageDigest.isEqual(hashReal, hashTest);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la verificare parola", e);
        }
    }

    private byte[] pbkdf2(char[] parola, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(parola, salt, ITERATIONS, KEY_LENGTH);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();   // sterge parola din memorie imediat dupa folosire
        }
    }


    private void valideazaEmailUnic(String email) {
        Optional<Utilizator> existent = Persistenta.interogheaza(() -> utilizatorRepo.findByEmail(email));
        if (existent.isPresent()) {
            throw new IllegalArgumentException("Email-ul este deja inregistrat: " + email);
        }
    }
}
