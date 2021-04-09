package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.Tenant;
import java.util.Set;

public class TenantNameMatchCandidateProvider implements MatchCandidateProvider {

    @Override
    public Set<String> buildMatchCandidates(Tenant tenant) {
        return Set.of(tenant.getFirstName() + " " + tenant.getLastName());
    }
}
