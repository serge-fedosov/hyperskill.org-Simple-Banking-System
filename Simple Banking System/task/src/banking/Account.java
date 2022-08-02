package banking;

public class Account {

    private long balance;
    private int pin;

    public Account(long balance, int pin) {
        this.balance = balance;
        this.pin = pin;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
}
