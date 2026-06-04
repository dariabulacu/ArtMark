package com.artmark.repository;

import com.artmark.audit.AuditService;
import com.artmark.db.GenericRepository;
import com.artmark.model.Lot;
import com.artmark.model.Oferta;
import com.artmark.model.Tranzactie;
import com.artmark.model.user.Utilizator;
import com.artmark.model.user.Vanzator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TranzactieRepository extends GenericRepository<Tranzactie> {

    // Folosim celelalte repository-uri pentru a incarca obiectele asociate
    private final LotRepository lotRepo = new LotRepository();
    private final OfertaRepository ofertaRepo = new OfertaRepository();
    private final UtilizatorRepository utilizatorRepo = new UtilizatorRepository();

    @Override
    public void save(Tranzactie t) throws SQLException {
        executeUpdate(
                "INSERT INTO TRANZACTIE(id, lot_id, oferta_id, vanzator_id, data_finalizarii, status) VALUES(?,?,?,?,?,?)",
                t.getId(),
                t.getLot().getId(),
                t.getOfertaCastigatoare().getId(),
                t.getVanzator().getId(),
                t.getDataFinalizarii(),
                t.getStatus().name());
        AuditService.getInstance().log("save_tranzactie");
    }

    @Override
    public Optional<Tranzactie> findById(String id) throws SQLException {
        AuditService.getInstance().log("findById_tranzactie");

        // Citim doar ID-urile din tabel
        List<String[]> rows = new ArrayList<>();
        executeQuery(
                "SELECT * FROM TRANZACTIE WHERE id=?",
                rs -> {
                    rows.add(new String[]{
                            rs.getString("id"),
                            rs.getString("lot_id"),
                            rs.getString("oferta_id"),
                            rs.getString("vanzator_id"),
                            rs.getTimestamp("data_finalizarii").toLocalDateTime().toString(),
                            rs.getString("status")
                    });
                    return null; // mapper-ul e folosit doar pentru colectare
                }, id);

        if (rows.isEmpty()) return Optional.empty();

        return Optional.of(construiesteTranzactie(rows.getFirst()));
    }

    @Override
    public List<Tranzactie> findAll() throws SQLException {
        AuditService.getInstance().log("findAll_tranzactii");

        List<String[]> rows = new ArrayList<>();
        executeQuery(
                "SELECT * FROM TRANZACTIE",
                rs -> {
                    rows.add(new String[]{
                            rs.getString("id"),
                            rs.getString("lot_id"),
                            rs.getString("oferta_id"),
                            rs.getString("vanzator_id"),
                            rs.getTimestamp("data_finalizarii").toLocalDateTime().toString(),
                            rs.getString("status")
                    });
                    return null;
                });

        List<Tranzactie> rezultat = new ArrayList<>();
        for (String[] row : rows) {
            rezultat.add(construiesteTranzactie(row));
        }
        return rezultat;
    }

    @Override
    public void update(Tranzactie t) throws SQLException {
        executeUpdate(
                "UPDATE TRANZACTIE SET status=?, data_finalizarii=? WHERE id=?",
                t.getStatus().name(), t.getDataFinalizarii(), t.getId());
        AuditService.getInstance().log("update_tranzactie");
    }

    @Override
    public void delete(String id) throws SQLException {
        executeUpdate("DELETE FROM TRANZACTIE WHERE id=?", id);
        AuditService.getInstance().log("delete_tranzactie");
    }

    // Incarca obiectele complete pe baza ID-urilor citite din tabel
    private Tranzactie construiesteTranzactie(String[] row) throws SQLException {
        String id = row[0];
        String lotId  = row[1];
        String ofertaId = row[2];
        String vanzatorId = row[3];
        java.time.LocalDateTime data = java.time.LocalDateTime.parse(row[4]);
        Tranzactie.StatusTranzactie status = Tranzactie.StatusTranzactie.valueOf(row[5]);
        Lot lot = lotRepo.findById(lotId).orElseThrow();
        Oferta oferta = ofertaRepo.findById(ofertaId).orElseThrow();
        Vanzator vanz = (Vanzator) utilizatorRepo.findById(vanzatorId).orElseThrow();

        return new Tranzactie(id, lot, oferta, vanz, data, status);
    }
}
