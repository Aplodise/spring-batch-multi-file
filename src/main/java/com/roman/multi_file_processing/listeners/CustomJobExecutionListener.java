package com.roman.multi_file_processing.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomJobExecutionListener implements JobExecutionListener {
    @Override
    public void afterJob(JobExecution jobExecution) {
        jobExecution.getStepExecutions()
                .stream()
                .findFirst()
                .ifPresent(stepExecution -> {
                    long writeCount = stepExecution.getWriteCount();
                    log.info(String.format("the job has written %s lines", writeCount));
                });

    }
}
