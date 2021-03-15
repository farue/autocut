package de.farue.autocut.domain.auditing;

import org.hibernate.envers.RevisionListener;

import de.farue.autocut.security.SpringSecurityAuditorAware;

public class AuditorRevisionEntityListener implements RevisionListener {

    private final SpringSecurityAuditorAware auditorAware;

    public AuditorRevisionEntityListener(SpringSecurityAuditorAware auditorAware) {
        this.auditorAware = auditorAware;
    }

    @Override
    public void newRevision(Object revisionEntity) {
        AuditorRevisionEntity auditorRevisionEntity = (AuditorRevisionEntity) revisionEntity;
        auditorRevisionEntity.setAuditor(auditorAware.getCurrentAuditor().orElse(null));
    }
}
