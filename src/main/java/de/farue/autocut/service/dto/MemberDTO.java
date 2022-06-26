package de.farue.autocut.service.dto;

import lombok.Data;

@Data
public class MemberDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailConfirmed;
    private boolean memberVerified;
}
