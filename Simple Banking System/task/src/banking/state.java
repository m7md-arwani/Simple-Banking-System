package banking;

import java.util.Scanner;

public class state {
    public static Scanner scanner = new Scanner(System.in);
    public static boolean isRunning = true;
    public static State currentState = State.SHOW_MENU;

    enum State {
        CREATE_AN_ACCOUNT, LOG_INTO_ACCOUNT, SHOW_MENU, LOGGED_IN
    }

    public static void start() {
        while (isRunning) {
            operationManager(currentState);
        }
    }

    public static void operationManager(State state) {
        switch (state) {
            case SHOW_MENU:
                System.out.println("1. Create an account\n2. Log into account\n0. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        currentState = State.CREATE_AN_ACCOUNT;
                        break;
                    case 2:
                        currentState = State.LOG_INTO_ACCOUNT;
                        break;
                    case 0:
                        System.out.println("Bye!");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Wrong number");


                }
                break;
            case CREATE_AN_ACCOUNT:
                currentState = State.SHOW_MENU;
                Main.creteNewAccount();
                break;
            case LOG_INTO_ACCOUNT:
                currentState = State.SHOW_MENU;
                Main.login();
                break;
            case LOGGED_IN:
                System.out.println("1. Balance\n2. Log out\n0. Exit");
                int option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 1:
                        System.out.print("Balance: ");
                        System.out.println(Main.balance);
                        break;
                    case 2:
                        Main.balance = 0; // remove the cache
                        currentState = State.SHOW_MENU;
                        System.out.println("You have successfully logged out!");
                        break;
                    case 0:
                        isRunning = false;
                        break;
                    default:
                        System.out.println("wrong number");
                        break;
                }
        }
    }


}
