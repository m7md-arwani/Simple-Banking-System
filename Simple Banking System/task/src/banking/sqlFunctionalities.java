package banking;

import java.sql.*;
import java.util.Scanner;

public class sqlFunctionalities {
    private static final Scanner scanner = new Scanner(System.in);

    public static void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS card (\n" +
                "id INTEGER PRIMARY KEY," +
                "number TEXT NOT NULL," +
                "pin TEXT NOT NULL," +
                "balance INTEGER DEFAULT 0" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // Establishing a connection with the database.
    public static Connection connect() {
        // SQLite connection string
        String url = Main.dbName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void insert(String numOfCard, String pin) {
        String sql = "INSERT INTO card(number,pin) VALUES(?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numOfCard);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addIncome() {

        System.out.println("Enter income:");
        int income = scanner.nextInt();
        scanner.nextLine();

        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setInt(1, income);
            pstmt.setString(2, Main.accountCard);
            //
            pstmt.executeUpdate();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Income was added!");

    }

    public static int getBalance() {
        String sql = "SELECT balance FROM card WHERE number = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, Main.accountCard);

            ResultSet set = pstmt.executeQuery();

            if (set.next()) {
                return set.getInt("balance");
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;

    }

    public static void transferBalance() {
        System.out.println("Transfer");
        System.out.println("Enter card number:");

        String cardNum = scanner.nextLine();
        if (cardNum.equals(Main.accountCard)) {
            System.out.println("You can't transfer money to the same account!");

        } else if (Main.lunhAlgo(cardNum.substring(0, 15)) != Character.getNumericValue(cardNum.charAt(15))) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");

        } else if (checkIfExist(cardNum)) {
            System.out.println("Enter how much money you want to transfer:");
            int amount = scanner.nextInt();
            scanner.nextLine();
            if (amount > getBalance()) {
                System.out.println("Not enough money!");
            } else {
                transferMechanism(Main.accountCard, cardNum, amount);

            }

        } else {
            System.out.println("Such a card does not exist.");
        }


    }


    public static boolean checkIfExist(String cardNum) {

        String sql = "SELECT number FROM card WHERE number = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, cardNum);

            ResultSet set = pstmt.executeQuery();

            if (set.next()) {
                return set.getString("number").equals(cardNum);
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;

    }

    public static void transferMechanism(String sender_account, String receiver_account, int amount) {
        //withdraw from sender
        String sql1 = "Update card SET balance = balance - ? WHERE number = ?";
        // adding money to the receiver's account
        String sql2 = "Update card SET balance = balance + ? WHERE number = ?";


        try (Connection conn = DriverManager.getConnection(Main.dbName)) {
            conn.setAutoCommit(false);
            try (PreparedStatement withdraw = conn.prepareStatement(sql1);
                 PreparedStatement add = conn.prepareStatement(sql2)) {

                // Create a savepoint
                //Savepoint savepoint = conn.setSavepoint();


                // withdrawing process
                withdraw.setInt(1, amount);
                withdraw.setString(2, sender_account);
                withdraw.executeUpdate();

                // adding process
                add.setInt(1, amount);
                add.setString(2, receiver_account);
                add.executeUpdate();

                conn.commit();


            } catch (SQLException e) {
                conn.rollback();
                System.out.println(e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Success!");


    }

    public static void close_account() {

        String sql = "DELETE FROM card WHERE number = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, Main.accountCard);
            //close it
            pstmt.executeUpdate();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("The account has been closed!");


    }

}
