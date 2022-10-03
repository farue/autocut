package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.Team;
import de.farue.autocut.service.dto.TeamDTO;
import java.time.Instant;
import java.time.ZoneId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface TeamMapper {
    @Mapping(target = "activeTeamMembersCount", source = "team", qualifiedByName = "activeTeamMembersCount")
    TeamDTO fromTeam(Team team);

    @Named("activeTeamMembersCount")
    default int teamMembersCount(Team team) {
        return (int) team
            .getTeamMemberships()
            .stream()
            .filter(m ->
                (m.getStart() == null || Instant.now().isAfter(m.getStart().atStartOfDay(ZoneId.systemDefault()).toInstant())) &&
                (m.getEnd() == null || Instant.now().isBefore(m.getEnd().atStartOfDay(ZoneId.systemDefault()).toInstant()))
            )
            .count();
    }
}
