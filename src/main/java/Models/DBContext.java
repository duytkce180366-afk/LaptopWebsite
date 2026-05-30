//package Models;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class DBContext {
//
//    private Connection conn;
//    private final String DB_USER;
//    private final String DB_PWD;
//    private final String DB_HOST;
//    private final String DB_NAME;
//    private final String DB_PORT;
//
//    public static boolean isNullOrEmptyConfig(String config) {
//        return config == null || config.isEmpty();
//    }
//
//    public DBContext() {
//        DB_PWD = isNullOrEmptyConfig(System.getenv("DB_PWD")) ? "123" : System.getenv("DB_PWD");
//        DB_HOST = isNullOrEmptyConfig(System.getenv("DB_HOST")) ? "localhost" : System.getenv("DB_HOST");
//        DB_PORT = isNullOrEmptyConfig(System.getenv("DB_PORT")) ? "1433" : System.getenv("DB_PORT");
//        DB_USER = isNullOrEmptyConfig(System.getenv("DB_USER")) ? "sa" : System.getenv("DB_USER");
//        DB_NAME = isNullOrEmptyConfig(System.getenv("DB_NAME")) ? "LaptopWebsiteDB" : System.getenv("DB_NAME");
//
//        String dbUrl = String.format(
//                "jdbc:sqlserver://%s:%s;databaseName=%s;TrustServerCertificate=True;",
//                DB_HOST,
//                DB_PORT,
//                DB_NAME
//        );
//
//        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            this.conn = DriverManager.getConnection(dbUrl, DB_USER, DB_PWD);
//        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public Connection getConnection() {
//        return conn;
//    }
//}
