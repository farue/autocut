package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.Team;
import de.farue.autocut.service.dto.TeamDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface TeamMapper {
    @Mapping(target = "teamMembersCount", source = "team", qualifiedByName = "teamMembersCount")
    TeamDTO fromTeam(Team team);

    @Named("teamMembersCount")
    default int teamMembersCount(Team team) {
        return team.getTeamMemberships().size();
    }
}
