package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.Compensation;
import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.service.dto.CompensationDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring", uses = { TimesheetMapper.class }, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Service
public interface CompensationMapper {
    CompensationDTO fromCompensation(Compensation compensation, Timesheet timesheet);
}
