package de.farue.autocut.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactFormDTO {

    private String name;

    private String apartment;

    @Email
    private String email;

    @NotBlank
    private String subject;

    @NotBlank
    private String message;

    private Boolean copyToOwnEmail;
}
