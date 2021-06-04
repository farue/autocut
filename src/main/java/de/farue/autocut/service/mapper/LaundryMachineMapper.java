package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.service.dto.LaundryMachineDTO;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface LaundryMachineMapper {
    LaundryMachineDTO fromLaundryMachine(LaundryMachine machine, Instant inUseUntil);
}
