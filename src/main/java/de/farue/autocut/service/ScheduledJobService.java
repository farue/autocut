package de.farue.autocut.service;

import de.farue.autocut.domain.ScheduledJob;
import de.farue.autocut.domain.enumeration.ScheduledJobStatus;
import de.farue.autocut.repository.ScheduledJobRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScheduledJobService {

    private final ScheduledJobRepository scheduledJobRepository;

    public ScheduledJobService(ScheduledJobRepository scheduledJobRepository) {
        this.scheduledJobRepository = scheduledJobRepository;
    }

    public Optional<ScheduledJob> findLastCompletedScheduledJob(String name) {
        return this.scheduledJobRepository.findFirstByNameAndStatusOrderByEndTimeDesc(name, ScheduledJobStatus.COMPLETED);
    }

    public void setDataStartTime(long jobId, Instant startTime) {
        this.scheduledJobRepository.updateDataStartTime(jobId, startTime);
    }

    public void setDataEndTime(long jobId, Instant endTime) {
        this.scheduledJobRepository.updateDataEndTime(jobId, endTime);
    }

    public long createNewScheduledJob(String name) {
        ScheduledJob job = new ScheduledJob().name(name).startTime(Instant.now()).status(ScheduledJobStatus.CREATED);
        job = this.scheduledJobRepository.save(job);
        return job.getId();
    }

    public void setJobCompleted(long jobId) {
        this.scheduledJobRepository.updateStatusCompleted(jobId, Instant.now());
    }

    public void setJobRunning(long jobId) {
        this.updateStatus(jobId, ScheduledJobStatus.RUNNING);
    }

    public void setJobError(long jobId, String exitMessage) {
        this.scheduledJobRepository.updateStatusError(jobId, Instant.now(), exitMessage);
    }

    private void updateStatus(long jobId, ScheduledJobStatus status) {
        this.scheduledJobRepository.updateStatus(jobId, status);
    }
}
