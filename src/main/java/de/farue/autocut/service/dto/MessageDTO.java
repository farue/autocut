package de.farue.autocut.service.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageDTO {

    @NotNull
    private String subject;

    @NotNull
    private String content;
}
