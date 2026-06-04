package com.artmark.db;


import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//clasa abstracta cu metode helper pentru jdbc, fiecare respository extinde clasa si are gratuit executeUpdate si executeQuery
public abstract class GenericRepository<T> implements Repository<T> {
    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private void setParameters(PreparedStatement ps, Object...params) throws SQLException{
        for (int i = 0; i<params.length; i++){
            if (params[i]==null)
                ps.setNull(i+1, Types.NULL);
            else
                ps.setObject(i+1,params[i]);
        }
    }
    //folosit pentru update insert delete
    protected void executeUpdate(String sql, Object... params) throws SQLException{
        try(PreparedStatement ps = getConnection().prepareStatement(sql)){
            setParameters(ps,params);
            ps.executeUpdate();

        }
    }

    //folosit pentru SELECT care ret mai multe randuri, iar mapper stie sa transforme un rand intr un obiect
    protected List<T> executeQuery(String sql, RowMapper<T> mapper, Object... param) throws SQLException{
        List<T> res = new ArrayList<>();
        try(PreparedStatement ps = getConnection().prepareStatement(sql)){
            setParameters(ps,param);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    res.add(mapper.map(rs));
                }
            }
        }
        return res;
    }

    //SELECT care ret cel mult un rand de ex getByid
    protected Optional<T> executeQuerySingle(String sql, RowMapper<T> mapper, Object... param) throws SQLException{
        List<T> res = executeQuery(sql, mapper, param);
        if (res.isEmpty())
            return Optional.empty();
        return Optional.of(res.getFirst());
    }
}
