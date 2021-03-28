package de.farue.autocut.service;

import static de.farue.autocut.utils.BigDecimalUtil.modify;

import de.farue.autocut.domain.Activity;
import de.farue.autocut.repository.ActivityRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.farue.autocut.domain.Activity;
import de.farue.autocut.domain.Lease;
import de.farue.autocut.domain.enumeration.SemesterTerms;
import de.farue.autocut.repository.ActivityRepository;
import de.farue.autocut.utils.DateUtil;

/**
 * Service Implementation for managing {@link Activity}.
 */
@Service
@Transactional
public class ActivityService {

    public static final String FEE_KEY = "member-fee";
    public static final String DISCOUNTED_FEE_KEY = "member-fee-discounted";

    private final Logger log = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepository activityRepository;
    private final GlobalSettingService globalSettingService;

    public ActivityService(ActivityRepository activityRepository, GlobalSettingService globalSettingService) {
        this.activityRepository = activityRepository;
        this.globalSettingService = globalSettingService;
    }

    /**
     * Save a activity.
     *
     * @param activity the entity to save.
     * @return the persisted entity.
     */
    public Activity save(Activity activity) {
        log.debug("Request to save Activity : {}", activity);
        return activityRepository.save(activity);
    }

    /**
     * Partially update a activity.
     *
     * @param activity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Activity> partialUpdate(Activity activity) {
        log.debug("Request to partially update Activity : {}", activity);

        return activityRepository
            .findById(activity.getId())
            .map(
                existingActivity -> {
                    if (activity.getYear() != null) {
                        existingActivity.setYear(activity.getYear());
                    }
                    if (activity.getTerm() != null) {
                        existingActivity.setTerm(activity.getTerm());
                    }
                    if (activity.getStart() != null) {
                        existingActivity.setStart(activity.getStart());
                    }
                    if (activity.getEnd() != null) {
                        existingActivity.setEnd(activity.getEnd());
                    }
                    if (activity.getDescription() != null) {
                        existingActivity.setDescription(activity.getDescription());
                    }
                    if (activity.getDiscount() != null) {
                        existingActivity.setDiscount(activity.getDiscount());
                    }
                    if (activity.getStwActivity() != null) {
                        existingActivity.setStwActivity(activity.getStwActivity());
                    }

                    return existingActivity;
                }
            )
            .map(activityRepository::save);
    }

    /**
     * Get all the activities.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Activity> findAll() {
        log.debug("Request to get all Activities");
        return activityRepository.findAll();
    }

    /**
     * Get one activity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Activity> findOne(Long id) {
        log.debug("Request to get Activity : {}", id);
        return activityRepository.findById(id);
    }

    /**
     * Delete the activity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Activity : {}", id);
        activityRepository.deleteById(id);
    }

    public List<Activity> findActivityOn(Lease lease, LocalDate date) {
        SemesterTerms term = DateUtil.semesterTermAt(date.getMonthValue());
        int activityYear = date.getMonthValue() <= 3 ? date.getYear() - 1 : date.getYear();

        return findActivity(lease, activityYear, term);
    }

    public List<Activity> findActivity(Lease lease, int year, SemesterTerms term) {
        return activityRepository.findAllByTerm(year, term, lease.getTenants(), Pageable.unpaged()).getContent();
    }

    public BigDecimal getFeeValue(Lease lease, LocalDate chargeDate) {
        BigDecimal positiveValue =
            isEligibleForDiscount(lease, chargeDate) ? globalSettingService.getValue(DISCOUNTED_FEE_KEY) : globalSettingService.getValue(FEE_KEY);
        return modify(positiveValue).negative();
    }

    public boolean isEligibleForDiscount(Lease lease, LocalDate chargeDate) {
        // Check if any activity during the last semester involves a discount
        List<Activity> activities = findActivityOn(lease, chargeDate.minusMonths(6));
        return activities.stream().anyMatch(Activity::getDiscount);
    }
}
