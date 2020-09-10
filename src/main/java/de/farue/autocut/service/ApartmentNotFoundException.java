package de.farue.autocut.service;

public class ApartmentNotFoundException extends RuntimeException {

    public ApartmentNotFoundException(String apartment) {
        super("Apartment " + apartment + " not found!");
    }
}
