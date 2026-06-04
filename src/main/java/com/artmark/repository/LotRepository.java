package com.artmark.repository;
import com.artmark.audit.AuditService;
import com.artmark.db.GenericRepository;
import com.artmark.db.RowMapper;
import com.artmark.model.Lot;
import com.artmark.model.Produs;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LotRepository extends GenericRepository<Lot> {

    private final ProdusRepository produsRepo = new ProdusRepository();

    private final RowMapper<Lot> mapper = rs -> {
        Lot lot = new Lot(
                rs.getString("id"),
                rs.getString("licitatie_id"),
                rs.getInt("nr_lot"),
                rs.getDouble("pret_inceput"),
                rs.getDouble("estimare_min"),
                rs.getDouble("estimare_max"),
                Lot.StatusLot.valueOf(rs.getString("status")));
        // Incarca produsele asociate lotului
        List<Produs> produse = produsRepo.findByLotId(lot.getId());
        produse.forEach(lot::addProdus);
        return lot;
    };

    @Override
    public void save(Lot lot) throws SQLException {
        // lotul isi poarta singur licitatie_id-ul
        executeUpdate(
                "INSERT INTO LOT(id,nr_lot,licitatie_id,pret_inceput,estimare_min,estimare_max,status) VALUES(?,?,?,?,?,?,?)",
                lot.getId(), lot.getNrLot(), lot.getLicitatieId(),
                lot.getPretInceput(), lot.getEstimareMin(), lot.getEstimareMax(), lot.getStatus().name());
        // lot_id-ul se seteaza in tabela produse pentru fiecare produs din lot
        for (Produs p : lot.getProduse()) {
            executeUpdate("UPDATE PRODUS SET lot_id=? WHERE id=?", lot.getId(), p.getId());
        }
        AuditService.getInstance().log("save_lot");
    }

    @Override
    public Optional<Lot> findById(String id) throws SQLException {
        AuditService.getInstance().log("findById_lot");
        return executeQuerySingle("SELECT * FROM LOT WHERE id=?", mapper, id);
    }

    @Override
    public List<Lot> findAll() throws SQLException {
        AuditService.getInstance().log("findAll_loturi");
        return executeQuery("SELECT * FROM LOT", mapper);
    }

    public List<Lot> findByLicitatieId(String licitatieId) throws SQLException {
        return executeQuery("SELECT * FROM LOT WHERE licitatie_id=?", mapper, licitatieId);
    }

    @Override
    public void update(Lot lot) throws SQLException {
        executeUpdate(
                "UPDATE LOT SET pret_inceput=?, estimare_min=?, estimare_max=?, status=? WHERE id=?",
                lot.getPretInceput(), lot.getEstimareMin(), lot.getEstimareMax(), lot.getStatus().name(), lot.getId());
        AuditService.getInstance().log("update_lot");
    }

    @Override
    public void delete(String id) throws SQLException {
        executeUpdate("DELETE FROM LOT WHERE id=?", id);
        AuditService.getInstance().log("delete_lot");
    }
}
