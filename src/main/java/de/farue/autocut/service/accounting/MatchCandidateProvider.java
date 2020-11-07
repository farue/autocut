package de.farue.autocut.service.accounting;

import java.util.Set;

import de.farue.autocut.domain.Tenant;

public interface MatchCandidateProvider {

    Set<String> buildMatchCandidates(Tenant tenant);
}
