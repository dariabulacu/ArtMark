package com.artmark.db;

import java.sql.ResultSet;
import java.sql.SQLException;


//Transforma un rand din RowSet intr un obiect Java, fiecare repository isi definestes propriul RowMapper cu clasa anonima
@FunctionalInterface
public interface RowMapper<T> {
    T map(ResultSet rs) throws SQLException;
}
