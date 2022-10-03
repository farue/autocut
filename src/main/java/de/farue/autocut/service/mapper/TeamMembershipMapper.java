package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.TeamMembership;
import de.farue.autocut.service.dto.TeamMembershipDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring", uses = { MemberMapper.class }, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Service
public interface TeamMembershipMapper {
    @Mapping(target = "member", source = "tenant")
    @Mapping(target = "teamId", source = "team.id")
    TeamMembershipDTO fromTeamMembership(TeamMembership teamMembership);
}
