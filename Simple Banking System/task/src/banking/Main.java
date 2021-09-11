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
        int n = 1000000000 + random.nextInt(9000000);
        String last10Digit = Integer.toString(n);
        String accountNumber = bin + last10Digit;
        int pin = 1000 + random.nextInt(9000);

        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(accountNumber);
        System.out.println("Your card PIN:");
        System.out.println(pin);
        // Storing the data of the new account in a hashmap.
        accounts.put(accountNumber, new ArrayList<>(List.of(pin, 0)));


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


}



