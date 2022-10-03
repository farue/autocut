package de.farue.autocut.service;

import de.farue.autocut.domain.*;
import de.farue.autocut.repository.TeamRepository;
import de.farue.autocut.security.AuthoritiesConstants;
import de.farue.autocut.security.SecurityUtils;
import de.farue.autocut.service.accounting.TransactionBookService;
import de.farue.autocut.service.dto.MemberDTO;
import de.farue.autocut.service.dto.MembershipDTO;
import de.farue.autocut.service.dto.TeamDTO;
import de.farue.autocut.service.dto.TransactionBookDTO;
import de.farue.autocut.service.mapper.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Transactional
@Service
public class AdminService {

    private final NetworkSwitchStatusService networkSwitchStatusService;
    private final TransactionBookService transactionBookService;
    private final LeaseService leaseService;
    private final MemberMapper memberMapper;
    private final LeaseMapper leaseMapper;
    private final TransactionBookMapper transactionBookMapper;
    private final InternetMapper internetMapper;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public AdminService(
        NetworkSwitchStatusService networkSwitchStatusService,
        TransactionBookService transactionBookService,
        LeaseService leaseService,
        MemberMapper memberMapper,
        LeaseMapper leaseMapper,
        TransactionBookMapper transactionBookMapper,
        InternetMapper internetMapper,
        TeamRepository teamRepository,
        TeamMapper teamMapper
    ) {
        this.networkSwitchStatusService = networkSwitchStatusService;
        this.transactionBookService = transactionBookService;
        this.leaseService = leaseService;
        this.memberMapper = memberMapper;
        this.leaseMapper = leaseMapper;
        this.transactionBookMapper = transactionBookMapper;
        this.internetMapper = internetMapper;
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    public TransactionBookDTO getCashTransactionBook(Lease lease) {
        TransactionBook cashTransactionBook = leaseService.getCashTransactionBook(lease);
        BigDecimal currentBalance = transactionBookService.getCurrentBalance(cashTransactionBook);
        return transactionBookMapper.fromTransactionBook(cashTransactionBook, currentBalance);
    }

    public List<MembershipDTO> getMemberships() {
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<Lease> leases = leaseService.getAllUnexpiredLeasesAt(LocalDate.now());
        List<MembershipDTO> memberList = new ArrayList<>();
        for (Lease lease : leases) {
            Set<Tenant> tenants = lease.getTenants();
            Apartment apartment = lease.getApartment();
            InternetAccess internetAccess = apartment.getInternetAccess();
            NetworkSwitchStatus internetStatus = networkSwitchStatusService.getSwitchInterfaceStatus(internetAccess).orElse(null);

            MembershipDTO memberListDTO = new MembershipDTO();
            memberListDTO.setLease(leaseMapper.fromLease(lease));
            List<MemberDTO> memberDTOs = tenants.stream().map(memberMapper::fromLaundryMachine).toList();
            memberListDTO.setMembers(memberDTOs);
            memberListDTO.setTransactionBook(getCashTransactionBook(lease));
            memberListDTO.setApartment(apartment);
            memberListDTO.setInternet(internetMapper.fromInternetAccess(internetAccess, internetStatus));
            memberList.add(memberListDTO);
        }
        return memberList;
    }

    public List<TeamDTO> getTeams() {
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return teamRepository.findAll().stream().map(teamMapper::fromTeam).toList();
    }
}
