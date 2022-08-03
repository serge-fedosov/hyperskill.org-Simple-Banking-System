package banking;

public class Main {
    public static void main(String[] args) {
        String file = null;
        for (int i = 0; i < args.length; i++) {
            if ("-fileName".equals(args[i]) && i + 1 < args.length) {
                file = args[i + 1];
            }
        }

        //file = "test0105.db";
        BankingSystem bankingSystem = new BankingSystem(file);
        bankingSystem.mainMenu();
    }
}