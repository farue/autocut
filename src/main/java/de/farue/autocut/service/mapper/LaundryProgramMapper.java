package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.service.dto.LaundryProgramDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface LaundryProgramMapper {
    @Mapping(target = "id", source = "program.id")
    @Mapping(target = ".", source = "program")
    LaundryProgramDTO fromLaundryMachineProgram(LaundryMachineProgram program);
}
