package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.Tenant;
import java.util.HashSet;
import java.util.Set;

public class TenantPurposeMatchCandidateProvider implements MatchCandidateProvider {

    @Override
    public Set<String> buildMatchCandidates(Tenant tenant) {
        return buildMatchCandidates(
            tenant.getLease().getApartment().getAddress().getStreetNumber(),
            tenant.getLease().getApartment().getNr(),
            tenant.getFirstName(),
            tenant.getLastName()
        );
    }

    public Set<String> buildMatchCandidates(String streetNumber, String apartmentNumber, String firstName, String lastName) {
        Set<String> candidates = new HashSet<>();
        candidates.add(streetNumber + " " + apartmentNumber + " " + firstName + " " + lastName);
        candidates.add(streetNumber + " " + apartmentNumber + " " + lastName);
        candidates.add(lastName + " " + streetNumber + " " + apartmentNumber);
        candidates.add(firstName + " " + lastName + " " + streetNumber + " " + apartmentNumber);
        return candidates;
    }
}
