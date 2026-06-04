package com.artmark.repository;

import com.artmark.audit.AuditService;
import com.artmark.db.GenericRepository;
import com.artmark.db.RowMapper;
import com.artmark.model.Oferta;
import com.artmark.model.user.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OfertaRepository extends GenericRepository<Oferta> {

    private static final String SELECT_SQL =
            "SELECT o.id, o.suma, o.data_ora, o.lot_id, " +
                    "       u.id AS c_id, u.nume AS c_nume, u.prenume AS c_prenume, " +
                    "       u.email AS c_email, u.parola_hash AS c_parola, " +
                    "       u.data_inreg AS c_data, u.buget_maxim AS c_buget " +
                    "FROM OFERTA o " +
                    "JOIN UTILIZATOR u ON o.client_id = u.id ";

    private final RowMapper<Oferta> mapper = rs -> {
        Client client = new Client(
                rs.getString("c_id"),
                rs.getString("c_nume"),
                rs.getString("c_prenume"),
                rs.getString("c_email"),
                rs.getString("c_parola"),
                rs.getTimestamp("c_data").toLocalDateTime(),
                rs.getDouble("c_buget"));
        return new Oferta(
                rs.getString("id"),
                rs.getString("lot_id"),
                client,
                rs.getDouble("suma"),
                rs.getTimestamp("data_ora").toLocalDateTime());
    };

    @Override
    public void save(Oferta o) throws SQLException {
        // oferta isi poarta singura lot_id-ul
        executeUpdate(
                "INSERT INTO OFERTA(id, lot_id, client_id, suma, data_ora) VALUES(?,?,?,?,?)",
                o.getId(), o.getLotId(), o.getClient().getId(), o.getSuma(), o.getDataOra());
        AuditService.getInstance().log("save_oferta");
    }

    @Override
    public Optional<Oferta> findById(String id) throws SQLException {
        AuditService.getInstance().log("findById_oferta");
        return executeQuerySingle(SELECT_SQL + "WHERE o.id=?", mapper, id);
    }

    public List<Oferta> findByLotId(String lotId) throws SQLException {
        AuditService.getInstance().log("findByLotId_oferta");
        return executeQuery(SELECT_SQL + "WHERE o.lot_id=? ORDER BY o.suma DESC", mapper, lotId);
    }

    // toate ofertele unui client, cele mai recente primele
    public List<Oferta> findByClientId(String clientId) throws SQLException {
        AuditService.getInstance().log("findByClientId_oferta");
        return executeQuery(SELECT_SQL + "WHERE o.client_id=? ORDER BY o.data_ora DESC", mapper, clientId);
    }

    @Override
    public List<Oferta> findAll() throws SQLException {
        AuditService.getInstance().log("findAll_oferte");
        return executeQuery(SELECT_SQL, mapper);
    }

    @Override
    public void update(Oferta o) throws SQLException {
        // Ofertele nu se modifica dupa creare
        throw new UnsupportedOperationException("Ofertele sunt imutabile.");
    }

    @Override
    public void delete(String id) throws SQLException {
        executeUpdate("DELETE FROM OFERTA WHERE id=?", id);
        AuditService.getInstance().log("delete_oferta");
    }
}