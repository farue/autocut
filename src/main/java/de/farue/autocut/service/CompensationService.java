package de.farue.autocut.service;

import de.farue.autocut.domain.Compensation;
import de.farue.autocut.domain.Timesheet;
import de.farue.autocut.domain.TimesheetStatement;
import de.farue.autocut.domain.TimesheetTime;
import de.farue.autocut.repository.TimesheetTimeRepository;
import de.farue.autocut.service.dto.CompensationDTO;
import de.farue.autocut.service.mapper.CompensationMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CompensationService {

    private static final BigDecimal TOTAL_COMPENSATION = new BigDecimal(2000);
    private static final BigDecimal MIN = new BigDecimal(5);
    private static final BigDecimal MAX = new BigDecimal(500);

    private final TimesheetTimeRepository timesheetTimeRepository;
    private final CompensationMapper compensationMapper;

    public CompensationService(TimesheetTimeRepository timesheetTimeRepository, CompensationMapper compensationMapper) {
        this.timesheetTimeRepository = timesheetTimeRepository;
        this.compensationMapper = compensationMapper;
    }

    public CompensationDTO getCompensation(long timesheetId, Instant earliest, Instant latest) {
        return calculateCompensations(earliest, latest)
            .stream()
            .filter(c -> timesheetId == c.getTimesheet().getId())
            .findFirst()
            .orElse(null);
    }

    public List<CompensationDTO> calculateCompensations(Instant earliest, Instant latest) {
        List<TimesheetTime> times = timesheetTimeRepository.findAllByEndAfterAndEndBefore(earliest, latest);
        Map<Timesheet, List<TimesheetTime>> timesByTimesheet = times.stream().collect(Collectors.groupingBy(TimesheetTime::getTimesheet));
        Collection<List<TimesheetTime>> groupedTimes = timesByTimesheet.values();
        Set<TimesheetStatement> statements = groupedTimes.stream().map(this::map).collect(Collectors.toSet());

        CompensationCalculator compensationCalculator = new CompensationCalculator(TOTAL_COMPENSATION, MIN, MAX);
        List<Compensation> compensations = compensationCalculator.calculate(statements);

        Map<Long, Timesheet> timesheetsById = timesByTimesheet
            .keySet()
            .stream()
            .collect(Collectors.toMap(Timesheet::getId, Function.identity()));
        return compensations.stream().map(c -> compensationMapper.fromCompensation(c, timesheetsById.get(c.getTimesheetId()))).toList();
    }

    private TimesheetStatement map(List<TimesheetTime> times) {
        if (times == null || times.size() == 0) {
            return null;
        }
        TimesheetStatement statement = new TimesheetStatement();
        statement.setWorkedTime(times.stream().mapToLong(TimesheetTime::getEffectiveTime).sum());
        statement.setTimesheetId(times.get(0).getTimesheet().getId());
        return statement;
    }
}
