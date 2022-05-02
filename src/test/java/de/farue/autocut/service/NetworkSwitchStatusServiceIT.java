package de.farue.autocut.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import de.farue.autocut.AutocutApp;
import de.farue.autocut.domain.NetworkSwitch;
import de.farue.autocut.domain.NetworkSwitchStatus;
import de.farue.autocut.repository.NetworkSwitchRepository;
import de.farue.autocut.service.NetworkSwitchStatusServiceIT.SwitchConnectionConfiguration;
import de.farue.autocut.service.internetaccess.SwitchCommandExecutor;
import de.farue.autocut.service.internetaccess.SwitchStatusColumns;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@Disabled // TODO
@SpringBootTest(classes = { AutocutApp.class, SwitchConnectionConfiguration.class })
class NetworkSwitchStatusServiceIT {

    private static final String SWITCH_STATUS_CONNECTED = "connected";
    private static final String SWITCH_STATUS_NOTCONNECT = "notconnect";
    private static final String SWITCH_STATUS_DISBALED = "disabled";

    private static final String SWITCH_STATUS_PORT_1 = "Gi0/1";
    private static final String SWITCH_STATUS_NAME_1 = "125-1";
    private static final String SWITCH_STATUS_VLAN_1 = "264";
    private static final String SWITCH_STATUS_DUPLEX_1 = "a-full";
    private static final String SWITCH_STATUS_SPEED_1 = "a-100";
    private static final String SWITCH_STATUS_TYPE_1 = "10/100/1000BaseTX";

    private static final String SWITCH_STATUS_PORT_2 = "Gi0/2";
    private static final String SWITCH_STATUS_NAME_2 = "125-2";
    private static final String SWITCH_STATUS_VLAN_2 = "264";
    private static final String SWITCH_STATUS_DUPLEX_2 = "auto";
    private static final String SWITCH_STATUS_SPEED_2 = "auto";
    private static final String SWITCH_STATUS_TYPE_2 = "10/100/1000BaseTX";

    private static final String SWITCH_STATUS_PORT_3 = "Gi0/3";
    private static final String SWITCH_STATUS_NAME_3 = "";
    private static final String SWITCH_STATUS_VLAN_3 = "1";
    private static final String SWITCH_STATUS_DUPLEX_3 = "auto";
    private static final String SWITCH_STATUS_SPEED_3 = "auto";
    private static final String SWITCH_STATUS_TYPE_3 = "Not Present";

    private static final String SWITCH_STATUS_PORT_4 = "Gi0/1";
    private static final String SWITCH_STATUS_NAME_4 = "125-1";
    private static final String SWITCH_STATUS_VLAN_4 = "264";
    private static final String SWITCH_STATUS_DUPLEX_4 = "a-full";
    private static final String SWITCH_STATUS_SPEED_4 = "a-100";
    private static final String SWITCH_STATUS_TYPE_4 = "10/100/1000BaseTX";

    private static final String SWITCH_STATUS_PORT_5 = "Po1";
    private static final String SWITCH_STATUS_NAME_5 = "Uplink n3k-tuerme";
    private static final String SWITCH_STATUS_VLAN_5 = "trunk";
    private static final String SWITCH_STATUS_DUPLEX_5 = "a-full";
    private static final String SWITCH_STATUS_SPEED_5 = "a-1000";
    private static final String SWITCH_STATUS_TYPE_5 = "";

    @Autowired
    private NetworkSwitchStatusService networkSwitchStatusService;

    @Autowired
    private NetworkSwitchRepository networkSwitchRepository;

    private NetworkSwitch switch1;
    private NetworkSwitch switch2;

    @BeforeEach
    void setUp() {
        NetworkSwitch switch1 = new NetworkSwitch().interfaceName("Gi0").sshHost("host1");
        this.switch1 = networkSwitchRepository.save(switch1);

        NetworkSwitch switch2 = new NetworkSwitch().interfaceName("Gi0").sshHost("host2");
        this.switch2 = networkSwitchRepository.save(switch2);
    }

