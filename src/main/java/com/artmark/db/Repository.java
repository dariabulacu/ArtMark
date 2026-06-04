package com.artmark.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


//Interfata pentru produs, utilizator, categorie si licitatie
//Implementare de operatii CRUD
public interface Repository<T> {
    void save(T entity) throws SQLException; //<=> INSERT INTO
    Optional<T> findById(String id) throws SQLException; //<=> SELECT * FROM ... WHERE id (pentru ca pot sa am si null ca sa nu mai dau throw la NullPointerException folosesc Optional)
    List<T> findAll() throws SQLException;// SELECT * FROM ...
    void update(T entity) throws SQLException; // UPDATE ... SET
    void delete (String id) throws SQLException;// DELETE FROM ... WHERE id = ...
}
