package de.farue.autocut.service.dto;

import lombok.Data;

@Data
public class LaundryProgramDTO {

    private Long id;
    private String name;
    private String subprogram;
    private Integer spin;
    private Boolean preWash;
    private Boolean protect;
    private Integer time;
}
