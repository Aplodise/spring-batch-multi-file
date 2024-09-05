package com.roman.multi_file_processing.processor;

import com.roman.multi_file_processing.dto.UploadFileDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InvoiceItemProcessor implements ItemProcessor<Resource, UploadFileDTO> {
    @Override
    public UploadFileDTO process(Resource item) throws Exception {
        log.info("=================> Processing the: {}", item);

        return new UploadFileDTO(item.getFilename(), item.getFile());
    }
}
