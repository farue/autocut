package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.Apartment;
import de.farue.autocut.service.dto.PublicApartmentDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface PublicApartmentMapper {
    PublicApartmentDTO apartmentToPublicApartment(Apartment apartment);
}
