package de.farue.autocut.service.dto;

import de.farue.autocut.domain.Address;
import de.farue.autocut.domain.enumeration.ApartmentTypes;
import lombok.Data;

@Data
public class ApartmentDTO {

    private String nr;
    private ApartmentTypes type;
    private int maxNumberOfLeases;
    private Address address;
}
