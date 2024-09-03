package com.roman.multi_file_processing.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobTrigger {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(cron = "0/30 * * ? * *")
    @SneakyThrows
    void trigger(){
        log.info("================> starting the job");
        var jobParameters = new JobParametersBuilder();
        jobParameters.addDate("uniqueness", new Date());
        jobParameters.addString("input.file.name", "src/main/resources/data");
        JobExecution jobExecution = this.jobLauncher.run(job, jobParameters.toJobParameters());

        log.info("job finished with the status: {}", jobExecution.getExitStatus());
    }
}
