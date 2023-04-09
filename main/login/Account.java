package login;

import crypto.AESUtil;
import db_connectors.Connectivity;
import main_logic.PEC;
import main_logic.Request;

import javax.swing.*;
import java.sql.*;

public class Account {

    private static Account singleton = null;

    /**
     * Private constructor.
     */
    private Account() {}

    /**
     * Instance creator.
     *
     * @return an instance of Account
     */
    public static Account instance() {
        if (singleton == null) {
            singleton = new Account();
        }
        return singleton;
    }

    public int login(Request r) throws SQLException {
        int userId = -1;
        boolean result = false;
        Connection connection = Connectivity.getConnection();
        String query = "SELECT user_id FROM users WHERE email = ? AND password = ?";
        PreparedStatement statement = null;
        String cipheredPassword;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(query);
            try {
                cipheredPassword = AESUtil.encryptItem(r.getPass1());
                statement.setString(1, AESUtil.encryptItem(r.getEmail()));
                statement.setString(2, cipheredPassword);
                resultSet = statement.executeQuery();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
                result = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (result) {
            return userId;
        } else {
            return -1;
        }
    }

    public int retrievePassword(Request r) throws SQLException{
        Connection connection = Connectivity.getConnection();
        String query = "SELECT password, question1, question2, answer1, answer2 FROM users WHERE email = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String q1 = "", q2 = "", a1 = "", a2 = "";
        try {
            statement = connection.prepareStatement(query);
            try {
                statement.setString(1, AESUtil.encryptItem(r.getEmail()));
                resultSet = statement.executeQuery();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (resultSet.next()) {
                q1 = AESUtil.decryptItem(resultSet.getString("question1"));
                q2 = AESUtil.decryptItem(resultSet.getString("question2"));
                a1 = AESUtil.decryptItem(resultSet.getString("answer1"));
                a2 = AESUtil.decryptItem(resultSet.getString("answer2"));
                r.setPass1(resultSet.getString("password"));
            } else {
                return 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (q1.compareToIgnoreCase(r.getQuestion1())==0 && q2.compareToIgnoreCase(r.getQuestion2())==0
        && a1.compareToIgnoreCase(r.getAnswer1())==0 && a2.compareToIgnoreCase(r.getAnswer2())==0) return 3;
        else {
            r.setPass1("");
            return 2;
        }
    }

    public int signup(Request r) throws SQLException {
        int checkCode = 0;
        // Connect to the database
        Connection conn = Connectivity.getConnection();
        String email = r.getEmail();
        String pass1 = r.getPass1();
        String pass2 = r.getPass2();
        String question1 = r.getQuestion1();
        String question2 = r.getQuestion2();
        String answer1 = r.getAnswer1();
        String answer2 = r.getAnswer2();
        // Check if the email is in the right format
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            checkCode = 1;
        } else {
            ResultSet rs = null;
            try {
                String checkSql = "SELECT COUNT(*) FROM users WHERE email=?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, AESUtil.encryptItem(email));
                // Execute the query to check if the user already exists
                rs = checkStmt.executeQuery();
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
                if (pass1.length() >= 8 && pass1.length() < 20) {
                    if (!pass1.equals(pass2)) {
                        checkCode = 4;
                    } else {
                        // Create a PreparedStatement to insert a new user
                        String sql = "INSERT INTO users (email, password, question1, question2, answer1, answer2, "+
                                "created_date) VALUES ( ?,?,?,?,?,?,now())";
                        int rowsAffected = 0;
                        PreparedStatement stmt = null;
                        try {
                            stmt = conn.prepareStatement(sql);
                            stmt.setString(1, AESUtil.encryptItem(email));
                            stmt.setString(2, AESUtil.encryptItem(pass1));
                            stmt.setString(3, AESUtil.encryptItem(question1));
                            stmt.setString(4, AESUtil.encryptItem(question2));
                            stmt.setString(5, AESUtil.encryptItem(answer1));
                            stmt.setString(6, AESUtil.encryptItem(answer2));
                            rowsAffected = stmt.executeUpdate();
                        } catch (SQLIntegrityConstraintViolationException e) {
                            checkCode = 6;
                        }
                        // Execute the query and check the number of rows affected
                        if (rowsAffected > 0 && checkCode==0 ) {
                            // added the call of login for further initialization,
                            // which can be moved somewhere else
                            int userID = login(r);
                            PEC.instance().finishLogin(userID);
                            checkCode = 5;
                        } else {
                            checkCode = 6;
                        }
                        // Close the connection and statement
                        stmt.close();
                        conn.close();
                    }
                } else {
                    checkCode = 3;
                }
            } else {
                checkCode = 2;
            }
        }
        return checkCode;
    }
}
