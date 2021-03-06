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
            sqlFunctionalities.createNewTable();
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
        sqlFunctionalities.insert(completeCardNumber, String.valueOf(pin));
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


    public static void login() {
        System.out.println("Enter your card number:");
        String card = scanner.nextLine();
        System.out.println("Enter your PIN:");
        int pin = scanner.nextInt();
        scanner.nextLine();
//        selectForLogin(card,String.valueOf(pin));

        String sql = "SELECT number, pin, balance FROM card\n" +
                "WHERE number = ? AND pin = ?";

        try (Connection conn = sqlFunctionalities.connect();
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


}








