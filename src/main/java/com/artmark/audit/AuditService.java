package com.artmark.audit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class AuditService {
    private static volatile AuditService instance;
    private final PrintWriter writer;

    private AuditService(){
        try{
            writer = new PrintWriter(new FileWriter("audit.csv", true));
            writer.println("actiune, timestamp");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Nu se poate deschide audit.csv.",e);
        }
    }
    public static AuditService getInstance(){
        if (instance==null){
            synchronized (AuditService.class) {
                if (instance == null)
                    instance = new AuditService();
            }
        }
        return instance;
    }
    public void log(String numeActiune){
        writer.println(numeActiune + "||" + LocalDateTime.now());
        writer.flush();
    }
}