    @Test
    void testCreateSwitchStatus() {
        Instant timestampTestStart = Instant.now();

        List<NetworkSwitchStatus> switchStatus = networkSwitchStatusService.getSwitchStatus(switch1);

        assertThat(switchStatus).hasSize(1);
        NetworkSwitchStatus status = switchStatus.get(0);
        assertThat(status.getNetworkSwitch()).isEqualTo(switch1);
        assertThat(status.getPort()).isEqualTo(SWITCH_STATUS_PORT_1);
        assertThat(status.getName()).isEqualTo(SWITCH_STATUS_NAME_1);
        assertThat(status.getStatus()).isEqualTo(SWITCH_STATUS_CONNECTED);
        assertThat(status.getVlan()).isEqualTo(SWITCH_STATUS_VLAN_1);
        assertThat(status.getSpeed()).isEqualTo(SWITCH_STATUS_SPEED_1);
        assertThat(status.getType()).isEqualTo(SWITCH_STATUS_TYPE_1);
        assertThat(status.getTimestamp()).isBetween(timestampTestStart, Instant.now());
    }

    @TestConfiguration
    static class SwitchConnectionConfiguration {

        @SuppressWarnings("unchecked")
        @Bean
        public Map<String, SwitchCommandExecutor> switchExecutorsByHostName() {
            SwitchCommandExecutor executorMock1 = mock(SwitchCommandExecutor.class);
            when(executorMock1.getStatus()).thenReturn(switch1status1(), switch1status2());

            SwitchCommandExecutor executorMock2 = mock(SwitchCommandExecutor.class);
            when(executorMock2.getStatus()).thenReturn(switch2status1(), switch2status2());

            Map<String, SwitchCommandExecutor> switchExecutorsByHostName = new HashMap<>();
            switchExecutorsByHostName.put("host1", executorMock1);
            switchExecutorsByHostName.put("host2", executorMock2);
            return switchExecutorsByHostName;
        }

        private Table<String, String, String> switch1status1() {
            List<String> rowKeys = List.of(SWITCH_STATUS_PORT_1);
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

            table.put(rowKeys.get(0), SwitchStatusColumns.PORT, SWITCH_STATUS_PORT_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.NAME, SWITCH_STATUS_NAME_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.STATUS, SWITCH_STATUS_CONNECTED);
            table.put(rowKeys.get(0), SwitchStatusColumns.VLAN, SWITCH_STATUS_VLAN_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.DUPLEX, SWITCH_STATUS_DUPLEX_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.SPEED, SWITCH_STATUS_SPEED_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.TYPE, SWITCH_STATUS_TYPE_1);

            return table;
        }

        private Table<String, String, String> switch1status2() {
            List<String> rowKeys = List.of(SWITCH_STATUS_PORT_1, SWITCH_STATUS_PORT_2, SWITCH_STATUS_PORT_3);
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

            table.put(rowKeys.get(0), SwitchStatusColumns.PORT, SWITCH_STATUS_PORT_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.NAME, SWITCH_STATUS_NAME_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.STATUS, SWITCH_STATUS_DISBALED);
            table.put(rowKeys.get(0), SwitchStatusColumns.VLAN, SWITCH_STATUS_VLAN_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.DUPLEX, SWITCH_STATUS_DUPLEX_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.SPEED, SWITCH_STATUS_SPEED_1);
            table.put(rowKeys.get(0), SwitchStatusColumns.TYPE, SWITCH_STATUS_TYPE_1);

            table.put(rowKeys.get(1), SwitchStatusColumns.PORT, SWITCH_STATUS_PORT_2);
            table.put(rowKeys.get(1), SwitchStatusColumns.NAME, SWITCH_STATUS_NAME_2);
            table.put(rowKeys.get(1), SwitchStatusColumns.STATUS, SWITCH_STATUS_NOTCONNECT);
            table.put(rowKeys.get(1), SwitchStatusColumns.VLAN, SWITCH_STATUS_VLAN_2);
            table.put(rowKeys.get(1), SwitchStatusColumns.DUPLEX, SWITCH_STATUS_DUPLEX_2);
            table.put(rowKeys.get(1), SwitchStatusColumns.SPEED, SWITCH_STATUS_SPEED_2);
            table.put(rowKeys.get(1), SwitchStatusColumns.TYPE, SWITCH_STATUS_TYPE_2);

            table.put(rowKeys.get(2), SwitchStatusColumns.PORT, SWITCH_STATUS_PORT_3);
            table.put(rowKeys.get(2), SwitchStatusColumns.NAME, SWITCH_STATUS_NAME_3);
            table.put(rowKeys.get(2), SwitchStatusColumns.STATUS, SWITCH_STATUS_DISBALED);
            table.put(rowKeys.get(2), SwitchStatusColumns.VLAN, SWITCH_STATUS_VLAN_3);
            table.put(rowKeys.get(2), SwitchStatusColumns.DUPLEX, SWITCH_STATUS_DUPLEX_3);
            table.put(rowKeys.get(2), SwitchStatusColumns.SPEED, SWITCH_STATUS_SPEED_3);
            table.put(rowKeys.get(2), SwitchStatusColumns.TYPE, SWITCH_STATUS_TYPE_3);

            return table;
        }

