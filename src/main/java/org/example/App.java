package org.example;

public class App {
    public static void main(String[] args) {
        StatementPrinter printer = new ConsoleStatementPrinter();
        AccountService account = new Account(new RealDateProvider(), printer);

        account.deposit(1000);
        account.withdraw(200);
        account.printStatement();
    }
}