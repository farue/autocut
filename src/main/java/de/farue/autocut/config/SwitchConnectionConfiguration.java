package de.farue.autocut.config;

import de.farue.autocut.service.internetaccess.AutoReconnectingSshShell;
import de.farue.autocut.service.internetaccess.SshConnection;
import de.farue.autocut.service.internetaccess.SwitchCommandExecutor;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwitchConnectionConfiguration {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    public Map<String, SwitchCommandExecutor> switchExecutorsByHostName() {
        Map<String, SwitchCommandExecutor> switchExecutorsByHostName = new HashMap<>();
        for (SshConnection switchConnection : applicationProperties.getSwitchConnections()) {
            SwitchCommandExecutor executor = new SwitchCommandExecutor(new AutoReconnectingSshShell(switchConnection));
            switchExecutorsByHostName.put(switchConnection.getHostname(), executor);
        }
        return switchExecutorsByHostName;
    }
}
