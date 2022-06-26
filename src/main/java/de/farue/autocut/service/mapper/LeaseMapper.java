package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.Lease;
import de.farue.autocut.service.dto.LeaseDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface LeaseMapper {
    LeaseDTO fromLease(Lease lease);
}
