package de.farue.autocut.service;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import de.farue.autocut.domain.InternetAccess;
import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.repository.NetworkSwitchRepository;
import de.farue.autocut.service.internetaccess.SwitchCommandExecutor;
import de.farue.autocut.service.internetaccess.SwitchStatusColumns;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tech.jhipster.config.JHipsterConstants;

@Service
@Profile("!" + JHipsterConstants.SPRING_PROFILE_PRODUCTION)
public class NetworkSwitchServiceMock extends NetworkSwitchService {

    private final Logger log = LoggerFactory.getLogger(NetworkSwitchServiceMock.class);

    private final InternetAccessService internetAccessService;

    public NetworkSwitchServiceMock(
        NetworkSwitchRepository networkSwitchRepository,
        Map<String, SwitchCommandExecutor> switchExecutorsByHostName,
        InternetAccessService internetAccessService
    ) {
        super(networkSwitchRepository, switchExecutorsByHostName);
        this.internetAccessService = internetAccessService;
    }

    @Override
    public void enable(InternetAccess internetAccess) {
        log.debug("Mocking enable internet access");
    }

    @Override
    public void disable(InternetAccess internetAccess) {
        log.debug("Mocking disable internet access");
    }

    @Override
    public Table<String, String, String> getStatus(NetworkSwitch networkSwitch) {
        List<InternetAccess> internetAccesses = internetAccessService.findAllByNetworkSwitch(networkSwitch);

        String interfaceName = networkSwitch.getInterfaceName();
        List<String> rowKeys = List.of(interfaceName);
        List<String> columnKeys = List.of(
            SwitchStatusColumns.PORT,
            SwitchStatusColumns.NAME,
            SwitchStatusColumns.STATUS,
            SwitchStatusColumns.VLAN,
            SwitchStatusColumns.DUPLEX,
            SwitchStatusColumns.SPEED,
            SwitchStatusColumns.TYPE
        );

        @SuppressWarnings("UnstableApiUsage")
        Table<String, String, String> table = ArrayTable.create(rowKeys, columnKeys);

        for (InternetAccess internetAccess : internetAccesses) {
            table.put(rowKeys.get(0), SwitchStatusColumns.PORT, interfaceName + "/" + internetAccess.getPort());
            table.put(rowKeys.get(0), SwitchStatusColumns.NAME, "");
            table.put(rowKeys.get(0), SwitchStatusColumns.STATUS, "connected");
            table.put(rowKeys.get(0), SwitchStatusColumns.VLAN, "vlan1");
            table.put(rowKeys.get(0), SwitchStatusColumns.DUPLEX, "a-full");
            table.put(rowKeys.get(0), SwitchStatusColumns.SPEED, "a-100");
            table.put(rowKeys.get(0), SwitchStatusColumns.TYPE, "10/100/1000BaseTX");
        }

        return table;
    }
}
