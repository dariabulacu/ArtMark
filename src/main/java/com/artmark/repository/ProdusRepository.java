package com.artmark.repository;

import com.artmark.audit.AuditService;
import com.artmark.db.GenericRepository;
import com.artmark.db.RowMapper;
import com.artmark.model.Categorie;
import com.artmark.model.Produs;
import com.artmark.model.user.Vanzator;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProdusRepository extends GenericRepository<Produs> {
    // JOIN cu categorii si utilizatori (vanzator) intr un  singur query
    private static final String SELECT_SQL =
            "SELECT p.id, p.titlu, p.descriere, p.data_adaugarii, p.disponibil, c.id AS cat_id," +
                    " c.nume AS cat_nume, c.descriere AS cat_desc, " +
                    "u.id AS v_id, u.nume AS v_nume, u.prenume AS v_prenume, " +
                    "u.email AS v_email, u.parola_hash AS v_parola, " +
                    "u.data_inreg AS v_data, u.companie AS v_companie " +
                    "FROM PRODUS p " +
                    "JOIN CATEGORIE c ON p.categorie_id = c.id " +
                    "JOIN UTILIZATOR u ON p.vanzator_id = u.id ";

    private final RowMapper<Produs> mapper = rs -> {
        Categorie cat = new Categorie(
                rs.getString("cat_id"),
                rs.getString("cat_nume"),
                rs.getString("cat_desc"));

        Vanzator vanz = new Vanzator(
                rs.getString("v_id"),
                rs.getString("v_nume"),
                rs.getString("v_prenume"),
                rs.getString("v_email"),
                rs.getString("v_parola"),
                rs.getTimestamp("v_data").toLocalDateTime(),
                rs.getString("v_companie"));

        return new Produs(
                rs.getString("id"),
                rs.getString("titlu"),
                rs.getString("descriere"),
                cat, vanz,
                rs.getTimestamp("data_adaugarii").toLocalDateTime(),
                rs.getBoolean("disponibil"));
    };

    @Override
    public void save(Produs p) throws SQLException {
        executeUpdate(
                "INSERT INTO PRODUS(id,titlu,descriere,categorie_id,vanzator_id,data_adaugarii,disponibil) VALUES(?,?,?,?,?,?,?)",
                p.getId(), p.getTitlu(), p.getDescriere(),
                p.getCategorie().getId(), p.getVanzator().getId(),
                p.getDataAdaugarii(), p.isDisponibil());
        AuditService.getInstance().log("save_produs");
    }

    @Override
    public Optional<Produs> findById(String id) throws SQLException {
        AuditService.getInstance().log("findById_produs");
        return executeQuerySingle(SELECT_SQL + "WHERE p.id=?", mapper, id);
    }

    @Override
    public List<Produs> findAll() throws SQLException {
        AuditService.getInstance().log("findAll_produse");
        return executeQuery(SELECT_SQL, mapper);
    }

    @Override
    public void update(Produs p) throws SQLException {
        executeUpdate(
                "UPDATE PRODUS SET titlu=?, descriere=?, categorie_id=?, disponibil=? WHERE id=?",
                p.getTitlu(), p.getDescriere(), p.getCategorie().getId(),
                p.isDisponibil(), p.getId());
        AuditService.getInstance().log("update_produs");
    }

    @Override
    public void delete(String id) throws SQLException {
        executeUpdate("DELETE FROM PRODUS WHERE id=?", id);
        AuditService.getInstance().log("delete_produs");
    }
    public List<Produs> findByLotId(String lotId) throws SQLException {
        return executeQuery(SELECT_SQL + "WHERE p.lot_id=?", mapper, lotId);
    }

}
