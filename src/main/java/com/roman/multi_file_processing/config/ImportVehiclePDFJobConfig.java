package com.roman.multi_file_processing.config;

import com.roman.multi_file_processing.dto.UploadFileDTO;
import com.roman.multi_file_processing.listeners.CustomJobExecutionListener;
import com.roman.multi_file_processing.processor.InvoiceItemProcessor;
import com.roman.multi_file_processing.service.EmailService;
import com.roman.multi_file_processing.service.dto.EmailContent;
import com.roman.multi_file_processing.writer.DigitalStorageItemWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;


@Configuration
@RequiredArgsConstructor
@Profile("pdf")
@Slf4j
public class ImportVehiclePDFJobConfig {

    @Value("${input.folder.vehicles.pdf}")
    private Resource[] resources;
    private final InvoiceItemProcessor invoiceItemProcessor;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final CustomJobExecutionListener jobExecutionListener;
    private final DigitalStorageItemWriter digitalStorageItemWriter;
    private final EmailService emailService;

    @Bean
    public Job importVehicleJob(Step importVehicleStep, Step mailSenderStep){
        return new JobBuilder("importInvoices", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importVehicleStep)
                .next(mailSenderStep)
                .listener(jobExecutionListener)
                .build();
    }

    @Bean
    public Step importVehicleStep(){
        return new StepBuilder("import invoices step", jobRepository)
                .<Resource, UploadFileDTO>chunk(2, platformTransactionManager)
                .reader(resourcesItemReader())
                .processor(invoiceItemProcessor)
                .writer(digitalStorageItemWriter)
                .listener(promotionListener())
                .taskExecutor(taskExecutor())
                .build();

    }

    @Bean
    public Step mailSenderStep(){
        return new StepBuilder("mail sender step", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<String> filesUploadedCodes = (List<String>) chunkContext.getStepContext().getJobExecutionContext()
                            .get("filesUploadedCodes");
                    var emailContent = new EmailContent(filesUploadedCodes);
                    emailService.send(emailContent);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener(){
        var promotionListener = new ExecutionContextPromotionListener();
        promotionListener.setKeys(new String[]{"filesUploadedCodes"});
        return promotionListener;
    }


    public ResourcesItemReader resourcesItemReader(){
        var resourcesReader = new ResourcesItemReader();
        resourcesReader.setResources(resources);
        return resourcesReader;
    }
    public VirtualThreadTaskExecutor taskExecutor(){
        return new VirtualThreadTaskExecutor("Custom-Thread-");
    }
}
