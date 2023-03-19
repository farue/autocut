package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.TimesheetTime;
import de.farue.autocut.service.dto.TimesheetTimeDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface TimesheetTimeMapper {
    TimesheetTimeDTO fromTimesheetTime(TimesheetTime time, boolean editable, boolean removable);
}
