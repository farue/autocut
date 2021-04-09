package de.farue.autocut.service.accounting;

import de.farue.autocut.domain.Tenant;
import java.util.Set;

public interface MatchCandidateProvider {
    Set<String> buildMatchCandidates(Tenant tenant);
}