        private Table<String, String, String> switch2status1() {
            List<String> rowKeys = List.of(SWITCH_STATUS_PORT_4, SWITCH_STATUS_PORT_5);
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

            table.put(rowKeys.get(0), SwitchStatusColumns.PORT, SWITCH_STATUS_PORT_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.NAME, SWITCH_STATUS_NAME_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.STATUS, SWITCH_STATUS_CONNECTED);
            table.put(rowKeys.get(0), SwitchStatusColumns.VLAN, SWITCH_STATUS_VLAN_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.DUPLEX, SWITCH_STATUS_DUPLEX_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.SPEED, SWITCH_STATUS_SPEED_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.TYPE, SWITCH_STATUS_TYPE_4);

            table.put(rowKeys.get(1), SwitchStatusColumns.PORT, SWITCH_STATUS_PORT_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.NAME, SWITCH_STATUS_NAME_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.STATUS, SWITCH_STATUS_CONNECTED);
            table.put(rowKeys.get(1), SwitchStatusColumns.VLAN, SWITCH_STATUS_VLAN_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.DUPLEX, SWITCH_STATUS_DUPLEX_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.SPEED, SWITCH_STATUS_SPEED_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.TYPE, SWITCH_STATUS_TYPE_5);

            return table;
        }

        private Table<String, String, String> switch2status2() {
            List<String> rowKeys = List.of(SWITCH_STATUS_PORT_4, SWITCH_STATUS_PORT_5);
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

            table.put(rowKeys.get(0), SwitchStatusColumns.PORT, SWITCH_STATUS_PORT_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.NAME, SWITCH_STATUS_NAME_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.STATUS, SWITCH_STATUS_DISBALED);
            table.put(rowKeys.get(0), SwitchStatusColumns.VLAN, SWITCH_STATUS_VLAN_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.DUPLEX, SWITCH_STATUS_DUPLEX_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.SPEED, SWITCH_STATUS_SPEED_4);
            table.put(rowKeys.get(0), SwitchStatusColumns.TYPE, SWITCH_STATUS_TYPE_4);

            table.put(rowKeys.get(1), SwitchStatusColumns.PORT, SWITCH_STATUS_PORT_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.NAME, SWITCH_STATUS_NAME_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.STATUS, SWITCH_STATUS_CONNECTED);
            table.put(rowKeys.get(1), SwitchStatusColumns.VLAN, SWITCH_STATUS_VLAN_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.DUPLEX, SWITCH_STATUS_DUPLEX_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.SPEED, SWITCH_STATUS_SPEED_5);
            table.put(rowKeys.get(1), SwitchStatusColumns.TYPE, SWITCH_STATUS_TYPE_5);

            return table;
        }
    }
}
