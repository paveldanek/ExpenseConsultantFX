package authentication;

import crypto.AESUtil;
import db_connectors.Connectivity;
import entities.Transaction;
import main_logic.PEC;
import main_logic.Request;

import java.sql.*;

/**
 * The Authentication class contains everything that has to do with user account
 * program logic. Sign-up, login, password retrieval, password change, and account
 * close.
 * @author SPAM team: Pavel Danek and Samuel Dinka
 */
public class Authentication {

    private static Authentication singleton = null;

    /**
     * Private constructor.
     */
    private Authentication() {}

    /**
     * Instance creator.
     *
     * @return an instance of Account
     */
    public static Authentication instance() {
        if (singleton == null) {
            singleton = new Authentication();
        }
        return singleton;
    }

    /**
     * Logs the user in.
     * @param r Request variable containing all necessary fields
     * @return user id of the newly logged-in user
     * @throws SQLException
     */
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
            PEC.instance().setCurrentUserID(userId);
            PEC.instance().setCurrentUserPass(cipheredPassword);
            PEC.instance().initialDBaseDownload();
            return userId;
        } else {
            return -1;
        }
    }

    /**
     * Signs a new user up. Stores user name, user ID, password, 2 security
     * questions, and corresponding answers--all in an encrypted form. If the
     * user is properly signed up, it also loggs them in.
     * @param r Request variable containing all necessary fields
     * @return a checkcode communicating the result of the sign-up
     * @throws SQLException
     */
    public int signup(Request r) throws SQLException {
        int checkCode = 0; // 0 = no code
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
            checkCode = 1; // 1 = not an email address
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
                        checkCode = 4; // 4 = confirm password does not match the first password entry
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
                            checkCode = 6; // 6 = SQL error occurred
                        }
                        // Execute the query and check the number of rows affected
                        if (rowsAffected > 0 && checkCode==0 ) {
                            // added the call of login for further initialization,
                            // which can be moved somewhere else
                            int userID = login(r);
                            checkCode = 5; // 5 = sign-up successful
                        } else {
                            checkCode = 6; // 6 = SQL error occurred
                        }
                        // Close the connection and statement
                        stmt.close();
                        conn.close();
                    }
                } else {
                    checkCode = 3; // 3 = incorrect length of password
                }
            } else {
                checkCode = 2; // 2 = the email is in the database already
            }
        }
        return checkCode;
    }

    /**
     * Retrieves a forgotten password.
     * @param r Request variable containing all necessary fields; the retrieved
     *          password is sent back through this variable in case of a successful
     *          retrieval as well
     * @return a code communicating the result of the retrieval
     * @throws SQLException
     */
    public int retrievePassword(Request r) throws SQLException{
        Connection connection = Connectivity.getConnection();
        String query = "SELECT password, question1, question2, answer1, answer2 FROM users WHERE email = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String q1 = "", q2 = "", a1 = "", a2 = "";
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, AESUtil.encryptItem(r.getEmail()));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                q1 = AESUtil.decryptItem(resultSet.getString("question1"));
                q2 = AESUtil.decryptItem(resultSet.getString("question2"));
                a1 = AESUtil.decryptItem(resultSet.getString("answer1"));
                a2 = AESUtil.decryptItem(resultSet.getString("answer2"));
                r.setPass1(resultSet.getString("password"));
            } else {
                return 1; // 1 = unrecognized email
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if ((q1.compareToIgnoreCase(r.getQuestion1())==0 && q2.compareToIgnoreCase(r.getQuestion2())==0
                && a1.compareToIgnoreCase(r.getAnswer1())==0 && a2.compareToIgnoreCase(r.getAnswer2())==0) ||
                (q1.compareToIgnoreCase(r.getQuestion2())==0 && q2.compareToIgnoreCase(r.getQuestion1())==0
                && a1.compareToIgnoreCase(r.getAnswer2())==0 && a2.compareToIgnoreCase(r.getAnswer1())==0))
            return 3; // 3 = retrieval successful, password can be accessed from r.getPass1()
                      // (recommended deleting the password from the Request.pass1 right after use)
        else {
            r.setPass1("");
            return 2; // 2 = security Q&A don't match
        }
    }

    /**
     * Changes the current user's password. Since all transaction and summary
     * entries are encoded using the user's password, this method not only changes
     * and stores the new credentials, but also pulls all relevant entries from the
     * database, "re-crypts" them with the new password, and uploads them back.
     * @param r Request variable containing all necessary fields
     * @return a code communicating the result of the password change
     * @throws SQLException
     */
    public int passwordChange(Request r) throws SQLException{
        if (!r.getPass1().equals(r.getPass2())) return 1; // 1 = the new password and its confirmation don't match
        if (r.getPass1().length()<8 || r.getPass1().length()>=20) return 2; // 2 = incorrect length of password
        Connection connection = Connectivity.getConnection();
        String query = "SELECT password FROM users WHERE email = ? AND user_id = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String oldPass = "";
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, AESUtil.encryptItem(r.getEmail()));
            statement.setInt(2, PEC.instance().getCurrentUserID());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                oldPass = resultSet.getString("password");
            } else {
                return 3; // 3 = unrecognized email
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (!r.getOldPass().equals(AESUtil.decryptItem(oldPass)))
            return 4; // 4 = current password wrong

        PEC.instance().setCurrentUserPass(AESUtil.encryptItem(r.getPass1()));
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        statement = null;
        int rowsAffected = 0;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, AESUtil.encryptItem(r.getPass1()));
            statement.setString(2, AESUtil.encryptItem(r.getEmail()));
            rowsAffected = statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        query = "SELECT summary_id, category_totals FROM summary WHERE user_id = ?";
        sql = "UPDATE summary SET category_totals = ? WHERE summary_id = ?";
        PreparedStatement statement1 = null, statement2 = null;
        resultSet = null; rowsAffected = 0;
        try {
            statement1 = connection.prepareStatement(query);
            statement1.setInt(1, PEC.instance().getCurrentUserID());
            resultSet = statement1.executeQuery();
            while (resultSet.next()) {
                String plainText = AESUtil.decryptStringTable(
                    resultSet.getString("category_totals"), r.getOldPass());
                String encryptedText = AESUtil.encryptStringTable(plainText, r.getPass1());
                int summaryID = resultSet.getInt("summary_id");
                statement2 = connection.prepareStatement(sql);
                statement2.setString(1, encryptedText);
                statement2.setInt(2, summaryID);
                rowsAffected = statement2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        query = "SELECT transaction_id, transaction_history FROM transaction WHERE user_id = ?";
        sql = "UPDATE transaction SET transaction_history = ? WHERE transaction_id = ?";
        statement1 = null; statement2 = null;
        resultSet = null; rowsAffected = 0;
        try {
            statement1 = connection.prepareStatement(query);
            statement1.setInt(1, PEC.instance().getCurrentUserID());
            resultSet = statement1.executeQuery();
            while (resultSet.next()) {
                String plainText = AESUtil.decryptStringTable(
                        resultSet.getString("transaction_history"), r.getOldPass());
                String encryptedText = AESUtil.encryptStringTable(plainText, r.getPass1());
                int transactionID = resultSet.getInt("transaction_id");
                statement2 = connection.prepareStatement(sql);
                statement2.setString(1, encryptedText);
                statement2.setInt(2, transactionID);
                rowsAffected = statement2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 5; // 5 = password change successful
    }

    /**
     * Performs the security check on input email and password during closing
     * of a PEC account (NOT a bank account).
     * @param r Request variable containing all necessary fields
     * @return a code communicating the result of the security check before closing
     * @throws SQLException
     */
    public int closeAccountDialog(Request r) throws SQLException{
        Connection connection = Connectivity.getConnection();
        String query = "SELECT password FROM users WHERE email = ? AND user_id = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String pass = "";
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, AESUtil.encryptItem(r.getEmail()));
            statement.setInt(2, PEC.instance().getCurrentUserID());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                pass = resultSet.getString("password");
            } else {
                return 1; // 1 = unrecognized email
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (!AESUtil.decryptItem(pass).equals(r.getPass1())) return 2; // 2 = incorrect password
        return 3; // 3 = close successful
    }

    /**
     * Irreversibly closes the current user's PEC account (NOT a bank account)
     * and deletes all associated database entries along with it.
     * @throws SQLException
     */
    public void closeAccount() throws SQLException{
        Connection connection = Connectivity.getConnection();
        int id = PEC.instance().getCurrentUserID();
        String transaction = "DELETE FROM transaction WHERE user_id = ?";
        String summary = "DELETE FROM summary WHERE user_id = ?";
        String category = "DELETE FROM category WHERE user_id = ?";
        String users = "DELETE FROM users WHERE user_id = ?";
        try {
            PreparedStatement s = connection.prepareStatement(transaction);
            s.setInt(1, id);
            int rowsAffected = s.executeUpdate();

            s = connection.prepareStatement(summary);
            s.setInt(1, id);
            rowsAffected = s.executeUpdate();

            s = connection.prepareStatement(category);
            s.setInt(1, id);
            rowsAffected = s.executeUpdate();

            s = connection.prepareStatement(users);
            s.setInt(1, id);
            rowsAffected = s.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
