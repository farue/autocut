package de.farue.autocut.service.dto;

import lombok.Data;

@Data
public class TimesheetDTO {

    private long id;
    private boolean enabled;
    private MemberDTO member;
    private long time;
}
