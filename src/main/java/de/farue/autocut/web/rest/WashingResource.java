package de.farue.autocut.web.rest;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryMachineProgram;
import de.farue.autocut.repository.LaundryMachineRepository;
import de.farue.autocut.service.WashingService;
import de.farue.autocut.web.rest.errors.LaundryMachineDoesNotExistException;
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

    private final LaundryMachineRepository laundryMachineRepository;

    @Autowired
    public WashingResource(WashingService washingService,
        LaundryMachineRepository laundryMachineRepository) {
        this.washingService = washingService;
        this.laundryMachineRepository = laundryMachineRepository;
    }

    @GetMapping("/laundry-machines")
    public List<LaundryMachine> getAllLaundryMachines(@RequestParam(required = false) Boolean enabled) {
        return washingService.getAllEnabledLaundryMachines().stream()
            .filter(machine -> enabled == null || enabled.equals(machine.isEnabled()))
            .collect(Collectors.toList());
    }

    @PostMapping("/laundry-machines/{machineId}/unlock")
    public void unlock(@PathVariable Long machineId, @RequestParam Long programId) {
        LaundryMachine machine = laundryMachineRepository.findById(machineId)
            .orElseThrow(LaundryMachineDoesNotExistException::new);
        LaundryMachineProgram program = machine.getPrograms().stream()
            .filter(p -> p.getId().equals(programId))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
        washingService.purchaseAndUnlock(machine, program);
    }
}
