package com.artmark.repository;

import com.artmark.audit.AuditService;
import com.artmark.db.GenericRepository;
import com.artmark.db.RowMapper;
import com.artmark.model.Categorie;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CategorieRepository extends GenericRepository<Categorie> {
    private final RowMapper<Categorie> mapper = rs -> new Categorie(
            rs.getString("id"),
            rs.getString("nume"),
            rs.getString("descriere")
    );
    @Override
    public void save(Categorie c) throws SQLException {
        executeUpdate("INSERT INTO CATEGORIE(id, nume, descriere) VALUES (?,?,?)",
                c.getId(), c.getNume(), c.getDescriere());
    }
    @Override
    public Optional<Categorie> findById(String id) throws SQLException{
        AuditService.getInstance().log("findById_categorie");
        return executeQuerySingle("SELECT * FROM CATEGORIE WHERE id = ?", mapper, id);
    }
    @Override
    public List<Categorie> findAll() throws SQLException{
        AuditService.getInstance().log("findAll_categorie");
        return executeQuery("SELECT * FROM CATEGORIE", mapper);
    }
    @Override
    public void update(Categorie c) throws SQLException{
        executeUpdate("UPDATE CATEGORIE SET nume=?, descriere = ? WHERE id = ?",
                c.getNume(), c.getDescriere(), c.getId());
        AuditService.getInstance().log("update_categorie");
    }

    @Override
    public void delete(String id) throws SQLException{
        executeUpdate("DELETE FROM CATEGORIE WHERE id = ?", id);
        AuditService.getInstance().log("delete_categorie");
    }
}
