package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class BankingSystem {

    String file;
    Scanner scanner = new Scanner(System.in);
    long currentAccount = 0;

    public BankingSystem(String file) {
        this.file = file;

        String url = "jdbc:sqlite:" + file;

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            if (con.isValid(5)) {
                //System.out.println("Connection is valid.");
                try (Statement statement = con.createStatement()) {
                    statement.execute("CREATE TABLE IF NOT EXISTS card (\n" +
                            "    id      INTEGER,\n" +
                            "    number  TEXT    NOT NULL,\n" +
                            "    pin     TEXT    NOT NULL,\n" +
                            "    balance INTEGER DEFAULT 0\n" +
                            ");");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private long[] getMaxAccount() {
        long maxAccount = 0;
        int maxId = 0;

        String url = "jdbc:sqlite:" + file;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet rs = statement.executeQuery("SELECT max(number), max(id) FROM card;")) {
                    rs.next();
                    maxAccount = rs.getLong(1);
                    maxId = rs.getInt(2);
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        if (maxAccount == 0) {
            maxAccount = 4000000000000000L;
        }

        long[] result = new long[] {maxAccount, maxId};
        return result;
    }

    private boolean isCorrectLogin(long account, String pin) {
        String url = "jdbc:sqlite:" + file;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet rs = statement.executeQuery("SELECT balance FROM card WHERE number = '" + account + "' AND pin = '" + pin + "';")) {
                    if (rs.next()) {
                        return true;
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        return false;
    }

    private void createAccount() {

        final String MESSAGE = "\nYour card has been created\n" +
                "Your card number:\n" +
                "%s\n" +
                "Your card PIN:\n" +
                "%s\n\n";

        long[] ma = getMaxAccount();
        long maxAccount = ma[0];
        int maxId = (int) ma[1];
        maxId++;

        long newNumber = Long.parseLong(String.valueOf(maxAccount).substring(6, 15));
        newNumber++;
        String newNumberStr = String.format("400000%09d0", newNumber);

        int sum = 0;
        for (int i = 1; i < 16; i++) {
            int k = (int) (newNumberStr.charAt(i - 1) - '0');
            if (i % 2 != 0) k *= 2;
            if (k > 9) k -= 9;
            sum += k;
        }
        int crc = (10 - sum % 10) % 10;

        newNumberStr = String.format("400000%09d%d", newNumber, crc);
        newNumber = Long.parseLong(newNumberStr);

        String pin = String.format("%04d", new Random().nextInt(10000));

        String url = "jdbc:sqlite:" + file;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.execute("INSERT INTO card (id, number, pin, balance)\n" +
                        "VALUES (" + maxId + ", '" + newNumber + "', '" + pin + "', 0);");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        System.out.printf(MESSAGE, newNumberStr, pin);

    }

    private int logIntoAccount() {

        System.out.println("\nEnter your card number:");
        long account = Long.parseLong(scanner.nextLine().trim());
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine().trim();

        if (isCorrectLogin(account, pin)) {
            System.out.println("\nYou have successfully logged in!\n");
            currentAccount = account;

            return loggedMenu();
        } else {
            System.out.println("\nWrong card number or PIN!\n\n");
        }

        return 1;
    }

    private void printBalance() {
        String url = "jdbc:sqlite:" + file;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet rs = statement.executeQuery("SELECT balance FROM card WHERE number = '" + currentAccount + "';")) {
                    rs.next();
                    System.out.printf("\nBalance: %d\n\n", rs.getLong(1));
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void closeAccount() {
        String url = "jdbc:sqlite:" + file;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.execute("DELETE FROM card WHERE number = '" + currentAccount + "';");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void addIncome() {

        System.out.println("\nEnter income:");
        long income = Long.parseLong(scanner.nextLine().trim());

        String url = "jdbc:sqlite:" + file;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.execute("UPDATE card SET balance = balance + " + income + " WHERE number = '" + currentAccount + "';");
                System.out.println("Income was added!\n");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void doTransfer() {

        System.out.println("\nTransfer\n" +
                "Enter card number:");

        long toAccount = Long.parseLong(scanner.nextLine().trim());
        String toAccountStr = String.valueOf(toAccount);

        int sum = 0;
        for (int i = 1; i < 16; i++) {
            int k = (int) (toAccountStr.charAt(i - 1) - '0');
            if (i % 2 != 0) k *= 2;
            if (k > 9) k -= 9;
            sum += k;
        }
        int crc = (int) (toAccountStr.charAt(15) - '0');
        sum += crc;

        if (sum % 10 != 0) {
            System.out.println("Probably you made a mistake in the card number. Please try again!\n");
            return;
        }

        if (toAccountStr.equals(String.valueOf(currentAccount))) {
            System.out.println("You can't transfer money to the same account!\n");
            return;
        }

        String url = "jdbc:sqlite:" + file;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet rs = statement.executeQuery("SELECT balance FROM card WHERE number = '" + toAccount + "';")) {
                    if (!rs.next()) {
                        System.out.println("Such a card does not exist.");
                        return;
                    }
                }

                System.out.println("Enter how much money you want to transfer:");
                long transfer = Long.parseLong(scanner.nextLine().trim());

                try (ResultSet rs = statement.executeQuery("SELECT balance FROM card WHERE number = '" + currentAccount + "';")) {
                    rs.next();
                    if (transfer > rs.getLong(1)) {
                        System.out.println("Not enough money!\n");
                        return;
                    }
                }

                statement.execute("UPDATE card SET balance = balance - " + transfer + " WHERE number = '" + currentAccount + "';");
                statement.execute("UPDATE card SET balance = balance + " + transfer + " WHERE number = '" + toAccount + "';");
                System.out.println("Success!\n");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public int loggedMenu() {

        final String LOGGED_MENU = "1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit";

        System.out.println(LOGGED_MENU);
        int n = Integer.parseInt(scanner.nextLine().trim());
        do {

            switch (n) {
                case 1:
                    printBalance();
                    break;
                case 2:
                    addIncome();
                    break;
                case 3:
                    doTransfer();
                    break;
                case 4:
                    closeAccount();
                    currentAccount = 0;
                    System.out.println("\nThe account has been closed!\n");
                    return 1;
                case 5:
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
                n = Integer.parseInt(scanner.nextLine().trim());
            }

        } while (n != 0);

        return 0;
    }

    public void mainMenu() {

        final String MAIN_MENU = "1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit";

        System.out.println(MAIN_MENU);
        int n = Integer.parseInt(scanner.nextLine().trim());
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
                n = Integer.parseInt(scanner.nextLine().trim());
            }

        } while (n != 0);

        System.out.println("\nBye!");

    }
}
