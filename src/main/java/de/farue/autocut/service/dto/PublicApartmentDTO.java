package de.farue.autocut.service.dto;

import de.farue.autocut.domain.Address;
import lombok.Data;

@Data
public class PublicApartmentDTO {

    private String nr;
    private Address address;
}
