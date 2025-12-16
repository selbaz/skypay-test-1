package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Account implements AccountService {

    private final List<Transaction> transactions = new ArrayList<>();
    private final DateProvider dateProvider;
    private final StatementPrinter printer;
    private int currentBalance = 0;

    private final Object lock = new Object();

    public Account(DateProvider dateProvider, StatementPrinter printer) {
        this.dateProvider = dateProvider;
        this.printer = printer;
    }

    @Override
    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant du depot doit etre positif");
        }

        LocalDate date = dateProvider.today();
        Transaction transaction = new Transaction(amount, date);

        synchronized (lock) {
            try {
                currentBalance = Math.addExact(currentBalance, amount);
            } catch (ArithmeticException e) {
                throw new IllegalStateException("Le solde depasse la capacité maximale autorisee");
            }
            transactions.add(transaction);
        }
    }

    @Override
    public void withdraw(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit etre positif");
        }

        LocalDate date = dateProvider.today();
        Transaction transaction = new Transaction(-amount, date);

        synchronized (lock) {
            if (currentBalance < amount) {
                throw new IllegalStateException("Solde insuffisant");
            }

            transactions.add(transaction);
            currentBalance -= amount;
        }
    }

    @Override
    public void printStatement() {

        List<Transaction> snapshot;
        synchronized (lock) {
            snapshot = new ArrayList<>(transactions);
        }

        if (snapshot.isEmpty()) {
            printer.print("Date || Amount || Balance");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int runningBalance = 0;

        StringBuilder fullStatement = new StringBuilder(30 + snapshot.size() * 50);
        fullStatement.append("Date || Amount || Balance");

        StringBuilder lineBuilder = new StringBuilder(50);
        List<String> linesToPrint = new ArrayList<>(snapshot.size());

        for (Transaction t : snapshot) {
            runningBalance += t.getAmount();
            lineBuilder.setLength(0);
            lineBuilder.append(formatter.format(t.getDate()))
                    .append(" || ")
                    .append(t.getAmount())
                    .append(" || ")
                    .append(runningBalance);

            linesToPrint.add(lineBuilder.toString());
        }

        for (int i = linesToPrint.size() - 1; i >= 0; i--) {
            fullStatement.append("\n").append(linesToPrint.get(i));
        }

        printer.print(fullStatement.toString());
    }
}
