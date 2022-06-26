package de.farue.autocut.service.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class LeaseDTO {

    private long id;
    private LocalDate start;
    private LocalDate end;
}
