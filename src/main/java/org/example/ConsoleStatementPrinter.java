package org.example;

public class ConsoleStatementPrinter implements StatementPrinter {
    @Override
    public void print(String text) {
        System.out.println(text);
    }
}