package de.farue.autocut.service;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("User has insufficient funds!");
    }
}
