package de.farue.autocut.service;

import de.farue.autocut.domain.Activity;
import de.farue.autocut.repository.ActivityRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Activity}.
 */
@Service
@Transactional
public class ActivityService {

    private final Logger log = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
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
            .map(existingActivity -> {
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
            })
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
}
