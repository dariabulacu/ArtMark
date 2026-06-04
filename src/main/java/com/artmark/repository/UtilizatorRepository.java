package com.artmark.repository;

import com.artmark.audit.AuditService;
import com.artmark.db.GenericRepository;
import com.artmark.db.RowMapper;
import com.artmark.model.user.Client;
import com.artmark.model.user.Utilizator;
import com.artmark.model.user.Vanzator;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UtilizatorRepository extends GenericRepository<Utilizator> {
    private final RowMapper<Utilizator> mapper = rs ->{
        String id = rs.getString("id");
        String tip = rs.getString("tip");
        String nume = rs.getString("nume");
        String prenume = rs.getString("prenume");
        String email = rs.getString("email");
        String parola = rs.getString("parola_hash");
        LocalDateTime dataInregistrare = rs.getTimestamp("data_inreg").toLocalDateTime();
        if ("VANZATOR".equalsIgnoreCase(tip)){
            return new Vanzator(id,nume,prenume,email,parola,dataInregistrare,rs.getString("companie"));
        }else{
            return new Client(id,nume,prenume,email,parola,dataInregistrare,rs.getDouble("buget_maxim"));
        }
    };
    @Override
    public void save (Utilizator u) throws SQLException{
        if (u instanceof Vanzator v){
            executeUpdate(
                    "INSERT INTO UTILIZATOR(id,tip,nume,prenume,email,parola_hash,data_inreg,companie) VALUES (?,?,?,?,?,?,?,?)",
                    v.getId(), "VANZATOR", v.getNume(), v.getPrenume(), v.getEmail(), v.getParolaHash(), v.getDataInregistrare(), v.getCompanie());
        }else if (u instanceof Client v){
            executeUpdate("INSERT INTO UTILIZATOR(id,tip,nume,prenume,email,parola_hash,data_inreg,buget_maxim) VALUES (?,?,?,?,?,?,?,?)",
                    v.getId(), "CLIENT", v.getNume(), v.getPrenume(), v.getEmail(), v.getParolaHash(), v.getDataInregistrare(), v.getBugetMaxim());
        }
        AuditService.getInstance().log("save_utilizator");
    }
    @Override
    public Optional<Utilizator> findById(String id) throws SQLException {
        AuditService.getInstance().log("findById_utilizator");
        return executeQuerySingle("SELECT * FROM UTILIZATOR WHERE id=?", mapper, id);
    }

    public Optional<Utilizator> findByEmail(String email) throws SQLException {
        AuditService.getInstance().log("findByEmail_utilizator");
        return executeQuerySingle("SELECT * FROM UTILIZATOR WHERE email=?", mapper, email);
    }

    @Override
    public List<Utilizator> findAll() throws SQLException {
        AuditService.getInstance().log("findAll_utilizatori");
        return executeQuery("SELECT * FROM UTILIZATOR", mapper);
    }

    @Override
    public void update(Utilizator u) throws SQLException {
        if (u instanceof Vanzator v) {
            executeUpdate(
                    "UPDATE UTILIZATOR SET nume=?,prenume=?,email=?,companie=? WHERE id=?",
                    v.getNume(), v.getPrenume(), v.getEmail(), v.getCompanie(), v.getId());
        } else if (u instanceof Client c) {
            executeUpdate(
                    "UPDATE UTILIZATOR SET nume=?,prenume=?,email=?,buget_maxim=? WHERE id=?",
                    c.getNume(), c.getPrenume(), c.getEmail(), c.getBugetMaxim(), c.getId());
        }
        AuditService.getInstance().log("update_utilizator");
    }

    @Override
    public void delete(String id) throws SQLException {
        executeUpdate("DELETE FROM UTILIZATOR WHERE id=?", id);
        AuditService.getInstance().log("delete_utilizator");
    }
}
