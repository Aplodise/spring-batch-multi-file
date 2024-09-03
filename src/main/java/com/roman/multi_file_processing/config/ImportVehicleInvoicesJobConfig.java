package com.roman.multi_file_processing.config;

import com.roman.multi_file_processing.dto.VehicleDTO;
import com.roman.multi_file_processing.listeners.CustomJobExecutionListener;
import com.roman.multi_file_processing.reader.MultiResourceReaderThreadSafe;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@RequiredArgsConstructor
public class ImportVehicleInvoicesJobConfig {

    @Value("${input.folder.vehicles}")
    private Resource[] resources;
    private static final Logger log = LoggerFactory.getLogger(ImportVehicleInvoicesJobConfig.class);
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final CustomJobExecutionListener jobExecutionListener;

    @Bean
    public Job importVehicleJob(Step importVehicleStep){
        return new JobBuilder("importVehicleJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importVehicleStep)
                .listener(jobExecutionListener)
                .build();
    }

    @Bean
    public Step importVehicleStep(){
        return new StepBuilder("importVehicleStep", jobRepository)
                .<VehicleDTO, VehicleDTO>chunk(100, platformTransactionManager)
                .reader(multiResourceReaderThreadSafe())
                .processor(ImportVehicleInvoicesJobConfig::vehicleProcessor)
                .writer(items -> log.info("Writing items: {}", items))
                .taskExecutor(taskExecutor())
                .build();

    }

    private static VehicleDTO vehicleProcessor(VehicleDTO item) {
        log.info("Processing the item: {}", item);
        return item;
    }



    public MultiResourceReaderThreadSafe<VehicleDTO> multiResourceReaderThreadSafe(){
        var multiResourceReader = new MultiResourceReaderThreadSafe<VehicleDTO>(multiResourceItemReader());
        multiResourceReader.setResources(resources);
        return multiResourceReader;
    }

    public MultiResourceItemReader<VehicleDTO> multiResourceItemReader(){
        return new MultiResourceItemReaderBuilder<VehicleDTO>()
                .name("vehicle resources reader")
                .resources(resources)
                .delegate(vehicleDTOFlatFileItemReader())
                .build();
    }

    public FlatFileItemReader<VehicleDTO> vehicleDTOFlatFileItemReader(){
        return new FlatFileItemReaderBuilder<VehicleDTO>()
                .name("vehicle reader")
                .saveState(Boolean.FALSE)
                .linesToSkip(1)
                .delimited()
                .delimiter(";")
                .names("referenceNumber", "model","type", "customerFullName")
                .comments("#")
                .targetType(VehicleDTO.class)
                .build();
    }

    public VirtualThreadTaskExecutor taskExecutor(){
        return new VirtualThreadTaskExecutor("Custom-Thread-");
    }
}
