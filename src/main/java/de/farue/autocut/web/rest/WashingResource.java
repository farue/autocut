package de.farue.autocut.web.rest;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.service.WashingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/washing")
@Transactional
public class WashingResource {

    private final Logger log = LoggerFactory.getLogger(WashingResource.class);

    private final WashingService washingService;

    @Autowired
    public WashingResource(WashingService washingService) {
        this.washingService = washingService;
    }

    @GetMapping("/laundry-machines")
    public List<LaundryMachine> getAllLaundryMachines(
        @RequestParam(required = false) Boolean enabled) {
        List<LaundryMachine> list = washingService.getAllEnabledLaundryMachines().stream()
            .filter(machine -> enabled == null || enabled == machine.isEnabled())
            .collect(Collectors.toList());
        return list;
    }

    @PostMapping("/laundry-machines/{machineId}/unlock")
    public void unlock(@PathVariable Long machineId, @RequestParam Long programId) {
        washingService.purchaseAndUnlock(machineId, programId);
    }
}
