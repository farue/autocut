package de.farue.autocut.domain.auditing;

import java.text.DateFormat;
import java.util.Objects;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

//@Entity
@RevisionEntity(AuditorRevisionEntityListener.class)
public class AuditorRevisionEntity extends DefaultRevisionEntity {

    private String auditor;

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AuditorRevisionEntity that = (AuditorRevisionEntity) o;
        return Objects.equals(auditor, that.auditor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), auditor);
    }

    @Override
    public String toString() {
        return (
            "AuditorRevisionEntity{" +
            "id='" +
            getId() +
            '\'' +
            ", revisionDate='" +
            DateFormat.getDateTimeInstance().format(getRevisionDate()) +
            '\'' +
            ", auditor='" +
            auditor +
            '\'' +
            "}"
        );
    }
}
