package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.service.dto.TimesheetDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring", uses = { MemberMapper.class }, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Service
public interface TimesheetMapper {
    TimesheetDTO fromTimesheet(Timesheet timesheet, long time);
}
