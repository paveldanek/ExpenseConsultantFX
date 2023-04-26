package db_connectors;

import crypto.AESUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

/**
 * This class handles the connection to the database. For your particular database,
 * please modify the USER and PASS constants below. The project relies on MySQL db.
 * @author SPAM team: Pavel Danek and Samuel Dinka
 */
public class Connectivity {
    private static String USER = "root";
    private static String PASS = "ics49901";
    private static Connection connection = null;
    private static boolean errorShown = false;

    /**
     * This method establishes the connection and checks the integrity of the database
     * itself, as well as of all the database tables. If a table is corrupt it deletes
     * it and creates a new one.
     * @return connection to the database
     */
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
        if (!checkTable("summary")) {
            if (!errorShown) {
                dbIsNotIntact();
                errorShown=true;
            }
            if (!createTableSummary()) {
                dbCannotBeFixed();
                System.exit(1);
            }
        }
        return connection;
    }

    /**
     * When called for the first time after starting the program, this method calls
     * the private getConnected() method, which checks the integrity of the whole
     * database. From that point on, whenever this method is called, it skips the
     * database checking, and just establishes the db connection.
     * @return a fresh database connection
     */
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

    /**
     * Displays the "Problem Connecting" error.
     */
    private static void dbProblemConnecting() {
        JOptionPane.showMessageDialog(null, "There's a problem connecting\n" +
                "to database. Attempting to fix...", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the "Database Not Intact" error.
     */
    private static void dbIsNotIntact() {
        JOptionPane.showMessageDialog(null, "The database does not seem\n"+
                "to be intact. Fixing...","Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the "Database Cannot Be Fixed" error.
     */
    private static void dbCannotBeFixed() {
        JOptionPane.showMessageDialog(null, "The database cannot be\n" +
                "fixed at this time.", "Error", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the "Connection Cannot Be Established" error.
     */
    private static void connectionCannotBeEstablished() {
        JOptionPane.showMessageDialog(null, "Connection to database\n"+
                "cannot be established.","Error", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Builds a whole new database and creates new tables.
     * @return TRUE if successful, FALSE if an error occurred
     */
    private static boolean buildDatabase() {
        try {
            if (!createDatabaseExpenseConsultant()) throw new SQLException();
            if (!createTableUsers()) throw new SQLException();
            if (!createTableTransaction()) throw new SQLException();
            if (!createTableCategory()) throw new SQLException();
            if (!createTableSummary()) throw new SQLException();
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

    /**
     * Checks the integrity of a single table in the db.
     * @param table name of the table
     * @return TRUE if it exists and is intact, FALSE if an error occurred
     */
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

    /**
     * Drops the old possible occurrance of the "expense_consultant" database
     * and creates a new one.
     * @return TRUE if successful, FALSE if an error occurred
     */
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

    /**
     * Drops the old possible occurrance of the "users" table
     * and creates a new one.
     * @return TRUE if successful, FALSE if an error occurred
     */
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
                    "email varchar(70) NOT NULL UNIQUE, password varchar(60) NOT NULL, " +
                    "question1 varchar(60), question2 varchar(60), answer1 varchar(40), " +
                    "answer2 varchar(40), created_date datetime DEFAULT CURRENT_TIMESTAMP)";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Drops the old possible occurrance of the "transaction" table
     * and creates a new one.
     * @return TRUE if successful, FALSE if an error occurred
     */
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
                    "bank_name varchar(30), account_nick varchar(50), " +
                    "user_id int, FOREIGN KEY (user_id) REFERENCES users (user_id))";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * Drops the old possible occurrance of the "category" table
     * and creates a new one.
     * @return TRUE if successful, FALSE if an error occurred
     */
    private static boolean createTableCategory() {
        int rowsAffected;
        String sql;
        PreparedStatement s;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/expense_consultant", USER, PASS);
            sql = "DROP TABLE IF EXISTS category";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
            sql = "CREATE TABLE category(category_id int NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "category_name varchar(35), user_id int, FOREIGN KEY (user_id) REFERENCES users (user_id))";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * Drops the old possible occurrance of the "summary" table
     * and creates a new one.
     * @return TRUE if successful, FALSE if an error occurred
     */
    private static boolean createTableSummary() {
        int rowsAffected;
        String sql;
        PreparedStatement s;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/expense_consultant", USER, PASS);
            sql = "DROP TABLE IF EXISTS summary";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
            sql = "CREATE TABLE summary(summary_id int NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "begin_date varchar(25), end_date varchar(25), total_out varchar(50), total_in varchar(50), " +
                    "category_totals text, account_nick varchar(50), user_id int, " +
                    "FOREIGN KEY (user_id) REFERENCES users (user_id))";
            s = connection.prepareStatement(sql);
            rowsAffected = s.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the "users" table has any inputs in it (a.k.a. if any users exist).
     * @return TRUE if there is at least 1 user in the table,
     *         FALSE if the table is empty (no users exist)
     */
    public static boolean anyUserExists() {
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

    /**
     * Stores the local MySQL server authentication credentials for future use;
     * these are only stored until the app quits.
     * @param userName local MySQL server user name
     * @param password local MySQL server password
     */
    public static void storeDBCredentials(String userName, String password) {
        USER = userName;
        PASS = password;
    }

}
