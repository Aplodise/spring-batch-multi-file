package com.roman.multi_file_processing.writer;

import com.roman.multi_file_processing.dto.UploadFileDTO;
import com.roman.multi_file_processing.storage.DigitalStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DigitalStorageItemWriter implements ItemWriter<UploadFileDTO> {

    private final DigitalStorageClient digitalStorageClient;
    private StepExecution stepExecution;

    @Override
    public void write(Chunk<? extends UploadFileDTO> chunk) throws Exception {
        chunk.getItems().forEach(this::process);
    }

    private void process(UploadFileDTO item) {
        List<String > list = (List<String>) this.stepExecution.getExecutionContext().get("filesUploadedCodes");
        String fileCode = upload(item);
        list.add(fileCode);
    }

    private String upload(UploadFileDTO item) {
            return digitalStorageClient.upload(item.file());
    }

    @BeforeStep
    public void initialize(StepExecution stepExecution){
        this.stepExecution = stepExecution;
        this.stepExecution.getExecutionContext().put("filesUploadedCodes", new ArrayList<String>());
    }
}
