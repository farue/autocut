package de.farue.autocut.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;

public class StwApartmentParser {

    private static final String STREET_NUMBER_GROUP = "streetno";
    private static final String APARTMENT_TYPE_GROUP = "type";
    private static final String APARTMENT_NUMBER_GROUP = "apartmentnr";

    public static final String APARTMENT_REGEX =
        "(?<" + STREET_NUMBER_GROUP + ">\\d)(?<" + APARTMENT_TYPE_GROUP + ">\\d)-(?<" + APARTMENT_NUMBER_GROUP + ">\\d{2})";

    private static final Pattern STW_APARTMENT_PATTERN = Pattern.compile(APARTMENT_REGEX);

    public static StwApartment parseApartmentString(String apartment) {
        Matcher matcher = STW_APARTMENT_PATTERN.matcher(apartment);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Malformed apartment nr: " + apartment);
        }

        int streetNrIdentifier = Integer.parseInt(matcher.group(STREET_NUMBER_GROUP));
        int apartmentTypeIdentifier = Integer.parseInt(matcher.group(APARTMENT_TYPE_GROUP));
        int apartmentNr = Integer.parseInt(matcher.group(APARTMENT_NUMBER_GROUP));
        StwApartment stwApartment = new StwApartment();
        stwApartment.setStreetNoIdentifier(streetNrIdentifier);
        stwApartment.setApartmentTypeIdentifier(apartmentTypeIdentifier);
        stwApartment.setApartmentNr(apartmentNr);
        return stwApartment;
    }

    @Data
    public static class StwApartment {

        private int streetNoIdentifier;
        private int apartmentTypeIdentifier;
        private int apartmentNr;
    }
}
