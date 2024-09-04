package com.roman.multi_file_processing.config;
import com.roman.multi_file_processing.dto.VehicleJsonDTO;
import com.roman.multi_file_processing.listeners.CustomJobExecutionListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@RequiredArgsConstructor
@Profile("json")
public class ImportVehicleInvoicesJsonJobConfig {

    @Value("${input.folder.vehicles.json}")
    private Resource[] resources;
    private static final Logger log = LoggerFactory.getLogger(ImportVehicleInvoicesJsonJobConfig.class);
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
                .<VehicleJsonDTO, VehicleJsonDTO>chunk(100, platformTransactionManager)
                .reader(synchronizedItemStreamReader())
                .processor(ImportVehicleInvoicesJsonJobConfig::vehicleProcessor)
                .writer(items -> log.info("Writing items: {}", items))
                .taskExecutor(taskExecutor())
                .build();

    }

    public SynchronizedItemStreamReader<VehicleJsonDTO> synchronizedItemStreamReader(){
        return new SynchronizedItemStreamReaderBuilder<VehicleJsonDTO>()
                .delegate(multiResourceItemReader())
                .build();
    }

    public MultiResourceItemReader<VehicleJsonDTO> multiResourceItemReader(){
        return new MultiResourceItemReaderBuilder<VehicleJsonDTO>()
                .name("vehicle resources reader")
                .resources(resources)
                .delegate(jsonItemReader())
                .build();
    }

    public JsonItemReader<VehicleJsonDTO> jsonItemReader(){
        return new JsonItemReaderBuilder<VehicleJsonDTO>()
                .name("json vehicle reader")
                .jsonObjectReader(new JacksonJsonObjectReader<>(VehicleJsonDTO.class))
                .strict(false)
                .build();
    }

    private static VehicleJsonDTO vehicleProcessor(VehicleJsonDTO item) {
        log.info("Processing the item: {}", item);

        return item;

    }


    public VirtualThreadTaskExecutor taskExecutor(){
        return new VirtualThreadTaskExecutor("Json-Thread-");
    }
}
