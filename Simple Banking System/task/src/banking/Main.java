package banking;


import java.sql.*;
import java.util.*;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    public static String accountCard; // card's number of current opened account.
    public static String dbName; // Database location.

    public static void main(String[] args) {
        if (args[0].equals("-fileName")) {
            dbName = "jdbc:sqlite:" + args[1];
            // The table will be created if it not exists.
            createNewTable();
        }
        state.start();


    }

    public static void creteNewAccount() {
        Random random = new Random();
        String bin = "400000";
        int n = 100000000 + random.nextInt(9000000);
        String last9Digit = Integer.toString(n);
        String accountNumber = bin + last9Digit;
        int checksumDigit = lunhAlgo(accountNumber);
        if (checksumDigit == -1) {
            System.out.println("Error");
        }
        String checks = Integer.toString(checksumDigit);
        String completeCardNumber = bin + last9Digit + checks;
        int pin = 1000 + random.nextInt(9000);

        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(completeCardNumber);
        System.out.println("Your card PIN:");
        System.out.println(pin);
        // Storing the new account in the database.
        insert(completeCardNumber, String.valueOf(pin));
    }


    public static int lunhAlgo(String num) {
        char[] cardNum = num.toCharArray();
        List<Integer> cardDigits = new ArrayList<>();
        // Transferring card Numbers from a String to digits to deal with.
        for (char s : cardNum) {
            cardDigits.add(Character.getNumericValue(s));
        }


        // Assuming index starts with 1, if the index is odd multiply the digit by 2.
        for (int j = 0; j < cardDigits.size(); j += 2) {

            cardDigits.set(j, cardDigits.get(j) * 2);

        }

        // If a digit is bigger than 9, then subtract 9.
        for (int z = 0; z < cardDigits.size(); z++) {
            if (cardDigits.get(z) > 9) {
                cardDigits.set(z, cardDigits.get(z) - 9);
            }
        }
        int sum = 0;
        // Get the sum of all digits.
        for (Integer cardDigit : cardDigits) {
            sum += cardDigit;
        }


        for (int x = 0; x <= 9; x++) {
            if ((x + sum) % 10 == 0) {
                return x;
            }
        }
        return -1;


    }

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
    private static Connection connect() {
        // SQLite connection string
        String url = dbName;
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


    public static void login() {
        System.out.println("Enter your card number:");
        String card = scanner.nextLine();
        System.out.println("Enter your PIN:");
        int pin = scanner.nextInt();
        scanner.nextLine();
//        selectForLogin(card,String.valueOf(pin));

        String sql = "SELECT number, pin, balance FROM card\n" +
                "WHERE number = ? AND pin = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, card);
            pstmt.setString(2, String.valueOf(pin));
            //
            ResultSet rs = pstmt.executeQuery();
            // store the result set
            if (rs.next()) {
                String pinFromUser = String.valueOf(pin);
                String cardFromdata = rs.getString("number");
                String pinFromData = rs.getString("pin");
                // getting the card number out of the try catch block.
                getCardNum(cardFromdata);
                if (cardFromdata.equals(card) && pinFromData.equals(pinFromUser)) {
                    System.out.println("You have successfully logged in!");
                    state.currentState = state.State.LOGGED_IN;


                } else {
                    System.out.println("Wrong card number or PIN!");
                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    // getting some info out of try-catch block.
    public static void getCardNum(String num) {
        accountCard = num;
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
            pstmt.setString(2, accountCard);
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
            pstmt.setString(1, accountCard);

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
        if (cardNum.equals(accountCard)) {
            System.out.println("You can't transfer money to the same account!");

        } else if (lunhAlgo(cardNum.substring(0, 15)) != Character.getNumericValue(cardNum.charAt(15))) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");

        } else if (checkIfExist(cardNum)) {
            System.out.println("Enter how much money you want to transfer:");
            int amount = scanner.nextInt();
            scanner.nextLine();
            if (amount > getBalance()) {
                System.out.println("Not enough money!");
            } else {
                transferMechanism(accountCard, cardNum, amount);

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


        try (Connection conn = DriverManager.getConnection(dbName)) {
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
            pstmt.setString(1, accountCard);
            //close it
            pstmt.executeUpdate();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("The account has been closed!");


    }


}








