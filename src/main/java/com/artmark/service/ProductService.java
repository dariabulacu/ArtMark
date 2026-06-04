package com.artmark.service;

import com.artmark.model.Categorie;
import com.artmark.model.Produs;
import com.artmark.model.user.Vanzator;
import com.artmark.repository.CategorieRepository;
import com.artmark.repository.ProdusRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// stocare in db prin repository-uri
public class ProductService {
    private final CategorieRepository categorieRepo;
    private final ProdusRepository produsRepo;

    public ProductService(CategorieRepository categorieRepo, ProdusRepository produsRepo) {
        this.categorieRepo = categorieRepo;
        this.produsRepo = produsRepo;
    }


    public Produs adaugaProdus(String titlu, String descriere, Categorie cat, Vanzator vanzator){
        Produs produs = new Produs(titlu, descriere, cat, vanzator, LocalDateTime.now());
        Persistenta.executa(() -> produsRepo.save(produs));
        return produs;
    }

    public Optional<Produs> cautaProdusId(String id){
        return Persistenta.interogheaza(() -> produsRepo.findById(id));
    }

    public List<Produs> produseVanzator(String vanzatorId){
        return toateProdusele().stream()
                .filter(p-> p.getVanzator().getId().equals(vanzatorId))
                .collect(Collectors.toList());
    }

    public Categorie adaugaCategorie(String nume, String descriere){
        boolean exista = toateCategoriile().stream()
                .anyMatch(c -> c.getNume().equalsIgnoreCase(nume));
        if (exista){
            throw new IllegalArgumentException("Categoria exista deja: " + nume);
        }
        Categorie categorie = new Categorie(nume, descriere);
        Persistenta.executa(() -> categorieRepo.save(categorie));
        return categorie;
    }

    public Optional<Categorie> cautaCategorieId(String id){
        return Persistenta.interogheaza(() -> categorieRepo.findById(id));
    }

    public Optional<Categorie> cautaCategorieNume(String numeCategorie){
        return toateCategoriile().stream()
                .filter(c -> c.getNume().equalsIgnoreCase(numeCategorie))
                .findFirst();
    }

    public List<Categorie> toateCategoriile(){
        return Persistenta.interogheaza(categorieRepo::findAll);
    }

    public List<Produs> toateProdusele(){
        return Persistenta.interogheaza(produsRepo::findAll);
    }
}
