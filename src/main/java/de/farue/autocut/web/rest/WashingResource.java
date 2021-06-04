package de.farue.autocut.web.rest;

import de.farue.autocut.domain.LaundryMachine;
import de.farue.autocut.domain.LaundryProgram;
import de.farue.autocut.repository.LaundryMachineProgramRepository;
import de.farue.autocut.repository.LaundryProgramRepository;
import de.farue.autocut.repository.UserRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.LaundryMachineService;
import de.farue.autocut.service.TenantService;
import de.farue.autocut.service.WashingService;
import de.farue.autocut.service.dto.LaundryMachineDTO;
import de.farue.autocut.service.dto.LaundryProgramDTO;
import de.farue.autocut.service.dto.WashitActivateDTO;
import de.farue.autocut.service.mapper.LaundryMachineMapper;
import de.farue.autocut.service.mapper.LaundryProgramMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/washing")
public class WashingResource {

    private final Logger log = LoggerFactory.getLogger(WashingResource.class);

    private final WashingService washingService;
    private final LaundryMachineService laundryMachineService;

    private final LaundryProgramRepository laundryProgramRepository;
    private final LaundryMachineProgramRepository laundryMachineProgramRepository;
    private final UserRepository userRepository;
    private final TenantService tenantService;
    private final LaundryMachineMapper laundryMachineMapper;
    private final LaundryProgramMapper laundryProgramMapper;

    public WashingResource(
        WashingService washingService,
        LaundryMachineService laundryMachineService,
        LaundryProgramRepository laundryProgramRepository,
        LaundryMachineProgramRepository laundryMachineProgramRepository,
        UserRepository userRepository,
        TenantService tenantService,
        LaundryMachineMapper laundryMachineMapper,
        LaundryProgramMapper laundryProgramMapper
    ) {
        this.washingService = washingService;
        this.laundryMachineService = laundryMachineService;
        this.laundryProgramRepository = laundryProgramRepository;
        this.laundryMachineProgramRepository = laundryMachineProgramRepository;
        this.userRepository = userRepository;
        this.tenantService = tenantService;
        this.laundryMachineMapper = laundryMachineMapper;
        this.laundryProgramMapper = laundryProgramMapper;
    }

    @GetMapping("/laundry-machines")
    public List<LaundryMachineDTO> getAllLaundryMachines() {
        log.debug("REST request to get all LaundryMachines");
        return laundryMachineService.findAll().stream().map(this::map).collect(Collectors.toList());
    }

    @GetMapping("/laundry-machines/{id}")
    public ResponseEntity<LaundryMachineDTO> getLaundryMachine(@PathVariable Long id) {
        log.debug("REST request to get LaundryMachine : {}", id);
        Optional<LaundryMachineDTO> laundryMachine = laundryMachineService.findOne(id).map(this::map);
        return ResponseUtil.wrapOrNotFound(laundryMachine);
    }

    @GetMapping("/laundry-machines/{id}/programs")
    public List<LaundryProgramDTO> getLaundryMachinePrograms(@PathVariable Long id) {
        log.debug("REST request to get all LaundryMachinePrograms for LaundryMachine: {}", id);
        return laundryMachineService
            .findOne(id)
            .stream()
            .flatMap(machine -> laundryMachineProgramRepository.findAllByMachine(machine).stream())
            .map(laundryProgramMapper::fromLaundryMachineProgram)
            .collect(Collectors.toList());
    }

    @PostMapping("/laundry-machines/{id}/unlock")
    public ResponseEntity<WashitActivateDTO> unlock(@PathVariable("id") Long machineId, @RequestParam Long programId) {
        LaundryMachine machine = laundryMachineService
            .findOne(machineId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        LaundryProgram program = laundryProgramRepository
            .findById(programId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Supplied program does not exist."));

        Optional<WashitActivateDTO> responseOptional = SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(tenantService::findOneByUser)
            .map(tenant -> washingService.purchaseAndUnlock(tenant, machine, program));
        if (responseOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(responseOptional.get());
    }

    @PostMapping("/laundry-machines/{id}/disable")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void disable(@PathVariable("id") Long machineId) {
        washingService.disableMachine(machineId);
    }

    @PostMapping("/laundry-machines/{id}/enable")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void enable(@PathVariable("id") Long machineId) {
        washingService.enableMachine(machineId);
    }

    private LaundryMachineDTO map(LaundryMachine machine) {
        Instant inUseUntil = washingService.getInUseUntilDate(machine).orElse(null);
        return laundryMachineMapper.fromLaundryMachine(machine, inUseUntil);
    }
}
