package de.farue.autocut.service.accounting;

import java.util.Set;

import de.farue.autocut.domain.Tenant;

public class TenantNameMatchCandidateProvider implements MatchCandidateProvider {

    @Override
    public Set<String> buildMatchCandidates(Tenant tenant) {
        return Set.of(tenant.getFirstName() + " " + tenant.getLastName());
    }
}
