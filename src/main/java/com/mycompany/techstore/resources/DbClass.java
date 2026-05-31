package com.mycompany.techstore.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbClass {

    private Connection conn;
    private final String DB_USER;
    private final String DB_PWD;
    private final String DB_HOST;
    private final String DB_NAME;
    private final String DB_PORT;

    public static boolean isNullOrEmptyConfig(String config) {
        return (config == null || config.length() == 0);
    }

    public DbClass() {
        // Defer actual connection initialization until first use to avoid throwing
        // during servlet/container startup. Environment validation happens on first getConnection().
        DB_PWD = isNullOrEmptyConfig(System.getenv("DB_PWD")) ? "abcD_123" : System.getenv("DB_PWD");
        DB_HOST = isNullOrEmptyConfig(System.getenv("DB_HOST")) ? "localhost" : System.getenv("DB_HOST");
        DB_PORT = isNullOrEmptyConfig(System.getenv("DB_PORT")) ? "1433" : System.getenv("DB_PORT");
        DB_USER = isNullOrEmptyConfig(System.getenv("DB_USER")) ? "sa" : System.getenv("DB_USER");
        DB_NAME = isNullOrEmptyConfig(System.getenv("DB_NAME")) ? "LaptopWebsiteDB" : System.getenv("DB_NAME");
    }

    public Connection getConnection() {
        // Lazy initialize connection
        if (this.conn != null) {
            try {
                if (this.conn.isValid(2)) {
                    return this.conn;
                } else {
                    try {
                        this.conn.close();
                    } catch (SQLException ignore) {
                    }
                    this.conn = null;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DbClass.class.getName()).log(Level.WARNING, "Error validating DB connection, will recreate", ex);
                try {
                    this.conn.close();
                } catch (SQLException ignore) {
                }
                this.conn = null;
            }
        }

        if (isNullOrEmptyConfig(DB_USER) || isNullOrEmptyConfig(DB_PWD)) {
            Logger.getLogger(DbClass.class.getName()).log(Level.SEVERE, "Database credentials (DB_USER/DB_PWD) are not set in environment variables.");
            throw new IllegalStateException("Missing database credentials (DB_USER/DB_PWD)");
        }

        String DB_URL = "jdbc:sqlserver://%s:%s;databaseName=%s;TrustServerCertificate=True;".formatted(
                DB_HOST,
                DB_PORT,
                DB_NAME
        );

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            this.conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
            return this.conn;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DbClass.class.getName()).log(Level.SEVERE, "Failed to initialize database connection", ex);
            throw new IllegalStateException("Failed to initialize database connection", ex);
        }
    }
}
