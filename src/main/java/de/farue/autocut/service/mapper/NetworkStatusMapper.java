package de.farue.autocut.service.mapper;

import de.farue.autocut.domain.NetworkSwitchStatus;
import de.farue.autocut.service.dto.NetworkStatusDTO;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface NetworkStatusMapper {
    @Mapping(target = "networkSwitchId", source = "networkSwitch.id")
    @Mapping(target = "lastUpdate", source = "timestamp")
    @Mapping(target = "speed", source = "speed", qualifiedByName = "mapSpeed")
    @Mapping(target = "maxPossibleSpeed", source = "type", qualifiedByName = "mapTypeToMaxPossibleSpeed")
    NetworkStatusDTO fromNetworkSwitchStatus(NetworkSwitchStatus status);

    @Named("mapSpeed")
    default int mapSpeed(String speed) {
        return findMaxNumber(speed);
    }

    @Named("mapTypeToMaxPossibleSpeed")
    default int mapTypeToMaxPossibleSpeed(String type) {
        return findMaxNumber(type);
    }

    default int findMaxNumber(String string) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(string);
        int max = 0;
        while (matcher.find()) {
            String match = matcher.group(1);
            try {
                int speed = Integer.parseInt(match);
                if (speed > max) {
                    max = speed;
                }
            } catch (NumberFormatException ignore) {}
        }
        return max;
    }
}
