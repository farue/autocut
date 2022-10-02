package de.farue.autocut.service.dto;

import lombok.Data;

@Data
public class TeamDTO {

    private long id;
    private String name;
    private String email;
    private int teamMembersCount;
}
