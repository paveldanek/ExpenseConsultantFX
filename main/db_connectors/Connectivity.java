package db_connectors;

import crypto.AESUtil;
import entities.Transaction;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class Connectivity {
    private static final String USER = "root";
    private static final String PASS = "ics49901";
    private static Connection connection = null;
    private static boolean errorShown = false;

    private static Connection getConnected() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/expense_consultant", USER, PASS);
        } catch (SQLException e) {
            dbProblemConnecting();
            if (!buildDatabase()) {
                connectionCannotBeEstablished();
                System.exit(1);
            }
        }
        if (!checkTable("users")) {
            if (!errorShown) {
                dbIsNotIntact();
                errorShown=true;
            }
            if (!buildDatabase()) {
                dbCannotBeFixed();
                System.exit(1);
            }
        }
        if (!checkTable("transaction")) {
            if (!errorShown) {
                dbIsNotIntact();
                errorShown=true;
            }
            if (!createTableTransaction()) {
                dbCannotBeFixed();
                System.exit(1);
            }
        }
        if (!checkTable("category")) {
            if (!errorShown) {
                dbIsNotIntact();
                errorShown=true;
            }
            if (!createTableCategory()) {
                dbCannotBeFixed();
                System.exit(1);
            }
        }
        return connection;
    }

    public static Connection getConnection() {
        if (connection==null) return getConnected();
        else {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost/expense_consultant", USER, PASS);
            } catch (SQLException e) {
                connectionCannotBeEstablished();
                System.exit(1);
            }
            return connection;
        }
    }

    private static void dbProblemConnecting() {
        JOptionPane.showMessageDialog(null, "There's a problem connecting\n" +
                "to database. Attempting to fix...", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void dbIsNotIntact() {
        JOptionPane.showMessageDialog(null, "The database does not seem\n"+
                "to be intact. Fixing...","Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void dbCannotBeFixed() {
        JOptionPane.showMessageDialog(null, "The database cannot be\n" +
                "fixed at this time.", "Error", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void connectionCannotBeEstablished() {
        JOptionPane.showMessageDialog(null, "Connection to database\n"+
                "cannot be established.","Error", JOptionPane.INFORMATION_MESSAGE);
    }

    private static boolean buildDatabase() {
        try {
             if (!createDatabaseExpenseConsultant()) throw new SQLException();
             if (!createTableUsers()) throw new SQLException();
             if (!createTableTransaction()) throw new SQLException();
             if (!createTableCategory()) throw new SQLException();
        } catch (SQLException e) {
            return false;
        }
        int rowsAffected;
        String sql;
        PreparedStatement s;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/expense_consultant", USER, PASS);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private static boolean checkTable(String table) {
        try {
            String query = "SELECT * FROM "+table;
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private static boolean createDatabaseExpenseConsultant() {
        int rowsAffected;
        String sql;
        PreparedStatement s;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/", USER, PASS);
            sql = "DROP SCHEMA IF EXISTS expense_consultant";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
            sql = "CREATE SCHEMA expense_consultant";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
            if (rowsAffected == 0) throw new SQLException();
            sql = "USE expense_consultant";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private static boolean createTableUsers() {
        int rowsAffected;
        String sql;
        PreparedStatement s;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/expense_consultant", USER, PASS);
            sql = "DROP TABLE IF EXISTS users";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
            sql = "CREATE TABLE users(user_id int NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "email varchar(100) NOT NULL UNIQUE, password varchar(100) NOT NULL, " +
                    "question1 varchar(100), question2 varchar(100), answer1 varchar(75), " +
                    "answer2 varchar(75), created_date datetime DEFAULT CURRENT_TIMESTAMP)";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private static boolean createTableTransaction() {
        int rowsAffected;
        String sql;
        PreparedStatement s;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/expense_consultant", USER, PASS);
            sql = "DROP TABLE IF EXISTS transaction";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
            sql = "CREATE TABLE transaction(transaction_id int NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "transaction_date varchar(25), transaction_history text, " +
                    "bank_name varchar(25), account_nick varchar(30), " +
                    "user_id int, FOREIGN KEY (user_id) REFERENCES users (user_id))";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private static boolean createTableCategory() {
        int rowsAffected;
        String sql;
        PreparedStatement s;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/expense_consultant", USER, PASS);
            sql = "DROP TABLE IF EXISTS category";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
            sql = "CREATE TABLE category(category_id int NOT NULL PRIMARY KEY AUTO_INCREMENT, "+
                    "category_name varchar(30), user_id int, FOREIGN KEY (user_id) REFERENCES users (user_id))";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public static boolean aUserExists() {
        Connection conn = getConnection();
        if (!checkTable("users")) return false;
        ResultSet rs = null;
        int count = 0;
        try {
            String checkForUsers = "SELECT COUNT(*) FROM users";
            PreparedStatement checkStmt = conn.prepareStatement(checkForUsers);
            // Execute the query to check if any entry in "users" table exists
            rs = checkStmt.executeQuery();
            rs.next();
            count = rs.getInt(1);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return count>0;
    }

}
