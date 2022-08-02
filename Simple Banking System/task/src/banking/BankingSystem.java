package banking;

import java.util.*;



public class BankingSystem {

    Scanner scanner = new Scanner(System.in);
    HashMap<Long, Account> accounts = new HashMap<>();
    long currentAccount = 0;

    private void createAccount() {

        final String MESSAGE = "\nYour card has been created\n" +
                "Your card number:\n" +
                "%s\n" +
                "Your card PIN:\n" +
                "%04d\n\n";

        long maxAccount = 0;
        if (accounts.size() == 0) {
            maxAccount = 4000000000000000L;
        } else {
            for (var elem : accounts.entrySet()) {
                maxAccount = elem.getKey() > maxAccount ? elem.getKey() : maxAccount;
            }
        }

        long newNumber = Long.parseLong(String.valueOf(maxAccount).substring(6, 15));
        newNumber++;
        String newNumberStr = String.format("400000%09d0", newNumber);

        int pin = new Random().nextInt(10000);
        accounts.put(Long.parseLong(newNumberStr), new Account(0, pin));

        System.out.printf(MESSAGE, newNumberStr, pin);

    }

    private int logIntoAccount() {

        System.out.println("\nEnter your card number:");
        long account = scanner.nextLong();
        System.out.println("Enter your PIN:");
        int pin = scanner.nextInt();

        if (accounts.containsKey(account) && accounts.get(account).getPin() == pin) {
            System.out.println("\nYou have successfully logged in!\n");
            currentAccount = account;

            return loggedMenu();
        } else {
            System.out.println("\nWrong card number or PIN!\n\n");
        }

        return 1;
    }

    private void printBalance() {
        System.out.printf("\nBalance: %d\n\n", accounts.get(currentAccount).getBalance());

    }

    public int loggedMenu() {

        final String LOGGED_MENU = "1. Balance\n" +
                "2. Log out\n" +
                "0. Exit";

        System.out.println(LOGGED_MENU);
        int n = scanner.nextInt();
        do {

            switch (n) {
                case 1:
                    printBalance();
                    break;
                case 2:
                    currentAccount = 0;
                    System.out.println("\nYou have successfully logged out!\n");
                    return 1;
                default:
                    break;
            }

            if (n == 0) {
                return 0;
            } else {
                System.out.println(LOGGED_MENU);
                n = scanner.nextInt();
            }

        } while (n != 0);

        return 0;
    }

    public void mainMenu() {

        final String MAIN_MENU = "1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit";

        System.out.println(MAIN_MENU);
        int n = scanner.nextInt();
        do {

            switch (n) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    n = logIntoAccount();
                    break;
                default:
                    break;
            }

            if (n != 0) {
                System.out.println(MAIN_MENU);
                n = scanner.nextInt();
            }

        } while (n != 0);

        System.out.println("\nBye!");

    }
}
