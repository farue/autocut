package de.farue.autocut.service.dto;

import de.farue.autocut.domain.enumeration.TeamRole;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TeamMembershipDTO {

    private long id;
    private long teamId;
    private TeamRole role;
    private LocalDate start;
    private LocalDate end;
    private MemberDTO member;
}
