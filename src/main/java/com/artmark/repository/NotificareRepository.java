package com.artmark.repository;

import com.artmark.audit.AuditService;
import com.artmark.db.GenericRepository;
import com.artmark.db.RowMapper;
import com.artmark.model.Notificare;
import com.artmark.model.user.Client;
import com.artmark.model.user.Utilizator;
import com.artmark.model.user.Vanzator;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class NotificareRepository extends GenericRepository<Notificare> {

    private static final String SELECT_SQL =
            "SELECT n.id, n.mesaj, n.data_creare, n.citita, " +
                    "       u.id AS u_id, u.tip, u.nume, u.prenume, u.email, " +
                    "       u.parola_hash, u.data_inreg, u.companie, u.buget_maxim " +
                    "FROM NOTIFICARE n " +
                    "JOIN UTILIZATOR u ON n.destinatar_id = u.id ";

    private final RowMapper<Notificare> mapper = rs -> {
        Utilizator destinatar;
        String tip = rs.getString("tip");
        if ("VANZATOR".equals(tip)) {
            destinatar = new Vanzator(rs.getString("u_id"), rs.getString("nume"),
                    rs.getString("prenume"), rs.getString("email"),
                    rs.getString("parola_hash"), rs.getTimestamp("data_inreg").toLocalDateTime(),
                    rs.getString("companie"));
        } else {
            destinatar = new Client(rs.getString("u_id"), rs.getString("nume"),
                    rs.getString("prenume"), rs.getString("email"),
                    rs.getString("parola_hash"), rs.getTimestamp("data_inreg").toLocalDateTime(),
                    rs.getDouble("buget_maxim"));
        }
        return new Notificare(
                rs.getString("id"),
                destinatar,
                rs.getString("mesaj"),
                rs.getTimestamp("data_creare").toLocalDateTime(),
                rs.getBoolean("citita"));
    };

    @Override
    public void save(Notificare n) throws SQLException {
        executeUpdate(
                "INSERT INTO NOTIFICARE(id, destinatar_id, mesaj, citita, data_creare) VALUES(?,?,?,?,?)",
                n.getId(), n.getDestinatar().getId(), n.getMesaj(), n.isCitita(), n.getData());
        AuditService.getInstance().log("save_notificare");
    }

    @Override
    public Optional<Notificare> findById(String id) throws SQLException {
        AuditService.getInstance().log("findById_notificare");
        return executeQuerySingle(SELECT_SQL + "WHERE n.id=?", mapper, id);
    }

    public List<Notificare> findByDestinatarId(String utilizatorId) throws SQLException {
        AuditService.getInstance().log("findByDestinatar_notificari");
        return executeQuery(SELECT_SQL + "WHERE n.destinatar_id=?", mapper, utilizatorId);
    }

    @Override
    public List<Notificare> findAll() throws SQLException {
        AuditService.getInstance().log("findAll_notificari");
        return executeQuery(SELECT_SQL, mapper);
    }

    @Override
    public void update(Notificare n) throws SQLException {
        executeUpdate("UPDATE NOTIFICARE SET citita=? WHERE id=?", n.isCitita(), n.getId());
        AuditService.getInstance().log("update_notificare");
    }

    @Override
    public void delete(String id) throws SQLException {
        executeUpdate("DELETE FROM NOTIFICARE WHERE id=?", id);
        AuditService.getInstance().log("delete_notificare");
    }
}
