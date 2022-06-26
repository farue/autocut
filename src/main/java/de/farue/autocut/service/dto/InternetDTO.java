package de.farue.autocut.service.dto;

import lombok.Data;

@Data
public class InternetDTO {

    private String ip;
    private String networkSwitch;
    private String switchport;
    private NetworkStatusDTO status;
}
