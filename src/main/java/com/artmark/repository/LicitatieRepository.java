package com.artmark.repository;

import com.artmark.audit.AuditService;
import com.artmark.db.GenericRepository;
import com.artmark.db.RowMapper;
import com.artmark.model.Licitatie;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LicitatieRepository extends GenericRepository<Licitatie> {

    //Loturile nu sunt incarcate automat ci se incarca separat prin LotRepository
    private final RowMapper<Licitatie> mapper = rs -> new Licitatie(
            rs.getString("id"),
            rs.getString("titlu"),
            rs.getString("descriere"),
            rs.getTimestamp("data_inceput").toLocalDateTime(),
            rs.getTimestamp("data_sfarsit").toLocalDateTime(),
            Licitatie.StatusLicitatie.valueOf(rs.getString("status")));

    @Override
    public void save(Licitatie l) throws SQLException {
        executeUpdate(
                "INSERT INTO LICITATIE(id,titlu,descriere,data_inceput,data_sfarsit,status) VALUES(?,?,?,?,?,?)",
                l.getId(), l.getTitlu(), l.getDescriere(), l.getDataInceput(), l.getDataSfarsit(), l.getStatus().name());
        AuditService.getInstance().log("save_licitatie");
    }

    @Override
    public Optional<Licitatie> findById(String id) throws SQLException {
        AuditService.getInstance().log("findById_licitatie");
        return executeQuerySingle("SELECT * FROM LICITATIE WHERE id=?", mapper, id);
    }

    @Override
    public List<Licitatie> findAll() throws SQLException {
        AuditService.getInstance().log("findAll_licitatii");
        return executeQuery("SELECT * FROM LICITATIE", mapper);
    }

    @Override
    public void update(Licitatie l) throws SQLException {
        executeUpdate(
                "UPDATE LICITATIE SET titlu=?, descriere=?, status=? WHERE id=?",
                l.getTitlu(), l.getDescriere(), l.getStatus().name(), l.getId());
        AuditService.getInstance().log("update_licitatie");
    }

    @Override
    public void delete(String id) throws SQLException {
        executeUpdate("DELETE FROM LICITATIE WHERE id=?", id);
        AuditService.getInstance().log("delete_licitatie");
    }
}