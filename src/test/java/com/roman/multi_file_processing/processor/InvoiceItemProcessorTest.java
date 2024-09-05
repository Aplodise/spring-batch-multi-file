package com.roman.multi_file_processing.processor;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.PathResource;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceItemProcessorTest {

    @SneakyThrows
    @Test
    void shouldProcess(){
        Path path = Path.of("/data/test_1.pdf");

        var itemProcessor = new InvoiceItemProcessor();
        PathResource pathResource = new PathResource(path);
        var uploadFileDTO = itemProcessor.process(pathResource);

        assertNotNull(uploadFileDTO.file());
        assertEquals(path.toFile().getName(), uploadFileDTO.fileName());
    }
}