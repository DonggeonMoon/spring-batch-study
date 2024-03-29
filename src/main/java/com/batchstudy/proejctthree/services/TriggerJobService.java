package com.batchstudy.proejctthree.services;

import com.batchstudy.proejctthree.EventHandler;
import com.batchstudy.proejctthree.config.SimpleJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TriggerJobService {

    private final Job simpleJob;
    private final JobLauncher jobLauncher;
    private final EventHandler eventHandler;

    public TriggerJobService(@SimpleJob Job simpleJob,
                             JobLauncher asyncJobLauncher,
                             EventHandler eventHandler) {
        this.simpleJob = simpleJob;
        this.jobLauncher = asyncJobLauncher;
        this.eventHandler = eventHandler;
    }

    public void triggerJob(Job job) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addParameter("uuid", new JobParameter(UUID.randomUUID().toString()))
                .toJobParameters();
        triggerJob(job, jobParameters);
    }

    public void triggerJob(Job job, JobParameters jobParameters) {
        try {
            this.jobLauncher.run(job, jobParameters);
            eventHandler.notifyNewJob();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
