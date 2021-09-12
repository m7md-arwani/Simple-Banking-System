package banking;

import java.util.*;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    // key = account number, value = list of pin and balance
    public static Map<String, List<Integer>> accounts = new HashMap<>();
    public static List<Integer> data = new ArrayList<>(); // current opened accounts

    public static void main(String[] args) {
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
        // Storing the data of the new account in a hashmap.
        accounts.put(completeCardNumber, new ArrayList<>(List.of(pin, 0)));


    }

    public static void login() {
        System.out.println("Enter your card number:");
        String card = scanner.nextLine();
        System.out.println("Enter your PIN:");
        int pin = scanner.nextInt();
        scanner.nextLine();

        if (accounts.get(card) == null) {
            System.out.println("Account number does not exist");
        } else {
            data.addAll(accounts.get(card));
            if (pin == data.get(0)) {
                System.out.println("You have successfully logged in!");
                state.currentState = state.State.LOGGED_IN;
            }
        }
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


}



