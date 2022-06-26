package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.domain.NetworkSwitchStatus;
import de.farue.autocut.service.dto.InternetDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring", uses = { NetworkStatusMapper.class }, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Service
public interface InternetMapper {
    @Mapping(target = "ip", source = "internetAccess.ip1")
    @Mapping(target = "networkSwitch", source = "internetAccess.networkSwitch.id")
    @Mapping(target = "switchport", source = "internetAccess", qualifiedByName = "switchport")
    @Mapping(target = "status", source = "status")
    InternetDTO fromInternetAccess(InternetAccess internetAccess, NetworkSwitchStatus status);

    @Named("switchport")
    default String switchport(InternetAccess internetAccess) {
        return internetAccess.getNetworkSwitch().getInterfaceName() + "/" + internetAccess.getPort();
    }
}
