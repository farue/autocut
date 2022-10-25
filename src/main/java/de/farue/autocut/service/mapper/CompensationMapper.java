package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.Compensation;
import de.farue.autocut.service.dto.CompensationDTO;
import de.farue.autocut.service.dto.TimesheetDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface CompensationMapper {
    CompensationDTO fromCompensation(Compensation compensation, TimesheetDTO timesheet);
}
